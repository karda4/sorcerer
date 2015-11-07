package ua.karda4.sorcerer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
//import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

public class MyFont{
	
	private BitmapFont bf;
	private static GlyphLayout glyphLayout = new GlyphLayout();
	
	public MyFont(Color clr_font, BitmapFont _bf){
		this.bf = _bf;
	}
		
	public MyFont(String file_name){
		this(null, file_name);
	}
	
	public MyFont(Color clr_font, String file_name){
		this.bf = new BitmapFont(Gdx.files.internal(Assets.pack_path+file_name), true);
	}
	
	public void drawString(Graphics g, String s, float x, float y, int anc){
		drawString(g, s, null, x, y, anc);
	}
	
	public void drawString(Graphics g, String str, Color clr, float x, float y, int anc){
		if(clr != null) bf.setColor(clr);
		glyphLayout.setText(bf, str);
		if ((anc & Graphics.BOTTOM) > 0) y -= glyphLayout.height;
		if ((anc & Graphics.VCENTER) > 0) y -= glyphLayout.height / 2;
		if ((anc & Graphics.RIGHT) > 0) x -= glyphLayout.width;
		if ((anc & Graphics.HCENTER) > 0) x -= glyphLayout.width / 2;
		bf.draw(g, str, x, y);
	}
	
	public int getHeight(){
		return (int)bf.getCapHeight();
	}
	
	public float getLineHeight(){
		return bf.getLineHeight();
	}
	
	public float getSpaceWidth(){
		return bf.getSpaceWidth();
	}
	
	public int stringWidth(String str){
		glyphLayout.setText(bf, str);
		return (int)glyphLayout.width;
		//return (int)bf.getBounds(str).width;
	}
	
	public int charWidth(char ch){
		glyphLayout.setText(bf, ch+"");
		return (int)glyphLayout.width;
		//return (int)bf.getBounds(ch+"").width;
	}

	public void dispose() {
		bf.dispose();
	}

	public void setColor(Color clrf) {
		bf.setColor(clrf);
	}
/*
	public TextBounds getWrappedBounds(String str, int wrapWidth) {
		return bf.getWrappedBounds(str, wrapWidth);
	}
	*/
	public GlyphLayout getGlyphLayout(String str, int wrapWidth) {
		glyphLayout.setText(bf, str, bf.getColor(), wrapWidth, Align.left, true);
		return glyphLayout;
		//return bf.getWrappedBounds(str, wrapWidth);
	}
	
	public void drawWrapped(Graphics g, String str, Color color, float x, float y, float wrapWidth) {
		bf.setColor(color);
		bf.draw(g, str, x, y, wrapWidth, Align.left, true);
		//bf.drawWrapped(g, str, x, y, wrapWidth);
	}

	public void drawWrapped(Graphics g, String str, Color color, float x, float y, float wrapWidth, int anc) {
		bf.setColor(color);
		int ha = Align.left;
		if(anc == 1) ha = Align.center;
		if(anc == 2) ha = Align.right;
		bf.draw(g, str, x, y, wrapWidth, ha, true);
		//bf.drawWrapped(g, str, x, y, wrapWidth, ha);
	}

	public void setColor(float r, float g, float b, float a) {
		bf.setColor(r, g, b, a);
	}

	public Color getColor() {
		return bf.getColor();
	}
}
