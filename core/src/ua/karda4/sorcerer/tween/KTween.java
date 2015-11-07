package ua.karda4.sorcerer.tween;

import com.badlogic.gdx.graphics.Color;

public class KTween {
	public byte type;
	public float x, y;
	public float width, height;
	public Color color = new Color(1, 1, 1, 1);
	public float scale = 1.0f;
	
	public KTween() {
		
	}
	
	public KTween(float x, float y, float width, float height) {
		set(x, y, width, height);
	}

	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains (float x, float y) {
		return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
	}
}
