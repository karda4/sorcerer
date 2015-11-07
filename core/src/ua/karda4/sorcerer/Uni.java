package ua.karda4.sorcerer;

import com.badlogic.gdx.math.MathUtils;

public class Uni {

	public final static int LEFT_TOP = Graphics.LEFT | Graphics.TOP;
	public final static int LEFT_BOTTOM = Graphics.LEFT | Graphics.BOTTOM;
	public final static int LEFT_VCENTER = Graphics.LEFT | Graphics.VCENTER;

	public final static int RIGHT_TOP = Graphics.RIGHT | Graphics.TOP;
	public final static int RIGHT_BOTTOM = Graphics.RIGHT | Graphics.BOTTOM;
	public final static int RIGHT_VCENTER = Graphics.RIGHT | Graphics.VCENTER;

	public final static int HCENTER_TOP = Graphics.HCENTER | Graphics.TOP;
	public final static int HCENTER_BOTTOM = Graphics.HCENTER | Graphics.BOTTOM;
	public final static int HCENTER_VCENTER = Graphics.HCENTER
			| Graphics.VCENTER;

	public Uni() {
		// TODO Auto-generated constructor stub
	}

	public static final int random(int start, int end) {
		return MathUtils.random(start, end);
	}
	
	public static final int abs(int i){
		return i >= 0 ? i : -i;
	}
	
	public static final Image createImage(String s) {
		return Image.createImage(s);
	}
}
