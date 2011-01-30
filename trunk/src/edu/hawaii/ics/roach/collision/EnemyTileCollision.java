package edu.hawaii.ics.roach.collision;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.collision.CollisionShape;

// ROACH GAME
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.Enemy;
import edu.hawaii.ics.roach.RoachSprite;


public class EnemyTileCollision extends PlayerTileCollision {


	public EnemyTileCollision(Roach game) {
		super(game);
	}


    // enemy bounding box
	public CollisionShape getCollisionShape1(Sprite s1) {
		rect1.setBounds(s1.getX(), s1.getY()+18, s1.getWidth(), s1.getHeight()-18);
		return rect1;
    }
	
	public void collided(Sprite s1, Sprite s2) {
		if (game.isFloor(s2) || s2.getID() == 35) {
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
