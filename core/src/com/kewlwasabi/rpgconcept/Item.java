package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public class Item {

    String name;

    int sheetX;
    int sheetY;

    float width;
    float height;

    float posX;
    float posY;

    World world;
    Body body;

    public Item(World world, String name, int sheetX, int sheetY, float width, float height, float posX, float posY) {
        this.name = name;
        this.sheetX = sheetX;
        this.sheetY = sheetY;

        this.width = width;
        this.height = height;

        this.posX = posX;
        this.posY = posY;
        this.world = world;

    }

    public void setPos(float x, float y) {
        posX = x;
        posY = y;
    }

    public TextureRegion getTextureRegion() { //initializes item file
        Texture sheet = new Texture("tilesheet.png");
        TextureRegion temp = new TextureRegion(sheet);

        TextureRegion[][] tempS = temp.split(8,8); //splits tilesheet

        return tempS[sheetX][sheetY]; //returns the item's sprite according to coordinate sheetX, sheetY of that item
    }

    public void initBody() { //creates bodies and fixture
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.position.set(posX + width/2, posY + height/2);
        bdef.type = BodyDef.BodyType.StaticBody; //static body since they dont move or get affected by physics
        body = world.createBody(bdef);

        shape.setAsBox(width/2, height/2);
        fdef.shape = shape;
        fdef.friction = 0;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData(this);
    }

}
