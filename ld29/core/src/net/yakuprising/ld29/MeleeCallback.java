package net.yakuprising.ld29;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

public class MeleeCallback implements QueryCallback {

	Vector2 origin;

	boolean player = true;
	
	List<CollisionData> hit = new ArrayList<CollisionData>();
	
	float length;
	float cone;
	float angle;
	
	Vector2 myDir;
	
	MeleeCallback(Vector2 origin, float angle, float length, float cone)
	{
		this.origin = origin;
		this.length = length;
		this.cone = cone;
		this.angle = angle;
		myDir = new Vector2((float)(Math.cos(angle)), (float)(Math.sin(angle)));
	}
	
	@Override
	public boolean reportFixture(Fixture fixture) {
		// TODO Auto-generated method stub
		Vector2 otherPosition = fixture.getBody().getWorldCenter();
		Vector2 diff = otherPosition.cpy().sub(origin);
		
		if (diff.len() < length)
		{
			hit.add((CollisionData)fixture.getBody().getUserData());
		}
		
		return true;
	}

	public void DoMelee()
	{
		for (CollisionData data : hit)
		{
			if (player && data.GetType() == ObjectType.Enemy)
			{
				Enemy e = (Enemy)data;
				e.body.applyLinearImpulse(origin.cpy().sub(e.GetPosition()), e.body.getPosition(), true);
				e.Damage(4);
			}
			if (!player && data.GetType() == ObjectType.Player)
			{
				Player e = (Player)data;
				e.body.applyLinearImpulse(origin.cpy().sub(e.GetPosition()), e.body.getPosition(), true);
				e.Damage(3);
			}
			if (data.GetType() == ObjectType.Crate)
			{
				Crate c = (Crate)data;
				c.Destroy();
			}
		}
	}
}
