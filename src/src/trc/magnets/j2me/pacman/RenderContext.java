/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author TeMPOraL
 */
public class RenderContext
{
    private PacGameCanvas canvas;
    private Graphics gfx;

    public static final int ALIGN_LEFT = Graphics.LEFT | Graphics.BASELINE;
    public static final int ALIGN_RIGHT = Graphics.RIGHT | Graphics.BASELINE;
    public static final int ALIGN_CENTER = Graphics.HCENTER | Graphics.BASELINE;

    public final PacGameCanvas getCanvas()
    {
	return canvas;
    }

    final void setCanvas(PacGameCanvas canvas)
    {
	this.canvas = canvas;
    }

    public final Graphics getGraphics()
    {
	return gfx;
    }

    final void setGraphics(Graphics graphics)
    {
	gfx = graphics;
    }

    public final void clearScreen()
    {
	gfx.setColor(0x000000);
	gfx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public final void printString(String what, int x, int y, int color, int align)
    {
	Debug.Assert((align == ALIGN_LEFT) || (align == ALIGN_RIGHT) || (align == ALIGN_CENTER), "GraphicsContext::printString() - Invalid alignment.");
	gfx.setColor(color);
	gfx.drawString(what, x, y, align);
    }

    public final int getScreenWidth()
    {
	return canvas.getWidth();
    }
}