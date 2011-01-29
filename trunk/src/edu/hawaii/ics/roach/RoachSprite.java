package edu.hawaii.ics.roach;

// JFC
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.sprite.AdvanceSprite;


public class RoachSprite extends AdvanceSprite {


	public static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;

	public static final int[][] animation =
		new int[][] { { 10, 11, 10, 9 }, // left animation
			  		  { 4, 5, 4, 3 },    // right animation
					  { 1, 2, 1, 0 },	 // up animation
					  { 7, 8, 7, 6 } };  // down animation


	private ArrayList	steppedTiles = new ArrayList();
	private ArrayList	lastSteppedTiles = new ArrayList();

	private boolean[]	arrow = new boolean[4];

	private Roach game;


	public RoachSprite(Roach game,
						 BufferedImage[] image, double x, double y) {
		super(image,x,y);

		this.game = game;

		setDirection(DOWN);
		getAnimationTimer().setDelay(100);
	}


	public void update(long elapsedTime) {
		if (arrow[LEFT]) 	moveX(-0.05*elapsedTime);
		if (arrow[RIGHT]) 	moveX( 0.05*elapsedTime);
		if (arrow[UP]) 		moveY(-0.05*elapsedTime);
		if (arrow[DOWN]) 	moveY( 0.05*elapsedTime);
		arrow[LEFT] 	= false;
		arrow[RIGHT] 	= false;
		arrow[UP] 		= false;
		arrow[DOWN] 	= false;

		super.update(elapsedTime);

		// set last stepped tiles
		lastSteppedTiles.clear();
		lastSteppedTiles.addAll(steppedTiles);

		// clear current stepped tiles
		steppedTiles.clear();
	}

	protected void animationChanged(int oldStat, int oldDir,
									int status, int direction) {
		setAnimationFrame(animation[direction]);
	}


	public void stepOn(Sprite tile) {
		// add to current stepped tiles
		steppedTiles.add(tile);

		if (lastSteppedTiles.contains(tile) == false) {
			// send event to the game
			game.stepOn(tile, this);
		}
	}

	public void hitWall() {
	}

	public void stepOnArrow(int dir) {
		arrow[dir] = true;
	}

}
