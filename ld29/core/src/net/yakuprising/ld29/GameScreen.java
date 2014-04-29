package net.yakuprising.ld29;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.esotericsoftware.tablelayout.Cell;

public class GameScreen extends BaseScreen implements PauseHandler 
{
	PauseMenu menu;
	ShapeRenderer shapeRenderer;
	GameWorld world;
	Label clipLabel;
	Label bulletsLabel;
	Label batteryLabel;
	Label healthLabel;
	boolean paused;
	LudumGame game;
	public GameScreen(final LudumGame game)
	{
		this.game = game;
		shapeRenderer = new ShapeRenderer();
		stage = new Stage();
		InputMultiplexer multiplexer = new InputMultiplexer();
		menu = new PauseMenu(game, stage, this);
		world = new GameWorld();
		
		Image battery = new Image(ResourceManager.GetTexture("battery.png"));
		Image bullet = new Image(ResourceManager.GetTexture("bullet.png"));
		Image bullets = new Image(ResourceManager.GetTexture("bullets.png"));
		Image health = new Image(ResourceManager.GetTexture("health.png"));
		BitmapFont f = new BitmapFont();
		f.setColor(Color.BLACK);
		f.scale(2);
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = f;
		clipLabel = new Label("", labelStyle);
		bulletsLabel = new Label("", labelStyle);
		batteryLabel = new Label("", labelStyle);
		healthLabel = new Label("", labelStyle);
		
		Table t = new Table();
		t.setFillParent(true);
		stage.addActor(t);
		
		t.center();
		t.bottom();
		Cell<?> c = t.add(battery);
		c.pad(5);
		t.add(batteryLabel);
		c = t.add(bullet);
		c.pad(5);
		t.add(clipLabel);
		c = t.add(bullets);
		c.pad(5);
		t.add(bulletsLabel);
		t.padBottom(50);
		c = t.add(health);
		c.pad(5);
		t.add(healthLabel);
		t.padBottom(50);
		
		multiplexer.addProcessor(world.GetInputProcessor());
		multiplexer.addProcessor(stage);
		
		processor = multiplexer;
	}
	
	@Override
	public void Draw(float delta)
	{
		
		world.draw();
	}
	
	@Override
	public void Update(float delta) 
	{
		if (paused)
		{
			return;
		}
		clipLabel.setText(String.format("x%d", world.player.GetBulletsInClip()));
		bulletsLabel.setText(String.format("x%d", world.player.GetAmmo()));
		batteryLabel.setText(String.format("%3.0f", world.player.GetBattery()));
		healthLabel.setText(String.format("%d", world.player.GetHealth()));
		if (!world.update())
		{
			game.SetToMenu();
		}
	}

	@Override
	public void Pause() {
		paused = true;
		world.Pause();
	}

	@Override
	public void UnPause() {
		paused = false;
		world.UnPause();
	}

}
