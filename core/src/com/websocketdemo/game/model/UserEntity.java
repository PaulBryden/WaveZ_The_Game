package com.websocketdemo.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;


public class UserEntity extends BaseEntity{
	public UserEntity(Body body, int UUID){
		super(body,UUID,true);
	}
	
	@Override
	public int GetID() {
		/*Weird Quirk with GWT Statics*/
		EntityFactoryID id = new EntityFactoryID();
		return EntityFactoryID.UserSoldier;
		
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public void damage(float amount) {
			m_Health_Update_Time=TimeUtils.millis();
			m_Health-=amount;
		
		
	}
	@Override
	public void setHealth(float health)
	{
		if((m_Health-0.5f)>health){
			if (m_audioID == EntityMovementIdentifiers.AudioIdentifiers.do_nothing) {
				m_audioID = EntityMovementIdentifiers.AudioIdentifiers.man_hurt;
			}
		}
		m_Health=health;
	}


}
