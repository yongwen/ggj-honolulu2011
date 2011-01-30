package edu.hawaii.ics.roach;

import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;


public class RoachGameApplet extends GameLoader {

    protected Game createAppletGame() {
        return new RoachGame();
    }

}

