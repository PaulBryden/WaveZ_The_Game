package com.websocketdemo.game.model;

import com.github.czyzby.websocket.WebSocket;

public class ThreadSafeSocket {
	WebSocket m_Socket1;
	public ThreadSafeSocket(WebSocket socket1){
		m_Socket1 = socket1;
	}
	
	public void send(Object packet){
		synchronized(this){
			m_Socket1.send(packet);
		}
	}
	public void close(){
		synchronized(this){
			m_Socket1.close();
		}
	}
}
