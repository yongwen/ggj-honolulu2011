package edu.hawaii.ics.roach.enemylogic;

// JFC
import java.awt.image.BufferedImage;

// ROACH GAME
import edu.hawaii.ics.roach.Enemy;
import edu.hawaii.ics.roach.Roach;


public class LeftRightPatrol extends Enemy {


	public LeftRightPatrol(Roach game,
						   BufferedImage[] image, double x, double y,
				 		   double speed, long animationDelay) {
		super(game,image,x,y,speed,animationDelay);

		setDirection(RIGHT);
		setAnimate(true);
		setLoopAnim(true);
	}


	public void hitWall() {
		super.hitWall();

		setDirection((getDirection() == RIGHT) ? LEFT : RIGHT);
	}

}
