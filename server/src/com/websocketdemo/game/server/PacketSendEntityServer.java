package com.websocketdemo.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.websocketdemo.game.model.EntityListWrapper;
import com.websocketdemo.game.model.IEntity;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;

public class PacketSendEntityServer{
	EntityListWrapper m_EntityList;
	PacketSendEntityServer(EntityListWrapper elWrapper){
		m_EntityList=elWrapper;
	}

	public void SendLocalData(ManualSerializer serializer,ServerWebSocket socket){
		
		for(IEntity i : m_EntityList.GetEntityList()){
			socket.writeFinalBinaryFrame(Buffer.buffer(serializer.serialize(i.getDataPacket(true))));
		}
}
	}

