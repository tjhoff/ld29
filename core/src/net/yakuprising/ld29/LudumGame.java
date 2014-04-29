package net.yakuprising.ld29;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class LudumGame extends Game {
	private MenuScreen menu;
	
	private GameScreen game;
	
	@Override
	public void create () 
	{
		SetToMenu();
	}
	
	public void SetToGame()
	{
		if (menu != null)
		{
			menu.dispose();
			menu = null;
		}
		
		if (game == null)
		{
			game = new GameScreen(this);
		}
		setScreen(game);
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
