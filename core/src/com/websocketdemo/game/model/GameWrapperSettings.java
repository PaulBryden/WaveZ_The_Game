package com.websocketdemo.game.model;

public class GameWrapperSettings {
	static final int RAYS_PER_ENTITY = 128;
	static final int ENTITYNUM = 2;
	static final float LIGHT_DISTANCE = 26f;
	static final float RADIUS = 0.75f;
	
	static final float viewportWidth = 48;
	static final float viewportHeight = 32;

	public final static int MAX_FPS = 30;
	public final static int MIN_FPS = 15;
	public final static float TIME_STEP = 1f / MAX_FPS;
	public final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
	public final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
	public final static int VELOCITY_ITERS = 6;
	public final static int POSITION_ITERS = 2;
}
