//In the name of Allah, the Most Merciful, The Ever Merciful

package com.roadhouse.boxheadonline;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.roadhouse.networking.NetworkThread;
import com.roadhouse.ui.InputHandler;


public class Boxhead implements ApplicationListener {

	public final static int DOWN_LEFT = 0;
	public final static int DOWN_RIGHT = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int UP_LEFT = 4;
	public final static int UP_RIGHT = 5;
	public final static int UP = 6;
	public final static int RIGHT = 7;

	public final static int ENEMIES_PER_LEVEL = 100;
	public final static float ENEMY_SPAWN_DELAY = 0.35f;
	public final static int BULLET_SPEED = 35;
	public final static int ENEMY_MAX_SPEED = 10;
	public final static int ENEMY_MIN_SPEED = 8;
	public final static int DESKTOP = 0;
	public final static int ANDROID = 1;
	
	public final static boolean DEVELOPER = true;
	
	
	public static int platform = -1;
	
	//"highscore_v2" --> b8174e9a5b95a049a72c7f9a2e49a849
	public final static String HIGHSCORE_KEY = "b8174e9a5b95a049a72c7f9a2e49a849";
	
	public static Preferences prefs;
	public static boolean played;

	OrthographicCamera camera;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	
	BitmapFont font, huge, _font;
	FreeTypeFontGenerator generator;

	Character character;
	Character peer;
	Joypad mover;
	Joypad shooter;

	Texture randimg;
	Texture profileIcon;

	ArrayList<Bullet> bullets;
	public static ArrayList<Enemy> enemies;
	ArrayList <ScoreUp> scoreUps;
	ArrayList <Explosion> explosions;

	public final static int SCREEN_WIDTH = 1280;
	public final static int SCREEN_HEIGHT = 720;
	//public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.:;,{}\"�`'<>";
	public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|/?-+=()*&.:;,{}<";

	boolean currentTouchOne = false;
	boolean currentTouchTwo = false;
	boolean isScheduled = true;
	boolean collideScheduled = false;

	boolean game = false;
	boolean gameOver = false;
	boolean pause = false;
	boolean mainMenu = true;
	boolean onlineMenu = false;
	boolean optionsMenu = false;

	boolean signedIn = false;
	boolean isServer = false;
	boolean multiplayerGame = false;
	
	boolean tutorial = true;
	boolean moving = false;
	boolean shooting = false;
	
	Button replayBtn;
	Button quitToMainBtn;

	Button singlePlayerBtn;
	Button leaderboardBtn;
	//Button signIn_OutBtn;
	Button multiplayerBtn;
	Button optionsBtn;

	Button invitePlayersBtn;
	Button showInvitationsBtn;
	Button backBtn;
	
	Button backFromOptionsBtn;

	Music music;
	RadioButtonSet options_difficulty;
	RadioButtonSet options_sound;
	
	
	double speedX, speedY;
	int level;
	int score;
	int kills;
	int deathLimit;

	int currentScore;
	float cex, cey;
	String healthString;
	AndroidAccessLayer android;

	boolean tapToStart = false;

	int myID;

	public Boxhead(String p)
	{
		super();
	}

	public Boxhead(int p, AndroidAccessLayer aal){
		super();
		android = aal;
		if (p == Boxhead.DESKTOP){
			signedIn = false;
			platform = Boxhead.DESKTOP;
		}
		else if (p == Boxhead.ANDROID){
			signedIn = android.isSignedIn();
			platform = Boxhead.ANDROID;
		}

	}

