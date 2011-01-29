package edu.hawaii.ics.roach.enemylogic;

// JFC
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.util.Utility;

// ROACH GAME
import edu.hawaii.ics.roach.Enemy;
import edu.hawaii.ics.roach.Roach;


public class Turner extends Enemy {


	private Timer 	timer;


	public Turner(Roach game,
				  BufferedImage[] image, double x, double y,
				  double speed, long animationDelay) {
		super(game,image,x,y,speed,animationDelay);

		changeDirection();
		setAnimate(true);
		setLoopAnim(true);

		timer = new Timer(2500); // change direction every 2.5 sec
	}


	public void update(long elapsedTime) {
		super.update(elapsedTime);

		if (timer.action(elapsedTime) && Utility.getRandom(0, 2) > 0) {
			changeDirection();
		}
	}


	public void hitWall() {
		super.hitWall();

		int oldDir = getDirection();
		int dir = Utility.getRandom(LEFT, DOWN);
		while (dir == oldDir) {
			dir = Utility.getRandom(LEFT, DOWN);
		}

		setDirection(dir);
	}


	private void changeDirection() {
		setDirection(Utility.getRandom(LEFT, DOWN));
	}

}
