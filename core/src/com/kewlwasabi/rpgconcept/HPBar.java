package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HPBar extends Actor {

    Color color; //color since the bar can be used for other stuff other than hp

    float width;
    float maxWidth;

    float height;
    float posX;
    float posY;
    float currentPoints;
    float maxPoints;

    float damageCounter = 0;
    float healingCounter = 0;
    float healingTemp = 0;

    Texture bar;

    Label disPoints;
    int disNum;

    public HPBar(Color color, float points, float width, float height, float posX, float posY) {
        this.color = color;
        this.width = width;
        this.maxWidth = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.currentPoints = points;
        this.maxPoints = points;
        this.disNum = (int)points;

        initComp();
        initLabel();
    }

    public void initComp() {
        Pixmap pixmap = createRect(1,1,color.r, color.g, color.b, color.a); //create rect from PixMap
        bar = new Texture(pixmap);



    }

    public void initLabel() {
        disPoints = new Label(Integer.toString(disNum), new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"))); //hp number with custom text
        disPoints.setPosition(posX + 10, posY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(bar, posX, posY, width, height);
        disPoints.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        float percentBar = currentPoints/maxPoints; //current percentage of bar

        if(damageCounter > 0) { //damage counter stores damage the character needs to take
            damageCounter = damageCounter - 20f*delta; //goes down by 20f every second
            width = percentBar*maxWidth + (damageCounter/maxPoints)*maxWidth; //scales width of hp bar by percent HP
            disNum = Math.round((width/maxWidth)*maxPoints); //gets current rounded HP
        }
        if(healingCounter > 0) { //for healing
            healingCounter = healingCounter - 20f*delta;
            healingTemp =- healingCounter;
            width = percentBar*maxWidth + (healingTemp/maxPoints)*maxWidth; //width increases with healing
            disNum = Math.round(((width/maxWidth)*maxPoints));
        }

        disPoints.setText(Integer.toString(disNum));
    }

    public Pixmap createRect(int width, int height, float r, float g, float b, float alpha) { //create rectangle method
        Pixmap pixmap =  new Pixmap(width,height, Pixmap.Format.RGBA8888); //uses RGBA8888 PixMap
        pixmap.setColor(r,g,b,alpha);
        pixmap.fill();
        pixmap.drawRectangle(0,0,width, height);

        return pixmap; //returns correct size pixmap

    }

    public void addPoints(float a) { //adds HP points
        if(currentPoints + a <= maxPoints) {
            healingCounter += a;
            healingTemp += a;
            currentPoints += a;
        }
    }

    public void subPoints(float a) { //subtracts HP points
        if(currentPoints - a >= 0) {
            damageCounter += a;
            currentPoints -= a;
        }
    }

    public void setMaxPoints(float a) {
        maxPoints = a;
    }

}
