package ua.karda4.sorcerer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Ball {
	private GameScreen parent;
	public int B_SPR;
	public int B_X;
	public int B_Y;
	public float B_XP;
	public float B_YP;
	public float B_DX;
	public float B_DY;
	public float B_DELAY;
	public float B_ANIM_APPEAR;
	private float B_SPEED_X;
	private float B_SPEED_Y;
	private float B_SPEED_FALLING;
	private float gravity = 6.0f;
	private float appearAlpha;
	private int CELL_W, CELL_H;
	
	public static final float ball_step_delay = 0.05f;
	private float ball_time_appear = 0.5f;
	
	public Ball(GameScreen _game_screen){
		parent = _game_screen;
		this.CELL_W = parent.CELL_W;
		this.CELL_H = parent.CELL_H;
	}
	
	public void init(int spr, int x, int y){
		reset();
		B_SPR = spr;
		B_X = x;
		B_Y = y;
		B_XP = (CELL_W >> 1) + (x * CELL_W);
		B_YP = (CELL_H >> 1) + (y * CELL_W);
		setAppearAlpha(ball_time_appear);
	}
	
	public void reset(){
		B_SPR = 0;
		B_X = B_Y = 0;
		B_XP = B_YP = 0;
		B_DX = B_DY = 0;
		B_DELAY = 0;
		setAppearAlpha(0);
	}
	
	public void copy(Ball b2){
		this.B_SPR = b2.B_SPR;
		this.B_X = b2.B_X;
		this.B_Y = b2.B_Y;
		this.B_XP = b2.B_XP;
		this.B_YP = b2.B_YP;
		this.B_DX = b2.B_DX;
		this.B_DY = b2.B_DY;
		this.B_DELAY = b2.B_DELAY;
		setAppearAlpha(b2.B_ANIM_APPEAR);
	}
	
	public void shift(int dy, boolean piano){
		B_Y += dy;
		float delta_y = dy * CELL_H;
		B_YP += delta_y;
		if(piano){
			B_DY -= delta_y;
			setSpeed(0, CELL_H, true);
		}
	}
	
	private void setSpeed(float dx, float dy, boolean fall){
		if(fall){
			B_SPEED_FALLING = dy;
		}
		else{
			B_SPEED_X = dx * 4.0f;
			B_SPEED_Y = dy * 4.0f;
		}
	}
	
	private void resetSpeed(){
		if(B_DX != 0 || B_DY != 0) return;
		B_SPEED_X = B_SPEED_Y = B_SPEED_FALLING = 0;
	}
	
	public void change(Ball b2, boolean piano){
		B_SPR = b2.B_SPR;
		if (piano) {
			float delta_x = B_XP - b2.B_XP;
			float delta_y = B_YP - b2.B_YP;
			B_DX -= delta_x;
			B_DY -= delta_y;
			setSpeed(delta_x, delta_y, false);
		}
	}
	
	public boolean availableToChange(){
		return B_DX == 0 && B_DY == 0 && B_DELAY == 0 && B_ANIM_APPEAR == 0;
	}
	
	private void setAppearAlpha(float t_appear){
		B_ANIM_APPEAR = t_appear;
		if(B_ANIM_APPEAR < 0) B_ANIM_APPEAR = 0;
		appearAlpha = (ball_time_appear - B_ANIM_APPEAR) / ball_time_appear;
	}

	public int math(float dt, boolean b_column_move) {
		int res = 0;
		if (B_DELAY > 0) {
			B_DELAY -= dt;
			if(B_DELAY < 0) B_DELAY = 0;
			res |= 0x1;
			return res;
		}
		if(!b_column_move || B_DY != 0){
			if(B_ANIM_APPEAR > 0){
				res |= 0x1;
				setAppearAlpha(B_ANIM_APPEAR - dt);
			}
		}
		if (B_DX != 0) {
			res |= 0x1;//bBallMoving = true;
			B_DX = parent.pianoDecrement(B_DX, B_SPEED_X * dt, 0);
			resetSpeed();
		}
		if (B_DY != 0) {
			res |= 0x1;//bBallMoving = true;
			res |= 0x2;//b_column_move = true;
			float speed;
			if(B_SPEED_Y != 0){
				speed = B_SPEED_Y;
			}
			else{
				accelarate(dt);
				speed = B_SPEED_FALLING;
			}
			B_DY = parent.pianoDecrement(B_DY, speed * dt, 0);
			resetSpeed();
		}
		return res;
	}
	
	private void accelarate(float dt){
		if(B_SPEED_FALLING == 0) return;
		B_SPEED_FALLING += B_SPEED_FALLING * gravity * dt;
	}
	
	public void draw(Graphics g, Color clr) {
		if (B_SPR == 0) return;
		float x = parent.MAP_X + B_XP + B_DX;
		float y = parent.MAP_Y + B_YP + B_DY;
		if(B_ANIM_APPEAR > 0){
			g.setColor(clr.r, clr.g, clr.b, appearAlpha);
		}
		TextureRegion tr = parent.trBall[B_SPR];
		g.draw(tr, x - (tr.getRegionWidth() >> 1), y - (tr.getRegionHeight() >> 1));
		if(B_ANIM_APPEAR > 0){
			g.setColor(clr);
		}
	}

	public void dispose() {
		reset();
	}
	
	public boolean canSelect(){
		return B_SPR != 0 && B_DX == 0 && B_DY == 0;
	}
}
