package com.websocketdemo.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.websocketdemo.game.model.UserEntity;
import com.websocketdemo.game.model.EntityMovementKeyEventProcessor;
import com.websocketdemo.game.model.IEntity;
import com.websocketdemo.game.model.EntityMovementIdentifiers;


public class ActionHandler extends InputAdapter {

	int lastDragX;
	int lastDragY;
	boolean m_IsRunning;
	boolean m_isMobile;
	public ActionHandler(OrthographicCamera camera, UserEntity iEntity){
		m_Entity=iEntity;
		m_Camera=camera;
		m_EventProcessor = new EntityMovementKeyEventProcessor(iEntity);
		m_IsRunning=true;
	}

	@Override

	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			m_Entity.setFiring(true);
		}
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			m_Entity.setFiring(true);
			if(m_IsRunning){
				Vector3 tempVec = new Vector3(screenX,screenY,0);
				tempVec=m_Camera.unproject(tempVec);
				m_EventProcessor.ProcessPointerMoveEvent(tempVec.x,tempVec.y);
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if(m_IsRunning){
			Vector3 tempVec = new Vector3(x,y,0);
			tempVec=m_Camera.unproject(tempVec);
			m_EventProcessor.ProcessPointerMoveEvent(tempVec.x,tempVec.y);
		}
			return true;

	}
	
	
	@Override
	public boolean keyDown(int keycode){
		if(m_IsRunning){
			switch (keycode) {
			
			case Input.Keys.W:
				m_EventProcessor.ProcessMoveStartEvent(EntityMovementIdentifiers.MovementIdentifiers.Forward.ordinal());
				return true;
			case Input.Keys.A:
				m_EventProcessor.ProcessMoveStartEvent(EntityMovementIdentifiers.MovementIdentifiers.Left.ordinal());
				return true;
			case Input.Keys.S:
				m_EventProcessor.ProcessMoveStartEvent(EntityMovementIdentifiers.MovementIdentifiers.Backward.ordinal());
				return true;
			case Input.Keys.D:
				m_EventProcessor.ProcessMoveStartEvent(EntityMovementIdentifiers.MovementIdentifiers.Right.ordinal());
				return true;
			default:
				return false;

			}
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode){
		switch (keycode) {
		
		case Input.Keys.W:
			m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Forward.ordinal());
			return true;
		case Input.Keys.A:
			m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Left.ordinal());
			return true;
		case Input.Keys.S:
			m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Backward.ordinal());
			return true;
		case Input.Keys.D:
			m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Right.ordinal());
			return true;
		default:
			return false;
		}
	}
	public void dispose(){
		m_IsRunning=false;
	}


	public EntityMovementKeyEventProcessor m_EventProcessor;
	private UserEntity m_Entity;
	private OrthographicCamera m_Camera;
}
