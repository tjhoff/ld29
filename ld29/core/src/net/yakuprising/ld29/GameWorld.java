package net.yakuprising.ld29;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.yakuprising.ld29.Map.MapEdge;
import net.yakuprising.ld29.Map.MapRoom;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld  implements PauseHandler
{
	private World world;
	private RayHandler rayHandler;
	
	private SpriteBatch batch;
	
	private ShapeRenderer renderer = new ShapeRenderer();
	
	private OrthographicCamera camera;
	
	private OrthographicCamera mapCamera;
	
	private Texture bg;
	
	ParticleManager particleManager;
	
	List<ParticleManager> particles = new ArrayList<ParticleManager>();
	
	private Light l;
	
	private List<Entity> entities = new ArrayList<Entity>();
	
	Player player;
	
	Light light;
	
	float w;
	float h;
	
	float n = 5;
	
	float r = 0;
	
	Map m;
	
	GameInputProcessor processor;
	
	public GameWorld()
	{
		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		world = new World(new Vector2(0,0), true);
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(world);
		//rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.1f);
		rayHandler.setCulling(true);
		rayHandler.setBlurNum(2);
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera(100 * w/h, 100);
		mapCamera = new OrthographicCamera(1500 * w/h, 1500);
		camera.update();
		
		world.setContactListener(new GameContactListener());
		
		particleManager = new ParticleManager(ParticleManager.CreateMesh(new Vector2(1,1), 1),
				ResourceManager.GetTexture("particle.png"),
				ResourceManager.GetFlatColorShader());
		ParticleManager bloodParticleManager = new ParticleManager(ParticleManager.CreateMesh(new Vector2(1,1), 1),
				ResourceManager.GetTexture("blood.png"),
				ResourceManager.GetFlatColorShader());
		
		bloodParticleManager.SetTimeToLive(600);
		bloodParticleManager.SetSpread(.12f);
		
		ParticleManager crateParticleManager = new ParticleManager(ParticleManager.CreateMesh(new Vector2(1,1), 1),
				ResourceManager.GetTexture("woodfragment.png"),
				ResourceManager.GetFlatColorShader());
		
		crateParticleManager.SetTimeToLive(600);
		crateParticleManager.SetSpread(.7f);
		
		particles.add(bloodParticleManager);
		particles.add(crateParticleManager);
		
		m = new Map(world, 100, 500, 500, 50);
		
		player = new Player(rayHandler, world);
		player.body.setTransform(m.rooms[0].position, 0);
		AIHandler handler = new AIHandler(world, player);
		for (int roomIndex = 1; roomIndex < m.rooms.length; roomIndex++)
		{
			MapRoom room = m.rooms[roomIndex];

			if (Math.random() < .85f)
			{
				for (int i = 0; i < 2; i++)
				{
					Vector2 cratePosition = room.position.cpy().add(Utils.RandomCircle(3)).add(Utils.RandomSpread(5));
					AmmoPickup a = new AmmoPickup(world);
					a.body.setTransform(cratePosition, (float)Math.random() * 90f);
					entities.add(a);
				}
			}
			
			if (Math.random() < .7f)
			{
				for (int i = 0; i < 2; i++)
				{
					Vector2 cratePosition = room.position.cpy().add(Utils.RandomCircle(3)).add(Utils.RandomSpread(5));
					BatteryPickup a = new BatteryPickup(world);
					a.body.setTransform(cratePosition, (float)Math.random() * 90f);
					entities.add(a);
				}
			}
			
			if (Math.random() < .4f)
			{
				for (int i = 0; i < 1; i++)
				{
					Vector2 cratePosition = room.position.cpy().add(Utils.RandomCircle(3)).add(Utils.RandomSpread(5));
					HealthPickup a = new HealthPickup(world);
					a.body.setTransform(cratePosition, (float)Math.random() * 90f);
					entities.add(a);
				}
			}
			
			if (Math.random() < .6f)
			{
				for (int i = 0; i < 5; i++)
				{
					Vector2 cratePosition = room.position.cpy().add(Utils.RandomCircle(3)).add(Utils.RandomSpread(5));
					Crate c = new Crate(world, crateParticleManager);
					c.body.setTransform(cratePosition, (float)Math.random() * 90f);
					entities.add(c);
				}
			}			
			
			if (Math.random() < .4f)
			{
				int monsterCount = 7 + (int)(Math.random()*7);
				for (int i = 0; i < monsterCount; i++)
				{
					Vector2 cratePosition = room.position.cpy().add(Utils.RandomCircle(10)).add(Utils.RandomSpread(5));
					Enemy e = new Enemy(world, handler, bloodParticleManager);
					e.body.setTransform(cratePosition, 0);
					entities.add(e);
				}
			}			
		}
		
		l = new PointLight(rayHandler, 128);
		l.setPosition(m.rooms[0].position);
		l.setDistance(500);
		l.setSoft(true);
		l.setStaticLight(true);
		l.setColor(new Color(.5f,.2f,.2f,.2f));
		
		l = new PointLight(rayHandler, 128);
		l.setPosition(m.rooms[m.rooms.length-1].position);
		l.setDistance(500);
		l.setSoft(true);
		l.setStaticLight(true);
		l.setColor(new Color(.5f,.9f,.2f,.2f));
		
		GunRayCastCallback callback = new GunRayCastCallback(particleManager, bloodParticleManager);
		
		processor = new GameInputProcessor(player, callback);
		bg = new Texture(Gdx.files.internal("bg.png"));
	}
	
	public InputProcessor GetInputProcessor()
	{
		return processor;
	}
	
	public void draw()
	{
		batch.setProjectionMatrix(camera.projection);
		batch.disableBlending();
		batch.begin();

		batch.draw(bg ,-50 * w/h, -50, 100 * w/h, 100);
		batch.end();
				
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.BLUE);
		for (MapRoom room : m.rooms)
		{
			renderer.circle(room.position.x, room.position.y, 5);
		}
		for (MapEdge edge : m.edges)
		{
			renderer.line(edge.first.position, edge.second.position);
		}
		renderer.setColor(Color.RED);
		m.RenderLines(renderer);
		renderer.end();
		
		m.Render(camera.combined);
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for (ParticleManager particle : particles)
		{
			particle.Draw(camera.combined);
		}
		player.Draw(camera.combined);
		for (Entity e : entities)
		{
			e.Draw(camera.combined);
		}
		
		rayHandler.render();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		particleManager.Draw(camera.combined);
		
		if (player.IsShooting())
		{
			float radians = player.GetAngle() * (float)Math.PI / 180;
			Vector2 dir = new Vector2((float)Math.cos(radians), (float)Math.sin(radians));
			Vector2 laserEnd = player.GetPosition().cpy().add(dir.cpy().scl(100));
			LaserCallback laserCallback = new LaserCallback(laserEnd);
			world.rayCast(laserCallback, player.GetPosition().cpy(), laserEnd);

			renderer.setProjectionMatrix(camera.combined);
			renderer.begin(ShapeType.Line);
			renderer.setColor(Color.RED);
			
			renderer.line(player.GetPosition().cpy().add(dir.cpy().scl(5)), laserCallback.closest);
			renderer.end();
		}
		
		
		if (Gdx.input.isKeyPressed(Keys.TAB))
		{
			DrawMap();
		}		
	}
	
	public void DrawMap()
	{
		renderer.setProjectionMatrix(mapCamera.combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(new Color(0,1,1,1));
		for (MapRoom room : m.rooms)
		{
			renderer.circle(room.position.x, room.position.y, 5);
		}
		for (MapEdge edge : m.edges)
		{
			renderer.line(edge.first.position, edge.second.position);
		}
		renderer.setColor(new Color(1,0,0,1));
		m.RenderLines(renderer);
		renderer.end();
		renderer.begin(ShapeType.Filled);
		renderer.setColor(new Color(0,1,0,1));
		renderer.circle(player.GetPosition().x, player.GetPosition().y, 6);
		renderer.circle(m.rooms[m.rooms.length-1].position.x, m.rooms[m.rooms.length-1].position.y, 10);
		renderer.end();		
		
	}
	
	public Vector2 ScreenToGame(float x, float y)
	{
		float gameX = (x - w/2) / (w) * 100 * w/h;
		float gameY = -(y - h/2) / (h) * 100;
		gameX += camera.position.x;
		gameY += camera.position.y;
		return new Vector2(gameX, gameY);
	}
	
	public boolean update()
	{
		float h = 0;
		float v = 0;
		boolean moving = false;
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			moving = true;
			h = -5;
		}
		if (Gdx.input.isKeyPressed(Keys.D))
		{
			moving = true;
			h = 5;
		}
		if (Gdx.input.isKeyPressed(Keys.W))
		{
			moving = true;
			v = 5;
		}
		if (Gdx.input.isKeyPressed(Keys.S))
		{
			moving = true;
			v = -5;
		}
		
		if (moving)
		{
			player.Move(h, v);
		}
		
		
		float x = Gdx.input.getX();
		float y = Gdx.input.getY();
		Vector2 gameCursor = ScreenToGame(x,y);
		
		gameCursor.sub(player.GetPosition());
		
		player.SetAngle((float)(Math.atan2(gameCursor.y, gameCursor.x) * 180.0f / Math.PI));
		
		world.step(1/60.0f, 8, 2);
		
		player.Update();
		
		for (Iterator<Entity> it = entities.iterator(); it.hasNext();)
		{
			Entity e = it.next();
			e.Update();
			if (e.IsDestroyed())
			{
				e.Dispose(world);
				it.remove();
				continue;
			}
		}
		Vector2 pPos = player.GetPosition();
		camera.position.set(pPos.x, pPos.y, 0);
		camera.update();
		//mapCamera.position.set(pPos.x, pPos.y, 0);
		//mapCamera.update();
		
		particleManager.Update(1);
		for (ParticleManager particle : particles)
		{
			particle.Update(1);
		}
		
		rayHandler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth * camera.zoom, camera.viewportHeight * camera.zoom);
		rayHandler.update();
		if (player.GetHealth() <= 0)
		{
			return false;
		}
		if (player.GetPosition().cpy().sub(m.rooms[m.rooms.length - 1].position).len() < 20)
		{
			return false;
		}
		return true;
	}

	@Override
	public void Pause() {
		processor.Pause();
	}

	@Override
	public void UnPause() {
		processor.UnPause();
	}
}
