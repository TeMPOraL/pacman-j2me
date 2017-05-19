/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.gamescreens;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import trc.magnets.j2me.pacman.Debug;
import trc.magnets.j2me.pacman.GameScreen;
import trc.magnets.j2me.pacman.GameScreenManager;
import trc.magnets.j2me.pacman.RenderContext;
import trc.magnets.j2me.pacman.UpdateContext;
import trc.magnets.j2me.pacman.game.Level;
import trc.magnets.j2me.pacman.game.Player;

/**
 *
 * @author TeMPOraL
 */
public class MainGame implements GameScreen
{
    private static final int LIVES_COUNTER_X = 5;
    private static final int LIVES_COUNTER_Y = 5;
    private static final int LIVES_COUNTER_SEPX = 5;

    private static final int SCORE_COUNTER_X = 5;
    private static final int SCORE_COUNTER_Y = 5;

    private static final int SCORE_COLOR = 0xFFFFFF;

    private Level gameLevel;
    private Player gamePlayer;

    private Image livesImg;
    private Sprite livesSprite;

    private int currentLevel = 0;

    //Game Level translation
    private int translateX;
    private int translateY;

    private GameScreenManager gsm;

    public void initScreen(GameScreenManager parent, GameCanvas canvas)
    {
	gameLevel = new Level();
	gamePlayer = new Player();
	gameLevel.setPlayer(gamePlayer);

	gsm = parent;

	try
	{
	    livesImg = Image.createImage("/gfx/1up.png");
	    livesSprite = new Sprite(livesImg);
	}
	catch(Exception e)
	{
	    //FIXME to handle or not to handle?
	}

	//compute level horizontal and vertical translation
	translateX = (canvas.getWidth() - gameLevel.getLevelWidth())/2;
	translateY = (canvas.getHeight() - gameLevel.getLevelHeight())/2;

	Debug.Assert( (translateX >= 0) && (translateY >= 0), "MainGame::initScreen() - canvas is too small.");
	gameLevel.setTranslation(translateX, translateY);

	currentLevel = -1;
	gameLevel.init();
	boolean res = changeLevel();	//Force-load first level.
	//FIXME change assert to real error handling
	Debug.Assert(res, "Failed to load first level.");
    }

    public void deInitScreen()
    {
	gameLevel.deInit();
	gameLevel = null;
	gamePlayer = null;
    }
    public void updateScreen(UpdateContext context)
    {
	if(!gameLevel.isOver())
	{
	    gameLevel.update(context);
	    //TODO update score board
	}
	else
	{
	    if(gameLevel.playerWasVictorious())
	    {
		if(!changeLevel())
		{
		    endGame();
		}
	    }
	    else
	    {
		endGame();
	    }
	}

	if(context.isKonami())
	{
	    if(!changeLevel())
	    {
		endGame();
	    }
	}
    }
    
    public void renderScreen(RenderContext context)
    {
	context.clearScreen();
	gameLevel.render(context);
	//TODO render Score Board
	//TODO render Status Bar
	renderLives(context);
	renderScore(context);
    }

    boolean changeLevel()
    {
	//1. Create next level file name.
	String levelName = "/levels/level" + (currentLevel + 1) + ".txt";

	//2. Try to open the file.
	String levelContents;
	try
	{
	    java.io.InputStream is = this.getClass().getResourceAsStream(levelName);

	    //3. If the file is OK, read it and initialize level.
	    StringBuffer sb = new StringBuffer();
	    int i;
	    while( ( i = is.read()) != -1 )
	    {
		sb.append((char)i);
	    }
	    levelContents = sb.toString();
	    
	    if(gameLevel.reloadFromString(levelContents))
	    {
		++currentLevel;
		return true;
	    }
	    else
	    {
		return false;
	    }
	}
	catch(Exception e)
	{
	    //4. Else, return false.
	    //We assume that failed level load == no level found.
	    System.err.println("Failed to load level " + levelName);
	    e.printStackTrace();
	    return false;
	}
    }

    void endGame()
    {
	//TODO implement endGame();
	//1. If player has enough score, go to High Scores
	//2. If the game was won, go to reward / credits screen
	//3. Otherwise go directly to the main menu.
	gsm.changeScreen("MainMenu");
    }

    final void renderLives(RenderContext context)
    {
	for(int i = 0 ; i < gamePlayer.getLives() ; ++i)
	{
	    livesSprite.setPosition(i*(livesSprite.getWidth() + LIVES_COUNTER_SEPX)  + translateX + LIVES_COUNTER_X, translateY + LIVES_COUNTER_Y + gameLevel.getLevelHeight() );
	    livesSprite.paint(context.getGraphics());
	}
    }

    final void renderScore(RenderContext context)
    {
	context.printString("SCORE: " + gamePlayer.getScore(), translateX + SCORE_COUNTER_X, translateY - SCORE_COUNTER_Y, SCORE_COLOR, RenderContext.ALIGN_LEFT);
	//context.printString("" + , translateX + SCORE_COUNTER_X, translateY - SCORE_COUNTER_Y + , SCORE_COLOR, RenderContext.ALIGN_LEFT);
    }
}
