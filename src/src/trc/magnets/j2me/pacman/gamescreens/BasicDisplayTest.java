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
public class BasicDisplayTest implements GameScreen
{
    GameScreenManager gsm;

    Image testImage;
    Sprite testSprite;

    Image tileImg;
    Sprite tileSpr;

    public void initScreen(GameScreenManager parent, GameCanvas canvas)
    {
	gsm = parent;

	//init test sprite
	try
	{
	    testImage = Image.createImage("/gfx/test_fullscreen.png");
	    testSprite = new Sprite(testImage);

	    tileImg = Image.createImage("/gfx/tileset.png");
	    tileSpr = new Sprite(tileImg);

	    tileSpr.setPosition(0, 100);
	}
	catch(Exception e)
	{
	    System.err.println(e.toString());
	}
    }

    public void deInitScreen()
    {
	//let them be GC'd
	testSprite = null;
	testImage = null;

	tileImg = null;
	tileSpr = null;
    }

    public void updateScreen(UpdateContext context)
    {
	if(context.risingEdge(UpdateContext.KEY_UP))
	{
	    System.out.println("Rising Edge: UP");
	    tileSpr.setPosition(tileSpr.getX(), tileSpr.getY() - 20);
	}
	if(context.risingEdge(UpdateContext.KEY_LEFT))
	{
	    System.out.println("Rising Edge: LEFT");
	    tileSpr.setPosition(tileSpr.getX() - 20, tileSpr.getY());
	}
	if(context.risingEdge(UpdateContext.KEY_B))
	{
	    System.out.println("Rising Edge: B");
	    tileSpr.setTransform(Sprite.TRANS_MIRROR);
	}
	if(context.fallingEdge(UpdateContext.KEY_DOWN))
	{
	    System.out.println("Falling Edge: DOWN");
	    tileSpr.setPosition(tileSpr.getX(), tileSpr.getY() + 20);
	}
	if(context.fallingEdge(UpdateContext.KEY_RIGHT))
	{
	    System.out.println("Falling Edge: RIGHT");
	    tileSpr.setPosition(tileSpr.getX() + 20, tileSpr.getY());
	}
	if(context.fallingEdge(UpdateContext.KEY_B))
	{
	    System.out.println("Falling Edge: B");
	    tileSpr.setTransform(Sprite.TRANS_NONE);
	}
	if(context.keyPressed(UpdateContext.KEY_A))
	{
	    System.out.println("Game A pressed");
	    tileSpr.setTransform(Sprite.TRANS_ROT90);
	}
	if(context.isKonami())
	{
	    System.out.println("Konami");
	    //context.EndGame();
	    gsm.changeScreen("MainGame");
	}
    }

    public void renderScreen(RenderContext context)
    {
	testSprite.paint(context.getGraphics());

	tileSpr.paint(context.getGraphics());
    }
}
