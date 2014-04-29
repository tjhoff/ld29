package net.yakuprising.ld29;

import com.badlogic.gdx.math.Vector2;

public class Utils 
{
	public static float Clamp(float val, float min, float max)
	{
		if (val < min)
		{
			return min;
		}
		if (val > max)
		{
			return max;
		}
		return val;
	}
		
	public static Vector2 VectorFromString(String str)
	{
		String[] strings = str.split(",");
		if (strings.length != 2)
		{
			System.out.println("can't do this shit");
			return new Vector2(0,0);
		}
		return new Vector2(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]));
	}
	
	public static Vector2 RandomSpread(float spreadSize)
	{
		double angle;
		float distance;
		angle = (Math.random() * Math.PI * 2);
		distance = (float)Math.random() * spreadSize;
		return new Vector2((float)Math.cos(angle) * distance, (float)Math.sin(angle) * distance);
	}
	
	public static Vector2 RandomCircle(float circleSize)
	{
		double angle;
		float distance;
		angle = (Math.random() * Math.PI * 2);
		distance = circleSize;
		return new Vector2((float)Math.cos(angle) * distance, (float)Math.sin(angle) * distance);
	}
	
	public static Vector2 RandomSquare(float spreadX, float spreadY)
	{
		float x = ((float)Math.random() - .5f) * spreadX * 2;
		float y = ((float)Math.random() - .5f) * spreadY * 2;
		return new Vector2(x,y);
	}
	
	public static float SmallestAngle(float angleA, float angleB)
	{
		float normal = angleA - angleB;
		float other = Math.min(angleA + (360-angleB), angleB + (360-angleA));
		return Math.min(normal, other);
	}
}