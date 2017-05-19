/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman;

import javax.microedition.lcdui.game.GameCanvas;

/**
 *
 * @author TeMPOraL
 */
public interface GameScreen
{
    //TODO comments
    public void initScreen(GameScreenManager parent, GameCanvas canvas);
    public void deInitScreen();
    public void updateScreen(UpdateContext context);
    public void renderScreen(RenderContext context);
}
