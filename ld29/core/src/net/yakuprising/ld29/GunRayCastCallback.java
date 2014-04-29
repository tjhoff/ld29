package net.yakuprising.ld29;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class GunRayCastCallback implements RayCastCallback 
{
	Fixture fixture = null;
	Vector2 point;
	Vector2 normal;
	
	ParticleManager sparks;
	ParticleManager blood;
	public GunRayCastCallback(ParticleManager sparks, ParticleManager blood)
	{
		this.sparks = sparks;
		this.blood = blood;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		// TODO Auto-generated method stub
		if (fraction < 0)
		{
			return fraction;
		}
		this.fixture = fixture;
		this.point = point.cpy();
		this.normal = normal.cpy();
		return fraction;
	}
	
	public void Handle()
	{
		if (fixture != null)
		{
			CollisionData data = (CollisionData)fixture.getBody().getUserData();
			if (data == null)
			{
				return;
			}
			if (data.GetType() == ObjectType.Enemy)
			{
				Enemy e = (Enemy)data;
				e.body.applyLinearImpulse(normal.cpy().scl(-10), point, true);
				e.Damage(5);
			}
			else if (data.GetType() == ObjectType.Crate)
			{
				Crate e = (Crate)data;
				e.body.applyLinearImpulse(normal.cpy().scl(-40), point, true);
				e.Destroy();
			}
			else 
			{
				sparks.CreateParticles(20, point, normal.cpy().scl(1));
			}
		}
	}
}
