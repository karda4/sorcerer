package ua.karda4.sorcerer;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

import de.tomgrill.gdxfacebook.core.GDXFacebook;
import de.tomgrill.gdxfacebook.core.GDXFacebookCallback;
import de.tomgrill.gdxfacebook.core.GDXFacebookConfig;
import de.tomgrill.gdxfacebook.core.GDXFacebookError;
import de.tomgrill.gdxfacebook.core.GDXFacebookGraphRequest;
import de.tomgrill.gdxfacebook.core.GDXFacebookSystem;
import de.tomgrill.gdxfacebook.core.JsonResult;
import de.tomgrill.gdxfacebook.core.SignInMode;
import de.tomgrill.gdxfacebook.core.SignInResult;
import ua.karda4.sorcerer.multiscreen.VirtualViewport;
import ua.karda4.sorcerer.tween.ButtonResult;
import ua.karda4.sorcerer.tween.GoalParticle;
import ua.karda4.sorcerer.tween.KTween;
import ua.karda4.sorcerer.tween.KTweenAccessor;
import ua.karda4.sorcerer.tween.SelectLevelItem;

public class GameScreen implements Screen, InputProcessor {

	private static final String TAG = GameScreen.class.getSimpleName();
	final Main main;
	
	OrthographicCamera camera;
	Graphics g;
	
	public static Color clrFontWhite = Color.WHITE;
	public static Color clrFontBrown = Color.valueOf("9c6727");
	public static Color clrFontRed = Color.RED;
	private static Color clrMenuItemActive = Color.valueOf("56dec0");
	private static Color clrMenuItemPassive = Color.valueOf("9dde55");
	private static Color clrLocTextBack = Color.valueOf("cce9e6");
	public static Color clrSubstrate = Color.valueOf("fefdfa");
	public static Color clrTutFrame = Color.valueOf("ffffff");
	public static Color clrBlackTransparent = new Color(0x000000C0);
	public static Color clrBackGame = Color.valueOf("a39d98");
	Audio audio;
	private Assets assets;
	public static TweenManager tweenManager;

	public GameScreen(final Main _main, Assets _assets) {
		main = _main;
		assets = _assets;
		camera = new OrthographicCamera();
		camera.setToOrtho(true, assets.W, assets.H);
		g = new Graphics(camera, assets.W, assets.H);
		tweenManager = new TweenManager();
		Tween.registerAccessor(GoalParticle.class, new KTweenAccessor());
		Tween.registerAccessor(SelectLevelItem.class, new KTweenAccessor());
		Tween.registerAccessor(ButtonResult.class, new KTweenAccessor());
		Tween.registerAccessor(TutorialHand.class, new KTweenAccessor());
		Tween.registerAccessor(KTween.class, new KTweenAccessor());


		// Gdx.app.setLogLevel(Application.LOG_INFO);

		/* set log level to see some log output */
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		install_gdx_facebook();

		initCanvas();

		int realH = Gdx.graphics.getHeight();
		int realW = Gdx.graphics.getWidth();

		resize(realW, realH);
//		runSplash();
	}

	@Override
	public void render(float delta) {
		setDTime(delta);
		tweenManager.update(delta);
		//int delta_milisec = (int) (delta * 1000);
		//Gdx.app.log("time", delta + " / " + delta_milisec + " / " + Gdx.graphics.getDeltaTime());
		math();
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		g.setProjectionMatrix(camera.combined);

		g.begin();
		paint(g, paintItem);
//		font.drawString(g, String.format("screen=%1$sx%2$s pack=%3$sx%4$s", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), width, height), width2, height2, Uni.HCENTER_VCENTER);
		g.end();
		//if(paintItem == I_SPLASH) disposeSplash();
	}

	@Override
	public void resize(int width, int height) {
		//MultipleVirtualViewportBuilder multipleVirtualViewportBuilder = new MultipleVirtualViewportBuilder(1024, 720, 1280, 800);
		//VirtualViewport vv = multipleVirtualViewportBuilder.getVirtualViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		VirtualViewport vv = new VirtualViewport(assets.W, assets.H);
		camera.setToOrtho(true, vv.getWidth(), vv.getHeight());
		//int transX = (((float)virtualW - (float)virtualH * aspectRatio) / 2f);
		int virtualW = assets.W;
		int virtualH = assets.H;
		float aspectRatio = (float) width / (float) height;
		if(aspectRatio >= (float) virtualW / (float) virtualH){
			//camera.setToOrtho(true, virtualH * aspectRatio, virtualH );
			int transX = Math.abs((int)(((float)virtualW - (float)virtualH * aspectRatio) / 2f)) ;
			camera.translate( -transX , 0 );
			camera.update();
		}
		else{
			//camera.setToOrtho(true, virtualW , virtualW / aspectRatio);
			int transY = Math.abs((int)(((float)virtualH - (float)virtualW / aspectRatio) / 2f)) ;
			camera.translate( 0 , -transY );
			camera.update();
		}
	}

	public static boolean bFirstStart = true;

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		switch(paintItemNext){
			case I_MENU_MAIN:
				if(bFirstStart) {
					initApp();
					loadAtlasCommon();
					loadFont();
					loadBoomParticles();
					loadAtlasBack();
					loadAtlasAvatar();
					audio = new Audio();
					loadAtlasLocIco();
					initItemSkillSize();
					initOutfitRects();
					//curLevel = 0;
					//loadGame();
					runMainMenu();
					bFirstStart = false;
				}
				else{

				}
				break;
			case I_SELECT_LOCATION:
				runSelectLocation(false);
				break;
		}
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		savePreferences();
		//if(!CHEAT_EASY) savePers(hero, fileSaveHero);
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		atlasCommon.dispose();
		atlasBack.dispose();
		font.dispose();
		g.dispose();
		audio.dispose();
		assets.getManager().dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode != Keys.BACK){
			bKeyboard = true;
		}
		boolean bButtonBack = (keycode == Keys.BACK || keycode == Keys.ESCAPE);
		switch(paintItem){
		case I_MENU_MAIN:
			if(processButtonBack(null, null, bButtonBack, true)) return true;
			if (keycode == Keys.LEFT || keycode == Keys.UP){
				cursorMenuMain--;
				if(cursorMenuMain < 0) cursorMenuMain = aAllMenuItems - 1;
			}
			if (keycode == Keys.RIGHT || keycode == Keys.DOWN){
				cursorMenuMain++;
				if(cursorMenuMain >= aAllMenuItems) cursorMenuMain = 0;
			}
			if (keycode == Keys.ENTER){
				audio.playSound(Audio.SND_CLICK_MENU);
				runSelectLevel(false);
			}
			break;
		case I_DIALOG:
		case I_MENU_PAUSE:
		case I_ACHIEVEMENTS_STAT:
		case I_LIST:
		case I_SELECT_LOCATION:
		case I_SELECT_LEVEL:
		case I_PERS:
		case I_SHOP:
		case I_SKILLS:
		case I_IMPROVE_STATS:
		case I_NEW_LEVEL:
		case I_ACHIEVEMENTS_DONE:
			if(processButtonBack(null, null, bButtonBack, true)) return true;
			break;
		case I_GAME:
			if(processButtonBack(null, null, bButtonBack, true)) return true;
			if (keycode == Keys.LEFT)
				keyMoveCursor(-1, 0);
			if (keycode == Keys.RIGHT)
				keyMoveCursor(1, 0);
			if (keycode == Keys.UP)
				keyMoveCursor(0, -1);
			if (keycode == Keys.DOWN)
				keyMoveCursor(0, 1);
			if (keycode == Keys.ENTER){
				if (bNoMoves) {
					restartGame();
				}else if (bResult) {
					
				}else{
					keyPressCursor();
				}
			}
			break;
		}
		return false;
	}
	
	private boolean processButtonBack(Vector3 touch, Rectangle rect, boolean anyway, boolean stateUp){
		if (!anyway && (rect == null || !rect.contains(touch.x, touch.y))) return false;
		if(!stateUp){
			audio.playSound(Audio.SND_CLICK_MENU);
			return true;
		}
		switch(paintItem){
		case I_MENU_MAIN:
			runDialog(false, DIALOG_TYPE_EXIT_GAME);
			break;
		case I_MENU_PAUSE:
			paintItem = I_GAME;
			break;
		case I_DIALOG:
			disposeDialog();
			paintItem = popPaintItemPrev();
			//paintItem = paintItemPrevious;
			break;
		case I_ACHIEVEMENTS_STAT:
		case I_LIST:
		case I_SELECT_LOCATION:
			runMainMenu();
			break;
		case I_ACHIEVEMENTS_DONE:
			addAchievmentBonus();
			paintItem = popPaintItemPrev();
			runWindowNewLevel();
			break;
		case I_SELECT_LEVEL:
			if (bConfirmWindow) {
				bConfirmWindow = false;
			} else if (bCannotSelectLevel) {
				bCannotSelectLevel = false;
			} else {
				runSelectLocation(true);
			}
			break;
		case I_PERS:
			if(outfitBagSelected >= 0){
				outfitBagSelected = -1;
			}
			else{
				switch(popPaintItemPrev()){
				case I_SELECT_LEVEL:
					runSelectLevel(true);
					break;
				case I_SELECT_LOCATION:
					runSelectLocation(true);
					break;
				}
			}
			break;
		case I_SHOP:
			if(outfitShopSelected >= 0){
				outfitShopSelected = -1;
				cursorOutfitSelected = -1;
			}
			else if(outfitBagSelected >= 0){
				outfitBagSelected = -1;
			}
			else{
				switch (popPaintItemPrev()) {
				case I_SELECT_LEVEL:
					runSelectLevel(true);
					break;
				case I_SELECT_LOCATION:
					runSelectLocation(true);
					break;
				}
			}
			break;
		case I_SKILLS:
			if(skillSelected >= 0){
				skillSelected = -1;
			}
			else{
				popPaintItemPrev();
				runPers(true);
			}
			break;
		case I_IMPROVE_STATS:
		case I_NEW_LEVEL:
			paintItem = popPaintItemPrev();
			break;
		case I_GAME:
			runPauseMenu();
			break;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	Vector3 touchPosDown = new Vector3();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchPosDown.set(screenX, screenY, 0);
		camera.unproject(touchPosDown);
		bKeyboard = false;
		
		switch(paintItem){
		case I_MENU_MAIN:
			for (int i = 0; i < aAllMenuItems; i++) {
				if (rectMenuItems[i].contains(touchPosDown.x, touchPosDown.y)) {
					cursorMenuMain = i;
					cursorMenuMainPressed = true;
					audio.playSound(Audio.SND_CLICK_MENU);
					return true;
				}
			}
			break;
		case I_ACHIEVEMENTS_STAT:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			break;
		case I_ACHIEVEMENTS_DONE:
			if(processButtonBack(touchPosDown, rButAchievmentDoneOK, false, false)) break;
			break;
		case I_LIST:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			break;
		case I_MENU_PAUSE:
			for (int i = 0; i < aPauseMenuItems; i++) {
				if (rectPauseMenuItems[i].contains(touchPosDown.x, touchPosDown.y)) {
					cursorPauseMenu = i;
					audio.playSound(Audio.SND_CLICK_MENU);
					return true;
				}
			}
			break;
		case I_DIALOG:
			for (int i = 0; i < aDialogButton; i++) {
				if (rectDialogButton[i].contains(touchPosUp.x, touchPosUp.y)) {
					audio.playSound(Audio.SND_CLICK_MENU);
					return true;
				}
			}
			break;
		case I_SELECT_LOCATION:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			for (int i = 0; i < aSelLocItems; i++) {
				if (rectSelLocItems[i].contains(touchPosDown.x, touchPosDown.y)) {
					cursorSelectLocation = i;
					Tween.to(twSelLocIco[i], KTweenAccessor.SCALE, selLocIcoSelectedTime).target(selLocIcoMaxScale).start(tweenManager);
					audio.playSound(Audio.SND_CLICK_MENU);
					return true;
				}
			}
			touchDownMenuPersAndShop();
			break;
		case I_SELECT_LEVEL:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			for (int i = 0; i < aSelLevItems; i++) {
				if (selLevItems[i].contains(touchPosDown.x, touchPosDown.y)) {
					cursorSelectLevel = i;
					audio.playSound(Audio.SND_CLICK_MENU);
					return true;
				}
			}
			touchDownMenuPersAndShop();
			break;
		case I_PERS:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			if (rButSkills.contains(touchPosDown.x, touchPosDown.y)) {
				audio.playSound(Audio.SND_CLICK_MENU);
			}
			break;
		case I_SHOP:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			break;
		case I_SKILLS:
			if(processButtonBack(touchPosDown, rectButBack, false, false)) break;
			break;
		case I_IMPROVE_STATS:
			if(processButtonBack(touchPosDown, rectButApply, false, false)) break;
			for (int i = 0; i < aPersInfoCommon; i++) {
				if (rButImproveStat[i].contains(touchPosDown.x, touchPosDown.y)) {
					cursorPersInfoCommon = i;
					audio.playSound(Audio.SND_CLICK_MENU);
					return true;
				}
			}
			break;
		case I_NEW_LEVEL:
			if(rButNewLevelImprove.contains(touchPosUp.x, touchPosUp.y)){
				audio.playSound(Audio.SND_CLICK_MENU);
			}else if(rButNewLevelOK.contains(touchPosUp.x, touchPosUp.y)){
				audio.playSound(Audio.SND_CLICK_MENU);
			}
			break;
		case I_GAME:
			if(gameMessage != null){
				if(gameMessage.getRectOK().contains(touchPosDown.x, touchPosDown.y)) {
					audio.playSound(Audio.SND_CLICK_MENU);
				}
			}else if (bNoMoves) {
				return false;
			}else  if (bResult) {
				return false;
			}else if (rectButMenu.contains(touchPosDown.x, touchPosDown.y)) {
				audio.playSound(Audio.SND_CLICK_MENU);
				return true;
			}else if (rectMap.contains(touchPosDown.x, touchPosDown.y)) {
				if(hero.skillUsingIndex >= 0) return false;
				if (!bCursor) {
					int x = (int) ((touchPosDown.x - MAP_X) / CELL_W);
					int y = (int) ((touchPosDown.y - MAP_Y) / CELL_H);
					bCursor = true;
					setCursor(x, y);
				}
				return true;
			}
			break;
		}
		return false;
	}
	
	Vector3 touchPosUp = new Vector3();
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		touchPosUp.set(screenX, screenY, 0);
		camera.unproject(touchPosUp);
		if(paintItem == I_GAME){
			if(touchPosUp.x < hudW && touchPosUp.y < hudAvatarH){
				//hero.setLevel(1);
				//hero.setExp(1275);
				enemy.setHealth(0);
			}
			if(touchPosUp.x > width - hudW && touchPosUp.y < hudAvatarH){
				hero.setHealth(0);
			}
		}
		switch(paintItem){
		case I_SPLASH:
			break;
		case I_MENU_MAIN:
			if(rButFBLogin.contains(touchPosUp.x, touchPosUp.y)){
				if(!bFBLoggedIn) {
					loginWithReadPermissions();
				}
				else{
					logout();
				}
			}
			else {
				for (int i = 0; i < aAllMenuItems; i++) {
					if (i == cursorMenuMain && rectMenuItems[i].contains(touchPosUp.x, touchPosUp.y)) {
						int index = getMenuMainIndex(i);
						switch (index) {
							case MENU_MAIN_ITEM_PLAY:
								main.runLoadingScreen(I_SELECT_LOCATION);
								break;
							case MENU_MAIN_ITEM_ACHIEVEMENTS:
								runAchievementsStat();
								break;
							case MENU_MAIN_ITEM_HELP:
								runList();
								break;
							case MENU_MAIN_ITEM_ARENA:

								break;
							case MENU_MAIN_ITEM_EXIT:
								runDialog(false, DIALOG_TYPE_EXIT_GAME);
								break;
							case MENU_MAIN_ITEM_MUSIC:
								Audio.bMusic = !Audio.bMusic;
								if (Audio.bMusic)
									audio.playMusic(Audio.MSC_MAIN_MENU, true);
								else
									audio.pauseMusic();
								break;
							case MENU_MAIN_ITEM_SOUND:
								Audio.bSound = !Audio.bSound;
								if (Audio.bSound)
									audio.playSound(Audio.SND_CLICK_MENU);
								break;
							case MENU_MAIN_ITEM_RESET_GAME:
								if (!bShowResetGameButton)
									break;
								runDialog(false, DIALOG_TYPE_RESET_GAME);
								break;
						}
						return true;
					}
				}
			}
			cursorMenuMainPressed = false;
			break;
		case I_ACHIEVEMENTS_STAT:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			break;
		case I_ACHIEVEMENTS_DONE:
			if(processButtonBack(touchPosUp, rButAchievmentDoneOK, false, true)) break;
			break;
		case I_LIST:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			break;
		case I_MENU_PAUSE:
			for (int i = 0; i < aPauseMenuItems; i++) {
				if (i == cursorPauseMenu && rectPauseMenuItems[i].contains(touchPosUp.x, touchPosUp.y)) {
					switch (i) {
					case MENU_PAUSE_ITEM_CONTINUE:
						paintItem = I_GAME;
						break;
					case MENU_PAUSE_ITEM_RESTART:
						restartGame();
						break;
					case MENU_PAUSE_ITEM_SELECT_LEVEL:
						runSelectLevel(false);
						break;
					case MENU_PAUSE_ITEM_MENU:
						runMainMenu();
						break;
					}
					return true;
				}
			}
			cursorPauseMenu = -1;
			break;
		case I_DIALOG:
			for (int i = 0; i < aDialogButton; i++) {
				if (rectDialogButton[i].contains(touchPosUp.x, touchPosUp.y)) {
					switch (i) {
					case DIALOG_BUTTON_OK:
						switch(dialogType){
						case DIALOG_TYPE_RESET_GAME:
							resetGameProgress();
							break;
						case DIALOG_TYPE_EXIT_GAME:
							Gdx.app.exit();
							break;
						}
						break;
					case DIALOG_BUTTON_CANCEL:
						break;
					}
					disposeDialog();
					paintItem = popPaintItemPrev();
					return true;
				}
			}
			break;
		case I_SELECT_LOCATION:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			for (int i = 0; i < aSelLocItems; i++) {
				if (i == cursorSelectLocation && rectSelLocItems[i].contains(touchPosUp.x, touchPosUp.y)) {
					disposeSelectLocation();
					curLocation = i;
					runSelectLevel(false);
					return true;
				}
			}
			touchUpMenuPersAndShop();
			if(cursorSelectLocation >= 0){
				Tween.to(twSelLocIco[cursorSelectLocation], KTweenAccessor.SCALE, selLocIcoSelectedTime).target(1.0f).start(tweenManager);
			}
			cursorSelectLocation = -1;
			break;
		case I_SELECT_LEVEL:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			if(bConfirmWindow){
				if(rectButtonFight.contains(touchPosUp.x, touchPosUp.y)){
					disposeSelectLevel();
					
					loadGame();
					return true;
				}
			}
			else{
				if(bCannotSelectLevel){
					bCannotSelectLevel = false;
				} else {
					for (int i = 0; i < aSelLevItems; i++) {
						if (i == cursorSelectLevel && selLevItems[i].contains(touchPosUp.x, touchPosUp.y)) {
							if(isLevelOpen(i) || CHEAT_ALL_LEVELS){
								curLevel = i;
								runConfirmWindow();
								bCannotSelectLevel = hero.getLife() == 0;
							}
							else{
								aStarCannotSelectLevel = getMustCollectStars(i);
								bCannotSelectLevel = true;
							}
						}
					}
				}
				touchUpMenuPersAndShop();
				cursorSelectLevel = -1;
			}
			break;
		case I_PERS:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			if(outfitBagSelected >= 0){
				touchUpOutfitBagSelected(touchPosUp);
			}
			else{
				if (rButSkills.contains(touchPosUp.x, touchPosUp.y)) {
					runSkills(false);
				}
				else{
					if(rButNewLevelImprove.contains(touchPosUp.x, touchPosUp.y) && hero.getStatFree() > 0){
						runImproveStats(false);
					}
					else{
						touchUpWinBag(touchPosUp);
						touchUpWinBody(touchPosUp);
					}
				}
			}
			break;
		case I_SHOP:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			if(outfitShopSelected >= 0){
				for (int i = 0; i < aButWinOutfitShopSelected; i++) {
					if (rButWinOutfitShopSelected[i].contains(touchPosUp.x, touchPosUp.y)) {
						switch (i) {
						case BUT_OUTFIT_SHOP_BUY:
							hero.buyBagItem(outfitShopSelected);
							runAchievementsDone(ACH_ID_COLLECT_THINGS);
							break;
						case BUT_OUTFIT_SHOP_CANCEL:
							break;
						}
						outfitShopSelected = -1;
					}
				}
				cursorOutfitSelected = -1;
			}
			else if(outfitBagSelected >= 0){
				touchUpOutfitBagSelected(touchPosUp);
			}
			else{
				for (int i = 0; i < aShopItems; i++) {
					if (rShopItems[i].contains(touchPosUp.x, touchPosUp.y)) {
						outfitShopSelected = shopItems[i];
						cursorOutfitSelected = -1;
						break;
					}
				}
				touchUpWinBag(touchPosUp);
			}
			break;
		case I_SKILLS:
			if(processButtonBack(touchPosUp, rectButBack, false, true)) break;
			if(skillSelected >= 0){
				for (int i = 0; i < aBUT_SKILL_SELECTED; i++) {
					if (rectButSkillSelected[i].contains(touchPosUp.x, touchPosUp.y)) {
						int skill_level = hero.skillsAllLevel[skillSelected] + 1;
						boolean b_max_level = skill_level >= Skills.getMaxLevel(Pers.HERO, skillSelected);
						switch (i) {
						case BUT_SKILL_SELECTED_MONEY:
							if (b_max_level)
								break;
							if (!hero.isSkillAvailableToBuy(skillSelected))
								break;
							int money = Skills.getParam(Pers.HERO, skillSelected, skill_level, Skill.SP_MONEY);
							if (hero.getMoney() >= money) {
								hero.setMoney(hero.getMoney() - money);
								hero.skillsAllLevel[skillSelected]++;
							}
							break;
						case BUT_SKILL_SELECTED_CASH:
							if (b_max_level)
								break;
							if (!hero.isSkillAvailableToBuy(skillSelected))
								break;
							int cash = Skills.getParam(Pers.HERO, skillSelected, skill_level, Skill.SP_CASH);
							if (hero.getCash() >= cash) {
								hero.setCash(hero.getCash() - cash);
								hero.skillsAllLevel[skillSelected]++;
							}
							break;
						case BUT_SKILL_SELECTED_ACTIVATE:
							if (skill_level == 0)
								break;
							if (Skills.skill[Pers.HERO][skillSelected].gameBoxIndex >= 0) {
								hero.delSkillGameBox(skillSelected);
							} else {
								hero.addSkillGameBox(skillSelected);
							}
							skillSelected = -1;
							break;
						}

					}
				}
			}
			else{
				for(int i = 0, iy = 0; iy < skillWindowMap.length; iy++){
					for(int ix = 0; ix < skillWindowMap[iy].length; ix++){
						if (rSkillsAll[i].contains(touchPosUp.x, touchPosUp.y)
								&& touchPosUp.x == touchPosDown.x && touchPosUp.y == touchPosDown.y) {
							int id = skillWindowMap[iy][ix];
							int i_skill = id - 1;
							skillSelected = i_skill;
						}
						i++;
					}
				}
				for (int i = 0; i < Pers.maxSkillsGameBox; i++) {
					if (hero.skillsGameBox[i] >= 0) {
						if (rSkillsHero[i].contains(touchPosUp.x, touchPosUp.y)) {
							skillSelected = hero.skillsGameBox[i];
						}
					}
				}
			}
			skillDragging = -1;
			break;
		case I_IMPROVE_STATS:
			if(processButtonBack(touchPosUp, rectButApply, false, true)) break;
			for (int i = 0; i < aPersInfoCommon; i++) {
				if (i == cursorPersInfoCommon && rButImproveStat[i].contains(touchPosUp.x, touchPosUp.y)) {
					addPersStat(persInfoCommon[i]);
					return true;
				}
			}
			cursorPauseMenu = -1;
			break;
		case I_NEW_LEVEL:
			if(rButNewLevelImprove.contains(touchPosUp.x, touchPosUp.y)){
				runImproveStats(false);
			}else if(rButNewLevelOK.contains(touchPosUp.x, touchPosUp.y)){
				paintItem = popPaintItemPrev();
				//bWindowNewLevel = false;
				//bWindowNewLevelClose = true;
			}
			break;
		case I_GAME:
			if(gameMessage != null){
				if(gameMessage.getRectOK().contains(touchPosUp.x, touchPosUp.y)) {
					gameMessage.aLive = false;
				}
			}
			else if (bNoMoves) {
				restartGame();
			}else if (bResult) {
				for (int i = 0; i < resButton.length; i++) {
					if (resButton[i].contains(touchPosUp.x, touchPosUp.y)) {
						switch (resButton[i].type) {
						case ButtonResult.RESTART:
							restartGame();
							return true;
						case ButtonResult.MENU:
							runMainMenu();
							return true;
						case ButtonResult.NEXT:
							nextGameLevel();
							return true;
						case ButtonResult.LEVEL:
							runSelectLevel(false);
							return true;
						}
					}
				}
			} else if (bEarnAchievment) {
				if (rectButEarnAchievment.contains(touchPosUp.x, touchPosUp.y)) {
					runPauseMenu();
				}
			}else if (rectButMenu.contains(touchPosUp.x, touchPosUp.y)) {
				runPauseMenu();
			}else if (rectMap.contains(touchPosUp.x, touchPosUp.y)) {
				int x = (int) ((touchPosUp.x - MAP_X) / CELL_W);
				int y = (int) ((touchPosUp.y - MAP_Y) / CELL_H);
				if(hero.skillUsingIndex >= 0){
					boolean b_done = false;
					switch (hero.skillUsingIndex) {
					case Skill.HERO_SHOT:
						b_done = deleteHorizontalLine(x, y);
						break;
					case Skill.HERO_RAIN:
						b_done = changeToGem(x, y, P_WATER);
						break;
					case Skill.HERO_HANDFUL_EARTH:
						b_done = deleteSquare(x, y, 3, 3);
						break;
					case Skill.HERO_THROW_STONE:
						b_done = deleteVerticalLine(x, y);
						break;
					}
					if (b_done) {
						hero.skillUsingIndex = -1;
						bDoneStep = true;
					}
				}
				else{
					if (bCursor) {
						float dx = touchPosUp.x - touchPosDown.x;
						float dy = touchPosUp.y - touchPosDown.y;
						float adx = dx >= 0 ? dx : -dx;
						float ady = dy >= 0 ? dy : -dy;
						if (adx > 0 || ady > 0) {
							int ox = 0, oy = 0;
							if (adx > ady) {
								ox = dx > 0 ? 1 : -1;
							} else {
								oy = dy > 0 ? 1 : -1;
							}
							changeBallsByUser(cursorX, cursorY, ox, oy, true);
						}
					}
				}
			}else if(touchUpHudHero(touchPosUp)){
				
			}
			
			bCursor = false;
			break;
		}
		return false;
	}

	private void touchUpWinBag(Vector3 touch) {
		if(outfitBagSelected >= 0) return;
		for(int i = 0; i < maxBagCells; i++){
			if(rBagItems[i].contains(touch.x, touch.y)){
				if(hero.bagItems[i] != null){
					outfitBagSelected = i;//hero.bagItems[i].index;
					cursorOutfitSelected = -1;
					break;
				}
			}
		}
	}
	
	private void touchUpWinBody(Vector3 touch){
		if(outfitBagSelected >= 0) return;
		for(int i = 0; i < aBodyPart; i++){
			if(rBodyPart[i].contains(touch.x, touch.y)){
				int index = hero.bodyIndex[i];
				if(index >= 0){
					outfitBagSelected = hero.bodyIndex[i];//hero.bagItems[i].index;
					cursorOutfitSelected = -1;
					break;
				}
			}
		}
	}

	private void touchUpOutfitBagSelected(Vector3 touch) {
		for(int i = 0; i < aButWinOutfitBagSelected; i++){
			if(rButWinOutfitBagSelected[i].contains(touch.x, touch.y)) {
				switch(i){
				case BUT_OUTFIT_BAG_REPAIR:
					hero.repairBagItem(outfitBagSelected);
					break;
				case BUT_OUTFIT_BAG_SALE:
					hero.saleBagItem(outfitBagSelected);
					break;
				case BUT_OUTFIT_BAG_PUT_ON:
					if(isBagOutfitWeared(outfitBagSelected)){
						hero.takeOffBody(outfitBagSelected);
					}
					else{
						hero.putOnBody(outfitBagSelected);
					}
					break;
				}
				outfitBagSelected = -1;
			}
		}
	}

	Vector3 touchDragPos = new Vector3();

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		touchDragPos.set(screenX, screenY, 0);
		camera.unproject(touchDragPos);
		switch (paintItem) {
			case I_GAME:
				if (gameMessage != null) return false;
				if (hero.skillUsingIndex >= 0) return false;
				if (bCursor) {
					if (rectMap.contains(touchPosDown.x, touchPosDown.y)) {
						// if (test_pnt(MAP_X, MAP_Y, MAP_W, MAP_H)) {
						float dx = touchDragPos.x - touchPosDown.x;
						float dy = touchDragPos.y - touchPosDown.y;
						float adx = dx >= 0 ? dx : -dx;
						float ady = dy >= 0 ? dy : -dy;
						if (adx > (CELL_W >> 1) || ady > (CELL_H >> 1)) {
							int ox = 0, oy = 0;
							if (adx > ady) {
								ox = dx > 0 ? 1 : -1;
							} else {
								oy = dy > 0 ? 1 : -1;
							}
							changeBallsByUser(cursorX, cursorY, ox, oy, true);
							bCursor = false;
						}
					}
				}
				break;
			case I_SKILLS:
				if(skillDragging >= 0){
					skillDraggingPos.set(touchDragPos.x - touchPosDown.x, touchDragPos.y - touchPosDown.y);
				}
				else {
					for (int i = 0, iy = 0; iy < skillWindowMap.length; iy++) {
						for (int ix = 0; ix < skillWindowMap[iy].length; ix++) {
							if (rSkillsAll[i].contains(touchPosDown.x, touchPosDown.y)) {
								int id = skillWindowMap[iy][ix];
								int i_skill = id - 1;
								skillDragging = i_skill;
								skillDraggingPos.set(0, 0);
							}
							i++;
						}
					}
				}
				break;
		}
		// Gdx.app.log("touchDragged: ", "x="+screenX+" y="+screenY);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	//----------------------- facebook functions ----------------//
	private GDXFacebook gdxFacebook;
	private Array<String> permissionsRead = new Array<String>();
	private Array<String> permissionsPublish = new Array<String>();

	private void install_gdx_facebook(){
		permissionsRead.add("email");
		permissionsRead.add("public_profile");
		permissionsRead.add("user_friends");

		permissionsPublish.add("publish_actions");

		GDXFacebookConfig config = new GDXFacebookConfig();
		config.APP_ID = "532384866939592"; // required
		config.PREF_FILENAME = ".facebookSessionData"; // optional
		config.GRAPH_API_VERSION = "v2.5"; // optional, default is v2.5

		gdxFacebook = GDXFacebookSystem.install(config);
	}

	private void loginWithReadPermissions() {
		gdxFacebook.signIn(SignInMode.READ, permissionsRead, new GDXFacebookCallback<SignInResult>() {

			@Override
			public void onSuccess(SignInResult result) {
				Gdx.app.debug(TAG, "SIGN IN (read permissions): User signed in successfully.");

				gainMoreUserInfo();
				setPublishButtonStatus(true);
				setLoginButtonStatus(true);

			}

			@Override
			public void onCancel() {
				Gdx.app.debug(TAG, "SIGN IN (read permissions): User canceled login process");

			}

			@Override
			public void onFail(Throwable t) {
				Gdx.app.error(TAG, "SIGN IN (read permissions): Technical error occured:");
				logout();
				t.printStackTrace();
			}

			@Override
			public void onError(GDXFacebookError error) {
				Gdx.app.error(TAG, "SIGN IN (read permissions): Error login: " + error.getErrorMessage());
				logout();

			}

		});

	}

	private Rectangle rButFBLogin = new Rectangle();
	private boolean bFBLoggedIn = false;

	private void initButFacebook(){
		float w, h, x, y;
		w = trFBLogin.getRegionWidth();
		h = trFBLogin.getRegionHeight();
		x = width2 - (w / 2);
		y = height - butPadd - h;
		rButFBLogin.setSize(w, h);
		rButFBLogin.setPosition(x, y);
	}

	private void drawFacebookLogButton(Graphics g){
		g.draw((bFBLoggedIn) ? trFBLogout : trFBLogin, rButFBLogin.x, rButFBLogin.y);
	}

	private void loginWithPublishPermissions() {
		gdxFacebook.signIn(SignInMode.PUBLISH, permissionsPublish, new GDXFacebookCallback<SignInResult>() {

			@Override
			public void onSuccess(SignInResult result) {
				Gdx.app.debug(TAG, "SIGN IN (publish permissions): User logged in successfully.");

				gainMoreUserInfo();
				setPublishButtonStatus(true);
				setLoginButtonStatus(true);
			}

			@Override
			public void onCancel() {
				Gdx.app.debug(TAG, "SIGN IN (publish permissions): User canceled login process");
			}

			@Override
			public void onFail(Throwable t) {
				Gdx.app.error(TAG, "SIGN IN (publish permissions): Technical error occured:");
				logout();
				t.printStackTrace();
			}

			@Override
			public void onError(GDXFacebookError error) {
				Gdx.app.error(TAG, "SIGN IN (publish permissions): Error login: " + error.getErrorMessage());
				logout();
			}

		});
	}

	private void setPublishButtonStatus(boolean enabled) {
		/*if (enabled) {
			requestPublishPermissionsButton.setTexture(requestEnabledTexture);
		} else {
			requestPublishPermissionsButton.setTexture(requestDisabledTexture);
		}*/

	}

	private void gainMoreUserInfo() {

		GDXFacebookGraphRequest request = new GDXFacebookGraphRequest().setNode("me").useCurrentAccessToken();

		gdxFacebook.newGraphRequest(request, new GDXFacebookCallback<JsonResult>() {

			@Override
			public void onSuccess(JsonResult result) {
				JsonValue root = result.getJsonValue();

				String fbNickname = root.getString("name");
				String fbIdForThisApp = root.getString("id");
				hero.nick = fbNickname;

				Gdx.app.debug(TAG, "Graph Reqest: successful");
			}

			@Override
			public void onCancel() {
				logout();
				Gdx.app.debug(TAG, "Graph Reqest: Request cancelled. Reason unknown.");

			}

			@Override
			public void onFail(Throwable t) {
				Gdx.app.error(TAG, "Graph Reqest: Failed with exception.");
				logout();
				t.printStackTrace();
			}

			@Override
			public void onError(GDXFacebookError error) {
				Gdx.app.error(TAG, "Graph Reqest: Error. Something went wrong with the access token.");
				logout();

			}
		});

	}

	private void setLoginButtonStatus(boolean loggedIn) {
		bFBLoggedIn = loggedIn;
	}

	private void logout() {
		gdxFacebook.signOut();
		//checkbox.setChecked(false);
		setPublishButtonStatus(false);
		setLoginButtonStatus(false);
	}

	// -----------------------GAME_JAVA-------------------------
	public boolean CHEAT_ALL_LEVELS = false;//true;
	public float CHEAT_SPEED = 1.0f;
	private boolean CHEAT_EASY = false;//true;

	public static int width, height;
	public static int width2;
	public static int height2;

	public final void initCanvas() {
		width = assets.W;// getWidth();
		height = assets.H;// getHeight();
		width2 = width >> 1;
		height2 = height >> 1;
		rScreen.setSize(width, height);
		rScreen.setPosition(0, 0);
	}

	public int curLevel;
	public int curLocation;
	private int maxLocation = 4;
	private int maxLevel;

	private int aStartOpenLevel = 5;
	private boolean bPlayedLevel[];
	private boolean bDoneLevel[];
	private boolean bDoneCurLevel;
	
	private boolean bTutorial;

	private void initApp() {
		loadTexts();
		loadLevelUp();
		loadHeroSkillsData();
		loadOutfitData();
		loadMapPattern();
		loadBallColors();
		loadMonsterData();
		loadMonsterSkillsData();
		loadAchievmentData();
		
		initMaxLevelParams();
		
		createEnemy();
		createHero();
		
		loadPreferences();
	}
	
	private void createHero(){
		hero = new Pers(this, Pers.HERO);
		hero.nick = "nick";
		hero.setLifeMax(5);
		hero.setLife(hero.getLifeMax());
		initHeroFirst();
	}
	
	private void initMaxLevelParams(){
		recordStep = new int[maxLevel];
		recordStar = new int[maxLevel];
		recordStarMax = maxLevel * starMaxNumber;
		bPlayedLevel = new boolean[maxLevel];
		bDoneLevel = new boolean[maxLevel];
	}
	
	private void initHeroFirst(){
		hero.setHealthMax(30);
		hero.setHealth(hero.getHealthMax(false));
		hero.setLevel(1);
		hero.setExp(0);
		hero.setPower(1);
		hero.setTactic(1);
		hero.setBrave(1);
		hero.setFire(1);
		hero.setWater(1);
		hero.setAir(1);
		hero.setEarth(1);
		hero.setMoney(100);
		hero.setCash(0);
		hero.setStatFree(0);
		
		hero.reset();
		
		if(CHEAT_EASY){
			//hero.setHealthMax(5);
			hero.setHealth(hero.getHealthMax(false));
			hero.setLevel(70);
			hero.setFire(999);
			hero.setWater(999);
			hero.setAir(999);
			hero.setEarth(999);
			hero.setMoney(1000000);
			hero.setCash(100);
		}
	}
	
	private void createEnemy(){
		enemy = new Pers(this, Pers.MONSTER);
	}
	
	private String gameVersion = "1.0.7";
	private boolean bGameNewVersion = false;
	
	private Preferences pref;
	private final String prefFile = "ua.karda4.sorcerer.pref";
	private final String pref_bSound = "bSound";
	private final String pref_bMusic = "bMusic";
	private final String pref_recordStep = "recordStep";
	private final String pref_recordStar = "recordStar";
	private final String pref_bPlayedLevel = "aPlayedLevel";
	private final String pref_bDoneLevel = "aDoneLevel";
	private final String pref_gameVersion = "gameVersion";
	private final String pref_heroLevel = "hLevel";
	private final String pref_heroLife = "hLife";
	private final String pref_heroLifeMax = "hLifeMax";
	private final String pref_heroMoney = "hMoney";
	private final String pref_heroCash = "hCash";
	private final String pref_heroHealthMax = "hHealthMax";
	private final String pref_heroHealth = "hHealth";
	private final String pref_heroExp = "hExp";
	private final String pref_heroPower = "hPower";
	private final String pref_heroTactic = "hTactic";
	private final String pref_heroBrave = "hBrave";
	private final String pref_heroFire = "hFire";
	private final String pref_heroWater = "hWater";
	private final String pref_heroAir = "hAir";
	private final String pref_heroEarth = "hEarth";
	private final String pref_heroStatFree = "hStatFree";
	private final String pref_heroAvatar = "hAvatar";
	private final String pref_heroNick = "hNick";
	private final String pref_heroSkillsGameBox = "hSkillsGameBox";
	private final String pref_heroSkillsAllLevel = "hSkillsAllLevel";
	private final String pref_heroAOpenBagCells = "hAOpenBagCells";
	private final String pref_heroBagItems = "hBagItems";
	private final String pref_heroBagItemsStrength = "hBagItemsStrength";
	private final String pref_heroBodyIndex = "hBodyIndex";
	private final String pref_heroBAchievment = "hBAchievment";
	private final String pref_heroAWinFights = "hAWinFights";
	private final String pref_heroACombo4 = "hACombo4";
	private final String pref_heroACombo5 = "hACombo5";
	private final String pref_heroALose = "hALose";
	private final String pref_heroAEarn5Stars = "hAEarn5Stars";
	private final String pref_heroCauseDamage = "hCauseDamage";
	private final String pref_heroSpendMoney = "hSpendMoney";
	
	private void loadPreferences(){
		if(pref == null){
			pref = Gdx.app.getPreferences(prefFile);
		}
		String version = pref.getString(pref_gameVersion);
		if(!gameVersion.equals(version)){
			bGameNewVersion = true;
			return;
		}
		Audio.bSound = pref.getBoolean(pref_bSound, true);
		Audio.bMusic = pref.getBoolean(pref_bMusic, true);
		for(int j = 0; j < maxLevel; j++){
			recordStep[j] = pref.getInteger(pref_recordStep+j, 0);
			recordStar[j] = pref.getInteger(pref_recordStar+j, 0);
			bPlayedLevel[j] = pref.getBoolean(pref_bPlayedLevel+j, false);
			bDoneLevel[j] = pref.getBoolean(pref_bDoneLevel+j, false);
		}
		
		//hero
		hero.setLevel(pref.getInteger(pref_heroLevel));
		hero.setLife(pref.getInteger(pref_heroLife));
		hero.setLifeMax(pref.getInteger(pref_heroLifeMax));
		hero.setMoney(pref.getInteger(pref_heroMoney));
		hero.setCash(pref.getInteger(pref_heroCash));
		hero.setHealthMax(pref.getInteger(pref_heroHealthMax));
		hero.setHealth(pref.getInteger(pref_heroHealth));
		hero.setExp(pref.getInteger(pref_heroExp));
		hero.setPower(pref.getInteger(pref_heroPower));
		hero.setTactic(pref.getInteger(pref_heroTactic));
		hero.setBrave(pref.getInteger(pref_heroBrave));
		hero.setFire(pref.getInteger(pref_heroFire));
		hero.setWater(pref.getInteger(pref_heroWater));
		hero.setAir(pref.getInteger(pref_heroAir));
		hero.setEarth(pref.getInteger(pref_heroEarth));
		hero.setStatFree(pref.getInteger(pref_heroStatFree));
		hero.setAvatar(pref.getInteger(pref_heroAvatar));
		hero.nick = pref.getString(pref_heroNick);
		for(int i = 0; i < Pers.maxSkillsGameBox; i++){
			hero.skillsGameBox[i] = pref.getInteger(pref_heroSkillsGameBox+i);
		}
		for(int i = 0; i < Skills.maxSkills[Pers.HERO]; i++){
			hero.skillsAllLevel[i] = pref.getInteger(pref_heroSkillsAllLevel+i);
		}
		hero.aOpenBagCells = pref.getInteger(pref_heroAOpenBagCells);
		for(int i = 0; i < maxBagCells; i++){
			int v = pref.getInteger(pref_heroBagItems + i);
			int strength = pref.getInteger(pref_heroBagItemsStrength + i);
			if(v >= 0) hero.setBagItem(i, v, strength);
		}
		for(int i = 0; i < aBodyPart; i++){
			hero.bodyIndex[i] = pref.getInteger(pref_heroBodyIndex+i);
		}
		for(int i = 0; i < aAchievments; i++){
			hero.bAchievment[i] = pref.getBoolean(pref_heroBAchievment+i, false);
		}
		hero.aWinFights = pref.getInteger(pref_heroAWinFights);
		hero.aCombo4 = pref.getInteger(pref_heroACombo4);
		hero.aCombo5 = pref.getInteger(pref_heroACombo5);
		hero.aLoseFights = pref.getInteger(pref_heroALose);
		hero.aEarn5Stars = pref.getInteger(pref_heroAEarn5Stars);
		hero.causeDamage = pref.getInteger(pref_heroCauseDamage);
		hero.spendMoney = pref.getInteger(pref_heroSpendMoney);
	}
	
	private void savePreferences(){
		if(pref == null) return;
		pref.putString(pref_gameVersion, gameVersion);
		pref.putBoolean(pref_bSound, Audio.bSound);
		pref.putBoolean(pref_bMusic, Audio.bMusic);
		for(int j = 0; j < maxLevel; j++){
			pref.putInteger(pref_recordStep+j, recordStep[j]);
			pref.putInteger(pref_recordStar+j, recordStar[j]);
			pref.putBoolean(pref_bPlayedLevel+j, bPlayedLevel[j]);
			pref.putBoolean(pref_bDoneLevel+j, bDoneLevel[j]);
		}
		
		//hero
		pref.putInteger(pref_heroLevel, hero.getLevel());
		pref.putInteger(pref_heroLife, hero.getLife());
		pref.putInteger(pref_heroLifeMax, hero.getLifeMax());
		pref.putInteger(pref_heroMoney, hero.getMoney());
		pref.putInteger(pref_heroCash, hero.getCash());
		pref.putInteger(pref_heroHealthMax, hero.getHealthMax(false));
		pref.putInteger(pref_heroHealth, hero.getHealth());
		pref.putInteger(pref_heroExp, hero.getExp());
		pref.putInteger(pref_heroPower, hero.getPower(false));
		pref.putInteger(pref_heroTactic, hero.getTactic(false));
		pref.putInteger(pref_heroBrave, hero.getBrave(false));
		pref.putInteger(pref_heroFire, hero.getFire(false));
		pref.putInteger(pref_heroWater, hero.getWater(false));
		pref.putInteger(pref_heroAir, hero.getAir(false));
		pref.putInteger(pref_heroEarth, hero.getEarth(false));
		pref.putInteger(pref_heroStatFree, hero.getStatFree());
		pref.putInteger(pref_heroAvatar, hero.getAvatar());
		pref.putString(pref_heroNick, hero.nick);
		for(int i = 0; i < Pers.maxSkillsGameBox; i++){
			pref.putInteger(pref_heroSkillsGameBox+i, hero.skillsGameBox[i]);
		}
		for(int i = 0; i < Skills.maxSkills[Pers.HERO]; i++){
			pref.putInteger(pref_heroSkillsAllLevel+i, hero.skillsAllLevel[i]);
		}
		pref.putInteger(pref_heroAOpenBagCells, hero.aOpenBagCells);
		for(int i = 0; i < maxBagCells; i++){
			int v = -1, strength = 0;
			if(hero.bagItems[i] != null){
				v = hero.bagItems[i].index;
				strength = hero.bagItems[i].getCurStrength();
			}
			pref.putInteger(pref_heroBagItems + i, v);
			pref.putInteger(pref_heroBagItemsStrength + i, strength);
		}
		for(int i = 0; i < aBodyPart; i++){
			pref.putInteger(pref_heroBodyIndex+i, hero.bodyIndex[i]);
		}
		for(int i = 0; i < aAchievments; i++){
			pref.putBoolean(pref_heroBAchievment+i, hero.bAchievment[i]);
		}
		pref.putInteger(pref_heroAWinFights, hero.aWinFights);
		pref.putInteger(pref_heroACombo4, hero.aCombo4);
		pref.putInteger(pref_heroACombo5, hero.aCombo5);
		pref.putInteger(pref_heroALose, hero.aLoseFights);
		pref.putInteger(pref_heroAEarn5Stars, hero.aEarn5Stars);
		pref.putInteger(pref_heroCauseDamage, hero.causeDamage);
		pref.putInteger(pref_heroSpendMoney, hero.spendMoney);
		pref.flush();
	}
	

