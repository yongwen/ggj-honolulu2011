package edu.hawaii.ics.roach.enemylogic;

// JFC
import java.awt.image.BufferedImage;

// ROACH GAME
import edu.hawaii.ics.roach.Enemy;
import edu.hawaii.ics.roach.Roach;


public class TurnLeftAlways extends Enemy {


	public TurnLeftAlways(Roach game,
						  BufferedImage[] image, double x, double y,
				 		  double speed, long animationDelay) {
		super(game,image,x,y,speed,animationDelay);

		setDirection(DOWN);
		setAnimate(true);
		setLoopAnim(true);
	}


	public void hitWall() {
		super.hitWall();

		switch (getDirection()) {
			case UP: 	setDirection(LEFT); break;
			case LEFT: 	setDirection(DOWN); break;
			case DOWN: 	setDirection(RIGHT); break;
			case RIGHT:	setDirection(UP); break;
		}
	}

}
