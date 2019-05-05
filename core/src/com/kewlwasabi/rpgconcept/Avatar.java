package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class Avatar {

    String className;
    int classOrder; //which row the character's sprite is in the sprite sheet
    int[] stats = new int[8]; //to store character's stats (attack, defence, speed, etc.)

    int hp;
    int mp;

    Array<Item> itemsInv; //array of items in inventory
    Array<Item> inContact; //array of items that are currently touching the character

    TextureRegion[][] sheet;        //these arrays are for loading the sprite animations
    TextureRegion[][] attacksheet;

    TextureRegion[] walkLeft;
    TextureRegion[] walkRight;
    TextureRegion[] walkFront;
    TextureRegion[] walkBack;

    TextureRegion[] attackLeft;
    TextureRegion[] attackRight;
    TextureRegion[] attackFront;
    TextureRegion[] attackBack;


    Animation<TextureRegion> animWalk; //Animation of textureregion for walking

    TextureRegion currentStill; //region of the texture for the currentstill (not moving)
    TextureRegion currentFrame; //region for current frame of animation

    Body body; //avatar body (Box2D physics)

    float stateTime = 0f;
    float posX;
    float posY;
    float avatarSpeed;

    float angle;

    World world; //world the avatar is in (passed onto Play class)

    public Avatar(String className, World world, float x, float y) {
        for(AvatarClass avaClass: AvatarClass.values()) {   //initializing avatar based on their class, different class has different stats
            if(avaClass.className().equals(className)) {
                this.className = avaClass.className();
                this.classOrder = avaClass.classOrder();
                for(int i = 0; i < 8; i++) {
                    this.stats[i] = avaClass.stat(i);
                }
            }
        }

        hp = this.stats[0]; //store hp and mp value
        mp = this.stats[0];

        posX = x;
        posY = y;
        this.world = world;

        initSheet(); //initialization spritesheet, animation, everything
        initAnimation();
        initBody();
        initItems();

    }

    public void initSheet() {
        TextureRegion temp = new TextureRegion(new Texture("players.png"));          //init sprite stills
        TextureRegion tempAtk = new TextureRegion(new Texture("spriteAttack.png"));  //init sprite attack sheet

        sheet = temp.split(8,8); //cut sheet into pieces into 2d array
        attacksheet = tempAtk.split(13, 8);

        TextureRegion[][] tempR = temp.split(8,8); //temp array i needed so it can be flipped and used as the "left" animation
        TextureRegion[][] tempAR = tempAtk.split(13, 8);

        itemsInv = new Array<Item>();

        walkRight = new TextureRegion[2]; //array init
        walkLeft = new TextureRegion[2];
        walkFront = new TextureRegion[3];
        walkBack = new TextureRegion[3];

        attackRight = new TextureRegion[2];
        attackLeft = new TextureRegion[2];
        attackFront = new TextureRegion[2];
        attackBack = new TextureRegion[2];

        int index = 0;
        for(int i = (classOrder-1)*3+1; i < ((classOrder-1)*3+3); i++) { //add respective animations and stills into array according to class
            index = 0;
            for(int j = 0; j < 3; j++) {
                if(i == (classOrder-1)*3+1) {
                    walkFront[index++] = sheet[i][j];
                }
                if(i == (classOrder-1)*3+2) {
                    walkBack[index++] = sheet[i][j];
                }
            }
        }
        for(int i = 0; i < 2; i++) { //I initialized the right/left animation separately since I just needed to flip the left to get right
            walkRight[i] = sheet[(classOrder-1)*3][i];
            tempR[(classOrder-1)*3][i].flip(true, false);
            walkLeft[i] = tempR[(classOrder-1)*3][i];
        }

        index = 0;
        for(int i = (classOrder-1)*3+1; i < ((classOrder-1)*3+3); i++) { //initializing the attack animation
            index = 0;
            for(int j = 0; j < 2; j++) {
                if(i == (classOrder-1)*3+1) {
                    attackFront[index++] = attacksheet[i][j];
                }
                if(i == (classOrder-1)*3+2) {
                    attackBack[index++] = attacksheet[i][j];
                }
            }
        }
        for(int i = 0; i < 2; i++) {
            attackRight[i] = attacksheet[(classOrder-1)*3][i];
            tempAR[(classOrder-1)*3][i].flip(true, false);
            attackLeft[i] = tempAR[(classOrder-1)*3][i];
        }




    }

    public void initAnimation() { //intialization the animation
        animWalk = new Animation<TextureRegion>(0.25f, walkFront); //every frame 0.25 float
        currentStill = walkFront[0]; //the first still is facing front
    }

    public void update(float delta) {
        stateTime+= delta; //statetime used for animation
        avatarSpeed = 0.13f*stats[4];

        checkAnimation(); //checks my current walking direction and loads the correct animation for it

        if((Gdx.input.isKeyPressed(Input.Keys.W)) || (Gdx.input.isKeyPressed(Input.Keys.A)) //when walking
                || (Gdx.input.isKeyPressed(Input.Keys.S)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            currentFrame = animWalk.getKeyFrame(stateTime, true); //updates currentFrame of animation according to stateTime
        } else if(!Gdx.input.isTouched()){
            currentFrame = currentStill;
        } else {
            currentFrame = animWalk.getKeyFrame(stateTime, true);
        }

        handleInput(); //handles input and position every delta call
        updatePosition();
    }

    public void checkAnimation() { //checks animation and updates my "still" and animation
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            animWalk = new Animation<TextureRegion>(0.25f, walkBack);
            currentStill = walkBack[0];
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            animWalk = new Animation<TextureRegion>(0.25f, walkFront);
            currentStill = walkFront[0];
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            animWalk = new Animation<TextureRegion>(0.25f, walkLeft);
            currentStill = walkLeft[0];
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            animWalk = new Animation<TextureRegion>(0.25f, walkRight);
            currentStill = walkRight[0];
        }
        if(Gdx.input.isTouched()) {
            checkAttackAnimation();
        }
    }

    public void handleInput() { //handles my input, I could add special attacks here later not just WASD handling
        handleWASD();
    }

    public void initBody() { //initialize my body
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.DynamicBody; //My character is a dynamic body
        bdef.position.set(posX + 32/PPM, posY + 32/PPM); //set position of char
        body = world.createBody(bdef); //create this body definition in the world

        fdef.friction = 0; //no friction since this is a top down game
        shape.setAsBox(64/2/PPM, 64/2/PPM); //sets fixture shape
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("avatar"); //create fixture for this body and has the data "avatar" used for contact signals later

    }

    public void updatePosition() { //update pos
        posX = body.getPosition().x - 32/PPM;
        posY = body.getPosition().y - 32/PPM;
    }

    public TextureRegion getCurrentStill() {
        return currentStill;
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public void checkSpeedUp() { //hold left shift to speed up
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            avatarSpeed = 0.13f*stats[4]*2;
        } else {
            avatarSpeed = 0.13f*stats[4]; //my speed scales according to the speed stat
        }
    }

    public void initItems() { //init item

        inContact = new Array<Item>();
        addItem(new Item(world, "Staff", 85, 11, 50/PPM, 50/PPM, 100/PPM, 100/PPM)); //added a staff to my character

    }

    public void addItem(Item item) { //adds items
        if(itemsInv.size < 8) {
            itemsInv.add(item);
        }
    }

    public void handleWASD() { //movement key handling
        checkSpeedUp();
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(0f, avatarSpeed);
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-avatarSpeed, 0f);
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.setLinearVelocity(0f, -avatarSpeed);
        } else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(avatarSpeed, 0f);
        } else {
            body.setLinearVelocity(0,0);
        }
    }

    public Item getItem(int a) {
        return itemsInv.get(a);
    }

    public void calculateAngle() { //calculates angle from character center to my mouse
        Vector2 point1 = new Vector2(800/2, 480/2); //coordinate of center
        float x = Gdx.input.getX();
        float y = V_HEIGHT - Gdx.input.getY();
        Vector2 point2 = new Vector2(x, y); //coordinate of my mouse

        angle = MathUtils.atan2((point2.y - point1.y),(point2.x - point1.x)); //angle is tangent of x2-x1 and y2-y1

    }

    public void checkAttackAnimation() { //attack animation according to current angle of my mouse
        calculateAngle();
        if((angle >= -MathUtils.PI/4) && (angle <= MathUtils.PI/4)) {
            animWalk = new Animation<TextureRegion>(7.5f/stats[5]/2, attackRight);
            currentStill = walkRight[0];
        } else if((angle >= MathUtils.PI/4) && (angle <= (MathUtils.PI/4)*3)) {
            animWalk = new Animation<TextureRegion>(7.5f/stats[5]/2, attackBack);
            currentStill = walkBack[0];
        } else if((angle >= (-MathUtils.PI/4)*3) && (angle <= (-MathUtils.PI/4))) {
            animWalk = new Animation<TextureRegion>(7.5f/stats[5]/2, attackFront);
            currentStill = walkFront[0];
        } else {
            animWalk = new Animation<TextureRegion>(7.5f/stats[5]/2, attackLeft);
            currentStill = walkLeft[0];
        }


    }


}
