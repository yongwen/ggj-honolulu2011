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
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		
		font.drawString(g, "Written in 48 hrs", GameFont.CENTER, 0, 175, getWidth());
		font.drawString(g, "for Global Game Jam 2011", GameFont.CENTER, 0, 200, getWidth());
		font.drawString(g, "Based on the Warlock game", GameFont.CENTER, 0, 250, getWidth());
		font.drawString(g, "by Paulus Tuerah", GameFont.CENTER, 0, 275, getWidth());
		
		font.drawString(g, "Authors:", GameFont.CENTER, 0, 325, getWidth());
		font.drawString(g, "David Chin", GameFont.CENTER, 0, 350, getWidth());
		font.drawString(g, "Gorm Lai", GameFont.CENTER, 0, 375, getWidth());
		font.drawString(g, "Robert Puckett", GameFont.CENTER, 0, 400, getWidth());
		font.drawString(g, "Yongwen Xu", GameFont.CENTER, 0, 425, getWidth());
		
	}

}