	@Override
	public void create() {		
		printf("Lonely Blue. v0.1");

		prefs = Gdx.app.getPreferences("myprefs");
		played = !prefs.get().isEmpty();

		if (!played){
			LBInputListener listener = new LBInputListener(prefs);
			Gdx.input.setOnscreenKeyboardVisible(true);
			Gdx.input.getTextInput(listener, "Display Name:", "");

			prefs.putInteger(HIGHSCORE_KEY, 0);
			prefs.flush();
		}

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/track1.mp3"));
		music.setVolume(0.5f);
		music.setLooping(true);
//		if (!DEVELOPER)
//		music.play(); 

		Texture.setEnforcePotImages(false);

		camera = new OrthographicCamera();
		camera.setToOrtho(true, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		level = 1;
		character = new Character();

		mover = new Joypad(true);
		shooter = new Joypad(false);
		randimg = new Texture(Gdx.files.internal("sprites/char.png"));
		profileIcon = new Texture(Gdx.files.internal("sprites/profile_icon.png"));
		
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		scoreUps = new ArrayList<ScoreUp>();
		explosions = new ArrayList<Explosion>();

		FreeTypeFontGenerator _generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Regular.ttf"));	
		font = _generator.generateFont(45, FONT_CHARACTERS, true);
		huge = _generator.generateFont(150, FONT_CHARACTERS, true);
		_font = _generator.generateFont(60, FONT_CHARACTERS, true);
		_generator.dispose();

		healthString = "";

		for (int i = 0 ; i < character.getHealth() /13; i++){
			healthString +="|";
		}
		int _t = (1280/2)-(640/2);
		replayBtn = new Button (_t, 200, "REPLAY", Button.ORANGE);
		quitToMainBtn = new Button (_t, 338+3, "BACK", Button.NAVY_BLUE);
		
		singlePlayerBtn = new Button (10, 250, "SINGLE PLAYER", Button.LIGHT_BLUE);
		multiplayerBtn = new Button (645, 250, "MULTIPLAYER", Button.RED);
		leaderboardBtn = new Button (10, 250+140, "LEADERBOARD", Button.PURPLE);
		optionsBtn = new Button (645, 250+140, "SETTINGS", Button.GREEN);
		//signIn_OutBtn = new Button (0,535, "SIGN IN", Button.LIGHT_GREY);
	
		invitePlayersBtn = new Button (_t, 200, "INVITE PLAYERS", Button.ORANGE);
		showInvitationsBtn = new Button (_t, 338+3, "SHOW INVITATIONS", Button.NAVY_BLUE);
		backBtn = new Button (_t,338+138+6, "BACK", Button.LIGHT_PURPLE);

		String[] texts = {"EASY", "MEDIUM", "HARD"};
		options_difficulty = new RadioButtonSet(400, 250, "DIFFICULTY", texts,1);
		String[] texts2 = {"ON", "OFF"};
		options_sound = new RadioButtonSet(400, 400, "SOUND", texts2, 0);
		
		backFromOptionsBtn = new Button(600, 550, "BACK", Button.ORANGE);
		
		deathLimit = 20;
	}

	@Override
	public void dispose() {
		randimg.dispose();
		Bullet.dispose();
		shooter.dispose();
		mover.dispose();

	}
	boolean update = false;

	public void delay(long milliseconds){
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	long start = 0;
	long current = 0;
	long elapsed = 0;
	long changed = 0;
	
	long grabed = 0;
	long g2 = 0;
	
	boolean moved = false;
	boolean shot = false;
	
	@Override
	public void render() {
		current = System.currentTimeMillis();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		
		batch.begin();
		
		
		batch.draw(Button.GREY, 0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
		if (game){

			if (!tapToStart){
				//draw initial game play stuff
				tapToStart = currentTouchOne || currentTouchTwo;
//				resetGame();
			}
			
			if (tutorial){

				elapsed = (current - start);
				changed = elapsed;
				long _ = changed;
				if (moving) moved = true;
				if (shooting) shot = true;
				
				if (_ > 500 && _ < 1500) font.draw(batch, "How", 500, 200);
				if (_ > 750 && _ < 1500) font.draw(batch, "How to", 500, 200);
				if (_ > 1000 && _ < 1500) font.draw(batch, "How to play...", 500, 200);
				
				if (_ > 1500 && !moved) font.draw(batch, "TAP", 150, 400);
				if (_ > 1750 && !moved) font.draw(batch, "TAP &", 150, 400);
				if (_ > 2000 && !moved) font.draw(batch, "TAP & HOLD", 150, 400);
				if (_ > 2250 && !moved) font.draw(batch, "... the left side to move", 150, 450);
				
				if (moved && grabed == 0){
					grabed = _;
				}

				if (_ -grabed > 500 && moved && !shot) font.draw(batch, "TAP", 900, 400);
				if (_ -grabed > 750 && moved && !shot) font.draw(batch, "TAP &", 900, 400);
				if (_ -grabed > 1000 && moved && !shot) font.draw(batch, "TAP & HOLD", 900, 400);
				if (_ -grabed > 1250 && moved && !shot) font.draw(batch, "... the right side to shoot", 600, 450);
				
				if (shot && g2 == 0){
					g2 = _;
				}
				
				if(moved && shot){
					long __ = _ - g2;
					if (__ > 0 && __ < 1500) font.draw(batch, "GET READY!", 600, 360);
					if (__ > 1500 && __ < 2500) font.draw(batch, "3", 630, 360);
					if (__ > 2500 && __ < 3500) font.draw(batch, "2", 630, 360);
					if (__ > 3500 && __ < 4500) font.draw(batch, "1", 630, 360);
					if (__ > 4500 && __ < 5500) font.draw(batch, "GO!", 630, 360);
					if (__ > 5500 && __ < 6500) {
						tutorial = false;
						spawnEnemies();
					}
					
				}
				
			}

			// If currently touching screen, then draw respective Joypad
			if (currentTouchOne) {
				mover.drawJoypad(batch);
			}
			if (currentTouchTwo) {
				shooter.drawJoypad(batch);
			}

			// Draw character on screen

			batch.draw(randimg, 
					character.getControl().x - character.getControl().radius,
					character.getControl().y - character.getControl().radius);

			if (multiplayerGame){
				batch.draw(randimg, 
						peer.getControl().x - peer.getControl().radius,
						peer.getControl().y - peer.getControl().radius);

			}

			//Item.drawItems(batch);
			
			for (int i = 0; i < enemies.size(); i++) {
				Enemy current = enemies.get(i);
				batch.draw(Enemy.getEneImg(), current.getEnemy().x - current.getEnemy().radius,
						current.getEnemy().y - current.getEnemy().radius, current.getEnemy().radius*2, current.getEnemy().radius*2);
				
			}

			// draw possible bullets that are shot
			for (int i = 0; i < bullets.size(); i++) {
				Bullet current = bullets.get(i);
				batch.draw(Bullet.getImage(), current.getControl().x,
						current.getControl().y);
			}

			for (int i = 0 ; i < scoreUps.size() ; i++){
				ScoreUp current = scoreUps.get(i);
				if (current.incrementOrRemove()){
					scoreUps.remove(i);
				}
				current.y--;	  

				font.draw(batch, "+" + current.currentScore, current.x, current.y);
			}

			for (int i = 0; i < explosions.size() ; i++){
				Explosion current = explosions.get(i);
				if(!current.isDone())current.draw(batch);
				else explosions.remove(i);
			}

			font.draw(batch, "Level: " + level, 30, 30);
			font.draw(batch, "Score: " + score, 1000, 30);
			font.draw(batch, healthString, 250, 30);
		}
		else {

			if (mainMenu){
				//if (signedIn)batch.draw(mainMenuImg_signedIn, 0, 0, 1280, 720, 0, 0, 1280, 720, false, true);
				//else batch.draw(mainMenuImg_signedOut, 0, 0, 1280, 720, 0, 0, 1280, 720, false, true);
//				batch.draw(profileNameBlock, 0,440, Button.BUTTON_WIDTH, Button.BUTTON_HEIGHT);
//				batch.draw(profileIcon, 0, 440);
				huge.draw(batch, ".LONELY BLUE", 65, 80);
//				font.draw(batch, prefs.getString("name"), 130, 475);
//				_font.draw(batch, Integer.toString(prefs.getInteger(HIGHSCORE_KEY)), 130, 600);

				singlePlayerBtn.drawButton(batch);
				leaderboardBtn.drawButton(batch);
				multiplayerBtn.drawButton(batch);
				//signIn_OutBtn.drawButton(batch);
				optionsBtn.drawButton(batch);

			}
			else if (gameOver){
				huge.draw(batch, "GAME OVER", 40, 40);
				replayBtn.drawButton(batch);
				quitToMainBtn.drawButton(batch);

			}
			else if (onlineMenu){
				huge.draw(batch, "MULTIPLAYER", 40, 40);
				invitePlayersBtn.drawButton(batch);
				showInvitationsBtn.drawButton(batch);
				backBtn.drawButton(batch);
			}
			else if(optionsMenu){
				huge.draw(batch, "SETTINGS", 40, 40);
				options_difficulty.drawText (batch);
				options_sound.drawText (batch);
				backFromOptionsBtn.drawButton(batch);
				
			}
		}
		// End drawing to batch<SpriteBatch>
		batch.end();
		
		if (!game){
			if(optionsMenu){
				options_difficulty.draw (shapeRenderer);
				options_sound.draw (shapeRenderer);
			}
		}

		////////////////////////////////////////////////////************************////////////////////////////

		int left = 4;
		int right = 4;
		if (game){
			if (!pause){ 
				if (multiplayerGame){
					nt.sendMessage(character.toString());
					peer.update(nt.getNewMessage());
				}
				if (platform == Boxhead.ANDROID || platform == Boxhead.DESKTOP){
					
					if (Gdx.input.isTouched(0)) {

						// Touch initialization
						Vector3 t1 = new Vector3();
						t1.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
						camera.unproject(t1);
						if (t1.x < SCREEN_WIDTH/2){
							left = 0;
						}
						else if (t1.x > SCREEN_WIDTH/2){
							right = 0;
						}

					} 

					if (Gdx.input.isTouched(1)) {

						// Touch initialization
						Vector3 t1 = new Vector3();
						t1.set(Gdx.input.getX(1), Gdx.input.getY(1), 0);
						camera.unproject(t1);
						if (t1.x < SCREEN_WIDTH/2 && left > 1){
							left = 1;
						}
						else if (t1.x > SCREEN_WIDTH/2 && right > 1){
							right = 1;
						}

					} 

					if (Gdx.input.isTouched(left)){

						Vector3 touchLeft = new Vector3();
						touchLeft.set(Gdx.input.getX(left), Gdx.input.getY(left), 0);
						camera.unproject(touchLeft);

						if (!currentTouchOne) {
							mover.initialTouch(touchLeft.x, touchLeft.y);
							
							currentTouchOne = true;
						}

						// adjust magnitude of knob with pad in mover<Joypad>
						mover.adjustMagnitude(touchLeft.x, touchLeft.y);
						// move character according to mover<Joypad> x/y differences
						character.moveCharacter(mover.getxDiff(), mover.getyDiff());
						moving = true;

					}
					else {
						mover.resetJoypad();
						currentTouchOne = false;
//						character.decreaseHealth();
//						healthString = "";
//						for (int k = 0; k < character.getHealth() /13; k++){
//							healthString+="|";
//						}
						moving = false;
					}

					if (Gdx.input.isTouched(right)){

						Vector3 touchRight = new Vector3();
						touchRight.set(Gdx.input.getX(right), Gdx.input.getY(right), 0);
						camera.unproject(touchRight);

						if (!currentTouchTwo) {
							shooter.initialTouch(touchRight.x, touchRight.y);
							currentTouchTwo = true;
						}

						shooter.adjustMagnitude(touchRight.x, touchRight.y);

						Double direction = shooter.calculateDirection(false);
						speedX = BULLET_SPEED * Math.cos(direction);
						speedY = BULLET_SPEED * Math.sin(direction);

						bullets.add(new Bullet(character.getControl().x - 8,
								character.getControl().y - 8, speedX, speedY));
						
						Item c = character.getCurrentItem();
						if (c != null){
							if (c.getType() == Item.DOUBLE_SHOT){
								bullets.add(new Bullet(character.getControl().x - 8,
										character.getControl().y - 8, speedX *-1, speedY *-1));
							}
						}
						shooting = true;

					}
					else {
						shooter.resetJoypad();
						currentTouchTwo = false;
						shooting = false;
					}
				}
//				else if (platform == Boxhead.DESKTOP){
//					if (Gdx.input.isTouched(0)) {
//
//						// Touch initialization
//						Vector3 t1 = new Vector3();
//						t1.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
//						camera.unproject(t1);
//					} 
//				}
				
				
				if (tapToStart){
					//gameplay
					// remove bullets that are outside of the screen, or move them
					for (int i = 0; i < bullets.size(); i++) {

						Bullet current = bullets.get(i);
						current.moveBullet();
						if (current.getControl().x > SCREEN_WIDTH
								|| current.getControl().x < 0
								|| current.getControl().y > SCREEN_HEIGHT
								|| current.getControl().y < 0) {
							bullets.remove(i);
							current = null;
						}
					}
					
					Item.expireItems();
					Item.isItemsColliding(character);
					character.expireCurrentItem();

//					printf(enemies.size());
//					printf ("kills:"+kills);
//					printf ("deathLimit:"+deathLimit);
//					printf (isScheduled);
//					printf(enemies.size());

					if (enemies.size() == 0 && !isScheduled && kills >= deathLimit) {
						level++;
						Timer t = Timer.instance;
						t.clear();
						Timer.schedule(new SpawnEnemies(level), 1, ENEMY_SPAWN_DELAY, level * ENEMIES_PER_LEVEL);
						kills = 0;
						deathLimit = level *ENEMIES_PER_LEVEL;
						printf ("newlevel");
						isScheduled = true;

					} else if (enemies.size() > 0 && isScheduled) {
						isScheduled = false;
					}

					try {
						for (int i = 0; i < enemies.size(); i++) {

							if (enemies.get(i).isColliding(character,-18)){

								//character.setHealth(0);
								//Thread.sleep(2000);
								
								character.decreaseHealth();
								healthString = "";
								for (int k = 0; k < character.getHealth() /13; k++){
									healthString+="|";
								}
							}
							else {
								enemies.get(i).move(character.getControl());
							}

							for (int j = 0; j < bullets.size(); j++) {
								if (enemies.get(i).isColliding(bullets.get(j), 30)) {
									enemies.get(i).decreaseHealth();
									//enemies.get(i).setHealth(-1);
									bullets.remove(j);
								}
							}
							if (enemies.get(i).getHealth() < 0){

								
								Item.createNewItem(Item.DOUBLE_SHOT, 
										enemies.get(i).getEnemy().x, 
										enemies.get(i).getEnemy().y);
								

//								float distance = character.getDistance(enemies.get(i));
//								if (distance > 550) currentScore = 10;
//								else if (distance > 400) currentScore = 5;
//								else if (distance > 300) currentScore = 4;
//								else if (distance > 200) currentScore = 3;
//								else if (distance > 100) currentScore = 2;
//								else currentScore = 1;
								currentScore = 1;
								score += currentScore;
								cex = enemies.get(i).getEnemy().x;
								cey = enemies.get(i).getEnemy().y;
								scoreUps.add(new ScoreUp(currentScore,cex,cey));
								explosions.add(new Explosion(cex,cey));

								enemies.remove(i);
								kills++;

							}
						}

					} catch (Exception e) {
						printf(e.getMessage());
					}

					if (character.getHealth() <= 0){
						game = false;
//						try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						gameOver = true;
					}
				}

			}
			//pause
			else {


			}
		}
		//!game
		else {


			if (Gdx.input.isTouched()) {

				// Touch initialization
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);

				if (mainMenu){
					singlePlayerBtn.setPressed(touchPos.x, touchPos.y);
					leaderboardBtn.setPressed(touchPos.x, touchPos.y);
					multiplayerBtn.setPressed(touchPos.x, touchPos.y);
					//signIn_OutBtn.setPressed(touchPos.x, touchPos.y);
					optionsBtn.setPressed(touchPos.x,  touchPos.y);
				}
				else if (gameOver){
					replayBtn.setPressed(touchPos.x, touchPos.y);
					quitToMainBtn.setPressed(touchPos.x, touchPos.y);

				}
				else if (onlineMenu){
					invitePlayersBtn.setPressed(touchPos.x, touchPos.y);
					showInvitationsBtn.setPressed(touchPos.x, touchPos.y);
					backBtn.setPressed(touchPos.x,touchPos.y);
				}
				else if (optionsMenu){
					options_difficulty.checkPressed(touchPos.x, touchPos.y);
					options_sound.checkPressed(touchPos.x, touchPos.y);
					backFromOptionsBtn.setPressed(touchPos.x,touchPos.y);
				}
				
			}else {
				if (mainMenu){
					if (singlePlayerBtn.isReleased()){
						singlePlayerBtn.setPressed(false);
						mainMenu = false;
						game = true;
						start = System.currentTimeMillis();
						current = System.currentTimeMillis();
						resetGame();
						
					}
					else if (leaderboardBtn.isReleased() ){
						leaderboardBtn.setPressed(false);
						if (signedIn){
							android.initiateLeaderboard();
						}
						else {
							android.initiateSignIn();
						}

					}
					else if (multiplayerBtn.isReleased()){
						multiplayerBtn.setPressed(false);
						if (signedIn){
							onlineMenu = true;
							mainMenu = false;	
						}
						else {
							android.initiateSignIn();
						}
					}
					else if (optionsBtn.isReleased()){
						optionsBtn.setPressed(false);
						mainMenu = false;
						optionsMenu = true;
					}
//					else if (signIn_OutBtn.isReleased()){
//						signIn_OutBtn.setPressed(false);
//						if (!signedIn)android.initiateSignIn();
//
//					}

				}
				else if (gameOver){
					if (prefs.getInteger(HIGHSCORE_KEY) < score){
						prefs.putInteger(HIGHSCORE_KEY, score);
						if (signedIn){
							android.submitHighscore(score);	
						}
						prefs.flush();
					}
					if (replayBtn.isReleased()){
						replayBtn.setPressed(false);
						gameOver = false;
						game = true;
						resetGame();
					}
					else if (quitToMainBtn.isReleased()){
						quitToMainBtn.setPressed(false);
						gameOver = false;
						mainMenu = true;
					}

				}
				else if (onlineMenu){
					if (invitePlayersBtn.isReleased()){
						invitePlayersBtn.setPressed(false);
						//onlineMenu = false;
						//createOrJoinRoom = true;
						isServer = true;
						android.initiateRoomCreation();
					}
					else if (showInvitationsBtn.isReleased()){
						showInvitationsBtn.setPressed(false);
						//onlineMenu = false;
						//createOrJoinRoom = true;
						isServer = false;
						android.initiateInviteInbox();

					}
					else if (backBtn.isReleased()){
						backBtn.setPressed(false);
						onlineMenu = false;
						mainMenu = true;
					}
				}
				else if (optionsMenu){
					if (backFromOptionsBtn.isReleased()){
						backFromOptionsBtn.setPressed(false);
						mainMenu = true;
						optionsMenu = false;
					}
					
				}


			}
		}


	}

	NetworkThread nt = null;
	public void startMultiplayerGame(InputStream is, OutputStream os, String npId){
		nt = new NetworkThread(is, os, npId);
		nt.start();
		multiplayerGame = true;
		onlineMenu = false;
		game = true;
		if (isServer)resetGame();
	}


	public void resetGame(){
		printf("resetGame called");
		level = 1;
		score = 0;
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();

		character.resetCharacter();
		healthString = "";

		Timer t = Timer.instance;
		t.clear();
		t = null;
		for (int i = 0 ; i < character.getHealth() /13; i++){
			healthString +="|";
		}

//		Timer.schedule(new SpawnEnemies(level), 0, ENEMY_SPAWN_DELAY, ENEMIES_PER_LEVEL);
	}
	
	public void spawnEnemies(){
		Timer.schedule(new SpawnEnemies(level), 0, ENEMY_SPAWN_DELAY, ENEMIES_PER_LEVEL);
	}


	public static void printf(Object o) {
		Gdx.app.log("pf", o.toString());

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public void onSignedIn (boolean connected){
		signedIn = connected;
	}

	public void onDisconnected (){

	}




}
