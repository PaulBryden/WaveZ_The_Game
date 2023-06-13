package com.websocketdemo.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.websocketdemo.game.model.EntityMovementIdentifiers.MovementIdentifiers;

public class EntityMovementKeyEventProcessor implements IEntityMovementEventProcessor {
	
	private UserEntity m_Entity;
	
	public EntityMovementKeyEventProcessor(IEntity iEntity){
		m_Entity = (UserEntity) iEntity;
	}
	
	public void ProcessMoveStartEvent(int forward){
		m_Entity.m_Data.VelocityList[forward] =true;
	}
	
	public void ProcessMoveStopEvent(int identifier){
		m_Entity.m_Data.VelocityList[identifier] = false;	
	}
	
	public void ProcessPointerMoveEvent(float x, float y){

		m_Entity.m_Data.pointerAngleRads = (float) (Math.atan2(
				m_Entity.m_Body.getWorldCenter().y- y,
				m_Entity.m_Body.getWorldCenter().x  - x
			));

	}

	public void CalculateStep(float stepTime){
		m_Entity.m_Body.setTransform(m_Entity.m_Body.getWorldCenter(), m_Entity.m_Data.pointerAngleRads);
		CalculateActualVelocityStep(stepTime);
	}
	
	private void CalculateActualVelocityStep(float stepTime){
		float tempX=0;
		float tempY=0;
		float accelerationX =0;
		float accelerationY =0;
		int directionCount=0;
		
		for (int i =0;i<EntityMovementIdentifiers.xVals.length;i++){
			if(m_Entity.m_Data.VelocityList[i]){

				tempX+=EntityMovementIdentifiers.xVals[i];
				tempY+=EntityMovementIdentifiers.yVals[i];
				directionCount++;
			}
		}
		
		if(directionCount>0){
			tempX=tempX/directionCount;
			tempY=tempY/directionCount;
			Vector2 tempVec = new Vector2(tempX,tempY);
			float angle = (float) ((m_Entity.m_Data.pointerAngleRads*(180f/Math.PI))+90f);
			tempVec.rotate(angle);
			
			accelerationX=(tempVec.x-m_Entity.m_Body.getLinearVelocity().x);
			accelerationY=(tempVec.y-m_Entity.m_Body.getLinearVelocity().y);
			m_Entity.m_Body.setLinearVelocity(m_Entity.m_Body.getLinearVelocity().x+(accelerationX-(EntityMovementIdentifiers.FRICTION)*m_Entity.m_Body.getLinearVelocity().x)*stepTime
					,m_Entity.m_Body.getLinearVelocity().y+(accelerationY-(EntityMovementIdentifiers.FRICTION)*m_Entity.m_Body.getLinearVelocity().y)*stepTime);
		}else{
			m_Entity.m_Body.setLinearVelocity(m_Entity.m_Body.getLinearVelocity().x+(accelerationX-(EntityMovementIdentifiers.FRICTION)*m_Entity.m_Body.getLinearVelocity().x)*stepTime
					,m_Entity.m_Body.getLinearVelocity().y+(accelerationY-(EntityMovementIdentifiers.FRICTION)*m_Entity.m_Body.getLinearVelocity().y)*stepTime);
	
		}
	}
	
}
