package net.yakuprising.ld29;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class ParticleManager 
{
	class Particle
	{
		Color color;
		float size;
		float rotation;
		float angularVelocity;
		Vector2 position;
		Vector2 velocity;		
		int timeToLive;
		
	}
	private static int indices[][] = 
		{{0,1,2}, {0,2,3}};
	
	private static Vector2 vertices[] =
		{ new Vector2(1,1), new Vector2(-1,1), new Vector2(-1,-1), new Vector2(1,-1) };
	
	private static Vector2 uvs[] = 
		{
		 new Vector2(1,0), new Vector2(0,0), new Vector2(0,1), new Vector2(1,1)
		};
	
	public static Mesh CreateMesh(Vector2 halfSize, float uvScale)
	{
		int numVertices = vertices.length;
		int numIndices = indices.length ;
		
		float vxData[] = new float[numVertices * 5];
		short idxData[] = new short[numIndices * 3];
		
		for (int i = 0; i < numVertices; i++)
		{
			int index = i*5;
			Vector2 vertex = vertices[i];
			Vector2 uv = uvs[i];
			vxData[index] = vertex.x * halfSize.x;
			vxData[index+1] = vertex.y * halfSize.y;
			vxData[index+2] = 0.0f;
			vxData[index+3] = uv.x * uvScale;
			vxData[index+4] = uv.y * uvScale;
		}
		
		for (int i = 0; i < numIndices; i++)
		{
			int index = i * 3;
			idxData[index] = (short)indices[i][0];
			idxData[index+1] = (short)indices[i][1];
			idxData[index+2] = (short)indices[i][2];
		}
		
		Mesh mesh = new Mesh(true, numVertices, numIndices*3, 
				new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), 
				new VertexAttribute( Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0"));
		mesh.setVertices(vxData);
		mesh.setIndices(idxData);
		return mesh;
	}
	
	private Texture texture;
	private ShaderProgram shader;
	
	private Particle[] particles;
	private int MAX_PARTICLES = 512;
	private int MAX_PARTICLE_TTL = 80;
	
	private int currentParticle;
	
	private float spread = .5f;
	
	Color initColor = Color.WHITE;
	Color endColor = new Color(.6f,.1f,0,0);
	
	private Mesh mesh;
	
	public ParticleManager(Mesh mesh, Texture texture, ShaderProgram shader)
	{
		this.mesh = mesh;
		this.texture = texture;
		this.shader = shader;
		
		particles = new Particle[MAX_PARTICLES];
		
		for (int i = 0; i < MAX_PARTICLES; i ++)
		{
			Particle particle = new Particle();
			
			particles[i] = particle;
			
			particle.position = new Vector2(0,0);
			particle.velocity = new Vector2(0,0);
			particle.timeToLive = 0;
			particle.size = 1.0f;
			particle.color = Color.WHITE;
			particle.rotation = 0.0f;
		}
	}
	
	public Particle GetNextParticle()
	{
		if (currentParticle + 1 > MAX_PARTICLES)
		{
			currentParticle = 0;
		}
		int thisParticle = currentParticle;
		currentParticle++;
		return particles[thisParticle];
	}
	
	public void CreateParticles(int num, Vector2 position, Vector2 velocity)
	{
		for (int i = 0; i < num; i ++)
		{
			Particle particle = GetNextParticle();
			
			particle.position.set(position);
			particle.velocity.set(velocity.add(Utils.RandomSpread(spread)));
			particle.timeToLive = MAX_PARTICLE_TTL;
			particle.size = 1;
			particle.color = Math.random() > .5 ? Color.RED : Color.YELLOW;
			particle.color.a = .5f;
			particle.rotation = (float)(Math.random() * Math.PI * 2);
			particle.angularVelocity = (float)((Math.random() - .5) * 2);
		}
	}
	
	public void Update(float updateScale)
	{
		for (Particle particle: particles)
		{
			if (particle.timeToLive == 0)
			{
				continue;
			}
			particle.velocity.scl(.9f);
			particle.position.add(new Vector2(particle.velocity).scl(updateScale));
			particle.angularVelocity *= .9f;
			particle.rotation += particle.angularVelocity * updateScale;
			
			particle.timeToLive --;
		}
	}
	
	public void Draw(Matrix4 matrix)
	{
		texture.bind();
		
		for (Particle particle: particles)
		{
			if (particle.timeToLive == 0)
			{
				continue;
			}
			
			float ratio = particle.timeToLive / (float)MAX_PARTICLE_TTL;
			Vector2 position = particle.position;
			float rotation = particle.rotation;
			float size = particle.size * ( .6f + .4f * ratio);
			Color color = new Color();
			color.r = initColor.r * ratio + endColor.r * 1/ratio;
			color.g = initColor.g * ratio + endColor.g * 1/ratio;
			color.b = initColor.b * ratio + endColor.b * 1/ratio;
			color.a = initColor.a * ratio + endColor.a * 1/ratio;
			
			Matrix4 drawMatrix = new Matrix4(matrix);
			drawMatrix.translate(position.x, position.y, 0);
			drawMatrix.rotate(0,0,1,(float)(rotation * 180.0 / Math.PI));
			drawMatrix.scale(size, size, 1);
			
			shader.begin();
			shader.setUniformMatrix("u_worldView", drawMatrix);
			shader.setUniformf("u_color", color.r, color.g, color.b, color.a);
			shader.setUniformi("u_texture", 0);
			mesh.render(shader, GL20.GL_TRIANGLES);
			shader.end();
			
		}	
		
	}

	public void SetTimeToLive(int i) {
		MAX_PARTICLE_TTL = i;
	}
	
	public void SetSpread(float spread)
	{
		this.spread = spread;
	}
}

