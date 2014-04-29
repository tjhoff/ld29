package net.yakuprising.ld29.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.yakuprising.ld29.LudumGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "UNDER";
		cfg.fullscreen = true;
		cfg.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		cfg.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		new LwjglApplication(new LudumGame(), cfg);
	}
}
