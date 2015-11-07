package ua.karda4.sorcerer.tween;

import aurelienribon.tweenengine.TweenAccessor;

public class KTweenAccessor implements TweenAccessor<KTween> {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;
	public static final int ALPHA = 4;
	public static final int SCALE = 5;
	public static final int COLOR = 6;

	@Override
	public int getValues(KTween target, int tweenType, float[] returnValues) {
		switch (tweenType) {
		case POSITION_X:
			returnValues[0] = target.x;
			return 1;
		case POSITION_Y:
			returnValues[0] = target.y;
			return 1;
		case POSITION_XY:
			returnValues[0] = target.x;
			returnValues[1] = target.y;
			return 2;
		case ALPHA:
			returnValues[0] = target.color.a;
			return 1;
		case SCALE:
			returnValues[0] = target.scale;
			return 1;
		case COLOR:
			returnValues[0] = target.color.r;
			returnValues[1] = target.color.g;
			returnValues[2] = target.color.b;
			return 3;
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(KTween target, int tweenType, float[] newValues) {
		switch (tweenType) {
		case POSITION_X:
			target.x = newValues[0];
			break;
		case POSITION_Y:
			target.y = newValues[0];
			break;
		case POSITION_XY:
			target.x = newValues[0];
			target.y = newValues[1];
			break;
		case ALPHA:
			target.color.a = newValues[0];
			break;
		case SCALE:
			target.scale = newValues[0];
			break;
		case COLOR:
			target.color.set(newValues[0], newValues[1], newValues[2], target.color.a);
			break;
		default:
			assert false;
		}
	}
}
