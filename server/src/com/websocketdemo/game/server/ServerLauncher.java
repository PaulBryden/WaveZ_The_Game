package com.websocketdemo.game.server;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.websocketdemo.game.model.DataPackets;
import com.websocketdemo.game.model.EntityDataPacket;
import com.websocketdemo.game.model.EntityDataPacketList;
import com.websocketdemo.game.model.EntityPacketQueue;
import com.websocketdemo.game.model.SyncClientTimeDataPacket;
import com.websocketdemo.game.model.UsernameUUIDPair;
import com.websocketdemo.game.model.UsernamesList;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ServerLauncher extends WebSocketServer {
	private ManualSerializer serializer;
    private EntityPacketQueue packetQueue;
    private ServerWrapper m_ServerWrapper;
    private SocketQueueWrapper replySockets;
    private UsernamesList m_UserList;

    private void Constructor(){
		serializer = new ManualSerializer();
		DataPackets.register(serializer);
		packetQueue = new EntityPacketQueue();
		replySockets = new SocketQueueWrapper();
		m_ServerWrapper = new ServerWrapper(packetQueue, replySockets);
		m_UserList=new UsernamesList();
        new Thread(m_ServerWrapper).start();
		
    }
	public ServerLauncher( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
		super.setTcpNoDelay(true);
		Constructor();
	}

	public ServerLauncher( InetSocketAddress address ) {
		super( address );
		super.setTcpNoDelay(true);
		Constructor();
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		//conn.send("Welcome to the server!"); //This method sends a message to the new client

		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the game" );

		replySockets.Add(conn);
		m_ServerWrapper.updateWaveState();
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println("Closing socket: "+conn.getRemoteSocketAddress()+":: "+reason);
		conn.close();
		replySockets.Remove(conn);
		if(replySockets.size()==0){
			m_UserList.m_userList.clear();
		}

	}

	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
		synchronized(this){
			final Object request=serializer.deserialize(message.array());
			
	        if (request instanceof EntityDataPacket) 
	        {
	        	packetQueue.addPacket((EntityDataPacket)request);
	        }
	        else if(request instanceof SyncClientTimeDataPacket)
	        {
	        	SyncClientTimeDataPacket packet = (SyncClientTimeDataPacket)request;
	        	packet.m_ServerTime=TimeUtils.millis();
	        	conn.send(serializer.serialize(packet));
	        	
	        }
	        else if(request instanceof EntityDataPacketList)
	        {        	
	        	EntityDataPacketList timePacket = ((EntityDataPacketList) request);
		    	for (EntityDataPacket i : timePacket.getPacketQueue()){
		            packetQueue.addPacket((EntityDataPacket) i);
		    	}
	        	
	        }
			else if(request instanceof UsernameUUIDPair)
			{
				UsernameUUIDPair userPacket = ((UsernameUUIDPair) request);
				m_UserList.m_userList.addLast(userPacket);

				for (WebSocketWrapper i : replySockets.m_SocketQueue)
				{
					i.AddToQueue(m_UserList);
				}

			}
		}
	}


	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = true;
		int port = 1045; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		ServerLauncher s = new ServerLauncher( port );
		s.start();
		System.out.println("Server started on port: " + s.getPort() );

	}
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			System.out.println("Closing socket "+conn.getRemoteSocketAddress()+"::  because of error");
			conn.close();
			replySockets.Remove(conn);
			if(replySockets.size()==0){
				m_UserList.m_userList.clear();
			}
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}



	@Override
	public void onStart() {

		System.out.println("Server started!");
	}
	@Override
	public void onMessage(WebSocket conn, String message) {
		// TODO Auto-generated method stub
		
	}

}