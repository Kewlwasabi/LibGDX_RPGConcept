package com.kewlwasabi.rpgconcept;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.kewlwasabi.rpgconcept.Constants.*;

public class MyRPGConcept extends Game {

	SpriteBatch batch;
	SpriteBatch hudBatch;

	Play play;

	@Override
	public void create() {
		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		play = new Play(this);

		setScreen(play);

	}

	@Override
	public void render() {
		super.render();

		FPS = Gdx.graphics.getFramesPerSecond();

	}

	@Override
	public void dispose() {
		batch.dispose();
		hudBatch.dispose();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

}