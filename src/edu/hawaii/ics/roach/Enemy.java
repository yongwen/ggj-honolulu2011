package edu.hawaii.ics.roach;

// JFC
import java.awt.image.BufferedImage;


public class Enemy extends RoachSprite {


	private double speed;
	private boolean freeze;


	public Enemy(Roach game,
				 BufferedImage[] image, double x, double y,
				 double speed, long animationDelay) {
		super(game,image,x,y);

		this.speed = speed;

		getAnimationTimer().setDelay(animationDelay);
	}


	public void setDirection(int dir) {
		if (freeze) return;

		super.setDirection(dir);

		switch (dir) {
			case LEFT: 	setSpeed(-speed, 0); break;
			case RIGHT: setSpeed(speed, 0); break;
			case UP: 	setSpeed(0, -speed); break;
			case DOWN: 	setSpeed(0, speed); break;
		}
	}

	protected void updateMovement(long elapsedTime) {
		if (!freeze) {
			// update movement only when the sprite not freeze
			super.updateMovement(elapsedTime);
		}
	}


	// make enemy freeze/unfreeze
	public void setFreeze(boolean b) {
		freeze = b;
	}

	public boolean isFreeze() {
		return freeze;
	}

}
