package net.yakuprising.ld29;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;

public class LudumGame extends Game {
	private MenuScreen menu;
	
	private GameScreen game;
	
	@Override
	public void create () 
	{
		System.out.println("Game created!");
		Pixmap pixmap = new Pixmap(Gdx.files.internal("cancel.png"));
		Gdx.input.setCursorImage(pixmap, 8, 8);
		SetToMenu();
	}
	
	public void SetToGame()
	{
		try
		{
			System.out.println("opening game");
			if (menu != null)
			{
				menu.dispose();
				menu = null;
			}
			
			if (game == null)
			{
				game = new GameScreen(this);
			}
			System.out.println("game created, setting to screen");
			setScreen(game);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void SetToMenu()
	{
		if (game != null)
		{
			game.dispose();
			game = null;
		}
		
		if (menu == null)
		{
			menu = new MenuScreen(this);
		}
		setScreen(menu);
	}

	@Override
	public void render () 
	{
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
		
	}
}
