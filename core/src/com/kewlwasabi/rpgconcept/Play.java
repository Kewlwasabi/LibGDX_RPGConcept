package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class Play implements Screen {

    MyRPGConcept game;

    OrthographicCamera camera;
    Viewport gameport;

    Texture pic;
    Map map;

    Array<Item> mapItems;

    World world;
    Avatar avatar;
    Random random;

    Array<Projectile> projectiles;

    boolean debug = false;
    Projectile destroyed;

    float camBaseX;
    float camBaseY;

    HUD hud;
    float accum = 0;

    AvatarContactListener contactListener;

    float elapsed = 0f;
    float durationShake;
    boolean shake = false;


    public Play(MyRPGConcept game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(400/PPM, 240/PPM, 0/PPM);
        world = new World(new Vector2(0,0), true);

        avatar = new Avatar("Samurai", world, 10/PPM,10/PPM);
        hud = new HUD(game.hudBatch, avatar);
        gameport = new FitViewport(V_WIDTH/PPM, V_HEIGHT/PPM, camera);
        map = new Map(this);
        mapItems = new Array<Item>();
        contactListener = new AvatarContactListener(this);
        world.setContactListener(contactListener);
        projectiles = new Array<Projectile>();
        random = new Random();

        initItems();
    }

    @Override
    public void render(float delta) {

        handleInput();
        avatar.update(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        map.renderMap();
        world.step(delta, 8, 3);
        checkforProj();

        game.batch.begin();
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

        for(Item a: mapItems) {
            game.batch.draw(a.getTextureRegion(), a.posX, a.posY, a.width, a.height);
        }

        for(Projectile a: projectiles) {
            a.act(delta);
            a.draw(game.batch, 1f);
        }

        game.batch.end();

        hud.update(delta);
        hud.render();

        if(shake) {
            elapsed += delta;
            screenShake(0);
        }

        if(!debug) {
            camera.zoom = 1f;
            camLerp();
        }

        System.out.println(shake);
    }

    @Override
    public void resize(int width, int height) {

        gameport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void camLerp() {
        Vector3 target = new Vector3(avatar.body.getPosition().x, avatar.body.getPosition().y, 0);
        Vector3 camPos = camera.position;

        camPos.lerp(target, 0.1f);
        camera.position.set(camPos);

        final float speed=0.1f;
        final float ispeed = 1.0f-speed;

        camPos.scl(ispeed);
        target.scl(speed);
        camPos.add(target);

        camera.position.set(camPos);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
    }

    public void debugKey() {
        float camSpeed = 10f/PPM;
        float zoomSpeed = Gdx.graphics.getDeltaTime() * 4;

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
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
        if(Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            zoomCamera(zoomSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            zoomCamera(-zoomSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {
            camera.zoom = 1f;
            camera.position.set(avatar.body.getPosition(), 0);
            camera.update();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
            hud.hpBar.subPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            hud.hpBar.addPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)) {
            hud.mpBar.subPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.APOSTROPHE)) {
            hud.mpBar.addPoints(10);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            hud.turnDisplayVisible(!hud.display.isVisible());
        }

        statDebug();
    }

    public void moveCamera(float x, float y) {
        camera.position.set(camera.position.x + x, camera.position.y + y, 0);
        camera.update();
    }

    public void zoomCamera(float zoom) {
        camera.zoom += zoom;
        camera.update();
    }

    public void initItems() {
        Item item;

        item = new Item(world, "Robe", 8, 0, 50/PPM, 50/PPM, 100/PPM, 100/PPM);
        mapItems.add(item);
        item = new Item(world, "Cloak", 6, 4, 50/PPM, 50/PPM, 120/PPM, 120/PPM);
        mapItems.add(item);

        for(Item a: mapItems) {
            a.initBody();
        }
    }

    public void handlePickup() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if(mapItems.size > 0 && hud.display.items.size > 0) {

                Item temp = avatar.inContact.peek();
                world.destroyBody(temp.body);
                mapItems.removeValue(temp, false);
                avatar.itemsInv.add(temp);
            }
        }
    }

    public void handleExit() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void handleDrop() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if(avatar.itemsInv.size > 0) {
                Item temp = avatar.itemsInv.pop();
                temp.setPos(avatar.body.getPosition().x, avatar.body.getPosition().y);

                mapItems.add(temp);
                temp.initBody();

            }
        }
    }

    public void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            debug = !debug;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.O)) {
            screenShake(1f);
        }

        if(debug) {
            debugKey();
        }
        handleDrop();
        handlePickup();
        handleExit();
        handleShoot();
    }

    public void handleShoot() {
        accum += Gdx.graphics.getDeltaTime();

        if((Gdx.input.isTouched()) && accum > 7.5f/avatar.stats[5]) {
            addProjectile();
            accum = 0f;
        }
    }

    public void addProjectile() {
         projectiles.add(new Projectile(40/PPM,40/PPM,84, 4,5,
                new Vector2(avatar.body.getPosition().x, avatar.body.getPosition().y), world));

    }

    public void removeProjectile(Projectile a) {
        projectiles.removeValue(a, false);
    }

    public void checkforProj() {
        if(destroyed != null) {
            world.destroyBody(destroyed.body);
            destroyed = null;
        }
    }

    public void statDebug() {

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

    public void screenShake(float duration) {
        if(!shake) {
            camBaseX = camera.position.x;
            camBaseY = camera.position.y;
            durationShake = duration;
            shake = true;
        } else if(durationShake - elapsed > 0) {
            float x = (random.nextFloat() - 0.5f) * (durationShake - elapsed);
            float y = (random.nextFloat() - 0.5f) * (durationShake - elapsed);
            camera.translate(x, y);
            camera.update();
        }
        if(durationShake - elapsed < 0) {
            shake = false;
            elapsed = 0;
            camera.position.set(camBaseX, camBaseY, 0);
        }

    }

}
