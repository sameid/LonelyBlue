package com.roadhouse.boxheadonline;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;

public class Enemy extends Collidable {

	private Circle enemy;
	private double speedX, speedY;

	final static int HEALTH_MAX = 4;
	final static int MAX_SPEED = Boxhead.ENEMY_MAX_SPEED;
	final static int MIN_SPEED = Boxhead.ENEMY_MIN_SPEED;
	private int health;
	private double direction;


	private static Texture eneImg;


	public Enemy (){
		super.setControl(new Circle());
		this.enemy = super.getControl();
		speedX = 0.0;
		speedY = 0.0;

		health =HEALTH_MAX;
		setEneImg(new Texture (Gdx.files.internal("sprites/enemy.png")));
	}

	public static Enemy enemySpawn(int level){
		int radius = 10 + (int) Math.sqrt (Math.pow (Boxhead.SCREEN_WIDTH/2, 2) + Math.pow (Boxhead.SCREEN_HEIGHT/2, 2)); // spawning radius (from center of map)
		int tempX, tempY;

		Random rand = new Random();

		tempX = rand.nextInt(radius);
		if (rand.nextInt(2) == 0){
			tempX *= -1;
		}
		tempY = (int) Math.sqrt (Math.pow (radius, 2) - Math.pow (tempX, 2)); // spawning y (from center of map)

		if (rand.nextInt(2) == 0){
			tempY *= -1;
		}
		Enemy newEnemy = new Enemy();
		newEnemy.getEnemy().radius = 32;
		newEnemy.getEnemy().x = Boxhead.SCREEN_WIDTH / 2 + tempX;
		newEnemy.getEnemy().y = Boxhead.SCREEN_HEIGHT / 2 + tempY;

		return newEnemy;
	}
	
	private int rs(){
		return MAX_SPEED + (int)(Math.random() * ((MAX_SPEED-MIN_SPEED) + 1));
	}

	private void calculateSpeed (){
		speedX = rs()*(Math.cos(direction));
		speedY = rs()*(Math.sin(direction));
	}

	public void move (Circle character){
		this.calculateDirection(character);
		this.calculateSpeed();
		enemy.x -= speedX;
		enemy.y -= speedY;
	}

	private void calculateDirection (Circle character){
		double down, top;
		top = (character.x + character.radius) - (enemy.x+ enemy.radius);
		down = (character.y + character.radius) - (enemy.y + enemy.radius);
		Double angle = 0.0;
		angle = (Math.atan(top / down));


		if (character.x + character.radius < enemy.x + enemy.radius ) {
			if (character.y + character.radius < enemy.y + enemy.radius) {
				angle = Math.PI/2 - angle;

			}else if (character.y + character.radius > enemy.y + enemy.radius) {
				angle = (Math.PI/2 + angle)*-1;

			}
		} else if (character.x + character.radius > enemy.x + enemy.radius) {
			if (character.y + character.radius < enemy.y + enemy.radius) {
				angle = Math.PI/2 + (angle*-1);
			} else if (character.y + character.radius > enemy.y + enemy.radius) {
				angle = (Math.PI/2 + angle)*-1;
			}
		}
		direction = angle;

	}

	public void decreaseHealth(){
		enemy.radius+=5;
		health--;
	}

	public Circle getEnemy() {
		return enemy;
	}
	public void setEnemy(Circle enemy) {
		this.enemy = enemy;
	}
	public double getSpeedX() {
		return speedX;
	}
	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}
	public double getSpeedY() {
		return speedY;
	}
	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}



	@Override
	public boolean isColliding(Collidable otherObject, double bound) {
		// TODO Auto-generated method stub
		Circle other = otherObject.getControl();

		double xDif = 0.0;
		double yDif = 0.0;
		double c = 0.0;

		try {
			if (otherObject instanceof Bullet) {
		
				return enemy.contains(other.x, other.y);				
			
			} else if (otherObject instanceof Character) {
				if (enemy.x + enemy.radius > other.x + other.radius) {
					xDif = enemy.x + enemy.radius - other.x + other.radius;
				} else if (enemy.x + enemy.radius < other.x + other.radius) {
					xDif = other.x + other.radius - enemy.x + enemy.radius;
				}

				if (enemy.y + enemy.radius > other.y + other.radius) {
					yDif = enemy.y + enemy.radius - other.y + other.radius;
				} else if (enemy.y + enemy.radius < other.y + other.radius) {
					yDif = other.y + other.radius - enemy.y + enemy.radius;
				}
				c = Math.sqrt(Math.pow(yDif, 2) + Math.pow(xDif, 2));
				c = c - 180;
			}
		} catch (Exception e) {
			return false;
		}

		return c <= bound;

	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public static Texture getEneImg() {
		return eneImg;
	}

	public static void setEneImg(Texture eneImg) {
		Enemy.eneImg = eneImg;
	}

}
