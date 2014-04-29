package net.yakuprising.ld29;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Map 
{
	static int cur_id = 0;
	public class MapRoom
	{
		
		int id;
		Vector2 position;
		List<MapEdge> edges = new ArrayList<MapEdge>();
		
		public MapRoom(Vector2 position, int id)
		{
			this.position = position;
			this.id = id;
		}
		
		public MapEdge Connect(MapRoom other)
		{
			MapEdge e = new MapEdge(this, other);
			this.edges.add(e);
			other.edges.add(e);
			return e;
		}
		
		public boolean IsConnected(MapRoom other)
		{
			for (MapEdge edge : edges)
			{
				if (edge.ContainsRoom(other))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	public class MapEdge
	{
		MapRoom first;
		MapRoom second;
		
		public MapEdge(MapRoom a, MapRoom b)
		{
			first = a;
			second = b;
		}
		
		public boolean ContainsRoom(MapRoom room)
		{
			if (first.id == room.id || second.id == room.id)
			{
				return true;
			}
			return false;
		}
	}
	
	public List<Room> physicsRooms = new ArrayList<Room>();
	
	public MapRoom rooms[];

	public List<MapEdge> edges = new ArrayList<MapEdge>();
	
	public Map(World world, int numRooms, int xdim, int ydim, int min)
	{
		int numPoints = numRooms;
		float mapSizeX = xdim;
		float mapSizeY = ydim;
		float minDistance = min;
		rooms = new MapRoom[numPoints];
		for (int i = 0; i < numPoints; i++)
		{
			
			Vector2 location;
			boolean validLocation = false;
			do {
				validLocation = true;
				if (i == 0)
				{
					location = new Vector2(-mapSizeX - minDistance * 2, 0);
				}
				else if (i == numPoints-1)
				{
					location = new Vector2(mapSizeY + minDistance * 2,0);
				}
				else
				{
					location = Utils.RandomSquare(mapSizeX, mapSizeY);
				}
				for (int j = 0; j < i; j++)
				{
					Vector2 otherLocation = rooms[j].position.cpy();
					if (otherLocation.sub(location).len() < minDistance)
					{
						validLocation = false;
						break;
					}
				}				
			}
			while (!validLocation);
			
			rooms[i] = new MapRoom(location, cur_id);
			cur_id ++;
		}
		
		for (MapRoom room : rooms)
		{
			if (room.edges.size() >= 3)
			{
				continue;
			}
			
			int tries = 0;
			
			while (room.edges.size() < 2 && tries < 20)
			{
				MapRoom closest = null;
				float minDist = Float.MAX_VALUE;
				for (MapRoom otherRoom : rooms)
				{
					if (room.IsConnected(otherRoom) || room.id == otherRoom.id)
					{
						continue;
					}
					
					Vector2 newEdgeDirection = otherRoom.position.cpy().sub(room.position);
					
					if (newEdgeDirection.len() > 300)
					{
						continue;
					}
					
					boolean validEdge = true;
					// Check this room's edges to see if any are too close together
					for (MapEdge edge : room.edges)
					{
						Vector2 otherRoomPosition = room.id == edge.first.id ? edge.second.position.cpy() : edge.first.position.cpy();
						Vector2 edgeDirection = otherRoomPosition.sub(room.position);
						
						if (newEdgeDirection.cpy().nor().dot(edgeDirection.nor()) > .7f)
						{
							validEdge = false;
						}
					}
					// Check other room's edges
					for (MapEdge edge : otherRoom.edges)
					{
						Vector2 otherRoomPosition = otherRoom.id == edge.first.id ? edge.second.position.cpy() : edge.first.position.cpy();
						Vector2 edgeDirection = otherRoomPosition.sub(otherRoom.position);
						
						if (newEdgeDirection.cpy().nor().scl(-1).dot(edgeDirection.nor()) > .7f)
						{
							validEdge = false;
						}
					}
					
					if (!validEdge)
					{
						continue;
					}
					
					float dist = otherRoom.position.cpy().sub(room.position).len();
					if (dist < minDist)
					{
						minDist = dist;
						closest = otherRoom;
					}
				}
				
				if (closest != null)
				{
					MapEdge e = room.Connect(closest);
					edges.add(e);					
				}
				else 
				{
					tries ++;
				}
			}
		}
		
		for (MapEdge edge : edges)
		{
			Room r = new Room();
			
			Vector2 dir1 = (edge.second.position.cpy().sub(edge.first.position)).nor();
			
			Vector2 firstEntrance = dir1.cpy().scl(30).add(edge.first.position);
			Vector2 cross1 = dir1.cpy();
			cross1.rotate90(1);
			cross1.scl(10);
			
			Vector2 dir2 = dir1.cpy().scl(-1);
			
			Vector2 secondEntrance = dir2.cpy().scl(30).add(edge.second.position);
			Vector2 cross2 = dir2.cpy();
			cross2.rotate90(1);
			cross2.scl(10);
			
			Vector2 [] opening1 = new Vector2[] { firstEntrance.cpy().add(cross1), firstEntrance.cpy().add(cross1.scl(-1))};
			Vector2 [] opening2 = new Vector2[] { secondEntrance.cpy().add(cross2), secondEntrance.cpy().add(cross2.scl(-1))};
			
			r.CreateCorridor(world, opening1, opening2);
			physicsRooms.add(r);
		}
		
		for (MapRoom room : rooms)
		{
			Room r = new Room();
						
			List<Vector2[]> openings = new ArrayList<Vector2[]>();
			
			for (MapEdge edge : room.edges)
			{
				Vector2 dir1 = (edge.second.position.cpy().sub(edge.first.position)).nor();
				
				Vector2 firstEntrance = dir1.cpy().scl(30).add(edge.first.position);
				Vector2 cross1 = dir1.cpy();
				cross1.rotate90(1);
				cross1.scl(10);
				
				Vector2 dir2 = dir1.cpy().scl(-1);
				
				Vector2 secondEntrance = dir2.cpy().scl(30).add(edge.second.position);
				Vector2 cross2 = dir2.cpy();
				cross2.rotate90(1);
				cross2.scl(10);
				
				Vector2 [] opening1 = new Vector2[] { firstEntrance.cpy().add(cross1), firstEntrance.cpy().add(cross1.scl(-1))};
				Vector2 [] opening2 = new Vector2[] { secondEntrance.cpy().add(cross2), secondEntrance.cpy().add(cross2.scl(-1))};
				
				Vector2 [] myOpening = edge.first.id == room.id ? opening1 : opening2;
				
				openings.add(myOpening);
			}
			

			r.CreateRoom(world, room.position, openings);
			
			physicsRooms.add(r);
		}
	}
	
	public void RenderLines(ShapeRenderer renderer)
	{
		for (Room r : physicsRooms)
		{
			r.RenderLines(renderer);
		}
	}
	
	public void Render(Matrix4 matrix)
	{
		for (Room r : physicsRooms)
		{
			r.Render(matrix);
		}
	}
}
