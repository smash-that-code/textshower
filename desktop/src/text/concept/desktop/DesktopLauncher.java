package text.concept.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import text.concept.TextShower;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
		config.samples = 4; //antialiasing
		config.title = "Start small and keep it fun (with text)!";
		config.width = 1920;
		config.height = 1000;
		new LwjglApplication(new TextShower(), config);
	}
}
