package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import static com.kewlwasabi.rpgconcept.Constants.V_HEIGHT;
import static com.kewlwasabi.rpgconcept.Constants.V_WIDTH;

public class PickupDisplay extends Actor { //Displays what items to be picked up

    Avatar avatar;

    Texture disBack;
    Texture itemBack;

    Array<Item> items;
    Item nullItem = null;

    public PickupDisplay(Avatar avatar) {
        this.avatar = avatar;
        items = new Array<Item>();

        initComp();
    }

    public void initComp() { //intialize grey background of display
        Pixmap pixmap = createRect((int)(V_WIDTH/4),(int)((V_HEIGHT/5)), 0.1f,0.1f,0.1f,0.5f);
        disBack = new Texture(pixmap);

        pixmap = createRect((int)V_WIDTH/20, (int)V_WIDTH/20, 0.5f, 0.5f, 0.5f, 0.5f);
        itemBack = new Texture(pixmap);


    }

    public Pixmap createRect(int width, int height, float r, float g, float b, float alpha) {
        Pixmap pixmap =  new Pixmap(width,height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r,g,b,alpha);
        pixmap.fill();
        pixmap.drawRectangle(0,0,width, height);

        return pixmap;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(disBack, V_WIDTH - V_WIDTH/4, V_WIDTH/80); //draws grey background for display
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 4; j++) {
                batch.draw(itemBack, (V_WIDTH-V_WIDTH/4) + V_WIDTH/73 + (V_WIDTH/20)*j + (V_HEIGHT/80)*j,
                        V_WIDTH/80 + 4 + (V_WIDTH/20)*i + (V_WIDTH/100)*i);
            }
        }

        int i = 0;
        int j = 0;
        for(Item a : items) { //draws item sprite over the grey background
            if(a != null) {
                batch.draw(items.get(i*4+j).getTextureRegion(),
                        (V_WIDTH-V_WIDTH/4) + V_WIDTH/73 + (V_WIDTH/20)*j + (V_HEIGHT/80)*j,
                                V_WIDTH/80 + 4 + V_HEIGHT/10 - (V_HEIGHT/10)*i,
                                V_WIDTH/20, V_WIDTH/20);

                j++;
                if(j > 3) {
                    j = 0;
                    i++; //when j > 3, reset j to 0 and i++, for shifting down a row
                }
            }
        }
    }

    public void addItem(Item item) {
        if(items.size < 8) {
            items.add(item);
        }
    }

    public void removeItem(Item item) {
        items.removeValue(item, false);

    }

}
