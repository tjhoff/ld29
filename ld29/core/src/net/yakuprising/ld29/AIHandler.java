package net.yakuprising.ld29;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class AIHandler {
	private Player player;
	private LineOfSight lineOfSight;
	public AIHandler(World world, Player player)
	{
		this.player = player;
		lineOfSight = new LineOfSight(world);
	}
	
	public void DoAI(Enemy e)
	{
		if (e.GetState() == Enemy.State.Idle)
		{	
			Vector2 diff = e.GetPosition().cpy().sub(player.GetPosition());
			float len = diff.len();
			if (len > 100 || len == 0)
			{
				
			}
			else 
			{
				if (lineOfSight.CheckVisibilty(e.GetPosition(), player.body))
				{
					e.SetState(Enemy.State.Attacking);
					e.SetTarget(player);
					ResourceManager.GetSound("alert.wav").play(.2f);
				}
			}
		}
		
	}
}
