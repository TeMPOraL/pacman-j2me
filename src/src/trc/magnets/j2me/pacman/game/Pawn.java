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
public abstract class Pawn
{
    //FIXME comments
    //movement direction constants
    //it is VERY important to keep those constants between 0 and MOVE_MAX being the last
    // - we assume that they can be used as array keys
    public static final int MOVE_STOP = 0;
    public static final int MOVE_UP = 1;
    public static final int MOVE_RIGHT = 2;
    public static final int MOVE_DOWN = 3;
    public static final int MOVE_LEFT = 4;
    public static final int MOVE_MAX = 5;

    public static final int DIRECTION_INFINITY = 1000;
    
    public static final int ON_CURRENT_TILE = 0;
    public static final int ON_NEXT_TILE = 1000;

    public static final int ANIMATION_DEFAULT_FRAME_TIME = 150;

    public static final int DEFAULT_MILIS_PER_TILE = 200;

    protected int occupiedTile;	//where is this pawn standing
    protected int movementDirection;  //movement direction
    protected int myDesiredMove = MOVE_STOP;

    protected Sprite displayedSprite;

    protected int animationAccumulator = 0;
    protected int animationFrameDuration = ANIMATION_DEFAULT_FRAME_TIME;

    protected int milisPerTile = DEFAULT_MILIS_PER_TILE;    //movement speed in miliseconds per tile
    protected int movementMilisAccumulator; //how much we have moved

    protected Level myLevel;

    public void setSprite(Sprite sprite)
    {
	displayedSprite = sprite;
	
	//make sure we have our reference pixel positioned properly
	displayedSprite.defineReferencePixel(displayedSprite.getWidth()/2, displayedSprite.getHeight()/2);
    }

    public void setLevel(Level newLevel)
    {
	Debug.Assert(newLevel != null, "Pawn::setLevel() - passed null player.");
	myLevel = newLevel;
    }

    public void update(UpdateContext uc)
    {
	if(myLevel.isPlayerDead())
	{
	    return;
	}
	//compute displayed sprite screen coords
	if(movementDirection == MOVE_STOP)
	{
	    movementMilisAccumulator = 0;
	    makeNextMove();
	}
	else
	{
	    movementMilisAccumulator += uc.getDeltaTime();
	    if(movementMilisAccumulator > milisPerTile)
	    {
		//TODO change movement direction
		movementMilisAccumulator -= milisPerTile;
		moveToNextTile();
		makeNextMove();
	    }
	}
	
	//update animation
	animationAccumulator -= uc.getDeltaTime();
	if(animationAccumulator <= 0)
	{
	    animationAccumulator = animationFrameDuration;
	    displayedSprite.nextFrame();
	}
    }
    
    public void render(RenderContext rc, int screenX, int screenY)
    {
	displayedSprite.setRefPixelPosition(screenX, screenY);
	displayedSprite.paint(rc.getGraphics());
    }

    public void setPosition(int tile)
    {
	Debug.Assert(tile < Level.LEVEL_MAX_X*Level.LEVEL_MAX_Y, "Pawn::setPosition() - Tile coord too big!");
	occupiedTile = tile;
    }

    public final int getPosition()
    {
	return occupiedTile;
    }

    public final int getDirection()
    {
	return movementDirection;
    }
    
    public final int getInterpolation()
    {
	return (Math.min(movementMilisAccumulator,milisPerTile)*ON_NEXT_TILE)/milisPerTile;
    }

    protected abstract boolean isObstacle(int tileType);

