package com.websocketdemo.game.model;

public class EntityMovementIdentifiers {
	public static  enum MovementIdentifiers {
	    Forward, Backward, Left, Right
	}
	public static  enum AudioIdentifiers {
	    do_nothing, garand_shot, zombie_moan, zombie_bite, zombie_hurt, man_hurt
	}
	public static float xVals[] = {0,0,-50,50};
	
	public static float yVals[] = {60,-60,0,0};
	public static float xValsZombie[] = {0,0,-11,11};
	
	public static float yValsZombie[] = {-11,-11,0,0};
	public static float FRICTION = 14;

}
