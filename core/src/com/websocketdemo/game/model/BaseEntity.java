package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.websocketdemo.game.model.EntityMovementIdentifiers.AudioIdentifiers;

public class BaseEntity implements IEntity {
	public Queue<EntityDataPacket> m_incomingPacketQueue;
	public long packetEnqueuedTime;
	public Body m_Body;
	public EntityMovementData m_Data;
	public int m_UUID;
	public int m_StepsSinceUpdate;
	public boolean m_isLocal;
	public float m_Health;
	public boolean m_isFiring;
	public AudioIdentifiers m_audioID;
	public long m_Health_Update_Time;
	private int fireFrequency=0;
	public long serverTimeLastUpdate;
	public BaseEntity(Body body, int UUID, boolean isLocal) {
		m_UUID = UUID;
		m_Body = body;
		m_Data = new EntityMovementData();
		m_StepsSinceUpdate = 0;
		m_incomingPacketQueue = new Queue<EntityDataPacket>();
		packetEnqueuedTime = TimeUtils.millis();
		m_isLocal=isLocal;
		m_Body.setLinearVelocity(0.0001f, 0.0001f);
		m_Body.setLinearDamping(0.12f);
		m_Health=100.000f;
		m_isFiring=false;
		m_audioID=AudioIdentifiers.do_nothing;
		m_Health_Update_Time=TimeUtils.millis();
		serverTimeLastUpdate=0;
	}

	@Override
	public void ProcessStep(float stepTime,boolean isServer) {
		this.m_Body.setTransform(this.m_Body.getWorldCenter(), this.m_Data.pointerAngleRads);
		while(m_incomingPacketQueue.size > 0) {
			if(serverTimeLastUpdate<m_incomingPacketQueue.last().m_Time){
				serverTimeLastUpdate=m_incomingPacketQueue.last().m_Time;
				velocityCompensate(stepTime,isServer);
				m_StepsSinceUpdate=0;
			}else{
				m_incomingPacketQueue.removeLast();
			}

		}
		m_StepsSinceUpdate++;
		fireFrequency++;
	}

	@Override
	public Body getBody() {
		return m_Body;
	}

	@Override
	public EntityMovementData getEntityMovementData() {

		return m_Data;
	}

	@Override
	public int GetUUID() {
		return m_UUID;

	}

	@Override
	public int GetID() {
		EntityFactoryID id = new EntityFactoryID();
		return id.Soldier;

	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return m_isLocal;
	}

	@Override
	public EntityDataPacket getDataPacket(boolean isServer) {
		EntityFactoryID id = new EntityFactoryID();
		if(this.m_Body==null){
			return new EntityDataPacket(this.getEntityMovementData(),  new Vector2(0,0), new Vector2(0,0),
					id.Soldier, m_UUID,0,m_Health,0,0);

		}
		Vector2 tempVel;
		if (Float.isNaN(this.getBody().getLinearVelocity().x) || Float.isNaN(this.getBody().getLinearVelocity().y)) {
			tempVel = new Vector2(0, 0);
		} else {
			tempVel = this.getBody().getLinearVelocity();
		}
		if(isServer){
		return new EntityDataPacket(this.getEntityMovementData(), tempVel, this.getBody().getPosition(),
				id.Soldier, m_UUID,0,m_Health,0,0);
		}else{

			return new EntityDataPacket(this.getEntityMovementData(), tempVel, this.getBody().getPosition(),
					id.Soldier, m_UUID,ServerTime.getMeanDelta(),m_Health,0,0);
		}

	}

	@Override
	public void parseDataPacket(EntityDataPacket packet) {

		m_StepsSinceUpdate = 0;
		m_incomingPacketQueue.addLast(packet);
		packetEnqueuedTime = TimeUtils.millis();


	}

	@Override
	public void parseDataPacket(EntityDataPacket packet, boolean isServer) {
		parseDataPacket(packet);
	}

	@Override
	public int getStepsSinceLastUpdate() {
		return m_StepsSinceUpdate;

	}
	
