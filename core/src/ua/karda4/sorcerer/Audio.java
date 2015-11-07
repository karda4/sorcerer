package ua.karda4.sorcerer;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Audio {
	
	private Sound sounds[];
	public static boolean bSound;
	private Music musics[];
	public static boolean bMusic;

	public static final int SND_CLICK_MENU = 0;
	public static final int SND_BOOM = 1;
	public static final int SND_LEVEL_WIN = 2;
	public static final int SND_LEVEL_LOSE = 3;
	public static final int SND_WATER_DROP = 4;
	public static final int SND_STAR = 5;
	private final int aSounds = 6;

	public static final int MSC_MAIN_MENU = 0;
	public static final int MSC_GAME = 1;
	public static final int MSC_GAME2 = 2;
	private final int aMusics = 3;

	public Audio() {
		bSound = true;
		loadMusicAndSounds();
	}

	private void loadMusicAndSounds() {
		loadSound(SND_CLICK_MENU, Assets.fileSoundClick3);
		loadSound(SND_BOOM, Assets.fileSoundBang);
		loadSound(SND_LEVEL_WIN, Assets.fileSoundLevelWin);
		loadSound(SND_LEVEL_LOSE, Assets.fileSoundLevelLose);
		loadSound(SND_WATER_DROP, Assets.fileSoundResultStar);
		loadSound(SND_STAR, Assets.fileSoundStar);
		
		loadMusic(MSC_MAIN_MENU, Assets.fileMusicMenu1);
		loadMusic(MSC_GAME, Assets.fileMusicGame1);
		loadMusic(MSC_GAME2, Assets.fileMusicGame2);
	}
	
	private void loadSound(int i, String file){
		if(sounds == null) sounds = new Sound[aSounds];
		try{
			sounds[i] = Main.main.getManager().get(Assets.path_audio+file);//Gdx.audio.newSound(Gdx.files.internal(Assets.path_audio+file));
		}catch(Exception e){
			System.out.println("sound error #"+i+"("+file+"): "+e.toString());
		}
	}
	
	private void loadMusic(int i, String file){
		if(musics == null) musics = new Music[aMusics];
		try{
			musics[i] = Main.main.getManager().get(Assets.path_audio+file);//Gdx.audio.newMusic(Gdx.files.internal(Assets.path_audio+file));
		}catch(Exception e){
			System.out.println("music error #"+i+"("+file+"): "+e.toString());
		}
	}

	public void playSound(int i) {
		if (!bSound)
			return;
		if(sounds[i] == null) return;
		sounds[i].play();
	}
	
	private void disposeSounds(){
		for(int i = sounds.length - 1; i >= 0; i--) sounds[i].dispose();
	}

	private int curMusic = -1;

	public void playMusic(int i, boolean loop) {
		if (!bMusic)
			return;
		if (i == curMusic && musics[i].isPlaying())
			return;
		if(musics[i] == null) return;
		stopMusic();
		curMusic = i;
		musics[i].setLooping(loop);
		musics[i].play();
	}

	public void stopMusic() {
		if (curMusic < 0)
			return;
		musics[curMusic].stop();
	}

	public void pauseMusic() {
		if (curMusic < 0)
			return;
		musics[curMusic].pause();
	}
	
	private void disposeMusics(){
		for(int i = musics.length - 1; i >= 0; i--) musics[i].dispose();
	}
	
	public void dispose(){
		disposeSounds();
		disposeMusics();
	}
}
