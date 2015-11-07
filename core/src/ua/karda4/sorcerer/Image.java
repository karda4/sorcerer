package ua.karda4.sorcerer;

import com.badlogic.gdx.graphics.Texture;

public class Image extends Texture{
	public final boolean flip = true;
	private final static String JPG = ".jpg";
	private final static String PNG = ".png";
	
	public Image(Texture _tex){
		super(_tex.getTextureData());
	}
	
	public Image(String path, String file){
		super(path + file);
	}
	
	public Image(String path, String file, boolean is_jpg) {
		this(path, file + (is_jpg ? JPG : PNG));
	}
	
	public static final Image createImage(String str){
		return new Image(Assets.res_path, str, false);
	}
	
	public static final Image createImagePJ(String str){
		return new Image(Assets.res_path, str, true);
	}	
}
