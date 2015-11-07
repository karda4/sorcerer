package ua.karda4.sorcerer.tween;

public class SelectLevelItem extends KTween{

	public boolean contains (float x, float y) {
		if(color.a < 0.8f) return false;
		return super.contains(x, y);
	}
}
