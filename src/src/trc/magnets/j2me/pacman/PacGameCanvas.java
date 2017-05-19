/*
 FIXME comments
 */

package trc.magnets.j2me.pacman;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.*;

/**
 *
 * @author TeMPOraL
 */
public class PacGameCanvas extends GameCanvas implements Runnable
{
    protected static final long SIMULATION_STEP_MSEC = 50;  //around 33 FPS
    private static final long DELAY_BEFORE_TERMINATING_MIDLET = 3000;

    GameScreenManager gsm = new GameScreenManager();

    protected boolean bShouldRun = false;

    UpdateContext uC = new UpdateContext();
    RenderContext rC = new RenderContext();

    //bonus - Konami Code :)
    static final int[] konamiCode = {UP,UP,DOWN,DOWN,LEFT,RIGHT,LEFT,RIGHT,GAME_B,GAME_A };
    int konamiCounter = 0;

    long debugTimeElapsed = 0;
    long debugUpdatesMade = 0;
    long debugFramesShown = 0;

    TRCPacMan pacMidlet;
    
    PacGameCanvas(TRCPacMan midlet)
    {
	super(false);
	pacMidlet = midlet;
    }

    public void initGame()
    {
	this.setFullScreenMode(true);
	
	uC.setCanvas(this);
	uC.setDisplay(null);
	uC.setDeltaTime(SIMULATION_STEP_MSEC);
	rC.setCanvas(this);
	rC.setGraphics(this.getGraphics());

	gsm.init();

	konamiCounter = 0;
    }

    public void deInitGame()
    {
	gsm.deInit();

	//NOTE Remove this in release version.
	//DISPLAY GOODBYE MESSAGE
	Alert goodbye = new Alert("Stats", "AVG UPS: " + ((1000*debugUpdatesMade) / debugTimeElapsed) + "\nAVG FPS: " + ((1000*debugFramesShown) / debugTimeElapsed) + "\nAVG ASSERTS PER SECOND: " + ((1000*Debug.getCheckedAssertionsCount())/debugTimeElapsed ), null, AlertType.INFO);
	goodbye.setTimeout(Alert.FOREVER);
	Display disp = Display.getDisplay(pacMidlet);
	disp.setCurrent(goodbye);

	//allow user to read that message
	try
	{
	    Thread.sleep(DELAY_BEFORE_TERMINATING_MIDLET);
	}
	catch(Exception e)
	{
	}
    }

    /**
     * Main game loop. Function is inheritted from Runnable, and thus will
     * be the entry point for a new thread.
     */
    public void run()
    {
	try
	{
	    long lastTime;
	    long currentTime;

	    long stepsToTake;    //simulation steps to take this frame
	    long stepAccumulator = 0;	//accumulate time

	    initGame();

	    bShouldRun = true;  //fire up the game

	    lastTime = System.currentTimeMillis();
	    while(bShouldRun)   //NOTE: a frame here means one iteration of this loop
	    {
		//Calculate time since last frame
		currentTime = System.currentTimeMillis();
		stepAccumulator += Math.max(currentTime - lastTime, 0); //makes sure that we handle
									//clock looping correctly
									//(might occur one day)

		debugTimeElapsed += currentTime - lastTime;
		lastTime = currentTime; //store beginning-of-the-frame-time

		//compute how many steps we need to make
		//we do a integer-division (with truncation) and then substraction
		//to isolate the fraction part of a step inside the accumulator
		stepsToTake = stepAccumulator / SIMULATION_STEP_MSEC;
		stepAccumulator -= stepsToTake * SIMULATION_STEP_MSEC;
		//[A] Debug.Assert(stepAccumulator >= 0, "Somehow we have negative stepAccumulator");


		//FIXME optimize later with while(...)
		for(int i = 0 ; i < stepsToTake ; ++i)
		{
		    //input is grabbed by updateLogic
		    updateLogic();
		    ++debugUpdatesMade;
		}
		render();
		++debugFramesShown;

		//Input post-processing
		uC.setKonami(false);	//clear Konami code

		//Sleep to give phone some time to process messages, etc.
		//Skipping that sleep causes phone to become unresponsible.
		try
		{
		    //[A] Debug.Assert((SIMULATION_STEP_MSEC - stepAccumulator > 0), "Negative sleep time.");
		    Thread.sleep(SIMULATION_STEP_MSEC - stepAccumulator);
		}
		catch(Exception e)
		{
		    //FIXME to handle or not to handle?
		}
	    }

	    //deinitialize all game stuff
	    deInitGame();
	    
	    //terminate app
	    pacMidlet.notifyDestroyed();
	}
	catch(Exception e)
	{
	    //TODO some better error handling (ie. permanent storage)
	    System.err.println(e.toString());
	    Alert errMsg = new Alert("Exception!", e.toString(), null, AlertType.ERROR);
	    errMsg.setTimeout(Alert.FOREVER);
	    Display disp = Display.getDisplay(pacMidlet);
	    disp.setCurrent(errMsg);
	    e.printStackTrace();
	}
	catch(Error e)
	{
	    System.err.println(e.toString());
	    Alert errMsg = new Alert("Error!", e.toString(), null, AlertType.ERROR);
	    errMsg.setTimeout(Alert.FOREVER);
	    Display disp = Display.getDisplay(pacMidlet);
	    disp.setCurrent(errMsg);
	    e.printStackTrace();
	}
    }

    /**
     * FIXME javadoc
     */
    public void stop()
    {
	System.out.println("STOP");
	System.out.println("Average UPS: " + (1000*debugUpdatesMade / debugTimeElapsed));
	bShouldRun = false;
    }

    public void updateLogic()
    {
	uC.setCurrentKeyState(getKeyStates());
	gsm.update(uC);
    }

    public void render()
    {
	gsm.render(rC);
	flushGraphics();
    }

    protected void keyPressed(int keyCode)
    {
	super.keyPressed(keyCode);

	//Handle Konami Code.
	if(getGameAction(keyCode) == konamiCode[konamiCounter])
	{
	    ++konamiCounter;
	    if(konamiCounter == konamiCode.length)
	    {
		konamiCounter = 0;
		//Set KONAMI flag
		uC.setKonami(true);
	    }
	}
	else
	{
	    //reset konami code counter
	    konamiCounter = 0;
	}
    }
}
