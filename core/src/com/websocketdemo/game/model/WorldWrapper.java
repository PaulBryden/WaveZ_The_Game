package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
public class WorldWrapper {
	
	private World m_World;
	private Body groundBody;

	/*Should be a function call from within the world wrapper*/
	//private UserEntity user;

	private EntityListWrapper entities;
	/*Setup for each relevant entity in the world. Generic Event Processor called every tick. Settings modified by External Event Handlers*/

	private EntityFactory m_Factory;
	private boolean m_isServer;
	private EntityContactListener m_ContactListener;
	public  WorldWrapper(boolean isServer){
		
		m_World = new World(new Vector2(0, 0), true);
		createBoundaries();
		entities = new EntityListWrapper(m_World);
		m_Factory= new EntityFactory(m_World,entities);
		m_isServer=isServer;
		m_ContactListener = new EntityContactListener(entities,isServer);
		m_World.setContactListener(m_ContactListener);
	}
	
	public final EntityDataPacketList getSerializedWorld(){
		synchronized(this){
			return entities.GetEntityDatapacketList(m_isServer);
		}
		
	}
	public final EntityListWrapper  getEntities()
	{		
		return entities;
	}
	
	public  World getWorld()
	{
		return m_World;
	}
	
	public void addEntity(IEntity entity)
	{		synchronized(this){
				entities.AddEntity(entity);
	}
	}
	
	public int validateActiveUsers(){
		boolean removedLocal=false;
		for(int i=0;i<entities.GetEntityList().size(); i++){
			if (entities.GetEntityList().get(i).getStepsSinceLastUpdate()>240){
					//log.error("Error: Disconnected from Server");
				m_World.destroyBody(entities.GetEntityList().get(i).getBody());
				entities.RemoveEntity(entities.GetEntityList().get(i));
						i--;
						removedLocal= true;		
				
			}
		}

		if(removedLocal){
			return UserDisconnectState.Disconnected;
		}else if(entities.getLocalEntities(false).size()==0) {
			return UserDisconnectState.Dead;
		}else{
			return UserDisconnectState.Connected;
		}

		
	}

	
	private void createBoundaries()
	{
		float halfWidth = GameWrapperSettings.viewportWidth / 2f;
		ChainShape chainShape = new ChainShape();
		
		chainShape.createLoop(new Vector2[] {
				new Vector2(-halfWidth, 0f),
				new Vector2(halfWidth, 0f),
				new Vector2(halfWidth, GameWrapperSettings.viewportHeight),
				new Vector2(-halfWidth, GameWrapperSettings.viewportHeight) });
		
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = m_World.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
	}
	
	public EntityDataPacketList WorldStep(float delta,float physicsTimeLeft){

		m_World.step(GameWrapperSettings.TIME_STEP, GameWrapperSettings.VELOCITY_ITERS, GameWrapperSettings.POSITION_ITERS);
		return entities.ProcessStep(delta,m_isServer);
	}

}
