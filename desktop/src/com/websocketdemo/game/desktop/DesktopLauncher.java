package com.websocketdemo.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.websocketdemo.game.WebSocketTechDemoApplication;
import com.websocketdemo.game.WebSocketTechDemo;

import com.github.czyzby.websocket.CommonWebSockets;
public class DesktopLauncher {
	public static void main (String[] arg) {
		CommonWebSockets.initiate();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=1280;
		config.height=720;
		new LwjglApplication(new WebSocketTechDemoApplication(false), config);
	}
}
