package com.websocketdemo.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.websocketdemo.game.controller.ActionHandler;

public class UserClient {

	OrthographicCamera m_camera;
	ActionHandler m_Handle;
	EntityFactory m_Factory;
	UserEntity clientEntity;
	InputMultiplexer m_Multiplexer;
	boolean m_isMobile;
	public UserClient(EntityFactory factory,OrthographicCamera camera,float xCoord, float yCoord,int UUID, InputMultiplexer multiplex,boolean isMobile){
		m_camera=camera;
		m_Multiplexer=multiplex;
		m_Factory=factory;
		m_isMobile=isMobile;
		CreateUser(xCoord,yCoord,UUID);
	}

	private void CreateUser(float xCoord, float yCoord,int UUID){
		try {
			clientEntity = (UserEntity) m_Factory.CreateEntity(EntityFactoryID.UserSoldier, xCoord, yCoord, UUID,true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!m_isMobile) {
			try {
				m_Handle = new ActionHandler(m_camera, clientEntity);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_Multiplexer.addProcessor(m_Handle);
		}
	}
	public UserEntity GetUser(){
		return clientEntity;
	}
	
	public void dispose(){
		m_Handle.dispose();
		m_Handle=null;
	}
	
	
}
