package edu.hawaii.ics.roach.menu;

// JFC
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.sprite.*;

// ROACH GAME
import edu.hawaii.ics.roach.RoachGame;


public class Instructions extends GameObject {

	BufferedImage 	titleImage;
	GameFont		font;

	AdvanceSprite	roachSprite, enemySprite;
	BufferedImage	burger, burgers, fullPlate, block, arrowKey, trap;

	int				page = 1;


 /****************************************************************************/
 /**************************** CONSTRUCTOR ***********************************/
 /****************************************************************************/

	public Instructions(GameEngine parent) {
		super(parent);
	}


 /****************************************************************************/
 /*************************** INIT RESOURCES *********************************/
 /****************************************************************************/

	public void initResources() {
		titleImage = getImage("images/title.png");
		burger = getImages("images/uppertileset.png", 5, 1)[1];
		burger = getImages("images/uppertileset.png", 5, 1)[2];
		fullPlate = getImages("images/uppertileset.png", 5, 1)[3];
		block = getImages("images/lowertileset.png", 15, 5)[33];
		trap = getImages("images/uppertileset.png", 15, 5)[4];
		arrowKey = getImage("images/arrow.jpg");

		font = fontManager.getFont(getImages("images/font.png", 16, 6));

		roachSprite = new AdvanceSprite(getImages("images/player.png",2,1));
		roachSprite.setAnimationFrame(new int[] { 4, 5, 4, 3 });
		roachSprite.getAnimationTimer().setDelay(160);
		roachSprite.setAnimate(true);
		roachSprite.setLoopAnim(true);

//		enemySprite = new AdvanceSprite(getImages("images/charset.png",12,7));
		enemySprite = new AdvanceSprite(getImages("images/roach.png",12,1));
		enemySprite.setAnimationFrame(new int[] { 4, 5, 4, 3 });
		enemySprite.getAnimationTimer().setDelay(160);
		enemySprite.setAnimate(true);
		enemySprite.setLoopAnim(true);
	}


 /****************************************************************************/
 /***************************** UPDATE GAME **********************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
		roachSprite.update(elapsedTime);
		enemySprite.update(elapsedTime);

		if (keyPressed(KeyEvent.VK_ESCAPE)) {
			finish();
		}

		if (keyPressed(KeyEvent.VK_ENTER)) {
			playSound("sounds/switch.wav");
			if (++page > 2) {
				finish();
			}
		}
	}


 /****************************************************************************/
 /***************************** RENDER GAME **********************************/
 /****************************************************************************/

	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		switch (page) {
	   		case 1:
	            font.drawString(g, "This is you    :", 110, 170);
	            roachSprite.render(g, 500, 150);

	            font.drawString(g, "<SPACE> to Kill:", 110, 220);
	            enemySprite.render(g, 500, 220);

	            font.drawString(g, "<A> to pick up:", 110, 270);
	            g.drawImage(burger, 500, 270, null);

	            font.drawString(g, "<D> to drop:", 110, 320);
	            g.drawImage(burger, 500, 320, null);
			            
	            font.drawString(g, "<Arrow> to move:", 110, 370);
	            g.drawImage(arrowKey, 500, 370, null);

				font.drawString(g, "Press Enter for next page", GameFont.CENTER, 0, 440, getWidth());
			break;

			case 2:
				
	            font.drawString(g, "<W> to lay wall:", 110, 170);
	            g.drawImage(block, 500, 170, null);
	            
	            font.drawString(g, "<E> to drop trap:", 110, 220);
	            g.drawImage(trap, 500, 220, null);

				font.drawString(g, "Press Enter", GameFont.CENTER, 0, 440, getWidth());
	        break;
		}
	}

}
