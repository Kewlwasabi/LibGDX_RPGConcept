package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class MyRPGConcept extends Game { //Game main class

	SpriteBatch batch; //batch to draw sprites
	SpriteBatch hudBatch; //batch to draw HUD

	Play play; //play which implements Screen

	@Override
	public void create() {
		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		play = new Play(this);

		setScreen(play); //set current screen to the "Play" scene

	}

	@Override
	public void render() {
		super.render();

		FPS = Gdx.graphics.getFramesPerSecond(); //get FPS to be displayed in the HUD

	}

	@Override
	public void dispose() { //dispose junk
		batch.dispose();
		hudBatch.dispose();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

}