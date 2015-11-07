package ua.karda4.sorcerer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

public class Assets {
	
	private AssetManager manager;
	
	public static boolean bUniscreen = true;
	public int W;
	public int H;
	public static int width;
	public static int height;
	public static int PACK_W;
	public static int PACK_H;
	private final static String pack_prepath = "pack";
	public static String pack_path;
	public static int RES_W;
	public static int RES_H;
	private final static String res_prepath = "res";
	public static String res_path;

	public final static String path_audio = "audio/";
	public final static String path_data = "data/";
	public final static String path_font = "font/";
	
	private final String pack_list[] = {"1024x720"};
	private final String res_list[] = {"1024x720"};
	
	public Assets(){
		manager = new AssetManager();
		initPack();
		W = bUniscreen ? Assets.PACK_W : Assets.width;
		H = bUniscreen ? Assets.PACK_H : Assets.height;
	}
	
	public AssetManager getManager() {
        return manager;
    }
	
	public void disposeManager(){
		manager.dispose();
	}
	
	public void initPack() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		
		boolean b_desktop = Gdx.app.getType() == ApplicationType.Desktop;
		
		find(b_desktop ? pack_list : getDirList(pack_prepath), 0);
		find(b_desktop ? res_list : getDirList(res_prepath), 1);
	}
	
	private static String[] getDirList(String strdir){
		FileHandle dir_pack;
		dir_pack = Gdx.files.internal(strdir);
		FileHandle list_pack[] = dir_pack.list();
		int size = list_pack.length;
		String list[] = new String[size];
		int ind;
		for(int i = 0; i < size; i++){
			list[i] = list_pack[i].toString();
			ind = list[i].lastIndexOf('/');
			if(ind >= 0) list[i] = list[i].substring(ind + 1);
		}
		return list;
	}

	private void find(String list[], int type) {
		int xx;
		int pw, ph;
		int max = 10;
		int w[] = new int[max];
		int h[] = new int[max];
		String name[] = new String[max];
		int count = 0;
		for (int i = 0; i < list.length; i++) {
			xx = list[i].indexOf('x');
			if (xx < 0){
				xx = list[i].indexOf('X');
			}
			pw = -1;
			ph = -1;

			if (xx < 0) {
				try {
					pw = Integer.parseInt(list[i]);
					w[count] = pw;
					h[count] = -1;
					name[count] = list[i];
					count++;
				} catch (Exception e) {
				}
			} else {
				try {
					pw = Integer.parseInt(list[i].substring(0, xx));
				} catch (Exception e) {
				}
				try {
					ph = Integer.parseInt(list[i].substring(xx + 1, list[i].length()));
				} catch (Exception e) {
				}

				w[count] = pw;
				h[count] = ph;
				name[count] = list[i];
				count++;
			}
		}
		int select_index = selectIndexBySpace(width, height, w, h, true, count);
		if(type == 0){//pack
			PACK_W = w[select_index];
			PACK_H = h[select_index];
			pack_path = pack_prepath + "/" + name[select_index] + "/";
		}
		else if(type == 1){
			RES_W = w[select_index];
			RES_H = h[select_index];
			res_path = res_prepath + "/" + name[select_index] + "/";
		}
	}
	
	private int selectIndexBySpace(int sw, int sh, int w[], int h[], boolean base, int count)
	{
		int index = -1;
		int min;
		int weights[] = new int[count];
		
		int S=sw*sh;
		
		int One=256;
		
		int kwh=sw*One/sh;

		for(int i = 0; i < count; i++)
		{
			int dw = Math.abs(sw - w[i])*One/sw;
		int dh = Math.abs(sh - h[i])*One/sh;
			
			int Si=w[i]*h[i];
			
			int kwhi=w[i]*One/h[i];
			
			int dkwh=Math.abs(kwh-kwhi);
			
			int dS=Math.abs(S-Si)*One/S;
			
			
			if (base) weights[i] = dS;
			  else weights[i] = (dw + dh) + dkwh;
			
			
		}

		
		min = weights[0];
		index = 0;
		for(int i = 1; i < count; i++)
		{
			if((min > weights[i]))
			{
				min = weights[i];
				index = i;
			}
		}
		return index;
	}
	
	public static final String fileAtlasCommon = "common.atlas";
	public static final String fileAtlasAvatar = "avatar.atlas";
	public static final String fileAtlasBack = "back.atlas";
	public static final String fileAtlasLocIco = "loc_ico.atlas";
	
	public static final String fileTextureBack = "bg.png";
	public static final String fileMapBack = "map.png";
	public static final String fileSplash = "splash.jpg";
	public static final String fileTitle = "game_title.png";

	public static final String fileFontSkranji = "skranji-bold.fnt";
	public static final String fileFontOpenSans = "OpenSans-Regular.ttf";
	public static final String fileFontOpenSans0 = "OpenSans-Regular0.ttf";
	public static final String fileFontOpenSans1 = "OpenSans-Regular1.ttf";
	
	public static final String fileSoundStar = "star.mp3";
	public static final String fileSoundClick3 = "click3.wav";
	public static final String fileSoundBang = "bang.mp3";
	public static final String fileSoundLevelWin = "level_win.mp3";
	public static final String fileSoundLevelLose = "level_lose.mp3";
	public static final String fileSoundResultStar = "click4.wav";
	
	public final static String fileMusicMenu1 = "menu1.mp3";
	public final static String fileMusicGame1 = "ingame1.mp3";
	public final static String fileMusicGame2 = "ingame2.mp3";
	
	public final static String fileStringText = "text.txt";
	
	public static String loadStringFromFile(String name){
		FileHandle handle = Gdx.files.internal("data/"+name);
		return handle.readString("UTF-8");
	}
	
	public String[] loadStringRowsFromFile(String name){
		String s = loadStringFromFile(name);
		if(s.indexOf("\r\n") >= 0){
			return s.split("\r\n");//windows format
		}
		else{
			return s.split("\n");//mac os format
		}
	}
}
