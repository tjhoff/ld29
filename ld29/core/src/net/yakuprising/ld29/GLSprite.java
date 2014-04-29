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

public class GLSprite {
	
	float colors[] = {.9f,.9f,.95f,.8f};
	private static int indices[][] = 
		{{0,1,2}, {0,2,3}};
	
	private static Vector2 vertices[] =
		{ new Vector2(1,1), new Vector2(-1,1), new Vector2(-1,-1), new Vector2(1,-1) };
	
	private static Vector2 uvs[] = 
		{
		 new Vector2(1,0), new Vector2(0,0), new Vector2(0,1), new Vector2(1,1)
		};
	
	public static Mesh CreateMesh(Vector2 halfSize, float uvScaleU, float uvScaleV)
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
			vxData[index+3] = uv.x * uvScaleU;
			vxData[index+4] = uv.y * uvScaleV;
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
	
	private Vector2 position = new Vector2(0,0);
	private float angle = 0;
	private Texture texture; 
	private Mesh mesh;
	private ShaderProgram shader;
	private float scale;
	private float parallax;
	
	
	public GLSprite(Texture t, Mesh m, ShaderProgram s, float scale, float parallax, Color color)
	{
		texture = t;
		mesh = m;
		shader = s;
		this.scale = scale;
		this.parallax = parallax;
		colors[0] = color.r;
		colors[1] = color.g;
		colors[2] = color.b;
		colors[3] = color.a;
		
	}
	
	public void SetTexture(Texture texture)
	{
		this.texture = texture;
	}
	
	public void Draw(Matrix4 matrix)
	{
		Matrix4 drawMatrix = new Matrix4(matrix);
		drawMatrix.translate(position.x*parallax, position.y*parallax, 0);
		drawMatrix.rotate(0,0,1,angle);
		drawMatrix.scale(scale, scale, 1);
		texture.bind();
		shader.begin();
		shader.setUniformMatrix("u_worldView", drawMatrix);
		shader.setUniformf("u_color", colors[0], colors[1], colors[2], colors[3]);
		shader.setUniformi("u_texture", 0);
		mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
	}
	
	public void SetColor(Color color)
	{
		colors[0] = color.r;
		colors[1] = color.g;
		colors[2] = color.b;
		colors[3] = color.a;
	}
	
	public void SetPosition(Vector2 position)
	{
		this.position.set(position);
	}
	
	public void SetAngle(float angle)
	{
		this.angle = angle;
	}
}
