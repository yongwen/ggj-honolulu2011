package edu.hawaii.ics.roach.enemylogic;

// JFC
import java.awt.image.BufferedImage;
import java.math.*;
//GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.util.Utility;
//ROACH
import edu.hawaii.ics.roach.Enemy;
import edu.hawaii.ics.roach.Roach;
import edu.hawaii.ics.roach.RoachSprite;

public class GoTowardFood extends Enemy {
	
	// Roaches must be within SNIFF_DISTANCE of food to go for it
	private static final double SNIFF_DISTANCE = 50;

	private Roach	game;
	private Timer 	timer;

	public GoTowardFood(Roach game,
						   BufferedImage[] image, double x, double y,
				 		   double speed, long animationDelay) {
		super(game,image,x,y,speed,animationDelay);
		this.game = game;
		
		setAnimate(true);
		setLoopAnim(true);

		timer = new Timer(10);

		moveToFood(-1);
	}
	
	private void moveToFood(int disallowDir) {
		int dir = -1;
		Sprite closestFood = findClosestFood();
		//System.out.println("roach "+this+" is at ("+this.getX()+", "+this.getY()+")");
		
		
		if(closestFood == null) {
			// no more food?
			//System.out.println("no more food?");
			changeDirection(disallowDir);
			timer.setDelay(2000);
			return;
		}
		
		double horizDistance = this.getX() - closestFood.getX();
		double vertDistance = this.getY() - closestFood.getY();
		if(Math.sqrt(horizDistance*horizDistance + vertDistance*vertDistance) > SNIFF_DISTANCE) {
			// food is too far away to sniff out
			//System.out.println("food too far away");
			changeDirection(disallowDir);
			timer.setDelay(2000);
			return;
		}
		
		//System.out.println("vertDistance = "+vertDistance);
		//System.out.println("horizDistance = "+horizDistance);
		if(Math.abs(vertDistance) > Math.abs(horizDistance)) {
			if(vertDistance > 0) {
				if(disallowDir != UP) {
					//System.out.println("moving UP"); 
					setDirection(UP); 
					return;
					}
				if(horizDistance > 0) setDirection(LEFT);
				else setDirection(RIGHT);
				return;
			} else { // vertDistance <= 0
				if(disallowDir != DOWN) {
					//System.out.println("moving DOWN");
					setDirection(DOWN);
					return;
					}
				if(horizDistance > 0) setDirection(LEFT);
				else setDirection(RIGHT);
				return;
			}
		} else { // Math.abs(vertDistance) <= Math.abs(horizDistance)
			if(horizDistance > 0) {
				if(disallowDir != LEFT) {
					//System.out.println("moving LEFT");
					setDirection(LEFT);
					return;
					}
				if(vertDistance > 0) setDirection(UP);
				else setDirection(DOWN);
				return;
			} else { //horizDistance <= 0
				if(disallowDir != RIGHT) {
					//System.out.println("moving RIGHT");
					setDirection(RIGHT);
					return;
					}
				if(vertDistance > 0) setDirection(UP);
				else setDirection(DOWN);
				return;
			}
		}
	}

	private void changeDirection(int disallowDir) {
		int dir = Utility.getRandom(LEFT, DOWN);
		while (dir == disallowDir) {
			dir = Utility.getRandom(LEFT, DOWN);
		}

		//System.out.println("changing direction of "+this+" to "+dir);
		setDirection(dir);
	}
	
	public Boolean isActiveFood(Sprite sprite) {
		if(sprite == null) return false;
		Boolean isActive = sprite.isActive();
		int id = sprite.getID();
		return (sprite != null && sprite.isActive() && sprite.getID() >= 0 && sprite.getID() <= 4);
	}
	
	private Sprite findClosestFood() {
		Sprite[] allFood = game.allFood().getSprites();
		Sprite closestFood = null;
		double closestDistance = 10000000000.0;
		for (Sprite sprite : allFood) {
			if(isActiveFood(sprite)) {
				double distance = Math.pow(this.getX()-sprite.getX(), 2) + Math.pow(this.getY()-sprite.getY(), 2);
				if(distance<closestDistance) {
					closestDistance = distance;
					closestFood = sprite;
				}
			}
			
		}
/*		if(closestFood == null) System.out.println("no more food!");
		else System.out.println("closest food is "+closestFood+" at ("+closestFood.getX()+", "+closestFood.getY()+")");
		*/
		return closestFood;
	}
	
	public void update(long elapsedTime) {
		if (timer.action(elapsedTime)) {
			moveToFood(-1);

			//timer.setDelay(10);
		}

		super.update(elapsedTime);
	}
	
	public void hitWall() {
		super.hitWall();

		// target direction and current direction must be different
		moveToFood(getDirection());

		timer.setDelay(300);
	}
	
	private Sprite findLeastTargetedFood() {
		Sprite[] allFood = game.allFood().getSprites();
		Sprite closestFood = null;
		double closestDistanceSquared = 10000000.0;
		for (Sprite sprite : allFood) {
			double distanceSquared = Math.pow(this.getX()-sprite.getX(), 2) + Math.pow(this.getY()-sprite.getY(), 2);
			if(distanceSquared<closestDistanceSquared) {
				closestDistanceSquared = distanceSquared;
				closestFood = sprite;
			}
		}
		return closestFood;
	}
}
