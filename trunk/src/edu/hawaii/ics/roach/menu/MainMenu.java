package edu.hawaii.ics.roach.menu;

// JFC
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.sprite.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.engine.audio.JavaLayerMp3Renderer;

// ROACH GAME
import edu.hawaii.ics.roach.RoachGame;


public class MainMenu extends GameObject {

	public static final int 	MENU_Y_SPACING 		= 40;
	public static final int		MENU_Y_START		= 190;

	
	BufferedImage 	titleImage;
	GameFont		font;

	int				option;

	AdvanceSprite	leftOptionSprite,
					rightOptionSprite;

	RoachGame		game;
	
	boolean			soundOn;
	

	public MainMenu(RoachGame parent) {
		super(parent);

		this.game = parent;
		soundOn = true;

	}
	

	public void initResources() {
		titleImage = getImage("images/title.png");

		font = fontManager.getFont(getImages("images/font.png", 16, 6));

		leftOptionSprite = new AdvanceSprite(getImages("images/roach.png",12,1));
		leftOptionSprite.setAnimationFrame(new int[] { 3,4,5 });
		leftOptionSprite.setAnimate(true);
		leftOptionSprite.setLoopAnim(true);
		leftOptionSprite.getAnimationTimer().setDelay(160);

		rightOptionSprite = new AdvanceSprite(getImages("images/roach.png",12,7));
		rightOptionSprite.setAnimationFrame(new int[] { 9,10,11 });
		rightOptionSprite.setAnimate(true);
		rightOptionSprite.setLoopAnim(true);
		rightOptionSprite.getAnimationTimer().setDelay(160);
		
		bsMusic.setBaseRenderer(new JavaLayerMp3Renderer());
		playMusic("music/Intro.mp3");
		
		

	}

	public void update(long elapsedTime) {
		
		if( soundOn )
		{
			if( !bsSound.isActive() ) bsSound.setActive(true);
			if( !bsMusic.isActive() ) bsMusic.setActive(true);
		}
		else
		{
			if( bsSound.isActive() ) bsSound.setActive(false);
			if( bsMusic.isActive() ) bsMusic.setActive(false);
		}
		
		
		leftOptionSprite.update(elapsedTime);
		rightOptionSprite.update(elapsedTime);

		if (keyPressed(KeyEvent.VK_DOWN)) {
			if (++option > 6) {
				option = 0;
			}
			playSound("sounds/time.wav");
		}
		if (keyPressed(KeyEvent.VK_UP)) {
			if (--option < 0) {
				option = 6;
			}
			playSound("sounds/time.wav");
		}
		if (keyPressed(KeyEvent.VK_ENTER)) {
			playSound("sounds/switch.wav");

			switch (option) {
				// start game
				case 0:
					parent.nextGameID = RoachGame.ROACH_GAME;
					finish();
				break;

				// instructions
				case 1:
					Instructions instructions = new Instructions(parent);
					instructions.start();
				break;

				// high score
				case 2:
					HighScore hiscore = new HighScore(parent);
					hiscore.start();
				break;

				// sound on/off
				case 3:
					soundOn = !soundOn;
				break;

				// level
				case 4:
					game.level++;
					if (game.level > 2) {
						game.level = 0;
					}
				break;

				// credits
				case 5:
					Credits credits = new Credits(parent);
					credits.start();
				break;

				// quit game
				case 6:
					finish();
				break;
			}
		}
	}

	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		String sound = (bsSound.isActive()) ? "ON" : "OFF";
		String lvl = game.levelDesc[game.level];
		font.drawString(g, "Start Game", 	GameFont.CENTER, 0, MENU_Y_START+0*MENU_Y_SPACING, getWidth());
		font.drawString(g, "Instructions", 	GameFont.CENTER, 0, MENU_Y_START+1*MENU_Y_SPACING, getWidth());
		font.drawString(g, "Hi-Scores", 	GameFont.CENTER, 0, MENU_Y_START+2*MENU_Y_SPACING, getWidth());
		font.drawString(g, "Sound: "+sound,	GameFont.CENTER, 0, MENU_Y_START+3*MENU_Y_SPACING, getWidth());
		font.drawString(g, "Level: "+lvl,	GameFont.CENTER, 0, MENU_Y_START+4*MENU_Y_SPACING, getWidth());
		font.drawString(g, "Credits", 		GameFont.CENTER, 0, MENU_Y_START+5*MENU_Y_SPACING, getWidth());
		font.drawString(g, "Quit Game", 	GameFont.CENTER, 0, MENU_Y_START+6*MENU_Y_SPACING, getWidth());

		int y = MENU_Y_START+(option*MENU_Y_SPACING);
		leftOptionSprite.render(g, 145, y);
		rightOptionSprite.render(g, 475, y);
		
	}

}
