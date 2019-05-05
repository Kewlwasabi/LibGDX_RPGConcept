package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class Map {

    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    Play play;

    Box2DDebugRenderer b2dr;

    public Map(Play play) { //initialize map
        this.play = play;
        b2dr = new Box2DDebugRenderer();

        initMap();
        initMapBodies();

    }

    public void initMap() {
        map = new TmxMapLoader().load("map.tmx"); //loads map data from TMX file (Tiled app used for making pixel maps)
        renderer = new OrthogonalTiledMapRenderer(map, SCALE/PPM); //renderer for the TiledMap scaled with PPM
    }

    public void renderMap() {
        renderer.setView(play.camera); //sets view to camera in Play class
        renderer.render();

        if(play.debug) { //if debug mode is on
            b2dr.render(play.world, play.camera.combined); //uses Box2DDebugRenderer, which shows the Body and Fixture outlines
        }

    }

    public void initMapBodies() {

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for(MapObject a: map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) { //gets MapObject from tmx file
            Rectangle rect = ((RectangleMapObject) a).getRectangle(); //converts object to Rectangle

            //creates bodies and fixtures for each wall and Map object
            bdef.position.set((rect.getX()*SCALE + (rect.getWidth()/2)*SCALE)/PPM, (rect.getY()*SCALE + (rect.getHeight()/2)*SCALE)/PPM);
            bdef.type = BodyDef.BodyType.StaticBody;
            body = play.world.createBody(bdef);

            shape.setAsBox(((rect.getWidth()/2)*SCALE)/PPM, ((rect.getHeight()/2)*SCALE)/PPM);
            fdef.shape = shape;
            fdef.friction = 0;
            body.createFixture(fdef).setUserData("wall");

        }
    }


}
