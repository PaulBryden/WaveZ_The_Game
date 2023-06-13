	package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;
public class EntityDataPacket implements Transferable<EntityDataPacket>, hashPacket{
	public EntityMovementData m_Data;
	public Vector2 m_Velocity;
	public Vector2 m_Position;
	public int m_TypeID;
	public int m_UUID;
	public float m_Delay;
	public float m_Health;
	public int m_Hash;
	public long m_Time;
	/*Should be pulled from the Entity itself*/
	
	EntityDataPacket(EntityMovementData data, Vector2 Velocity, Vector2 Position, int ID, int UUID, float delay, float health,int hash,long time){
		m_Data=data;
		m_Velocity=Velocity;
		m_Position=Position;
		m_TypeID=ID;
		m_UUID=UUID;
		m_Health=health;
		m_Hash=hash;
		m_Time=time;
	}
	public EntityDataPacket(){
	m_Data = new EntityMovementData();
	m_Velocity = new Vector2();
	m_Position = new Vector2();
	m_Delay=0;
	m_Health=0;
	m_Hash=0;
	m_Time=0;
	
	}
	EntityDataPacket(boolean[] directions,float angle,float VelX, float VelY, float PosX, float PosY, int ID,int UUID, float delay, float health, int hash, long time){
		m_Data = new EntityMovementData();
		m_Data.VelocityList=directions;
		m_Data.pointerAngleRads=angle;
		m_Velocity= new Vector2(VelX,VelY);
		m_Position= new Vector2(PosX,PosY);
		m_TypeID=ID;
		m_UUID=UUID;
		m_Health=health;
		m_Hash=hash;
		m_Time=time;
	}
	
	@Override
	public void serialize(Serializer serializer) throws SerializationException {
			serializer.serializeBooleanArray(m_Data.VelocityList).serializeFloat(m_Data.pointerAngleRads)
					.serializeFloat(m_Velocity.x)
					.serializeFloat(m_Velocity.y)
					.serializeFloat(m_Position.x)
					.serializeFloat(m_Position.y)
					.serializeInt(m_TypeID)
					.serializeInt(m_UUID)
					.serializeFloat(m_Delay)
					.serializeFloat(m_Health)
					.serializeInt(this.hashCode())
					.serializeLong(TimeUtils.millis());
	
						
						
	}
	@Override
	public EntityDataPacket deserialize(Deserializer deserializer) throws SerializationException {
		
		return new EntityDataPacket(deserializer.deserializeBooleanArray(),
				deserializer.deserializeFloat(),
				deserializer.deserializeFloat(),
				deserializer.deserializeFloat(),
				deserializer.deserializeFloat(),
				deserializer.deserializeFloat(),
				deserializer.deserializeInt(),
				deserializer.deserializeInt(),
				deserializer.deserializeFloat(),
				deserializer.deserializeFloat(),
				deserializer.deserializeInt(),
				deserializer.deserializeLong());
	}
	@Override
	public int GetHash() {
		// TODO Auto-generated method stub
		return m_Hash;
	}
}
