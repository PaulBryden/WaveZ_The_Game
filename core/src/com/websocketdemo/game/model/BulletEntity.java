package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.websocketdemo.game.model.EntityMovementIdentifiers.AudioIdentifiers;

public class BulletEntity implements IEntity{
float m_Health;
Body m_Body;
int m_UUID;
public EntityMovementData m_Data;
private Queue<EntityDataPacket> m_incomingPacketQueue;
private int m_StepsSinceUpdate;
private long packetEnqueuedTime;
AudioIdentifiers m_audioID;
public BulletEntity(Body boxBody,int UUID){
	m_audioID=AudioIdentifiers.garand_shot;
	m_Body=boxBody;
	m_UUID=UUID;
	m_Data = new EntityMovementData();
	m_incomingPacketQueue = new Queue<EntityDataPacket>();
	m_StepsSinceUpdate=0;
	packetEnqueuedTime=0;
	m_Health=5;

	m_audioID=AudioIdentifiers.garand_shot;
}
	@Override
	public void ProcessStep(float stepTime, boolean isServer) {
		// TODO Auto-generated method stub
		if(m_incomingPacketQueue.size>0){
			velocityCompensate(stepTime,isServer);
		}
	}

	@Override
	public Body getBody() {
		// TODO Auto-generated method stub
		return m_Body;
	}

	@Override
	public EntityMovementData getEntityMovementData() {
		// TODO Auto-generated method stub
		return m_Data;
	}

	@Override
	public int GetID() {
		// TODO Auto-generated method stub
		return EntityFactoryID.Bullet;
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int GetUUID() {
		// TODO Auto-generated method stub
		return m_UUID;
	}

	@Override
	public void parseDataPacket(EntityDataPacket packet) {

		m_StepsSinceUpdate = 0;
		m_incomingPacketQueue.addLast(packet);
		packetEnqueuedTime = TimeUtils.millis();

	}

	@Override
	public void parseDataPacket(EntityDataPacket packet, boolean isServer) {
		// TODO Auto-generated method stub
		parseDataPacket(packet);
		
	}

	@Override
	public int getStepsSinceLastUpdate() {
		// TODO Auto-generated method stub
		return m_StepsSinceUpdate;
	}

	@Override
	public void velocityCompensate(float delta, boolean isServer) {

		EntityDataPacket netPacket = m_incomingPacketQueue.removeLast();
		if(!isServer){
			m_Health=netPacket.m_Health;
		}
		float networkSimTime=0;
		if(isServer)
		{
			networkSimTime=((TimeUtils.millis()- (float)packetEnqueuedTime)+netPacket.m_Delay)/1000;
		}
		else
		{
			networkSimTime = ((TimeUtils.millis()- (float)packetEnqueuedTime)+ServerTime.getMeanDelta())/1000;
		}
		
		Vector2 localSimVector;
		Vector2 networkSimVector;
		if(isServer || !isLocal()){
			this.m_Data.pointerAngleRads=netPacket.m_Data.pointerAngleRads;
			this.m_Data.VelocityList=netPacket.m_Data.VelocityList;
		}
			localSimVector=this.m_Body.getLinearVelocity();
			networkSimVector=netPacket.m_Velocity;

		Vector2 compensatedSimVector = new Vector2((localSimVector.x+networkSimVector.x)/2,(localSimVector.y+networkSimVector.y)/2);

		Vector2 localStartingPosition = this.m_Body.getPosition();
		Vector2 localRestingPosition = new Vector2((localStartingPosition.x+(compensatedSimVector.x*delta)),(localStartingPosition.y+(compensatedSimVector.y*delta)));
		
		Vector2 networkStartingPosition = netPacket.m_Position;
		Vector2 networkRestingPosition = new Vector2((networkStartingPosition.x+(compensatedSimVector.x*networkSimTime)),(networkStartingPosition.y+(networkSimVector.y*networkSimTime)));
		
		Vector2 FinalSimRestingPosition = new Vector2((networkRestingPosition.x+localRestingPosition.x)/2,(networkRestingPosition.y+localRestingPosition.y)/2);
		
		Vector2 finalVelocityVector = new Vector2(((FinalSimRestingPosition.x-localStartingPosition.x)*2),((FinalSimRestingPosition.y-localStartingPosition.y))*2);
		
		m_Body.setLinearVelocity(compensatedSimVector.x+finalVelocityVector.x,compensatedSimVector.y+finalVelocityVector.y);
		

		m_incomingPacketQueue.clear();

		
	}

	@Override
	public EntityDataPacket getDataPacket(boolean isServer) {
		EntityFactoryID id = new EntityFactoryID();
		if(this.m_Body==null){
			return new EntityDataPacket(this.getEntityMovementData(),  new Vector2(0,0), new Vector2(0,0),
					id.Bullet, m_UUID,0,m_Health,0,0);

		}
		// TODO Auto-generated method stub
		Vector2 tempVel;
		if (Float.isNaN(this.getBody().getLinearVelocity().x) || Float.isNaN(this.getBody().getLinearVelocity().y)) {
			tempVel = new Vector2(0, 0);
		} else {
			tempVel = this.getBody().getLinearVelocity();
		}
		if(isServer){
		return new EntityDataPacket(this.getEntityMovementData(), tempVel, this.getBody().getPosition(),
				id.Bullet, m_UUID,0,m_Health,0,0);
		}else{

			return new EntityDataPacket(this.getEntityMovementData(), tempVel, this.getBody().getPosition(),
					id.Bullet, m_UUID,ServerTime.getMeanDelta(),m_Health,0,0);
		}
	}

	@Override
	public void damage(float amount) {
		m_Health-=amount;
		
	}

	@Override
	public float getHealth() {
		// TODO Auto-generated method stub
		return m_Health;
	}
	@Override
	public boolean isFiring() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setFiring(boolean firingState) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setHealth(float health) {
		// TODO Auto-generated method stub
		m_Health=health;
	}
	@Override
	public AudioIdentifiers popAudioState() {
		// TODO Auto-generated method stub
		AudioIdentifiers tempID= m_audioID;
		m_audioID=EntityMovementIdentifiers.AudioIdentifiers.do_nothing;
		return tempID;
	}
	

}
