package com.websocketdemo.game.model;

import java.util.HashSet;

import com.badlogic.gdx.utils.Queue;

public class EntityPacketQueue {
	Queue<EntityDataPacket> packetQueue;
	
	public EntityPacketQueue(){
		packetQueue = new Queue<EntityDataPacket>();
	}
	public void addPacket(EntityDataPacket packet){
		synchronized(this){
				packetQueue.addLast(packet);
			
		}
	}
	
	public EntityDataPacket popPacket() throws NullPointerException{
			if(packetQueue.size==0){
				throw new NullPointerException();
			}
		synchronized(this) {
			return packetQueue.removeFirst();
		}
		
	}
	
	public int size(){
		return packetQueue.size;
	}
	
}
