package com.websocketdemo.game;

import com.badlogic.gdx.Screen;
import com.websocketdemo.game.model.GameState;
import com.websocketdemo.game.model.ClientGameWrapper;


public class WebSocketTechDemo implements Screen {

	
	private WebSocketTechDemoApplication m_game;
	private ClientGameWrapper m_gameWorld;
	public WebSocketTechDemo(final WebSocketTechDemoApplication game, boolean isMobile) {
		this.m_game=game;
		m_gameWorld = new ClientGameWrapper(m_game, GameState.Menu, isMobile);
	}
	
	@Override
	public void render(float delta) {
		m_gameWorld.render();
		
	}
	

	@Override
	public void dispose() {
		m_gameWorld.dispose();
	}

	@Override
	public void pause() {
	}
	
	@Override
	public void resize(int arg0, int arg1) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
	
}

