package com.websocketdemo.game.model;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.websocketdemo.game.model.EntityMovementIdentifiers.AudioIdentifiers;

public interface IEntity {
	
	public IEntityMovementEventProcessor[] EventProcessors = new IEntityMovementEventProcessor[1];
	public void ProcessStep(float stepTime, boolean isServer);
	public Body getBody();
	public EntityMovementData getEntityMovementData();
	public int GetID();
	public boolean isLocal();
	int GetUUID();
	public void parseDataPacket(EntityDataPacket packet);
	void parseDataPacket(EntityDataPacket packet, boolean isServer);
	int getStepsSinceLastUpdate();
	void velocityCompensate(float delta, boolean isServer);
	EntityDataPacket getDataPacket(boolean isServer);
	void damage(float amount);
	float getHealth();
	boolean isFiring();
	void setFiring(boolean firingState);
	void setHealth(float health);
	AudioIdentifiers popAudioState();
	
}

