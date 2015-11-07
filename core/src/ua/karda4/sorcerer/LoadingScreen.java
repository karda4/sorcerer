package ua.karda4.sorcerer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public class LoadingScreen implements Screen, InputProcessor {

    private Texture background;
    private Texture title;
    private SpriteBatch batch;
    final Main main;
    private TextureAtlasParameter taParam;
    private BitmapFontParameter bfParam;
    private String CYRILLIC_CHARS;

    public LoadingScreen(Main _main) {
        main = _main;
        batch = new SpriteBatch();
        CYRILLIC_CHARS = Assets.loadStringFromFile("cyrillic_chars.txt");
        taParam = new TextureAtlasParameter(true);
        bfParam = new BitmapFontParameter();
        bfParam.flip = true;
    }
    
    private FreeTypeFontLoaderParameter getTTF_Param(int size){
    	FreeTypeFontLoaderParameter ttfParam = new FreeTypeFontLoaderParameter();
		ttfParam.fontFileName = Assets.path_font + Assets.fileFontOpenSans;
		ttfParam.fontParameters.size = size;
		ttfParam.fontParameters.minFilter = TextureFilter.Linear;
		ttfParam.fontParameters.magFilter = TextureFilter.Linear;
		ttfParam.fontParameters.flip = true;
		ttfParam.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS+CYRILLIC_CHARS;
		return ttfParam;
    }
 
    @Override
    public void show() {
    	Gdx.input.setInputProcessor(this);

        // depending on type loading appropriate assets
        switch (GameScreen.paintItemNext) {
            case GameScreen.I_MENU_MAIN:
                if(GameScreen.bFirstStart) {
                    // loading assets needed for loading screen
                    main.getManager().load(Assets.res_path + Assets.fileSplash, Texture.class);
                    main.getManager().load(Assets.res_path + Assets.fileTitle, Texture.class);
                    // waiting for above assets to load
                    main.getManager().finishLoading();

                    // creating background that will be drawn while AssetManager loads
                    background = main.getManager().get(Assets.res_path + Assets.fileSplash, Texture.class);
                    title = main.getManager().get(Assets.res_path + Assets.fileTitle, Texture.class);

                    main.getManager().load(Assets.pack_path + Assets.fileAtlasCommon, TextureAtlas.class, taParam);
                    main.getManager().load(Assets.pack_path + Assets.fileFontSkranji, BitmapFont.class, bfParam);
                    main.getManager().load(Assets.pack_path + Assets.fileAtlasAvatar, TextureAtlas.class, taParam);
                    main.getManager().load(Assets.res_path + Assets.fileTextureBack, Texture.class);
                    main.getManager().load(Assets.pack_path + Assets.fileAtlasBack, TextureAtlas.class, taParam);
                    main.getManager().load(Assets.path_audio + Assets.fileSoundClick3, Sound.class);
                    main.getManager().load(Assets.path_audio + Assets.fileSoundBang, Sound.class);
                    main.getManager().load(Assets.path_audio + Assets.fileSoundLevelWin, Sound.class);
                    main.getManager().load(Assets.path_audio + Assets.fileSoundLevelLose, Sound.class);
                    main.getManager().load(Assets.path_audio + Assets.fileSoundResultStar, Sound.class);
                    main.getManager().load(Assets.path_audio + Assets.fileSoundStar, Sound.class);
                    main.getManager().load(Assets.path_audio + Assets.fileMusicGame1, Music.class);
                    main.getManager().load(Assets.path_audio + Assets.fileMusicGame2, Music.class);
                    main.getManager().load(Assets.path_audio + Assets.fileMusicMenu1, Music.class);
                    main.getManager().load(Assets.pack_path + Assets.fileAtlasLocIco, TextureAtlas.class, taParam);


                    FileHandleResolver resolver = new InternalFileHandleResolver();
                    main.getManager().setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
                    main.getManager().setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

                    main.getManager().load(Assets.fileFontOpenSans0, BitmapFont.class, getTTF_Param(32));
                    main.getManager().load(Assets.fileFontOpenSans1, BitmapFont.class, getTTF_Param(20));
                }
                else{

                }
				break;
			case GameScreen.I_GAME:
            
            	break;
            case GameScreen.I_SELECT_LOCATION:
                main.getManager().load(Assets.res_path + Assets.fileMapBack, Texture.class);
                break;
        }
    }
    
    private float alphaBackground = 0.0f;
	private int gameTitleH;
	private int gameTitlePadd;
	private int gameTitleDY;
	private boolean gameTitleShow = false;
	private float timeSplash = 1.5f;
	private float timeTitle = 3.0f;
	private float timerPass = 0.0f;
 
    @Override
    public void render(float delta) {
    	math(delta);

        if(GameScreen.bFirstStart) {
            // drawing simple loading screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            Color clr = batch.getColor();
            batch.setColor(clr.r, clr.g, clr.b, alphaBackground);
            batch.draw(background, 0, 0, Assets.width, Assets.height);
            if (gameTitleShow) {
                float cw = title.getWidth();
                float ch = title.getHeight();
                float maxw = Assets.width;
                if (cw > maxw) {
                    ch = title.getHeight() * (maxw / cw);
                    cw = maxw;
                }
                float x = (Assets.width - cw) / 2;
                float y = gameTitlePadd - gameTitleDY;
                batch.draw(title, x, y, cw, ch);
            }
            batch.setColor(clr);
            //batch.draw(background, Assets.width - background.getWidth() >> 1, Assets.height - background.getHeight() >> 1);
            batch.end();
        }
    }
 
    @Override
    public void resize(int width, int height) {
    }
 
    @Override
    public void hide() {
        if(GameScreen.bFirstStart) {
            main.getManager().unload(Assets.res_path + Assets.fileSplash);
            main.getManager().unload(Assets.res_path + Assets.fileTitle);
        }
    }
 
    @Override
    public void pause() {
    }
 
    @Override
    public void resume() {
    }
 
    @Override
    public void dispose() {
    }

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(isGameTitleShowed()){
			completeLoading();
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		return false;
	}

    private void math(float delta){
    	timerPass += delta;
        if(GameScreen.bFirstStart) {
            if (alphaBackground < 1.0f) {
                alphaBackground += delta;
                if (alphaBackground > 1.0f) alphaBackground = 1.0f;
            }
            mathGameTitle(delta);

            if (canStartTitle()) {
                initTitle();
                if (isGameTitleShowedAndWaited()) {
                    completeLoading();
                }
            }
        }
        else{
            if(main.getManager().update()) completeLoading();
        }
    }
    
    private boolean b_complete = false;
    
    private void completeLoading(){
    	if(b_complete) return;
    	b_complete = true;
        main.runGameScreen();
    }
    
    private boolean canStartTitle(){
    	return main.getManager().update() && alphaBackground == 1.0f && timerPass >= timeSplash;
    }
    
    private void initTitle(){
    	if(gameTitleShow) return;
    	gameTitleShow = true;
    	gameTitleH = title.getHeight();
		gameTitlePadd = gameTitleH >> 2;
		gameTitleDY = gameTitleH + gameTitlePadd;
    }
    
    private void mathGameTitle(float dt){
    	if(!gameTitleShow) return;
		float speed = gameTitleH << 1;
		float dx = speed * dt;
		if(gameTitleDY > 0){
			gameTitleDY -= dx;
			if(gameTitleDY < 0) gameTitleDY = 0;
		}
	}
    
    private boolean isGameTitleShowed(){
    	return gameTitleShow && gameTitleDY == 0;
    }
    
    private boolean isGameTitleShowedAndWaited(){
		return isGameTitleShowed() && timerPass >= timeSplash + timeTitle;
	}

    public void init() {
        b_complete = false;
    }
}
