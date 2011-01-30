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
	BufferedImage	key, exitDoor, scroll, block, arrowKey;

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
		key = getImages("images/uppertileset.png", 11, 1)[1];
		exitDoor = getImages("images/uppertileset.png", 11, 1)[2];
		scroll = getImages("images/uppertileset.png", 11, 1)[10];
		block = getImages("images/lowertileset.png", 10, 5)[33];
		arrowKey = getImage("images/arrow.jpg");

		font = fontManager.getFont(getImages("images/font.png", 16, 6));

		roachSprite = new AdvanceSprite(getImages("images/player.png",2,1));
		roachSprite.setAnimationFrame(new int[] { 4, 5, 4, 3 });
		roachSprite.getAnimationTimer().setDelay(160);
		roachSprite.setAnimate(true);
		roachSprite.setLoopAnim(true);

		enemySprite = new AdvanceSprite(getImages("images/charset.png",12,7));
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
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		switch (page) {
	   		case 1:
	            font.drawString(g, "This is you    :", 110, 150);
	            roachSprite.render(g, 500, 150);

	            font.drawString(g, "Avoid these    :", 110, 200);
	            enemySprite.render(g, 500, 200);

	            font.drawString(g, "Collect these  :", 110, 250);
	            g.drawImage(key, 500, 250, null);

	            /*
	            font.drawString(g, "To get to this :", 110, 300);
	            g.drawImage(exitDoor, 500, 300, null);
				*/
	            
	            font.drawString(g, "Move key       :", 110, 300);
	            g.drawImage(arrowKey, 470, 300, null);

	            font.drawString(g, "press <SPACE> to kill:", 110, 350);

				font.drawString(g, "Press Enter", GameFont.CENTER, 0, 430, getWidth());
			break;

			case 2:
	            font.drawString(g, "Collect these  :", 110, 150);
	            g.drawImage(scroll, 500, 150, null);

	            font.drawString(g, "And press <SPACE>", 110, 200);

				font.drawString(g, "to create these:", 110, 250);
	            g.drawImage(block, 500, 250, null);

				font.drawString(g, "Press Enter", GameFont.CENTER, 0, 430, getWidth());
	        break;
		}
	}

}
