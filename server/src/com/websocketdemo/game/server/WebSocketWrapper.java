package com.websocketdemo.game.server;

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;

import com.badlogic.gdx.Gdx;
import com.github.czyzby.websocket.serialization.Transferable;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.websocketdemo.game.model.DataPackets;
import com.websocketdemo.game.model.ThreadSafeQueue;


public class WebSocketWrapper implements Runnable{
	
	WebSocket m_Socket;
	ThreadSafeQueue m_Queue;
	boolean m_isRunning;
	ManualSerializer m_serializer;
	WebSocketWrapper(WebSocket socket){
		m_Socket = socket;
		m_isRunning=true;
		m_serializer = new ManualSerializer();
		m_Queue= new ThreadSafeQueue();
		DataPackets.register(m_serializer);
	}

	@Override
	public void run() 
	{
		while(m_isRunning){
			while(m_Queue.size>0){
				try{
					Transferable<?> item	=	PopFromQueue();
					if(item!=null){
						m_Socket.sendFrame(m_Socket.getDraft().createFrames(ByteBuffer.wrap(m_serializer.serialize(item)),true));
				}
				}catch(Exception e){
					Gdx.app.error("Server" ,e.toString());
				}
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Transferable<?> PopFromQueue()
	{
			return m_Queue.removeFirst();
		
	}
	
	public void AddToQueue(Transferable<?> packet)
	{
			if (m_isRunning) {

				m_Queue.addLast(packet);
			}

	}
		
	
	
	public void Stop()
	{
		m_isRunning=false;
		m_Socket.close();
	}
	
	
	
	
}
