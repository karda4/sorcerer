package ua.karda4.sorcerer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import ua.karda4.sorcerer.Main;

public class DesktopLauncher {
	
	private static final String packFileName = "common";
	//private static final String inputDir = "d:/JOB/Vanya/!GRAPHICS/Sorcerer/atlas/" + packFileName;
	private static final String inputDir = "/users/Admin/documents/Vanya/GRAPHICS/Sorcerer/atlas/" + packFileName;
	private static final String outputDir = inputDir + "_res";
	private static final boolean bCreateTexture = false;//true;
	
	public static void main (String[] arg) {
		if(bCreateTexture){
			Settings settings = new Settings();
			settings.maxWidth = 2048;
			settings.maxHeight = 2048;
			TexturePacker.process(settings, inputDir, outputDir, packFileName);
		}
        
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Sorcerer";
		config.width = 1024;
		config.height = 720;
		new LwjglApplication(new Main(), config);
	}
}
