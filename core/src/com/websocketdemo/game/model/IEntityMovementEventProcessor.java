package com.websocketdemo.game.model;


public interface IEntityMovementEventProcessor {
	
	
	public void ProcessMoveStartEvent(int forward);
	
	public void ProcessMoveStopEvent(int identifier);
	
	public void ProcessPointerMoveEvent(float x, float y);

	public void CalculateStep(float stepTime);
	
	
}

