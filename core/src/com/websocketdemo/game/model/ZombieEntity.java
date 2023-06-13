package com.websocketdemo.game.model;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.websocketdemo.game.model.EntityMovementIdentifiers.AudioIdentifiers;

public class ZombieEntity extends BaseEntity {
	EntityListWrapper m_Entities;
	Random m_Rnd;
	public ZombieEntity(Body body, int UUID, boolean isLocal,EntityListWrapper wrapper) {
		super(body, UUID, isLocal);
		m_Entities=wrapper;
		super.m_Data.VelocityList[0] =true;
		super.m_Data.VelocityList[1] =false;
		super.m_Data.VelocityList[2] =false;
		super.m_Data.VelocityList[3] =false;
		super.m_Data.pointerAngleRads=0;
		m_Rnd= new Random(TimeUtils.millis());
		super.m_Health=20+m_Rnd.nextFloat()*80;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void ProcessStep(float stepTime,boolean isServer) {
		IEntity closest = null;
		Vector2 nearVector = new Vector2(500, 500);
		/*FOR GWT Weirdness*/
		if (m_Rnd.nextFloat() > 0.998) {
			if (super.m_audioID == AudioIdentifiers.do_nothing) {
				super.m_audioID = EntityMovementIdentifiers.AudioIdentifiers.zombie_moan;
			}
		}
		EntityFactoryID id = new EntityFactoryID();
		for (IEntity i : m_Entities.GetEntityList()) {
			if (i.GetID() == id.LocalSoldier || i.GetID() == id.Soldier || i.GetID() == id.UserSoldier) {
				if (closest == null) {
					Vector2 tempVector = i.getBody().getPosition();
					tempVector.sub(super.getBody().getPosition());
					if (tempVector.len() < nearVector.len()) {
						nearVector = tempVector;
						closest = i;
					}
				} else {
					Vector2 tempVector = i.getBody().getPosition();
					tempVector.sub(super.getBody().getPosition());
					if (tempVector.len() < nearVector.len()) {
						nearVector = tempVector;
						closest = i;
					}

				}
			}
		}
		if (closest != null) {

			super.m_Data.pointerAngleRads = (float) (Math.atan2(
					closest.getBody().getPosition().y - super.m_Body.getPosition().y,
					closest.getBody().getPosition().x - super.m_Body.getPosition().x
			) + Math.PI);

			//super.m_Data.pointerAngleRads=(float) ((float) (super.getBody().getPosition().angleRad(closest.getBody().getPosition())));
		}
		if (isServer) {
			this.velocityCompensate(stepTime, isServer);
		} else {
			while (m_incomingPacketQueue.size > 0) {

				if (super.serverTimeLastUpdate < m_incomingPacketQueue.last().m_Time) {
					super.serverTimeLastUpdate = m_incomingPacketQueue.last().m_Time;
					super.velocityCompensate(stepTime, isServer);

					m_StepsSinceUpdate=0;
				} else {
					m_incomingPacketQueue.removeLast();
				}


			}

			m_StepsSinceUpdate++;
		}
	}

	@Override
	public boolean isFiring() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int GetID() {
		EntityFactoryID id = new EntityFactoryID();
		return id.Zombie;

	}
	
	@Override
	public EntityDataPacket getDataPacket(boolean isServer) {
		EntityFactoryID id = new EntityFactoryID();
		if(this.m_Body==null){
			return new EntityDataPacket(this.getEntityMovementData(),  new Vector2(0,0), new Vector2(0,0),
					id.Zombie, m_UUID,0,m_Health,0,0);
		}
		Vector2 tempVel;
		if (Float.isNaN(this.getBody().getLinearVelocity().x) || Float.isNaN(this.getBody().getLinearVelocity().y)) {
			tempVel = new Vector2(0, 0);
		} else {
			tempVel = this.getBody().getLinearVelocity();
		}
		if(isServer){
		return new EntityDataPacket(this.getEntityMovementData(), tempVel, this.getBody().getPosition(),
				id.Zombie, m_UUID,0,m_Health,0,0);
		}else{

			return new EntityDataPacket(this.getEntityMovementData(), tempVel, this.getBody().getPosition(),
					id.Zombie, m_UUID,ServerTime.getMeanDelta(),m_Health,0,0);
		}

	}
	

	public void velocityCompensate(float delta,boolean isServer) {

		Vector2 localSimVector;
		localSimVector=super.emulateMotionVectors(this.m_Body.getLinearVelocity(),delta,EntityMovementIdentifiers.xValsZombie,EntityMovementIdentifiers.yValsZombie);
		m_Body.setLinearVelocity(new Vector2(localSimVector.x/2,localSimVector.y/2));
		m_incomingPacketQueue.clear();
	}



}
