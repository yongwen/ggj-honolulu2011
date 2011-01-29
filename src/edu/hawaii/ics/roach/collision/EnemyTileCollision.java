package edu.hawaii.ics.roach.collision;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.collision.CollisionShape;

// ROACH GAME
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.Enemy;


public class EnemyTileCollision extends PlayerTileCollision {


	public EnemyTileCollision(Roach game) {
		super(game);
	}


    // enemy bounding box
	public CollisionShape getCollisionShape1(Sprite s1) {
		rect1.setBounds(s1.getX(), s1.getY()+18, s1.getWidth(), s1.getHeight()-18);
		return rect1;
    }

}
