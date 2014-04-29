package net.yakuprising.ld29;

import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ResourceManager
{
	private static Hashtable<String, Texture> textures = new Hashtable<String, Texture>();
	public static Texture GetTexture(String texturePath)
	{
		Texture texture = textures.get(texturePath);
		if (texture == null)
		{
			try {
				texture = new Texture(Gdx.files.internal(texturePath));
			}
			catch (Exception ex)
			{
				Pixmap pixmap = new Pixmap(64, 64, Format.RGBA8888);
				pixmap.setColor(Color.MAGENTA);
				pixmap.fill();
				texture = new Texture(pixmap);
			}
			
			texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			textures.put(texturePath, texture);
		}
		return texture;
	}
	
	private static Hashtable<String, Sound> sounds = new Hashtable<String, Sound>();
	public static Sound GetSound(String soundPath)
	{
		Sound sound = sounds.get(soundPath);
		if (sound == null)
		{
			sound = Gdx.audio.newSound(Gdx.files.internal(soundPath));
			sounds.put(soundPath, sound);
		}
		return sound;
	}
	static private String vertexShader = "attribute vec4 a_position;    \n" + 
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" + 
            "uniform mat4 u_worldView;\n" + 
            "varying vec4 v_color;" + 
            "varying vec2 v_texCoords;" + 
            "void main()                  \n" + 
            "{                            \n" + 
            "   v_color = vec4(1, 1, 1, 1); \n" + 
            "   v_texCoords = a_texCoord0; \n" + 
            "   gl_Position =  u_worldView * a_position;  \n"      + 
            "}                            \n" ;
	
	static private String fragmentShader = "#ifdef GL_ES\n" +
              "precision mediump float;\n" + 
              "#endif\n" + 
              "varying vec4 v_color;\n" + 
              "varying vec2 v_texCoords;\n" + 
              "uniform sampler2D u_texture;\n" + 
              "void main()                                  \n" + 
              "{                                            \n" + 
              "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
              "}";
	
	static private String flatColorVertexShader = "attribute vec4 a_position;    \n" + 
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" + 
            "uniform mat4 u_worldView;\n" + 
            "varying vec4 v_color;" + 
            "varying vec2 v_texCoords;" + 
            "void main()                  \n" + 
            "{                            \n" + 
            "   v_color = vec4(1, 1, 1, 1); \n" + 
            "   v_texCoords = a_texCoord0; \n" + 
            "   gl_Position =  u_worldView * a_position;  \n"      + 
            "}                            \n" ;
	
	static private String flatColorFragmentShader = "#ifdef GL_ES\n" +
              "precision mediump float;\n" + 
              "#endif\n" + 
              "varying vec4 v_color;\n" + 
              "varying vec2 v_texCoords;\n" + 
              "uniform sampler2D u_texture;\n" +
              "uniform vec4 u_color;\n" +
              "void main()                                  \n" + 
              "{                                            \n" + 
              "  gl_FragColor = u_color * v_color * texture2D(u_texture, v_texCoords);\n" +
              "}";
	
	
	
	
	static private ShaderProgram flatShader;
	
	static ShaderProgram GetFlatShader()
	{
		if (flatShader == null)
		{
			flatShader = new ShaderProgram(vertexShader, fragmentShader);
			if (!flatShader.isCompiled())
			{
				System.out.println(flatShader.getLog());
			}
		}
		return flatShader;
	}
	
	static private ShaderProgram flatColorShader;
	
	static ShaderProgram GetFlatColorShader()
	{
		if (flatColorShader == null)
		{
			flatColorShader = new ShaderProgram(flatColorVertexShader, flatColorFragmentShader);
			if (!flatColorShader.isCompiled())
			{
				System.out.println(flatColorShader.getLog());
			}
		}
		return flatColorShader;
		
	}
}
