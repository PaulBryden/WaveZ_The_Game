package com.websocketdemo.game.model;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;


public class EntityDataPacketList  implements Transferable<EntityDataPacketList>{
	private  Queue<EntityDataPacket> m_PacketList;
	
	public EntityDataPacketList(){
		m_PacketList=new Queue<EntityDataPacket>();
	}
	public EntityDataPacketList( Queue<EntityDataPacket> packetList){
		m_PacketList=packetList;
	}
	
	@Override
	public void serialize(Serializer serializer) throws SerializationException {
            synchronized(this) {
                for (EntityDataPacket i : m_PacketList) {
                    if (i != null) {

                        serializer.serializeTransferable(i);
                    }
                }
            }

		
	}

	@Override
	public EntityDataPacketList deserialize(Deserializer deserializer) throws SerializationException {
		m_PacketList = new Queue<EntityDataPacket>();
		// TODO Auto-generated method stub
		while(true){
			try{
				EntityDataPacket packet = new EntityDataPacket();
				m_PacketList.addFirst(deserializer.deserializeTransferable(packet));
			}catch(Exception e){
				return new EntityDataPacketList(m_PacketList);
			}
		}
		
	}
	
	public Queue<EntityDataPacket> getPacketQueue(){
		return m_PacketList;
		
	}
	
}
