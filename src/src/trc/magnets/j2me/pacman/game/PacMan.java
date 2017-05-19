/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.game;

import javax.microedition.lcdui.game.Sprite;
import trc.magnets.j2me.pacman.Debug;
import trc.magnets.j2me.pacman.RenderContext;
import trc.magnets.j2me.pacman.UpdateContext;

/**
 *
 * @author TeMPOraL
 */
public class PacMan extends Pawn
{
    protected static final int EATING_MILIS_PER_TILE = (10*DEFAULT_MILIS_PER_TILE)/9;
    protected static final int POWERPILL_MILIS_PER_TILE = (10*DEFAULT_MILIS_PER_TILE)/14;

    static final int[] animationDead = {0};
    static final int[] animationMoving = {0, 1, 2, 1};

    protected boolean bOnPowerPill;

    protected boolean isObstacle(int tileType)
    {
	return TileTools.isObstacleForPlayer(tileType);
    }

    public PacMan()
    {
	//milisPerTile = 200;
    }

    protected void resetState()
    {
	super.resetState();
	bOnPowerPill = false;
	displayedSprite.setFrameSequence(animationMoving);
    }

    public void update(UpdateContext uc)
    {
	super.update(uc);

	//include input
	if(uc.keyPressed(UpdateContext.KEY_DOWN))
	{
	    myDesiredMove = MOVE_DOWN;
	}
	else if(uc.keyPressed(UpdateContext.KEY_UP))
	{
	    myDesiredMove = MOVE_UP;
	}
	else if(uc.keyPressed(UpdateContext.KEY_LEFT))
	{
	    myDesiredMove = MOVE_LEFT;
	}
	else if(uc.keyPressed(UpdateContext.KEY_RIGHT))
	{
	    myDesiredMove = MOVE_RIGHT;
	}
    }

    public void render(RenderContext rc, int screenX, int screenY)
    {
	//apply proper transform
	switch (movementDirection)
	{
	    case MOVE_LEFT:
	    {
		displayedSprite.setTransform(Sprite.TRANS_MIRROR);
		break;
	    }
	    case MOVE_DOWN:
	    {
		displayedSprite.setTransform(Sprite.TRANS_ROT90);
		break;
	    }
	    case MOVE_UP:
	    {
		displayedSprite.setTransform(Sprite.TRANS_ROT270);
		break;
	    }
	    case MOVE_RIGHT:
	    {
		displayedSprite.setTransform(Sprite.TRANS_NONE);
		break;
	    }
	    case MOVE_STOP:
	    {
		//Keep previous transform when in stop mode.
		break;
	    }
	    
	    default:
	    {
		Debug.Assert(false, "PacMan::render() - invalid movement direction; should never get here.");
	    }
	}
	super.render(rc, screenX, screenY);
    }

    protected void moveToNextTile()
    {
	super.moveToNextTile();
	myLevel.onPacmanPass(occupiedTile);
    }

    public void eatenPowerPill()
    {
	//speedup
	bOnPowerPill = true;
	milisPerTile = POWERPILL_MILIS_PER_TILE;
	myLevel.activatePowerPill();
	myLevel.eatenPill();
    }

    public void eatenPill()
    {
	//just slow down
	if(!bOnPowerPill)
	{
	    milisPerTile = EATING_MILIS_PER_TILE;
	}
	myLevel.eatenPill();
    }

    public void steppedOnFreeSpace()
    {
	if(!bOnPowerPill)
	{
	    milisPerTile = DEFAULT_MILIS_PER_TILE;
	}
    }

    public void disablePowerPill()
    {
	bOnPowerPill = false;
    }

    public boolean isOnPowerPill()
    {
	return bOnPowerPill;
    }

    public void die()
    {
	displayedSprite.setFrameSequence(animationDead);
    }
}
