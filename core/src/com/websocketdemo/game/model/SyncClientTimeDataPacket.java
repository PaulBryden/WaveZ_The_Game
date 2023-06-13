package com.websocketdemo.game.model;

import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;

public class SyncClientTimeDataPacket implements Transferable<SyncClientTimeDataPacket>{
	public long m_LocalTime;
	public long m_ServerTime;

	SyncClientTimeDataPacket(){
		
	}
	SyncClientTimeDataPacket(long localTime, long serverTime){
		m_LocalTime=localTime;
		m_ServerTime=serverTime;
	}
	@Override
	public void serialize(Serializer serializer) throws SerializationException {
		serializer.serializeLong(m_LocalTime).serializeLong(m_LocalTime);
	}
	@Override
	public SyncClientTimeDataPacket deserialize(Deserializer deserializer) throws SerializationException {
		return new SyncClientTimeDataPacket(deserializer.deserializeLong(),deserializer.deserializeLong());
	}
}
