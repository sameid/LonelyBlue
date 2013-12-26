package com.roadhouse.boxheadonline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;

public class Button {
	
	public static float BUTTON_WIDTH = Boxhead.SCREEN_WIDTH;
	public static float BUTTON_HEIGHT = 128;
	
	private boolean pressed;
	private boolean hasBeenSet;
	private Rectangle touch;
	private String text;
	
	static Texture regImg = new Texture(Gdx.files.internal("testing/button.png"));
	static Texture touchImg = new Texture(Gdx.files.internal("testing/buttonTouch.png"));
	static FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("testing/big_noodle_titling.ttf"));
	static BitmapFont font = generator.generateFont(96, Boxhead.FONT_CHARACTERS, true);
	
	public Button (float x, float y, String text){
		this.touch = new Rectangle();
		this.touch.x = x;
		this.touch.y = y;
		this.touch.width = BUTTON_WIDTH;
		this.touch.height = BUTTON_HEIGHT;
		this.text = text;
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
	
	public void drawButton (SpriteBatch batch, float x){
		 
		if (this.isPressed()){
			font.setColor(Color.BLACK);
			batch.draw(touchImg, touch.x, touch.y, BUTTON_WIDTH, BUTTON_HEIGHT);	
			
		}
		else {
			font.setColor(Color.WHITE);
			//batch.draw(regImg, touch.x, touch.y);
		}
		font.draw(batch, text, touch.x + x, touch.y + 30);
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
