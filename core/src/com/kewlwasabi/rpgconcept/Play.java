package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import static com.kewlwasabi.rpgconcept.Constants.*; //import statics from Constant

public class Play implements Screen {

    MyRPGConcept game; //need game object for the batch

    OrthographicCamera camera; //ortho cam
    Viewport gameport; //current viewport

    Map map; //map for rendering

    Array<Item> mapItems; //array of items on the map

    World world; //this world
    Avatar avatar; //the character you control
    Random random; //random

    Array<Projectile> projectiles; //list of projectiles on the map

    boolean debug = false; //flag for debugging mode
    Projectile destroyed; //to temporarily store the "destoryed" projectile when it collides with wall (check AvatarContactListener class)

    float camBaseX; //camera originX, need to store coordinates for screenshake feature
    float camBaseY;

    HUD hud; //Hud
    float accum = 0; //accum of time needed for handling projectile frequency

    AvatarContactListener contactListener; //contactlistener for handling object interaction (not limited to Avatar i misnamed it)

    float elapsed = 0f; //elapsed duration of time for handling different features
    float durationShake; //duration of screenshake
    boolean shake = false; //flag for checking screenshake feature


    public Play(MyRPGConcept game) {
        this.game = game;
    } //constructor

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(400/PPM, 240/PPM, 0/PPM); //camera origin set in center
        world = new World(new Vector2(0,0), true); //world holding objects

        avatar = new Avatar("Samurai", world, 10/PPM,10/PPM); //init "Samurai class as Avatar"
        hud = new HUD(game.hudBatch, avatar); //HUD object
        gameport = new FitViewport(V_WIDTH/PPM, V_HEIGHT/PPM, camera); //world
        map = new Map(this);
        mapItems = new Array<Item>();
        contactListener = new AvatarContactListener(this);
        world.setContactListener(contactListener); //set world contact listener as the AvatarContactListener
        projectiles = new Array<Projectile>();
        random = new Random();

