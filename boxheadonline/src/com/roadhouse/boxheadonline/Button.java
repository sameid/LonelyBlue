package com.roadhouse.boxheadonline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Button {
	
//	final static String BLUE = "01579aa9";
//	final static String RED = "01e76e66";
//	final static String PURPLE = "01695b8e";
//	final static String GREEN = "011b7e5a";
//	final static String ORANGE = "01de8650";
//	final static String NAVY_BLUE = "0128545b";
//	final static String LIGHT_PURPLE = "018c5d79";
//	final static String LIGHT_GREY = "012e2e2e";
	public static Texture LIGHT_BLUE = new Texture(Gdx.files.internal("colors/light_blue.png"));
	public static Texture RED = new Texture(Gdx.files.internal("colors/red.png"));
	public static Texture PURPLE = new Texture(Gdx.files.internal("colors/purple.png"));
	public static Texture LIGHT_GREY = new Texture(Gdx.files.internal("colors/light_grey.png"));
	public static Texture ORANGE = new Texture(Gdx.files.internal("colors/orange.png"));
	public static Texture NAVY_BLUE = new Texture(Gdx.files.internal("colors/navy_blue.png"));
	public static Texture GREY = new Texture(Gdx.files.internal("colors/grey.png"));
	public static Texture GREEN = new Texture(Gdx.files.internal("colors/green.png"));
	public static Texture LIGHT_PURPLE = new Texture(Gdx.files.internal("colors/light_purple.png"));

		
	public static float BUTTON_WIDTH = 625;
	public static float BUTTON_HEIGHT = 130;
	
	private boolean pressed;
	private boolean hasBeenSet;
	private Rectangle touch;
	private String text;
	private Texture color;
	private float fx, fy;
	private Texture image;
	private boolean hasImage;
	
	static Texture touchImg = new Texture(Gdx.files.internal("sprites/buttonTouch.png"));
	
	FreeTypeFontGenerator _generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Bold.ttf"));	
	BitmapFont font = _generator.generateFont(30, Boxhead.FONT_CHARACTERS, true);
	
	public Button (float x, float y, String text, Texture color){
		this.touch = new Rectangle();
		this.touch.x = x;
		this.touch.y = y;
		this.touch.width = BUTTON_WIDTH;
		this.touch.height = BUTTON_HEIGHT;
		
		this.text = text;
		this.color = color;
		this.hasImage = false;
		this.fx = this.touch.x + this.touch.width/2 - font.getBounds(this.text).width/2;
	    this.fy = (this.touch.y + this.touch.height/2 + font.getBounds(this.text).height/2) -20;

	}

	public void setPressed (float x, float y){
		pressed = this.touch.contains(x, y);
		hasBeenSet = true;
	}
	
	public boolean isPressed(){
		return pressed;
	}
	
	public boolean isReleased(){
		return this.isHasBeenSet() && this.isPressed();
	}
	
	public void drawButton (SpriteBatch batch){
		 
		if (this.isPressed()){
			batch.draw(touchImg, touch.x, touch.y, touch.width, touch.height);	
		}
		batch.draw(this.color, touch.x,touch.y, touch.width, touch.height);
		if (!this.hasImage){
			font.draw(batch, this.text, this.fx, this.fy);	
		}else {
			batch.draw(this.image, this.fx, this.fy, this.image.getWidth(), this.image.getHeight(), 
					(int)this.fx, (int)this.fy, this.image.getWidth(), this.image.getHeight(), false, true);
		}
		
		
	}


	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}



	public boolean isHasBeenSet() {
		return hasBeenSet;
	}


	public void setHasBeenSet(boolean hasBeenSet) {
		this.hasBeenSet = hasBeenSet;
	}
	


}
