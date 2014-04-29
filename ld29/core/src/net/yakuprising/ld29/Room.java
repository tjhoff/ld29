package net.yakuprising.ld29;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;

public class Room implements CollisionData
{
	private List<Vector2[]> openings;
	private Body body;
	private List<List<Vector2>> edges = new ArrayList<List<Vector2>>();
	private List<GLSprite> sprites;
	public Room()
	{
		
	}
	
	public void CreateRoom(World world, Vector2 roomPosition, List<Vector2[]> openings)
	{
		BodyDef roomDef = new BodyDef();
		roomDef.type = BodyType.StaticBody;
		
		body = world.createBody(roomDef);
		body.setUserData(this);
		
		List<List<Vector2>> chains = new ArrayList<List<Vector2>>();
		
		List<Vector2[]> sortedOpenings = SortOpenings(openings, roomPosition);
		
		for (int i = 0; i < sortedOpenings.size(); i++)
		{
			List<Vector2> chain = new ArrayList<Vector2>();
			int nextIndex = i+1;
			if (nextIndex > sortedOpenings.size()-1)
			{
				nextIndex = 0;
			}
			Vector2 vx1 = sortedOpenings.get(i)[1];
			Vector2 vx2 = sortedOpenings.get(nextIndex)[0];
			chain.add(vx1);
			float dot = vx1.cpy().sub(roomPosition).nor().dot(vx2.cpy().sub(roomPosition).nor());
			
			// If we're a valid edge for expansion
			if (dot < .5 && vx1.cpy().sub(vx2).len() > 1.0);
			{
				float a = (vx1.cpy().sub(roomPosition)).angle();
				float b = (vx2.cpy().sub(roomPosition)).angle();
				if (b < a)
				{
					b += 360;
				}
				float angle = b-a;
				int numSegments = 1;
				for (int j = 0; j < numSegments; j++)
				{
					// We need more points to make the room better
					Vector2 midpoint = vx1.cpy().add(vx2).scl((j+1) / ((float)numSegments+1));
					Vector2 dir = midpoint.cpy().sub(roomPosition).nor();
					dir.scl(angle < 180 ? -20 : 20);
					Vector2 newPoint = roomPosition.cpy().add(dir);
					chain.add(newPoint);
				}				
				
			}
			
			chain.add(vx2);
			
			chains.add(chain);
		}
		
		for (List<Vector2> chain : chains)
		{
			ChainShape shape = new ChainShape();
			Vector2 [] chainList = new Vector2[chain.size()];
			shape.createChain(chain.toArray(chainList));
			
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = .1f;
			Filter filter = fixtureDef.filter;
			filter.categoryBits = GetType().bitmask;
			filter.maskBits = (short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask);
			
			body.createFixture(fixtureDef);
			shape.dispose();
			
			edges.add(chain);
		}
		
		BuildSprites();
	}
	
	public void CreateCorridor(World world, Vector2[] opening1, Vector2[] opening2)
	{
		BodyDef roomDef = new BodyDef();
		roomDef.type = BodyType.StaticBody;
		
		body = world.createBody(roomDef);
		body.setUserData(this);
		
		EdgeShape edge1 = new EdgeShape();
		edge1.set(opening1[0], opening2[1]);
		
		List<Vector2> edge1List = new ArrayList<Vector2>();
		edge1List.add(opening1[0]);
		edge1List.add(opening2[1]);
		
		List<Vector2> edge2List = new ArrayList<Vector2>();
		edge2List.add(opening1[1]);
		edge2List.add(opening2[0]);
		
		edges.add(edge1List);
		edges.add(edge2List);
		
		EdgeShape edge2 = new EdgeShape();
		edge2.set(opening1[1], opening2[0]);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = edge1;
		fixtureDef.density = .1f;
		Filter filter = fixtureDef.filter;
		filter.categoryBits = GetType().bitmask;
		filter.maskBits = (short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask);
		
		body.createFixture(fixtureDef);
		
		fixtureDef.shape = edge2;
		fixtureDef.density = .1f;
		filter = fixtureDef.filter;
		filter.categoryBits = GetType().bitmask;
		filter.maskBits = (short)(ObjectType.Crate.bitmask | ObjectType.Enemy.bitmask | ObjectType.Player.bitmask | ObjectType.Static.bitmask);
		
		body.createFixture(fixtureDef);
		
		BuildSprites();
	}
	
	public Vector2[] GetOpening(int opening)
	{
		return openings.get(opening);
	}
	
	public List<Vector2[]> SortOpenings(List<Vector2[]> openings, Vector2 roomPosition)
	{
		List<Vector2[]> sorted = new ArrayList<Vector2[]>();
		while (openings.size() > 0)
		{
			float leastAngle = Float.MAX_VALUE;
			Vector2[] nextOpening = null;
			int nextIndex = 0;
			for (int i = 0; i < openings.size(); i++)
			{
				Vector2[] opening = openings.get(i);
				Vector2 diff = opening[0].cpy().sub(roomPosition).scl(-1);
				float angle = 360- diff.angle();
				if (angle < leastAngle)
				{
					nextIndex = i;
					nextOpening = opening;
					leastAngle = angle;
				}
			}
			if (nextOpening != null)
			{
				openings.remove(nextIndex);
				sorted.add(nextOpening);
			}			
		}
		return sorted;
	}
	
	public void BuildSprites()
	{
		sprites = new ArrayList<GLSprite>();
		
		for (List<Vector2> edge : edges)
		{			
			for (int i = 0; i < edge.size()-1; i++)
			{
				Vector2 diff = edge.get(i).cpy().sub(edge.get(i+1));
				Vector2 mid = edge.get(i).cpy().add(edge.get(i+1)).scl(.5f);
				float len = diff.len();
				GLSprite sprite = new GLSprite(ResourceManager.GetTexture("wall.jpg"), GLSprite.CreateMesh(new Vector2(len/2, 2), len/4,1), ResourceManager.GetFlatColorShader(), 1, 1, Color.WHITE);
				sprite.SetAngle(diff.angle());
				sprite.SetPosition(mid);
				sprites.add(sprite);
				
			}
		}
			
	}
	
	public void RenderLines(ShapeRenderer renderer)
	{
		for (List<Vector2> edge : edges)
		{			
			for (int i = 0; i < edge.size()-1; i++)
			{
				renderer.line(edge.get(i), edge.get(i+1));
			}
		}
	}

	public void Render(Matrix4 matrix) 
	{
		for (GLSprite sprite : sprites)
		{
			sprite.Draw(matrix);
		}
	}

	@Override
	public ObjectType GetType() {
		return ObjectType.Static;
	}

	@Override
	public void HandleBeginContact(CollisionData other) {
		// TODO Auto-generated method stub
		
	}
}
