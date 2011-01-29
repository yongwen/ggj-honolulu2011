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

// ROACH GAME
import edu.hawaii.ics.roach.RoachGame;


public class MainMenu extends GameObject {


	BufferedImage 	titleImage;
	GameFont		font;

	int				option;

	AdvanceSprite	leftOptionSprite,
					rightOptionSprite;

	RoachGame		game;


	public MainMenu(RoachGame parent) {
		super(parent);

		this.game = parent;
	}

	public void initResources() {
		titleImage = getImage("images/title.jpg");

		font = fontManager.getFont(getImages("images/font.png", 16, 6));

		leftOptionSprite = new AdvanceSprite(getImages("images/charset.png",12,7));
		leftOptionSprite.setAnimationFrame(new int[] { 15, 16, 17, 16 });
		leftOptionSprite.setAnimate(true);
		leftOptionSprite.setLoopAnim(true);
		leftOptionSprite.getAnimationTimer().setDelay(160);

		rightOptionSprite = new AdvanceSprite(getImages("images/charset.png",12,7));
		rightOptionSprite.setAnimationFrame(new int[] { 21, 22, 23, 22 });
		rightOptionSprite.setAnimate(true);
		rightOptionSprite.setLoopAnim(true);
		rightOptionSprite.getAnimationTimer().setDelay(160);

		playMusic("music/mainmenu.mid");
	}

	public void update(long elapsedTime) {
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
					bsSound.setActive(!bsSound.isActive());
					bsMusic.setActive(!bsMusic.isActive());

					if (bsMusic.isActive()) {
						bsMusic.play(bsMusic.getLastAudioFile());
					}
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
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		String sound = (bsSound.isActive()) ? "ON" : "OFF";
		String lvl = game.levelDesc[game.level];
		font.drawString(g, "Start Game", 	GameFont.CENTER, 0, 150, getWidth());
		font.drawString(g, "Instructions", 	GameFont.CENTER, 0, 190, getWidth());
		font.drawString(g, "Hi-Scores", 	GameFont.CENTER, 0, 230, getWidth());
		font.drawString(g, "Sound: "+sound,	GameFont.CENTER, 0, 270, getWidth());
		font.drawString(g, "Level: "+lvl,	GameFont.CENTER, 0, 310, getWidth());
		font.drawString(g, "Credits", 		GameFont.CENTER, 0, 350, getWidth());
		font.drawString(g, "Quit Game", 	GameFont.CENTER, 0, 410, getWidth());

		int y = (option == 6) ? 410 : 150+(option*40);
		leftOptionSprite.render(g, 145, y);
		rightOptionSprite.render(g, 475, y);
	}

}
