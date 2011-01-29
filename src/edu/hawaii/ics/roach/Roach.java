package edu.hawaii.ics.roach;

// JFC
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.*;
import java.util.Comparator;
import java.util.ArrayList;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.sprite.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.collision.*;
import com.golden.gamedev.util.*;

// ROACH GAME
import edu.hawaii.ics.roach.enemylogic.*;
import edu.hawaii.ics.roach.collision.*;
import edu.hawaii.ics.roach.menu.HighScore;


public class Roach extends GameObject implements Comparator {


 /*************************** PLAYER CONSTANTS *******************************/

	public static final double 	PLAYER_SPEED 		= 0.033;
	public static final double 	PLAYER_TURBO_SPEED 	= 0.08;
	public static final long 	PLAYER_ANIMATION 	= 140;
	public static final int 	PLAYER_ID 			= 1000;


 /******************************* GAME STATE *********************************/

	public static final int SHOW_TITLE 			= 0;
	public static final int PLAYING 			= 1;
	public static final int GOING_TO_NEXT_LEVEL = 2;
	public static final int LOSE 				= 3;
	public static final int WIN					= 4;

	private int			gameState;
	private String		loseTitle;
	private boolean		confirmExit;


 /**************************** GAME VARIABLES ********************************/

	// map data
	private MapData		levelData;
	private int			level;
	private boolean		TEST_MAP;

	private String		title;
	private int			time;
	private Sprite		exitPoint;

	private int			life;
	private int			score;
	private int			scroll;


 /******************************** PLAY FIELD ********************************/

	private PlayField		playfield;

	private RoachSprite	player;
	private double			playerSpeed;

	private SpriteGroup		PLAYER_GROUP;
	private SpriteGroup		ENEMY_GROUP;
	private SpriteGroup		LOWER_GROUP;
	private SpriteGroup		UPPER_GROUP;


 /********************************* IMAGES ***********************************/

	private BufferedImage[]	lowerImages;
	private BufferedImage[]	upperImages;
	private BufferedImage[]	coins;
	private BufferedImage[] roachImage;

	private GameFont	font;


 /********************************* TIMER ************************************/

	private Timer	timerTime	= new Timer(1000);
	private Timer	timerTitle	= new Timer(2500);
	private Timer	timerNext	= new Timer(2000);
	private Timer	timerLost	= new Timer(3000);
	private Timer	timerWin	= new Timer(4000);
	private Timer	timerFreeze	= new Timer(8000);
	private Timer	timerSpeed	= new Timer(10000);


	private RoachGame	game;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	public Roach(RoachGame parent) {
		super(parent);

		game = parent;
	}


 /****************************************************************************/
 /**************************** INIT RESOURCES ********************************/
 /****************************************************************************/

	public void initResources() {
		lowerImages = getImages("images/lowertileset.png", 10, 5);
		upperImages = getImages("images/uppertileset.png", 11, 1);
		coins = getImages("images/coins.png", 3, 1);
		roachImage = getImages("images/roach.png", 12, 1);

		font = fontManager.getFont(getImages("images/font.png", 16, 6));

		// create background
		int w = 480, h = 360;
		Background backgr = new Background(w, h);

		// the background is drawn from 75, 67
		int offsetX = 75, offsetY = 67;
		backgr.setClip(offsetX, offsetY, w, h);

		playfield = new PlayField(backgr);
		playfield.setComparator(this);

		LOWER_GROUP  = playfield.addGroup(new SpriteGroup("Lower"));
		UPPER_GROUP  = playfield.addGroup(new SpriteGroup("Upper"));
		ENEMY_GROUP  = playfield.addGroup(new SpriteGroup("Enemy"));
		PLAYER_GROUP = playfield.addGroup(new SpriteGroup("Player"));

		playfield.addCollisionGroup(PLAYER_GROUP, LOWER_GROUP, new PlayerTileCollision(this));
		playfield.addCollisionGroup(PLAYER_GROUP, UPPER_GROUP, new PlayerItemCollision(this));
		playfield.addCollisionGroup(ENEMY_GROUP, LOWER_GROUP, new EnemyTileCollision(this));
		playfield.addCollisionGroup(PLAYER_GROUP, ENEMY_GROUP, new PlayerEnemyCollision(this));


		// init game variable
		level = 1;
		life = (game.level == 0) ? 4 : 3; // level easy: 4 lifes
		score = 0;

		if (game.testLevel == null) {
			levelData = loadMap(level);

		} else {
			levelData = game.testLevel;
			TEST_MAP = true;
			playMusic("music/music1.mid");
		}

		initLevel(levelData);

		gameState = SHOW_TITLE;
	}

