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
    int classOrder;
    int[] stats = new int[8];

    int hp;
    int mp;

    Array<Item> itemsInv;
    Array<Item> inContact;

    TextureRegion[][] sheet;
    TextureRegion[][] attacksheet;

    TextureRegion[] walkLeft;
    TextureRegion[] walkRight;
    TextureRegion[] walkFront;
    TextureRegion[] walkBack;

    TextureRegion[] attackLeft;
    TextureRegion[] attackRight;
    TextureRegion[] attackFront;
    TextureRegion[] attackBack;


    Animation<TextureRegion> animWalk;

    TextureRegion currentStill;
    TextureRegion currentFrame;

    Body body;

    float stateTime = 0f;
    float posX;
    float posY;
    float avatarSpeed;

    float angle;

    World world;

    public Avatar(String className, World world, float x, float y) {
        for(AvatarClass avaClass: AvatarClass.values()) {
            if(avaClass.className().equals(className)) {
                this.className = avaClass.className();
                this.classOrder = avaClass.classOrder();
                for(int i = 0; i < 8; i++) {
                    this.stats[i] = avaClass.stat(i);
                }
            }
        }

        hp = this.stats[0];
        mp = this.stats[0];

        posX = x;
        posY = y;
        this.world = world;

        initSheet();
        initAnimation();
        initBody();
        initItems();

    }

    public void initSheet() {
        TextureRegion temp = new TextureRegion(new Texture("players.png"));
        TextureRegion tempAtk = new TextureRegion(new Texture("spriteAttack.png"));

        sheet = temp.split(8,8);
        attacksheet = tempAtk.split(13, 8);

        TextureRegion[][] tempR = temp.split(8,8);
        TextureRegion[][] tempAR = tempAtk.split(13, 8);

        itemsInv = new Array<Item>();

        walkRight = new TextureRegion[2];
        walkLeft = new TextureRegion[2];
        walkFront = new TextureRegion[3];
        walkBack = new TextureRegion[3];

        attackRight = new TextureRegion[2];
        attackLeft = new TextureRegion[2];
        attackFront = new TextureRegion[2];
        attackBack = new TextureRegion[2];

        int index = 0;
        for(int i = (classOrder-1)*3+1; i < ((classOrder-1)*3+3); i++) {
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
        for(int i = 0; i < 2; i++) {
            walkRight[i] = sheet[(classOrder-1)*3][i];
            tempR[(classOrder-1)*3][i].flip(true, false);
            walkLeft[i] = tempR[(classOrder-1)*3][i];
        }

        index = 0;
        for(int i = (classOrder-1)*3+1; i < ((classOrder-1)*3+3); i++) {
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

    public void initAnimation() {
        animWalk = new Animation<TextureRegion>(0.25f, walkFront);
        currentStill = walkFront[0];
    }

    public void update(float delta) {
        stateTime+= delta;
        avatarSpeed = 0.13f*stats[4];

        checkAnimation();

        if((Gdx.input.isKeyPressed(Input.Keys.W)) || (Gdx.input.isKeyPressed(Input.Keys.A))
                || (Gdx.input.isKeyPressed(Input.Keys.S)) || (Gdx.input.isKeyPressed(Input.Keys.D))) {
            currentFrame = animWalk.getKeyFrame(stateTime, true);
        } else if(!Gdx.input.isTouched()){
            currentFrame = currentStill;
        } else {
            currentFrame = animWalk.getKeyFrame(stateTime, true);
        }

        handleInput();
        updatePosition();
    }

    public void checkAnimation() {
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

    public void handleInput() {
        handleWASD();
    }

    public void initBody() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(posX + 32/PPM, posY + 32/PPM);
        body = world.createBody(bdef);

        fdef.friction = 0;
        shape.setAsBox(64/2/PPM, 64/2/PPM);
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("avatar");

    }

    public void updatePosition() {
        posX = body.getPosition().x - 32/PPM;
        posY = body.getPosition().y - 32/PPM;
    }

    public TextureRegion getCurrentStill() {
        return currentStill;
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public void checkSpeedUp() {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            avatarSpeed = 0.13f*stats[4]*2;
        } else {
            avatarSpeed = 0.13f*stats[4];
        }
    }

    public void initItems() {

        inContact = new Array<Item>();
        addItem(new Item(world, "Staff", 85, 11, 50/PPM, 50/PPM, 100/PPM, 100/PPM));

    }

    public void addItem(Item item) {
        if(itemsInv.size < 8) {
            itemsInv.add(item);
        }
    }

    public void handleWASD() {
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

    public void calculateAngle() {
        Vector2 point1 = new Vector2(800/2, 480/2);
        float x = Gdx.input.getX();
        float y = V_HEIGHT - Gdx.input.getY();
        Vector2 point2 = new Vector2(x, y);

        angle = MathUtils.atan2((point2.y - point1.y),(point2.x - point1.x));

    }

    public void checkAttackAnimation() {
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
