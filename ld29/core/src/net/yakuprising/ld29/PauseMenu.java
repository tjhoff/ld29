package net.yakuprising.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PauseMenu {
	private Skin skin;
	private Window pauseWindow;
	private Stage stage;
	public PauseMenu(final LudumGame game, Stage stage,final PauseHandler pauseHandler)
	{
		this.stage = stage;
		skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 2, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		
		skin.add("white", new Texture(pixmap));
		
		skin.add("cancelButton", new Texture(Gdx.files.internal("cancel.png")));
		
		BitmapFont f = new BitmapFont();
		f.setColor(Color.BLACK);
		skin.add("default", f);
	
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.down = skin.newDrawable("white", Color.GRAY);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
				
		final TextButton exitButton = new TextButton("EXIT", skin);
		final TextButton resumeButton = new TextButton("RESUME", skin);
		final TextButton menuButton = new TextButton("MAIN MENU", skin);
		
		
		WindowStyle pauseWindowStyle = new WindowStyle();
		pauseWindowStyle.titleFont = skin.getFont("default");
		pauseWindowStyle.background = skin.newDrawable("white", Color.CLEAR);
		pauseWindowStyle.stageBackground = skin.newDrawable("white", new Color(1,1,1,.2f));
		skin.add("windowStyle", pauseWindowStyle);
		
		exitButton.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					Gdx.app.exit();
				}
			}
		);	
		
		resumeButton.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					pauseHandler.UnPause();
					pauseWindow.setVisible(false);
				}
			}
		);	
		
		menuButton.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					game.SetToMenu();
				}
			}
		);	
		
		pauseWindow = new Window("PAUSED", skin, "windowStyle");
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		
		pauseWindow.pad(20);
		pauseWindow.defaults().spaceBottom(10);
		pauseWindow.row().fill().expandX();
		pauseWindow.add(resumeButton);
		pauseWindow.row().fill().expandX();
		pauseWindow.add(menuButton);
		pauseWindow.row().fill().expandX();
		pauseWindow.add(exitButton);
		pauseWindow.setMovable(false);
		pauseWindow.pack();
		
		pauseWindow.setPosition(stage.getWidth()/2 - pauseWindow.getWidth()/2, stage.getHeight()/2 - pauseWindow.getHeight()/2);
		
		stage.addCaptureListener(new InputListener()
			{
				@Override
				public boolean keyDown (InputEvent event, int keycode) 
				{					
					if (keycode == Keys.ESCAPE)
					{
						if (pauseWindow.isVisible())
						{
							pauseWindow.setVisible(false);
							pauseHandler.UnPause();
						}
						else 
						{
							pauseWindow.setVisible(true);
							pauseHandler.Pause();
						}
						return true;
					}
					return false;
				}
				
				@Override
				public boolean keyUp(InputEvent event, int keycode)
				{
					return false;
				}
			}
		);
		
		stage.addActor(pauseWindow);
		pauseWindow.setVisible(false);
	}
	
	public Stage GetStage()
	{
		return stage;
	}
}
