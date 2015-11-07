package ua.karda4.sorcerer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class Main extends Game {
	
	private AssetManager manager;
	private Assets assets;
	private GameScreen gameScreen;
	private LoadingScreen loadingScreen;
	public static Main main;
	
	@Override
	public void create() {
		main = this;
		manager = new AssetManager();
		assets = new Assets();
		runLoadingScreen(GameScreen.I_MENU_MAIN);
		//runGameScreen();
	}

	@Override
	public void dispose() {
		 super.dispose();
		 manager.dispose();
	}
	
	public AssetManager getManager() {
        return manager;
    }
	
	public void runGameScreen(){
		if(gameScreen == null) gameScreen = new GameScreen(this, assets);
		setScreen(gameScreen);
	}
	
	public void runLoadingScreen(int type){
        GameScreen.paintItemNext = type;
		if(loadingScreen == null) loadingScreen = new LoadingScreen(this);
        loadingScreen.init();
		setScreen(loadingScreen);
	}
}