/*
	public static void writeFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		//file.writeString(s, false);
		file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(s), false);
	}

	public static String readFile(String fileName) {
		FileHandle file = Gdx.files.local(fileName);
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				//return s;
				return com.badlogic.gdx.utils.Base64Coder.decodeString(s);
			}
		}
		return "";
	}
	
	private final String fileSaveHero = "ua.karda4.sorcerer.hero.sav";
	
	public static void savePers(Pers pers, String file_name) {
		Json json = new Json();
		writeFile(file_name, json.toJson(pers));
	}
	
	public static Pers loadPers(String file_name, int kind) {
		String save = readFile(file_name);
		if (!save.isEmpty()) {
			Json json = new Json();
			Pers pers = json.fromJson(Pers.class, save);
			return pers;
		}
		return null;
	}*/

	private float _dTime;

	private void setDTime(float dt) {
		_dTime = (CHEAT_SPEED == 1) ? dt : dt * CHEAT_SPEED;
	}

	private float getDTime() {
		return _dTime;
	}

	private void math() {
		switch(paintItem){
		case I_GAME:
			break;
		default:
			hero.mathLife(getDTime());
			break;
		}
		switch (paintItem) {
		/*case I_SPLASH:
			mathSplash();
			break;*/
		case I_MENU_MAIN:
			mathMainMenu();
			break;
		case I_ACHIEVEMENTS_STAT:
			mathAchievementsStat();
			break;
		case I_ACHIEVEMENTS_DONE:
			mathAchievementsDone();
			break;
		case I_MENU_PAUSE:
			mathPauseMenu();
			break;
		case I_LIST:
			mathList();
			break;
		case I_SELECT_LOCATION:
			mathSelectLocation();
			break;
		case I_SELECT_LEVEL:
			mathSelectLevel();
			break;
		case I_GAME:
			mathGame();
			break;
		case I_DIALOG:
			mathDialog();
			break;
		case I_PERS:
			mathPers();
			break;
		case I_SHOP:
			mathShop();
			break;
		case I_SKILLS:
			mathSkills();
			break;
		case I_IMPROVE_STATS:
			mathImproveStats();
			break;
		case I_NEW_LEVEL:
			mathWindowNewLevel();
			break;
		}
	}

	public int paintItem;
	public static int paintItemNext = -1;
	//public static byte paintItemPrevious;
	public static final byte I_MENU_MAIN = 1;
	public static final byte I_GAME = 2;
	public static final byte I_SPLASH = 3;
	public static final byte I_SELECT_LOCATION = 5;
	public static final byte I_SELECT_LEVEL = 6;
	public static final byte I_LIST = 7;
	public static final byte I_MENU_PAUSE = 8;
	public static final byte I_DIALOG = 9;
	public static final byte I_ACHIEVEMENTS_STAT = 10;
	public static final byte I_PERS = 11;
	public static final byte I_SHOP = 12;
	public static final byte I_SKILLS = 13;
	public static final byte I_IMPROVE_STATS = 14;
	public static final byte I_ACHIEVEMENTS_DONE = 15;
	public static final byte I_NEW_LEVEL = 16;
	
	private int aPaintItemPrev = 0;
	private int maxPaintItemPrev = 10;
	private int paintItemPrev[] = new int[maxPaintItemPrev];
	
	public void setPaintItemPrev(int item){
		if(aPaintItemPrev == maxPaintItemPrev){
			for(int i = 0; i < maxPaintItemPrev - 1; i++){
				paintItemPrev[i] = paintItemPrev[1 + 1];
			}
			paintItemPrev[aPaintItemPrev - 1] = item;
		}
		else{
			paintItemPrev[aPaintItemPrev++] = item;
		}
	}
	
	public int popPaintItemPrev(){
		if(aPaintItemPrev == 0) return -1;
		aPaintItemPrev--;
		return paintItemPrev[aPaintItemPrev];
	}
	
	public int getPaintItemPrev(){
		if(aPaintItemPrev == 0) return -1;
		return paintItemPrev[aPaintItemPrev - 1];
	}

	public final void paint(Graphics g, int item) {
		try {
			switch (item) {
			case I_MENU_MAIN:
				drawMainMenu(g);
				break;
			case I_MENU_PAUSE:
				drawPauseMenu(g);
				break;
			case I_LIST:
				drawList(g);
				break;
			case I_GAME:
				drawGame(g);
				break;
			case I_SELECT_LOCATION:
				drawSelectLocation(g);
				break;
			case I_SELECT_LEVEL:
				drawSelectLevel(g);
				break;
			case I_DIALOG:
				drawDialog(g);
				break;
			case I_ACHIEVEMENTS_STAT:
				drawAchievementsStat(g);
				break;
			case I_ACHIEVEMENTS_DONE:
				drawAchievementsDone(g);
				break;
			case I_PERS:
				drawPers(g);
				break;
			case I_SHOP:
				drawShop(g);
				break;
			case I_SKILLS:
				drawWindowSkills(g);
				break;
			case I_IMPROVE_STATS:
				drawImproveStats(g);
				break;
			case I_NEW_LEVEL:
				drawWindowNewLevel(g);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//----------------- ACHIEVEMENTS STATISTIC ----------------------------//
	
	private void runAchievementsStat(){
		paintItem = I_ACHIEVEMENTS_STAT;
		initAchievementsStat();
	}
	
	private void initAchievementsStat(){
		int x = width - butBackW - butPadd;
		int y = height - butPadd - butBackH;
		rectButBack.setPosition(x, y);
		
		initRectAchievmentStat();
	}
	
	private void mathAchievementsStat(){

	}
	
	private Rectangle rWinAchievmentStat = new Rectangle();
	private Rectangle rAchievmentStat[];
	private int winAchievmentStatTitleH;
	private int winAchivementStatPaddX;
	private int winAchivementStatPaddY;
	private int rAchievmentStatW, rAchievmentStatH;
	
	private void initRectAchievmentStat(){
		int fontH = font.getHeight();
		winAchivementStatPaddX = fontH;
		winAchivementStatPaddY = fontH;
		winAchievmentStatTitleH = fontH << 1;
		
		int winw = width;
		int winh = height - winAchievmentStatTitleH - butBackH - (butPadd << 1);
		rWinAchievmentStat.setSize(winw, winh);
		rWinAchievmentStat.setPosition(0, winAchievmentStatTitleH);
		
		if(rAchievmentStat == null){
			rAchievmentStat = new Rectangle[aAchievments];
			for(int i = 0; i < aAchievments; i++) rAchievmentStat[i] = new Rectangle();
		}
		int aw = 3;
		int ah = 4;
		
		rAchievmentStatW = (int) ((rWinAchievmentStat.width - (aw + 1) * winAchivementStatPaddX) / aw);
		rAchievmentStatH = (int) ((rWinAchievmentStat.height - (ah + 1) * winAchivementStatPaddY) / ah);
		float x_start = rWinAchievmentStat.x + winAchivementStatPaddX;
		float x_end = rWinAchievmentStat.x + rWinAchievmentStat.width - winAchivementStatPaddX;
		float y_start = rWinAchievmentStat.y + winAchivementStatPaddY;
		float x = x_start, y = y_start;
		int dw = winAchivementStatPaddX, dh = winAchivementStatPaddY;
		for(int i = 0; i < aAchievments; i++){
			rAchievmentStat[i].setSize(rAchievmentStatW, rAchievmentStatH);
			rAchievmentStat[i].setPosition(x, y);
			x += rAchievmentStatW + dw;
			if(x >= x_end){
				y += rAchievmentStatH + dh;
				x = x_start;
			}
		}
	}
	
	public Rectangle rScreen = new Rectangle();
	
	private void drawAchievementsStat(Graphics g){
		int fontH = font.getHeight();
		drawBackImage(g);
		drawButtonBack(g);
		
		float x = width2;
		float y = winAchievmentStatTitleH >> 1;
		font.drawString(g, TXT[1][7], x, y, Uni.HCENTER_VCENTER);
		
		g.fillRect(Color.LIGHT_GRAY, rWinAchievmentStat);
		g.setClip(rWinAchievmentStat);
		int padd = fontH >> 1;
		int icoW = rAchievmentStatH - (padd << 1);
		int icoH = icoW;
		int wrapWidth = rAchievmentStatW - icoW - (padd << 1) - padd;
		for(int i = 0; i < aAchievments; i++){
			Color clr_back = hero.bAchievment[i] ? Color.GOLD : Color.GRAY;
			Color clr_font = hero.bAchievment[i] ? clrFontBrown : clrFontWhite;
			g.fillRect(clr_back, rAchievmentStat[i]);
			x = rAchievmentStat[i].x;
			y = rAchievmentStat[i].y;
			if(y >= rWinAchievmentStat.y + rWinAchievmentStat.height) break;
			x += padd;
			y += padd;
			g.fillRect(Color.WHITE, x, y, icoW, icoH);
			x += icoW + padd;
			font.drawString(g, achievmentText[i][AT_TITLE], clr_font, x, y, Uni.LEFT_TOP);
			y += fontH + padd;
			font_mini.drawWrapped(g, achievmentText[i][AT_DESCRIPTION], clr_font, x, y, wrapWidth);
		}
		g.setClip(rScreen);
	}
	
	//----------------- ACHIEVEMENTS DONE ----------------------------//
	
	public static final int ACH_ID_REACH_LVL = 1;
	public static final int ACH_ID_WIN_FIGHTS = 2;
	public static final int ACH_ID_COLLECT_MONEY = 3;
	public static final int ACH_ID_REPAIR_THINGS = 4;
	public static final int ACH_ID_COLLECT_THINGS = 5;
	public static final int ACH_ID_COMBO4 = 6;
	public static final int ACH_ID_COMBO5 = 7;
	public static final int ACH_ID_LOSE_FIGHTS = 8;
	public static final int ACH_ID_EARN_5_STARS = 9;
	public static final int ACH_ID_CAUSE_DAMAGE = 10;
	public static final int ACH_ID_SPEND_MONEY = 11;
	
	public boolean runAchievementsDone(int id) {
		int index = -1;
		int v = -1;
		for(int i = 0; i < aAchievments; i++){
			int ach[] = achievmentParam[i];
			if(ach[AP_ID] == id && !hero.bAchievment[i]){
				switch(id){
				case ACH_ID_REACH_LVL:
					v = hero.getLevel();
					break;
				case ACH_ID_WIN_FIGHTS:
					v = hero.aWinFights;
					break;
				case ACH_ID_COLLECT_MONEY:
					v = hero.getMoney();
					break;
				case ACH_ID_COLLECT_THINGS:
					v = hero.aBagItems;
					break;
				case ACH_ID_COMBO4:
					v = hero.aCombo4;
					break;
				case ACH_ID_COMBO5:
					v = hero.aCombo5;
					break;
				case ACH_ID_LOSE_FIGHTS:
					v = hero.aLoseFights;
					break;
				case ACH_ID_EARN_5_STARS:
					v = hero.aEarn5Stars;
					break;
				case ACH_ID_CAUSE_DAMAGE:
					v = hero.causeDamage;
					break;
				case ACH_ID_SPEND_MONEY:
					v = hero.spendMoney;
					break;
				}
				if(v >= ach[AP_REQUIREMENT]){
					index = i;
					break;
				}
			}
		}
		if(index < 0) return false;
		setPaintItemPrev(paintItem);
		paintItem = I_ACHIEVEMENTS_DONE;
		iAchievmentDone = index;
		initAchievementsDone();
		return true;
	}
	
	private Rectangle rWinAchievmentDone = new Rectangle();
	private Vector2 vWinAchievmentDone = new Vector2();
	private Rectangle rButAchievmentDoneOK = new Rectangle();
	private int iAchievmentDone;

	private void initAchievementsDone() {
		int fontH = font.getHeight();
		float w = width2;
		float h = height2;
		float x = (width - w) / 2;
		float y = (height - h) / 2;
		rWinAchievmentDone.setSize(w, h);
		rWinAchievmentDone.setPosition(x, y);
		rWinAchievmentDone.getCenter(vWinAchievmentDone);
		
		w = font.stringWidth(strButOK) + (fontH << 1);
		h = fontH << 1;
		x = rWinAchievmentDone.x + (rWinAchievmentDone.width - w) / 2;
		y = rWinAchievmentDone.y + rWinAchievmentDone.height - butPadd - h;
		rButAchievmentDoneOK.setSize(w, h);
		rButAchievmentDoneOK.setPosition(x, y);
	}
	
	private void addAchievmentBonus(){
		int p[] = achievmentParam[iAchievmentDone];
		for(int i = aAP - 1; i >= 0; i--){
			if(p[i] > 0){
				switch(i){
				case AP_EXP:
					hero.setExp(hero.getExp() + p[i]);
					break;
				case AP_MONEY:
					hero.setMoney(hero.getMoney() + p[i]);
					break;
				case AP_CASH:
					hero.setCash(hero.getCash() + p[i]);
					break;
				}
			}
		}
		hero.bAchievment[iAchievmentDone] = true;
	}

	private void mathAchievementsDone() {
		
	}

	private void drawAchievementsDone(Graphics g) {
		paint(g, getPaintItemPrev());
		g.fillRect(clrSubstrate, rWinAchievmentDone);
		float x, y;
		int fontH = font.getHeight();
		int padd = fontH >> 1;
		x = vWinAchievmentDone.x;
		y = rWinAchievmentDone.y + padd + (fontH >> 1);
		font.drawString(g, TXT[13][8], clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		y += fontH << 1;
		font.drawString(g, achievmentText[iAchievmentDone][AT_TITLE], clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		y += fontH;
		x = rWinAchievmentDone.x + padd;
		int w = (int) (rWinAchievmentDone.width - (padd << 1));
		font_mini.drawWrapped(g, achievmentText[iAchievmentDone][AT_DESCRIPTION], clrFontBrown, x, y, w, 1);
		y = rButAchievmentDoneOK.y - (fontH << 1);
		x = vWinAchievmentDone.x;
		String s = null;
		int arr[] = {AP_EXP, AP_MONEY, AP_CASH};
		for(int i = arr.length - 1; i >= 0; i--){
			int index = arr[i];
			if(achievmentParam[iAchievmentDone][index] > 0){
				switch(index){
				case AP_EXP:
					s = TXT[12][7];
					break;
				case AP_MONEY:
					s = TXT[12][10];
					break;
				case AP_CASH:
					s = TXT[12][11];
					break;
				}
				s += ": "+achievmentParam[iAchievmentDone][index];
				font.drawString(g, s, clrFontBrown, x, y, Uni.HCENTER_VCENTER);
				y -= fontH << 1;
			}
		}
		drawButtonText(g, strButOK, rButAchievmentDoneOK, false);
	}

	// ---------------------------- TEXT --------------------------------//
	public static String TXT[][];
	
	private void loadTexts() {
		TXT = loadStringMatrix("text.txt", "=");
		listHelpTxt = "";
		for(int i = 0; i < TXT[I_LIST].length; i++){
			if(i > 0) listHelpTxt += '\n';
			listHelpTxt += TXT[I_LIST][i];
		}
		
		strButOK = TXT[13][5];
		strButNewLevelImprove = TXT[13][2];
	}
	
	private String listHelpTxt;
	
	// ---------------------------- LEVEL UP --------------------------------//
	private int level_up[][];
	private int LU_STAT_POINTS = 0;
	private int LU_EXP_TO_LEVEL = 1;

	private void loadLevelUp() {
		String matrix[][] = loadStringMatrix("level_up.txt", "\t");
		int a_row = matrix.length;
		level_up = new int[a_row+1][2];
		for (int i = 0; i < a_row; i++) {
			int a = matrix[i].length;
			for(int j = 0; j < a; j++){
				level_up[i+1][j] = Integer.parseInt(matrix[i][j]);
			}
		}
	}
	
	//----------------------------- MONSTER -------------------------------//
	private static int aMonsters;
	private int monsterParam[][];
	private final int MP_LEVEL = 0;
	private final int MP_HEALTH = 1;
	private final int MP_EXTRA_DAMAGE = 2;
	private final int MP_FIRE = 3;
	private final int MP_WATER = 4;
	private final int MP_AIR = 5;
	private final int MP_EARTH = 6;
	private final int MP_SKILL = 7;
	
	private String monsterText[][];
	
	private void loadMonsterData(){
		monsterText = loadStringMatrix("monster_text.txt", "\t");
		String matrix[][] = loadStringMatrix("monster_param.txt", "\t");
		monsterParam = convertStringMatrixToIntMatrix(matrix);
		aMonsters = monsterParam.length;
	}
	
	public static int aAchievments;
	private int achievmentParam[][];
	private final int AP_ID = 0;
	private final int AP_REQUIREMENT = 1;
	private final int AP_EXP = 2;
	private final int AP_MONEY = 3;
	private final int AP_CASH = 4;
	private final int aAP = 5;
	
	private String achievmentText[][];
	private final int AT_TITLE = 0;
	private final int AT_DESCRIPTION = 1;
	
	private void loadAchievmentData(){
		achievmentText = loadStringMatrix("achievment_text.txt", "\t");
		String matrix[][] = loadStringMatrix("achievment_param.txt", "\t");
		achievmentParam = convertStringMatrixToIntMatrix(matrix);
		aAchievments = achievmentParam.length;
	}
	
	// ---------------------------- SKILLS --------------------------------//
	
	private int skillWindowMap[][];
	
	private void loadHeroSkillsData(){
		String m_text[][] = loadStringMatrix("hero_skill_text.txt", "\t");
		String matrix[][] = loadStringMatrix("hero_skill_param.txt", "\t");
		int m_param[][] = convertStringMatrixToIntMatrix(matrix);
		matrix = loadStringMatrix("hero_skill_demand.txt", "\t");
		int m_demand[][] = convertStringMatrixToIntMatrix(matrix);
		matrix = loadStringMatrix("hero_skill_map.txt", "\t");
		skillWindowMap = convertStringMatrixToIntMatrix(matrix);
		matrix = null;
		
		int kind = Pers.HERO;
		Skills.createSkills(kind, m_text.length);
		int id;
		for(int i = 0; i < Skills.maxSkills[kind]; i++){
			Skills.skill[kind][i] = new Skill(m_text[i][Skills.TEXT_TITLE], m_text[i][Skills.TEXT_DESCRIPTION]);
			id = i+1;
			Skills.skill[kind][i].id = id;
			Skills.skill[kind][i].min_pers_level = m_demand[i][Skills.DEMAND_MIN_PERS_LEVEL];
			System.arraycopy(m_demand[i], 0, Skills.skill[kind][i].demand, 0, Skills.skill[kind][i].a_demand);
			int res[][] = databaseSelect(m_param, Skill.SP_ID, id);
			res = databaseSort(res, Skill.SP_LEVEL, 1);
			Skills.skill[kind][i].initParam(res);
		}
	}
	
	private int[][] databaseSelect(int data[][], int ind_column, int value){
		int res[][] = null;
		if(data == null) return null;
		int a = data.length;
		int aa = data[0].length;
		int arr[] = new int[a];
		int count = 0;
		for(int i = 0; i < a; i++){
			if(data[i][ind_column] == value){
				arr[count++] = i;
			}
		}
		res = new int[count][aa];
		for(int i = 0; i < count; i++){
			System.arraycopy(data[arr[i]], 0, res[i], 0, aa);
		}
		return res;
	}
	
	private int[][] databaseSort(int data[][], int ind_column, int dir){
		int a = data[0].length;
		int t[] = new int[a];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length - i - 1; j++) {
				if(dir > 0 && data[j][ind_column] > data[j + 1][ind_column] ||
				   dir < 0 && data[j][ind_column] < data[j + 1][ind_column]){
					System.arraycopy(data[j], 0, t, 0, a);
					System.arraycopy(data[j + 1], 0, data[j], 0, a);
					System.arraycopy(t, 0, data[j + 1], 0, a);
				}
			}
		}
		return data;
	}
	
	private void loadOutfitData(){
		loadOutfitData(1, "weapon");
		loadOutfitData(2, "armor");
		loadOutfitData(3, "hat");
		loadOutfitData(4, "other");
	}
	
	public static OutfitData outfitData[];
	
	private void loadOutfitData(int id, String type){
		String m_text[][] = loadStringMatrix(type+"_text.txt", "\t");
		String matrix[][] = loadStringMatrix(type+"_param.txt", "\t");
		int m_param[][] = convertStringMatrixToIntMatrix(matrix);
		int count = OutfitData.AMOUNT;
		OutfitData.AMOUNT += m_text.length;
		if(outfitData == null){
			outfitData = new OutfitData[OutfitData.AMOUNT];
		}
		else{
			OutfitData tmp[] = new OutfitData[OutfitData.AMOUNT];
			System.arraycopy(outfitData, 0, tmp, 0, outfitData.length);
			outfitData = tmp;
		}
		for(int i = 0; i < m_text.length; i++){
			outfitData[count+i] = new OutfitData();
			outfitData[count+i].id = id;
			outfitData[count+i].name = m_text[i][0];
			outfitData[count+i].level = m_param[i][0];
			outfitData[count+i].strength = m_param[i][1];
			outfitData[count+i].price_repair = m_param[i][2];
			outfitData[count+i].price_buy = m_param[i][3];
			outfitData[count+i].price_sale = m_param[i][4];
			switch(id){
			case 1:
				outfitData[count+i].damage = m_param[i][5];
				outfitData[count+i].power = m_param[i][6];
				break;
			case 2:
				outfitData[count+i].health = m_param[i][5];
				break;
			case 3:
				outfitData[count+i].brave = m_param[i][5];
				outfitData[count+i].tactic = m_param[i][6];
				break;
			case 4:
				outfitData[count+i].fire = m_param[i][5];
				outfitData[count+i].water = m_param[i][6];
				outfitData[count+i].air = m_param[i][7];
				outfitData[count+i].earth = m_param[i][8];
				break;
			}
			
		}
	}
	
	private void loadMonsterSkillsData(){
		String m_text[][] = loadStringMatrix("monster_skill_text.txt", "\t");
		String matrix[][] = loadStringMatrix("monster_skill_param.txt", "\t");
		int m_param[][] = convertStringMatrixToIntMatrix(matrix);
		matrix = null;
		
		int kind = Pers.MONSTER;
		Skills.createSkills(kind, m_text.length);
		int id;
		for(int i = 0; i < Skills.maxSkills[kind]; i++){
			Skills.skill[kind][i] = new Skill(m_text[i][Skills.TEXT_TITLE], m_text[i][Skills.TEXT_DESCRIPTION]);
			id = i+1;
			Skills.skill[kind][i].id = id;
			int res[][] = new int[1][Skill.aSP];
			int lev = 0;
			res[lev][Skill.SP_FIRE] = m_param[i][0];
			res[lev][Skill.SP_WATER] = m_param[i][1];
			res[lev][Skill.SP_AIR] = m_param[i][2];
			res[lev][Skill.SP_EARTH] = m_param[i][3];
			Skills.skill[kind][i].initParam(res);
		}
	}
		
	private String[][] loadStringMatrix(String file_name, String symbol){
		String s[] = assets.loadStringRowsFromFile(file_name);
		int a_row = s.length;
		String res[][] = new String[a_row][];
		for(int i = 0; i < a_row; i++){
			if(s[i] == null || s[i].length() == 0) continue;
			res[i] = s[i].split(symbol);
		}
		return res;
	}
	
	private int[][] convertStringMatrixToIntMatrix(String matrix[][]){
		int a = matrix.length;
		int res[][] = new int[a][];
		for(int i = 0; i < a; i++){
			int aa = matrix[i].length;
			res[i] = new int[aa];
			for(int j = 0; j < aa; j++){
				res[i][j] = Integer.parseInt(matrix[i][j]);
			}
		}
		return res;
	}

	//-------------------------- SPLASH ------------------------------------
	/*
	private final void runSplash(){
		paintItem = I_SPLASH;
		loadSplash();
		setGameTitleDY();
	}
	
	private Image splashImg;
	private Image gameTitleImg;
	private int gameTitleH;
	private int gameTitlePadd;
	private float gameTitleDY;
	private boolean gameTitleInit;
	
	private void setGameTitleDY(){
		gameTitleDY = gameTitleH + gameTitlePadd;
		gameTitleInit = true;
	}
	
	private void mathGameTitle(){
		if(gameTitleInit){
			gameTitleInit = false;
			return;
		}
		float speed = gameTitleH << 1;
		float dt = getDTime();
		float dx = speed * dt;
		if(gameTitleDY > 0){
			gameTitleDY -= dx;
			if(gameTitleDY < 0) gameTitleDY = 0;
		}
	}
	
	private boolean isGameTitleShowed(){
		return gameTitleDY == 0;
	}
	
	private void loadSplash() {
		try {
			try {
				splashImg = Image.createImage("splash");
				gameTitleImg = Image.createImage("game_title");
			} catch (Exception e) {}
		} catch (OutOfMemoryError ee) {}
		if(gameTitleImg != null){
			gameTitleH = gameTitleImg.getHeight();
			gameTitlePadd = gameTitleH >> 2;
		}
	}
	
	private void mathSplash(){
		mathGameTitle();
	}
	
	private void drawSplash(Graphics g){
		if(splashImg == null) return;
		g.drawImage(splashImg, 0, 0, width, height);
		float x = width >> 1;
		float y = height - gameTitleH - gameTitlePadd + gameTitleDY;
		g.drawImage(gameTitleImg, x, y, Uni.HCENTER_TOP);
		//g.draw(splashImg, 0, 0, width, height);
	}
	
	private void disposeSplash(){
		if(splashImg != null) splashImg.dispose();
		splashImg = null;
		if(gameTitleImg != null) gameTitleImg.dispose();
		gameTitleImg = null;
	}
	*/
	// ------------------------- MENU MAIN---------------------------------------

	private final int MENU_MAIN_ITEM_PLAY = 0;
	private final int MENU_MAIN_ITEM_HELP = 1;
	private final int MENU_MAIN_ITEM_MUSIC = 2;
	private final int MENU_MAIN_ITEM_SOUND = 3;
	private final int MENU_MAIN_ITEM_RESET_GAME = 4;
	private final int MENU_MAIN_ITEM_EXIT = 5;
	private final int MENU_MAIN_ITEM_ARENA = 6;
	private final int MENU_MAIN_ITEM_ACHIEVEMENTS = 7;
	private final int menuItems[] = {
		MENU_MAIN_ITEM_PLAY,
		MENU_MAIN_ITEM_ARENA,
		MENU_MAIN_ITEM_ACHIEVEMENTS,
		//MENU_MAIN_ITEM_HELP,
		MENU_MAIN_ITEM_EXIT
	};
	private final int menuIcons[] = {
			MENU_MAIN_ITEM_MUSIC,
			MENU_MAIN_ITEM_SOUND,
			MENU_MAIN_ITEM_RESET_GAME
	};
	private final int aMainMenuItems = menuItems.length;//2;
	private final int aMainMenuIcons = menuIcons.length;
	private final int aAllMenuItems = aMainMenuItems + aMainMenuIcons;
	private int cursorMenuMain;
	private boolean cursorMenuMainPressed;
	private String menuItemsTitle[];// = {"Play", "Sound"};
	private int menuItemsW;
	private int menuItemsH;
	private int menuItemsDH;
	private Rectangle rectMenuItems[];
	
	private int getMenuMainIndex(int rect_item){
		if(rect_item >= aMainMenuItems){
			rect_item -= aMainMenuItems;
			return menuIcons[rect_item];
		}
		else{
			return menuItems[rect_item];
		}
	}
	
	private boolean bShowResetGameButton;
	
	private boolean isGameStarted(){
		for(int j = 0; j < maxLevel; j++){
			if(bPlayedLevel[j]) return true;
		}
		return false;
	}
	
	private void runMainMenu() {
		paintItem = I_MENU_MAIN;
		loadBackImage();
		audio.playMusic(Audio.MSC_MAIN_MENU, true);
		boolean b_game_started = isGameStarted();
		bShowResetGameButton = b_game_started;
		bTutorial = !b_game_started;
		initMainMenu();
		initButFacebook();
		cursorMenuMain = 0;
		cursorMenuMainPressed = false;
	}

	private void initMainMenu() {
		if (menuItemsTitle != null)
			return;
		
		menuItemsTitle = new String[aMainMenuItems];
		rectMenuItems = new Rectangle[aAllMenuItems];
		int fontH = font.getHeight() * 2;
		int witem = 0;
		int wi;
		for (int i = 0; i < aMainMenuItems; i++) {
			int j = menuItems[i];
			menuItemsTitle[i] = TXT[I_MENU_MAIN][j];
			wi = font.stringWidth(menuItemsTitle[i]);
			if (witem < wi)
				witem = wi;
		}
		menuItemsW = witem + (fontH * 2);
		menuItemsH = fontH + (fontH / 4);
		if (menuItemsW < mItemW)
			menuItemsW = mItemW;
		if (menuItemsH < mItemH)
			menuItemsH = mItemH;
		menuItemsDH = mItemDH;
		float h_all = (menuItemsH + menuItemsDH) * aMainMenuItems - menuItemsDH;
		float y = (height - h_all) / 2;
		float x = width - menuItemsW >> 1;
		for (int i = 0; i < aMainMenuItems; i++) {
			rectMenuItems[i] = new Rectangle(x, y, menuItemsW, menuItemsH);
			y += menuItemsH + menuItemsDH;
		}
		x = butPadd;
		y = butPadd;
		for(int i = aMainMenuItems; i < aAllMenuItems; i++){
			rectMenuItems[i] = new Rectangle(x, y, butMainMenuMiniW, butMainMenuMiniH);
			y += butPadd + butMainMenuMiniH;
			int index = getMenuMainIndex(i);
			if(index == MENU_MAIN_ITEM_RESET_GAME){
				rectMenuItems[i].x = width - butPadd - butMainMenuMiniW;
				rectMenuItems[i].y = butPadd;
			}
		}
		
	}

	private void mathMainMenu() {

	}

	private void drawMainMenu(Graphics g) {
		drawBackImage(g);
		float x, y;
		TextureRegion tr;
		boolean b_active;
		for (int i = 0; i < rectMenuItems.length; i++) {
			int index = getMenuMainIndex(i);
			if(index == MENU_MAIN_ITEM_RESET_GAME && !bShowResetGameButton) continue;
			x = rectMenuItems[i].x;
			y = rectMenuItems[i].y;
			if(i < aMainMenuItems){
				b_active = (cursorMenuMainPressed || bKeyboard) && i == cursorMenuMain;
				tr = b_active ? trMenuItem0 : trMenuItem1;
				if(tr != null && tr.getTexture() != null){
					g.draw(tr, x, y, rectMenuItems[i].width, rectMenuItems[i].height);
				}
				else{
					g.fillRect(b_active ? clrMenuItemActive : clrMenuItemPassive, x, y, rectMenuItems[i].width, rectMenuItems[i].height);
				}
				x += (rectMenuItems[i].width / 2);
				y += (rectMenuItems[i].height / 2);
				font.drawString(g, menuItemsTitle[i], clrFontWhite, x, y, Uni.HCENTER_VCENTER);
				if(index == MENU_MAIN_ITEM_ARENA){
					x = rectMenuItems[i].x + rectMenuItems[i].width - trMenuLock.getRegionWidth();
					y = rectMenuItems[i].y + rectMenuItems[i].height - trMenuLock.getRegionHeight();
					g.draw(trMenuLock, x, y);
				}
			}
			else{
				switch(index){
				case MENU_MAIN_ITEM_MUSIC:
					tr = Audio.bMusic ? trMusicOn : trMusicOff;
					break;
				case MENU_MAIN_ITEM_SOUND:
					tr = Audio.bSound ? trSoundOn : trSoundOff;
					break;
				case MENU_MAIN_ITEM_RESET_GAME:
					tr = trResRestart;
					break;
				default:
					tr = null;
					break;
				}
				if(tr != null) g.draw(tr, x, y);
			}
		}

		drawFacebookLogButton(g);
	}
	
	//---------------------------- MENU PAUSE ----------------------
	
	private final int MENU_PAUSE_ITEM_CONTINUE = 0;
	private final int MENU_PAUSE_ITEM_RESTART = 1;
	private final int MENU_PAUSE_ITEM_SELECT_LEVEL = 2;
	private final int MENU_PAUSE_ITEM_MENU = 3;
	private final int aPauseMenuItems = 4;
	private int cursorPauseMenu;
	private int menuPauseItemsW;
	private int menuPauseItemsH;
	private int menuPauseDW;
	private int menuPauseDH;
	private int menuPauseX;
	private int menuPauseY;
	private int menuPauseW;
	private int menuPauseH;
	private int menuPausePadd;
	private Rectangle rectPauseMenuItems[];
	private int menuPauseTitleH;
	private float menuPauseAppearTime;
	private float menuPauseAppearTimeMax = 0.6f;
	
	private void runPauseMenu() {
		if(bTutorial) return;
		paintItem = I_MENU_PAUSE;
		cursorPauseMenu = -1;
		menuPauseAppearTime = menuPauseAppearTimeMax;
		initPauseMenu();
	}

	private final void initPauseMenu() {
		if(rectPauseMenuItems != null) return;
		rectPauseMenuItems = new Rectangle[aPauseMenuItems];
		menuPauseItemsW = resButtonW;
		menuPauseItemsH = resButtonH;
		menuPauseDW = menuPauseDH = menuPauseItemsW >> 1;
		menuPausePadd = menuPauseItemsW;
		int fontH = font.getHeight();
		menuPauseTitleH = fontH;
		int ax = aPauseMenuItems;
		int ay = 1;
		menuPauseW = (menuPauseItemsW + menuPauseDW) * ax - menuPauseDW + (menuPausePadd << 1);
		menuPauseH = (menuPauseItemsH + menuPauseDH) * ay - menuPauseDH + (menuPausePadd << 1);
		menuPauseH += menuPausePadd + menuPauseTitleH;
		menuPauseX = MAP_CENTER_X - (menuPauseW >> 1);
		menuPauseY = MAP_CENTER_Y - (menuPauseH >> 1);
		int count = 0;
		int yy = menuPauseY + (menuPausePadd << 1) + menuPauseTitleH;
		for(int y = 0; y < ay; y++){
			int xx = menuPauseX + menuPausePadd;
			for(int x = 0; x < ax; x++){
				rectPauseMenuItems[count++] = new Rectangle(xx, yy, menuPauseItemsW, menuPauseItemsH);
				xx += menuPauseItemsW + menuPauseDW;
			}
			yy += menuPauseItemsH + menuPauseDH;
		}
	}

	private void mathPauseMenu() {
		float dt = getDTime();
		if(menuPauseAppearTime > 0){
			menuPauseAppearTime -= dt;
		}
	}

	private void drawPauseMenu(Graphics g) {
		drawGame(g);
		drawPauseMenuItems(g);
	}
	
	private void drawPauseMenuItems(Graphics g){
		Color clr = g.getColor();
		Color clrf = clrFontBrown;//font.getColor();
		Color clrf_n = clrf.cpy();
		drawNinePatch(g, npSubstrate, clrSubstrate, menuPauseX, menuPauseY, menuPauseW, menuPauseH);
		if(menuPauseAppearTime > 0){
			float a = (menuPauseAppearTimeMax - menuPauseAppearTime) / menuPauseAppearTimeMax;
			g.setColor(clr.r, clr.g, clr.b, a);
			clrf_n.a = a;
		}
		
		TextureRegion tr=null;
		float x, y;
		x = menuPauseX + (menuPauseW >> 1);
		y = menuPauseY + menuPausePadd;
		font.drawString(g, TXT[I_MENU_PAUSE][0], clrf_n, x, y, Uni.HCENTER_TOP);
		for(int i = 0; i < aPauseMenuItems; i++){
			switch(i){
			case MENU_PAUSE_ITEM_CONTINUE:
				tr = trResNext;
				break;
			case MENU_PAUSE_ITEM_RESTART:
				tr = trResRestart;
				break;
			case MENU_PAUSE_ITEM_SELECT_LEVEL:
				tr = trResLevel;
				break;
			case MENU_PAUSE_ITEM_MENU:
				tr = trResMenu;
				break;
			}
			g.draw(tr, rectPauseMenuItems[i].x, rectPauseMenuItems[i].y);
		}
		if(menuPauseAppearTime > 0){
			g.setColor(clr);
			font.setColor(clrf);
		}
	}
	
	//-------------------------- DIALOG ------------------------------------
	private String dialogTxt;
	private GlyphLayout dialogGL;
	//private TextBounds dialogTxtBounds;
	private int dialogWrapWidth;
	private int dialogX;
	private int dialogY;
	private int dialogW;
	private int dialogH;
	private int dialogPadd;
	private Rectangle rectDialogButton[];
	private final byte DIALOG_BUTTON_OK = 0;
	private final byte DIALOG_BUTTON_CANCEL = 1;
	private final byte aDialogButton = 2;
	private int dialogType;
	private final int DIALOG_TYPE_RESET_GAME = 0;
	private final int DIALOG_TYPE_EXIT_GAME = 1;
	
	private void runDialog(boolean back, int type){
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_DIALOG;
		initDialog(type);
	}
	
	private void initDialog(int type){
		dialogType = type;
		dialogTxt = TXT[I_DIALOG][2 + dialogType];
		int D = width < height ? width : height;
		dialogW = D - (D >> 3);
		int fontH = font.getHeight();
		dialogPadd = fontH << 1;
		dialogWrapWidth = dialogW - (dialogPadd << 1);
		dialogGL = font.getGlyphLayout(dialogTxt, dialogWrapWidth);
		//dialogTxtBounds = font.getWrappedBounds(dialogTxt, dialogWrapWidth);
		dialogH = (int) (dialogGL.height);
		//dialogH = (int) (dialogTxtBounds.height);
		dialogH += (dialogButtonH) + (dialogPadd << 1) + butPadd;
		//if(dialogH < (D >> 1)) dialogH = (D >> 1);
		dialogX = width2 - (dialogW >> 1);
		dialogY = height2 - (dialogH >> 1);
		rectDialogButton = new Rectangle[aDialogButton];
		float x, y;
		x = dialogX + (dialogW >> 2) - (dialogButtonW >> 1);
		y = dialogY + dialogH - dialogButtonH - butPadd;
		rectDialogButton[DIALOG_BUTTON_OK] = new Rectangle(x, y, dialogButtonW, dialogButtonH);
		x = dialogX + dialogW - (dialogW >> 2) - (dialogButtonW >> 1);
		rectDialogButton[DIALOG_BUTTON_CANCEL] = new Rectangle(x, y, dialogButtonW, dialogButtonH);
	}
		
	private void mathDialog(){
		if(getPaintItemPrev() == I_MENU_MAIN){

		}
	}
	
	private void drawDialog(Graphics g){
		switch(getPaintItemPrev()){
		case I_MENU_MAIN:
			drawBackImage(g);
			break;
		}
		drawNinePatch(g, npSubstrate, clrSubstrate, dialogX, dialogY, dialogW, dialogH);
		TextureRegion tr;
		float x = dialogX + dialogPadd;//(dialogW >> 1) - (dialogGL.width / 2);
		float y = dialogY + dialogPadd;
		font.drawWrapped(g, dialogTxt, clrFontBrown, x, y, dialogWrapWidth, 1);
		for(int i = 0; i < aDialogButton; i++){
			x = rectDialogButton[i].x;
			y = rectDialogButton[i].y;
			tr = i == DIALOG_BUTTON_OK ? trDialogOK : trDialogCancel;
			g.draw(tr, x, y);
		}
	}
	
	private void resetGameProgress(){
		bShowResetGameButton = false;
		bTutorial = true;
		for(int j = 0; j < maxLevel; j++){
			recordStep[j] = recordStar[j] = 0;
			bPlayedLevel[j] = false;
			bDoneLevel[j] = false;
		}
		initHeroFirst();
	}
	
	private void disposeDialog(){
		
	}
	
	//---------------------------- LIST ----------------------------
	
	private void runList(){
		paintItem = I_LIST;
		initList();
	}
	
	private void initList(){
		int x = width - butBackW - butPadd;
		int y = height - butPadd - butBackH;
		rectButBack = new Rectangle(x, y, butBackW, butBackH);
	}
	
	private void mathList(){

	}
	
	private Rectangle rectButBack;
	
	private void drawList(Graphics g){
		drawBackImage(g);
		drawListText(g);
		drawButtonBack(g, rectButBack.x, rectButBack.y);
	}
	
	private void drawListText(Graphics g){
		int paddTxt = font.getHeight() << 1;
		int w_text = width - (butBackW << 1) - (butPadd << 2) - (paddTxt << 1);
		int h_text = (int) font.getGlyphLayout(listHelpTxt, w_text).height;
		int x = width - w_text >> 1;
		int h = height - butBackH - (butPadd << 1);
		int y = h - h_text >> 1;
		drawNinePatch(g, npSubstrate, clrSubstrate, x - paddTxt, y - paddTxt, w_text + (paddTxt << 1), h_text + (paddTxt << 1));
		font.drawWrapped(g, listHelpTxt, clrFontBrown, x, y, w_text, 1);
	}
	
	// ---------------------------- PERS ----------------------------
	
	private void runPers(boolean back) {
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_PERS;
		initPers();
	}
	
	private int persInfoW;

	private void initPers() {
		float x = width - butBackW - butPadd;
		float y = height - butPadd - butBackH;
		rectButBack.setPosition(x, y);
		outfitBagSelected = -1;
		int fontH = font.getHeight();
		int d = fontH >> 2;
		persInfoW = width2;
		initHealthBar((persInfoW >> 1) - (d << 1), fontH + (fontH >> 1));
		initRectButSkills();
		
		int sh = fontH << 1;
		int sw_1 = font.stringWidth(strButNewLevelImprove) + (fontH << 1);
		x = (persInfoW >> 1) + d;
		y = height - butBackH - (butPadd << 1) - sh;
		rButNewLevelImprove.setSize(sw_1, sh);
		rButNewLevelImprove.setPosition(x, y);
	}

	private void mathPers() {
		hero.mathHudHealthW(hudHealthW);
	}

	private void drawPers(Graphics g) {
		drawBackImage(g);
		drawPersInfo(g);
		drawBagWindow(g, rectWinBag.x, rectWinBag.y, rectWinBag.width, rectWinBag.height);
		drawBodyWindow(g, rectWinBody.x, rectWinBody.y, rectWinBody.width, rectWinBody.height);
		drawButtonBack(g);
		drawBagOutfitSelected(g);
	}
	
	static Rectangle rectWinBody = new Rectangle();
	public static final int aBodyPart = 4;
	public static Rectangle rBodyPart[];
	
	private void drawBodyWindow(Graphics g, float x, float y, float w, float h){
		g.fillRect(Color.LIGHT_GRAY, x, y, w, h);
		int fontH = font.getHeight();
		int padd = fontH;
		String s = TXT[10][4];
		x += padd;
		y += (winShopTitleH >> 1);
		font.drawString(g, s, clrFontWhite, x, y, Uni.LEFT_VCENTER);
		float X, Y, W, H;
		int d = winShopPadd >> 2;
		for(int i = 0; i < aBodyPart; i++){
			X = rBodyPart[i].x;
			Y = rBodyPart[i].y;
			W = rBodyPart[i].width;
			H = rBodyPart[i].height;
			g.fillRect(Color.CORAL, X - d, Y - d, W + (d << 1), H + (d << 1));
			if(hero.bodyIndex[i] >= 0){
				Outfit o = hero.bagItems[hero.bodyIndex[i]];
				OutfitData od = outfitData[o.index];
				g.fillRect(Color.WHITE, X, Y, W, H);
				font_mini.drawString(g, od.name, clrFontBrown, X + (W / 2), Y + (H / 2), Uni.HCENTER_VCENTER);
			}
		}
	}
	
	private void drawPersInfo(Graphics g) {
		int i_str = 12;
		int fontH = font.getHeight();
		int fontH_mini = font_mini.getHeight();
		
		int nickH = fontH + (fontH >> 1);
		int avatarH = (fontH << 2) + (fontH << 1);
		float x = persInfoW >> 2;
		float y = (nickH >> 1);
		font.drawString(g, hero.nick, clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		y += (nickH >> 1) + (avatarH >> 1);
		font.drawString(g, "AVATAR", clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		y += (avatarH >> 1);
		int d = itemSkillPadd;
		x = d;
		int w = (persInfoW >> 1) - (d << 1);
		int h_health = hudHealthH + d;
		y = rButSkills.y - h_health;
		font.drawString(g, TXT[12][9] + ": " + hero.getHealthMax(true), clrFontBrown, x, y + (hudHealthH >> 1), Uni.LEFT_VCENTER);
		x = itemSkillPadd;
		y = rButSkills.y + itemSkillPadd;
		y = drawSkills(g, hero, x, y, w, itemSkillH, itemSkillPadd, false);
		
		x = (persInfoW >> 1) + d;
		y = nickH >> 1;
		int dx = fontH_mini >> 1;
		int dy = fontH_mini << 2;
		String t[] = TXT[i_str];
		drawPersInfoParam(g, t[Pers.PERSI_LEVEL], Integer.toString(hero.getLevel()), font_mini, x, y, dx);
		y += dy;
		drawPersInfoParam(g, t[Pers.PERSI_EXP], hero.getExp() + "/" + level_up[hero.getLevel()][LU_EXP_TO_LEVEL], font_mini, x, y, dx);
		y += dy;
		drawPersInfoCommon(g, x, y, dx, dy, false);
		
		//drawButtonText(g, strButSkills, rButSkills, false);
		if(hero.getStatFree() > 0){
			drawButtonText(g, strButNewLevelImprove, rButNewLevelImprove, false);
		}
	}
	
	private final int persInfoCommon[] = {
		Pers.PERSI_POWER,
		Pers.PERSI_TACTIC,
		Pers.PERSI_BRAVE,
		Pers.PERSI_FIRE,
		Pers.PERSI_WATER,
		Pers.PERSI_AIR,
		Pers.PERSI_EARTH
	};
	
	private final int aPersInfoCommon = persInfoCommon.length;
	private Rectangle rButImproveStat[];
	private int cursorPersInfoCommon;
	
	private void drawPersInfoCommon(Graphics g, float x, float y, int dx, int dy, boolean b_with_buttons){
		String t[] = TXT[12];
		int index;
		for(int i = 0; i < aPersInfoCommon; i++, y += dy){
			index = persInfoCommon[i];
			drawPersInfoParam(g, t[index], Integer.toString(hero.getStat(index)), font_mini, x, y, dx);
			if(b_with_buttons){
				drawButtonText(g, "+", rButImproveStat[i], i == cursorPersInfoCommon);
			}
		}
	}
	
	private void drawPersInfoParam(Graphics g, String s, String param, MyFont f, float x, float y, int d){
		f.drawString(g, s, clrFontBrown, x, y, Uni.LEFT_VCENTER);
		x += f.stringWidth(s) + d;
		f.drawString(g, param, clrFontBrown, x, y, Uni.LEFT_VCENTER);
	}
	
	private int getPersInfoParamW(String s, String param, MyFont f, int d){
		return f.stringWidth(s) + d + f.stringWidth(param);
	}
	
	private void addPersStat(int type){
		if(hero.getStatFree() <= 0) return;
		hero.setStatFree(hero.getStatFree() - 1);
		switch (type) {
		case Pers.PERSI_POWER:
			hero.setPower(hero.getPower(false) + 1);
			break;
		case Pers.PERSI_TACTIC:
			hero.setTactic(hero.getTactic(false) + 1);
			break;
		case Pers.PERSI_BRAVE:
			hero.setBrave(hero.getBrave(false) + 1);
			break;
		case Pers.PERSI_FIRE:
			hero.setFire(hero.getFire(false) + 1);
			break;
		case Pers.PERSI_WATER:
			hero.setWater(hero.getWater(false) + 1);
			break;
		case Pers.PERSI_AIR:
			hero.setAir(hero.getAir(false) + 1);
			break;
		case Pers.PERSI_EARTH:
			hero.setEarth(hero.getEarth(false) + 1);
			break;
		}
	}
	
	private Pers hero;
	private Pers enemy;
	
	private Rectangle rButSkills = new Rectangle();

	private void initRectButSkills(){
		int w = (persInfoW >> 1);//font.stringWidth(strButSkills) + fontH;
		int h = Pers.maxSkillsGameBox * (itemSkillH + (itemSkillPadd << 1));//fontH << 1;
		rButSkills.setSize(w, h);
		rButSkills.setPosition(0, height - h);//(butPadd, height - butBackH - (butPadd << 1) - h);
	}
	
	// ---------------------------- SHOP ----------------------------
	
	private void runShop(boolean back) {
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_SHOP;
		initShop();
	}

	private void initShop() {
		int x = width - butBackW - butPadd;
		int y = height - butPadd - butBackH;
		rectButBack.setPosition(x, y);
		
		outfitShopSelected = -1;
		outfitBagSelected = -1;
		initShopItemsRect();
	}

	private void mathShop() {

	}

	private void drawShop(Graphics g) {
		drawBackImage(g);
		drawShopWindow(g);
		drawBagWindow(g, rectWinBag.x, rectWinBag.y, rectWinBag.width, rectWinBag.height);
		drawButtonBack(g);
		drawOutfitSelected(g);
	}
	
	static Rectangle rectWinShop = new Rectangle();
	static int winShopTitleH;
	static int winShopPadd;
	static int itemOutfitW, itemOutfitH;
	static int itemOutfitDW;
	
	private int[] getAvailableOutfit(int pers_level){
		int arr[] = new int[OutfitData.AMOUNT];
		int count = 0;
		for(int i = 0; i < OutfitData.AMOUNT; i++){
			if(outfitData[i].level <= pers_level) arr[count++] = i;
		}
		int res[] = new int[count];
		System.arraycopy(arr, 0, res, 0, count);
		return res;
	}
	
	private static Rectangle rShopItems[];
	private int aShopItems;
	private int shopItems[];
	
	private void initShopItemsRect(){
		shopItems = getAvailableOutfit(hero.getLevel());
		aShopItems = shopItems.length;
		rShopItems = new Rectangle[aShopItems];
		float x = rectWinShop.x + winShopPadd;
		float y = rectWinShop.y + winShopTitleH;
		int cw = itemOutfitW;
		int ch = itemOutfitH;
		for(int i = 0; i < aShopItems; i++){
			rShopItems[i] = new Rectangle(x, y, cw, ch);
			x += cw + itemOutfitDW;
			if(x >= rectWinShop.x + rectWinShop.width - winShopPadd){
				y += ch + itemOutfitDW;
				x = rectWinShop.x + winShopPadd;
			}
		}
	}
	
	private void drawShopWindow(Graphics g) {
		int fontH = font.getHeight();
		g.fillRect(clrFontBrown, rectWinShop);
		font.drawString(g, TXT[10][1], clrFontWhite, rectWinShop.x + (rectWinShop.width / 2), rectWinShop.y + (winShopTitleH >> 1), Uni.HCENTER_VCENTER);
		int price_h = fontH << 1;
		float yy;
		for(int i = 0; i < aShopItems; i++){
			OutfitData o = outfitData[shopItems[i]];
			g.fillRect(clrSubstrate, rShopItems[i]);
			font_mini.drawWrapped(g, o.name, clrFontBrown, rShopItems[i].x, rShopItems[i].y + (fontH), rShopItems[i].width, 1);
			//font_mini.drawString(g, o.name, clrFontBrown, rShopItems[i].x + (rShopItems[i].width / 2), rShopItems[i].y + (fontH), Uni.HCENTER_VCENTER);
			yy = rShopItems[i].y + rShopItems[i].height - price_h;
			g.fillRect(clrFontRed, rShopItems[i].x, yy, rShopItems[i].width, price_h);
			font.drawString(g, Integer.toString(o.price_buy), rShopItems[i].x + (rShopItems[i].width / 2), yy + (price_h >> 1), Uni.HCENTER_VCENTER);
		}
	}
	
	static Rectangle rectWinBag = new Rectangle();
	
	private void drawBagWindow(Graphics g, float x, float y, float w, float h) {
		g.setClip(x, y, w, h);
		g.fillRect(Color.DARK_GRAY, x, y, w, h);
		int fontH = font.getHeight();
		int padd = fontH;
		int icoMoneyW = trIcoMoney.getRegionWidth();
		int icoMoneyH = trIcoMoney.getRegionHeight();
		int icoCashW = trIcoCash.getRegionWidth();
		int icoCashH = trIcoCash.getRegionHeight();
		String s = TXT[10][3];
		float X = x + padd;
		float Y = y + (winShopTitleH >> 1);
		font.drawString(g, s, clrFontWhite, X, Y, Uni.LEFT_VCENTER);
		X += font.stringWidth(s) + (padd << 1);
		g.draw(trIcoMoney, X, Y - (icoMoneyH >> 1));
		X += icoMoneyW;
		s = Integer.toString(hero.getMoney());
		font_mini.drawString(g, s, clrFontWhite, X, Y, Uni.LEFT_VCENTER);
		X += font_mini.stringWidth(s) + (padd << 1);
		g.draw(trIcoCash, X, Y - (icoCashH >> 1));
		X += icoCashW;
		s = Integer.toString(hero.getCash());
		font_mini.drawString(g, s, clrFontWhite, X, Y, Uni.LEFT_VCENTER);
		int d = 2;
		float rx, ry, rw, rh;
		for(int i = 0; i < maxBagCells; i++){
			Outfit o = hero.bagItems[i];
			rx = rBagItems[i].x;
			ry = rBagItems[i].y;
			rw = rBagItems[i].width;
			rh = rBagItems[i].height;
			g.fillRect(Color.BLACK, rx - d, ry - d, rw + (d << 1), rh + (d << 1));
			if(o != null){
				OutfitData od = outfitData[o.index];
				g.fillRect(clrSubstrate, rBagItems[i]);
				GlyphLayout gl = font.getGlyphLayout(od.name, (int)rw);
				font_mini.drawWrapped(g, od.name, clrFontBrown, rx, ry + (rh - gl.height) / 2, rw, 1);
				//font_mini.drawString(g, od.name, clrFontBrown, rx + (rw / 2), ry + (rh / 2), Uni.HCENTER_VCENTER);
			}
			if(i >= hero.aOpenBagCells){
				g.draw(trMenuLock, rx + (rw / 2), ry + (rh / 2));
			}
			if(isBagOutfitWeared(i)){
				font.drawString(g, "V", Color.RED, rx + rw - fontH, ry + rh - fontH, Uni.RIGHT_BOTTOM);
			}
		}
		g.setClip(0, 0, width, height);
	}
		
	private Rectangle rWinOutfitSelected = new Rectangle();
	private Vector2 cWinOutfitSelected = new Vector2();
	private Rectangle rButWinOutfitBagSelected[];
	private final int aButWinOutfitBagSelected = 3;
	private final int BUT_OUTFIT_BAG_REPAIR = 0;
	private final int BUT_OUTFIT_BAG_SALE = 1;
	private final int BUT_OUTFIT_BAG_PUT_ON = 2;
	
	private Rectangle rButWinOutfitShopSelected[];
	private final int aButWinOutfitShopSelected = 2;
	private final int BUT_OUTFIT_SHOP_BUY = 0;
	private final int BUT_OUTFIT_SHOP_CANCEL = 1;
	private int cursorOutfitSelected;
	
	private int outfitShopSelected;
	
	private void drawOutfitSelected(Graphics g){
		drawShopOutfitSelected(g);
		drawBagOutfitSelected(g);
	}
	
	private void drawShopOutfitSelected(Graphics g){
		if(outfitShopSelected < 0) return;
		int fontH = font.getHeight();
		g.fillRect(clrBlackTransparent, 0, 0, width, height);
		g.fillRect(clrSubstrate, rWinOutfitSelected);
		OutfitData od = outfitData[outfitShopSelected];
		font.drawWrapped(g, od.name, clrFontBrown, rWinOutfitSelected.x, rWinOutfitSelected.y + fontH, rWinOutfitSelected.width, 1);
		float y = rButWinOutfitShopSelected[0].y - fontH;
		font.drawString(g, "money: "+od.price_buy, clrFontBrown, cWinOutfitSelected.x, y, Uni.HCENTER_VCENTER);
		for(int i = 0; i < aButWinOutfitShopSelected; i++){
			drawButtonText(g, TXT[18][i], rButWinOutfitShopSelected[i], i == cursorOutfitSelected);
		}
		drawButtonBack(g);
	}
	
	private int outfitBagSelected;
	
	private void drawBagOutfitSelected(Graphics g){
		if(outfitBagSelected < 0) return;
		int fontH = font.getHeight();
		int icoMoneyW = trIcoMoney.getRegionWidth();
		int icoMoneyH = trIcoMoney.getRegionHeight();
		Outfit o = hero.bagItems[outfitBagSelected];
		if(o == null) return;
		OutfitData od = outfitData[o.index];
		g.fillRect(clrBlackTransparent, 0, 0, width, height);
		g.fillRect(clrSubstrate, rWinOutfitSelected);
		
		float y = rWinOutfitSelected.y + fontH;
		font.drawString(g, od.name, clrFontBrown, rWinOutfitSelected.x + (rWinOutfitSelected.width / 2), y, Uni.HCENTER_VCENTER);
		y += fontH << 1;
		font.drawString(g, "stregth: "+o.getCurStrength()+"/"+od.strength, clrFontBrown, rWinOutfitSelected.x + (rWinOutfitSelected.width / 2), y, Uni.HCENTER_VCENTER);
		int i_str;
		String s, v = null;
		int sw = 0, w = 0;
		for(int i = 0; i < aButWinOutfitBagSelected; i++){
			i_str = i;
			if(i == BUT_OUTFIT_BAG_PUT_ON && isBagOutfitWeared(outfitBagSelected)){
				i_str = 3;
			}
			s = TXT[17][i_str];
			sw = font.stringWidth(s);
			w = sw;
			switch(i){
			case BUT_OUTFIT_BAG_REPAIR:
				v = Integer.toString(hero.getBagItemRepairPrice(outfitBagSelected));
				w += icoMoneyW + font.stringWidth(v);
				break;
			case BUT_OUTFIT_BAG_SALE:
				v = Integer.toString(hero.getBagItemSalePrice(outfitBagSelected));
				w += icoMoneyW + font.stringWidth(v);
				break;
			case BUT_OUTFIT_BAG_PUT_ON:
				v = null;
				break;
			}
			
			boolean b_active = false;
			Color clr = b_active ? clrMenuItemActive : clrMenuItemPassive;
			int d = 0;
			Rectangle rect = rButWinOutfitBagSelected[i];
			Color clr_fnt = clrFontWhite;
			g.fillRect(clr, rect.x - d, rect.y - d, rect.width + (d << 1), rect.height + (d << 1));
			float x = rect.x + (rect.width - w) / 2;
			font.drawString(g, s, clr_fnt, x, rect.y + (rect.height / 2), Uni.LEFT_VCENTER);
			if(v != null){
				x += sw;
				g.draw(trIcoMoney, x, rect.y + (rect.height - icoMoneyH) / 2);
				x += icoMoneyW;
				font.drawString(g, v, clr_fnt, x, rect.y + (rect.height / 2), Uni.LEFT_VCENTER);
			}
//			drawButtonText(g, s, rButWinOutfitBagSelected[i], i == cursorOutfitSelected);
		}
		drawButtonBack(g);
	}
	
	private boolean isBagOutfitWeared(int i_bag){
		for(int i = aBodyPart - 1; i >= 0; i--){
			if(hero.bodyIndex[i] == i_bag){
				return true;
			}
		}
		return false;
	}
	
	private void initOutfitRects(){
		int fontH = font.getHeight();
		int w = (int) (width - (3 * (float)width / 7));
		int h = height;// - butBackH - (butPadd << 1);
		rectWinShop.setSize(w, h);
		rectWinShop.setPosition(0, 0);
		winShopTitleH = fontH << 1;
		winShopPadd = fontH;
		
		itemOutfitW = width >> 3;
		itemOutfitH = h / 4;
		itemOutfitDW = fontH >> 1;
		
		int win_padd = (fontH >> 1);
		w = width - w - win_padd;
		rectWinBag.setSize(w, h);
		rectWinBag.setPosition(rectWinShop.x + rectWinShop.width + win_padd, 0);
		w = itemOutfitW + (winShopPadd << 1);
		rectWinBody.setSize(w, h);
		rectWinBody.setPosition(rectWinBag.x - rectWinBody.width, 0);
		
		
		//initRectShopOutfitDrawed();
		w = width / 3;
		h = (int) (rectWinShop.height - (fontH << 1));
		float x = (width - w) / 2;
		float y = rectWinShop.y + fontH;
		rWinOutfitSelected.setSize(w, h);
		rWinOutfitSelected.setPosition(x, y);
		rWinOutfitSelected.getCenter(cWinOutfitSelected);
		if(rButWinOutfitShopSelected == null){
			rButWinOutfitShopSelected = new Rectangle[aButWinOutfitShopSelected];
			for(int i = 0; i < aButWinOutfitShopSelected; i++) rButWinOutfitShopSelected[i] = new Rectangle();
		}
		if(rButWinOutfitBagSelected == null){
			rButWinOutfitBagSelected = new Rectangle[aButWinOutfitBagSelected];
			for(int i = 0; i < aButWinOutfitBagSelected; i++) rButWinOutfitBagSelected[i] = new Rectangle();
		}
		int butOutfitSelDX = fontH;
		int butOutfitSelDY = fontH >> 2;
		int butOutfitSelW = (int) (rWinOutfitSelected.width - (butOutfitSelDX << 1));
		int butOutfitSelH = fontH + (fontH);
		x = rWinOutfitSelected.x + butOutfitSelDX;
		y = rWinOutfitSelected.y + rWinOutfitSelected.height - butOutfitSelDY - butOutfitSelH;
		for(int i = aButWinOutfitShopSelected - 1; i >= 0; i--){
			rButWinOutfitShopSelected[i].setSize(butOutfitSelW, butOutfitSelH);
			rButWinOutfitShopSelected[i].setPosition(x, y);
			y -= butOutfitSelDY + butOutfitSelH;
		}
		y = rWinOutfitSelected.y + rWinOutfitSelected.height - butOutfitSelDY - butOutfitSelH;
		for(int i = aButWinOutfitBagSelected - 1; i >= 0; i--){
			rButWinOutfitBagSelected[i].setSize(butOutfitSelW, butOutfitSelH);
			rButWinOutfitBagSelected[i].setPosition(x, y);
			y -= butOutfitSelDY + butOutfitSelH;
		}
		
		initBagItemsRect();
		
		if(rBodyPart == null){
			 rBodyPart = new Rectangle[aBodyPart];
			 x = rectWinBody.x + winShopPadd;
			 y = rectWinBody.y + winShopTitleH + winShopPadd;
			 w = itemOutfitW;
			 h = (int) ((rectWinBody.height - winShopTitleH - winShopPadd * (aBodyPart + 1)) / aBodyPart);
			 for(int i = 0; i < aBodyPart; i++){
				 rBodyPart[i] = new Rectangle();
				 rBodyPart[i].setSize(w, h);
				 rBodyPart[i].setPosition(x, y);
				 y += h + winShopPadd;
			 }
			 
		}
	}
	
	public static Rectangle rBagItems[];
	public static int maxBagCells = 20;
	
	static void initBagItemsRect(){
		rBagItems = new Rectangle[maxBagCells];
		float x = GameScreen.rectWinBag.x + GameScreen.winShopPadd;
		float y = GameScreen.rectWinBag.y + GameScreen.winShopTitleH;
		int cw = GameScreen.itemOutfitW;
		int ch = GameScreen.itemOutfitH;
		for(int i = 0; i < maxBagCells; i++){
			rBagItems[i] = new Rectangle(x, y, cw, ch);
			x += cw + GameScreen.itemOutfitDW;
			if(x >= GameScreen.rectWinBag.x + GameScreen.rectWinBag.width - GameScreen.winShopPadd){
				y += ch + GameScreen.itemOutfitDW;
				x = GameScreen.rectWinBag.x + GameScreen.winShopPadd;
			}
		}
	}
	
	// ---------------------------- SKILLS ----------------------------
	
	//public static int maxSkills;
	private int skillSelected;
	private int skillDragging;
	private Vector2 skillDraggingPos = new Vector2();

	private void runSkills(boolean back) {
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_SKILLS;
		initSkills();
	}
	
	private static int itemSkillW, itemSkillH;
	private static int itemSkillPadd;
	
	private void initItemSkillSize(){
		int fontH = font.getHeight();
		itemSkillW = width / 5;
		itemSkillH = (fontH << 1) + (fontH << 1);
		itemSkillPadd = fontH >> 1;
	}
	
	private Rectangle rSkillsAll[];
	private int rSkillAllDW, rSkillAllDH;
	private Rectangle rWinSkillsHero = new Rectangle();
	private int winSkillsHeroTitlePadd;
	private int winSkillsHeroTitleH;
	private Rectangle rSkillsHero[];
	private int winSkillsAllW, winSkillsAllH;

	private void initSkills() {
		skillSelected = -1;
		skillDragging = -1;
		if(rSkillsAll == null){
			rSkillsAll = new Rectangle[Skills.maxSkills[Pers.HERO]];
			for(int i = 0; i < Skills.maxSkills[Pers.HERO]; i++) rSkillsAll[i] = new Rectangle();
		}
		if(rSkillsHero == null){
			rSkillsHero = new Rectangle[Pers.maxSkillsGameBox];
			for(int i = 0; i < Pers.maxSkillsGameBox; i++) rSkillsHero[i] = new Rectangle();
		}
		int x = width - butBackW - butPadd;
		int y = height - butPadd - butBackH;
		rectButBack.setPosition(x, y);
		
		int fontH = font.getHeight();
		
		int win_hero_w = width >> 2;
		rWinSkillsHero.setSize(win_hero_w, height);
		rWinSkillsHero.setPosition(0, 0);
		winSkillsHeroTitlePadd = (fontH >> 1);
		winSkillsHeroTitleH = fontH;
		int title_h = (winSkillsHeroTitlePadd << 1) + winSkillsHeroTitleH;
		int ah = Pers.maxSkillsGameBox;
		int dw = (int)((rWinSkillsHero.width / 2) - (itemSkillW >> 1));
		int dh = ((int)rWinSkillsHero.height - title_h - (ah * itemSkillH)) / (ah + 1);
		float xx = rWinSkillsHero.x + dw;
		float yy = rWinSkillsHero.y + title_h + dh;
		for(int i = 0; i < Pers.maxSkillsGameBox; i++, yy += itemSkillH + dh){
			rSkillsHero[i].setSize(itemSkillW, itemSkillH);
			rSkillsHero[i].setPosition(xx, yy);
		}
		
		winSkillsAllW = width - win_hero_w;
		winSkillsAllH = height;
		ah = skillWindowMap.length;
		int aw = skillWindowMap[0].length;
		/*int aw = 3;
		ah = Skills.maxSkills[Pers.HERO] / aw;
		if(Skills.maxSkills[Pers.HERO] % aw != 0) ah++;*/
		rSkillAllDW = (winSkillsAllW - (aw * itemSkillW)) / (aw + 1);
		rSkillAllDH = (winSkillsAllH - (ah * itemSkillH)) / (ah + 1);
		int count = 0;
		yy = rSkillAllDH;
		for(int i = 0; i < ah; i++, yy += itemSkillH + rSkillAllDH){
			xx = (int)rWinSkillsHero.width + rSkillAllDW;
			for(int j = 0; j < aw; j++, xx += itemSkillW + rSkillAllDW){
				rSkillsAll[count].setSize(itemSkillW, itemSkillH);
				rSkillsAll[count].setPosition(xx, yy);
				count++;
			}
		}
		
		initSkillSelected();
	}
	
	private Rectangle rectWinSkillSelected = new Rectangle();
	private Rectangle rectButSkillSelected[];
	private final int BUT_SKILL_SELECTED_MONEY = 0;
	private final int BUT_SKILL_SELECTED_CASH = 1;
	private final int BUT_SKILL_SELECTED_ACTIVATE = 2;
	private final int aBUT_SKILL_SELECTED = 3;
	private int winSkillSelectedPadd;
	
	private void initSkillSelected(){
		int fontH = font.getHeight();
		winSkillSelectedPadd = fontH;
		float w = itemSkillW + (winSkillSelectedPadd << 1);
		float h = winSkillsAllH - (fontH << 1);
		float x = (width - w) / 2;
		float y = (winSkillsAllH - h) / 2;
		rectWinSkillSelected.setSize(w, h);
		rectWinSkillSelected.setPosition(x, y);
		if(rectButSkillSelected == null){
			rectButSkillSelected = new Rectangle[aBUT_SKILL_SELECTED];
			for(int i = 0; i < aBUT_SKILL_SELECTED; i++) rectButSkillSelected[i] = new Rectangle();
		}
		
		int but_skil_selected_h = fontH + fontH;
		x = rectWinSkillSelected.x + winSkillSelectedPadd;
		y = rectWinSkillSelected.y + rectWinSkillSelected.height - winSkillSelectedPadd - but_skil_selected_h;
		for(int i = aBUT_SKILL_SELECTED - 1; i >= 0; i--, y -= but_skil_selected_h + winSkillSelectedPadd){
			rectButSkillSelected[i].setSize(itemSkillW, but_skil_selected_h);
			rectButSkillSelected[i].setPosition(x, y);
		}
	}

	private void mathSkills() {

	}

	private void drawWindowSkills(Graphics g) {
		drawBackImage(g);
		drawSkillsAll(g);
		drawSkillHero(g);
		drawButtonBack(g);
		drawSkillSelected(g);
	}
	
	private void drawSkillSelected(Graphics g){
		if(skillSelected < 0) return;
		Skill sk = Skills.skill[Pers.HERO][skillSelected];
		int skill_level = hero.skillsAllLevel[skillSelected];// + 1;
		//if(skill_level >= sk.maxLevel) skill_level = sk.maxLevel - 1;
		g.fillRect(clrBlackTransparent, 0, 0, width, height);
		g.fillRect(clrSubstrate, rectWinSkillSelected);
		float x = rectWinSkillSelected.x + (rectWinSkillSelected.width / 2) - (itemSkillW >> 1);
		float y = rectWinSkillSelected.y + winSkillSelectedPadd;
//		int skill_level = hero.skillsAllLevel[skillSelected];
		drawSkillItem(g, Pers.HERO, skillSelected, x, y, itemSkillW, itemSkillH, false, false);
		y += itemSkillH + winSkillSelectedPadd;
		font_mini.drawWrapped(g, sk.description, clrFontBrown, x, y, itemSkillW);
		int index_t;
		String s = null;
		boolean b_available_to_buy = hero.isSkillAvailableToBuy(skillSelected);
		boolean b_can_activate = hero.isSkillCanActivate(skillSelected);
		int value;
		Color clr_fnt = clrFontWhite;
		for(int i = 0; i < aBUT_SKILL_SELECTED; i++){
			index_t = 2 + i;
			boolean b_available = true;
			switch(i){
			case BUT_SKILL_SELECTED_MONEY:
				if(skill_level >= sk.maxLevel) continue;
				value = Skills.getParam(Pers.HERO, skillSelected, skill_level + 1, Skill.SP_MONEY);
				s = TXT[16][index_t];
				s += ": " + value;
				if(!b_available_to_buy) b_available = false;
				clr_fnt = hero.getMoney() >= value ? clrFontWhite : clrFontRed;
				break;
			case BUT_SKILL_SELECTED_CASH:
				if(skill_level >= sk.maxLevel) continue;
				value = Skills.getParam(Pers.HERO, skillSelected, skill_level + 1, Skill.SP_CASH);
				s = TXT[16][index_t];
				s += ": " + value;
				if(!b_available_to_buy) b_available = false;
				clr_fnt = hero.getCash() >= value ? clrFontWhite : clrFontRed;
				break;
			case BUT_SKILL_SELECTED_ACTIVATE:
				if(Skills.skill[Pers.HERO][skillSelected].gameBoxIndex >= 0) index_t = 5;
				s = TXT[16][index_t];
				if(!b_can_activate) b_available = false;
				clr_fnt = clrFontWhite;
				break;
			}
			drawButtonText(g, s, clr_fnt, rectButSkillSelected[i], false, b_available);
		}
		drawButtonBack(g);
	}
	
	private void drawSkillHero(Graphics g){
		int fontH = font.getHeight();
		g.fillRect(clrBlackTransparent, rWinSkillsHero);
		float x = rWinSkillsHero.x + (rWinSkillsHero.width / 2);
		float y = rWinSkillsHero.y + winSkillsHeroTitlePadd + (winSkillsHeroTitleH >> 1);
		float w, h;
		font.drawString(g, TXT[16][0], clrFontWhite, x, y, Uni.HCENTER_VCENTER);
		int d = fontH >> 2;
		for(int i = 0; i < Pers.maxSkillsGameBox; i++){
			x = rSkillsHero[i].x;
			y = rSkillsHero[i].y;
			w = rSkillsAll[i].width;
			h = rSkillsAll[i].height;
			g.fillRect(clrFontWhite, x - d, y - d, w + (d << 1), h + (d << 1));
			if(hero.skillsGameBox[i] >= 0){
				int i_skill = hero.skillsGameBox[i];
				int skill_level = hero.skillsAllLevel[i_skill];
				drawSkillItem(g, Pers.HERO, i_skill, x, y, w, h, false, false);
			}
			else{
				font.drawString(g, TXT[16][1], clrFontBrown, x + (w / 2), y + (h / 2), Uni.HCENTER_VCENTER);
			}
		}
	}

	private void drawSkillsAll(Graphics g) {
		float x, y, w, h;
		int fontH = font.getHeight();
		int d = fontH >> 2;
		int i = 0;
		int lineSize = fontH >> 2;
		int ay = skillWindowMap.length;
		for(int iy = 0; iy < ay; iy++){
			int ax = skillWindowMap[iy].length;
			for(int ix = 0; ix < ax; ix++){
				int id = skillWindowMap[iy][ix];
				int i_skill = id - 1;
				int skill_level = hero.skillsAllLevel[i_skill];// + 1;
				x = rSkillsAll[i].x;
				y = rSkillsAll[i].y;
				w = rSkillsAll[i].width;
				h = rSkillsAll[i].height;
				g.fillRect(clrFontWhite, x - d, y - d, w + (d << 1), h + (d << 1));
				if(ix < ax - 1) g.fillRect(Color.WHITE, x + w, y + (h / 2) - (lineSize >> 1), rSkillAllDW, lineSize);
				if(iy < ay - 1) g.fillRect(Color.WHITE, x + (w / 2) - (lineSize >> 1), y + h, lineSize, rSkillAllDH);
				if(i == skillDragging){
					x += skillDraggingPos.x;
					y += skillDraggingPos.y;
				}
				drawSkillItem(g, Pers.HERO, i_skill, x, y, w, h, true, !hero.isSkillAvailableToBuy(i_skill));
				i++;
			}
		}
	}
	
	private int arr_skill_item_param[] = {
			Skill.SP_FIRE,
			Skill.SP_WATER,
			Skill.SP_AIR,
			Skill.SP_EARTH
	};
	
	private void drawSkillItem(Graphics g, int kind, int index, float x, float y, float w, float h, boolean b_with_close, boolean b_close){
		int skill_level = hero.skillsAllLevel[index];
		int fontH = font.getHeight();
		int font_miniH = font_mini.getHeight();
		int manna_w = fontH + (fontH >> 1);
		int manna_h = manna_w;
		int manna_dw = fontH >> 2;
		int spr;
		Color clr_fnt;
		Color clr_back = skill_level >= 0 ? Color.GOLD : clrFontBrown;
		if(b_with_close && b_close) {
			clr_back = Color.BLACK;
		}
		g.fillRect(clr_back, x, y, w, h);
		font_mini.drawString(g, Skills.skill[kind][index].title, clrFontWhite, x + (w / 2), y + itemSkillPadd + (fontH >> 1), Uni.HCENTER_VCENTER);
		int levcw = font_miniH;
		int levch = font_miniH;
		float xx = x + w - levcw;
		float yy = y;
		g.fillRect(Color.GREEN, xx, yy, levcw, levch);
		font_mini.drawString(g, Integer.toString(skill_level + 1), Color.BLACK, xx + (levcw >> 1), yy + (levch >> 1), Uni.HCENTER_VCENTER);
		if(skill_level >= 0) {
			float X = x + manna_dw;// + w / 4;
			float Y = y + h - itemSkillPadd - manna_h;
			int param;
			for (int j = 0; j < arr_skill_item_param.length; j++) {
				param = Skills.getParam(kind, index, skill_level, arr_skill_item_param[j]);
				if (param <= 0) continue;
				switch (arr_skill_item_param[j]) {
					case Skill.SP_FIRE:
						spr = P_FIRE;
						clr_fnt = clrFontWhite;
						break;
					case Skill.SP_WATER:
						spr = P_WATER;
						clr_fnt = clrFontBrown;
						break;
					case Skill.SP_AIR:
						spr = P_AIR;
						clr_fnt = clrFontBrown;
						break;
					case Skill.SP_EARTH:
						spr = P_EARTH;
						clr_fnt = clrFontWhite;
						break;
					default:
						spr = -1;
						clr_fnt = null;
						break;
				}
				g.fillRect(clrFontWhite, X - 1, Y - 1, manna_w + 2, manna_h + 2);
				g.fillRect(ballColors[spr], X, Y, manna_w, manna_h);
				font.drawString(g, param + "", clr_fnt, X + (manna_w >> 1), Y + (manna_h >> 1), Uni.HCENTER_VCENTER);
				X += manna_w + manna_dw;
			}
		}
	}

	// --------------------------- SELECT LOCATION
	// -----------------------------------

	Rectangle rectSelLocItems[];
	private final int aSelLocItems = 4;
	private int selLocItemsW;
	private int selLocItemsH;
	private String selLocItems[];
	private int cursorSelectLocation;

	private void runSelectLocation(boolean back) {
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_SELECT_LOCATION;
		initSelectLocation();
		cursorSelectLocation = -1;
		// loadFonSelectLevel();
	}

	private Image selLocBackImg;
	private int selLocBackImgW;
	private int selLocBackImgH;

	public final void loadSelLocBackImage() {
		if(selLocBackImg != null) return;
		try {
			try {
				selLocBackImg = new Image((Texture)Main.main.getManager().get(Assets.res_path + Assets.fileMapBack));
			} catch (Exception e) {}
		} catch (OutOfMemoryError e) {}
		if (selLocBackImg != null) {
			selLocBackImgW = width;// backImg.getRegionWidth();
			selLocBackImgH = height;// backImg.getRegionHeight();
		}
	}

	private void drawSelLocBackImage(Graphics g) {
		if (selLocBackImg != null) {
			g.drawImage(selLocBackImg, 0, 0, width, height);
		}
	}

	private void initSelectLocation() {
		loadSelLocBackImage();
		if (selLocItems == null){
			selLocItems = new String[aSelLocItems];
			rectSelLocItems = new Rectangle[aSelLocItems];
			twSelLocItemTxt = new KTween[aSelLocItems];
			twSelLocIco = new KTween[aSelLocItems];
			for(int i = 0; i < aSelLocItems; i++){
				twSelLocItemTxt[i] = new KTween();
				twSelLocIco[i] = new KTween();
			}
			float fontH = font.getHeight();// * 2;
			int witem = 0;
			int wi;
			for (int i = 0; i < aSelLocItems; i++) {
				selLocItems[i] = TXT[I_SELECT_LOCATION][i];
				wi = font.stringWidth(selLocItems[i]);
				if (witem < wi)
					witem = wi;
			}
			locTextPadd = (int)fontH;
			selLocItemsW = (int) (witem + (locTextPadd * 2));
			selLocItemsH = (int)npLocTextBackMinH;
			if(isLocIcoLoaded(0)){
				selLocItemsW += locIcoW;
				if(selLocItemsH < locIcoH){
					selLocItemsH = locIcoH;
				}
			}
			float dx = (width - (selLocItemsW * 2)) / 3;
			float dy = (height - butBackH - (butPadd << 1) - (selLocItemsH * 2)) / 3;
			float xx1 = dx;
			float xx2 = dx + selLocItemsW + dx;
			float yy1 = dy;
			float yy2 = dy + selLocItemsH + dy;
			rectSelLocItems[0] = new Rectangle(xx1, yy1, selLocItemsW, selLocItemsH);
			rectSelLocItems[1] = new Rectangle(xx2, yy1, selLocItemsW, selLocItemsH);
			rectSelLocItems[2] = new Rectangle(xx1, yy2, selLocItemsW, selLocItemsH);
			rectSelLocItems[3] = new Rectangle(xx2, yy2, selLocItemsW, selLocItemsH);	
		}
		initMenuPersAndShop();
		float x = width - butBackW - butPadd;
		float y = height - butBackH - butPadd;
		rectButBack.setPosition(x, y);
		float delay = 0.0f;
		float timeSelLocIcoAppear = 0.6f;
		float scale_all = selLocIcoMaxScale + (selLocIcoMaxScale - 1.0f);
		float t1 = timeSelLocIcoAppear * selLocIcoMaxScale / scale_all;
		float t2 = timeSelLocIcoAppear - t1;
		
		for(int i = 0; i < aSelLocItems; i++){
			tweenManager.killTarget(twSelLocIco[i]);
			Tween.set(twSelLocIco[i], KTweenAccessor.SCALE).target(0.0f).start(tweenManager);
			Tween.to(twSelLocIco[i], KTweenAccessor.SCALE, t1).target(selLocIcoMaxScale).start(tweenManager);
			Tween.to(twSelLocIco[i], KTweenAccessor.SCALE, t2).target(1.0f).delay(t1).start(tweenManager);

			tweenManager.killTarget(twSelLocItemTxt[i]);
			Tween.set(twSelLocItemTxt[i], KTweenAccessor.ALPHA).target(0.0f).start(tweenManager);
			Tween.to(twSelLocItemTxt[i], KTweenAccessor.ALPHA, 1.0f).target(1.0f).delay(timeSelLocIcoAppear + delay).start(tweenManager);
			delay += 0.2f;
		}
	}
	
	private float selLocIcoMaxScale = 1.2f;
	private float selLocIcoSelectedTime = 0.2f;
	
	private float locIcoRotate;
	
	private void mathLocIcoRotate(){
		float dt = getDTime();
		float speed = 30.0f;
		locIcoRotate += speed * dt;
		locIcoRotate %= 360;
	}

	private void mathSelectLocation() {
		mathLocIcoRotate();
	}

	private final void drawSelectLocation(Graphics g) {
		drawSelLocBackImage(g);
		drawMenuSelectLocation(g);
		drawMenuPersAndShop(g);
		drawHeroLifes(g);
		drawButtonBack(g);
	}
	
	private final void drawHeroLifes(Graphics g){
		int fontH = font.getHeight();
		int cw = fontH << 1;
		int ch = fontH << 1;
		int dw = cw >> 2;
		int w = -dw;
		for(int i = hero.getLifeMax() - 1; i >= 0; i--) w += cw + dw;
		float x = width - w >> 1;
		float y = fontH >> 1;
		int d = dw >> 2;
		font_mini.drawString(g, "life: " + hero.getLife(), clrFontBrown, x - (fontH << 1), y + (ch >> 1), Uni.RIGHT_VCENTER);
		for(int i = 0; i < hero.getLifeMax(); i++){
			g.fillRect(Color.WHITE, x - d, y - d, cw + (d << 1), ch + (d << 1));
			g.fillRect(Color.BLACK, x, y, cw, ch);
			if(i < hero.getLife()){
				g.fillRect(Color.WHITE, x, y, cw, ch);
			}
			else if(i == hero.getLife()){
				float h = (hero.timerLife * ch) / hero.oneLifeRegenTime;
				g.fillRect(Color.WHITE, x, y + ch - h, cw, h);
			}
			x += cw + dw;
		}
		//y += ch + fontH;
		x -= dw;
		x += (fontH << 1);
		if(hero.getLife() < hero.getLifeMax()){
			font_mini.drawString(g, hero.getLifeTime(), clrFontBrown, x, y + (ch >> 1), Uni.HCENTER_VCENTER);
		}
	}

	private final void disposeSelectLocation() {
	
	}
	
	private KTween twSelLocItemTxt[];
	private KTween twSelLocIco[];

	private final void drawMenuSelectLocation(Graphics g) {
		float x, y;
		int dy = (int) ((selLocItemsH - npLocTextBackMinH) / 2);
		for (int i = 0; i < rectSelLocItems.length; i++) {
			x = rectSelLocItems[i].x;
			y = rectSelLocItems[i].y;
			x += (locIcoW >> 1);
			drawNinePatch(g, npLocTextBack, clrLocTextBack, x, y + dy, selLocItemsW - (locIcoW >> 1), npLocTextBackMinH);
			if(isLocIcoLoaded(i)){
				float cw = locIcoW;
				float ch = locIcoH;
				float scale = twSelLocIco[i].scale;
				if(scale != 1.0f){
					cw = locIcoW * scale;
					ch = locIcoH * scale;
				}
				g.draw(trLocIco[i], x - (cw / 2), y + ((selLocItemsH - ch) / 2), cw / 2, ch / 2, cw, ch, 1, 1, locIcoRotate, true);
				x += (locIcoW >> 1);
			}
			x += locTextPadd;
			font.setColor(clrFontBrown.r, clrFontBrown.g, clrFontBrown.b, twSelLocItemTxt[i].color.a);
			font.drawString(g, selLocItems[i], x, y + (selLocItemsH >> 1), Uni.LEFT_VCENTER);
		}
	}
	
	private boolean isLocIcoLoaded(int index){
		return trLocIco != null && trLocIco[index] != null && trLocIco[index].getTexture() != null;
	}

	// --------------------------- SELECT LEVEL
	// -----------------------------------
	
	SelectLevelItem selLevItems[];
	private int aSelLevItems;// = 4;
	private int aSelLevItemsW;
	private int aSelLevItemsH;
	private int selLevW;
	private int selLevH;
	private int selLevItemW;
	private int selLevItemH;
	private int selLevItemsDX;
	private int selLevItemsDY;
	private int selLevItemPadd;
	private int selLevItemsDH;
	private String selLevItemsTxt[];
	private int cursorSelectLevel;
	private static Rectangle rMenuPersAndShop[] = new Rectangle[2];
	private static int menuPersAndShopW;
	private int hMenuPersAndShop;
	private final int BUT_PERS = 0;
	private final int BUT_SHOP = 1;
	private static int cursorMenuPersAndShop;
	private boolean bConfirmWindow;
	
	private boolean touchDownMenuPersAndShop(){
		int a = rMenuPersAndShop.length;
		for (int i = 0; i < a; i++) {
			if (rMenuPersAndShop[i].contains(touchPosDown.x, touchPosDown.y)) {
				cursorMenuPersAndShop = i;
				audio.playSound(Audio.SND_CLICK_MENU);
				return true;
			}
		}
		return false;
	}
	
	private boolean touchUpMenuPersAndShop(){
		int a = rMenuPersAndShop.length;
		for (int i = 0; i < a; i++) {
			if (rMenuPersAndShop[i].contains(touchPosDown.x, touchPosDown.y)) {
				switch (i) {
				case BUT_PERS:
					runPers(false);
					return true;
				case BUT_SHOP:
					runShop(false);
					return true;
				}
			}
		}
		cursorMenuPersAndShop = -1;
		return false;
	}

	private void runSelectLevel(boolean back) {
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_SELECT_LEVEL;
		initSelectLevel();
		cursorSelectLevel = -1;
		cursorMenuPersAndShop = -1;
		bConfirmWindow = false;
		bCannotSelectLevel = false;
		audio.playMusic(Audio.MSC_MAIN_MENU, true);
		// loadFonSelectLevel();
	}
	
	private int selLevHudW, selLevHudH;

	private final void initSelectLevel() {
		if (selLevItemsTxt == null){
			aSelLevItems = maxLevel;
			aSelLevItemsW = 5;
			aSelLevItemsH = 4;
			selLevItemsTxt = new String[aSelLevItems];
			selLevItems = new SelectLevelItem[aSelLevItems];
			int count = 0;
			for(int i = 0; i < aSelLevItemsH; i++){
				for(int j = 0; j < aSelLevItemsW; j++){
					selLevItems[count++] = new SelectLevelItem();
				}
			}
			int fontH = font.getHeight();
			int witem = 0;
			int wi;
			for (int i = 0; i < aSelLevItems; i++) {
				selLevItemsTxt[i] = "" + (i + 1);
				wi = font.stringWidth(selLevItemsTxt[i]);
				if (witem < wi)
					witem = wi;
			}
			selLevItemPadd = (int) (fontH / 4);
			selLevItemW = (int) (witem + (selLevItemPadd << 1));
			selLevItemH = (int) (fontH + starMiniH + (selLevItemPadd << 1));
			if(trSelLevBox != null){
				selLevItemW = boxLevW;
				selLevItemH = boxLevH;
				selLevItemPadd = boxLevH >> 4;
				int hh = selLevItemH - (selLevItemPadd << 1) - fontH - starMiniH;
				selLevItemsDH = hh / 3;
			}
			
			selLevHudW = width / 10;
			if(selLevHudW < menuPersAndShopW) selLevHudW = menuPersAndShopW;
			selLevHudH = fontH;
			selLevW = width - selLevHudW - (butPadd << 1);
			selLevH = height;
			selLevItemsDX = (selLevW - (selLevItemW * aSelLevItemsW)) / (aSelLevItemsW + 1);
			selLevItemsDY = (selLevH - (selLevItemH * aSelLevItemsH)) / (aSelLevItemsH + 1);
			
			initConfirmWindow();
		}
		initMenuPersAndShop();
		float x = width - butBackW - butPadd;
		float y = height - butBackH - butPadd;
		rectButBack.setPosition(x, y);
		float X = width - selLevW;
		float Y = 0;
		int dw2 = (selLevItemW >> 1);
		int dh2 = (selLevItemH >> 1);
		x = X + selLevItemsDX + dw2;
		y = Y + selLevItemsDY + dh2;
		int count = 0;
		float yy = y;
		float delta_time = 0.0f;
		for(int i = 0; i < aSelLevItemsH; i++){
			float xx = x;
			for(int j = 0; j < aSelLevItemsW; j++){
				selLevItems[count].set(xx - dw2, yy - dh2, selLevItemW, selLevItemH);
				tweenManager.killTarget(selLevItems[count]);
				Tween.set(selLevItems[count], KTweenAccessor.ALPHA).target(0.0f).start(tweenManager);
				Tween.from(selLevItems[count], KTweenAccessor.POSITION_XY, 1.0f).delay(delta_time).target(selLevItems[count].x, selLevItems[count].y + (selLevItemH >> 1)).start(tweenManager);
				Tween.to(selLevItems[count], KTweenAccessor.ALPHA, 1.0f).delay(delta_time).target(1.0f).start(tweenManager);
				delta_time += 0.05f;
				count++;
				xx += selLevItemW + selLevItemsDX;
			}
			yy += selLevItemH + selLevItemsDY;
		}
		
	}
	
	private Rectangle rectButtonFight = new Rectangle();
	private Rectangle rectConfirmWindow = new Rectangle();

	private void runConfirmWindow(){
		bConfirmWindow = true;
		int fontH = font.getHeight();
		int winw = width - (width >> 1);
		int winh = height - (height >> 1);
		int winx = width - winw >> 1;
		int winy = height - winh >> 1;
		rectConfirmWindow.setSize(winw, winh);
		rectConfirmWindow.setPosition(winx, winy);
		int w = font.stringWidth(TXT[6][2]) + (fontH << 2);
		int h = fontH << 1;
		rectButtonFight.setSize(w, h);
		rectButtonFight.setPosition(width - w >> 1, rectConfirmWindow.y + rectConfirmWindow.height);
	}
	
	private void initConfirmWindow(){

	}
	
	private void drawConfirmWindow(Graphics g){
		if(!bConfirmWindow) return;
		g.fillRect(clrBlackTransparent, 0, 0, width, height);
		Rectangle rc = rectConfirmWindow;
		g.fillRect(clrFontWhite, rc);
		font.drawString(g, TXT[11][0], clrFontBrown, rc.x + (rc.width / 2), rc.y + (rc.height / 2), Uni.HCENTER_VCENTER);
		font.drawString(g, TXT[12][Pers.PERSI_HEALTH]+": "+hero.getHealthMax(true), clrFontBrown, rc.x + (rc.width / 2), rc.y + (rc.height / 4), Uni.HCENTER_VCENTER);
		drawButtonText(g, TXT[6][2], rectButtonFight, false);
	}

	private void mathSelectLevel() {

	}

	private final void drawSelectLevel(Graphics g) {
		drawBackImage(g);
		drawMenuSelectLevel(g);
		drawSelectLevelStarNumber(g);
		drawCannotSelectLevel(g);
		drawButtonBack(g);
		drawMenuPersAndShop(g);
		drawHeroLifes(g);
		drawConfirmWindow(g);
	}
	
	private void initMenuPersAndShop(){
		cursorMenuPersAndShop = -1;
		int a = rMenuPersAndShop.length;
		int fontH = font.getHeight();
		int h = fontH << 1;
		int dh = fontH >> 1;
		hMenuPersAndShop = (h + dh) * a - dh;
		int sw, max_sw = 0;
		int x = butPadd;
		int y = height - hMenuPersAndShop - butPadd;//height2 - (h + fontH >> 1);
		for(int i = 0; i < a; i++){
			if(rMenuPersAndShop[i] == null) rMenuPersAndShop[i] = new Rectangle();
			sw = font.stringWidth(TXT[10][i]) + (fontH << 1);
			if(max_sw < sw) max_sw = sw;
			rMenuPersAndShop[i].setPosition(x, y);
			rMenuPersAndShop[i].setSize(max_sw, h);
			y += h + dh;
		}
		menuPersAndShopW = max_sw;
	}
	
	public static final void drawMenuPersAndShop(Graphics g){
		int a = rMenuPersAndShop.length;
		for(int i = 0; i < a; i++){
			drawButtonText(g, TXT[10][i], rMenuPersAndShop[i], i == cursorMenuPersAndShop);
		}
	}
	
	public static void drawButtonText(Graphics g, String str, Rectangle rect, boolean b_active){
		drawButtonText(g, str, rect, b_active, true);
	}
	
	public static void drawButtonText(Graphics g, String str, Rectangle rect, boolean b_active, boolean b_available){
		drawButtonText(g, str, clrFontWhite, rect, b_active, b_available);
	}
	
	public static void drawButtonText(Graphics g, String str, Color clr_fnt, Rectangle rect, boolean b_active, boolean b_available){
		Color clr = b_active ? clrMenuItemActive : clrMenuItemPassive;
		int d = 0;//(int)moveButtonTextValue;
		g.fillRect(clr, rect.x - d, rect.y - d, rect.width + (d << 1), rect.height + (d << 1));
		font.drawString(g, str, clr_fnt, rect.x + (rect.width / 2), rect.y + (rect.height / 2), Uni.HCENTER_VCENTER);
		if(!b_available){
			g.fillRect(clrBlackTransparent, rect);
		}
	}
		
	private boolean bCannotSelectLevel;
	private int aStarCannotSelectLevel;
	
	private boolean isLevelOpen(int lev){
		return getMustCollectStars(lev) <= 0;
	}
	
	private int getMustCollectStars(int lev){
		int a = 0;
		if(lev >= aStartOpenLevel){
			a = lev - aStartOpenLevel + 1;
		}
		a *= starMaxNumber;
		a -= getRecordStar();
		return a;
	}
	
	private void drawCannotSelectLevel(Graphics g){
		if(!bCannotSelectLevel) return;
		int ind = 6;
		String str = Integer.toString(aStarCannotSelectLevel);//TXT[ind][0] + " " + aStarCannotSelectLevel + " " + TXT[ind][1];
		int fontH = font.getHeight();
		float fsy = font.getLineHeight() - fontH;
		int rowIcoH = fontH;
		int padd = fontH << 1;
		int D = width < height ? width : height;
		D -= D >> 2;
		int w = D;
		if(isNinePatchLoaded(npSubstrate) && w < npSubstrate.getTotalWidth()) w = (int) npSubstrate.getTotalWidth();
		float h_text = fontH + fsy + rowIcoH;
		float h = (padd << 1) + h_text + dialogButtonH + butPadd;
		if(isNinePatchLoaded(npSubstrate) && h < npSubstrate.getTotalHeight()) h = (int) npSubstrate.getTotalHeight();
		float x = width2 - (w >> 1);
		float y = height2 - (h / 2);
		drawNinePatch(g, npSubstrate, clrSubstrate, x, y, w, h);
		
		float xx = x + (w >> 1);
		float yy = y + padd + fontH;//h - h_text >> 1;
		font.drawString(g, TXT[ind][0], clrFontBrown, xx, yy, Uni.HCENTER_BOTTOM);
		yy += fsy + rowIcoH;
		float w_txt1 = font.stringWidth(TXT[ind][1]) + font.getSpaceWidth();
		float ww = w_txt1 + font.stringWidth(str);
		xx -= ww / 2;
		font.drawString(g, TXT[ind][1], clrFontBrown, xx, yy, Uni.LEFT_BOTTOM);
		xx += w_txt1;
		font.drawString(g, str, clrFontBrown, xx, yy, Uni.LEFT_BOTTOM);
		
		//font.drawWrapped(g, str, clrFontBrown, xx, yy, w_text, 1);
		
		xx = x + (w - dialogButtonW >> 1);
		yy = y + h - dialogButtonH - butPadd;
		g.draw(trDialogOK, xx, yy);
	}

	private final void disposeSelectLevel() {
		
	}
	
	private void drawSelectLevelStarNumber(Graphics g){
		String str = getRecordStar() + "/" + recordStarMax;
		int x = butPadd + (selLevHudW >> 1);
		int y = butPadd + (selLevHudH >> 1);
		font.drawString(g, "stars: "+str, clrFontWhite, x, y, Uni.HCENTER_VCENTER);
	}
	
	private final void drawMenuSelectLevel(Graphics g) {
		float x, y, xx, yy;
		boolean lock = false;
		TextureRegion tr;
		int padd_star = starMiniW >> 2;
		int w_stars = -padd_star;
		for(int i = 0; i < starMaxNumber; i++) w_stars += starMiniW + padd_star;
		Color clr = g.getColor();
		Color clrf = clrFontWhite;//font.getColor();
		for (int i = 0; i < selLevItems.length; i++) {
			lock = !isLevelOpen(i) && !CHEAT_ALL_LEVELS;
			tr = lock ? trSelLevBoxLock : trSelLevBox;
			x = selLevItems[i].x + (selLevItems[i].width / 2);
			y = selLevItems[i].y + (selLevItems[i].height / 2);
			float a = selLevItems[i].color.a;
			g.setColor(clr.r, clr.g, clr.b, a);
			font.setColor(clrf.r, clrf.g, clrf.b, a);
			if(tr != null && tr.getTexture() != null){
				g.draw(tr, selLevItems[i].x, selLevItems[i].y);
			}
			else{
				g.fillRect(selLevItems[i].x, selLevItems[i].y, selLevItemW, selLevItemH);
			}
			xx = x - (w_stars >> 1);
			yy = y + (selLevItemH >> 1) - selLevItemPadd - selLevItemsDH - starMiniH;
			int i_star;
			for (int j = 0; j < starMaxNumber; j++) {
				if (lock) {
					i_star = 2;
				} else {
					i_star = j < recordStar[i] ? 0 : 1;
				}
				g.draw(trStarMini[i_star], xx, yy);
				xx += starMiniW + padd_star;
			}
			xx = x;
			yy = selLevItems[i].y + selLevItemPadd + selLevItemsDH;
			font.drawString(g, selLevItemsTxt[i], font.getColor(), xx, yy, Uni.HCENTER_TOP);
			if(lock){
				tr = trMenuLock;
				xx = selLevItems[i].x - (tr.getRegionHeight() >> 2);
				yy = selLevItems[i].y - (tr.getRegionHeight() >> 2);
				g.draw(tr, xx, yy);
			}
		}
		g.setColor(clr);
		font.setColor(clrf);
	}
	
	private final void mathLoader() {
		MODE = MD_CLASSIC;
		step = Pers.HERO;
		initMap();
		initBalls();
		initEnemy();
		initHero();
		setupCursorAfterLoad(MAP_SIZE_W >> 1, MAP_SIZE_H >> 1);
		mathCursor();
		
		loadBackImage();
		
		initPadding();
		initHud();
		initStartGameBalls();
		initTutorial();
		paintItem = I_GAME;
		bLoadGame = false;
		startGameMusic();
	}
	
	private void initHero(){
		hero.startGame();
	}
	
	private void initEnemy(){
		int index = curLevel;
		if(index >= aMonsters) index = aMonsters - 1;
		int mp[] = monsterParam[index];
		enemy.nick = monsterText[index][0];//"enemy";
		enemy.index_hud = 1;
		enemy.setHealthMax(mp[MP_HEALTH]);
		enemy.setHealth(enemy.getHealthMax(false));
		enemy.setFire(mp[MP_FIRE]);
		enemy.setWater(mp[MP_WATER]);
		enemy.setAir(mp[MP_AIR]);
		enemy.setEarth(mp[MP_EARTH]);
		
		enemy.delSkillGameBoxAll();
		int i_skill = monsterParam[index][MP_SKILL] - 1;
		if(i_skill >= 0){
			int kind = Pers.MONSTER;
			int i_level = 0;
			int v;
			v = Skills.getParam(kind, i_skill, i_level, Skill.SP_FIRE);
			if(enemy.getFire(false) < v) enemy.setFire(v);
			v = Skills.getParam(kind, i_skill, i_level, Skill.SP_WATER);
			if(enemy.getWater(false) < v) enemy.setWater(v);
			v = Skills.getParam(kind, i_skill, i_level, Skill.SP_AIR);
			if(enemy.getAir(false) < v) enemy.setAir(v);
			v = Skills.getParam(kind, i_skill, i_level, Skill.SP_EARTH);
			if(enemy.getEarth(false) < v) enemy.setEarth(v);
			enemy.addSkillGameBox(i_skill);
			enemy.skillsAllLevel[i_skill] = 0;
		}
		enemy.startGame();
		
		//enemy.manna[Pers.MN_EARTH] = enemy.getEarth();
	}
	
	private void startGameMusic(){
		musicGameIndex++;
		if(musicGameIndex < Audio.MSC_GAME) musicGameIndex = Audio.MSC_GAME;
		if(musicGameIndex >= Audio.MSC_GAME + musicGameNumber) musicGameIndex = Audio.MSC_GAME;
		audio.playMusic(musicGameIndex, true);
	}

	// ------------------------- GAME ----------------------------
	
	private Ball ball[];
	public int aBallType = 7;

	private boolean bBallMoving;
	private int musicGameIndex;
	private int musicGameNumber = 2;

	private float messPoint[][];
	private int a_mess_param = 6;
	private byte MSG_VALUE = 0;
	private byte MSG_X = 1;
	private byte MSG_Y = 2;
	private byte MSG_DY = 3;
	private byte MSG_TIMER = 4;
	private byte MSG_COMBO = 5;
	private float messTimeMax = 1.5f;
	
	private final int P_FIRE = 1;
	private final int P_WATER = 4;
	private final int P_AIR = 5;
	private final int P_EARTH = 6;
	private final int P_SKULL = 2;
	private final int P_MONEY = 3;
	private final int P_EXP = 7;

	private void addMess(int value, int x, int y, int dx, int dy) {
		if (messPoint == null) {
			int a_max_mess = 10;
			messPoint = new float[a_max_mess][a_mess_param];
		}
		int xx = (x * CELL_W) + (CELL_W >> 1);
		int yy = (y * CELL_H) + (CELL_H >> 1);
		if (dx != 0) {
			xx = (x - dx) * CELL_W + (dx * CELL_W >> 1);
		}
		if (dy != 0) {
			yy = (y - dy) * CELL_H + (dy * CELL_H >> 1);
		}
		for (int i = messPoint.length - 1; i >= 0; i--) {
			if (messPoint[i][MSG_VALUE] == 0) {
				messPoint[i][MSG_VALUE] = value;
				messPoint[i][MSG_X] = xx;
				messPoint[i][MSG_Y] = yy;
				messPoint[i][MSG_DY] = 0;
				messPoint[i][MSG_TIMER] = messTimeMax;
				messPoint[i][MSG_COMBO] = point_current;
				return;
			}
		}
	}

	private void mathMess() {
		if (messPoint == null) return;
		float anim_speed = CELL_H >> 1;
		float dt = getDTime();
		for (int i = messPoint.length - 1; i >= 0; i--) {
			float mp[] = messPoint[i];
			if (mp[MSG_VALUE] != 0) {
				mp[MSG_TIMER] -= dt;
				mp[MSG_DY] -= anim_speed * dt;
				if (mp[MSG_TIMER] <= 0) {
					mp[MSG_TIMER] = 0;
					mp[MSG_VALUE] = 0;
				}
			}
		}
	}

	private void drawMess(Graphics g) {
		if (messPoint == null) {
			return;
		}
		String txt;
		float x, y;
		for (int i = messPoint.length - 1; i >= 0; i--) {
			if (messPoint[i][MSG_VALUE] != 0) {
				float m[] = messPoint[i];
				txt = "" + (int)m[MSG_VALUE];
				if (m[MSG_VALUE] > 0)
					txt = "+" + txt;
				x = MAP_X + m[MSG_X];
				y = MAP_Y + (m[MSG_Y] + m[MSG_DY]);
				font.drawString(g, txt, clrFontWhite, x, y, Uni.HCENTER_VCENTER);
			}
		}
	}
	
	private Message gameMessage;
	
	private void addGameMessage(String title, String txt){
		gameMessage = new Message(title, txt);
	}
	
	private void drawGameMessage(Graphics g){
		if(gameMessage == null) return;
		gameMessage.draw(g);
	}
	
	private void mathGameMessage(float dt){
		if(gameMessage == null) return;
		boolean b = gameMessage.math(dt);
		if(!b){
			gameMessage = null;
			int v, amount;
			if(step == Pers.HERO){
				switch(hero.skillUsingIndex){
				case Skill.HERO_STRIKE:
					v = 3 + (hero.getManna(Pers.MN_EARTH) / 5);
					enemy.addHealth(-v);
					bDoneStep = true;
					break;
				case Skill.HERO_SHOT:
					break;
				case Skill.HERO_DODGE:
					hero.setSkillStep(hero.skillUsingIndex, hero.getSkillStep(hero.skillUsingIndex) + 2 + 1);//+1 because counting enemy steps
					bDoneStep = true;
					break;
				case Skill.HERO_ACCELERATION:
					hero.setSkillStep(hero.skillUsingIndex, hero.getSkillStep(hero.skillUsingIndex) + 3);
					bDoneStep = true;
					break;
				case Skill.HERO_PRAYER:
					hero.setSkillStep(hero.skillUsingIndex, hero.getSkillStep(hero.skillUsingIndex) + 5);
					bDoneStep = true;
					break;
				case Skill.HERO_PROVOCATION:
					hero.bProvocation = true;
					bDoneStep = true;
					break;
				case Skill.HERO_RAIN:
					break;
				case Skill.HERO_BEER:
					hero.addHealth(5);
					bDoneStep = true;
					break;
				case Skill.HERO_HANDFUL_EARTH:
					break;
				case Skill.HERO_FIG:
					hero.setSkillStep(hero.skillUsingIndex, hero.getSkillStep(hero.skillUsingIndex) + 2);
					bDoneStep = true;
					break;
				case Skill.HERO_GREED:
					deleteGems(P_MONEY);
					bDoneStep = true;
					break;
				case Skill.HERO_THROW_STONE:
					break;
				case Skill.HERO_KICK:
					v = 15;
					enemy.addHealth(-v);
					bDoneStep = true;
					break;
				case Skill.HERO_EARTHQUAKE:
					amount = deleteGems(P_EARTH);
					v = 10 + amount;
					enemy.addHealth(-v);
					bDoneStep = true;
					break;
				case Skill.HERO_POISON_EXPLOSION:
					amount = 0;
					amount += deleteGems(P_FIRE);
					amount += deleteGems(P_WATER);
					v = 20 + amount;
					enemy.addHealth(-v);
					bDoneStep = true;
					break;
				}
				if(bDoneStep) hero.skillUsingIndex = -1;
			}
			else{
				int x, y;
				int res[];
				switch(enemy.skillUsingIndex){
				case Skill.MONSTER_STRIKE:
				case Skill.MONSTER_BITE:
					v = 3 + (enemy.getManna(Pers.MN_EARTH) / 5);
					hero.addHealth(-v);
					bDoneStep = true;
					break;
				case Skill.MONSTER_BURP:
					res = getRandomMapPosition(-1);
					x = res[0];
					y = res[1];
					deleteHorizontalLine(x, y);
					bDoneStep = true;
					break;
				case Skill.MONSTER_ROOTS:
					enemy.setSkillStep(enemy.skillUsingIndex, enemy.getSkillStep(enemy.skillUsingIndex) + 2 + 1);//+1 because counting enemy steps
					bDoneStep = true;
					break;
				case Skill.MONSTER_SEA_WAVE:
					res = getRandomMapPosition(P_WATER);
					x = res[0];
					y = res[1];
					changeToGem(x, y, P_WATER);
					bDoneStep = true;
					break;
				case Skill.MONSTER_SONG:
					enemy.setSkillStep(enemy.skillUsingIndex, enemy.getSkillStep(enemy.skillUsingIndex) + 5);
					bDoneStep = true;
					break;
				case Skill.MONSTER_LAUGH:
					enemy.bProvocation = true;
					bDoneStep = true;
					break;
				case Skill.MONSTER_LIGHTING:
					res = getRandomMapPosition(-1);
					x = res[0];
					y = res[1];
					deleteVerticalLine(x, y);
					v = (enemy.getManna(Pers.MN_AIR) / 5);
					hero.addHealth(-v);
					bDoneStep = true;
					break;
				case Skill.MONSTER_FIREBALL:
					res = getRandomMapPosition(-1);
					x = res[0];
					y = res[1];
					deleteSquare(x, y, 3, 3);
					v = (enemy.getManna(Pers.MN_FIRE) / 5);
					hero.addHealth(-v);
					bDoneStep = true;
					break;
				case Skill.MONSTER_VAMPIRE:
					v = 10;
					hero.addHealth(-v);
					enemy.addHealth(v);
					bDoneStep = true;
					break;
				case Skill.MONSTER_REGENARATION:
					enemy.setSkillStep(enemy.skillUsingIndex, enemy.getSkillStep(enemy.skillUsingIndex) + 5);
					bDoneStep = true;
					break;
				}
			}
		}
	}
	
	private int[] getRandomMapPosition(int not_spr){
		int x = Uni.random(0, MAP_SIZE_W - 1);
		int y = Uni.random(0, MAP_SIZE_H - 1);
		int ax = 0;
		while(!MAP[y][x] || ball[MAP_BALL[y][x]].B_SPR == not_spr){
			x++;
			ax++;
			if(x >= MAP_SIZE_W) x = 0;
			if(ax >= MAP_SIZE_W){
				ax = 0;
				y++;
				if(y >= MAP_SIZE_H) y = 0;
			}
		}
		int res[] = new int[2];
		res[0] = x;
		res[1] = y;
		return res;
	}
	
	private void drawBall(Graphics g) {
		if (ball == null) return;
		int len = ball.length;
		Color clr = g.getColor();
		for (int i = 0; i < len; i++) {
			if(ball[i] != null){
				ball[i].draw(g, clr);
			}
		}
	}
	
	private boolean bFinish;
	private boolean bResult;
	private boolean bWin;

	private Rectangle rectButMenu = new Rectangle();

	private void drawButtonMenuImage(Graphics g) {
		if (bResult) return;
		float x = rectButMenu.x;// width - paddingX - butMenuW;
		float y = rectButMenu.y;// height - paddingX - butMenuH;
		g.draw(trButMenu, x, y);
	}

	private int point_current;
	private boolean b_point_grow;

	public final void initStartGameBalls() {
		while (deleteAndShiftBalls(false)) {
			int aa = 0;
		}
		int num;

		float delay = 0;
		int count = 0;
		int rand = Uni.random(0, 4);
		float ball_step_delay = Ball.ball_step_delay;
		switch (rand) {
		case 0:
			for (int Y = MAP_SIZE_H - 1; Y >= 0; Y--) {
				for (int X = 0; X < MAP_SIZE_W; X++) {
					if(!MAP[Y][X]) break;
					num = MAP_BALL[Y][X];
					ball[num].B_DELAY = delay;
				}
				delay += ball_step_delay;
			}
			break;
		case 1:
			for (int Y = MAP_SIZE_H - 1; Y >= 0; Y--) {
				for (int X = MAP_SIZE_W - 1; X >= 0; X--) {
					if(!MAP[Y][X]) break;
					num = MAP_BALL[Y][X];
					ball[num].B_DELAY = delay;
				}
				delay += ball_step_delay;
			}
			break;
		case 2:
			for (int X = MAP_SIZE_W - 1; X >= 0; X--) {
				for (int Y = MAP_SIZE_H - 1; Y >= 0; Y--) {
					if(!MAP[Y][X]) break;
					num = MAP_BALL[Y][X];
					ball[num].B_DELAY = delay;
				}
				delay += ball_step_delay;
			}
			break;
		case 3:
			for (int X = 0; X < MAP_SIZE_W; X++) {
				for (int Y = MAP_SIZE_H - 1; Y >= 0; Y--) {
					if(!MAP[Y][X]) break;
					num = MAP_BALL[Y][X];
					ball[num].B_DELAY = delay;
				}
				delay += ball_step_delay;
			}
			break;
		case 4:
			for (int Y = MAP_SIZE_H - 1; Y >= 0; Y--) {
				count++;
				if (count >= 2) {
					count = 0;
				}
				for (int X = 0; X < MAP_SIZE_W; X++) {
					if(!MAP[Y][X]) break;
					num = MAP_BALL[Y][X];
					ball[num].B_DELAY = delay;
				}
				delay += ball_step_delay;
			}
			break;
		}
	}
	
	private int ballIntColors[];
	public float ballFloatColors[][];
	public Color ballColors[];
	
	private void loadBallColors(){
		String s[] = assets.loadStringRowsFromFile("ball_colors.txt");
		int count = s.length;
		ballIntColors = new int[count + 1];
		ballFloatColors = new float[count + 1][];
		ballColors = new Color[count + 1];
		
		int i = 0;
		while(i < s.length){
			ballIntColors[i + 1] = Integer.parseInt(s[i], 16);
			ballFloatColors[i + 1] = getFloatColor(ballIntColors[i + 1]);
			ballColors[i + 1] = Color.valueOf(s[i]);
			i++;
		}
	}
	
	private boolean bLoadGame;
	private boolean bReloadAfterNoMoves;
	
	private int addManna(int spr, int amount){
		Pers p_passive = step == Pers.HERO ? enemy : hero;
		Pers p_active = step == Pers.HERO ? hero : enemy;
		int points = amount;
		switch(spr){
		case P_SKULL:
			if(step == Pers.MONSTER){
				if(p_passive.getSkillStep(Skill.HERO_DODGE) > 0) break;
			}
			if(p_active.bProvocation || p_passive.bProvocation) points <<= 1;
			p_passive.addHealth(-points);
			if(step == Pers.HERO){
				hero.causeDamage += points;
				runAchievementsDone(ACH_ID_CAUSE_DAMAGE);
			}
			break;
		case P_FIRE:
			points += hero.getFire(true) / 5;
			p_active.setManna(Pers.MN_FIRE, points);
			break;
		case P_WATER:
			points += hero.getWater(true) / 5;
			p_active.setManna(Pers.MN_WATER, points);
			break;
		case P_AIR:
			points += hero.getAir(true) / 5;
			p_active.setManna(Pers.MN_AIR, points);
			if(step == Pers.HERO){
				if(p_active.getSkillStep(Skill.HERO_ACCELERATION) > 0){
					bDoneStep = false;
				}
			}
			break;
		case P_EARTH:
			points += hero.getEarth(true) / 5;
			p_active.setManna(Pers.MN_EARTH, points);
			break;
		case P_MONEY:
			p_active.setMoney(p_active.getMoney() + points);
			break;
		case P_EXP:
			p_active.setExp(p_active.getExp() + points);
			break;
		}
		return points;
	}

	private boolean ballsInvert(int x, int y, int dx, int dy, boolean with_invert) {
		if (dx < 3 && dy < 3) {
			return false;
		}
		int num;
		if (with_invert && !bLoadGame) {
			int amount = dx == 0 ? dy : dx;
			if(amount >= 4){
				bDoneStep = false;
			}
			num = MAP_BALL[y - dy][x - dx];
			int spr = Uni.abs(ball[num].B_SPR);
			
			int add_point = addManna(spr, amount);//addHeroPoint(amount);
			addMess(add_point, x, y, dx, dy);
			if(spr == P_MONEY){
				runAchievementsDone(ACH_ID_COLLECT_MONEY);
			}
			if(amount == 4){
				hero.aCombo4++;
				runAchievementsDone(ACH_ID_COMBO4);
			}
			if(amount == 5){
				hero.aCombo5++;
				runAchievementsDone(ACH_ID_COMBO5);
			}
		}
		while (dx > 0 || dy > 0) {
			num = MAP_BALL[y - dy][x - dx];
			if (with_invert && ball[num].B_SPR > 0) {
				ball[num].B_SPR = -ball[num].B_SPR;
			}
			dx = (dx == 0) ? 0 : dx - 1;
			dy = (dy == 0) ? 0 : dy - 1;
		}
		return true;
	}

	public void initBalls() {
		ball = null;
		initBallTypeSet();
		for (int y = 0; y < MAP_SIZE_H; y++) {
			for (int x = 0; x < MAP_SIZE_W; x++) {
				setBallParam(x, y);
			}
		}
	}

	private void nextGameLevel() {
		curLevel++;
		loadGame();
	}

	private final void loadGame() {
		if (curLevel >= maxLevel) {
			curLevel = maxLevel - 1;
		}
		bPlayedLevel[curLevel] = true;
		preloadGame(false);

		mathLoader();
	}

	public int paddingX;

	public void initPadding() {
		int W = 0;
		int max_map_w = MAP_W;
		if(mapImgW == 0){
			mapImgW = MAP_W;
			mapImgH = MAP_H;
		}
		if (max_map_w < mapImgW) max_map_w = mapImgW;
		W += max_map_w;// + butMenuW;
		int a = 2;//4;
		paddingX = (width - W) / a;
		MAP_CENTER_X = paddingX + (max_map_w >> 1);
		MAP_CENTER_Y = height >> 1;
		MAP_X = MAP_CENTER_X - (MAP_W >> 1);
		MAP_Y = MAP_CENTER_Y - (MAP_H >> 1);
		int x = MAP_CENTER_X - (mapImgW >> 1);
		int y = MAP_CENTER_Y - (mapImgH >> 1);
		rectMap.set(x, y, mapImgW, mapImgH);
		//x = width - (paddingX >> 1) - (butMenuW >> 1);
		//y = height - (paddingX >> 1) - (butMenuH >> 1);
		x = width - butMenuW;
		y = height - butMenuH;
		rectButMenu.set(x, y, butMenuW, butMenuH);
		
	}
	
	public void initHealthBar(int w, int h){
		hudHealthW = w;
		hudHealthH = h;
	}

	private void preloadGame(boolean b_restart) {
		bDoneCurLevel = bDoneLevel[curLevel];
		bDoneStep = false;
		bLoadGame = true;
		freeGameTweens();
		timerCursor = 0;
		messPoint = null;
		if (!bReloadAfterNoMoves) {
			heroSteps = 0;
			heroTime = 0;
		}
		bCursor = false;
		bFinish = false;
		bResult = false;
		bNoMoves = false;
		bEarnAchievment = false;
	}
	
	private void freeGameTweens(){
		tweenManager.killTarget(twTutorialHand);
	}

	private void mathGame() {
		float dt = getDTime();
		mathGameMessage(dt);
		mathBall();
		mathMess();
		mathBoomEffect();
		mathHud(dt);
		
		if (bNoMoves) {
			return;
		}
		mathTime();
		mathCursor();

		mathFinish();
		mathWindowNewLevel();
		mathResult(dt);
		mathCheckNoSteps();
		mathChangePersStep();
		mathEnemyStep();		
	}
	
	private void mathChangePersStep(){
		if(bDoneStep && !bBallMoving && gameMessage == null){
			bDoneStep = false;
			
			boolean b_change = true;
			
			Pers p = (step == Pers.HERO) ? hero : enemy;
			int v;
			for(int i = 0; i < Skills.maxSkills[step]; i++){
				if(p.getSkillStep(i) > 0){
					p.setSkillStep(i, p.getSkillStep(i) - 1);
					if(step == Pers.HERO){
						switch(i){
						case Skill.HERO_PRAYER:
							v = 1;
							p.addHealth(v);
							break;
						case Skill.HERO_FIG:
							b_change = false;
							break;
						}
					}
					else{
						switch(i){
						case Skill.MONSTER_ROOTS:
							b_change = false;
							break;
						case Skill.MONSTER_SONG:
							v = 1 + p.getManna(Pers.MN_AIR) / 5;
							hero.addHealth(v);
							break;
						case Skill.MONSTER_REGENARATION:
							v = 2;
							p.addHealth(v);
							break;
						}
					}
				}
			}
			
			if(b_change){
				step = (step == Pers.HERO) ? Pers.MONSTER : Pers.HERO;
				
				if(step == Pers.MONSTER) heroSteps++;
			}
		}		
	}
	
	private boolean bDoneStep;
	
	private void mathEnemyStep(){
		if(step == Pers.HERO) return;
		if(bNoMoves || bResult) return;
		if(bBallMoving || gameMessage != null) return;
		if(bDoneStep) return;
		
		boolean b_change_ball = true;
		if(Uni.random(0, 1) == 0){
			int arr[] = new int[Pers.maxSkillsGameBox];
			int a = 0;
			for(int i = 0; i < Pers.maxSkillsGameBox; i++){
				int i_skill = enemy.skillsGameBox[i];
				if(i_skill >= 0 && (enemy.isSkillCanUseInGame(i_skill))){
					arr[a++] = i;
					//useSkillInGame(hero, i_skill, i);
				}
			}
			if(a > 0){
				int i_skill_game_box = Uni.random(0, a - 1);
				int i_skill = enemy.skillsGameBox[i_skill_game_box];
				useSkillInGame(enemy, i_skill, i_skill_game_box);
				b_change_ball = false;
			}
		}
		if(b_change_ball){
			int arr[] = getRandomChangeBalls();
			changeBallsByUser(arr[0], arr[1], arr[2] - arr[0], arr[3] - arr[1], false);
		}
	}
	
	private void mathFinish(){
		if(bFinish) return;
		if(bBallMoving) return;
		if(hero.getHealth() <= 0 || enemy.getHealth() <= 0){
			bFinish = true;
			bWin = enemy.getHealth() <= 0;
			if(bWin){
				bDoneLevel[curLevel] = true;
				hero.aWinFights++;
			}
			else{
				hero.setLife(hero.getLife() - 1);
				hero.aLoseFights++;
				decrOutfitStrength();
			}
			initResult();
		}
	}
	
	private void decrOutfitStrength(){
		int index;
		Outfit o;
		for(int i = aBodyPart - 1; i >= 0; i--){
			index = hero.bodyIndex[i];
			if(index < 0) continue;
			o = hero.bagItems[index];
			o.setCurStrength(o.getCurStrength() - 1);
		}
	}
	
	private void mathResult(float delta) {
		if(!bFinish) return;
		if(runWindowNewLevel())return;

		//if(!bWindowNewLevelClose) return;
		int arr[] = {
			ACH_ID_REACH_LVL,
			ACH_ID_WIN_FIGHTS,
			ACH_ID_LOSE_FIGHTS,
			ACH_ID_EARN_5_STARS
		};
		for(int i = arr.length - 1; i >= 0; i--){
			if(runAchievementsDone(arr[i])) return;
		}
		if(bResult) return;
		bResult = true;
		checkRecord();
		audio.playSound(bWin ? Audio.SND_LEVEL_WIN : Audio.SND_LEVEL_LOSE);
	}
	
	private int resX, resY, resW, resH;
	private int resPadd;
	private int resDH;
	private int resHH;
	
	private int resRowAmount;

	private String resTitle;
	private int resTitleW;
	private String resSteps;
	private int resStepsW;
	private int resStepsH;
	private int aResButtons;
	private ButtonResult resButton[];
	private int resButPaddDX;
	private int resButtonAllW;
	
	private void initFinishButtons(){
		int fontH = font.getHeight();
		int sh = fontH << 1, sw;
		
		strButEarnAchievment = TXT[13][4];
		sw = font.stringWidth(strButEarnAchievment) + (fontH << 1);
		rectButEarnAchievment.setSize(sw, sh);
		rectButEarnAchievment.setPosition(resX + (resW >> 1) - (sw >> 1), resY + resH);
		
		float x = resX + (resW >> 1) - (resButtonAllW >> 1);
		float y = resY + resH - resPadd - resButtonH;
		resButton = new ButtonResult[aResButtons];
		float delta_y = (float)resButtonH / 20;
		float delay = 0.0f;
		for(int i = 0; i < aResButtons; i++){
			resButton[i] = new ButtonResult();
			resButton[i].set(x, y - delta_y, resButtonW, resButtonH);
			Tween.to(resButton[i], KTweenAccessor.POSITION_Y, 1.0f).target(y + delta_y).repeatYoyo(-1, 0.0f).delay(delay).start(tweenManager);
			delay += 0.5f;
			x += resButtonW + resButPaddDX;
		}
		resButton[0].type = ButtonResult.RESTART;
		resButton[1].type = ButtonResult.MENU;
		if(!bWin || !isLevelOpen(curLevel + 1)){//curLevel + 1 >= aOpenLevel[curLocation]){
			resButton[2].type = ButtonResult.LEVEL;
		}
		else{
			resButton[2].type = ButtonResult.NEXT;
		}
	}
	
	private final int starMaxNumber = 5;
	private int resStarNumber;
	private int resStarPaddX;
	private int resStarAllW;
	
	
	private void initResultStarsNumber(){
		resStarNumber = 0;
		if(bWin){
			int min_step = 25 + (75 * curLevel / maxLevel);
			int min_health = 75 - (25 * curLevel / maxLevel);
			int proc_health = 100 * hero.getHealth() / hero.getHealthMax(true);
			resStarNumber++;
			if(heroSteps <= min_step) resStarNumber++;
			if(proc_health >= min_health) resStarNumber++;
			if(bDoneCurLevel){
				float time_per_step = heroTime / (float)heroSteps;
				float time_on_level = 5*60 + (3*60 * curLevel / maxLevel);
				if(time_per_step <= 30.0f) resStarNumber++;
				if(heroTime <= time_on_level) resStarNumber++;
			}
		}
		if(resStarNumber == starMaxNumber){
			hero.aEarn5Stars++;
		}
	}

	private void initResult() {
		initResultStarsNumber();
		
		resTitle = TXT[I_GAME][bWin ? 0 : 1];
		resSteps = Integer.toString(heroSteps);//TXT[I_GAME][2] + heroPoints;
		
		aResButtons = 3;
		int fontH = font.getHeight();
		resDH = fontH;
		resPadd = fontH;
		resButPaddDX = resButtonW >> 1;
		resStarPaddX = fontH >> 1;
		resHH = fontH;
		resRowAmount = 1;
		resStepsH = fontH;
		
		resH = ((resDH + resHH) * resRowAmount) + resStepsH + resDH + resButtonH + (resPadd << 1);
		if(bWin) resH += resDH + starH;
		
		resButtonAllW = -resButPaddDX;
		for (int i = 0; i < aResButtons; i++) resButtonAllW += resButtonW + resButPaddDX;
		
		int w = resButtonAllW + (resPadd << 1);
		resW = w;
		resTitleW = font.stringWidth(resTitle);
		w = resTitleW + (resPadd << 1);
		if (resW < w)
			resW = w;
		resStepsW = font.stringWidth(resSteps);
		w = resStepsW + (resPadd << 1);
		if (resW < w)
			resW = w;
		resStarAllW = -resStarPaddX;
		for(int i = 0; i < starMaxNumber; i++) resStarAllW += starW + resStarPaddX;
		w = resStarAllW + (resPadd << 1);
		if (resW < w) resW = w;
		resX = MAP_CENTER_X - (resW >> 1);
		resY = MAP_CENTER_Y - (resH >> 1);
		
		initResultStars();
		initFinishButtons();
	}
	
	public boolean runWindowNewLevel(){
		boolean b_new = false;
		while(hero.getLevel() < level_up.length && hero.getExp() >= level_up[hero.getLevel()][LU_EXP_TO_LEVEL]){
			int statFree = level_up[hero.getLevel()][LU_STAT_POINTS];
			winNewLevelStatFree += statFree;
			hero.setStatFree(hero.getStatFree() + statFree); 
			hero.setLevel(hero.getLevel() + 1);
			b_new = true;
		}
		if(!b_new) return false;
		initWindowNewLevel();
		setPaintItemPrev(paintItem);
		paintItem = I_NEW_LEVEL;
		return true;
	}
	
	//private boolean bWindowNewLevel;
	//private boolean bWindowNewLevelClose;
	private Rectangle rWinNewLevel = new Rectangle();
	private Vector2 vWinNewLevel = new Vector2();
	private Rectangle rWinNewLevelTitle = new Rectangle();
	private Vector2 vWinNewLevelTitle = new Vector2();
	private int winNewLevelStatFree;
	
	private void mathWindowNewLevel(){
		
	}
	
	private void initWindowNewLevel(){
		int fontH = font.getHeight();
		rWinNewLevel.width = width2;
		rWinNewLevel.height = height2;
		rWinNewLevelTitle.width = fontH << 2;
		rWinNewLevelTitle.height = fontH << 2;
		rWinNewLevel.x = width2 - (rWinNewLevel.width / 2);
		rWinNewLevel.y = height2 - (rWinNewLevel.height + (rWinNewLevelTitle.height / 2)) / 2;
		rWinNewLevelTitle.x = rWinNewLevel.x + (rWinNewLevel.width - rWinNewLevelTitle.width) / 2;
		rWinNewLevelTitle.y = rWinNewLevel.y - (rWinNewLevelTitle.height) / 2;
		
		rWinNewLevel.getCenter(vWinNewLevel);
		rWinNewLevelTitle.getCenter(vWinNewLevelTitle);
		
		int sh = fontH << 1;
		int sw_0 = font.stringWidth(strButOK) + (fontH << 1);
		int sw_1 = font.stringWidth(strButNewLevelImprove) + (fontH << 1);
		int dw = fontH;
		int ww = sw_0 + dw + sw_1;
		float x = rWinNewLevel.x + (rWinNewLevel.width - ww) / 2;
		float y = rWinNewLevel.y + rWinNewLevel.height;
		rButNewLevelOK.setSize(sw_0, sh);
		rButNewLevelOK.setPosition(x, y);
		
		x += sw_0 + dw;
		rButNewLevelImprove.setSize(sw_1, sh);
		rButNewLevelImprove.setPosition(x, y);
	}
	
	private String strButNewLevelImprove;
	private Rectangle rButNewLevelImprove = new Rectangle();
	private String strButOK;
	private Rectangle rButNewLevelOK = new Rectangle();
	
	private void drawWindowNewLevel(Graphics g){
		paint(g, getPaintItemPrev());
		int fontH = font.getHeight();
		float x = rWinNewLevel.x;
		float y = rWinNewLevel.y;
		float w = rWinNewLevel.width;
		float h = rWinNewLevel.height;
		drawNinePatch(g, npSubstrate, Color.GRAY, x, y, w, h);
		g.fillRect(Color.WHITE, rWinNewLevelTitle);
		x = vWinNewLevelTitle.x;
		y = vWinNewLevelTitle.y;
		font.drawString(g, Integer.toString(hero.getLevel()), clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		
		x = vWinNewLevel.x;
		y = rWinNewLevelTitle.y + rWinNewLevelTitle.height + fontH;
		font.drawString(g, TXT[13][6], Color.WHITE, x, y, Uni.HCENTER_VCENTER);
		y = vWinNewLevel.y + ((int)rWinNewLevelTitle.height >> 2) + (fontH >> 1);
		font.drawString(g, TXT[13][7] + winNewLevelStatFree, Color.WHITE, x, y, Uni.HCENTER_VCENTER);
		drawButtonText(g, strButOK, rButNewLevelOK, false);
		drawButtonText(g, strButNewLevelImprove, rButNewLevelImprove, false);
	}
	
	private String strButApply;
	private Rectangle rectButApply = new Rectangle();
	
	private void runImproveStats(boolean back){
		if(!back) setPaintItemPrev(paintItem);
		paintItem = I_IMPROVE_STATS;
		cursorPersInfoCommon = -1;
		initImproveStats();
	}
	
	private void initImproveStats(){
		int fontH = font.getHeight();
		int sh = fontH << 1;
		strButApply = TXT[13][3];
		int sw = font.stringWidth(strButApply) + (fontH << 1);
		rectButApply.setSize(sw, sh);
		rectButApply.setPosition(width - sw, height - sh);
		
		int x = width >> 2;
		int y = (fontH << 1) + (fontH);
		int dx = fontH >> 1;
		int dy = fontH << 1;
		int max_sw = 0;
		int index;
		String t[] = TXT[12];
		String s;
		for(int i = 0; i < aPersInfoCommon; i++){
			index = persInfoCommon[i];
			s = Integer.toString(hero.getStat(index));
			sw = getPersInfoParamW(t[index], s, font_mini, dx);
			if(max_sw < sw) max_sw = sw;
		}
		
		if(rButImproveStat == null){
			rButImproveStat = new Rectangle[aPersInfoCommon];
			for(int i = 0; i < aPersInfoCommon; i++, y += dy){
				rButImproveStat[i] = new Rectangle();
				rButImproveStat[i].setSize(fontH, fontH);
				rButImproveStat[i].setPosition(x + max_sw + fontH, y - (fontH >> 1));
			}
		}
	}
	
	private void mathImproveStats(){
		
	}
	
	private boolean drawImproveStats(Graphics g){
		drawBackImage(g);
		int fontH = font.getHeight();
		String s = TXT[15][0]+": "+hero.getStatFree();
		int x = width2;
		int y = fontH;
		font.drawString(g, s, clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		x = width >> 2;
		y += fontH << 1;
		drawPersInfoCommon(g, x, y, fontH >> 1, fontH << 1, true);
		
		drawButtonText(g, strButApply, rectButApply, false);
		return true;
	}
	
	private boolean bEarnAchievment;
	private String strButEarnAchievment;
	private Rectangle rectButEarnAchievment = new Rectangle();
	
	private boolean initEarnAchievment(){
		bEarnAchievment = true;
		return true;
	}
	
	private void drawEarnAchievment(Graphics g){
		if(!bEarnAchievment) return;
		drawNinePatch(g, npSubstrate, clrSubstrate, resX, resY, resW, resH);
		g.fillRect(clrMenuItemPassive, rectButEarnAchievment);
		font.drawString(g, strButEarnAchievment, rectButEarnAchievment.x + (rectButEarnAchievment.width / 2), rectButEarnAchievment.y + (rectButEarnAchievment.height / 2), Uni.HCENTER_VCENTER);
	}
		
	private KTween twResultStars[];
	
	TweenCallback callStarGoldComplete = new TweenCallback() {
		@Override
		public void onEvent(int arg0, BaseTween<?> arg1) {
			Tween tween = (Tween)arg1;
			KTween target = (KTween)tween.getTarget();
			audio.playSound(Audio.SND_STAR);
			Tween.to(target, KTweenAccessor.POSITION_Y, 0.2f).target(target.y - (CELL_H >> 3)).repeatYoyo(1, 0.0f).start(tweenManager);
		}
	};
	//private float starAlpha[];
	
	private void initResultStars(){
		if(!bWin) return;
		if(twResultStars == null){
			twResultStars = new KTween[starMaxNumber];
			for(int i = 0; i < starMaxNumber; i++){
				twResultStars[i] = new KTween();
			}
		}
		int x = resX + (resW - resStarAllW >> 1);
		int y = resY + resPadd + resHH + resDH;
		float delta = 1.0f;
		float dt_step = 0.5f;
		for(int i = 0; i < starMaxNumber; i++){
			tweenManager.killTarget(twResultStars[i]);
			Tween.set(twResultStars[i], KTweenAccessor.POSITION_XY).target(x, y).start(tweenManager);
			Tween.set(twResultStars[i], KTweenAccessor.ALPHA).target(0.0f).start(tweenManager);
			if(i < resStarNumber){
				Tween.to(twResultStars[i], KTweenAccessor.ALPHA, dt_step).target(1.0f).delay(delta).setCallback(callStarGoldComplete).setCallbackTriggers(TweenCallback.COMPLETE).start(tweenManager);
			}
			x += starW + resStarPaddX;
			delta += dt_step;
		}
	}
	
	private void drawResult(Graphics g) {
		if (!bResult) {
			return;
		}
		drawNinePatch(g, npSubstrate, clrSubstrate, resX, resY, resW, resH);

		float x = resX + (resW >> 1);
		float y = resY + resPadd + (resHH >> 1);
		font.drawString(g, resTitle, clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		y += (resHH >> 1) + resDH;
		TextureRegion tr;
		Color clr = g.getColor();
		float sx, sy;
		if(bWin){
			for(int i = 0; i < starMaxNumber; i++){
				sx = twResultStars[i].x;
				sy = twResultStars[i].y;
				if(twResultStars[i].color.a < 1.0f){
					g.draw(trStarSilver, sx, sy);
				}
				if(twResultStars[i].color.a > 0.0f){
					g.setColor(twResultStars[i].color);
					g.draw(trStarGold, sx, sy);
					g.setColor(clr);
				}
			}
			y += starH;
		}
		y += -(resHH >> 1) + resDH + (resStepsH >> 1);
		font.drawString(g, "steps: "+resSteps, clrFontBrown, x, y, Uni.HCENTER_VCENTER);
		
		//g.fillRect(clrMenuItemPassive, rectButResultOK);
		//font.drawString(g, strButResultOK, rectButResultOK.x + (rectButResultOK.width / 2), rectButResultOK.y + (rectButResultOK.height / 2), Uni.HCENTER_VCENTER);
		for (int i = 0; i < aResButtons; i++) {
			tr = getResButtonTextureRegion(i);
			g.setColor(clr.r, clr.g, clr.b, resButton[i].color.a);
			if(tr != null) g.draw(tr, resButton[i].x, resButton[i].y);
		}
		g.setColor(clr);
	}
	
	private TextureRegion getResButtonTextureRegion(int i){
		switch(resButton[i].type){
		case ButtonResult.RESTART:
			return trResRestart;
		case ButtonResult.MENU:
			return trResMenu;
		case ButtonResult.NEXT:
			return trResNext;
		case ButtonResult.LEVEL:
			return trResLevel;
		}
		return null;
	}
	
	private int step;
	
	private final void drawBackGame(Graphics g){
		g.fillRect(clrBackGame, 0, 0, width, height);
	}

	private final void drawGame(Graphics g) {
		drawBackGame(g);
		drawMap(g);
		
		drawHud(g, hero, hudW >> 1);
		drawHud(g, enemy, width - (hudW >> 1));
		
		if(paintItem == I_MENU_PAUSE) return;
		
		drawBall(g);
		drawTutorial(g);
		drawBoomEffect(g);
		drawCursor(g);
		drawButtonMenuImage(g);
		drawMess(g);
		drawNoMoves(g);
		drawResult(g);
		//drawWindowNewLevel(g);
		drawEarnAchievment(g);
		drawGameMessage(g);
	}
	
	private void deleteBallsOnMap(int arr[][]){
		int x, y;
		int num, spr;
		int a_spr[] = new int[aBallType + 1];
		for(int i = 0; i < arr.length; i++){
			x = arr[i][0];
			y = arr[i][1];
			if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) continue;
			if(!MAP[y][x]) continue;
			num = MAP_BALL[y][x];
			spr = Uni.abs(ball[num].B_SPR);
			ball[num].B_SPR = -spr;
			ball[num].B_DELAY = 0.1f;
			a_spr[spr]++;
		}
		for(int i_spr = 1; i_spr < a_spr.length; i_spr++){
			if(a_spr[i_spr] > 0){
				int points = addManna(i_spr, a_spr[i_spr]);
				int a = points / a_spr[i_spr];
				int b = points - (a * (a_spr[i_spr] - 1));
				int count = 0;
				for(int i = 0; i < arr.length; i++){
					x = arr[i][0];
					y = arr[i][1];
					if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) continue;
					if(!MAP[y][x]) continue;
					num = MAP_BALL[y][x];
					spr = Uni.abs(ball[num].B_SPR);
					if(spr == i_spr){
						addMess(count == (a_spr[i_spr] - 1) ? b : a, x, y, 0, 0);
						count++;
					}
				}
			}
		}
	}
	
	private boolean deleteHorizontalLine(int x, int y){
		if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) return false;
		if(!MAP[y][x]) return false;
		int arr[][] = new int[MAP_SIZE_W][2];
		for(int i = 0; i < MAP_SIZE_W; i++){
			arr[i][0] = i;
			arr[i][1] = y;
		}
		deleteBallsOnMap(arr);
		return true;
	}
	
	private boolean deleteVerticalLine(int x, int y){
		if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) return false;
		if(!MAP[y][x]) return false;
		int arr[][] = new int[MAP_SIZE_H][2];
		for(int i = 0; i < MAP_SIZE_H; i++){
			arr[i][0] = x;
			arr[i][1] = i;
		}
		deleteBallsOnMap(arr);
		return true;
	}
	
	private boolean deleteSquare(int x, int y, int aw, int ah){
		if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) return false;
		if(!MAP[y][x]) return false;
		int arr[][] = new int[aw * ah][2];
		int count = 0;
		for(int iy = y - (ah >> 1); iy <= y + (ah >> 1); iy++){
			for(int ix = x - (aw >> 1); ix <= x + (aw >> 1); ix++){
				arr[count][0] = ix;
				arr[count][1] = iy;
				count++;
			}
		}
		deleteBallsOnMap(arr);
		return true;
	}
	
	private boolean changeToGem(int x, int y, int spr){
		if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) return false;
		if(!MAP[y][x]) return false;
		int num = MAP_BALL[y][x];
		int spr_base = ball[num].B_SPR;
		if(spr == spr_base) return false;
		int len = ball.length;
		for (int i = 0; i < len; i++) {
			if(ball[i] != null){
				if(ball[i].B_SPR == spr_base){
					ball[i].B_SPR = spr;
					ball[i].B_DELAY = 0.1f;
				}
			}
		}
		return true;
	}
	
	private int deleteGems(int spr){
		int len = ball.length;
		int amount = 0;
		for (int i = 0; i < len; i++) {
			if(ball[i] != null && ball[i].B_SPR == spr){
				ball[i].B_SPR = -spr;
				ball[i].B_DELAY = 0.1f;
				amount++;
			}
		}
		int points = addManna(spr, amount);
		int a = points / amount;
		int b = points - (a * (amount - 1));
		int count = 0;
		for (int i = 0; i < len; i++) {
			if(ball[i] != null && Uni.abs(ball[i].B_SPR) == spr){
				addMess(count == (amount - 1) ? b : a, ball[i].B_X, ball[i].B_Y, 0, 0);
				count++;
			}
		}
		return amount;
	}
	
	private int hudW;
	private int hudHealthW;
	private int hudHealthH;
	
	private void mathHud(float dt){
		hero.mathHudHealthW(hudHealthW);
		enemy.mathHudHealthW(hudHealthW);
		mathHudCurrentStepBlink(dt);
	}
	
	private int hudAvatarH;
	private int hudSkillY;
	private int hudPaddX, hudPaddY;
	private int hudMannaW, hudMannaH;
	
	private void initHud(){
		int fontH = font.getHeight();
		hudW = width - MAP_W >> 1;
		initHealthBar(hudW - (butPadd), fontH + (fontH >> 1));
		
		hudPaddX = fontH >> 2;
		hudPaddY = fontH >> 2;
		hudMannaW = hudW >> 2;
		hudMannaH = hudW >> 2;
		hudAvatarH = avatarH;
		hudSkillY = hudPaddY + hudAvatarH + hudPaddY + hudHealthH + hudPaddY + hudMannaH + hudPaddY + hudPaddY;
		
		mathHud(0);
	}
	
	private boolean touchUpHudHero(Vector3 touch){
		if(hero.skillUsingIndex >= 0) return false;
		int x = 0;
		int y = hudSkillY;
		int dh = itemSkillH + hudPaddY;
		for(int i = 0; i < Pers.maxSkillsGameBox; i++){
			if(touch.x >= x && touch.x < x + hudW && touch.y >= y && touch.y < y + dh){
				int i_skill = hero.skillsGameBox[i];
				if(i_skill >= 0 && (hero.isSkillCanUseInGame(i_skill))){
					useSkillInGame(hero, i_skill, i);
				}
				return true;
			}
			y += dh;
		}
		return false;
	}
	
	private void useSkillInGame(Pers p, int i_skill, int i_skill_game_box){
		int kind = p.getKind();
		int i_level = p.getSkillLevel(i_skill);
		int v;
		v = Skills.getParam(kind, i_skill, i_level, Skill.SP_FIRE);
		if(v > 0) p.setManna(Pers.MN_FIRE, -v);
		v = Skills.getParam(kind, i_skill, i_level, Skill.SP_WATER);
		if(v > 0) p.setManna(Pers.MN_WATER, -v);
		v = Skills.getParam(kind, i_skill, i_level, Skill.SP_AIR);
		if(v > 0) p.setManna(Pers.MN_AIR, -v);
		v = Skills.getParam(kind, i_skill, i_level, Skill.SP_EARTH);
		if(v > 0) p.setManna(Pers.MN_EARTH, -v);
		
		
		p.skillUsingIndex = i_skill;
		String title = Skills.skill[kind][i_skill].title;
		String txt = Skills.skill[kind][i_skill].description;
		addGameMessage(title, txt);
	}
	
	private int hudCurrentStepBlink;
	private int hudCurrentStepBlinkDir = 1;
	
	private void mathHudCurrentStepBlink(float dt){
		int min = 50;
		int max = 150;
		int speed = 200;
		hudCurrentStepBlink += hudCurrentStepBlinkDir * speed * dt;
		if(hudCurrentStepBlinkDir > 0){
			 if(hudCurrentStepBlink >= max){
				 hudCurrentStepBlink -= max - hudCurrentStepBlink;
				 hudCurrentStepBlinkDir = -hudCurrentStepBlinkDir;
			 }
		}
		if(hudCurrentStepBlinkDir < 0){
			 if(hudCurrentStepBlink <= min){
				 hudCurrentStepBlink += hudCurrentStepBlink - min;
				 hudCurrentStepBlinkDir = -hudCurrentStepBlinkDir;
			 }
		}
	}
	
	private void drawHudAvatar(Graphics g, Pers p, float x, float y, float w, float h){
		g.draw(trAvatar[p.getKind()][p.getAvatar()], x, y);
		int fontH = font.getHeight();
		font.drawString(g, p.nick, clrFontWhite, x + (w / 2), y + (fontH >> 1), Uni.HCENTER_VCENTER);
		if(step == p.getKind()){
			String clr_base = p.getKind() == Pers.HERO ? "00FF00" : "FF0000";
			Color clr1 = Color.valueOf(clr_base+Integer.toString(hudCurrentStepBlink, 16));
			Color clr2 = Color.valueOf(clr_base+"00");
			int d = avatarW >> 3;
			drawGradientBorder(g, x, y, w, h, d, clr1, clr2);
		}
	}
	
	private void drawHud(Graphics g, Pers p, int x){
		float y = hudPaddY;//(nickH >> 1);
		int avatarDX = hudW - avatarW >> 1;
		drawHudAvatar(g, p, x - (hudW >> 1) + avatarDX, y, avatarW, hudAvatarH);
		y += hudAvatarH + hudPaddY;
		drawHealthBar(g, p, x - (hudHealthW >> 1), y, hudPaddY);
		y += hudHealthH + hudPaddY;//fontH + (ballH >> 1);
		
		int X = x - (hudMannaW << 1);
		int type = 0, spr = 0;
		for(int i = 0; i < 4; i++){
			switch(i){
			case 0:
				type = Pers.MN_FIRE;
				spr = P_FIRE;
				break;
			case 1:
				type = Pers.MN_WATER;
				spr = P_WATER;
				break;
			case 2:
				type = Pers.MN_AIR;
				spr = P_AIR;
				break;
			case 3:
				type = Pers.MN_EARTH;
				spr = P_EARTH;
				break;
			}
			g.fillRect(ballColors[spr], X, y, hudMannaW, hudMannaH);
			font_mini.drawString(g, p.getManna(type)+"", clrFontWhite, X + (hudMannaW >> 1), y + (hudMannaH >> 1), Uni.HCENTER_VCENTER);
			X += hudMannaW;
		}
		
		y += hudMannaH + hudPaddY + hudPaddY;
		X = x - (hudW >> 1) + hudPaddX;
		int w = hudW - (hudPaddY >> 1);
		int h = itemSkillH;
		h -= h >> 2;
		y = drawSkills(g, p, X, hudSkillY, w, h, hudPaddY, true);
		y += hudMannaH >> 1;
		
		X = x - (hudW >> 1) + hudPaddX;
		g.draw(trIcoMoney, X, y - trIcoMoney.getRegionHeight() / 2);
		X += trIcoMoney.getRegionWidth();
		g.fillRect(ballColors[P_MONEY], X, y - (hudMannaH >> 1), hudMannaW, hudMannaH);
		font_mini.drawString(g, Integer.toString(p.getMoney()), clrFontWhite, X + (hudMannaW >> 1), y, Uni.HCENTER_VCENTER);
		y += hudMannaH + hudPaddY;
		
		X = x - (hudW >> 1) + hudPaddX;
		g.draw(trIcoExp, X, y - trIcoExp.getRegionHeight() / 2);
		X += trIcoExp.getRegionWidth();
		g.fillRect(ballColors[P_EXP], X, y - (hudMannaH >> 1), hudMannaW, hudMannaH);
		font_mini.drawString(g, Integer.toString(p.getExp()), clrFontWhite, X + (hudMannaW >> 1), y, Uni.HCENTER_VCENTER);
	}
	
	private float drawSkills(Graphics g, Pers p, float x, float y, int w, int h, int d, boolean b_with_close){
		int i_skill;
		for(int i = 0; i < Pers.maxSkillsGameBox; i++){
			g.fillRect(clrFontWhite, x - d, y - d, w + (d << 1), h + (d << 1));
			i_skill = p.skillsGameBox[i];
			if(i_skill >= 0){
				drawSkillItem(g, p.getKind(), p.skillsGameBox[i], x, y, w, h, b_with_close, !p.isSkillCanUseInGame(i_skill));
			}
			else{
				font_mini.drawString(g, "Skill "+(i + 1)+" -empty-", clrFontBrown, x + (w >> 1), y + (h >> 1), Uni.HCENTER_VCENTER);
			}
			y += h + (d << 1);
		}
		return y;
	}
	
	private void drawHealthBar(Graphics g, Pers p, float x, float y, int d){
		g.fillRect(Color.LIGHT_GRAY, x - d, y - d, hudHealthW + (d << 1), hudHealthH + (d << 1));
		g.fillRect(Color.GRAY, x, y, hudHealthW, hudHealthH);
		g.fillRect(Color.GREEN, x, y, p.hudHealthCurW, hudHealthH);
		font.drawString(g, p.getHealth() + "/" + p.getHealthMax(true), clrFontWhite, x + (hudHealthW >> 1), y + (hudHealthH >> 1), Uni.HCENTER_VCENTER);
	}
	
	private Color clrTutorial = new Color(0, 0, 0, 0.7f);
	private int tutorialPos[][];
	
	private void drawTutorial(Graphics g) {
		if(!bTutorial) return;
		g.fillRect(clrTutorial, 0, 0, width, height);
		int X, Y;
		
		Color clr = g.getColor();
		float x, y;
		for(int i = 0; i < tutorialPos.length; i++){
			X = tutorialPos[i][0];
			Y = tutorialPos[i][1];
			x = MAP_X + tutorialPos[i][2];
			y = MAP_Y + tutorialPos[i][3];
			int i_cell = (X + Y) & 0x1;
			if(MAP[Y][X]) g.draw(trCell[i_cell], x, y);
			ball[MAP_BALL[Y][X]].draw(g, clr);
		}
		
		float f_x_min = 0, f_x_max = 0, f_y_min = 0, f_y_max = 0;
		for(int i = 0; i < tutorialPos.length; i++){
			if(i == 0){
				f_x_min = f_x_max = tutorialPos[i][2];
				f_y_min = f_y_max = tutorialPos[i][3];
			}
			else{
				if(f_x_min > tutorialPos[i][2]) f_x_min = tutorialPos[i][2];
				if(f_x_max < tutorialPos[i][2]) f_x_max = tutorialPos[i][2];
				if(f_y_min > tutorialPos[i][3]) f_y_min = tutorialPos[i][3];
				if(f_y_max < tutorialPos[i][3]) f_y_max = tutorialPos[i][3];
			}
		}
		float fx = MAP_X + f_x_min;
		float fy = MAP_Y + f_y_min;
		float fw = f_x_max - f_x_min + CELL_W;
		float fh = f_y_max - f_y_min + CELL_H;
		drawNinePatch(g, npTutFrame, clrTutFrame, fx, fy, fw, fh);
		
		g.draw(trTutorialHand, MAP_X + twTutorialHand.x, MAP_Y + twTutorialHand.y);
	}
	
	private class TutorialHand extends KTween{
		
	}
	
	TutorialHand twTutorialHand;
	
	private void initTutorial(){
		if(!bTutorial) return;
		if(tutorialPos == null){
			tutorialPos = new int[2][4];
			twTutorialHand = new TutorialHand();
		}
		int arr[] = getRandomChangeBalls();
		setTutorialPos(0, arr[0], arr[1]);
		setTutorialPos(1, arr[2], arr[3]);
		
		Tween.set(twTutorialHand, KTweenAccessor.POSITION_XY).target(tutorialPos[0][2], tutorialPos[0][3] + (CELL_H >> 1)).start(tweenManager);
		Tween.to(twTutorialHand, KTweenAccessor.POSITION_XY, 1.5f).target(tutorialPos[1][2], tutorialPos[1][3] + (CELL_H >> 1)).repeatYoyo(-1, 0.3f).start(tweenManager);
	}
	
	private boolean canSwapBalls(int x, int y, int x2, int y2){
		if(!bTutorial) return true;
		boolean flag = true;
		for(int i = 0; i < tutorialPos.length; i++){
			if(x == tutorialPos[i][0] && y == tutorialPos[i][1] || x2 == tutorialPos[i][0] && y2 == tutorialPos[i][1]){
				
			}
			else{
				flag = false;
			}
		}
		if(flag) bTutorial = false;
		return flag;
	}
	
	private int[] getRandomChangeBalls(){
		int i = 0;
		int arr[][] = new int[MAP_SIZE_W * MAP_SIZE_H * 2][4];
		for (int Y = 0; Y < MAP_SIZE_H; Y++) {
			for (int X = 0; X < MAP_SIZE_W - 1; X++) {
				if (changeBall_StepsAvailable(X, Y, 1, 0)) {
					arr[i][0] = X;
					arr[i][1] = Y;
					arr[i][2] = X + 1;
					arr[i][3] = Y;
					i++;
				}
			}
		}
		for (int X = 0; X < MAP_SIZE_W; X++) {
			for (int Y = 0; Y < MAP_SIZE_H - 1; Y++) {
				if (changeBall_StepsAvailable(X, Y, 0, 1)) {
					arr[i][0] = X;
					arr[i][1] = Y;
					arr[i][2] = X;
					arr[i][3] = Y + 1;
					i++;
				}
			}
		}
		if(i == 0) return null;
		int index = MathUtils.random(0, i - 1);
		return arr[index];
	}
	
	private void setTutorialPos(int index, int x, int y){
		tutorialPos[index][0] = x;
		tutorialPos[index][1] = y;
		tutorialPos[index][2] = x * CELL_W;
		tutorialPos[index][3] = y * CELL_H;
	}
	
	//--------------------------- GAME BOOM PARTICLES ---------------
	
	private ParticleEffect protoBoom;
	private ParticleEffectPool boomEffectPool;
	Array<PooledEffect> boomEffects = new Array<PooledEffect>();
	
	private void loadBoomParticles(){
		protoBoom = new ParticleEffect();
		protoBoom.load(Gdx.files.internal(Assets.pack_path+"exp.pe"), atlasCommon);//Gdx.files.internal(Assets.pack_path));
		
		boomEffectPool = new ParticleEffectPool(protoBoom, 3, 50);
	}
	
	private void addBoomEffect(int X, int Y, int type){
		int num = MAP_BALL[Y][X];
		Ball b = ball[num];
		float x = MAP_X + b.B_XP + b.B_DX;
		float y = MAP_Y + b.B_YP + b.B_DY;
		PooledEffect effect = boomEffectPool.obtain();
		effect.setPosition(x, y);
		Array<ParticleEmitter> emitters = effect.getEmitters();
		int a = emitters.size;
		for(int i = 0; i < a; i++){
			emitters.get(i).getTint().setColors(ballFloatColors[type]);
			emitters.get(i).setAdditive(false);
		}
		boomEffects.add(effect);
	}
	
	private float[] getFloatColor(int clr){
		float res[] = new float[3];
		for(int i = 0, shift = 16; i < 3; i++, shift -= 8){
			res[i] = (float)((clr >> shift) & 0xff) / 255.0f;
		}
		return res;
	}
	
	private void mathBoomEffect(){
		for (int i = boomEffects.size - 1; i >= 0; i--) {
		    PooledEffect effect = boomEffects.get(i);
		    if (effect.isComplete()) {
		        effect.free();
		        boomEffects.removeIndex(i);
		    }
		}
	}
	
	private void drawBoomEffect(Graphics g){
		for (int i = boomEffects.size - 1; i >= 0; i--) {
		    PooledEffect effect = boomEffects.get(i);
		    effect.draw(g, getDTime());
		}
	}
	
	private void resetBoomEffect(){
		for (int i = boomEffects.size - 1; i >= 0; i--)
			boomEffects.get(i).free();
		boomEffects.clear();
	}
	
	//--------------------------- HUD ---------------------
	
	public void initMap() {
		parseMapFill();
		int size = (MAP_SIZE_W * MAP_SIZE_H >> 1);
		ball_change_index = new boolean[size];
		ball_change_limit = MAP_SIZE_W < MAP_SIZE_H ? MAP_SIZE_W : MAP_SIZE_H;
		ball_change = new int[size][ball_change_limit];

		MAP_W = MAP_SIZE_W * CELL_W;
		MAP_H = MAP_SIZE_H * CELL_H;
		
		MAP_BALL = new int[MAP_SIZE_H][MAP_SIZE_W];
		int count = 0;

		for (int y = 0; y < MAP_SIZE_H; y++) {
			for (int x = 0; x < MAP_SIZE_W; x++) {
				MAP_BALL[y][x] = count++;
			}
		}
	}
	
	private String map_pattern[][];
	
	private void loadMapPattern() {
		String s[] = assets.loadStringRowsFromFile("map.txt");
		int i = 0;
		int h;
		i = 0;
		int a = 0;
		String row[];
		while(i < s.length){
			row = s[i].split("\t");
			h = Integer.parseInt(row[0]);
			i += h + 1;
			a++;
		}
		maxLevel = a;
		map_pattern = new String[a][];
		i = 0;
		a = 0;
		while(i < s.length){
			row = s[i++].split("\t");
			h = Integer.parseInt(row[0]);
			map_pattern[a] = new String[h];
			for(int j = 0; j < h; j++){
				row = s[i+j].split("\t");
				map_pattern[a][j] = new String();
				for(int k = 0; k < row.length; k++){
					map_pattern[a][j] += row[k];//new String(s[i + j]);
				}
			}
			i += h;
			a++;
		}		
	}
	
	private void parseMapFill(){
		String p[] = map_pattern[curLevel % map_pattern.length];
		MAP_SIZE_H = p.length;
		MAP_SIZE_W = p[0].length();
		MAP = new boolean[MAP_SIZE_H][MAP_SIZE_W];
		for(int y = 0; y < MAP_SIZE_H; y++){
			for(int x = 0; x < MAP_SIZE_W; x++){
				MAP[y][x] = p[y].charAt(x) == '1';
			}
		}
	}
	
	public static MyFont font;
	public static MyFont font_mini;
	
	private void loadFont(){
		//BitmapFont bf = main.getManager().get(Assets.pack_path + Assets.fileFontSkranji);
		BitmapFont bf = main.getManager().get(Assets.fileFontOpenSans0);
		font = new MyFont(clrFontBrown, bf);
		BitmapFont bf2 = main.getManager().get(Assets.fileFontOpenSans1);
		font_mini = new MyFont(clrFontBrown, bf2);
		
		//font = new MyFont(clrFontBrown, Assets.fileFont);
	}

	public int ballW;
	public int ballH;
	public TextureRegion trBall[];

	private TextureAtlas atlasCommon;
	
	public final void loadAtlasCommon() {
		try {
			try {
				atlasCommon = main.getManager().get(Assets.pack_path + Assets.fileAtlasCommon);
			} catch (Exception ex) {}
		} catch (OutOfMemoryError e) {}		
		initAtlasCommon(atlasCommon);
	}
	
	private final void setCellScale(){
		cellScale = 1.0f;
		//if(CELL_H * MAP_SIZE_H_MAX > height){
			float cell_h = (float)height / (float)MAP_SIZE_H_MAX;
			cellScale = cell_h / (float) CELL_H;
			CELL_W *= cellScale;
			CELL_H *= cellScale;
//		}
	}
	
	public final void initAtlasCommon(TextureAtlas atlas) {
		if(atlas == null) return;
		trBall = new TextureRegion[aBallType + 1];
		for(int j = 1; j <= aBallType; j++){
			trBall[j] = atlas.findRegion("ball"+j);
			if(ballW < trBall[j].getRegionWidth()) ballW = trBall[j].getRegionWidth();
			if(ballH < trBall[j].getRegionHeight()) ballH = trBall[j].getRegionHeight();
		}
		CELL_W = ballW;
		CELL_H = ballH;
		trCell = new TextureRegion[aCellImg];
		for(int i = 0; i < aCellImg; i++){
			trCell[i] = atlas.findRegion(stuff_cell+i);
			if(i == 0 && trCell[i] != null){
				CELL_W = trCell[i].getRegionWidth();
				CELL_H = trCell[i].getRegionHeight();				
			}
		}
		setCellScale();
				
		trStarGold = atlas.findRegion(stuff_star0);
		trStarSilver = atlas.findRegion(stuff_star1);
		starW = trStarGold.getRegionWidth();
		starH = trStarGold.getRegionHeight();
		trResNext = atlas.findRegion(stuff_res_next);
		resButtonW = trResNext.getRegionWidth();
		resButtonH = trResNext.getRegionHeight();
		trResLevel = atlas.findRegion(stuff_res_level);
		trResMenu = atlas.findRegion(stuff_res_menu);
		trResRestart = atlas.findRegion(stuff_res_restart);
		trCursor = new TextureRegion[aCursorImg];
		for(int i = 0; i < aCursorImg; i++) trCursor[i] = atlas.findRegion(stuff_cursor+i);
		trButMenu = atlas.findRegion(stuff_but_menu);
		butMenuW = trButMenu.getRegionWidth();
		butMenuH = trButMenu.getRegionHeight();
		trButBack = atlas.findRegion(menu_button_back);
		butBackW = trButBack.getRegionWidth();
		butBackH = trButBack.getRegionHeight();
		rectButBack = new Rectangle();//new Rectangle(x, y, butBackW, butBackH);
		rectButBack.setSize(butBackW, butBackH);
		butPadd = butBackH / 4;
		trMusicOn = atlas.findRegion(menu_music_on);
		trMusicOff = atlas.findRegion(menu_music_off);
		trSoundOn = atlas.findRegion(menu_sound_on);
		trSoundOff = atlas.findRegion(menu_sound_off);
		butMainMenuMiniW = trMusicOn.getRegionWidth();
		butMainMenuMiniH = trMusicOn.getRegionHeight();
		int a = menu_star.length;
		trStarMini = new TextureRegion[a];
		for(int i = 0; i < a; i++) trStarMini[i] = atlas.findRegion(menu_star[i]);
		starMiniW = trStarMini[0].getRegionWidth();
		starMiniH = trStarMini[0].getRegionHeight();
		trSelLevBox = atlas.findRegion(menu_box_lev);
		trSelLevBoxLock = atlas.findRegion(menu_box_lev_lock);
		trMenuLock = atlas.findRegion(menu_lock);
		boxLevW = trSelLevBox.getRegionWidth();
		boxLevH = trSelLevBox.getRegionHeight();
		trDialogOK = atlas.findRegion(menu_dialog_ok);
		trDialogCancel = atlas.findRegion(menu_dialog_cancel);
		dialogButtonW = trDialogOK.getRegionWidth();
		dialogButtonH = trDialogOK.getRegionHeight();
		trTutorialHand = atlas.findRegion(tutorial_hand);
		trParticle = atlas.findRegion(file_name_particle);
		trGradient = atlas.findRegion(stuff_gradient);
		trIcoMoney = atlas.findRegion(stuff_ico_money);
		trIcoCash = atlas.findRegion(stuff_ico_cash);
		trIcoExp = atlas.findRegion(stuff_ico_exp);
		trFBLogin = atlas.findRegion(stuff_fb_login);
		trFBLogout = atlas.findRegion(stuff_fb_logout);
	}
	
	private TextureAtlas atlasAvatar;
	
	public final void loadAtlasAvatar() {
		try {
			try {
				atlasAvatar = main.getManager().get(Assets.pack_path + Assets.fileAtlasAvatar);
			} catch (Exception ex) {}
		} catch (OutOfMemoryError e) {}		
		initAtlasAvatar(atlasAvatar);
	}
	
	private String avatar_str = "avatar";
	private TextureRegion trAvatar[][];
	private int aAvatar[] = {1, 1};
	private int avatarW, avatarH;
	
	private void initAtlasAvatar(TextureAtlas atlas) {
		trAvatar = new TextureRegion[Pers.aKind][];
		for(int i = 0; i < Pers.aKind; i++){
			int a = aAvatar[i];
			trAvatar[i] = new TextureRegion[a];
			for(int j = 0; j < a; j++){
				trAvatar[i][j] = atlas.findRegion(avatar_str+i+"-"+j);
				if(trAvatar[i][j] != null && i == Pers.HERO && j == 0){
					avatarW = trAvatar[i][j].getRegionWidth();
					avatarH = trAvatar[i][j].getRegionHeight();
				}
			}
		}
	}
	
	private TextureAtlas atlasBack;
	
	public final void loadAtlasBack() {
		try {
			try {
				atlasBack = main.getManager().get(Assets.pack_path + Assets.fileAtlasBack);
			} catch (Exception ex) {}
		} catch (OutOfMemoryError e) {}		
		initAtlasBack(atlasBack);
	}
	
	public final void initAtlasBack(TextureAtlas atlas) {
		int fontH = font.getHeight();
		int D = width < height ? width : height;
		mItemW = D - (D / 3);
		mItemH = fontH + (fontH << 1);
		mItemDH = mItemH / 4;
		mapImgW = mapImgH = 0;
		if(atlas != null){
			trMap = atlas.findRegion(stuff_map);
			if(trMap != null){
				mapImgW = trMap.getRegionWidth();
				mapImgH = trMap.getRegionHeight();
			}
			trMenuItem0 = atlas.findRegion(menu_item_active);
			trMenuItem1 = atlas.findRegion(menu_item_passive);
			if(trMenuItem1 != null){
				mItemW = trMenuItem1.getRegionWidth();
				mItemH = trMenuItem1.getRegionHeight();
				mItemDH = mItemH / 4;
			}
			trGoalBack = atlas.findRegion(stuff_goal_back);
			npLocTextBack = scale9(atlas.findRegion(loc_text_back));//atlasCommon.createPatch(loc_text_back);
			npLocTextBackMinW = npLocTextBack.getTotalWidth();
			npLocTextBackMinH = npLocTextBack.getTotalHeight();
			npSubstrate = scale9(atlas.findRegion(str_substrate));
			npTutFrame = scale9(atlas.findRegion(str_tut_frame));
		}
	}
	
	private TextureAtlas atlasLocIco;
	
	public final void loadAtlasLocIco() {
		try {
			try {
				atlasLocIco = main.getManager().get(Assets.pack_path + Assets.fileAtlasLocIco);
			} catch (Exception ex) {}
		} catch (OutOfMemoryError e) {}		
		initAtlasLocIco(atlasLocIco);
	}
	
	public final void initAtlasLocIco(TextureAtlas atlas) {
		loc_ico = new String[aSelLocItems];
		trLocIco = new TextureRegion[aSelLocItems];
		for(int i = 0; i < aSelLocItems; i++){
			loc_ico[i] = loc_ico_name + (i + 1);
			trLocIco[i] = atlas.findRegion(loc_ico[i]);
			if(locIcoW < trLocIco[i].getRegionWidth()) locIcoW = trLocIco[i].getRegionWidth();
			if(locIcoH < trLocIco[i].getRegionHeight()) locIcoH = trLocIco[i].getRegionHeight();
		}
	}
	
	private NinePatch scale9(AtlasRegion ar){
		if(ar == null) return null;
		ar.flip(false, true);
		return new NinePatch(ar, ar.splits[0], ar.splits[1], ar.splits[2], ar.splits[3]);
	}

	private String stuff_goal_back = "goal_back";
	private TextureRegion trGoalBack;
	private String stuff_cell = "cell";
	private TextureRegion trCell[];
	private String stuff_cursor = "cursor";
	private TextureRegion trCursor[];
	private String stuff_star0 = "res_star2";
	private TextureRegion trStarGold;
	private String stuff_star1 = "res_star1";
	private TextureRegion trStarSilver;
	private String stuff_res_next = "Forward-btn";
	private TextureRegion trResNext;
	private String stuff_res_level = "level-btn";
	private TextureRegion trResLevel;
	private String stuff_res_menu = "Menu-btn";
	private TextureRegion trResMenu;
	private String stuff_res_restart = "Reload-btn";
	private TextureRegion trResRestart;
	private String stuff_but_menu = "Pause-btn";
	private TextureRegion trButMenu;
	private String stuff_gradient = "gradient_sample";
	private TextureRegion trGradient;
	private String stuff_ico_money = "ico_money";
	private TextureRegion trIcoMoney;
	private String stuff_ico_cash = "ico_cash";
	private TextureRegion trIcoCash;
	private String stuff_ico_exp = "ico_exp";
	private TextureRegion trIcoExp;
	private String stuff_fb_login = "fb_login_button";
	private TextureRegion trFBLogin;
	private String stuff_fb_logout = "fb_logout_button";
	private TextureRegion trFBLogout;
	private int butMenuW;
	private int butMenuH;
	
	private int starW;
	private int starH;
	private int resButtonW;
	private int resButtonH;
	
	private String menu_item_active = "die_choice";
	private TextureRegion trMenuItem0;
	private String menu_item_passive = "die";
	private TextureRegion trMenuItem1;
	private String menu_music_on = "music_on";
	private TextureRegion trMusicOn;
	private String menu_music_off = "music_off";
	private TextureRegion trMusicOff;
	private String menu_sound_on = "sound_on";
	private TextureRegion trSoundOn;
	private String menu_sound_off = "sound_off";
	private TextureRegion trSoundOff;
	private String menu_star[] = {"star", "star2", "star3"};
	private TextureRegion trStarMini[];
	private String menu_lock = "lock";
	private TextureRegion trMenuLock;
	private String menu_button_back = "Back";
	private TextureRegion trButBack;
	private String menu_box_lev = "box";
	private TextureRegion trSelLevBox;
	private String menu_box_lev_lock = "box_lock";
	private TextureRegion trSelLevBoxLock;
	private String menu_dialog_ok = "ok";
	private TextureRegion trDialogOK;
	private String menu_dialog_cancel = "x";
	private TextureRegion trDialogCancel;
	private String tutorial_hand = "tut_hand";
	private TextureRegion trTutorialHand;
	private String file_name_particle = "particle";
	public TextureRegion trParticle;
	private String loc_ico_name = "l";
	private String loc_ico[];
	private TextureRegion trLocIco[];
	private String loc_text_back = "p";
	private NinePatch npLocTextBack;
	private float npLocTextBackMinW;
	private float npLocTextBackMinH;
	private String str_substrate = "substrate";
	private NinePatch npSubstrate;
	private String str_tut_frame = "tut_frame";
	private NinePatch npTutFrame;
	private int mItemW;
	private int mItemH;
	private int mItemDH;
	private int butBackW;
	private int butBackH;
	private int butPadd;
	private int butMainMenuMiniW;
	private int butMainMenuMiniH;
	private int starMiniW;
	private int starMiniH;
	private int boxLevW;
	private int boxLevH;
	private int dialogButtonW;
	private int dialogButtonH;
	private int locIcoW;
	private int locIcoH;
	private int locTextPadd;
/*
	private void initCellW(int v){
		if(CELL_W < v) CELL_W = v;
	}
	
	private void initCellH(int v){
		if(CELL_H < v) CELL_H = v;
	}
	*/
	private final void drawButtonBack(Graphics g){
		g.draw(trButBack, rectButBack.x, rectButBack.y);
	}

	private final void drawButtonBack(Graphics g, float x, float y) {
		g.draw(trButBack, x, y);
	}

	private Image backImg;
	private float backImgX;

	public final void loadBackImage() {
		if(backImg != null) return;
		try {
			try {
				backImg = new Image((Texture)Main.main.getManager().get(Assets.res_path + Assets.fileTextureBack));//Image.createImagePJ("back");
			} catch (Exception e) {}
		} catch (OutOfMemoryError e) {}
	}

	private void drawBackImage(Graphics g) {
		if (backImg != null) {
			g.drawImage(backImg, 0, 0, width, height);
		}
	}
	
	private void mathTime() {
		if (bResult) {
			return;
		}
		float dt = getDTime();
		if(!bTutorial) heroTime += dt;		
	}
	
	private boolean bNoMoves;

	private void mathCheckNoSteps() {
		if (bNoMoves) {
			return;
		}
		if (bBallMoving || bResult) {
			return;
		}
		if(ball == null) return;
		for(int i = ball.length - 1; i >= 0; i--){
			if(ball[i] == null) continue;
			if(ball[i].B_SPR == 0) return;
			if(ball[i].B_DX != 0) return;
			if(ball[i].B_DY != 0) return;
			if(ball[i].B_DELAY != 0) return;
			if(ball[i].B_ANIM_APPEAR > 0) return;
		}
		for (int Y = 0; Y < MAP_SIZE_H; Y++) {
			for (int X = 0; X < MAP_SIZE_W - 1; X++) {
				if (changeBall_StepsAvailable(X, Y, 1, 0)) {
					return;
				}
			}
		}
		for (int X = 0; X < MAP_SIZE_W; X++) {
			for (int Y = 0; Y < MAP_SIZE_H - 1; Y++) {
				if (changeBall_StepsAvailable(X, Y, 0, 1)) {
					return;
				}
			}
		}
		bNoMoves = true;
	}

	private boolean changeBall_StepsAvailable(int x, int y, int dir_x, int dir_y) {
		int arr[] = new int[ball_change_limit];
		boolean flag = initBallChangeArray(x, y, dir_x, dir_y, arr);
		if(!flag) return false;
		changeBalls(arr, false, false);
		
		boolean b_match = false;
		int num;
		for(int j = 0; j < arr.length; j++){
			num = arr[j];
			if(num < 0) break;
			if (checkLine(ball[num].B_X, ball[num].B_Y, false)) {
				b_match = true;
			}
		}
		
		changeBalls(arr, false, true);
		return b_match;
	}
	
	private boolean checkLine(int x, int y, boolean with_invert) {
		boolean flag = false;
		int num;
		int curr;
		int prev;
		
		int X, Y;
		int CX, CY;
		for(int i = 0; i < 2; i++){
			if(i == 0 && y >= MAP_SIZE_H || i == 1 && x >= MAP_SIZE_W){
				continue;
			}
			curr = 0;
			prev = -1;
			CX = CY = 0;
			X = i == 0 ? 0 : x;
			Y = i == 1 ? 0 : y;
			while(X < MAP_SIZE_W && Y < MAP_SIZE_H){
				if(MAP[Y][X]){
					num = MAP_BALL[Y][X];
					curr = Uni.abs(ball[num].B_SPR);
					if(!ball[num].availableToChange() && !bLoadGame) curr = 0;
					if (curr == prev) {
						if(i == 0){
							CX += CX == 0 ? 2 : 1;
						}
						else{
							CY += CY == 0 ? 2 : 1;
						}
					}
				}
				else{
					curr = -1;
				}
				if(curr == 0){
					CX = CY = 0;
				}
				if(curr != prev){
					flag = ballsInvert(X, Y, CX, CY, with_invert) ? true : flag;
					CX = CY = 0;
				}
				prev = curr;
				if(i == 0) X++;
				else Y++;
			}
			flag = ballsInvert(X, Y, CX, CY, with_invert) ? true : flag;
		}
		return flag;
	}

	private int MODE;
	private int MD_CLASSIC = 0;
	private int MD_LINE = 1;
	private int MD_CLOCK = 2;
	
	private boolean MAP[][];
	private int MAP_BALL[][];
	private int MAP_SIZE_W_MAX = 8;
	private int MAP_SIZE_H_MAX = 8;
	private int MAP_SIZE_W;
	private int MAP_SIZE_H;
	private int MAP_CENTER_X;
	private int MAP_CENTER_Y;
	public int MAP_X;
	public int MAP_Y;
	private int MAP_W;
	private int MAP_H;
	public int CELL_W;
	public int CELL_H;
	public float cellScale;

	private final int aCellImg = 2;
	private final Color colorCells[] = {
			Color.valueOf("648795"),
			Color.valueOf("355261")
	};
	private float timerCursor;
	private int aCursorImg = 2;
	private int cursorX, cursorY;
	private int cursorXp, cursorYp;
	private boolean bCursor;
	private int cCursor;
	private boolean bKeyboard = false;
	
	public void setCursor(int x, int y) {
		cursorX = x;
		cursorY = y;
		cursorXp = (cursorX * CELL_W) + (CELL_W >> 1);
		cursorYp = (cursorY * CELL_H) + (CELL_H >> 1);
	}
	
	private void setupCursorAfterLoad(int x, int y){
		int aw = MAP_SIZE_W;
		while(!MAP[y][x]){
			x++;
			if(x >= MAP_SIZE_W) x = 0;
			aw--;
			if(aw == 0){
				aw = MAP_SIZE_W;
				y++;
				if(y >= MAP_SIZE_H) y = 0;
			}
		}
		setCursor(x, y);
	}

	private void drawCursor(Graphics g) {
		if (bResult || !bKeyboard)
			return;
		int index = bCursor ? 0 : cCursor;
		int x = MAP_X + cursorXp - (trCursor[index].getRegionWidth() >> 1);
		int y = MAP_Y + cursorYp - (trCursor[index].getRegionHeight() >> 1);
		g.draw(trCursor[index], x, y);
	}

	public void mathCursor() {
		float dt = getDTime();
		timerCursor += dt;
		float anim_speed = 1.0f;
		while (timerCursor > anim_speed) {
			timerCursor -= anim_speed;
			cCursor++;
			if (cCursor >= aCursorImg)
				cCursor = 0;
		}
	}

	private void keyMoveCursor(int dx, int dy) {
		if (bNoMoves || bResult) return;
		int xx = cursorX + dx;
		int yy = cursorY + dy;
		if (xx < 0 || xx >= MAP_SIZE_W || yy < 0 || yy >= MAP_SIZE_H) {
			return;
		}
		if (bCursor) {
			changeBallsByUser(cursorX, cursorY, dx, dy, true);
		}
		setCursor(cursorX + dx, cursorY + dy);
	}

	private int heroSteps;

	private void keyPressCursor() {
		/*if (bResult) {
			bResult = false;
			loadGame();
			return;
		}*/
		if (bCursor) {
			bCursor = false;
		} else {
			if(MAP[cursorY][cursorX]){
				int num = MAP_BALL[cursorY][cursorX];
				bCursor = ball[num].canSelect();
			}
		}
	}

	private int recordStep[];
	private int recordStar[];
	private int recordStarMax;

	private void checkRecord() {
		if (!bWin) {
			return;
		}
		if (recordStep[curLevel] < heroSteps) {
			recordStep[curLevel] = heroSteps;
		}
		if (recordStar[curLevel] < resStarNumber) {
			recordStar[curLevel] = resStarNumber;
		}
	}
		
	private int getRecordStar(){
		int a = 0;
		for(int i = 0; i < maxLevel; i++) a += recordStar[i];
		return a;
	}

	private float heroTime;
	
	private int ball_change[][];
	private boolean ball_change_index[];
	private int ball_change_limit;//2;
	
	private boolean initBallChangeArray(int x, int y, int dir_x, int dir_y, int arr[]){
		int n;
		int i = 0;
		int a;
		if(MODE == MD_CLOCK){
			a = 4;
			int move[][] = {{1, 0},{0, 1},{-1, 0},{0, -1}};
			int k = move.length - 1;
			for(; k >= 0; k--){
				if(move[k][0] == dir_x && move[k][1] == dir_y) break;
			}
			for(; i < a; i++){
				x += move[k][0];
				y += move[k][1];
				if(!MAP[y][x]) return false;
				if(x < 0 || y < 0 || x >= MAP_SIZE_W || y >= MAP_SIZE_H) return false;
				n = MAP_BALL[y][x];
				arr[i] = n;
				k++;
				if(k >= move.length) k = 0;
			}
		}
		else{
			if(MODE == MD_LINE){
				a = dir_x != 0 ? MAP_SIZE_W : MAP_SIZE_H;
			}
			else{
				a = 2;
			}
			for(; i < a; i++){
				if(!MAP[y][x]) return false;
				n = MAP_BALL[y][x];
				arr[i] = n;
				x += dir_x;
				if(x < 0) x = MAP_SIZE_W - 1;
				if(x >= MAP_SIZE_W) x = 0;
				y += dir_y;
				if(y < 0) y = MAP_SIZE_H - 1;
				if(y >= MAP_SIZE_H) y = 0;
			}
		}
		while(i < arr.length) arr[i++] = -1;
		return true;
	}
	
	private void changeBallsByUser(int x, int y, int dir_x, int dir_y, boolean you) {
		if(you){
			if(step == Pers.MONSTER || bDoneStep) return;
		}
		int nx = x + dir_x;
		int ny = y + dir_y;
		if(nx < 0 || ny < 0 || nx >= MAP_SIZE_W || ny >= MAP_SIZE_H) return;
		if(!canSwapBalls(x, y, nx, ny)) return;
		point_current = 1;
		bCursor = false;
		int index = addBallChangeIndex();
		boolean flag = initBallChangeArray(x, y, dir_x, dir_y, ball_change[index]);
		if(!flag){
			clearBallChangeIndex(index);
			return;
		}
		if(canChangeBalls(ball_change[index], index)){
			changeBalls(ball_change[index], true, false);
		}
		bDoneStep = true;
	}
	
	private boolean canChangeBalls(int arr[], int index){
		int i_ball;
		for(int i = 0; i < arr.length; i++){
			i_ball = arr[i];
			if(i_ball < 0) break;
			if(!ball[i_ball].availableToChange()){
				clearBallChangeIndex(index);
				return false;
			}
		}
		return true;
	}
	
	private Ball tmpBall = new Ball(this);

	private void changeBalls(int arr[], boolean piano, boolean back) {
		int len = 0;
		while(len < arr.length && arr[len] >= 0) len++;
		int num, num2 = 0;
		if(back){
			num = arr[0];
			tmpBall.copy(ball[num]);
			for (int i = 1; i < len; i++) {
				num2 = arr[i];
				ball[num].change(ball[num2], piano);
				num = num2;
			}
			ball[num].change(tmpBall, piano);
		}
		else{
			num = arr[len - 1];
			tmpBall.copy(ball[num]);
			for (int i = len - 2; i >= 0; i--) {
				num2 = arr[i];
				ball[num].change(ball[num2], piano);
				num = num2;
			}
			ball[num].change(tmpBall, piano);
		}
	}

	private void drawNoMoves(Graphics g) {
		if (!bNoMoves) {
			return;
		}
		String str = TXT[3][0];
		int fontH = font.getHeight();
		int padd = fontH << 1;
		int D = width < height ? width : height;
		D -= D >> 2;
		int w = D;
		if(isNinePatchLoaded(npSubstrate) && w < npSubstrate.getTotalWidth()) w = (int) npSubstrate.getTotalWidth();
		int w_text_max = w - (padd << 1);
		GlyphLayout gl = font.getGlyphLayout(str, w_text_max);
		int w_text = (int) gl.width;
		int h_text = (int) gl.height;
		int h = (padd << 1) + h_text + dialogButtonH + butPadd;
		if(isNinePatchLoaded(npSubstrate) && h < npSubstrate.getTotalHeight()) h = (int) npSubstrate.getTotalHeight();
		int x = MAP_CENTER_X - (w >> 1);
		int y = MAP_CENTER_Y - (h >> 1);
		drawNinePatch(g, npSubstrate, clrSubstrate, x, y, w, h);
		
		int xx = x + (w - w_text >> 1);
		int yy = y + padd;//h - h_text >> 1;
		font.drawWrapped(g, str, clrFontBrown, xx, yy, w_text, 1);
		
		xx = x + (w - dialogButtonW >> 1);
		yy = y + h - dialogButtonH - butPadd;
		g.draw(trDialogOK, xx, yy);
	}
	
	private void drawNinePatch(Graphics g, NinePatch np, Color clr, float x, float y, float w, float h){
		if(isNinePatchLoaded(np)){
			if(w < np.getTotalWidth()){
				x -= (np.getTotalWidth() - w) / 2;
				w = np.getTotalWidth();
			}
			if(h < np.getTotalHeight()){
				y -= (np.getTotalHeight() - h) / 2;
				h = np.getTotalHeight();
			}
			np.draw(g, x, y, w, h);
		}
		else{
			g.fillRect(clr, x, y, w, h);
		}
	}
	
	private void drawNinePatch2(Graphics g, NinePatch np, Color clr, float x, float y, float w, float h){
		if(isNinePatchLoaded(np)){
			np.draw(g, x, y, w, h);
		}
		else{
			g.fillRect(clr, x, y, w, h);
		}
	}
	
	private boolean isNinePatchLoaded(NinePatch np){
		return np != null && np.getTexture() != null; 
	}

	private void mathBall() {
		float dt = getDTime();
		bBallMoving = false;
		int num;
		for (int x = 0; x < MAP_SIZE_W; x++) {
			boolean b_column_move = false;
			for (int y = MAP_SIZE_H - 1; y >= 0; y--) {
				if(!MAP[y][x]) continue;
				num = MAP_BALL[y][x];
				int res = ball[num].math(dt, b_column_move);
				if((res & 0x1) != 0) bBallMoving = true;
				if((res & 0x2) != 0) b_column_move = true;
			}
		}

		for (int i = ball_change_index.length - 1; i >= 0; i--) {
			if (ball_change_index[i]) {
				boolean b_match = false;
				boolean b_move = false;
				for(int j = 0; j < ball_change[i].length; j++){
					num = ball_change[i][j];
					if(num < 0) break;
					if(ball[num].availableToChange()){
						if(checkLine(ball[num].B_X, ball[num].B_Y, false)){
							b_match = true;
						}
					}
					else{
						b_move = true;
					}
				}
				if(!b_move){
					clearBallChangeIndex(i);
					if(!b_match){
						changeBalls(ball_change[i], true, true);
					}
				}
			}
		}
		deleteAndShiftBalls(bBallMoving);
	}

	private void clearBallChangeIndex(int index) {
		ball_change_index[index] = false;
	}

	private int addBallChangeIndex() {
		for (int i = 0; i < ball_change_index.length; i++) {
			if (!ball_change_index[i]) {
				ball_change_index[i] = true;
				return i;
			}
		}
		return -1;
	}

	private boolean deleteAndShiftBalls(boolean ball_moving) {
		if (bReloadAfterNoMoves) {
			return false;
		}
		boolean flag = false;
		deleteBalls();
		// point_current = point_start;
		if (!ball_moving) {
			if (b_point_grow) {
				b_point_grow = false;
				point_current++;
			}
		}
		int num;
		for (int x = 0; x < MAP_SIZE_W; x++) {
			float delay = 0;
			for (int y = MAP_SIZE_H - 1; y >= 0; y--) {
				if(!MAP[y][x]) continue;
				num = MAP_BALL[y][x];
				if (ball[num].B_SPR == 0 && ball[num].B_DELAY == 0.0f) {
					flag = true;
					bBallMoving = true;
					shiftBallColumn(x, y, delay);
					delay += Ball.ball_step_delay;
				}
				
			}
		}
		return flag;
	}
	
	private void shiftBallColumn(int x, int y, float delay) {
		int num = MAP_BALL[y][x];
		int num2 = -1;
		int yy = 0;
		for(int i = y - 1; i >= 0; i--){
			yy++;
			if(!MAP[i][x]) break;
			if(ball[MAP_BALL[i][x]].B_SPR != 0){
				num2 = MAP_BALL[i][x];
				break;
			}
		}
		if(num2 < 0){
			setBallParam(x, y);
		}
		else{
			ball[num].copy(ball[num2]);
			ball[num2].reset();
			ball[num].shift(yy, !bLoadGame);
		}
		if(!bLoadGame) ball[num].B_DELAY += delay; 
	}

	private void setBallParam(int x, int y) {
		if(!MAP[y][x]) return;
		int num = MAP_BALL[y][x];
		if (ball == null) {
			ball = new Ball[MAP_SIZE_W * MAP_SIZE_H];
		}
		if(ball[num] == null){
			ball[num] = new Ball(this);
		}
		int index = Uni.random(0, aBallTypeSet - 1);
		int spr = ballTypeSet[index];
		//int start_spr = 1;
		//int spr = Uni.random(start_spr, start_spr + aBallType - 1);
		ball[num].init(spr, x, y);
		//ball[num].init(trBall[spr], x, y);
	}
	
	private int maxBallTypeOnLevel = 7;//6;
	private int ballTypeSet[];
	private int aBallTypeSet;
	
	private void initBallTypeSet(){
		if(ballTypeSet == null) ballTypeSet = new int[aBallType];
		int list[] = {P_SKULL};//listTypeGoal(curLevel);//goalPanel.listTypeGoalItemShowed();
		int a_list = list.length;
		int rest[] = new int[aBallType - a_list];
		int count = 0;
		for(int i = 1; i <= aBallType; i++){
			if(!isExistInArray(list, i)){
				rest[count++] = i;
			}
		}
		shuffleArray(rest);
		aBallTypeSet = a_list > maxBallTypeOnLevel ? a_list : maxBallTypeOnLevel;
		System.arraycopy(list, 0, ballTypeSet, 0, a_list);
		if(aBallTypeSet > a_list){
			System.arraycopy(rest, 0, ballTypeSet, a_list, aBallTypeSet - a_list);
		}
	}
	
	private boolean isExistInArray(int arr[], int value){
		for(int i = arr.length - 1; i >= 0; i--){
			if(arr[i] == value) return true;
		}
		return false;
	}
	
	private void shuffleArray(int[] ar) {
		for (int i = ar.length - 1; i > 0; i--) {
			int index = Uni.random(0, i - 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	private void deleteBalls() {
		boolean flag = checkForDeleteAll(true);
		if(flag && !bLoadGame) audio.playSound(Audio.SND_BOOM);
		int num;
		for (int y = 0; y < MAP_SIZE_H; y++) {
			for (int x = 0; x < MAP_SIZE_W; x++) {
				if(!MAP[y][x]) continue;
				num = MAP_BALL[y][x];
				if(ball[num].B_SPR < 0){
					setExplosion(x, y, -ball[num].B_SPR);
					ball[num].dispose();
					if(!bLoadGame) ball[num].B_DELAY = 0.15f;
				}
			}
		}
	}

	private boolean checkForDeleteAll(boolean with_invert) {
		boolean b_match = false;
		for (int i = 0; i < MAP_SIZE_W; i++) {
			if(checkLine(i, i, with_invert)){
				b_match = true;
			}
		}
		return b_match;
	}

	private final void setExplosion(int x, int y, int type) {
		if (bLoadGame) {
			return;
		}
		addBoomEffect(x, y, type);
	}

	private void restartGame() {
		paintItem = I_GAME;
		bReloadAfterNoMoves = bNoMoves;
		preloadGame(true);
		if (bReloadAfterNoMoves) {
			shuffleBalls();
			hero.resetManna();
			enemy.resetManna();
		} else {
			initMap();
			initBalls();
			initEnemy();
			initHero();
		}
		initStartGameBalls();
		
		bReloadAfterNoMoves = false;
		bLoadGame = false;
	}

	private void shuffleBalls() {
		int max = MAP_SIZE_W * MAP_SIZE_H;
		Ball tmp = new Ball(this);
		int r;
		for (int i = 0; i < max; i++) {
			Ball b = ball[i];
			if(b == null) continue;
			if (b.B_SPR == 0) continue;
			r = Uni.random(0, max - 1);
			while (r == i || ball[r] == null || ball[r].B_SPR == 0) {
				r++;
				if (r >= max) {
					r = 0;
				}
			}
			tmp.copy(b);
			b.init(ball[r].B_SPR, b.B_X, b.B_Y);
			ball[r].init(tmp.B_SPR, ball[r].B_X, ball[r].B_Y);
		}
	}

	// private Image mapImg;
	private String stuff_map = "map";
	private TextureRegion trMap;
	private int mapImgW;
	private int mapImgH;
	private Rectangle rectMap = new Rectangle();
	
	private void drawMap(Graphics g) {
		if (trMap != null && trMap.getTexture() != null) {
			g.draw(trMap, rectMap.x, rectMap.y);
		}
		for (int Y = 0, y = MAP_Y; Y < MAP_SIZE_H; Y++, y += CELL_H) {
			for (int X = 0, x = MAP_X; X < MAP_SIZE_W; X++, x += CELL_W) {
				int i_cell = (X + Y) & 0x1;
				if(MAP[Y][X]) g.draw(trCell[i_cell], x, y, CELL_W, CELL_H);
			}
		}
		drawGradientBorder(g, MAP_X, MAP_Y, MAP_W, MAP_H, CELL_W << 1, clrGradient[0], clrGradient[1]);
	}
	
	private final Color clrGradient[] = {
			Color.valueOf("000000c0"),
			Color.valueOf("00000000")
	};
	
	private final void drawGradientBorder(Graphics g, float x, float y, float w, float h, float d, Color clr1, Color clr2){
		g.drawGradient(trGradient, x, y, w, d, clr2, clr1, false);
		g.drawGradient(trGradient, x, y + h - d, w, d, clr1, clr2, false);
		g.drawGradient(trGradient, x, y, d, h, clr1, clr2, true);
		g.drawGradient(trGradient, x + w - d, y, d, h, clr2, clr1, true);
	}

	public final float pianoDecrement(float val, float delta, float target) {
		if (val != target) {
			float prev = val;
			val += delta;
			if(prev > target && val < target || prev < target && val > target){
				val = target;
			}
		}
		return val;
	}
}
