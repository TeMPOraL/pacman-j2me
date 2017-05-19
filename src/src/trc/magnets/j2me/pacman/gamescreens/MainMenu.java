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

/**
 *
 * @author TeMPOraL
 */
public class MainMenu implements GameScreen
{
    private static int MENU_ITEM_BLOCK_START_Y = 175;
    private static int MENU_ITEM_SEP_Y = 30;
    private static int MENU_ITEM_COLOR = 0xFFFFFF;
    private static int MENU_SELECTED_ITEM_COLOR = 0xFFFF00;
    private static int MENU_LOGO_Y = 15;

    private int selectedItem = 0;
    private String[] itemNames = { "NEW GAME", "ABOUT", "QUIT" };
    private String[] itemDests = { "MainGame", "About", "Quit" };

    private Image logoImg;
    private Sprite logoSprite;

    private GameScreenManager gsm;


    public void initScreen(GameScreenManager parent, GameCanvas canvas)
    {
	gsm = parent;
	
	//load PacMan logo
	try
	{
	    logoImg = Image.createImage("/gfx/pacmanlogo.png");
	    logoSprite = new Sprite(logoImg);
	}
	catch(Exception e)
	{
	    //...
	}

	//compute center of screen
	logoSprite.setPosition((canvas.getWidth() - logoSprite.getWidth())/2, MENU_LOGO_Y);

	Debug.Assert(itemNames.length == itemDests.length, "MainMenu::initScreen() - number of names and destinations of menu items don't match");
    }

    public void deInitScreen()
    {
	logoImg = null;
	logoSprite = null;
    }

    public void renderScreen(RenderContext context)
    {
	context.clearScreen();
	//TODO Draw PacMan logo
	logoSprite.paint(context.getGraphics());
	for(int i = 0 ; i < itemNames.length ; ++i)
	{
	    context.printString(itemNames[i], context.getScreenWidth()/2, MENU_ITEM_BLOCK_START_Y + i*MENU_ITEM_SEP_Y, ((i == selectedItem) ? MENU_SELECTED_ITEM_COLOR : MENU_ITEM_COLOR), RenderContext.ALIGN_CENTER);
	}
    }

    public void updateScreen(UpdateContext context)
    {
	if(context.risingEdge(UpdateContext.KEY_DOWN))
	{
	    ++selectedItem;
	}
	else if(context.risingEdge(UpdateContext.KEY_UP))
	{
	    --selectedItem;
	}
	
	//loop menu selection
	selectedItem = (selectedItem >= itemNames.length) ? 0 : (selectedItem < 0) ? itemNames.length-1 : selectedItem;
	if(context.risingEdge(UpdateContext.KEY_A) || context.risingEdge(UpdateContext.KEY_B) ||
		context.risingEdge(UpdateContext.KEY_C) || context.risingEdge(UpdateContext.KEY_D) ||
		context.risingEdge(UpdateContext.KEY_FIRE))
	{
	    gsm.changeScreen(itemDests[selectedItem]);
	}
    }
}
