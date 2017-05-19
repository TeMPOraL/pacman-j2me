/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.gamescreens;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import trc.magnets.j2me.pacman.GameScreen;
import trc.magnets.j2me.pacman.GameScreenManager;
import trc.magnets.j2me.pacman.RenderContext;
import trc.magnets.j2me.pacman.UpdateContext;

/**
 *
 * @author TeMPOraL
 */
public class AboutScreen implements GameScreen
{
    private Image aboutImg;
    private Sprite aboutSpr;

    private int imageY;
    private int imageX;

    private static int IMAGE_Y_DELAY = 10;
    private static int IMAGE_Y_STUFF_PER_MILISECOND = 50;

    private GameScreenManager gsm;
    
    public void initScreen(GameScreenManager parent, GameCanvas canvas)
    {
	gsm = parent;
	try
	{
	    aboutImg = Image.createImage("/gfx/about.png");
	    aboutSpr = new Sprite(aboutImg);
	}
	catch(Exception e)
	{
	    //...
	}
	imageX = (canvas.getWidth() - aboutSpr.getWidth()) / 2;
	imageY = (canvas.getHeight() + IMAGE_Y_DELAY)*1000;
    }

    public void deInitScreen()
    {
	aboutImg = null;
	aboutSpr = null;
    }

    public void updateScreen(UpdateContext context)
    {
	imageY -= context.getDeltaTime() * IMAGE_Y_STUFF_PER_MILISECOND;
	if((imageY < - (aboutSpr.getHeight() + 42 ) * 1000))
	{
	    gsm.changeScreen("MainMenu");
	}
	aboutSpr.setPosition(imageX, imageY/1000);

	if(context.risingEdge(UpdateContext.KEY_FIRE))
	{
	    gsm.changeScreen("MainMenu");
	}
    }

    public void renderScreen(RenderContext context)
    {
	context.clearScreen();
	aboutSpr.paint(context.getGraphics());
    }
}
