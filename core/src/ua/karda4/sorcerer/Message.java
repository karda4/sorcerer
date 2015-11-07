package ua.karda4.sorcerer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Message {
	String title;
	String txt;
	Rectangle rect = new Rectangle();
	float timer;
	float time = 1.5f;
	float x_move;
	float speed_move;
	
	public Message(String title, String txt){
		this.title = title;
		this.txt = txt;
		timer = time;
		int w = GameScreen.width >> 1;
		int h = GameScreen.height >> 1;
		int x = GameScreen.width - w >> 1;
		int y = GameScreen.height - h >> 1;
		rect.setSize(w, h);
		rect.setPosition(x, y);
		int fontH = GameScreen.font.getHeight();
		rOK.setSize(GameScreen.font.stringWidth(GameScreen.TXT[0][1]) + (fontH << 1), fontH << 1);
		rOK.setPosition(rect.x + (rect.width / 2) - (rOK.width / 2), rect.y + rect.height - rOK.height - (fontH >> 1));
		x_move = -(x + w);
		speed_move = GameScreen.width;
		aLive = true;
	}
	
	public boolean aLive = false;
	
	public boolean math(float dt){
		if(!aLive) return false;
		if(x_move < 0){
			x_move += speed_move * dt;
			if(x_move > 0){
				x_move = 0;
			}
		}
		else if(timer > 0){
			timer -= dt;
		}
		else{
			x_move += speed_move * dt;
			if(x_move > GameScreen.width - rect.x){
				return false;
			}
		}
		return true;
	}
	
	private Rectangle rOK = new Rectangle();
	
	public Rectangle getRectOK(){
		return rOK;
	}
	
	public void draw(Graphics g){
		int fontH = GameScreen.font.getHeight();
		int padd = fontH;
		int ww = (int) (rect.width - (padd << 1));
		g.fillRect(Color.DARK_GRAY, rect);
		GameScreen.font.drawWrapped(g, title, Color.YELLOW, rect.x + (rect.width / 2), rect.y + fontH, Uni.HCENTER_VCENTER);
		GameScreen.font.drawWrapped(g, txt, Color.WHITE, rect.x + padd, rect.y + (fontH << 1), ww);
		GameScreen.drawButtonText(g, GameScreen.TXT[0][1], rOK, false);
	}
}
