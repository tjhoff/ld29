package net.yakuprising.ld29;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class HealthPickup extends Entity 
{
private int amount;
	
	public HealthPickup(World world)
	{
		sprite = new GLSprite(ResourceManager.GetTexture("healthpickup.png"), GLSprite.CreateMesh(new Vector2(2,2), 1, 1), ResourceManager.GetFlatColorShader(), 1, 1, Color.WHITE);
		CircleShape shape = new CircleShape();
		shape.setRadius(2f);
		
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.linearDamping = .1f;
		def.fixedRotation = true;
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = .1f;
		fixtureDef.isSensor = true;
		Filter filter = fixtureDef.filter;
		filter.categoryBits = GetType().bitmask;
		filter.maskBits = (short)(ObjectType.Player.bitmask);
		
		body = world.createBody(def);
		body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
		
		amount = 5 + (int)(Math.random() * 5);
	}
	
	@Override
	public ObjectType GetType() {
		return ObjectType.HealthPickup;
	}
	
	public int GetAmount()
	{
		return amount;
	}

	@Override
	public void HandleBeginContact(CollisionData other) {
		// TODO Auto-generated method stub
		
	}
}
