package edu.hawaii.ics.roach.enemylogic;

// JFC
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.util.Utility;

import edu.hawaii.ics.roach.Enemy;
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.RoachSprite;


public class Zombie extends Enemy {


	private RoachSprite	target;

	private Timer 	timer;


	public Zombie(Roach game,
				  BufferedImage[] image, double x, double y,
				  double speed, long animationDelay, RoachSprite target) {
		super(game,image,x,y,speed,animationDelay);

		setAnimate(true);
		setLoopAnim(true);

		timer = new Timer(10);

		this.target = target;
		findTarget(-1);
	}

	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			findTarget(-1);

			timer.setDelay(10);
		}

		super.update(elapsedTime);
	}


	public void hitWall() {
		super.hitWall();

		// target direction and current direction must be different
		findTarget(getDirection());

		timer.setDelay(300);
	}


	private void findTarget(int disallowDir) {
		int dir = -1;
		if ((int) getX() < (int) target.getX() && disallowDir != RIGHT) {
			dir = RIGHT;
		}
		if (dir == -1 && (int) getX() > (int) target.getX() && disallowDir != LEFT) {
			dir = LEFT;
		}
   		if (dir == -1 && (int) getY() < (int) target.getY() && disallowDir != DOWN) {
			dir = DOWN;
		}
		if (dir == -1 && (int) getY() > (int) target.getY() && disallowDir != UP) {
			dir = UP;
		}

		if (dir == -1 || (getX() == getOldX() && getY() == getOldY())) {
			// can't find target
			changeDirection(disallowDir);
			timer.setDelay(2000);

			return;
		}

		setDirection(dir);
	}


	private void changeDirection(int disallowDir) {
		int dir = Utility.getRandom(LEFT, DOWN);
		while (dir == disallowDir) {
			dir = Utility.getRandom(LEFT, DOWN);
		}

		setDirection(dir);
	}

}
