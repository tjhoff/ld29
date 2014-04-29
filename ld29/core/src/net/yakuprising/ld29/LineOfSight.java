package net.yakuprising.ld29;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class LineOfSight implements RayCastCallback {

	private World world;
	
	private boolean hit;
	
	private Body target;
	
	public LineOfSight(World world)
	{
		this.world = world;
	}
	
	public boolean CheckVisibilty(Vector2 position, Body target)
	{
		hit = true;
		this.target = target;
		world.rayCast(this, position, target.getPosition());
		return hit;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		if (!fixture.getBody().equals(target) && fraction < 1.0f)
		{
			hit = false;
			return 0;
		}
		
		return fraction;
	}

}
