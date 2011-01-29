package edu.hawaii.ics.roach.menu;

// JFC
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.io.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.util.Utility;

// ROACH GAME
import edu.hawaii.ics.roach.RoachGame;
import edu.hawaii.ics.roach.HiScoreData;


public class HighScore extends GameObject {

	public static final int VIEW_SCORE 	  = 0;
	public static final int INSERT_SCORE  = 1;

	int				gameState = VIEW_SCORE;
	int				insertScore = 0;

	BufferedImage	titleImage;
	GameFont		font;

	HiScoreData[]	hiscore;
	HiScoreData		addscore;
	boolean			newHiScore;

	Timer			blinkTimer = new Timer(300);
	boolean			blink;


 /****************************************************************************/
 /**************************** CONSTRUCTOR ***********************************/
 /****************************************************************************/

	public HighScore(GameEngine parent) {
		super(parent);
	}


 /****************************************************************************/
 /*************************** INIT RESOURCES *********************************/
 /****************************************************************************/

	public void initResources() {
		titleImage = getImage("images/title.jpg");

		font = fontManager.getFont(getImages("images/font.png", 16, 6));

		hiscore = new HiScoreData[9];
		for (int i=0;i < hiscore.length;i++) {
			hiscore[i] = new HiScoreData();
		}

		// loading file
		try {
			File f = bsIO.getFile("hiscore.dat", bsIO.WORKING_DIRECTORY);
			DataInputStream din = new DataInputStream(new FileInputStream(f));
			for (int i=0;i < hiscore.length;i++) {
				hiscore[i] = new HiScoreData(din.readUTF(), din.readUTF(),
											 din.readUTF(), din.readUTF());
			}
		} catch (Exception e) {
			// error occured when loading hi-score file
			// either by corrupted file or file has not been created
			saveHiScore();
		}

		if (addscore != null) {
			newHiScore = false;
			for (int i=0;i < hiscore.length;i++) {
				if (Integer.parseInt(addscore.score) > Integer.parseInt(hiscore[i].score)) {
					// new high-score
					for (int j=hiscore.length-1;j > i;j--) {
						hiscore[j] = hiscore[j-1];
					}
					hiscore[i] = addscore;
					newHiScore = true;
					gameState = INSERT_SCORE;
					break;
				}
			}

			if (newHiScore == false) {
				// failed to achieve high score
				// listed at the bottom
				addscore.name = "you";
			}
		}

		playMusic("music/mainmenu.mid");
	}

	private void saveHiScore() {
		try {
			File f = bsIO.setFile("hiscore.dat", bsIO.WORKING_DIRECTORY);
			DataOutputStream dout = new DataOutputStream(new FileOutputStream(f));
			for (int i=0;i < hiscore.length;i++) {
				dout.writeUTF(hiscore[i].score);
				dout.writeUTF(hiscore[i].name);
				dout.writeUTF(hiscore[i].stage);
				dout.writeUTF(hiscore[i].level);
			}
		} catch (Exception e) {
		}
	}

	public void insertScore(int score, int stage, int level) {
		addscore = new HiScoreData(score, stage, level);
	}


 /****************************************************************************/
 /***************************** UPDATE GAME **********************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
		switch (gameState) {
			case VIEW_SCORE:
				if (keyPressed(KeyEvent.VK_ESCAPE) || keyPressed(KeyEvent.VK_ENTER)) {
					finish();
				}
			break;

			case INSERT_SCORE:
				if (blinkTimer.action(elapsedTime)) {
					blink = !blink;
				}

				int keyCode = bsInput.getKeyPressed();
				switch (keyCode) {
					case KeyEvent.VK_BACK_SPACE:
						addscore.name = addscore.name.substring(0, addscore.name.length()-1);
					break;

					case KeyEvent.VK_ESCAPE:
					case KeyEvent.VK_ENTER:
						saveHiScore();
						finish();
					break;

					default:
						String st = getKeyText(keyCode);
						if (st != null && addscore.name.length() < 3) {
							addscore.name += st;
						}
					break;
				}

			break;
		}
	}

	private String getKeyText(int keyCode) {
		String st = null;

		switch (keyCode) {
			case KeyEvent.VK_SPACE: st = " "; break;
			case KeyEvent.VK_MINUS: st = "-"; break;
			case KeyEvent.VK_BACK_QUOTE: st = "`"; break;
			case KeyEvent.VK_QUOTE: st = "'"; break;
			default:
				st = KeyEvent.getKeyText(keyCode).toUpperCase();
				if (st.startsWith("NUMPAD")) {
					st = st.substring(7);
				}
				if (st.length() == 0 || st.length() > 1) {
					// invalid key
					return null;
				}
		}

		if (bsInput.isKeyDown(KeyEvent.VK_SHIFT)) {
			switch (keyCode) {
				case KeyEvent.VK_1: st = "!"; break;
				case KeyEvent.VK_2: st = "@"; break;
				case KeyEvent.VK_3: st = "#"; break;
				case KeyEvent.VK_4: st = "$"; break;
				case KeyEvent.VK_5: st = "%"; break;
				case KeyEvent.VK_6: st = "^"; break;
				case KeyEvent.VK_7: st = "&"; break;
				case KeyEvent.VK_8: st = "*"; break;
				case KeyEvent.VK_9: st = "("; break;
				case KeyEvent.VK_0: st = ")"; break;
			}
		}

		return st;
	}


 /****************************************************************************/
 /***************************** RENDER GAME **********************************/
 /****************************************************************************/

	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(titleImage, 10, 10, null);

		for (int i=0;i < hiscore.length;i++) {
			printHiScore(g, i, hiscore[i]);

			if (gameState == INSERT_SCORE && hiscore[i] == addscore) {
				if (!blink && addscore.name.length() < 3) {
					font.drawString(g, "_", 260+(addscore.name.length()*22), 115+(i*36));
				}
			}
		}

		if (addscore != null && newHiScore == false) {
			printHiScore(g, 9, addscore);
		}
	}

	private void printHiScore(Graphics2D g, int num, HiScoreData data) {
		int y = 120+(num*36);
		num++;
		if (num < 10) {
			font.drawString(g, " "+num, 30, y);
		} else {
			font.drawString(g, String.valueOf(num), 30, y);
		}

		if (data.score.equals("-1")) {
			font.drawString(g, "<empty>", 100, y);

		} else {
			font.drawString(g, data.score, 100, y);
			font.drawString(g, data.name,  260, y);
			font.drawString(g, data.stage, 350, y);
			font.drawString(g, data.level, 455, y);
		}
	}

}
