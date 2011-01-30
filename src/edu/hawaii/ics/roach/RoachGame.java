package edu.hawaii.ics.roach;

// JFC
import java.awt.Dimension;
import javax.swing.JPanel;

// GTGE
import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.funbox.GameSettings;

// ROACH GAME
import edu.hawaii.ics.roach.menu.*;


public class RoachGame extends GameEngine {


	public static final int MAIN_MENU = 0;
	public static final int ROACH_GAME = 1;

	public String[] 	levelDesc = new String[] { "Easy", "Normal", "Hard" };
	public int			level = 1;

	public MapData		testLevel;	// set by map builder to test a map


 /****************************************************************************/
 /************************* INIT COMMON RESOURCES ****************************/
 /****************************************************************************/

	public void initResources() {
		// preload all images
		getImage("images/title.jpg");
		getImages("images/lowertileset.png", 10, 5);
		getImages("images/uppertileset.png", 11, 1);
		getImages("images/coins.png", 3, 1);
		getImages("images/player.png", 12, 1);
		getImages("images/charset.png", 12, 7);
		getImages("images/sparkle.png", 6, 1);
		getImages("images/font.png", 16, 6);

		// preload font
		fontManager.getFont(getImages("images/font.png", 16, 6),
					 		" !\"#$%&'()*+,-./" +
							"0123456789:;<=>?" +
							"@ABCDEFGHIJKLMNO" +
							"PQRSTUVWXYZ['\\]^" +
							"_abcdefghijklmno" +
							"pqrstuvwxyz{|}~");
	}


	public GameObject getGame(int GameID) {
		switch (GameID) {
			case MAIN_MENU 		: return new MainMenu(this);
			case ROACH_GAME 	: return new Roach(this);
		}

		return null;
	}


 /****************************************************************************/
 /****************************** MAIN-CLASS **********************************/
 /****************************************************************************/

	public static void main(String[] args) {
		GameSettings settings = new GameSettings(RoachGame.class.getResource("images/title.jpg")) {
			public void start() {
				GameLoader game = new GameLoader();
				game.setup(new RoachGame(), new Dimension(640,480),
						   fullscreen.isSelected(), bufferstrategy.isSelected());
				game.start();
			}

			protected JPanel initSettings() {
				JPanel pane = super.initSettings();
				pane.remove(sound);

				return pane;
			}
		};
	}

	{ distribute = true; }

}
