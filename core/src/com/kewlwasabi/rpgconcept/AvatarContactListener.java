package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class AvatarContactListener implements ContactListener {

    Play play;

    public AvatarContactListener(Play play) {
        this.play = play;
    }

    @Override
    public void beginContact(Contact contact) { //handling for beginning of contact

        Object objA = contact.getFixtureA().getUserData(); //get object of 2 fixtures touching each other
        Object objB = contact.getFixtureB().getUserData();

        if((objB.getClass().equals(Item.class)) && objA.equals("avatar")) { //if its item and avatar
            play.hud.turnDisplayVisible(true); //turns on inventory display on the bottom right

            play.avatar.inContact.add((Item)objB); //
            play.hud.display.addItem((Item)objB);
            play.hud.pickupTip.setVisible(true); //pickup tip visible

        }

        if((objA.equals("wall")) && objB.getClass().equals(Projectile.class)) { //if objects are wall and projectile, it destroys projectile
            play.destroyed = (Projectile) objB;
            play.removeProjectile((Projectile)objB);
        }

    }

    @Override
    public void endContact(Contact contact) { //handling of ending contact

        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if(objB.getClass().equals(Item.class) && objA.equals("avatar")) { //if its item and avatar
            play.hud.display.removeItem((Item)objB);  //remove item from hud display

            if(play.hud.display.items.size == 0) {

                System.out.println(play.avatar.inContact.peek().name); //for debugging purposes
                play.avatar.inContact.removeValue(play.avatar.inContact.peek(), false); //removes first item character is in contact with
                play.hud.pickupTip.setVisible(false); //set tip to not visible
                play.hud.turnDisplayVisible(false);  //turn display off
            }

        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
