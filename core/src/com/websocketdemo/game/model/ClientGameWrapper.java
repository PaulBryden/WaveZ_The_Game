package com.websocketdemo.game.model;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.websocket.data.WebSocketState;
import com.websocketdemo.game.WebSocketTechDemo;
import com.websocketdemo.game.WebSocketTechDemoApplication;
import com.websocketdemo.game.WebSocketTechDemoMainMenu;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketHandler;
import com.github.czyzby.websocket.WebSocketHandler.Handler;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.github.czyzby.websocket.serialization.impl.ManualSerializer;
import com.badlogic.gdx.utils.Timer;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class ClientGameWrapper {

	private final Sound maleHurt;
	private boolean incorrectVersion=false;
	private boolean cannotConnect=false;
	private WebSocket socket1;
	private WebSocket socket2;
	/* This should stay here */
	OrthographicCamera camera;
	float scaleFactor=1;
	/* This should stay here */
	SpriteBatch batch;
	BitmapFont font;
	BitmapFont menuFont;
	/* These should be associated with their relevant entity */
	TextureRegion soldier;
	TextureRegion bullet;
	TextureRegion thumbOverlay;
	TextureRegion zombie;
	TextureRegion touchPadTex;
	Texture bg;
	TextureRegion buttonStartTex;
	TextureRegion buttonHowToTex;
	TextureRegion healthBack;
	TextureRegion healthFront;
	TextureRegion tally;
	TextureRegion tallySlant;
	TextureRegion Logo;
	TextureRegion textBox;
	boolean m_Visible;
	float physicsTimeLeft;
	long aika;
	int times;
	Stage stageClient;
	Stage stageMenu;
	Button moveButton;
	Touchpad touchPad;
	InputMultiplexer inputMultiplexer;
	Button buttonFire;
	Button buttonsToggle;
	Button buttonStart;
	Button buttonHowTo;
	Button m_instructionButton;
	TextField m_Field;
	Label m_Url;
	Label m_Version;
	Image LogoImage;
	EntityFactory m_Factory;
	Sound garandShotSound;
	Sound zombieMoanSound;
	Sound zombieHurtSound;
	Sound zombieBiteSound;
	WaveDataPacket m_WaveDataPacket;
	String m_Username;
	private UsernamesList m_UserIdentifierList;
	/** pixel perfect projection for font rendering */
	Matrix4 normalProjection = new Matrix4();

	boolean showText = true;

	/*
	 * Should be setup in the GameWrapper as it is a setting and passed into the
	 * World?
	 */
	/** BOX2D LIGHT STUFF */
	RayHandler rayHandler;
	PointLight pointLight;

	ArrayList<Light> lights = new ArrayList<Light>(GameWrapperSettings.ENTITYNUM);

	int packetEnqueueCounter;
	EntityPacketQueue m_ReceivePacketQueue;
	private WorldWrapper m_WorldWrapper;
	private UserClient m_Client;
	PacketQueueReceiveHandler packetHandler;
	WebSocketTechDemoApplication m_game;
	ThreadSafeSocket m_ThreadSocket;
	ThreadSafeSocket m_ThreadSocket2;
	ThreadSafeQueue packetSendQueue;
	private TextureRegion sight;
	private TextureRegion cross;
	Music music;
	private ThreadSafeQueue packetSendQueue2;
	int m_GameState;
	boolean m_isMobile;

	private void handleStepped(){
		if (packetEnqueueCounter < 10) {
			packetEnqueueCounter++;

		} else {
			packetEnqueueCounter=0;
			if(packetSendQueue.size<packetSendQueue2.size &&packetSendQueue.size<7){
				//packetSendQueue.addLast(m_WorldWrapper.getSerializedWorld());
				EntityDataPacketList list = new EntityDataPacketList();
				for (EntityDataPacket i: m_WorldWrapper.getEntities().getLocalEntities(false)){
					list.getPacketQueue().addFirst(i);
				}
				packetSendQueue.addLast(list);
			}else if(packetSendQueue2.size<7){
				EntityDataPacketList list = new EntityDataPacketList();
				for (EntityDataPacket i: m_WorldWrapper.getEntities().getLocalEntities(false)){
					list.getPacketQueue().addFirst(i);
				}
				packetSendQueue2.addLast(list);

			}
		}
		int userState=m_WorldWrapper.validateActiveUsers();
		if (userState>0) {
			m_Visible = false;
			m_ThreadSocket.close();
			m_ThreadSocket2.close();
			String stateMessage;
			if(userState==UserDisconnectState.Disconnected){
				stateMessage="You Disconnected from the Server.";
			}else{
				stateMessage="You are dead... You made it to round "+m_WaveDataPacket.m_waveNumber;

			}
			m_game.setScreen(new WebSocketTechDemoMainMenu(m_game,stateMessage,m_isMobile));
			m_game.getScreen().dispose();
		}
	}

	private void setupClient(){
		Random rand = new Random();
		stageClient = new Stage();
		m_Client = new UserClient(m_Factory, camera, rand.nextFloat()*23, rand.nextFloat()*23, rand.nextInt(),inputMultiplexer,m_isMobile);
		m_WorldWrapper.addEntity(m_Client.GetUser());
		packetEnqueueCounter = 0;
		packetSendQueue = new ThreadSafeQueue();
		packetSendQueue2 = new ThreadSafeQueue();
		m_ReceivePacketQueue = new EntityPacketQueue();
		packetHandler = new PacketQueueReceiveHandler(m_ReceivePacketQueue, m_WorldWrapper.getEntities());
		TextureRegionDrawable thumbStick= new TextureRegionDrawable(thumbOverlay);
		TextureRegionDrawable buttonFireText= new TextureRegionDrawable(sight);
		TextureRegionDrawable toggleButton = new TextureRegionDrawable(cross);
		TextureRegionDrawable toggleTouchpad = new TextureRegionDrawable(touchPadTex);
		moveButton = new Button(thumbStick);
		buttonFire = new Button(buttonFireText);
		buttonsToggle = new Button(toggleButton);
		touchPad = new Touchpad(3f,new Touchpad.TouchpadStyle(toggleTouchpad,toggleTouchpad));

		buttonFire.setBounds(Gdx.graphics.getWidth()-(90*scaleFactor),30*scaleFactor, 60*scaleFactor, 60*scaleFactor);
		moveButton.setBounds(Gdx.graphics.getWidth()-170*scaleFactor,30*scaleFactor, 80*scaleFactor, 80*scaleFactor);
		touchPad.setBounds(50*scaleFactor,30*scaleFactor, 60*scaleFactor,60*scaleFactor);
		buttonsToggle.setBounds(Gdx.graphics.getWidth()-(50*scaleFactor),Gdx.graphics.getHeight()-40*scaleFactor, 30*scaleFactor, 30*scaleFactor);

		m_UserIdentifierList= new UsernamesList();
		if(!m_isMobile) {
			buttonFire.setVisible(false);
			moveButton.setVisible(false);
			touchPad.setVisible(false);
		}

		buttonsToggle.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				m_Visible = false;
				m_ThreadSocket.close();
				m_ThreadSocket2.close();
				m_game.setScreen(new WebSocketTechDemo(m_game,m_isMobile));
				return true;
			}
		});
		buttonFire.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				m_Client.clientEntity.setFiring(true);
				return true;
			}
		});
		moveButton.addListener(new ClickListener() {
			EntityMovementKeyEventProcessor m_EventProcessor = new EntityMovementKeyEventProcessor(m_Client.GetUser());
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

				m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Forward.ordinal());
				m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Backward.ordinal());
				m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Left.ordinal());
				m_EventProcessor.ProcessMoveStopEvent(EntityMovementIdentifiers.MovementIdentifiers.Right.ordinal());

			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				m_EventProcessor.ProcessMoveStartEvent(EntityMovementIdentifiers.MovementIdentifiers.Forward.ordinal());

				return true;
			}
		});
		touchPad.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// This is run when anything is changed on this actor.
				float deltaX = ((Touchpad) actor).getKnobPercentX();
				float deltaY = ((Touchpad) actor).getKnobPercentY();
				Vector2 vec = new Vector2(deltaX,deltaY);
				if(!((deltaX<0.1 && deltaX>-0.1) && (deltaY<0.1 && deltaY>-0.1))) {
					m_Client.clientEntity.m_Data.pointerAngleRads = vec.angleRad()+(float)Math.PI;
				}

			}
		});
		stageClient.addActor(touchPad);
		stageClient.addActor(moveButton);
		stageClient.addActor(buttonFire);
		stageClient.addActor(buttonsToggle);
		inputMultiplexer.addProcessor(stageClient);
		Gdx.input.setInputProcessor(inputMultiplexer);
		try
		{
			socket1.connect();
			setupSocket(socket1);
		}
		catch(Exception e)
		{
			Gdx.app.debug("1",e.getMessage());
			cannotConnect=true;
			return;
		}
		try
		{
			socket2.connect();
			setupSocket(socket2);
		}
		catch(Exception e)
		{
			Gdx.app.debug("1",e.getMessage());
			cannotConnect=true;
			return;
		}
		m_ThreadSocket = new ThreadSafeSocket(socket1);
		m_ThreadSocket2 = new ThreadSafeSocket(socket2);
		Timer.instance().scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				SyncClientTimeDataPacket packetReturn = new SyncClientTimeDataPacket(TimeUtils.millis(), 0);
					packetSendQueue.addFirst(new UsernameUUIDPair(m_Client.clientEntity.m_UUID,m_Username));
					//packetSendQueue.addFirst(packetReturn);
							WriteNetworkQueue(m_ThreadSocket,packetSendQueue);
								WriteNetworkQueue(m_ThreadSocket2,packetSendQueue2);


			}
		}, 1f);

	}

	private void setupSocket(WebSocket socket){
		socket.addListener(getEntityListListener());
		socket.addListener(getTimeListener());
		socket.addListener(getEntityListener());
		socket.addListener(getWaveListener());
		socket.addListener(getUserList());
		socket.addListener(getVersionPacketListener());

		final ManualSerializer serializer = new ManualSerializer();
		socket.setSerializer(serializer);
		DataPackets.register(serializer);
	}

	public ClientGameWrapper(WebSocketTechDemoApplication game, int gameState,boolean isMobile) {
		/**
		 * OPERATIONS FOR ALL VERSIONS OF THE GAME
		 */
		ServerTime.Wipe();

		socket1 = ExtendedNet.getNet().newWebSocket("www.wavezthegame.online", 1045, "socket1");
		socket2 = ExtendedNet.getNet().newWebSocket("www.wavezthegame.online", 1045, "socket2");

		garandShotSound = Gdx.audio.newSound(Gdx.files.internal("data/garand-shot.mp3"));
		zombieMoanSound = Gdx.audio.newSound(Gdx.files.internal("data/zombie-moan.mp3"));
		zombieHurtSound = Gdx.audio.newSound(Gdx.files.internal("data/zombie-hurt.mp3"));
		zombieBiteSound = Gdx.audio.newSound(Gdx.files.internal("data/zombie-bite.mp3"));
		maleHurt = Gdx.audio.newSound(Gdx.files.internal("data/jab.mp3"));
		m_WaveDataPacket = new WaveDataPacket();
		inputMultiplexer = new InputMultiplexer();
		m_Visible = true;
		m_game = game;
		camera = new OrthographicCamera(GameWrapperSettings.viewportWidth, GameWrapperSettings.viewportHeight);
		camera.position.set(0, GameWrapperSettings.viewportHeight / 2f, 0);
		camera.update();
		m_WorldWrapper = new WorldWrapper(false);

		m_Factory = new EntityFactory(m_WorldWrapper.getWorld(),m_WorldWrapper.getEntities());
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(new Color(1f,1f,1f,0.3f));
		font.setUseIntegerPositions(false);
		font.getData().setScale(0.1f);
		scaleFactor = Gdx.graphics.getBackBufferHeight()/360f;
		System.out.println(Gdx.graphics.getHeight());
		menuFont = new BitmapFont();
		menuFont.getData().setScale(1.2f*scaleFactor);
		menuFont.setUseIntegerPositions(false);
		thumbOverlay=new TextureRegion(new Texture(Gdx.files.internal("data/pointer.png")));
		soldier = new TextureRegion(new Texture(Gdx.files.internal("data/survivor.png")));
		bullet = new TextureRegion(new Texture(Gdx.files.internal("data/bullet.png")));
		cross = new TextureRegion(new Texture(Gdx.files.internal("data/cross.png")));
		sight = new TextureRegion(new Texture(Gdx.files.internal("data/sight.png")));
		zombie = new TextureRegion(new Texture(Gdx.files.internal("data/zombie.png")));
		bg = new Texture(Gdx.files.internal("data/bg.jpg"));
		touchPadTex = new TextureRegion(new Texture(Gdx.files.internal("data/touchpad.png")));
		buttonStartTex = new TextureRegion(new Texture(Gdx.files.internal("data/start-button.png")));
		buttonHowToTex = new TextureRegion(new Texture(Gdx.files.internal("data/how-to-button.png")));
		tally = new TextureRegion(new Texture(Gdx.files.internal("data/tally.png")));
		tallySlant = new TextureRegion(new Texture(Gdx.files.internal("data/tally-slant.png")));
		healthBack= new TextureRegion(new Texture(Gdx.files.internal("data/health-bar.png")));
		healthFront = new TextureRegion(new Texture(Gdx.files.internal("data/health-bar-percent.png")));
		textBox = new TextureRegion(new Texture(Gdx.files.internal("data/text-box.png")));
		Logo = new TextureRegion(new Texture(Gdx.files.internal("data/logo.png")));
		m_GameState=gameState;
		m_isMobile=isMobile;

		if(m_GameState==GameState.Playing) {
			setupClient();
		}else if(m_GameState==GameState.Menu){
			setupMenu();
		}
		normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		/** BOX2D LIGHT START */
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(m_WorldWrapper.getWorld());
		initLights();
		music = Gdx.audio.newMusic(Gdx.files.internal("data/ambient.mp3"));
		music.setVolume(0.5f);                 // sets the volume to half the maximum volume
		music.setLooping(true);                // will repeat playback until music.stop() is called
		music.play();                          // resumes the playback

	}

	private void teardownMenu(){
		while(m_WorldWrapper.getEntities().GetEntityList().size()>0)
		{
			IEntity entity=m_WorldWrapper.getEntities().GetEntityList().get(0);
			m_WorldWrapper.getEntities().RemoveEntity(entity);
			m_WorldWrapper.getWorld().destroyBody(entity.getBody());
		}
		buttonStart.setVisible(false);
		buttonHowTo.setVisible(false);
		LogoImage.setVisible(false);
		m_Field.setVisible(false);
		m_Url.setVisible(false);
		m_Version.setVisible(false);

		inputMultiplexer.removeProcessor(stageMenu);
	}

	private void setupMenu() {
		Random rnum = new Random();
		int switcheroo=1;
		try {
			for(int i=0;i<24;i++) {
				IEntity e =m_Factory.CreateEntity(EntityFactoryID.Zombie, (float) switcheroo*(rnum.nextFloat()*22f), (rnum.nextFloat()*23f), (int) (rnum.nextFloat() * 1000000), true);
				e.getBody().setTransform(e.getBody().getPosition(),(float)(rnum.nextFloat()*2*3.14));
				m_WorldWrapper.addEntity(e);
				switcheroo*=-1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		stageMenu = new Stage();

		TextureRegionDrawable startButton= new TextureRegionDrawable(buttonStartTex);
		TextureRegionDrawable howButton= new TextureRegionDrawable(buttonHowToTex);
        TextureRegionDrawable tallyTexture= new TextureRegionDrawable(tally);
		TextureRegionDrawable textTexture=new TextureRegionDrawable(textBox);
        tallyTexture.setLeftWidth(1f);
		tallyTexture.setRightWidth(1f);
		TextureRegionDrawable healthTexture= new TextureRegionDrawable(healthFront);
		buttonStart = new Button(startButton);
		buttonHowTo = new Button(howButton);
		LogoImage = new Image(Logo);
		TextField.TextFieldStyle tempStyle=new TextField.TextFieldStyle(menuFont,new Color(0.68f,0,0,1f),tallyTexture,healthTexture,textTexture);
		m_Field = new TextField("Default User",tempStyle);
		m_Field.setMaxLength(12);
		m_Field.setAlignment(Align.center);
		m_Url = new Label("Credits", new Label.LabelStyle(menuFont,new Color(0.68f,0,0,1f)));
		m_Version = new Label("v "+Version.VersionNumber, new Label.LabelStyle(menuFont,new Color(0.68f,0,0,1f)));
		stageMenu.addActor(buttonStart);
		stageMenu.addActor(buttonHowTo);
		stageMenu.addActor(LogoImage);
		stageMenu.addActor(m_Url);
		stageMenu.addActor(m_Version);
		buttonHowTo.setBounds(((Gdx.graphics.getWidth()/2)-96*scaleFactor) ,(Gdx.graphics.getHeight()-300*scaleFactor), 192*scaleFactor, 48*scaleFactor);
		buttonStart.setBounds(((Gdx.graphics.getWidth()/2)-96*scaleFactor),(Gdx.graphics.getHeight()-240*scaleFactor), 192*scaleFactor, 48*scaleFactor);
		LogoImage.setBounds(((Gdx.graphics.getWidth()/2)-120*scaleFactor),(Gdx.graphics.getHeight()-100*scaleFactor), 240*scaleFactor, 75*scaleFactor);
		m_Field.setPosition((Gdx.graphics.getWidth()/2)-96*scaleFactor ,(Gdx.graphics.getHeight()-180*scaleFactor));
		m_Field.setSize(192*scaleFactor, 48*scaleFactor);
		m_Url.setPosition((Gdx.graphics.getWidth()*0.85f) ,(Gdx.graphics.getHeight()-30*scaleFactor));
		m_Version.setPosition((Gdx.graphics.getWidth()*0.85f) ,(25*scaleFactor));
		buttonStart.setVisible(true);
		buttonHowTo.setVisible(true);
		LogoImage.setVisible(true);
		m_Url.setVisible(true);
		m_Version.setVisible(true);
		if(m_isMobile)
		{
			m_instructionButton = new Button(new TextureRegionDrawable(new TextureRegion(new Texture("data/instructions_touch.png"))));
		}
		else
		{
			m_instructionButton = new Button(new TextureRegionDrawable(new TextureRegion(new Texture("data/instructions.png"))));

		}
		m_instructionButton.setBounds((((Gdx.graphics.getWidth()-455*scaleFactor)/2) ),((Gdx.graphics.getHeight()-300*scaleFactor)/2), 450*scaleFactor, 300*scaleFactor);
		stageMenu.addActor(m_instructionButton);
		stageMenu.addActor(m_Field);
		m_instructionButton.setVisible(false);
		m_instructionButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				m_instructionButton.setVisible(false);
				buttonStart.setVisible(true);
				buttonHowTo.setVisible(true);
				LogoImage.setVisible(true);
				m_Field.setVisible(true);
				m_Url.setVisible(true);
				m_Version.setVisible(true);
				return false;
			}
		});
		buttonHowTo.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				m_instructionButton.setVisible(true);
				buttonStart.setVisible(false);
				buttonHowTo.setVisible(false);
				LogoImage.setVisible(false);
				m_Field.setVisible(false);

				return false;
			}
		});
		m_Url.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.net.openURI("http://www.wavezthegame.online/credits.html");
				return false;
			}
		});


		buttonStart.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				m_Username=m_Field.getText();
				teardownMenu();
				m_GameState=GameState.Playing;
				setupClient();
				return false;
			}
		});

		inputMultiplexer.addProcessor(stageMenu);
		Gdx.input.setInputProcessor(inputMultiplexer);
		m_Field.setCursorPosition(m_Field.getText().length());
		stageMenu.setKeyboardFocus(m_Field);

	}

	private WebSocketListener getEntityListener() {
		// WebSocketHandler is an implementation of WebSocketListener that uses
		// the current Serializer (ManualSerializer
		// in this case) to create objects from received raw data. Instead of
		// forcing you to work with Object and do
		// manual casting, this listener allows to register handlers for each
		// expected packet class.
		final WebSocketHandler m_EntityDataPacketHandler = new WebSocketHandler();
		// Registering Ping handler:
		m_EntityDataPacketHandler.registerHandler(EntityDataPacket.class, new Handler<EntityDataPacket>() {
			@Override
			public boolean handle(final WebSocket webSocket, final EntityDataPacket packet) {

				m_ReceivePacketQueue.addPacket(packet);

				return true;

			}

		});
		// Side note: this would be a LOT cleaner with Java 8 lambdas (or using
		// another JVM language, like Kotlin).
		return m_EntityDataPacketHandler;
	}


	private WebSocketListener getVersionPacketListener() {
		// WebSocketHandler is an implementation of WebSocketListener that uses
		// the current Serializer (ManualSerializer
		// in this case) to create objects from received raw data. Instead of
		// forcing you to work with Object and do
		// manual casting, this listener allows to register handlers for each
		// expected packet class.
		final WebSocketHandler m_VersionPacketHandler = new WebSocketHandler();
		// Registering Ping handler:
		m_VersionPacketHandler.registerHandler(VersionDataPacket.class, new Handler<VersionDataPacket>() {
			@Override
			public boolean handle(final WebSocket webSocket, final VersionDataPacket packet) {

				if(!packet.m_Version.equals(Version.VersionNumber))
				{
					incorrectVersion=true;

				}
				return true;

			}

		});
		// Side note: this would be a LOT cleaner with Java 8 lambdas (or using
		// another JVM language, like Kotlin).
		return m_VersionPacketHandler;
	}

	private WebSocketListener getUserList()
	{
		// WebSocketHandler is an implementation of WebSocketListener that uses
		// the current Serializer (ManualSerializer
		// in this case) to create objects from received raw data. Instead of
		// forcing you to work with Object and do
		// manual casting, this listener allows to register handlers for each
		// expected packet class.
		final WebSocketHandler userListHandler = new WebSocketHandler();
		// Registering Ping handler:
		userListHandler.registerHandler(UsernamesList.class, new Handler<UsernamesList>() {
			@Override
			public boolean handle(final WebSocket webSocket, final UsernamesList packet) {

				synchronized(this) {
					m_UserIdentifierList = packet;
					return true;
				}

			}

		});
		// Side note: this would be a LOT cleaner with Java 8 lambdas (or using
		// another JVM language, like Kotlin).
		return userListHandler;
	}


	private WebSocketListener getEntityListListener() {
		// WebSocketHandler is an implementation of WebSocketListener that uses
		// the current Serializer (ManualSerializer
		// in this case) to create objects from received raw data. Instead of
		// forcing you to work with Object and do
		// manual casting, this listener allows to register handlers for each
		// expected packet class.
		final WebSocketHandler m_EntityDataPacketHandler = new WebSocketHandler();
		// Registering Ping handler:
		m_EntityDataPacketHandler.registerHandler(EntityDataPacketList.class, new Handler<EntityDataPacketList>() {
			@Override
			public boolean handle(final WebSocket webSocket, final EntityDataPacketList packetList) {
					for (EntityDataPacket i : packetList.getPacketQueue()) {
						m_ReceivePacketQueue.addPacket(i);
					}
				return true;
			}

		});
		// Side note: this would be a LOT cleaner with Java 8 lambdas (or using
		// another JVM language, like Kotlin).
		return m_EntityDataPacketHandler;
	}

	private WebSocketListener getTimeListener() {
		final WebSocketHandler m_TimePacketHandler = new WebSocketHandler();
		m_TimePacketHandler.registerHandler(SyncClientTimeDataPacket.class, new Handler<SyncClientTimeDataPacket>() {
			@Override
			public boolean handle(final WebSocket webSocket, final SyncClientTimeDataPacket packet) {
				ServerTime.serverTimeSync=packet.m_ServerTime;
				ServerTime.LocalTimeSync=(long) (packet.m_LocalTime+ServerTime.getMeanDelta());

				synchronized(this){
					ServerTime.AddDelta((TimeUtils.millis() - packet.m_LocalTime) / 2);
				}
				Timer.instance().scheduleTask(new Timer.Task() {
					@Override
					public void run() {
						synchronized(this){
							if (m_Visible) {

								SyncClientTimeDataPacket packetReturn = new SyncClientTimeDataPacket(TimeUtils.millis(), 0);
								packetSendQueue.addFirst(packetReturn);
							}
						}

					}
				}, 4f);

				return true;
			}

		});
		// Side note: this would be a LOT cleaner with Java 8 lambdas (or using
		// another JVM language, like Kotlin).
		return m_TimePacketHandler;
	}

	private WebSocketListener getWaveListener() {
		final WebSocketHandler m_WavePacketHandler = new WebSocketHandler();
		m_WavePacketHandler.registerHandler(WaveDataPacket.class, new Handler<WaveDataPacket>() {
			@Override
			public boolean handle(final WebSocket webSocket, final WaveDataPacket packet) {

						synchronized(this){
							if (m_Visible) {
								m_WaveDataPacket=packet;
							}
						}
			return false;
			}

		});
		// Side note: this would be a LOT cleaner with Java 8 lambdas (or using
		// another JVM language, like Kotlin).
		return m_WavePacketHandler;
	}

	public void renderMenu(){

		Random rnum = new Random();
		camera.position.set(new Vector3(0f,
				13f, 0f));
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		for (IEntity i : m_WorldWrapper.getEntities().GetEntityList()){
				i.getBody().getAngle();
			Vector2 force = new Vector2((float)(Math.cos(i.getBody().getAngle()) * (rnum.nextFloat())*-2f), (float)(Math.sin(i.getBody().getAngle()) * (rnum.nextFloat())*-2f));
			i.getBody().setAngularVelocity(((rnum.nextFloat()-0.5f)*3));
				i.getBody().applyForceToCenter(force,true);
		}
		batch.begin();
		{
			batch.draw(bg, -GameWrapperSettings.viewportWidth / 2f, 0, GameWrapperSettings.viewportWidth,
					GameWrapperSettings.viewportHeight);
			batch.enableBlending();
			for (int i = 0; i < m_WorldWrapper.getEntities().GetEntityList().size(); i++) {
				IEntity p_entity = m_WorldWrapper.getEntities().GetEntityList().get(i);
				Vector2 position = p_entity.getBody().getPosition();
				float angle = MathUtils.radiansToDegrees * p_entity.getBody().getAngle();

				playAudio(p_entity);


				if (p_entity.GetID() == EntityFactoryID.UserSoldier || p_entity.GetID() == EntityFactoryID.Soldier || p_entity.GetID() == EntityFactoryID.Zombie) {

					if (p_entity.GetID() == EntityFactoryID.Zombie) {
						batch.draw(zombie, position.x - GameWrapperSettings.RADIUS,
								position.y - GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS,
								GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS * 2,
								GameWrapperSettings.RADIUS * 2, 2f, 2f, angle);

					} else {
						batch.draw(soldier, position.x - GameWrapperSettings.RADIUS,
								position.y - GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS,
								GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS * 2,
								GameWrapperSettings.RADIUS * 2, 2f, 2f, angle);
						//font.draw(batch, Integer.toString((int)p_entity.getHealth()), position.x- GameWrapperSettings.RADIUS*0.5f, position.y- GameWrapperSettings.RADIUS);

					}
				}
			}
		}
		batch.end();
		rayHandler.setCombinedMatrix(camera);
		clearLights();
		createLightFlicker(rnum);
		rayHandler.update();
		rayHandler.render();
		/*Add Staging*/

		stageMenu.act(Gdx.graphics.getDeltaTime());
		stageMenu.draw();



	}
	private void createLightFlicker(Random rnum)
	{
		PointLight light = new PointLight(rayHandler,30);
		light.setDistance((rnum.nextFloat()*4)+14);
		light.setPosition(0,13);
		light.setColor(255, 255, 255, (int)(rnum.nextFloat()*3 )+2);
		lights.add(light);

	}
	public void renderRunning(boolean stepped){

		if(incorrectVersion){
			m_Visible = false;
			m_ThreadSocket.close();
			m_ThreadSocket2.close();
			String stateMessage="Error: You are running an old version of the game. Please Update.";
			m_game.setScreen(new WebSocketTechDemoMainMenu(m_game,stateMessage,m_isMobile));
			return;
		}else if(cannotConnect){
			m_Visible = false;
			String stateMessage="Error:: Could not connect to the server. Try again.";
			m_game.setScreen(new WebSocketTechDemoMainMenu(m_game,stateMessage,m_isMobile));
			cannotConnect=false;
		}

		ArrayList<IEntity>  rayCastBodies = new ArrayList<IEntity>();
		if (!m_Client.GetUser().m_Body.getWorldCenter()
				.epsilonEquals(new Vector2(camera.position.x, camera.position.y), 4f)) {
			camera.position.set(new Vector3(m_Client.GetUser().m_Body.getWorldCenter().x,
					m_Client.GetUser().m_Body.getWorldCenter().y, 0f));

		}

		camera.position.set(new Vector3(m_Client.GetUser().m_Body.getWorldCenter().x,
				m_Client.GetUser().m_Body.getWorldCenter().y, 0f));
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.disableBlending();
		batch.begin();
			{
				batch.draw(bg, -GameWrapperSettings.viewportWidth / 2f, 0, GameWrapperSettings.viewportWidth,
						GameWrapperSettings.viewportHeight);
				batch.enableBlending();
			for (int i = 0; i < m_WorldWrapper.getEntities().GetEntityList().size(); i++) {
				IEntity p_entity = m_WorldWrapper.getEntities().GetEntityList().get(i);
				Vector2 position = p_entity.getBody().getPosition();
				float angle = MathUtils.radiansToDegrees * p_entity.getBody().getAngle();

				playAudio(p_entity);


				if (p_entity.GetID() == EntityFactoryID.UserSoldier || p_entity.GetID() == EntityFactoryID.Soldier || p_entity.GetID()==EntityFactoryID.Zombie){

					if(p_entity.GetID()==EntityFactoryID.Zombie)
					{
						batch.draw(zombie, position.x - GameWrapperSettings.RADIUS,
								position.y - GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS,
								GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS * 2,
								GameWrapperSettings.RADIUS * 2, 2f, 2f, angle);

					}else
					{
						batch.draw(soldier, position.x - GameWrapperSettings.RADIUS,
								position.y - GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS,
								GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS * 2,
								GameWrapperSettings.RADIUS * 2, 2f, 2f, angle);

					}
					if(p_entity.GetID() == EntityFactoryID.Soldier ||p_entity.GetID() == EntityFactoryID.Zombie){
						//font.draw(batch, Integer.toString((int)p_entity.getHealth()), position.x- GameWrapperSettings.RADIUS*0.5f, position.y- GameWrapperSettings.RADIUS);

					}

				}else if(p_entity.GetID() ==EntityFactoryID.Bullet){
					batch.draw(bullet, position.x - GameWrapperSettings.RADIUS,
							position.y - GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS,
							0.55f, 0.55f,
							0.55f, 0.55f, 0.55f, angle);
				}
			}
		}
		batch.end();


		if (stepped) {

			handleStepped();
			rayHandler.setCombinedMatrix(camera);
			clearLights();

			initLights();

			rayHandler.update();
			/** BOX2D LIGHT START */

		}


		rayHandler.render();
		batch.enableBlending();
		batch.begin();

			IEntity p_entity = m_Client.clientEntity;
			Vector2 position = p_entity.getBody().getPosition();
			if (p_entity.GetID() == EntityFactoryID.UserSoldier) {

				batch.draw(healthBack, (position.x - (GameWrapperSettings.viewportWidth / 2)) + (1f) , position.y + ((GameWrapperSettings.viewportHeight / 2)) - 1.5f , 16f , 0.8f );
				batch.draw(healthFront, (position.x - (GameWrapperSettings.viewportWidth / 2)) + (1.1f) , position.y + ((GameWrapperSettings.viewportHeight / 2)) - 1.4f , (p_entity.getHealth() / 100) * 15.8f , 0.6f );
				for (int i = 0; i < m_WaveDataPacket.m_waveNumber; i++) {
					if ((i + 1) % 5 == 0) {
						batch.draw(tallySlant, (position.x - (GameWrapperSettings.viewportWidth / 2)) + (((i-3f))) , position.y + ((GameWrapperSettings.viewportHeight / 2)) - 4.5f , 3.5f , 2.5f );
					} else {
						//batch.draw(tally,(position.x-(GameWrapperSettings.viewportWidth/2))+(1f+i)*scaleFactor, position.y+((GameWrapperSettings.viewportHeight/2))-2f*scaleFactor,0.2f*scaleFactor,1f*scaleFactor);
						batch.draw(tally, (position.x - (GameWrapperSettings.viewportWidth / 2)) + (1f + i) , position.y + ((GameWrapperSettings.viewportHeight / 2)) - 4.5f , 0.5f , 2.5f );
					}

				}

				Vector2 tempSightVector = new Vector2(0f, 8f);
				tempSightVector.rotate((float) ((p_entity.getBody().getAngle() / Math.PI) * 180) + 90f);
				batch.draw(sight, (position.x - GameWrapperSettings.RADIUS) + tempSightVector.x,
						(position.y - GameWrapperSettings.RADIUS) + tempSightVector.y, GameWrapperSettings.RADIUS,
						GameWrapperSettings.RADIUS, GameWrapperSettings.RADIUS * 2,
						GameWrapperSettings.RADIUS * 2, 2f, 2f, 0);

				Vector2 sightPosition = new Vector2(tempSightVector.x + position.x, tempSightVector.y + position.y);


				for (IEntity i : m_WorldWrapper.getEntities().GetEntityList()) {
					if(i.GetID()==EntityFactoryID.Zombie || i.GetID()==EntityFactoryID.Soldier) {
						Vector2 compareVector;
						compareVector = new Vector2(p_entity.getBody().getPosition());
						compareVector.sub(i.getBody().getPosition());
						float angle=compareVector.angleRad();
						float otherAngle=p_entity.getEntityMovementData().pointerAngleRads;
						if(angle<0){
							angle=(2*(float)Math.PI)+angle;
						}

						if(otherAngle<0){
							otherAngle=(2*(float)Math.PI)+p_entity.getEntityMovementData().pointerAngleRads;
						}
						if (compareVector.len() < GameWrapperSettings.LIGHT_DISTANCE/2) {
							if (angle > otherAngle -0.3 && angle< otherAngle + (0.3)) {


								batch.draw(healthBack, i.getBody().getPosition().x - (1.5f), i.getBody().getPosition().y - (1f ), 3f , 0.2f );
								batch.draw(healthFront, i.getBody().getPosition().x - (1.5f), i.getBody().getPosition().y - (1f ), (i.getHealth()/100)*3f , 0.2f );							}
								String username = m_UserIdentifierList.getUsername(i.getDataPacket(false).m_UUID);
							if(i.GetID()==EntityFactoryID.Soldier){
								font.draw(batch, m_UserIdentifierList.getUsername(i.getDataPacket(false).m_UUID), i.getBody().getPosition().x - (0.3f * username.length()), i.getBody().getPosition().y + GameWrapperSettings.RADIUS * 2.5f);
							}
						}
					}



				}

			}
		batch.end();

		stageClient.act(Gdx.graphics.getDeltaTime());
		stageClient.draw();
	}
	public void render() {
		if (m_Visible) {
			boolean stepped = fixedStep(Gdx.graphics.getDeltaTime());
			Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			if(m_GameState==GameState.Playing) {
				renderRunning(stepped);
			}
			else if(m_GameState==GameState.Menu)
			{
				renderMenu();
			}
			/** BOX2D LIGHT END */

			long time = TimeUtils.nanoTime();

			aika += TimeUtils.nanoTime() - time;
		}
	}

	private void playAudio(IEntity p_entity) {
		// TODO Auto-generated method stub
		/*
		garandShotSound = Gdx.audio.newSound(Gdx.files.internal("data/garand-shot.mp3"));
		zombieMoanSound = Gdx.audio.newSound(Gdx.files.internal("data/zombie-moan.mp3"));
		zombieHurtSound = Gdx.audio.newSound(Gdx.files.internal("data/zombie-hurt.mp3"));
		zombieBiteSound = Gdx.audio.newSound(Gdx.files.internal("data/zombie-bite.mp3"));*/
		switch(p_entity.popAudioState()){
		case garand_shot:
			garandShotSound.play(0.1f);
			break;
		case zombie_bite:
			zombieBiteSound.play(0.3f);
			break;
		case zombie_hurt:
			zombieHurtSound.play(0.1f);
			break;
		case zombie_moan:
			zombieMoanSound.play(0.1f);
			break;
		case man_hurt:
			maleHurt.play(0.1f);
			break;
		}
	}

	void clearLights() {
		if (lights.size() > 0) {
			for (Light light : lights) {
				light.remove();
			}
			lights.clear();
		}
	}

	void initLights() {
		Random rnum = new Random();
		for (int i = 0; i < m_WorldWrapper.getEntities().GetEntityList().size(); i++) {
			if (m_WorldWrapper.getEntities().GetEntityList().get(i).GetID() == EntityFactoryID.UserSoldier
					|| m_WorldWrapper.getEntities().GetEntityList().get(i).GetID() == EntityFactoryID.Soldier){

				ConeLight light = new ConeLight(rayHandler, GameWrapperSettings.RAYS_PER_ENTITY, null,
						GameWrapperSettings.LIGHT_DISTANCE +(rnum.nextFloat()*6), 0, 0, 0f, 32+(rnum.nextFloat()*6));
				light.attachToBody(m_WorldWrapper.getEntities().GetEntityList().get(i).getBody(),
						GameWrapperSettings.RADIUS / 1.5f, GameWrapperSettings.RADIUS / 1.5f, 180);
			light.setColor(255, 255, 255, 4f+(rnum.nextFloat()*2));

			lights.add(light);
			}else

				if (m_WorldWrapper.getEntities().GetEntityList().get(i).GetID() == EntityFactoryID.Bullet){
					PointLight light = new PointLight(rayHandler,5);
					light.setDistance(2.5f);
					light.attachToBody(m_WorldWrapper.getEntities().GetEntityList().get(i).getBody());
				light.setColor(255, 255, 255, 5f);

				lights.add(light);
			}

			//lights.add(new PointLight(rayHandler, GameWrapperSettings.RAYS_PER_ENTITY/3, new Color(1,1,1,0.5f), 6, 0, 15));
		}
	}

	private boolean fixedStep(float delta) {
		physicsTimeLeft += delta;
		if (physicsTimeLeft > GameWrapperSettings.MAX_TIME_PER_FRAME)
			physicsTimeLeft = GameWrapperSettings.MAX_TIME_PER_FRAME;

		boolean stepped = false;
		while (physicsTimeLeft >= GameWrapperSettings.TIME_STEP ) {
			EntityDataPacketList tempList =m_WorldWrapper.WorldStep(delta, physicsTimeLeft);
			if(m_GameState==GameState.Playing) {
				packetHandler.handleQueue(false);
				if (tempList.getPacketQueue().size > 0) {

					packetEnqueueCounter = 0;
					for (EntityDataPacket i : m_WorldWrapper.getEntities().getLocalEntities(false)) {
						tempList.getPacketQueue().addLast(i);
					}
					if (packetSendQueue.size < packetSendQueue2.size) {
						packetSendQueue.addLast(tempList);
					} else {
						packetSendQueue2.addLast(tempList);
					}
				}
			}
			physicsTimeLeft -= GameWrapperSettings.TIME_STEP;
			stepped = true;
		}
		return stepped;
	}

	private void WriteNetworkQueue(ThreadSafeSocket socket, ThreadSafeQueue queue) {

		Timer.instance().schedule(new WriteNetworkQueueTask(queue,socket) , 0.01f , 0.01f);

	}

	public void resize(int width, int height){

		//stageClient.getViewport().update(width, height, true);
	}

	public void dispose() { // stops the playback
		m_ThreadSocket.close();
		m_ThreadSocket2.close();
		music.dispose();
		rayHandler.dispose();
		m_WorldWrapper.getWorld().dispose();
		m_Client.dispose();
		stageClient.dispose();
		stageMenu.dispose();

	}

}