    //Override this method to facilitate decision making.
    //Also include boundaries, or the pawn might go out of screen.
    protected void makeNextMove()
    {
	//no move if player dead
	if(myLevel.isPlayerDead())
	{
	    movementDirection = MOVE_STOP;
	    return;
	}

	int tX;
	int tY;
	if(myLevel.getTile(occupiedTile) != Level.CELL_TELEPORTATION)
	{
	    //ignore desired move if it could lead to collision
	    tX = TileTools.tileXCoordFromID(occupiedTile) + getNextTileXDir(myDesiredMove);
	    tY = TileTools.tileYCoordFromID(occupiedTile) + getNextTileYDir(myDesiredMove);
	    if(isObstacle(myLevel.getTile(TileTools.tileIDFromCoords(tX, tY))))
	    {
		myDesiredMove = movementDirection;	//continue moving as before
	    }
	    else
	    {
		movementDirection = myDesiredMove;
	    }
	}

	//stop movement when colliding with walls & stuff
	tX = TileTools.tileXCoordFromID(occupiedTile) + getNextTileXDir(movementDirection);
	tY = TileTools.tileYCoordFromID(occupiedTile) + getNextTileYDir(movementDirection);

	if(isObstacle(myLevel.getTile(TileTools.tileIDFromCoords(tX, tY))))
	{
	    myDesiredMove = MOVE_STOP;  //clear previous decision
	    movementDirection = MOVE_STOP;
	}
    }

    //Called by Update() to change reference tile (the one we're interpolating from).
    //Override to do events connected with entering a field.
    protected void moveToNextTile()
    {
	//int tX = (movementDirection == MOVE_LEFT) ? -1 : ((movementDirection == MOVE_RIGHT) ? +1 : 0);
	//int tY = (movementDirection == MOVE_UP) ? -1 : ((movementDirection == MOVE_DOWN) ? +1 : 0);
	int tX = getNextTileXDir(movementDirection);
	int tY = getNextTileYDir(movementDirection);

	//transit to new tile
	occupiedTile = TileTools.tileIDFromCoords(TileTools.tileXCoordFromID(occupiedTile) + tX,
						    TileTools.tileYCoordFromID(occupiedTile) + tY);

	//handle teleportation
	if(myLevel.getTile(occupiedTile) == Level.CELL_TELEPORTATION)
	{
	    int telX = TileTools.tileXCoordFromID(occupiedTile);
	    int telY = TileTools.tileYCoordFromID(occupiedTile);

	    if(telX == 0)
	    {
		telX = Level.LEVEL_MAX_X - 1;
	    }
	    else if(telX == Level.LEVEL_MAX_X - 1)
	    {
		telX = 0;
	    }
	    else if(telY == 0)
	    {
		telY = Level.LEVEL_MAX_Y -1;
	    }
	    else if(telY == Level.LEVEL_MAX_Y - 1)
	    {
		telY = 0;
	    }
	    else
	    {
		Debug.Assert(false, "Pawn::moveToNextTile() - invalid teleportation cell.");
	    }

	    occupiedTile = TileTools.tileIDFromCoords(telX, telY);
	}
    }

    protected final int getNextTileXDir(int moveDir)
    {
	return ((moveDir == MOVE_LEFT) ? -1 : ((moveDir == MOVE_RIGHT) ? +1 : 0));
    }

    protected final int getNextTileYDir(int moveDir)
    {
	return ((moveDir == MOVE_UP) ? -1 : ((moveDir == MOVE_DOWN) ? +1 : 0));
    }

    protected void resetState()
    {
	movementDirection = MOVE_STOP;
	myDesiredMove = MOVE_STOP;
	movementMilisAccumulator = 0;
    }

    public Sprite getSprite()
    {
	return displayedSprite;
    }

    protected boolean collidesWith(Pawn otherPawn)
    {
	//return displayedSprite.collidesWith(otherPawn.getSprite(), true);
	int a = getPosition();
	int b;
	if(myLevel.getTile(a) == Level.CELL_TELEPORTATION)
	{
	    b = a;
	}
	else
	{
	    b = TileTools.tileIDFromCoords(TileTools.tileXCoordFromID(getPosition()) + getNextTileXDir(movementDirection), TileTools.tileYCoordFromID(getPosition()) + getNextTileYDir(movementDirection));
	}

	int c = otherPawn.getPosition();
	int d;
	if(myLevel.getTile(c) == Level.CELL_TELEPORTATION)
	{
	    d = c;
	}
	else
	{
	    d = TileTools.tileIDFromCoords(TileTools.tileXCoordFromID(otherPawn.getPosition()) + otherPawn.getNextTileXDir(movementDirection), TileTools.tileYCoordFromID(otherPawn.getPosition()) + otherPawn.getNextTileYDir(movementDirection));
	}
	return (a == c) || (a == d) || (b == c) || (b == d);

    }
}