        initItems();
    }

    @Override
    public void render(float delta) { //called every delta seconds, game is capped at 60 fps so around 60 calls

        handleInput(); //input is handled at beginning of render
        avatar.update(delta); //update character

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //clears the screen for every render call

        game.batch.setProjectionMatrix(camera.combined); //set ProjectionMatrix relative to camera

        map.renderMap();
        world.step(delta, 8, 3); //world iterations
        checkforProj(); //checks for "dead" projectiles

        game.batch.begin(); //begin batch for drawing
        for(Item a: mapItems) { //draws all map items
            game.batch.draw(a.getTextureRegion(), a.posX, a.posY, a.width, a.height);
        }

        if(!Gdx.input.isTouched()) {
            game.batch.draw(avatar.getCurrentFrame(), avatar.posX,
                    avatar.posY, 64 / PPM, 64 / PPM);
        } else if((avatar.angle >= (-MathUtils.PI/4)*3) && (avatar.angle <= (MathUtils.PI/4)*3)) {
            game.batch.draw(avatar.getCurrentFrame(), avatar.posX,
                    avatar.posY, 104 / PPM, 64 / PPM);
        } else {
            game.batch.draw(avatar.getCurrentFrame(), avatar.posX - 40/PPM,
                    avatar.posY, 104 / PPM, 64 / PPM);
        }

        for(Projectile a: projectiles) { //draws projectiles on the map
            a.act(delta);
            a.draw(game.batch, 1f);
        }

        game.batch.end();

        hud.update(delta); //update hud
        hud.render(); //renders hud

        if(shake) { //checks if shaking is active
            elapsed += delta; //stores elapsed time of shaking effect
            screenShake(0); //screenShake
        }

        if(!debug) { //if not in debug mode, camera zoom is scaled to 1
            camera.zoom = 1f;
            camLerp(); //linear interpolation for camera movement
        }

    }

    @Override
    public void resize(int width, int height) { //for different screen sizes android implementation if I wanted to

        gameport.update(width, height);
    }

    @Override
    public void pause() { //functions for android, closing and opening the app

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() { //batches disposed in MyRPGConcept, no need to dispose here

    }

    public void camLerp() {
        Vector3 target = new Vector3(avatar.body.getPosition().x, avatar.body.getPosition().y, 0); //target position to lerp to
        Vector3 camPos = camera.position;

        camPos.lerp(target, 0.1f);
        camera.position.set(camPos);

        final float speed=0.1f;
        final float ispeed = 1.0f-speed; //speed of lerp

        camPos.scl(ispeed);
        target.scl(speed);
        camPos.add(target);

        camera.position.set(camPos);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined); //set ProjectionMatrix to camera
    }

    public void debugKey() { //when debug key is active, these are the inputs I can use
        float camSpeed = 10f/PPM; //camera speed
        float zoomSpeed = Gdx.graphics.getDeltaTime() * 4; //zoomspeed

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {      //camera movement
            moveCamera(0, camSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveCamera(-camSpeed, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveCamera(0, -camSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveCamera(camSpeed, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.MINUS)) {    //camera zooming
            zoomCamera(zoomSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            zoomCamera(-zoomSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {        //reset camera
            camera.zoom = 1f;
            camera.position.set(avatar.body.getPosition(), 0);
            camera.update();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {  //hp bar +/-
            hud.hpBar.subPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            hud.hpBar.addPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)) { //mp bar +/-
            hud.mpBar.subPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.APOSTROPHE)) {
            hud.mpBar.addPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {         //toggles the display of inventory (was using this to test)
            hud.turnDisplayVisible(!hud.display.isVisible());
        }

        statDebug(); //this handles the inputs for changing my character's stats
    }

    public void moveCamera(float x, float y) { //move camera
        camera.position.set(camera.position.x + x, camera.position.y + y, 0);
        camera.update();
    }

    public void zoomCamera(float zoom) { //zoom camera
        camera.zoom += zoom;
        camera.update();
    }

    public void initItems() { //initialize the Robe and cloak on map
        Item item;

        item = new Item(world, "Robe", 8, 0, 50/PPM, 50/PPM, 100/PPM, 100/PPM);
        mapItems.add(item); //added to map items array
        item = new Item(world, "Cloak", 6, 4, 50/PPM, 50/PPM, 120/PPM, 120/PPM);
        mapItems.add(item); //added to map items array

        for(Item a: mapItems) { //initalize the map items' bodies (Box2D)
            a.initBody();
        }
    }

    public void handlePickup() { //handles picking up items
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) { //E to pickup
            if(mapItems.size > 0 && hud.display.items.size > 0) {

                Item temp = avatar.inContact.peek(); //gets first item character is in contact with
                world.destroyBody(temp.body); //destorys the body on the map
                mapItems.removeValue(temp, false); //remove from map array
                avatar.itemsInv.add(temp); //add to character's inventory
            }
        }
    }

    public void handleExit() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { //escape to exit program
            Gdx.app.exit();
        }
    }

    public void handleDrop() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) { //press q to drop items
            if(avatar.itemsInv.size > 0) {
                Item temp = avatar.itemsInv.pop(); //inventory last item
                temp.setPos(avatar.body.getPosition().x, avatar.body.getPosition().y); //sets the item position to where character is

                mapItems.add(temp); //add to map items array
                temp.initBody(); // initialize the body

            }
        }
    }

    public void handleInput() { //handling basic inputs regardless of debug mode
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) { //debug mode toggle
            debug = !debug;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.O)) { //shakes screen
            screenShake(1f);
        }

        if(debug) {
            debugKey();
        }
        handleDrop(); //handling different things, these are called every delta
        handlePickup();
        handleExit();
        handleShoot();
    }

    public void handleShoot() { //handles shooting
        accum += Gdx.graphics.getDeltaTime(); //stores time for handling attack speed

        if((Gdx.input.isTouched()) && accum > 7.5f/avatar.stats[5]) { //can only shoot every 7.5/(character's dexerity) seconds
            addProjectile(); //add projectile to map
            accum = 0f; //reset accumulation back to 0 when you shoot a projectile
        }
    }

    public void addProjectile() {
         projectiles.add(new Projectile(40/PPM,40/PPM,84, 4,5,
                new Vector2(avatar.body.getPosition().x, avatar.body.getPosition().y), world));

    }

    public void removeProjectile(Projectile a) {
        projectiles.removeValue(a, false);
    } //remove projectile

    public void checkforProj() { //checks for projectiles that are to be destoryed (hit walls/enemy)
        if(destroyed != null) {
            world.destroyBody(destroyed.body);
            destroyed = null;
        }
    }

    public void statDebug() { //pressing 1 through 8 adds to the stat, pressing shift + number subtracts the stat

        if((Gdx.input.isKeyPressed(Input.Keys.NUM_3)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            if(avatar.stats[2] > 5) {
                avatar.stats[2] -= 1;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            avatar.stats[2] += 1;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.NUM_4)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            if(avatar.stats[3] > 5) {
                avatar.stats[3] -= 1;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            avatar.stats[3] += 1;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.NUM_5)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            if(avatar.stats[4] > 5) {
                avatar.stats[4] -= 1;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            avatar.stats[4] += 1;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.NUM_6)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            if(avatar.stats[5] > 5) {
                avatar.stats[5] -= 1;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            avatar.stats[5] += 1;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.NUM_7)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            if(avatar.stats[6] > 5) {
                avatar.stats[6] -= 1;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_7)) {
            avatar.stats[6] += 1;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.NUM_8)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))) {
            if(avatar.stats[7] > 5) {
                avatar.stats[7] -= 1;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_8)) {
            avatar.stats[7] += 1;
        }
    }

    public void screenShake(float duration) { //screen shake
        if(!shake) {
            camBaseX = camera.position.x; //records the "base" camera position
            camBaseY = camera.position.y;
            durationShake = duration; //sets duration
            shake = true;
        } else if(durationShake - elapsed > 0) { //during the shake
            float x = (random.nextFloat() - 0.5f) * (durationShake - elapsed); //gets random translation coordinate
            float y = (random.nextFloat() - 0.5f) * (durationShake - elapsed); //magnitude of translation scales with time elapsed
            camera.translate(x, y);
            camera.update();
        }
        if(durationShake - elapsed < 0) { //when shake is over
            shake = false;
            elapsed = 0;
            camera.position.set(camBaseX, camBaseY, 0); //return camera to base coordinate
        }

    }

}
