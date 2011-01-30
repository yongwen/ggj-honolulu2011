package edu.hawaii.ics.roach;

// JFC
import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JPanel;

// GTGE
import com.golden.gamedev.Game;
import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.funbox.GameSettings;
import com.golden.gamedev.util.ImageUtil;

import com.golden.gamedev.engine.BaseGraphics;
import com.golden.gamedev.engine.graphics.AppletMode;
import com.golden.gamedev.engine.graphics.FullScreenMode;
import com.golden.gamedev.engine.graphics.WindowedMode;

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
		getImage("images/title.png");
		getImages("images/lowertileset.png", 10, 5);
		getImages("images/uppertileset.png", 7, 1);
		getImages("images/coins.png", 3, 1);
		getImages("images/player.png", 12, 1);
		getImages("images/roach.png", 12, 1);
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

  protected void notifyExit() {
	if ((this.bsGraphics instanceof Applet) == false) {
		// non-applet game should call System.exit(0);
		try {
			System.exit(0);
		}
		catch (Exception e) {
		}
		
	}
	else {
		// applet game should display to the user
		// that the game has been ended
		final Applet applet = (Applet) this.bsGraphics;
		BufferedImage src = ImageUtil.createImage(this.getWidth(), this
		        .getHeight());
		Graphics2D g = src.createGraphics();
		
		try {
			// fill background
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			// play with transparency a bit
			g.setComposite(AlphaComposite.getInstance(
			        AlphaComposite.SRC_OVER, 0.8f));
			
			// draw in a circle only
			Shape shape = new java.awt.geom.Ellipse2D.Float(
			        this.getWidth() / 10, this.getHeight() / 10, this
			                .getWidth()
			                - (this.getWidth() / 10 * 2), this.getHeight()
			                - (this.getHeight() / 10 * 2));
			g.setClip(shape);
			
			// draw the game unto this image
			if (this instanceof GameEngine) {
				((GameEngine) this).getCurrentGame().render(g);
			}
			this.render(g);
			
			g.dispose();
		}
		catch (Exception e) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.dispose();
		}
		
		// make it as gray
		BufferedImage converted = null;
		try {
			// technique #1
			// ColorSpace gray = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			// converted = new ColorConvertOp(gray, null).filter(src, null);
			
			// technique #2
			BufferedImage image = new BufferedImage(src.getWidth(), src
			        .getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			Graphics gfx = image.getGraphics();
			gfx.drawImage(src, 0, 0, null);
			gfx.dispose();
			converted = image;
			
			// technique #3
			// ImageFilter filter = new GrayFilter(true, 75);
			// ImageProducer producer = new
			// FilteredImageSource(colorImage.getSource(), filter);
			// Image mage = this.createImage(producer);
			
		}
		catch (Throwable e) {
		}
		final BufferedImage image = (converted != null) ? converted : src;
		
		applet.removeAll();
		applet.setIgnoreRepaint(false);
		
		Canvas canvas = new Canvas() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 8493852179266447783L;
			
			public void paint(Graphics g1) {
				Graphics2D g = (Graphics2D) g1;
				
				// draw game image
				g.drawImage(image, 0, 0, null);
				
				// draw text
				g.setColor(Color.YELLOW);
				g.setFont(new Font("Verdana", Font.BOLD, 12));
				g.drawString("Game has been ended", 10, 25);
				g.drawString("Thank you for playing!", 10, 45);
			}
		};
		canvas.setSize(applet.getSize());
		
		applet.add(canvas);
		applet.repaint();
		canvas.repaint();
	}
}

 /****************************************************************************/
 /****************************** MAIN-CLASS **********************************/
 /****************************************************************************/

	public static void main(String[] args) {
		GameLoader game = new GameLoader();
        game.setup(new RoachGame(), new Dimension(640,480), false);
        
        BaseGraphics gfx = game.getGame().bsGraphics;
   	    if (gfx instanceof WindowedMode) {
   	       // remove this listener
   	       ((WindowedMode) gfx).getFrame().setTitle("Roach Survival");
   	    }
        
        game.start();
	}

	{ distribute = true; }

}
