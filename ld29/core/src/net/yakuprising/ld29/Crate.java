package net.yakuprising.ld29;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Crate extends Entity {

	ParticleManager particles;
	public Crate(World world, ParticleManager particleManager)
	{
		this.particles = particleManager;
		sprite = new GLSprite(ResourceManager.GetTexture("crate.png"), GLSprite.CreateMesh(new Vector2(1,1), 1, 1), ResourceManager.GetFlatColorShader(), 1, 1, Color.WHITE);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1, 1);
		
		BodyDef playerDef = new BodyDef();
		playerDef.type = BodyType.DynamicBody;
		playerDef.linearDamping = .1f;
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = .1f;
		Filter filter = fixtureDef.filter;
		filter.categoryBits = GetType().bitmask;
		filter.maskBits = (short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask);
		
		body = world.createBody(playerDef);
		body.createFixture(fixtureDef);
		body.setUserData(this);
		shape.dispose();
	}
	
	@Override
	public ObjectType GetType() {
		// TODO Auto-generated method stub
		return ObjectType.Crate;
	}

	@Override
	public void HandleBeginContact(CollisionData other) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void Destroy()
	{
		particles.CreateParticles(5, position, new Vector2());
		ResourceManager.GetSound("cratedestroy.wav").play(.4f);
		super.Destroy();
	}

}
