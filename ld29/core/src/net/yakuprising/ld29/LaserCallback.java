package net.yakuprising.ld29;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class LaserCallback implements RayCastCallback {

	public Vector2 closest = new Vector2();
	private float lastFraction = Float.MAX_VALUE;
	
	public LaserCallback(Vector2 endVector)
	{
		closest.set(endVector);
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) 
	{
		if (fraction > 0 && fraction < lastFraction)
		{
			closest.set(point);
		}
		
		
		return fraction;
	}

}
