package net.yakuprising.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class BaseScreen implements Screen {
	protected Stage stage;
	
	protected InputProcessor processor;
	
	public abstract void Draw(float delta);
	public abstract void Update(float delta);
	
	@Override
	public void render(float delta) 
	{
		Update(delta);
		Draw(delta);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) 
	{
		Viewport v = new ScreenViewport();
		v.update(width, height, true);
		stage.setViewport(v);
	}

	@Override
	public void show() 
	{
		Gdx.input.setInputProcessor(processor);
	}

	@Override
	public void hide() 
	{
	}

	@Override
	public void pause() 
	{
	}

	@Override
	public void resume() 
	{
	}

	@Override
	public void dispose() 
	{
		stage.dispose();
	}

}
