package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


import static com.kewlwasabi.rpgconcept.Constants.*;

public class HUD {

    private Stage stage;
    private Stage overLayStage;

    Table tableInv;
    Table tableStats;

    ShapeRenderer renderer;

    Texture background;
    Texture invBack;
    Texture itemBack;
    TextureRegion classImg;

    Texture hpPic;
    Texture mpPic;

    SpriteBatch batch;

    Label label;
    Skin skin;

    ToolTip hpTip;
    ToolTip mpTip;
    ToolTip pickupTip;

    HPBar hpBar;
    HPBar mpBar;

    PickupDisplay display;
    FPSCounter fpsCounter;

    Avatar avatar;

    Label atk;
    Label def;
    Label spd;
    Label dex;
    Label vit;
    Label wis;


    public HUD(SpriteBatch batch, Avatar avatar) {
        this.batch = batch;
        this.avatar = avatar;

        initComp();
        initBack();
        initInv();
        initResource();
        initStats();
        initClassInfo();
        initToolTip();

        stage.addActor(tableInv);
        stage.addActor(tableStats);
    }

    public void initComp() {
        stage = new Stage();
        overLayStage = new Stage();
        tableInv = new Table();
        tableStats = new Table();
        skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));
        renderer = new ShapeRenderer();

        display = new PickupDisplay(avatar);
        display.setVisible(false);

        fpsCounter = new FPSCounter();
        overLayStage.addActor(fpsCounter);

        stage.addActor(display);

    }

    public Pixmap createRect(int width, int height, float r, float g, float b, float alpha) {
        Pixmap pixmap =  new Pixmap(width,height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r,g,b,alpha);
        pixmap.fill();
        pixmap.drawRectangle(0,0,width, height);

        return pixmap;

    }

    public void initBack() {
        Pixmap pixmap = createRect((int)((V_WIDTH/4)),(int)((V_HEIGHT) - V_HEIGHT/48), 0,0,0,0.5f);
        background = new Texture(pixmap);
        pixmap = createRect((int)((V_WIDTH/4) - (V_WIDTH/80)),(int)((V_HEIGHT/5)), 0.1f,0.1f,0.1f,0.5f);
        invBack = new Texture(pixmap);
        pixmap = createRect((int)V_WIDTH/20, (int)V_WIDTH/20, 0.5f, 0.5f, 0.5f, 0.5f);
        itemBack = new Texture(pixmap);
    }

    public void initInv() {

        tableInv.setPosition((V_WIDTH-V_WIDTH/4),V_HEIGHT/4);
        tableInv.setSize((int)((V_WIDTH/4) - (V_WIDTH/80)),(int)(V_HEIGHT/5));

        for(int i = 1; i < 5; i++) {
            label = new Label(Integer.toString(i), skin);
            tableInv.add(label).fillY().pad((V_WIDTH / 80), V_WIDTH / 47, (V_WIDTH / 67), V_WIDTH / 47);


        }
        tableInv.row();
        for(int i = 5; i < 9; i++) {
            label = new Label(Integer.toString(i), skin);
            tableInv.add(label).fillY().pad((V_WIDTH/67),V_WIDTH/47,(V_WIDTH/80),V_WIDTH/47);
        }


    }

    public void initResource() {
        mpBar = new HPBar(new Color(0, 0, 1, 1), avatar.mp, (int)((V_WIDTH/4) - V_WIDTH/18), V_HEIGHT/16,
                (V_WIDTH-V_WIDTH/4) + V_WIDTH/18,V_HEIGHT/2 + V_HEIGHT/6);
        stage.addActor(mpBar);
        mpPic = new Texture("mp.png");
        hpBar = new HPBar(new Color(1,0, 0, 1), avatar.hp, (int)((V_WIDTH/4) - V_WIDTH/18), V_HEIGHT/16,
                (V_WIDTH-V_WIDTH/4) + V_WIDTH/18,V_HEIGHT/2 + V_HEIGHT/16 + V_WIDTH/80 + V_HEIGHT/6);
        stage.addActor(hpBar);
        hpPic = new Texture("hp.png");
    }

    public void initStats() {

        tableStats.setPosition((V_WIDTH-V_WIDTH/4) + V_WIDTH/160, V_HEIGHT/2 - V_WIDTH/40);
        tableStats.setSize((int)((V_WIDTH/4) - V_WIDTH/80), (int)(V_HEIGHT/5));
        tableStats.clear();

        String text;
        for(int i = 1; i < 4; i++) {
            switch (i) {
                case 1:
                    text = "Atk: " + avatar.stats[i+1];
                    atk = new Label(text, skin);
                    tableStats.add(atk).pad(V_WIDTH/80,V_WIDTH/80,V_WIDTH/80,V_WIDTH/80).fillY();
                    break;
                case 2:
                    text = "Def: " + avatar.stats[i+1];
                    def = new Label(text, skin);
                    tableStats.add(def).pad(V_WIDTH/80,V_WIDTH/80,V_WIDTH/80,V_WIDTH/80).fillY();
                    break;
                case 3:
                    text = "Spd: " + avatar.stats[i+1];
                    spd = new Label(text, skin);
                    tableStats.add(spd).pad(V_WIDTH/80,V_WIDTH/80,V_WIDTH/80,V_WIDTH/80).fillY();
                    break;
                default:
                    text = null;
            }

        }
        tableStats.row();
        for(int i = 4; i < 7; i++) {
            switch(i) {
                case 4:
                    text = "Dex: " + avatar.stats[i+1];
                    dex = new Label(text, skin);
                    tableStats.add(dex).pad(V_WIDTH/80,V_WIDTH/80,V_WIDTH/80,V_WIDTH/80).fillY();
                    break;
                case 5:
                    text = "Vit: " + avatar.stats[i+1];
                    vit = new Label(text, skin);
                    tableStats.add(vit).pad(V_WIDTH/80,V_WIDTH/80,V_WIDTH/80,V_WIDTH/80).fillY();
                    break;
                case 6:
                    text = "Wis: " + avatar.stats[i+1];
                    wis = new Label(text, skin);
                    tableStats.add(wis).pad(V_WIDTH/80,V_WIDTH/80,V_WIDTH/80,V_WIDTH/80).fillY();
                    break;
                default:
                    text = null;
            }
        }


    }

    public void initClassInfo() {
        classImg = new TextureRegion(avatar.walkRight[0]);
        label = new Label(avatar.className, skin);
        label.setPosition((V_WIDTH-V_WIDTH/4) + V_WIDTH/8, V_HEIGHT - V_HEIGHT/8);
        stage.addActor(label);

    }

    public void render() {
        batch.begin();

        batch.draw(background,(V_WIDTH-V_WIDTH/4), V_HEIGHT/4 - V_HEIGHT/48);
        batch.draw(invBack, (V_WIDTH-V_WIDTH/4) + V_WIDTH/160, V_HEIGHT/4);

        batch.draw(hpPic, (V_WIDTH-V_WIDTH/4) + V_WIDTH/80,
                V_HEIGHT/2 + V_WIDTH/20 + V_HEIGHT/6, V_HEIGHT/16, V_HEIGHT/16);

        batch.draw(mpPic, (V_WIDTH-V_WIDTH/4) + V_WIDTH/80,
                V_HEIGHT/2 + V_HEIGHT/6, V_HEIGHT/16, V_HEIGHT/16);

        batch.draw(classImg, (V_WIDTH-V_WIDTH/4) + V_WIDTH/80,
                V_HEIGHT - V_HEIGHT/8, V_HEIGHT/16, V_HEIGHT/16);

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 4; j++) {
                batch.draw(itemBack, (V_WIDTH-V_WIDTH/4) + V_WIDTH/73 + (V_WIDTH/20)*j + (V_HEIGHT/80)*j,
                        V_HEIGHT/4 + 4 + V_HEIGHT/10 - (V_HEIGHT/10)*i);
            }
        }

        batch.end();

        stage.act();
        stage.draw();

        batch.begin();

        int i = 0;
        int j = 0;
        for(Item a : avatar.itemsInv) {
            if(a != null) {
                batch.draw(avatar.itemsInv.get(i*4+j).getTextureRegion(),
                        (V_WIDTH - V_WIDTH / 4) + V_WIDTH / 73 + (V_WIDTH / 20) * j + (V_HEIGHT / 80) * j,
                        V_HEIGHT/4 + 4 + V_HEIGHT/10 - (V_HEIGHT/10)*i,
                        V_WIDTH / 20, V_WIDTH / 20);

                j++;
                if(j > 3) {
                    j = 0;
                    i++;
                }
            }
        }
        batch.end();

        overLayStage.act();
        overLayStage.draw();
    }

    public void turnDisplayVisible(boolean a) {
        display.setVisible(a);
    }

    public void initToolTip() {
        hpTip = new ToolTip("Health Points", (V_WIDTH-V_WIDTH/4) + V_WIDTH/80,
                V_WIDTH, V_HEIGHT/2 + V_WIDTH/20 + V_HEIGHT/6, V_HEIGHT/2 + V_WIDTH/20 + V_HEIGHT/6 + V_HEIGHT/16);
        mpTip = new ToolTip("Mana Points", (V_WIDTH-V_WIDTH/4) + V_WIDTH/80,
                V_WIDTH,V_HEIGHT/2 + V_HEIGHT/6, V_HEIGHT/2 + V_HEIGHT/6 + V_HEIGHT/16);
        pickupTip = new ToolTip("Press \"E\" to pickup", (V_WIDTH-V_WIDTH/4) + V_WIDTH/73,
                (V_WIDTH-V_WIDTH/4) + V_WIDTH/73 + (V_WIDTH/20)*3 + (V_HEIGHT/80)*3 + V_WIDTH/20, V_WIDTH/80 + 4,
                V_WIDTH/80 + 4 + (V_WIDTH/20)*1 + (V_WIDTH/100)*1 + V_WIDTH/20);
        pickupTip.setVisible(false);

        overLayStage.addActor(hpTip);
        overLayStage.addActor(mpTip);
        overLayStage.addActor(pickupTip);
    }

    public void updateStats() {
        atk.setText("Atk: " + avatar.stats[2]);
        def.setText("Def: " + avatar.stats[3]);
        spd.setText("Spd: " + avatar.stats[4]);
        dex.setText("Dex: " + avatar.stats[5]);
        vit.setText("Vit: " + avatar.stats[6]);
        wis.setText("Wis: " + avatar.stats[7]);
    }

    public void update(float delta) {
        updateStats();
    }

}
