package com.websocketdemo.game.model;

import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;


public class UsernameUUIDPair implements Transferable<UsernameUUIDPair>{
	public long UUID;
	public String Username;

	public UsernameUUIDPair(){
		UUID=0; Username="";
	}
	public UsernameUUIDPair(long uuid, String username){
		UUID=uuid; Username=username;
	}
	
	@Override
	public void serialize(Serializer serializer) throws SerializationException {
            synchronized(this) {
                        serializer.serializeLong(UUID).serializeString(Username);
            }

		
	}

	@Override
	public UsernameUUIDPair deserialize(Deserializer deserializer) throws SerializationException {
		return new UsernameUUIDPair(deserializer.deserializeLong(),deserializer.deserializeString());
	}
	
}
