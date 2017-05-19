/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman;

import java.io.InputStream;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;

/**
 *
 * @author TeMPOraL
 */
class WhiteboardMagnet
{
    protected String label;
    protected Object stickedObject;

    WhiteboardMagnet(String labelHow, Object stickWhat)
    {
	label = labelHow;
	stickedObject = stickWhat;
    }

    public String getLabel()
    {
	return label;
    }

    public Object getStickedObject()
    {
	return stickedObject;
    }
}
public class UpdateContext
{
    public static final int KEY_LEFT = GameCanvas.LEFT_PRESSED;
    public static final int KEY_RIGHT = GameCanvas.RIGHT_PRESSED;
    public static final int KEY_UP = GameCanvas.UP_PRESSED;
    public static final int KEY_DOWN = GameCanvas.DOWN_PRESSED;
    public static final int KEY_A = GameCanvas.GAME_A_PRESSED;
    public static final int KEY_B = GameCanvas.GAME_B_PRESSED;
    public static final int KEY_C = GameCanvas.GAME_C_PRESSED;
    public static final int KEY_D = GameCanvas.GAME_D_PRESSED;
    public static final int KEY_FIRE = GameCanvas.FIRE_PRESSED;

    public static final int SOUND_PACMAN_DIE = 0;//
    public static final int SOUND_EAT_PILL = 1;
    public static final int SOUND_EAT_POWERPILL = 2;
    public static final int SOUND_NEW_GAME = 3;//
    public static final int SOUND_LEVEL_NEXT = 4;//
    public static final int SOUND_EAT_GHOST = 5;
    public static final int SOUND_MAX = 6;

    private static Player[] sounds;

    private PacGameCanvas canvas;
    private Display display;	//someone might like to use vibration features
    private long deltaTime;

    private int currentKeyState;
    private int lastKeyState;

    private boolean konami;

    private Vector whiteboard = new Vector();

    private Random rNG = new Random();

    UpdateContext()
    {
	rNG.setSeed(System.currentTimeMillis());

	//create sounds
	InputStream istream;
	sounds = new Player[SOUND_MAX];
	try
	{
	    //SOUND_NEXT_LEVEL
	    istream = getClass().getResourceAsStream("/sfx/newlevel.wav");
	    sounds[SOUND_LEVEL_NEXT] = Manager.createPlayer(istream, "audio/X-wav");
	    sounds[SOUND_LEVEL_NEXT].prefetch();

	    //SOUND_NEW_GAME
	    istream = getClass().getResourceAsStream("/sfx/newgame.wav");
	    sounds[SOUND_NEW_GAME] = Manager.createPlayer(istream, "audio/X-wav");
	    sounds[SOUND_NEW_GAME].prefetch();

	    //SOUND_EAT_GHOST
	    istream = getClass().getResourceAsStream("/sfx/eatghost.wav");
	    sounds[SOUND_EAT_GHOST] = Manager.createPlayer(istream, "audio/X-wav");
	    sounds[SOUND_EAT_GHOST].prefetch();

	    //SOUND_PACMAN_DIE
	    istream = getClass().getResourceAsStream("/sfx/killed.wav");
	    sounds[SOUND_PACMAN_DIE] = Manager.createPlayer(istream, "audio/X-wav");
	    sounds[SOUND_PACMAN_DIE].prefetch();

	    //SOUND_EAT_PILL
	    istream = getClass().getResourceAsStream("/sfx/eatpill1.wav");
	    sounds[SOUND_EAT_PILL] = Manager.createPlayer(istream, "audio/X-wav");
	    sounds[SOUND_EAT_PILL].prefetch();

	    //SOUND_EAT_POWERPILL
	    istream = getClass().getResourceAsStream("/sfx/eatpill2.wav");
	    sounds[SOUND_EAT_POWERPILL] = Manager.createPlayer(istream, "audio/X-wav");
	    sounds[SOUND_EAT_POWERPILL].prefetch();
	}
	catch(Exception e)
	{
	    System.out.println(e.toString());
	    e.printStackTrace();
	}
    }

    public long getDeltaTime()
    {
	return deltaTime;
    }

    void setDeltaTime(long deltaTime)
    {
	this.deltaTime = deltaTime;
    }

    public PacGameCanvas getCanvas()
    {
	return canvas;
    }

    void setCanvas(PacGameCanvas canvas)
    {
	this.canvas = canvas;
    }

    public Display getDisplay()
    {
	return display;
    }

    void setDisplay(Display display)
    {
	this.display = display;
    }

