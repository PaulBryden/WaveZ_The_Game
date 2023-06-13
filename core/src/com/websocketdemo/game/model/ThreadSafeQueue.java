package com.websocketdemo.game.model;

import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.websocket.serialization.Transferable;

public class ThreadSafeQueue extends Queue<Transferable<?>>{
	
	@Override
	public void addFirst(Transferable<?> object) {
		// TODO Auto-generated method stub
		synchronized(this)
		{
			super.addFirst(object);
		}
	}

	@Override
	public void addLast(Transferable<?> object) {
		// TODO Auto-generated method stub
		synchronized(this)
		{
			super.addLast(object);
		}
	}
	
	@Override
	public Transferable<?> removeFirst(){
		synchronized(this)
		{
			return super.removeFirst();
		}
		
	}
	
	@Override
	public void clear(){
		synchronized(this)
		{
			super.clear();
		}
	}
	
	@Override
	public Transferable<?> removeLast(){
		synchronized(this)
		{
			return super.removeLast();
		}
		
	}


}
