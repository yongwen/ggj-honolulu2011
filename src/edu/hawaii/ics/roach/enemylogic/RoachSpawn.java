package edu.hawaii.ics.roach.enemylogic;

//JFC
import java.awt.image.BufferedImage;
//GTGE
import java.awt.image.BufferedImage;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.util.*;
import com.golden.gamedev.engine.BaseLoader;

//ROACH
import edu.hawaii.ics.roach.*;
import edu.hawaii.ics.roach.enemylogic.*;

public class RoachSpawn extends Sprite {
	
	// number of milliseconds before roaches start to spawn
	private static final int ROACH_FREE_START_TIME = 10000;
	// time between roach spawns waves
	private static final int ROACH_WAVE_TIME = 1000;
	// maximum number of roaches spawned at level 1,

	
	private Roach game;
	private int roachesSpawned = 0;
	
    Timer timer = new Timer(ROACH_FREE_START_TIME);
    private BufferedImage[] roachImage;
    
	public RoachSpawn(Roach game,
						BufferedImage image, double x, double y) {
		super(image,x,y);
		this.game = game;
		roachImage = game.getImages("images/roach.png", 12, 1, 0, 11);
		spawnSomeRoaches();
		}

    public void update(long elapsedTime) {
      if (timer.action(elapsedTime)) {
    	  timer.setDelay(ROACH_WAVE_TIME);
    	  spawnSomeRoaches();
      }
    }
    
    private void spawnSomeRoaches() {
		double posx = this.getX();
		double posy = this.getY();
		int level = game.currentLevel();

    	if (roachesSpawned >= game.ROACHES_PER_LEVEL*game.currentLevel()) {
    		timer.setDelay(10000000);
    	} else {
			double ENEMY_SPEED = game.ENEMY_SPEED_NORMAL;
			switch (level) {
				case 0: ENEMY_SPEED = game.ENEMY_SPEED_EASY; break; // easy
				case 2: ENEMY_SPEED = game.ENEMY_SPEED_HARD; break; // hard
			}

			double speed = 0;
			long animationDelay = 170;

			speed = 0.031 * ENEMY_SPEED;
			/*
			    // vampire
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
			*/

			try {
				Enemy enemy = null;
				enemy = new GoTowardFood(game, roachImage, posx, posy, speed, animationDelay);
				if (enemy != null) {
					game.addRoach(enemy);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			roachesSpawned++;
    	}
	
    }
}
