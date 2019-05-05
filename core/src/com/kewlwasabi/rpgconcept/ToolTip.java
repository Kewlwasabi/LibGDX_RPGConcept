package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class ToolTip extends Actor { //tooltip that shows up when you hover an item

    String message;
    float textWidth;
    float textHeight;

    float posX;
    float posY;

    float posX2;
    float posY2;

    Texture box;

    GlyphLayout layout;

    Label label;

    boolean hover = false;

    Skin skin;

    public ToolTip(String message, float posX, float posX2, float posY, float posY2) {
        this.message = message;
        this.posX = posX;
        this.posY = posY;
        this.posX2 = posX2;
        this.posY2 = posY2;

        calculateLen();
        initBox();
        initLabel();
    }

    public void calculateLen() { //calculates length of a string
        skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));

        layout = new GlyphLayout();
        layout.setText(skin.getFont("default-font"), message);

        textWidth = layout.width;
        textHeight = layout.height;

    }

    public void initBox() {
        Pixmap pixmap = createRect((int)textWidth + 10, (int)textHeight + 10, 0f, 0f, 0f, 0.7f);
        box = new Texture(pixmap);
    }

    @Override
    public void act(float delta) { //flags if mouse is between posX, posX2, posY, posY2 region
        if((Gdx.input.getX() > posX) && (Gdx.input.getX() < posX2)
                && (V_HEIGHT - Gdx.input.getY() > posY) && (V_HEIGHT - Gdx.input.getY() < posY2)) {
            hover = true;
        } else {
            hover = false;
        }

        label.setPosition(Gdx.input.getX() + 5, V_HEIGHT - Gdx.input.getY()); //sets position

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(hover) { //when mouse is hovering
            batch.draw(box, Gdx.input.getX(), V_HEIGHT - Gdx.input.getY()); //draws tooltip
            label.act(Gdx.graphics.getDeltaTime());
            label.draw(batch, parentAlpha);
        }
    }

    public Pixmap createRect(int width, int height, float r, float g, float b, float alpha) {
        Pixmap pixmap =  new Pixmap(width,height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r,g,b,alpha);
        pixmap.fill();
        pixmap.drawRectangle(0,0,width, height);

        return pixmap;

    }

    public void initLabel() {
        label = new Label(message, skin);
    }
}
