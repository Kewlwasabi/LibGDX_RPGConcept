package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class Projectile extends Actor {

    float width;
    float height;
    int sheetPosX;
    int sheetPosY;

    float posX;
    float posY;

    float projectileSpeed;
    float angle;

    float rotateAngle = -45;

    Vector2 avaPos;

    TextureRegion proj;
    World world;

    Body body;

    public Projectile(float width, float height, int sheetPosX, int sheetPosY, float projectileSpeed, Vector2 avaPos, World world) {
        this.width = width;
        this.height = height;
        this.sheetPosX = sheetPosX;
        this.sheetPosY = sheetPosY;
        this.projectileSpeed = projectileSpeed;
        this.avaPos = avaPos;
        this.world = world;

        posX = avaPos.x;
        posY = avaPos.y;

        initComp();
        calculateAngle();
        initSensor();
    }

    public void initComp() {
        proj = getTextureRegion();
    }

    public TextureRegion getTextureRegion() {
        Texture sheet = new Texture("tilesheet.png");
        TextureRegion temp = new TextureRegion(sheet);

        TextureRegion[][] tempS = temp.split(8,8);

        return tempS[sheetPosX][sheetPosY];
    }

    @Override
    public void act(float delta) {
        posX += (projectileSpeed*MathUtils.cos(angle))*delta;
        posY += (projectileSpeed*MathUtils.sin(angle)*delta);


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(proj, posX, posY, width/2, height/2,
                width, height, 1f, 1f, rotateAngle+((angle*180)/MathUtils.PI));
    }

    public void calculateAngle() {
        Vector2 point1 = new Vector2(800/2, 480/2);
        float x = Gdx.input.getX();
        float y = V_HEIGHT - Gdx.input.getY();
        Vector2 point2 = new Vector2(x, y);

        angle = MathUtils.atan2((point2.y - point1.y),(point2.x - point1.x));

    }

    public void initSensor() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(posX + width/2, posY + height/2);

        body = world.createBody(bdef);

        fdef.friction = 0;
        fdef.isSensor = true;
        shape.setAsBox(width/2, height/2);
        fdef.shape = shape;

        body.createFixture(fdef).setUserData(this);
        body.setLinearVelocity(projectileSpeed*MathUtils.cos(angle), projectileSpeed*MathUtils.sin(angle));
    }

}
