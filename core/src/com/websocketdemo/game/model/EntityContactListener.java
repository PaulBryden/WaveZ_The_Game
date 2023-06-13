package com.websocketdemo.game.model;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

class EntityContactListener implements ContactListener
{
	EntityListWrapper m_List;
	boolean m_isServer;
	EntityContactListener(EntityListWrapper list, boolean isServer){
		m_List=list;
		m_isServer=isServer;
	}

@Override
public void beginContact(Contact contact) {
	EntityFactoryID id = new EntityFactoryID();
	IEntity A=null;
	IEntity B=null;
	try{
	 A= m_List.getEntityFromBody(contact.getFixtureA().getBody());
	}catch(NullPointerException e){
	}
	try{
		 B= m_List.getEntityFromBody(contact.getFixtureB().getBody());
		}catch(NullPointerException e){
		}
	if(A!=null && A.GetID()==id.Bullet){
		A.damage(10);
		if(B!=null){
			if (B.GetID()==id.Zombie){
				ZombieEntity BZomb= (ZombieEntity)B;
				BZomb.m_audioID=EntityMovementIdentifiers.AudioIdentifiers.zombie_hurt;
			}
			if(m_isServer)
			B.damage(10);
		}
	}
	if(B!=null && B.GetID()==id.Bullet){
		 B.damage(10);
		if(A!=null){
			if (A.GetID()==id.Zombie){
				ZombieEntity AZomb= (ZombieEntity)A;
				AZomb.m_audioID=EntityMovementIdentifiers.AudioIdentifiers.zombie_hurt;
			}
			if(m_isServer)
			A.damage(10);
		}
	}
	
	if(A!=null && A.GetID()==id.Zombie){
		if(B!=null && B.GetID()!=id.Zombie &&B.GetID()!=id.Bullet){
				ZombieEntity AZomb= (ZombieEntity)A;
				AZomb.m_audioID=EntityMovementIdentifiers.AudioIdentifiers.zombie_bite;

				if(m_isServer)
				B.damage(10);
		}
		
	}
	if(B!=null && B.GetID()==id.Zombie){
		if(A!=null && A.GetID()!=id.Zombie &&A.GetID()!=id.Bullet){
			ZombieEntity BZomb= (ZombieEntity)B;
			BZomb.m_audioID=EntityMovementIdentifiers.AudioIdentifiers.zombie_bite;

			if(m_isServer)
			A.damage(10);
		}
	}
	
	
	
}

@Override
public void endContact(Contact contact) {
	// TODO Auto-generated method stub
	
}

@Override
public void preSolve(Contact contact, Manifold oldManifold) {
	// TODO Auto-generated method stub
	
}

@Override
public void postSolve(Contact contact, ContactImpulse impulse) {
	// TODO Auto-generated method stub
	
}
};
