package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class FPSCounter extends Actor {

    Label label;

    float FPS;

    public FPSCounter() {
        initComp();
    }

    public void initComp() {
        label = new Label(Float.toString(this.FPS), new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"))); //FPS custom text
        label.setPosition(20, V_HEIGHT - 30);
    }

    public void act(float delta) {
        this.FPS = Constants.FPS; //updates FPS in Constants to this
        label.setText(Float.toString(this.FPS));
        label.act(delta);
    }

    public void draw(Batch batch, float parentAlpha) {
        label.draw(batch, parentAlpha);
    }

}
