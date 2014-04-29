package net.yakuprising.ld29;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

public class GameInputProcessor implements InputProcessor, PauseHandler {

	private Player player;
	private GunRayCastCallback callback;
	private boolean paused;
	
	private boolean shooting = false;
	
	Vector2 aimLocation = new Vector2();
	float lastX = 0;
	float lastY = 0;
	
	GameInputProcessor(Player player, GunRayCastCallback callback)
	{
		this.player = player;
		this.callback = callback;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (paused)
		{
			return false;
		}
		if (keycode == Keys.R)
		{
			player.Reload();
		}
		if (keycode == Keys.F){
			player.SetLightVisibility(!player.GetLightVisibility());
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (paused)
		{
			return false;
		}
		if (button == Buttons.LEFT)
		{
			if (!shooting)
			{
				player.Melee();
			}
			else 
			{
				player.Shoot(callback);
			}
		}
		if (button == Buttons.RIGHT)
		{
			shooting = true;
			player.SetShooting(true);
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.RIGHT)
		{
			shooting = false;
			player.SetShooting(false);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Pause() {
		paused = true;
	}

	@Override
	public void UnPause() {
		paused = false;
	}

}
