package edu.hawaii.ics.roach.collision;

// JFC
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.sprite.VolatileSprite;
import com.golden.gamedev.object.collision.BasicCollisionGroup;
import com.golden.gamedev.object.collision.CollisionShape;

// ROACH GAME
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.Text;


public class PlayerItemCollision extends BasicCollisionGroup {

	// item constants
	// see 'images/uppertileset.png'
	public static final int KEY 			= 1;
	public static final int EXIT_POINT 		= 2;
	public static final int RED_GEM 		= 3;
	public static final int GREEN_GEM 		= 4;
	public static final int BLUE_GEM 		= 5;
	public static final int EXTRA_LIFE 		= 6;
	public static final int START_POINT 	= 7;
	public static final int BOOTS 			= 8;
	public static final int FREEZE 			= 9;
	public static final int BLOCK_SCROLL 	= 10;


	Roach		game;


	public PlayerItemCollision(Roach game) {
		this.game = game;

		pixelPerfectCollision = true;
	}

    public CollisionShape getCollisionShape1(Sprite s1) {
        // smaller bounding box
		rect1.setBounds(s1.getX(), s1.getY()+17, s1.getWidth(), 7);

		return rect1;
    }

    public CollisionShape getCollisionShape2(Sprite s2) {
	    // smaller bounding box
        rect2.setBounds(s2.getX(), s2.getY()+13, s2.getWidth(), 11);

        return rect2;
    }

	public void collided(Sprite s1, Sprite s2) {
		if (s2.getID() != EXIT_POINT) {
			// collided item is taken by roach (disappeared)
			// except for exit door
			s2.setActive(false);
		}

		switch (s2.getID()) {
			case KEY: 			game.pickKey(); break;
			case EXIT_POINT: 	game.nextLevel(); break;
			case RED_GEM: 		game.addCoin(200, s2.getX(), s2.getY()); break;
			case GREEN_GEM: 	game.addCoin(500, s2.getX(), s2.getY()); break;
			case BLUE_GEM:		game.addCoin(1000, s2.getX(), s2.getY()); break;
			case EXTRA_LIFE:	game.lifeUp(); break;
			case BOOTS: 		game.speedUp(); break;
			case FREEZE:		game.freezeEnemy(true); break;
			case BLOCK_SCROLL:	game.pickScroll(); break;
		}
	}

}
