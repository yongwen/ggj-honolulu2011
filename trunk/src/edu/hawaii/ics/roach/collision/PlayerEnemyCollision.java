package edu.hawaii.ics.roach.collision;

// GTGE
import java.awt.event.KeyEvent;

import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.collision.BasicCollisionGroup;
import com.golden.gamedev.object.collision.CollisionShape;

// ROACH GAME
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.Enemy;


public class PlayerEnemyCollision extends BasicCollisionGroup {


	Roach game;

	public PlayerEnemyCollision(Roach game) {
		this.game = game;

		pixelPerfectCollision = true;
	}

    // player bounding box
	public CollisionShape getCollisionShape1(Sprite s1) {
		rect1.setBounds(s1.getX()+4, s1.getY()+18, s1.getWidth()-8, s1.getHeight()-18);
		return rect1;
    }

    // enemy bounding box
	public CollisionShape getCollisionShape2(Sprite s2) {
		rect2.setBounds(s2.getX()+4, s2.getY()+18, s2.getWidth()-8, s2.getHeight()-18);
		return rect2;
    }

	public void collided(Sprite s1, Sprite s2) {
		// game.getCaught();
		if (game.keyPressed(KeyEvent.VK_SPACE))
		{
			game.killRoach(s2);
		}
	}
}