	protected Vector2 emulateMotionVectors(Vector2 startingVel, float timeStep,float[] xVals,float[] yVals){
		float tempX = 0;
		float tempY = 0;
		float accelerationX = 0;
		float accelerationY = 0;
		int directionCount = 0;
		for (int i = 0; i < 4; i++) {
		    if(m_Data.VelocityList!=null&&m_Data.VelocityList.length>0) {
                if (this.m_Data.VelocityList[i]) {
                    tempX += EntityMovementIdentifiers.xVals[i];
                    tempY += EntityMovementIdentifiers.yVals[i];
                    directionCount++;
                }
            }
		}
		float angle = (float) ((this.m_Data.pointerAngleRads * (180f / Math.PI) + 90f));

		if (directionCount > 0) {
			tempX = tempX / directionCount;
			tempY = tempY / directionCount;
			
		}
		Vector2 tempVec = new Vector2(tempX, tempY);
		tempVec.rotate(angle);

		accelerationX=(tempVec.x-startingVel.x);
		accelerationY=(tempVec.y-startingVel.y);

		return new Vector2(
				startingVel.x + (accelerationX
								- (EntityMovementIdentifiers.FRICTION) * startingVel.x) * timeStep,
				startingVel.y
						+ (accelerationY - (EntityMovementIdentifiers.FRICTION) * startingVel.y)
								* timeStep);
		
	}
	
	@Override
	public void velocityCompensate(float delta,boolean isServer) {

		EntityDataPacket netPacket = m_incomingPacketQueue.removeLast();

		float networkSimTime=0;
		if(isServer)
		{
			networkSimTime=((TimeUtils.millis()- (float)packetEnqueuedTime)+netPacket.m_Delay)/1000;
		}
		else
		{
			networkSimTime = ((TimeUtils.millis()- (float)packetEnqueuedTime)+ServerTime.getMeanDelta())/1000;
		}

		if(!isServer){

			setHealth(netPacket.m_Health);
		}
		
		Vector2 localSimVector;
		Vector2 networkSimVector;
		if(isServer || !isLocal()){
			this.m_Data.pointerAngleRads=netPacket.m_Data.pointerAngleRads;
			this.m_Data.VelocityList=netPacket.m_Data.VelocityList;
			this.m_Body.setTransform(this.m_Body.getWorldCenter(), netPacket.m_Data.pointerAngleRads);
		}


			localSimVector=emulateMotionVectors(this.m_Body.getLinearVelocity(),delta,EntityMovementIdentifiers.xVals,EntityMovementIdentifiers.yVals);
			networkSimVector = emulateMotionVectors(netPacket.m_Velocity,networkSimTime,EntityMovementIdentifiers.xVals,EntityMovementIdentifiers.yVals);
		if(!(isLocal() || isServer)){
			localSimVector=this.m_Body.getLinearVelocity();
			networkSimVector=netPacket.m_Velocity;
		}
		Vector2 compensatedSimVector = new Vector2((localSimVector.x+networkSimVector.x)/2,(localSimVector.y+networkSimVector.y)/2);

		Vector2 localStartingPosition = this.m_Body.getPosition();
		Vector2 localRestingPosition = new Vector2((localStartingPosition.x+(compensatedSimVector.x*delta)),(localStartingPosition.y+(compensatedSimVector.y*delta)));
		
		Vector2 networkStartingPosition = netPacket.m_Position;
		Vector2 networkRestingPosition = new Vector2((networkStartingPosition.x+(compensatedSimVector.x*networkSimTime)),(networkStartingPosition.y+(networkSimVector.y*networkSimTime)));
		
		Vector2 FinalSimRestingPosition = new Vector2((networkRestingPosition.x+localRestingPosition.x)/2,(networkRestingPosition.y+localRestingPosition.y)/2);
		
		
		Vector2 finalVelocityVector = new Vector2(((FinalSimRestingPosition.x-localStartingPosition.x)),((FinalSimRestingPosition.y-localStartingPosition.y)));

			finalVelocityVector.add(compensatedSimVector);
				m_Body.setLinearVelocity(finalVelocityVector.x,finalVelocityVector.y);

	}

	@Override
	public void damage(float amount) {
		m_Health_Update_Time=TimeUtils.millis();
		m_Health-=amount;
		
	}

	@Override
	public float getHealth() {
		
		return m_Health;
	}

	@Override
	public boolean isFiring() {
		// TODO Auto-generated method stub
		return m_isFiring;
	}

	@Override
	public void setFiring(boolean firingState) {
		// TODO Auto-generated method stub
		if(fireFrequency>13 && firingState){
			m_isFiring=firingState;
			fireFrequency =0;
		}else{
			if(!firingState){
				m_isFiring=firingState;

			}
		}
		
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
