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


public class EnemyItemCollision extends BasicCollisionGroup {

	Roach		game;

	public EnemyItemCollision(Roach game) {
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
		int itemId = s2.getID();
		if  (itemId <= Roach.MAX_FOOD_ID) {
			// collided FOOD item is taken by roach
			game.roachAteFood(s1, s2);		
		} else if (itemId == Roach.EMPTY_TRAP_ID) {
			game.roachTrap(s1, s2);
		} 
		
		/*
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
		*/
	}

}