    void setCurrentKeyState(int keyState)
    {
	lastKeyState = currentKeyState;
	currentKeyState = keyState;
    }

    public boolean risingEdge(int keyCode)
    {
	//NOTE potentially bottlenecking assert
//[A] 	Debug.Assert( ((keyCode == KEY_LEFT) || (keyCode == KEY_RIGHT) || (keyCode == KEY_UP) ||
//[A] 			(keyCode == KEY_DOWN) || (keyCode == KEY_A) || (keyCode == KEY_B) ||
//[A] 			(keyCode == KEY_C) || (keyCode == KEY_D) || (keyCode == KEY_FIRE)),
//[A] 			"Invalid key code passed to risingEdge()");
	return (((lastKeyState & keyCode) == 0) && ((currentKeyState & keyCode) != 0));
    }

    public boolean fallingEdge(int keyCode)
    {
	//NOTE potentially bottlenecking assert
//[A] 	Debug.Assert( ((keyCode == KEY_LEFT) || (keyCode == KEY_RIGHT) || (keyCode == KEY_UP) ||
//[A] 			(keyCode == KEY_DOWN) || (keyCode == KEY_A) || (keyCode == KEY_B) ||
//[A] 			(keyCode == KEY_C) || (keyCode == KEY_D) || (keyCode == KEY_FIRE)),
//[A] 			"Invalid key code passed to fallingEdge()");
	return (((lastKeyState & keyCode) != 0) && ((currentKeyState & keyCode) == 0));
    }

    public boolean keyPressed(int keyCode)
    {
	//NOTE potentially bottlenecking assert
//[A] 	Debug.Assert( ((keyCode == KEY_LEFT) || (keyCode == KEY_RIGHT) || (keyCode == KEY_UP) ||
//[A] 			(keyCode == KEY_DOWN) || (keyCode == KEY_A) || (keyCode == KEY_B) ||
//[A] 			(keyCode == KEY_C) || (keyCode == KEY_D) || (keyCode == KEY_FIRE)),
//[A] 			"Invalid key code passed to keyPressed()");
	return ((currentKeyState & keyCode) != 0);
    }

    public int getKeyStates()
    {
	return currentKeyState;
    }

    /**
     * @return Returns true if Konami code was activated this frame.
     */
    public boolean isKonami()
    {
	return konami;
    }

    /**
     * NOTE this function should be used to clear Konami Code status every
     * game frame.
     * @param konami - true to activate Konami Code; false to clear it
     */
    void setKonami(boolean konami)
    {
	this.konami = konami;
    }

    public Object readFromWhiteboard(String name)
    {
	for(int i = 0 ; i < whiteboard.size() ; ++i)
	{
	    Debug.Assert(whiteboard.elementAt(i) instanceof WhiteboardMagnet, "Found non-WhiteboardMagnet on the Whiteboard when reading.");
	    WhiteboardMagnet magnet = (WhiteboardMagnet) whiteboard.elementAt(i);
	    if(magnet.getLabel().equals(name))
	    {
		return magnet.getStickedObject();
	    }
	}
	//not found
	return null;
    }

    public void stickToWhiteboard(String name, Object value)
    {
	Debug.Assert(readFromWhiteboard(name) == null, "Object already on the whiteboard.");

	WhiteboardMagnet magnet = new WhiteboardMagnet(name, value);
	whiteboard.addElement(magnet);
    }

    public void removeFromWhiteboard(String name)
    {
	for(int i = 0 ; i < whiteboard.size() ; ++i)
	{
	    Debug.Assert(whiteboard.elementAt(i) instanceof WhiteboardMagnet, "Found non-WhiteboardMagnet on Whiteboard when removing.");
	    WhiteboardMagnet magnet = (WhiteboardMagnet) whiteboard.elementAt(i);
	    if(magnet.getLabel().equals(name))
	    {
		whiteboard.removeElementAt(i);
		return;
	    }
	}

	//not found
	Debug.Assert(false, "Should never get here.");
    }

    public void endGame()
    {
	canvas.stop();
    }

    public int getRandomNumber(int max)
    {
	return rNG.nextInt(max);
    }

    public void playSound(int soundType)
    {
	//[A] Debug.Assert((soundType >= 0) && soundType < SOUND_MAX, "UpdateContext::playSound() - invalid sound handle.");
	try
	{
	    //sounds[soundType].stop();
	    sounds[soundType].start();
	}
	catch(Exception e)
	{
	    Debug.Assert(false, e.toString());
	}
    }
}