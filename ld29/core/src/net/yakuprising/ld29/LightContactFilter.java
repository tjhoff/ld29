package net.yakuprising.ld29;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class LightContactFilter implements ContactFilter {

	public boolean filterEnemies = false;
	
	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		CollisionData a = (CollisionData)fixtureA.getBody().getUserData();
		CollisionData b = (CollisionData)fixtureB.getBody().getUserData();
		if (a.GetType() == ObjectType.AmmoPickup || a.GetType() == ObjectType.BatteryPickup)
		{
			return false;
		}
		if (b.GetType() == ObjectType.AmmoPickup || b.GetType() == ObjectType.BatteryPickup)
		{
			return false;
		}
		
		if (filterEnemies)
		{
			if (a.GetType() == ObjectType.Enemy || b.GetType() == ObjectType.Enemy)
			{
				return false;
			}
		}
		
		return true;
	}

}
