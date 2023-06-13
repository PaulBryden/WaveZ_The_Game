package com.websocketdemo.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WebSocketTechDemoApplication extends Game {
	
	public SpriteBatch batch;
	public BitmapFont font;
	boolean isMobile;
	public WebSocketTechDemoApplication(boolean Mobile){
		isMobile=Mobile;
	}

	public void create() {
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();
		this.setScreen(new WebSocketTechDemo(this,isMobile));
	}

	public void render() {
		super.render(); //important!
	}
	
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}