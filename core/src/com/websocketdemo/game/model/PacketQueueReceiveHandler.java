package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;

public class PacketQueueReceiveHandler{
	EntityPacketQueue m_PacketQueue;
	EntityListWrapper m_EntityList;
	public PacketQueueReceiveHandler(EntityPacketQueue pQueue, EntityListWrapper elWrapper){
		m_PacketQueue=pQueue;
		m_EntityList=elWrapper;
	}

/*Should be executed before world.step()*/
	public boolean handleQueue(boolean isServer){
		boolean dataAvailable=false;
		while(m_PacketQueue.size()>0){
			EntityDataPacket packet;
			try{
				packet= m_PacketQueue.popPacket();

				if(Float.isNaN(packet.m_Velocity.x) ||Float.isNaN(packet.m_Velocity.y) ||  Float.isNaN(packet.m_Position.y)||  Float.isNaN(packet.m_Position.x) ){

				}else{
					if(m_EntityList.deserializeEntity(packet,isServer)) dataAvailable=true;
					
				}
			}catch(NullPointerException e){
			}
		}
		return dataAvailable;
		
	}
}
