/*
 FIXME fill comments
 */

package trc.magnets.j2me.pacman;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * @author TeMPOraL
 */
public class TRCPacMan extends MIDlet
{
    private Display display;
    private PacGameCanvas gameCanvas;
    private Thread gameThread;
    
    public void startApp()
    {
	//create game object and run it in the new thread
	gameCanvas = new PacGameCanvas(this);
	gameThread = new Thread(gameCanvas);
	gameThread.start();

	//sets object to be displayed by this midlet
	display = Display.getDisplay(this);
	display.setCurrent(gameCanvas);
    }

    public void pauseApp()
    {
	//TODO pauseApp() code
    }

    public void destroyApp(boolean unconditional)
    {
	//a polite stop - asks the game to terminate in a natural way
	gameCanvas.stop();
    }
}
