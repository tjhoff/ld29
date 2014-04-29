package net.yakuprising.ld29;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Entity implements CollisionData 
{
	protected Body body;
	protected GLSprite sprite;
	Vector2 position = new Vector2();
	protected boolean moving = false;
	float angle = 0.0f;
	
	private boolean destroyed = false;
	
	
	protected float maxSpeed = 15.0f;
	
	public void Draw(Matrix4 matrix)
	{
		sprite.Draw(matrix);;
	}
	
	public void Update()
	{
		if (!moving)
		{
			body.setLinearDamping(3.0f);
		}
		else 
		{
			Vector2 currentVel = body.getLinearVelocity();
			
			if (currentVel.len() < maxSpeed)
			{
				body.setLinearVelocity(currentVel.nor().scl(maxSpeed));	
			}
		}
		moving = false;
		
		angle = body.getAngle();
		sprite.SetAngle(angle);
		position = body.getPosition();
		sprite.SetPosition(position);
	}
	
	public Vector2 GetPosition()
	{
		return position;
	}
	
	public float GetAngle()
	{
		return angle;
	}
	
	public void Move(float x, float y)
	{
		moving = true;
		Vector2 currentVel = body.getLinearVelocity();
		Vector2 newVel = new Vector2(x,y).nor().scl(Math.max(Math.abs(x),Math.abs(y)));
		float dot = currentVel.cpy().nor().dot(newVel.cpy().nor());
		float speed = currentVel.len();
		if (dot < 0)
		{
			body.setLinearVelocity(0,0);
		}
		if (speed > maxSpeed)
		{
			body.setLinearVelocity(currentVel.nor().scl(maxSpeed));
		}
		
		body.applyLinearImpulse(newVel, body.getWorldCenter(), true);			
		
				
	}
	
	public void SetAngle(float angle)
	{
		body.setTransform(body.getPosition(), angle);
	}
	
	public void Destroy()
	{
		destroyed = true;
	}
	
	public boolean IsDestroyed()
	{
		return destroyed;
	}

	public void Dispose(World world) {
		world.destroyBody(body);
	}
}
