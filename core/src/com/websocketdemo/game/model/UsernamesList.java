package com.websocketdemo.game.model;

import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.serialization.SerializationException;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.Deserializer;
import com.github.czyzby.websocket.serialization.impl.Serializer;


public class UsernamesList implements Transferable<UsernamesList>{
	public Queue<UsernameUUIDPair> m_userList;

	public UsernamesList(){
		m_userList=new Queue<UsernameUUIDPair>();
	}
	public UsernamesList(Queue<UsernameUUIDPair> userList){
		m_userList=userList;
	}
	
	@Override
	public void serialize(Serializer serializer) throws SerializationException {
            synchronized(this) {
            	for (UsernameUUIDPair i : m_userList)
            	{
					serializer.serializeTransferable(i);
				}
            }

		
	}

	@Override
	public UsernamesList deserialize(Deserializer deserializer) throws SerializationException {
		Queue<UsernameUUIDPair> tempUserList = new Queue<UsernameUUIDPair>();

		while(true){
			try{
				UsernameUUIDPair packet = new UsernameUUIDPair();
				tempUserList.addFirst(deserializer.deserializeTransferable(packet));
			}catch(Exception e){
				return new UsernamesList(tempUserList);
			}
		}
	}

	public String getUsername(long UUID){
	    for (UsernameUUIDPair i : m_userList){
	        if (i.UUID==UUID){
	                return i.Username;}
        }
        return "";
    }
	
}
