package edu.hawaii.ics.roach;

// JFC
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.Timer;


public class Text extends Sprite {

	private Timer timer;


	public Text(BufferedImage image, double x, double y) {
		super(image,x,y+15);

		setVerticalSpeed(-0.015);
		timer = new Timer(1300);
	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);

		if (timer.action(elapsedTime)) {
			setActive(false);
		}
	}

}
