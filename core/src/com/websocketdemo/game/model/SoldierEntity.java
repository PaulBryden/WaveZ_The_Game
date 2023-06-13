package com.websocketdemo.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;


public class SoldierEntity extends BaseEntity{

	public SoldierEntity(Body boxBody, int UUID, boolean isLocal){
		super(boxBody,UUID,isLocal);
	}
}