	private void initLevel(MapData data) {
		if (data == null) {
			return;
		}

		playfield.clearPlayField();

		// add player
		player = new RoachSprite(this, roachImage, 100, 100);
		player.setID(PLAYER_ID);
		player.getAnimationTimer().setDelay(PLAYER_ANIMATION);
		playerSpeed = PLAYER_SPEED;
		PLAYER_GROUP.add(player);

		timerFreeze.setActive(false);
		timerSpeed.setActive(false);
		scroll = 0;

        // construct map
		title = data.title;
        time = data.time;

		// construct lower and upper tiles
        int[][] lowerTiles = data.lowerTiles;
        int[][] upperTiles = data.upperTiles;
		for (int j=0;j < 15;j++)
		for (int i=0;i < 20;i++) {
        	int lower = data.lowerTiles[i][j];
        	int upper = data.upperTiles[i][j];

			if (lower > 0) {
				if (lower >= 37 && lower <= 40) {
					// animated block
					AnimatedSprite animatedBlock = new AnimatedSprite(lowerImages,i*24,j*24);
					animatedBlock.setAnimationFrame(37, 40);
					animatedBlock.setFrame(37-lower+1);
					animatedBlock.getAnimationTimer().setDelay(150);
					animatedBlock.setAnimate(true);
					animatedBlock.setLoopAnim(true);
					animatedBlock.setID(lower);
					animatedBlock.setLayer(isFloor(animatedBlock) ? 1 : 0);
					LOWER_GROUP.add(animatedBlock);

				} else {
					// static block
					Sprite block = new Sprite(lowerImages[lower],i*24,j*24);
					block.setID(lower);
					block.setLayer(isFloor(block) ? 1 : 0);
					LOWER_GROUP.add(block);
				}
			}

			if (upper > 0) {
				if (upper == 2) {
					// exit point
					exitPoint = new Sprite(upperImages[2], i*24, j*24);
					exitPoint.setID(2);
				} else if (upper == 7) {
					// start point
					player.setLocation(i*24, j*24);
				} else {
					Sprite item = new Sprite(upperImages[upper],i*24,j*24);
					item.setID(upper);
					UPPER_GROUP.add(item);
				}
			}
        }

        // construct enemies
		int count = data.enemyListData.length;
        for (int i=0;i < count;i++) {
	        int[] prop = (int[]) data.enemyListData[i];
			int charType = prop[0];
			int logicType = prop[1];
			double posx = prop[2] * 24;
			double posy = prop[3] * 24;
			BufferedImage[] image = getImages("images/charset.png", 12, 7, charType*12, (charType*12)+11);

			double ENEMY_SPEED = 1.0;
			switch (game.level) {
				case 0: ENEMY_SPEED = 0.8; break; // easy
				case 1: ENEMY_SPEED = 1.0; break; // normal
				case 2: ENEMY_SPEED = 1.4; break; // hard
			}

			double speed = 0;
			long animationDelay = 0;
			switch (charType) {
				// vamp
				case 0: speed = 0.023 * ENEMY_SPEED; animationDelay = 300; break;
				// witch
				case 1: speed = 0.031 * ENEMY_SPEED; animationDelay = 170; break;
				// bully
				case 2: speed = 0.026 * ENEMY_SPEED; animationDelay = 250; break;
				// light knight
				case 3: speed = 0.027 * ENEMY_SPEED; animationDelay = 280; break;
				// dwarf
				case 4: speed = 0.021 * ENEMY_SPEED; animationDelay = 350; break;
				// clown
				case 5: speed = 0.029 * ENEMY_SPEED; animationDelay = 240; break;
				// dragon
				case 6: speed = 0.034 * ENEMY_SPEED; animationDelay = 250; break;
			}

			try {
				Enemy enemy = null;
				switch (logicType) {
					// stand still
					case 0: enemy = new Enemy(this, image, posx, posy, speed, animationDelay); break;
					// left-right patrol
					case 1: enemy = new LeftRightPatrol(this, image, posx, posy, speed, animationDelay); break;
					// up-down patrol
					case 2: enemy = new UpDownPatrol(this, image, posx, posy, speed, animationDelay); break;
					// turn left always
					case 3: enemy = new TurnLeftAlways(this, image, posx, posy, speed, animationDelay); break;
					// turn right always
					case 4: enemy = new TurnRightAlways(this, image, posx, posy, speed, animationDelay); break;
					// turner
					case 5: enemy = new Turner(this, image, posx, posy, speed, animationDelay); break;
					// zombie
					case 6: enemy = new Zombie(this, image, posx, posy, speed, animationDelay, player); break;
				}
				if (enemy != null) {
					ENEMY_GROUP.add(enemy);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private MapData loadMap(int index) {
		MapData data = null;

		String filename = "levels/level" + index + ".map";

		try {
			data = new MapData();
			data.load(bsIO.getStream(filename));

			playMusic("music/music" + (index / 5 + 1) + ".mid");
        } catch (Exception e) {
	        data = null;
//            System.err.println("Failed to load map: "+filename);
        }

        return data;
	}

	// sorting game object (sprites/tiles)
	// first sort based on layer, higher layer will be placed on bottom (floor = layer 1)
	// then sort based on y-position, greater y placed on top
	public int compare(Object o1, Object o2) {
		Sprite s1 = (Sprite) o1;
		Sprite s2 = (Sprite) o2;

		if (s1.getLayer() != s2.getLayer()) {
			return (s2.getLayer() - s1.getLayer());
		}

		return ((int) s1.getY() - (int) s2.getY());
	}

	private void showHiScore() {
		HighScore highscore = new HighScore(parent);
		highscore.insertScore(score,level,game.level);
		highscore.start();

		// back to main menu
		parent.nextGameID = RoachGame.MAIN_MENU;
		finish();
	}


 /****************************************************************************/
 /***************************** UPDATE GAME **********************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
		if (confirmExit) {
			if (keyPressed(KeyEvent.VK_Q) || keyPressed(KeyEvent.VK_ESCAPE)) {
				showHiScore();

			} else if (keyPressed(KeyEvent.VK_R)) {
				// restart level
				life--;

				if (life >= 0) {
					gameState = SHOW_TITLE;
					initLevel(loadMap(level));
				} else {
					gameState = LOSE;
				}

				confirmExit = false;

			} else if (keyPressed(KeyEvent.VK_C) || keyPressed(KeyEvent.VK_ENTER)) {
				// resume game
				confirmExit = false;
			}

			return;
		}


		switch (gameState) {
		// IN-GAME
		case PLAYING:
			if (keyPressed(KeyEvent.VK_ESCAPE)) {
				if (!TEST_MAP) {
					confirmExit = true;
				} else {
					finish();
				}
			}

			if (keyDown(KeyEvent.VK_SHIFT)) {
				// turbo mode
				elapsedTime += 30;
			}


			if (timerTime.action(elapsedTime)) {
				time--;
				if (time <= 0) {
					timeUp();
				} else if (time < 10) {
					playSound("sounds/time.wav");
				}
			}
			if (timerFreeze.action(elapsedTime)) {
				freezeEnemy(false);
			}
			if (timerSpeed.action(elapsedTime)) {
				playerSpeed = PLAYER_SPEED;
			}

			playfield.update(elapsedTime);

			player.setAnimate(false);

			if (keyPressed(KeyEvent.VK_SPACE) && scroll > 0) {
				// create blocking stone
				int destX = (int) ((player.getX()+12) / 24),
					destY = (int) ((player.getY()+12) / 24);
				switch (player.getDirection()) {
					case RoachSprite.LEFT:  destX = (int) ((player.getX()+6) / 24) - 1; break;
					case RoachSprite.RIGHT: destX = (int) ((player.getX()+18) / 24) + 1; break;
					case RoachSprite.UP: 	  destY = (int) ((player.getY()+10) / 24) - 1; break;
					case RoachSprite.DOWN:  destY = (int) ((player.getY()+24) / 24) + 1; break;
				}

				Sprite tile = getTileAt(destX, destY);
				if (tile != null && isFloor(tile) &&
					getObjectAt(destX, destY) == null) {
					// create block
					scroll--;
					switchTile(tile, 33);

					playSound("sounds/block.wav");
				} else {
					// unable create block
					playSound("sounds/scroll2.wav");
				}
			}

			if (keyDown(KeyEvent.VK_LEFT) && keyDown(KeyEvent.VK_RIGHT)) {
				// pressing both left and right key
				player.setFrame(0);
				player.setDirection(player.DOWN);

			} else if (keyDown(KeyEvent.VK_LEFT)) {
				// moving left
				player.moveX(-playerSpeed * elapsedTime);
				player.setAnimate(true);
				player.setDirection(player.LEFT);

			} else if (keyDown(KeyEvent.VK_RIGHT)) {
				// moving right
				player.moveX(playerSpeed * elapsedTime);
				player.setAnimate(true);
				player.setDirection(player.RIGHT);

			} else if (keyDown(KeyEvent.VK_UP) && keyDown(KeyEvent.VK_DOWN)) {
				// pressing both up and down key
				player.setFrame(0);
				player.setDirection(player.DOWN);

			} else if (keyDown(KeyEvent.VK_UP)) {
				// moving up
				player.moveY(-playerSpeed * elapsedTime);
				player.setAnimate(true);
				player.setDirection(player.UP);

			} else if (keyDown(KeyEvent.VK_DOWN)) {
				// moving down
				player.moveY(playerSpeed * elapsedTime);
				player.setAnimate(true);
				player.setDirection(player.DOWN);
			}
		break;


		// INTRO SHOWING TITLE
		case SHOW_TITLE:
			if (timerTitle.action(elapsedTime) ||
				keyPressed(KeyEvent.VK_ENTER)) {
				gameState = PLAYING;
			}
		break;


		// NEXT LEVEL
		case GOING_TO_NEXT_LEVEL:
			if (timerNext.action(elapsedTime) ||
				keyPressed(KeyEvent.VK_ENTER)) {
				gameState = SHOW_TITLE;
				level++;
				MapData nextLevelData = loadMap(level);
				if (nextLevelData != null) {
					initLevel(nextLevelData);
				} else {
					// last level
					gameState = WIN;
				}
			}
		break;


		// GET CAUGHT - TIME UP
		case LOSE:
			if (timerLost.action(elapsedTime) ||
				keyPressed(KeyEvent.VK_ENTER)) {
				if (life >= 0) {
					gameState = PLAYING;
					initLevel(loadMap(level));
				} else {
					// die!
					showHiScore();
				}
			}
		break;


		// YOU WIN!!!
		case WIN:
			if (timerWin.action(elapsedTime) ||
				keyPressed(KeyEvent.VK_ENTER)) {
				// win!!
				level--; // save hiscore on the last level
				showHiScore();
			}
		break;
		}
	}


 /****************************************************************************/
 /****************************** RENDER GAME *********************************/
 /****************************************************************************/

	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (confirmExit) {
			font.drawString(g, "[C]ONTINUE", 220, 180);
			font.drawString(g, "[R]ESTART", 220, 220);
			font.drawString(g, "[Q]UIT", 220, 260);

			return;
		}

		switch (gameState) {
		case PLAYING:
			playfield.render(g);
			// draw title
			font.drawString(g, title, GameFont.CENTER, 0, 15, getWidth());
			// draw time
			font.drawString(g, "Time:" + time, 455, 440);
			// draw score
			font.drawString(g, "Score:" + String.valueOf(score), 10, 440);
			if (scroll > 0) {
				// draw scroll
				g.drawImage(upperImages[10], 13, 300, null);
				font.drawString(g, String.valueOf(scroll), 10, 333);
			}
			// draw life
			g.drawImage(roachImage[7], 13, 365, null);
			font.drawString(g, String.valueOf(life), 10, 398);
		break;


		case SHOW_TITLE:
			font.drawString(g, "Level "+level, GameFont.CENTER, 0, 187, getWidth());
			font.drawString(g, title, GameFont.CENTER, 0, 224, getWidth());
		break;


		case GOING_TO_NEXT_LEVEL:
			font.drawString(g, "Level Complete! Well Done!", GameFont.CENTER, 0, 224, getWidth());
		break;


		case LOSE:
			if (life >= 0) {
				font.drawString(g, loseTitle, GameFont.CENTER, 0, 224, getWidth());
			} else {
				font.drawString(g, "Argh! You're Dead!", GameFont.CENTER, 0, 224, getWidth());
			}
		break;


		case WIN:
			font.drawString(g, "CONGRATULIONS! YOU'VE WON!!", GameFont.CENTER, 0, 224, getWidth());
		break;
		}
	}


 /****************************************************************************/
 /***************************** GAME LOGIC ***********************************/
 /****************************************************************************/

	public void pickKey() {
		// check whether all keys has been pick
		int size = UPPER_GROUP.getSize();
		Sprite[] s = UPPER_GROUP.getSprites();
		boolean showExitPoint = true;
		for (int i=0;i < size;i++) {
			if (s[i].isActive() && s[i].getID() == 1) {
				showExitPoint = false;
				break;
			}
		}

		playSound("sounds/key.wav");

		// if all keys has been taken, show exit point
		if (showExitPoint) {
			UPPER_GROUP.add(exitPoint);

			BufferedImage[] img = getImages("images/sparkle.png", 6, 1);
			VolatileSprite sparkle = new VolatileSprite(img, exitPoint.getX(), exitPoint.getY());
			sparkle.getAnimationTimer().setDelay(150);
			playfield.add(sparkle);

			playSound("sounds/sparkle.wav");
		}
	}

	public void pickScroll() {
		scroll++;
		score += 50;

		playSound("sounds/scroll.wav");
	}

	public void speedUp() {
		playerSpeed = PLAYER_TURBO_SPEED;
		timerSpeed.setActive(true);
		score += 100;

		playSound("sounds/speedup.wav");
	}

	public void lifeUp() {
		life++;
		score += 200;

		playSound("sounds/lifeup.wav");
	}

	public void freezeEnemy(boolean freeze) {
		Sprite[] enemy = ENEMY_GROUP.getSprites();
		int size = ENEMY_GROUP.getSize();
		for (int i=0;i < size;i++) {
			((Enemy) enemy[i]).setFreeze(freeze);
		}

		timerFreeze.setActive(freeze);

		if (freeze) {
			score += 50;

			playSound("sounds/freeze.wav");
		}
	}

	public void addCoin(int score, double x, double y) {
		this.score += score;

		switch (score) {
			case 200: playfield.add(new Text(coins[0],x,y)); break;
			case 500: playfield.add(new Text(coins[1],x,y)); break;
			case 1000: playfield.add(new Text(coins[2],x,y)); break;
		}

		playSound("sounds/coins.wav");
	}

	public void nextLevel() {
		gameState = GOING_TO_NEXT_LEVEL;

		score += time * 5;
		score += level * 200;

		if (TEST_MAP) {
			finish();
		}
	}

	public void getCaught() {
		life--;
		gameState = LOSE;
		loseTitle = "Oops! They got you..";

		if (TEST_MAP) {
			finish();
		}
	}

	public void timeUp() {
		life--;
		gameState = LOSE;
		loseTitle = "You have to be a bit quicker";

		if (TEST_MAP) {
			finish();
		}
	}

	public void stepOn(Sprite tile, Sprite s) {
		switch (tile.getID()) {
			case 4: playSound("sounds/switch.wav"); switchTile(tile, 5); switchTile(10, 11); break;
			case 5: playSound("sounds/switch.wav"); switchTile(tile, 4); switchTile(11, 10); break;

			case 6: playSound("sounds/switch.wav"); switchTile(tile, 7); switchTile(12, 13); break;
			case 7: playSound("sounds/switch.wav"); switchTile(tile, 6); switchTile(13, 12); break;

			case 8: playSound("sounds/switch.wav"); switchTile(tile, 9); switchTile(14, 15); break;
			case 9: playSound("sounds/switch.wav"); switchTile(tile, 8); switchTile(15, 14); break;

			case 16: playSound("sounds/switch2.wav"); switchTile(tile, 17); switchTile(22, 23); break;
			case 17: playSound("sounds/switch2.wav"); switchTile(tile, 16); switchTile(23, 22); break;

			case 18: playSound("sounds/switch2.wav"); switchTile(tile, 19); switchTile(24, 25); break;
			case 19: playSound("sounds/switch2.wav"); switchTile(tile, 18); switchTile(25, 24); break;

			case 20: playSound("sounds/switch2.wav"); switchTile(tile, 21); switchTile(26, 27); break;
			case 21: playSound("sounds/switch2.wav"); switchTile(tile, 20); switchTile(27, 26); break;

			// teleport in
			case 28:
				ArrayList teleports = new ArrayList();
				Sprite[] sprite = LOWER_GROUP.getSprites();
				int size = LOWER_GROUP.getSize();
				for (int i=0;i < size;i++) {
					if (sprite[i].getID() == 36) {
						// teleport out
						teleports.add(sprite[i]);
					}
				}

				if (teleports.size() > 0) {
					int rnd = getRandom(0, teleports.size()-1);
					Sprite out = (Sprite) teleports.get(rnd);
					s.setLocation(out.getX(), out.getY());

					BufferedImage[] img = getImages("images/sparkle.png", 6, 1);
					VolatileSprite sparkle = new VolatileSprite(img, tile.getX(), tile.getY());
					sparkle.getAnimationTimer().setDelay(150);
					playfield.add(sparkle);

					playSound("sounds/teleport.wav");
				}
			break;

		}
	}

	private void switchTile(int oldID, int newID) {
		Sprite[] sprite = LOWER_GROUP.getSprites();
		int size = LOWER_GROUP.getSize();
		for (int i=0;i < size;i++) {
			if (sprite[i].getID() == oldID) {
				switchTile(sprite[i], newID);
			}
		}
	}

	private Sprite getTileAt(int tileX, int tileY) {
		int x = tileX * 24;
		int y = tileY * 24;
		Sprite[] sprite = LOWER_GROUP.getSprites();
		int size = LOWER_GROUP.getSize();
		for (int i=0;i < size;i++) {
			if (sprite[i].getX() == x &&
				sprite[i].getY() == y) {
				return sprite[i];
			}
		}

		return null;
	}

	private Sprite getObjectAt(int tileX, int tileY) {
		int x = tileX * 24;
		int y = tileY * 24;
		Sprite[] sprite = UPPER_GROUP.getSprites();
		int size = UPPER_GROUP.getSize();
		for (int i=0;i < size;i++) {
			if (sprite[i].getX() == x &&
				sprite[i].getY() == y) {
				return sprite[i];
			}
		}

		return null;
	}

	private void switchTile(Sprite tile, int newID) {
		tile.setID(newID);
		tile.setImage(lowerImages[newID]);

		tile.setLayer(isFloor(tile) ? 1 : 0);
	}

	// see 'images/lowertileset.png'
	public boolean isFloor(Sprite tile) {
		switch (tile.getID()) {
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 11:
			case 13:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 23:
			case 25:
			case 27:
			case 28:
			case 29:
			case 30:
			case 31:
			case 32:
			case 36: return true; 	// floor
			default: return false;	// wall
		}
	}

}