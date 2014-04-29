package net.yakuprising.ld29;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Player extends Entity
{
	private Light light;
	private Light sightLight;
	private ConeLight muzzleFlash;
	private int muzzleFlashCount = 0;
	
	private GLSprite meleeSprite;
	
	private World world;
	
	private float nightVision;
	
	private int clip = 0;
	private int clipSize = 10;
	
	private int ammo = 50;
	
	public float battery = 100;
	
	private float batteryDrainAmount = .04f;
	
	private int flip = 0;
	
	private int attacking = 0;
	
	private int attackCooldown = 0;
	
	private int health = 100;
	
	private Texture[] moveTextures;
	private int moveIndex = 0;
	
	public boolean shooting;
	
	public Player(RayHandler rayHandler, World world)
	{
		this.world = world;
		
		Filter lightFilter = new Filter();
		lightFilter.categoryBits = ObjectType.Static.bitmask;
		lightFilter.maskBits = (short)((short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask));
		
		
		light = new ConeLight(rayHandler, 128, new Color(1,1,1,.1f), 200, 0, 0, 0,20);
		light.setDistance(200.0f);
		Light.setContactFilter(lightFilter);
		
		muzzleFlash = new ConeLight(rayHandler, 128, new Color(1,.7f,.4f,.2f), 200, 0, 0, 0,120);
		muzzleFlash.setDistance(100.0f);
		muzzleFlash.setActive(false);
		muzzleFlash.setStaticLight(true);
		sightLight = new PointLight(rayHandler, 128);
		sightLight.setSoft(true);
		sightLight.setDistance(30);
		sightLight.setColor(.15f,.1f,.1f,.01f);
		sprite = new GLSprite(ResourceManager.GetTexture("person_still.png"), GLSprite.CreateMesh(new Vector2(2,2), 1, 1), ResourceManager.GetFlatColorShader(), 1, 1, Color.WHITE);
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
		filter.maskBits = (short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask | ObjectType.AmmoPickup.bitmask | ObjectType.BatteryPickup.bitmask | ObjectType.HealthPickup.bitmask);
		
		body = world.createBody(playerDef);
		body.createFixture(fixtureDef);
		body.setUserData(this);
		playerShape.dispose();
		
		maxSpeed = 35.0f;
	}
	
	public void SetShooting(boolean shooting)
	{
		this.shooting = shooting;
		if (this.shooting == true)
		{
			maxSpeed = 10f;
		}
		else if (this.shooting == false)
		{
			maxSpeed = 35.0f;
		}
	}
	
	public boolean IsShooting()
	{
		return shooting;
	}
	
	@Override
	public void Update()
	{
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
		super.Update();
		if (battery < 0)
		{
			battery = 0;
			SetLightVisibility(false);
		}
		if (light.isActive())
		{
			if (battery > 10 && (battery-batteryDrainAmount) < 10)
			{
				ResourceManager.GetSound("lowbattery.wav").play();
			}
			battery -=  batteryDrainAmount;
			light.setDistance(50 + battery * 1.5f);
			light.setDirection(angle);
			light.setPosition(position);
		}
		else {
			if (nightVision < 1.0)
			{
				nightVision += .005;
			}
			else
			{
				nightVision = 1.0f;
			}
			sightLight.setDistance(nightVision * 20 + 30);
			sightLight.setColor(.15f,.1f,.1f,.01f + nightVision * .1f);
		}
		
		if (muzzleFlash.isActive())
		{
			muzzleFlashCount--;
			if (muzzleFlashCount == 0)
			{
				muzzleFlash.setActive(false);
			}
		}
		
		sightLight.setPosition(position);

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
	
	public void SetLightVisibility(boolean visible)
	{
		if (!visible || (visible && battery > 0))
		{
			light.setActive(visible);
		}
		
		if (visible && battery > 0)
		{
			nightVision = 0.0f;
			sightLight.setDistance(30);
			sightLight.setColor(.15f,.1f,.1f,.01f);
		}
	}
	
	public boolean GetLightVisibility()
	{
		return light.isActive();
	}
	
	public void Melee()
	{
		if (attackCooldown > 0)
		{
			return;
		}
		ResourceManager.GetSound("attack.wav").play(.3f);
		MeleeCallback callback = new MeleeCallback(position,angle,5,45);
		float range = 20;
		world.QueryAABB(callback, -range + position.x, -range + position.y, range + position.x, range+position.y);
		callback.DoMelee();
		attacking = 3;
		attackCooldown = 5;
	}
	
	public void Shoot(GunRayCastCallback callback)
	{
		if (clip > 0)
		{			
			float radians = angle * (float)Math.PI / 180.0f;
			Vector2 point2 = position.cpy().add(new Vector2((float)Math.cos(radians), (float)Math.sin(radians)).scl(100));
			world.rayCast(callback, position.cpy(), point2);
			ResourceManager.GetSound("shoot.wav").play(.6f, (float)Math.random() * .1f + .5f, .5f);
			callback.Handle();
			muzzleFlash.setActive(true);
			muzzleFlash.setPosition(position);
			muzzleFlash.setConeDegree(110 + (float)Math.random()*20);
			muzzleFlash.setDirection(angle);
			muzzleFlashCount = 5;
			
			clip --;
			
			nightVision = 0.0f;
			sightLight.setDistance(30);
			sightLight.setColor(.15f,.1f,.10f,.01f);
			
			SetAngle(angle + 5 * ((float)Math.random() - .5f));
		}
		else if (ammo > 0)
		{
			Reload();
		}
		else 
		{
			ResourceManager.GetSound("click.wav").play(.2f, (float)Math.random() * .1f + 1f, .5f);
		}
	}
	
	public void Reload()
	{
		int amount = Math.min(clipSize - clip, ammo);
		if (amount > 0)
		{
			ResourceManager.GetSound("reload.wav").play();
			ammo -= amount;
			clip += amount;
		}
	}
	
	public int GetBulletsInClip()
	{
		return clip;
	}
	
	public int GetAmmo()
	{
		return ammo;
	}
	
	public float GetBattery()
	{
		return battery;
	}
	
	public int GetHealth()
	{
		return health;
	}

	@Override
	public ObjectType GetType() {
		return ObjectType.Player;
	}

	@Override
	public void HandleBeginContact(CollisionData other) {
		if (other.GetType() == ObjectType.AmmoPickup)
		{
			ResourceManager.GetSound("pickup.wav").play();
			AmmoPickup pickup = (AmmoPickup)other;
			ammo += pickup.GetAmount();
			pickup.Destroy();
		}
		if (other.GetType() == ObjectType.BatteryPickup)
		{
			ResourceManager.GetSound("pickup.wav").play();
			BatteryPickup pickup = (BatteryPickup)other;
			battery += pickup.GetAmount();
			if (battery > 100)
			{
				battery = 100;
			}
			pickup.Destroy();
		}
		if (other.GetType() == ObjectType.HealthPickup)
		{
			ResourceManager.GetSound("pickup.wav").play();
			HealthPickup pickup = (HealthPickup)other;
			health += pickup.GetAmount();
			if (health > 100)
			{
				health = 100;
			}
			pickup.Destroy();
		}
	}

	public void Damage(int i) {
		// TODO Auto-generated method stub
		health -= i;
	}
}
