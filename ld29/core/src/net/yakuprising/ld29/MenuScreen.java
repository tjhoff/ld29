package net.yakuprising.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Cell;

public class MenuScreen extends BaseScreen {
	private Skin skin;
	private Window infoWindow;
	private TextButton infoButton;
	public MenuScreen(final LudumGame game)
	{
		stage = new Stage();
		processor = stage;
		skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 2, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		
		skin.add("white", new Texture(pixmap));
		
		NinePatch patch = new NinePatch(new Texture(Gdx.files.internal("window_ninepatch.png")), 10,10,25,10);
		
		skin.add("windowDrawable", patch);
		
		skin.add("cancelButton", new Texture(Gdx.files.internal("cancel.png")));
		
		skin.add("logo", new Texture(Gdx.files.internal("under_logo.jpg")));
		
		BitmapFont f = new BitmapFont();
		f.setColor(Color.BLACK);
		skin.add("default", f);
	
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
		
		Table table = new Table();
		
		table.setFillParent(true);
		stage.addActor(table);
		
		Image logo = new Image(skin.getDrawable("logo"));
		
		table.add(logo);

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		table.row();
		Label infoLabel = new Label("WASD: Move, Aim: R-Click, Attack / Shoot : L-Click / L-Click while aiming, F: Toggle flashlight, Tab: View Map. GET TO THE GREEN ROOM", labelStyle);
		final TextButton startButton = new TextButton("START", skin);
		Cell<?> c = table.add(startButton);
		c.pad(10);
		infoButton = new TextButton("INFO", skin);
		c = table.add(infoButton);
		c.pad(10);
		final TextButton exitButton = new TextButton("EXIT", skin);
		c = table.add(exitButton);
		c.pad(10);
		table.row();
		table.add(infoLabel);
		
		
		WindowStyle infoWindowStyle = new WindowStyle();
		infoWindowStyle.titleFont = skin.getFont("default");
		infoWindowStyle.background = skin.getDrawable("windowDrawable");
		skin.add("windowStyle", infoWindowStyle);
		
		infoButton.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					if (infoButton.isChecked())
					{
						InfoOpen();
					}
					else
					{
						InfoClosed();
					}
				}
			
			}
		);
		
		exitButton.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					Gdx.app.exit();
				}
			}
		);
		
		startButton.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor) 
			{
				game.SetToGame();
			}
		}
	);
		
		infoWindow = new Window("GAME INFORMATION", skin, "windowStyle");
		
		
		final ImageButton closeButton = new ImageButton(skin.getDrawable("cancelButton"));
		
		closeButton.addListener(new ChangeListener()
			{
				@Override
				public void changed(ChangeEvent event, Actor actor) 
				{
					InfoClosed();				
				}
			}
		);
		
		final Label textLabel = new Label("Ludum Dare 29 - \nUNDER\n Created by TJ Hoff in 48 Hours", labelStyle);
		textLabel.setWrap(true);
		
		infoWindow.pad(10);
		infoWindow.padTop(25);
		infoWindow.getButtonTable().pad(5);
		infoWindow.getButtonTable().add(closeButton);
		infoWindow.getButtonTable().setHeight(infoWindow.getPadTop());
		infoWindow.getButtonTable().setVisible(true);
		infoWindow.defaults().spaceBottom(10);
		infoWindow.row().fill().expandX();
		c = infoWindow.add(textLabel);
		c.align(BaseTableLayout.CENTER);
		
		infoWindow.setMovable(false);
		infoWindow.setHeight(200);
		infoWindow.setWidth(300);
		
		infoWindow.setPosition(stage.getWidth()/2 - infoWindow.getWidth()/2, stage.getHeight()/2 - infoWindow.getHeight()/2);
		
		infoWindow.addListener(new InputListener()
			{
				@Override
				public boolean keyDown (InputEvent event, int keycode) 
				{					
					System.out.println(keycode);
					if (keycode == Keys.ESCAPE)
					{
						InfoClosed();
						return true;
					}
					return false;
				}
				
				@Override
				public boolean keyUp(InputEvent event, int keycode)
				{
					System.out.println(keycode);
					return false;
				}
			}
		);
		
		InfoClosed();
		stage.addActor(infoWindow);
	}
	
	public void InfoOpen()
	{
		infoWindow.setVisible(true);
	}
	
	public void InfoClosed()
	{
		infoWindow.setVisible(false);
		infoButton.setChecked(false);
	}

	@Override
	public void Draw(float delta) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void Update(float delta) {
		// TODO Auto-generated method stub
		
	}

}
