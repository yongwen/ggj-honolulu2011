package edu.hawaii.ics.roach.menu;

// JFC
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;


public class Credits extends GameObject {


	BufferedImage 	titleImage;
	BufferedImage	creditsImage;
	GameFont		font;


 /****************************************************************************/
 /**************************** CONSTRUCTOR ***********************************/
 /****************************************************************************/

	public Credits(GameEngine parent) {
		super(parent);
	}


 /****************************************************************************/
 /*************************** INIT RESOURCES *********************************/
 /****************************************************************************/

	public void initResources() {
		titleImage = getImage("images/title.png");
		creditsImage = getImage("images/credits.jpg");

		font = fontManager.getFont(getImages("images/font.png", 16, 6));
	}


 /****************************************************************************/
 /***************************** UPDATE GAME **********************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
		if (keyPressed(KeyEvent.VK_ESCAPE) || keyPressed(KeyEvent.VK_ENTER)) {
			playSound("sounds/switch.wav");

			finish();
		}
	}


 /****************************************************************************/
 /***************************** RENDER GAME **********************************/
 /****************************************************************************/

	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		font.drawString(g, "Original Game By:", GameFont.CENTER, 0, 125, getWidth());
		g.drawImage(creditsImage, 120, 160, null);

		font.drawString(g, "Remake By", GameFont.CENTER, 0, 380, getWidth());
		font.drawString(g, "PAULUS TUERAH", GameFont.CENTER, 0, 414, getWidth());
	}

}
