package ua.karda4.sorcerer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class Graphics extends SpriteBatch{
	
	public static final int HCENTER = 1;
	public static final int VCENTER = 2;
	public static final int LEFT = 4;
	public static final int RIGHT = 8;
	public static final int TOP = 16;
	public static final int BOTTOM = 32;
	public static final int BASELINE = 64;
	
	public static final int SOLID = 0;
	public static final int DOTTED = 1;
	
	private Camera camera;
	
	public Graphics(Camera _camera, int scr_width, int scr_height) {
		camera = _camera;
//		scissors = new Rectangle();
//		clipBounds = new Rectangle(0, 0, scr_width, scr_height);
//		shapeRend = new ShapeRenderer();
		this.initClip(0, 0, camera.viewportWidth, camera.viewportHeight);
	}
	
	public void setProjectionMatrix (Matrix4 projection) {
		super.setProjectionMatrix(projection);
		shapeRend.setProjectionMatrix(projection);
	}
	
	Rectangle scissors = new Rectangle();
	Rectangle clipBounds = new Rectangle();
	Rectangle clipTmp = new Rectangle();
	ShapeRenderer shapeRend = new ShapeRenderer();
	
	public final void setClip(float x, float y, float w, float h){
		clipTmp.set(x, y, w, h);
		setClip(clipTmp);
	}
	
	public final void initClip(float x, float y, float w, float h){
		clipTmp.set(x, y, w, h);
		initClip(clipTmp);
	}
	
	public final void setClip(Rectangle rect){
		if(rect.width <= 0 || rect.height <= 0) return;
		if(rect.x == clipBounds.x && rect.y == clipBounds.y &&
				rect.width == clipBounds.width && rect.height == clipBounds.height) return;
		this.flush();
		ScissorStack.popScissors();
		//if(rect.width == camera.viewportWidth && rect.height == camera.viewportHeight && rect.x == 0 && rect.y == 0) return;
		initClip(rect);
	}
	
	public final void initClip(Rectangle rect){
		clipBounds.set(rect);
		ScissorStack.calculateScissors(camera, this.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);
	}
	
	public final float getClipX(){
		return clipBounds.x;
	}
	
	public final float getClipY(){
		return clipBounds.y;
	}
	
	public final float getClipWidth(){
		return clipBounds.width;
	}
	
	public final float getClipHeight(){
		return clipBounds.height;
	}
		
	public void fillRect(Color clr, float x, float y, float width, float height){
		boolean b_drawing = this.isDrawing(); 
		if (b_drawing) this.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRend.begin(ShapeType.Filled);
		shapeRend.setColor(clr);
		shapeRend.rect(x, y, width, height);
		shapeRend.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		if (b_drawing) this.begin();
	}
	
	public void fillRect(Color clr, Rectangle rect){
		fillRect(clr, rect.x, rect.y, rect.width, rect.height);
	}
	
	public final void fillRect(float x, float y, float width, float height){
		fillRect(this.getColor(), x, y, width, height);
	}
	
	public final void drawRect(int x, int y, int w, int h){
		
	}
	
	public void drawImage(Image img, float x, float y, int w, int h){
		if(img.flip){
			this.draw(img, x, y, w, h, 0, 0, 1, 1);
		}
		else{
			this.draw(img, x, y, w, h, 0, 1, 1, 0);
		}
	}
	
	public void drawImage(Image img, float x, float y){
		drawImage(img, x, y, img.getWidth(), img.getHeight());
	}
	
	public void drawImage(Image img, float x, float y, int anc){
		if(anc != 20){
			if ((anc & Graphics.BOTTOM) > 0) y -= img.getHeight();
			if ((anc & Graphics.VCENTER) > 0) y -= img.getHeight() >> 1;
			if ((anc & Graphics.RIGHT) > 0) x -= img.getWidth();
			if ((anc & Graphics.HCENTER) > 0) x -= img.getWidth() >> 1;
		}
		drawImage(img, x, y);
	}
	
	public void drawImage(Image img, float x, float y, int w, int h, int anc){
		if(anc != 20){
			if ((anc & Graphics.BOTTOM) > 0) y -= h;
			if ((anc & Graphics.VCENTER) > 0) y -= h >> 1;
			if ((anc & Graphics.RIGHT) > 0) x -= w;
			if ((anc & Graphics.HCENTER) > 0) x -= w >> 1;
		}
		drawImage(img, x, y, w, h);
	}
	
	//------------------gradient-----------------
	static final float[] vertices = new float[20];

	public void drawGradient(TextureRegion white, 
	      float x, float y, 
	      float width, float height, 
	      Color colorA, Color colorB, 
	      boolean horizontal) {
	   float ca = colorA.toFloatBits();
	   float cb = colorB.toFloatBits();
	   int idx = 0;
	   float u = white.getU();
	   float v = white.getV2();
	   float u2 = white.getU2();
	   float v2 = white.getV();
	   
	   //bottom left
	   vertices[idx++] = x;
	   vertices[idx++] = y;
	   vertices[idx++] = horizontal ? ca : cb;
	   vertices[idx++] = u;
	   vertices[idx++] = v;

	   //top left
	   vertices[idx++] = x;
	   vertices[idx++] = y + height;
	   vertices[idx++] = ca;
	   vertices[idx++] = u;
	   vertices[idx++] = v2;

	   //top right 
	   vertices[idx++] = x + width;
	   vertices[idx++] = y + height;
	   vertices[idx++] = horizontal ? cb : ca;
	   vertices[idx++] = u2;
	   vertices[idx++] = v2;

	   //bottom right
	   vertices[idx++] = x + width;
	   vertices[idx++] = y;
	   vertices[idx++] = cb;
	   vertices[idx++] = u2;
	   vertices[idx++] = v;
	   this.draw(white.getTexture(), vertices, 0, vertices.length);
	}
}
