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
    public void beginContact(Contact contact) {

        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if((objB.getClass().equals(Item.class)) && objA.equals("avatar")) {
            play.hud.turnDisplayVisible(true);

            play.avatar.inContact.add((Item)objB);
            play.hud.display.addItem((Item)objB);
            play.hud.pickupTip.setVisible(true);

        }

        if((objA.equals("wall")) && objB.getClass().equals(Projectile.class)) {
            play.destroyed = (Projectile) objB;
            play.removeProjectile((Projectile)objB);
        }

    }

    @Override
    public void endContact(Contact contact) {

        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if(objB.getClass().equals(Item.class) && objA.equals("avatar")) {
            play.hud.display.removeItem((Item)objB);

            if(play.hud.display.items.size == 0) {

                System.out.println(play.avatar.inContact.peek().name);
                play.avatar.inContact.removeValue(play.avatar.inContact.peek(), false);
                play.hud.pickupTip.setVisible(false);
                play.hud.turnDisplayVisible(false);
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
