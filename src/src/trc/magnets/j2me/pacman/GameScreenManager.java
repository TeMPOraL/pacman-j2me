/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//FIXME we should store class names, not class instances themselves
//(storing objects wastes memory)

package trc.magnets.j2me.pacman;

import java.util.Vector;
import trc.magnets.j2me.pacman.gamescreens.AboutScreen;
import trc.magnets.j2me.pacman.gamescreens.BasicDisplayTest;
import trc.magnets.j2me.pacman.gamescreens.MainGame;
import trc.magnets.j2me.pacman.gamescreens.MainMenu;

//TODO lots of commenting
class ScreenData
{
    public GameScreen screen;
    public String name;

    ScreenData(GameScreen gs, String gsName)
    {
	screen = gs;
	name = gsName;
    }

    public String getName()
    {
	return name;
    }

    public GameScreen getScreen()
    {
	return screen;
    }
}
/**
 *
 * @author TeMPOraL
 */
public class GameScreenManager
{
    protected GameScreen currentScreen = null;
    protected GameScreen changeTo = null;
    protected Vector gameScreens = new Vector();

    protected boolean bShouldQuit = false;

    public void init()
    {
	//register used screens
	registerScreen("BasicDisplayTest", new BasicDisplayTest());
	registerScreen("MainMenu", new MainMenu());
	registerScreen("MainGame", new MainGame());
	registerScreen("About", new AboutScreen());
	
	changeScreen("MainMenu");
    }
    public void deInit()
    {
	currentScreen.deInitScreen();
    }

    public void registerScreen(String name, GameScreen screen)
    {
	Debug.Assert(screen != null, "Tried to register a NULL game screen.");
	gameScreens.addElement(new ScreenData(screen, name));
    }
    
    public void changeScreen(String whatTo)
    {
	ScreenData sD;
	if(whatTo.equals("Quit"))
	{
	    bShouldQuit = true;
	    return;
	}
	else
	{
	    for(int i = 0 ; i < gameScreens.size() ; ++i)
	    {
		sD = (ScreenData)gameScreens.elementAt(i);
		if(sD.getName().equals(whatTo))	//FIXME Demeter?
		{
		    changeTo = sD.getScreen();
		    return;
		}

	    }
	}
	Debug.Assert(false, "Should never get here.");
    }

    public void update(UpdateContext context)
    {
	if(bShouldQuit)
	{
	    context.endGame();
	}
	else if(changeTo != null)
	{
	    System.out.println("Changing screens!");
	    if(currentScreen != null)
	    {
		currentScreen.deInitScreen();
	    }
	    currentScreen = changeTo;
	    currentScreen.initScreen(this, context.getCanvas());
	    changeTo = null;
	}
	currentScreen.updateScreen(context);
    }

    public void render(RenderContext context)
    {
	if(currentScreen != null)
	{
	    currentScreen.renderScreen(context);
	}
    }
}
