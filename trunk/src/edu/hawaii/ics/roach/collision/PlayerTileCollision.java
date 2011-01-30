package edu.hawaii.ics.roach.collision;

// JFC
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.collision.*;

// ROACH GAME
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.RoachSprite;


public class PlayerTileCollision extends CollisionGroup {


	Roach game;


	public PlayerTileCollision(Roach game) {
		this.game = game;
	}


 	// player bounding box
    public CollisionShape getCollisionShape1(Sprite s1) {
		rect1.setBounds(s1.getX()+4, s1.getY()+18, s1.getWidth()-8, s1.getHeight()-18);
		return rect1;
    }


    // tile bounding box
    public CollisionShape getCollisionShape2(Sprite s2) {
		rect2.setBounds(s2.getX(), s2.getY()+8, s2.getWidth(), s2.getHeight()-8);
		return rect2;
    }


	public void collided(Sprite s1, Sprite s2) {
		if (game.isFloor(s2) || s2.getID() == game.BLOCK_ID) {
			// step on floor
			((RoachSprite) s1).stepOn(s2);

			switch (s2.getID()) {
				// step on arrow
				case 29: ((RoachSprite) s1).stepOnArrow(RoachSprite.RIGHT); break;
				case 30: ((RoachSprite) s1).stepOnArrow(RoachSprite.LEFT); break;
				case 31: ((RoachSprite) s1).stepOnArrow(RoachSprite.UP); break;
				case 32: ((RoachSprite) s1).stepOnArrow(RoachSprite.DOWN); break;
			}

		} else {
			
			// hit wall
			revertPosition1();

			// check whether the sprite is stuck in a block
			if (isCollide(s1, s2, getCollisionShape1(s1), getCollisionShape2(s2)) == false) {
				// sprite not stuck
				((RoachSprite) s1).hitWall();
			}
		}
	}

}
