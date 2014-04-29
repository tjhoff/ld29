package net.yakuprising.ld29;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Enemy extends Entity {

	private int health = 10;
	
	private int stun = 0;
	
	private int attacking = 0;
	private int attackCooldown = 0;
	
	private GLSprite meleeSprite;
	World world;
	
	public enum State
	{
		Attacking,
		Idle,
		Moving
	};
	
	private State currentState = State.Idle;
	
	private Entity target;
	
	private ParticleManager bloodParticleManager;
	
	private AIHandler ai;
	private int flip = 0;
	
	private Texture[] moveTextures;
	private int moveIndex = 0;
	public Enemy(World world, AIHandler ai, ParticleManager bloodParticleManager)
	{
		this.world = world;
		this.bloodParticleManager = bloodParticleManager;
		sprite = new GLSprite(ResourceManager.GetTexture("person_still.png"), GLSprite.CreateMesh(new Vector2(2,2), 1, 1), ResourceManager.GetFlatColorShader(), 1, 1, new Color(.7f,.5f,.8f,1));
		meleeSprite = new GLSprite(ResourceManager.GetTexture("melee.png"), GLSprite.CreateMesh(new Vector2(4,4), 1, 1), ResourceManager.GetFlatColorShader(), 1, 1, Color.WHITE);
		moveTextures = new Texture[] { 
				ResourceManager.GetTexture("person_walk_1.png"),
				ResourceManager.GetTexture("person_walk_3.png"), 
				ResourceManager.GetTexture("person_walk_1.png"),
				ResourceManager.GetTexture("person_walk_2.png"),
				ResourceManager.GetTexture("person_walk_4.png"),
				ResourceManager.GetTexture("person_walk_2.png")};
		CircleShape playerShape = new CircleShape();
		playerShape.setRadius(1f);
		
		BodyDef playerDef = new BodyDef();
		playerDef.type = BodyType.DynamicBody;
		playerDef.linearDamping = .1f;
		playerDef.fixedRotation = true;
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerShape;
		fixtureDef.density = .1f;
		Filter filter = fixtureDef.filter;
		filter.categoryBits = GetType().bitmask;
		filter.maskBits = (short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask);
		
		body = world.createBody(playerDef);
		body.createFixture(fixtureDef);
		body.setUserData(this);
		playerShape.dispose();
		
		this.ai = ai;
	}
	
	public void SetState(State state)
	{
		currentState = state;
	}
	
	public State GetState()
	{
		return currentState;
	}
	
	public void SetTarget(Entity target)
	{
		this.target = target;
	}

	@Override
	public void Update()
	{
		if (stun > 0)
		{
			stun--;
		}
		else 
		{		
			ai.DoAI(this);
			
			if (currentState == State.Idle)
			{
				Idle();
			}
			else if (currentState == State.Attacking)
			{
				Attack();
			}
			
			if (moving)
			{
				if (flip > 0)
				{
					flip --;
				}
				if (flip == 0)
				{
					moveIndex = (moveIndex + 1)% moveTextures.length;
					sprite.SetTexture(moveTextures[moveIndex]);
					flip = 8;
				}
			}		
			if (attackCooldown > 0)
			{
				attackCooldown --;
			}
		}
		super.Update();
	}
	
	@Override
	public void Draw(Matrix4 matrix)
	{
		if (attacking > 0)
		{
			attacking --;
			meleeSprite.SetPosition(position);
			meleeSprite.SetAngle(angle);
			meleeSprite.Draw(matrix);
		}
		super.Draw(matrix);
	}
	
	public void Idle()
	{
		
	}
	
	public void Attack()
	{
		Vector2 diff = target.GetPosition().cpy().sub(position);
		Vector2 dir = diff.cpy().nor().scl(2);
		SetAngle((float)Math.atan2(dir.y, dir.x) * 180 / (float)Math.PI);
		
		if (diff.len() < 5)
		{
			Melee();
		}
		else 
		{
			Move(dir.x, dir.y);
		}
	}
	
	public void Melee()
	{
		if (attackCooldown > 0)
		{
			return;
		}
		ResourceManager.GetSound("attack.wav").play(.3f);
		MeleeCallback callback = new MeleeCallback(position,angle,5,45);
		callback.player = false;
		float range = 20;
		world.QueryAABB(callback, -range + position.x, -range + position.y, range + position.x, range+position.y);
		callback.DoMelee();
		attacking = 3;
		attackCooldown = 10;
	}
	
	@Override
	public ObjectType GetType() {
		// TODO Auto-generated method stub
		return ObjectType.Enemy;
	}
	
	public void Damage(int damage)
	{
		health -= damage;
		bloodParticleManager.CreateParticles(10, position, new Vector2());
		if (health <= 0)
		{
			ResourceManager.GetSound("kill.wav").play();
			Destroy();
		}
		else 
		{
			ResourceManager.GetSound("hurt.wav").play(.5f);
		}
		stun = 30;
	}

	@Override
	public void HandleBeginContact(CollisionData other) {
		// TODO Auto-generated method stub
		
	}
}
