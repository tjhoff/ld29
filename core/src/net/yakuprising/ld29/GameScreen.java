package net.yakuprising.ld29;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen extends BaseScreen 
{
	PauseMenu menu;
	ShapeRenderer shapeRenderer;
	OrthographicCamera camera;
	Vector2 pos = new Vector2();
	RayHandler rayHandler;
	public GameScreen(final LudumGame game)
	{
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(100 * w/h, 100);
		shapeRenderer = new ShapeRenderer();
		stage = new Stage();
		menu = new PauseMenu(game, stage);
	}
	
	@Override
	public void Draw(float delta)
	{
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(1,0,0,1);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.circle(pos.x, pos.y, 5);
		shapeRenderer.end();		
	}
	
	@Override
	public void Update(float delta) 
	{
		if (menu.IsPaused())
		{
			return;
		}
		pos.x += .1;
	}

}
