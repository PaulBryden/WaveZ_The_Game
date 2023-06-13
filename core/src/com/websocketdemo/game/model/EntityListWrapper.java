package com.websocketdemo.game.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.serialization.impl.Deserializer;

public class EntityListWrapper{

	private ArrayList<IEntity> m_EntityList;
	private ArrayList<IEntity> m_EntityRemovalList;
	private HashSet<Integer> m_RecentlyDeletedUUID;
	private EntityFactory m_EntityFactory;
	World m_World;
	EntityListWrapper(World world){
		m_World=world;
		m_EntityList=new ArrayList<IEntity>();
		m_EntityFactory= new EntityFactory(m_World,this);
		m_EntityRemovalList = new ArrayList<IEntity>();
		m_RecentlyDeletedUUID = new HashSet<Integer>();
	}
	public void AddEntity(IEntity entity){
	    synchronized(this){

			if(!m_EntityList.contains(entity)){
				m_EntityList.add(entity);
			}
	    }
	}
	public void RemoveEntity(IEntity entity){
		synchronized(this){
			if(m_EntityList.contains(entity)){
				m_EntityList.remove(entity);
			}
		}
	}
	
	public IEntity getEntityFromBody(Body body) throws NullPointerException{
		IEntity entity;
		for( IEntity i : m_EntityList){
			if (i.getBody()==body){
				entity=i;
				return entity;
			}
			
		}
		throw new NullPointerException(); 
		
	}
	
	public final ArrayList<IEntity> GetEntityList(){
	   return m_EntityList;
	}
	
	public ArrayList<EntityDataPacket> getLocalEntities(boolean isServer){

		ArrayList<EntityDataPacket> eList= new ArrayList<EntityDataPacket>();
		for( IEntity i : m_EntityList){
			
			if(i.isLocal()){
				eList.add(i.getDataPacket(isServer));
			}
		}
		return eList;
		
	}
	
	public EntityDataPacketList ProcessStep(float delta, boolean isServer){
		ArrayList<IEntity> deleteList = new ArrayList<IEntity>();
		EntityListWrapper addList = new EntityListWrapper(m_World);
		EntityDataPacketList list = new EntityDataPacketList();
		for(IEntity i : m_EntityList){
			if(i.getHealth()>0){
			i.ProcessStep(delta,isServer);
			if(i.isFiring()){
				i.setFiring(false);
				IEntity entity=null;
				Random rand = new Random();
				float angle = (float) ((i.getEntityMovementData().pointerAngleRads * (180f / Math.PI) + 93f));
				try {
					
					Vector2 tempBulletVector = new Vector2(0.85f,2.6f);
					tempBulletVector.rotate(angle);	
					entity=m_EntityFactory.CreateEntity(EntityFactoryID.Bullet, i.getBody().getPosition().x+tempBulletVector.x, i.getBody().getPosition().y+tempBulletVector.y, rand.nextInt(),true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(entity!=null){
					Vector2 tempVec = new Vector2(0, 14);
					tempVec.rotate(angle);
					entity.getBody().setLinearVelocity(tempVec);
					addList.AddEntity(entity);
				}
			}
			}else{
				deleteList.add(i);
			}
			if(!i.isLocal() && i.getStepsSinceLastUpdate()>40 ){
				if(i.GetID()==EntityFactoryID.Soldier ){
					if(i.getStepsSinceLastUpdate()>120){
						i.setHealth(0);
						deleteList.add(i);
					}
				}else{
					deleteList.add(i);
				}
			}
		}
		for (IEntity a : addList.GetEntityList()){
				this.AddEntity(a);
			}
		for (IEntity d : deleteList){
				if(m_RecentlyDeletedUUID.size()>50){
					m_RecentlyDeletedUUID.clear();
				}
				m_RecentlyDeletedUUID.add(d.GetUUID());

			if(isServer){
				if(d.GetID()!=EntityFactoryID.Bullet)
					addList.AddEntity(d);
				
			}
			RemoveEntity(d);
			m_EntityFactory.m_World.destroyBody(d.getBody());
		}
		EntityDataPacketList tempList =addList.GetEntityDatapacketList(false);
		return (tempList);
	}
	
	public EntityDataPacketList GetEntityDatapacketList(boolean isServer){
		Queue<EntityDataPacket> packetQueue = new Queue<EntityDataPacket>();
		
		for(IEntity i : m_EntityList){
			packetQueue.addLast(i.getDataPacket(isServer));
		}
		

		return new EntityDataPacketList(packetQueue);
	}
	public boolean deserializeEntity(EntityDataPacket packet,boolean isServer){
		boolean entityExists=false;
		IEntity entityRemove=null;
		for(IEntity i : m_EntityList){
			if(i.GetUUID()==packet.m_UUID){
				entityExists=true;

				if(packet.m_Health<=0 || m_RecentlyDeletedUUID.contains(packet.m_UUID)){

					entityRemove=i;
				}else{
					i.parseDataPacket(packet,isServer);
				}
			}
		}
		if(entityRemove!=null){

			if(m_RecentlyDeletedUUID.size()>50){
				m_RecentlyDeletedUUID.clear();
			}
			m_RecentlyDeletedUUID.add(entityRemove.GetUUID());
			RemoveEntity(entityRemove);
			m_World.destroyBody(entityRemove.getBody());
		}
		if(packet.m_Health<=0){
			return false;
		}
		
		if(!entityExists &&!m_RecentlyDeletedUUID.contains(packet.m_UUID)){
			try {
				IEntity entity;

				entity=(m_EntityFactory.CreateEntity(packet.m_TypeID, packet.m_Position.x, packet.m_Position.y, packet.m_UUID,false));
				entity.parseDataPacket(packet);
				this.AddEntity(entity);
				entity.setHealth(packet.m_Health);
				if(entity.GetID()==EntityFactoryID.Bullet){
					entity.getBody().setLinearVelocity(packet.m_Velocity);
					return true;
				}
			} catch (Exception e) {
				System.out.println("Couldn't create entity");
				e.printStackTrace();
			}
			
		}

		return false;
	}
}
