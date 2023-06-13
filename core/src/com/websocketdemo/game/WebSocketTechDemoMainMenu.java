package com.websocketdemo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class WebSocketTechDemoMainMenu  implements Screen {
      
	OrthographicCamera camera;
	WebSocketTechDemoApplication game;
	String m_Text;
	TextureRegion Logo;
	Stage stageMenu;
	Button buttonBack;
	TextureRegion buttonBackTex;
	boolean m_isMobile;
	Image LogoImage;
	Label m_Url;
	BitmapFont menuFont;
	boolean returnToMenu=false;
	float scaleFactor=1;

	public WebSocketTechDemoMainMenu(final WebSocketTechDemoApplication game, String text,boolean isMobile) {

		scaleFactor = Gdx.graphics.getBackBufferHeight()/360f;
		buttonBackTex = new TextureRegion(new Texture(Gdx.files.internal("data/back.png")));
		TextureRegionDrawable backButton= new TextureRegionDrawable(buttonBackTex);
		buttonBack= new Button(backButton);
		menuFont = new BitmapFont();
		menuFont.getData().setScale(1.2f*scaleFactor);
		menuFont.setUseIntegerPositions(false);
		m_Url = new Label(text, new Label.LabelStyle(menuFont,new Color(0.68f,0,0,1f)));
		m_Url.setWrap(true);
		m_Url.setAlignment(Align.center);
		this.game = game;
		m_Text=text;
		stageMenu = new Stage();
		game.font = new BitmapFont();
		game.font.setColor(new Color(0.7f,0.0f,0f,1f));
		game.font.getData().setScale(2f);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 720);

		Logo = new TextureRegion(new Texture(Gdx.files.internal("data/logo.png")));
		LogoImage = new Image(Logo);
		stageMenu.addActor(LogoImage);
		stageMenu.addActor(buttonBack);
		stageMenu.addActor(m_Url);
		buttonBack.setBounds(((Gdx.graphics.getWidth()/2)-96*scaleFactor),(Gdx.graphics.getHeight()-260*scaleFactor), 192*scaleFactor, 48*scaleFactor);

		m_Url.setBounds(((Gdx.graphics.getWidth()/2)-85*scaleFactor),(Gdx.graphics.getHeight()-180*scaleFactor), 170*scaleFactor, 48*scaleFactor);

		LogoImage.setBounds(((Gdx.graphics.getWidth()/2)-120*scaleFactor),(Gdx.graphics.getHeight()-120*scaleFactor), 240*scaleFactor, 75*scaleFactor);
		buttonBack.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				returnToMenu=true;
				return false;
			}
		});
		stageMenu.addListener(buttonBack.getClickListener());
		Gdx.input.setInputProcessor(stageMenu);
		buttonBack.setVisible(true);
		LogoImage.setVisible(true);
		m_Url.setVisible(true);
		m_isMobile=isMobile;

	}
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(returnToMenu) {
			game.setScreen(new WebSocketTechDemo(game, m_isMobile));
		}
		stageMenu.act(Gdx.graphics.getDeltaTime());
		stageMenu.draw();

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

        //Rest of class still omitted...

}

