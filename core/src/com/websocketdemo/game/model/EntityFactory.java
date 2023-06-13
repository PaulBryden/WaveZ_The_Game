package com.websocketdemo.game.model;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class EntityFactory {
	
	public World m_World;
	EntityListWrapper m_EntityWrapper;
	public EntityFactory(World world,EntityListWrapper wrapper){
		m_World=world;
		m_EntityWrapper=wrapper;
	}
	
	public IEntity CreateEntity(int ID, float xCoord, float yCoord,int UUID, boolean isLocal) throws Exception{
		IEntity entity;
		switch (ID){
			case EntityFactoryID.UserSoldier:{	/*UserSoldier*/	
				PolygonShape pEntity = new PolygonShape();
				pEntity.setAsBox(1.5f,1.5f);
				FixtureDef def = new FixtureDef();
				def.restitution = 0.9f;
				def.friction = 0.01f;
				def.shape = pEntity;
				def.density = 1f;
				BodyDef boxBodyDef = new BodyDef();
				boxBodyDef.type = BodyType.DynamicBody;
				boxBodyDef.position.x = xCoord;
				boxBodyDef.position.y = yCoord;
				Body boxBody = m_World.createBody(boxBodyDef);
				boxBody.createFixture(def);
				boxBody.setFixedRotation(true);
				boxBody.setBullet(true);
				pEntity.dispose();
				return new UserEntity(boxBody,UUID);
			}
			case EntityFactoryID.Soldier:{ /*Soldier*/
				PolygonShape pEntity = new PolygonShape();
				pEntity.setAsBox(1.5f,1.5f);
				FixtureDef def = new FixtureDef();
				def.restitution = 0.9f;
				def.friction = 0.01f;
				def.shape = pEntity;
				def.density = 1f;
				BodyDef boxBodyDef = new BodyDef();
				boxBodyDef.type = BodyType.DynamicBody;
				boxBodyDef.position.x = xCoord;
				boxBodyDef.position.y = yCoord;
				Body boxBody = m_World.createBody(boxBodyDef);
				boxBody.setFixedRotation(true);
				boxBody.setBullet(true);
				boxBody.createFixture(def);
				pEntity.dispose();
				return new SoldierEntity(boxBody,UUID,false);
			}
			case EntityFactoryID.Light:{
			}/*Light*/
			
			case EntityFactoryID.Enemy:{
				/*Enemy*/
			}
			case EntityFactoryID.LocalSoldier:{ /*Soldier*/
				PolygonShape pEntity = new PolygonShape();
				pEntity.setAsBox(1.5f,1.5f);
				FixtureDef def = new FixtureDef();
				def.restitution = 0.9f;
				def.friction = 0.01f;
				def.shape = pEntity;
				def.density = 1f;
				BodyDef boxBodyDef = new BodyDef();
				boxBodyDef.type = BodyType.DynamicBody;
				boxBodyDef.position.x = xCoord;
				boxBodyDef.position.y = yCoord;
				Body boxBody = m_World.createBody(boxBodyDef);
				boxBody.setFixedRotation(true);
				boxBody.setBullet(true);
				boxBody.createFixture(def);
				pEntity.dispose();
				return new SoldierEntity(boxBody,UUID,isLocal);
			}
			case EntityFactoryID.Bullet:{
				PolygonShape pEntity = new PolygonShape();
				pEntity.setAsBox(0.25f,0.25f);
				FixtureDef def = new FixtureDef();
				def.restitution = 0.9f;
				def.friction = 0.01f;
				def.shape = pEntity;
				def.density = 0.01f;
				BodyDef boxBodyDef = new BodyDef();
				boxBodyDef.type = BodyType.DynamicBody;
				boxBodyDef.position.x = xCoord;
				boxBodyDef.position.y = yCoord;
				Body boxBody = m_World.createBody(boxBodyDef);
				boxBody.setBullet(true);
				boxBody.createFixture(def);
				pEntity.dispose();
				return new BulletEntity(boxBody,UUID);
			}
			case EntityFactoryID.Zombie:{ /*Zombie*/
				PolygonShape pEntity = new PolygonShape();
				pEntity.setAsBox(1.5f,1.5f);
				FixtureDef def = new FixtureDef();
				def.restitution = 0.9f;
				def.friction = 0.01f;
				def.shape = pEntity;
				def.density = 1f;
				BodyDef boxBodyDef = new BodyDef();
				boxBodyDef.type = BodyType.DynamicBody;
				boxBodyDef.position.x = xCoord;
				boxBodyDef.position.y = yCoord;
				Body boxBody = m_World.createBody(boxBodyDef);
				boxBody.setFixedRotation(false);
				boxBody.createFixture(def);
				pEntity.dispose();
				return new ZombieEntity(boxBody,UUID,isLocal,m_EntityWrapper);
			}
			default:{
				
			}
		}
		throw new Exception("Error, Unknown Type of Entity");
		}
	
}
