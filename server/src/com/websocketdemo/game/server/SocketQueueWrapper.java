package com.websocketdemo.game.server;

import java.util.LinkedList;
import java.util.List;

import org.java_websocket.WebSocket;

import com.badlogic.gdx.utils.Queue;

import io.vertx.core.http.ServerWebSocket;

public class SocketQueueWrapper {
	List<WebSocketWrapper> m_SocketQueue;
	public SocketQueueWrapper()
	{
		m_SocketQueue = new LinkedList<WebSocketWrapper>();
		
	}
	public void  Add(WebSocket webSocket){
		synchronized(this)
		{
				WebSocketWrapper socketWrap = new WebSocketWrapper(webSocket);
				new Thread(socketWrap).start();
				m_SocketQueue.add(socketWrap);
		}
		
	}
	public void  Remove(WebSocket conn){
		synchronized(this)
		{
			WebSocketWrapper toRemove = null;
			for (WebSocketWrapper i : m_SocketQueue){
				if (i.m_Socket.equals(conn)){
					toRemove=i;
				}
			}
			if(toRemove!=null){
				toRemove.Stop();
				m_SocketQueue.remove(toRemove);
			}
		}
	}
	
	
	public WebSocketWrapper get(int i){
		synchronized(this)
		{
			if(i<m_SocketQueue.size()){
				return m_SocketQueue.get(i);
				
			}
			throw new IndexOutOfBoundsException();
		}
	}
	
	public int size(){

			return m_SocketQueue.size();
		
	}
	
}
