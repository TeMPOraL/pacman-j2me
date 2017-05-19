/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.game;

import javax.microedition.lcdui.game.Sprite;
import trc.magnets.j2me.pacman.Debug;
import trc.magnets.j2me.pacman.UpdateContext;


//concrete examples - AI

/**
 *
 * @author TeMPOraL
 */
public class Monster extends Pawn
{
    //afraid animation set
    protected static final int[] ANIMATION_AFRAID = {0, 1};
    protected static final int[] ANIMATION_RECOVERING_FROM_FEAR = {0, 1, 2, 3};

    //normal animation set (directional)
    protected static final int[] ANIMATION_MOVE_LEFT = {4, 5};
    protected static final int[] ANIMATION_MOVE_RIGHT = {6, 7};
    protected static final int[] ANIMATION_MOVE_UP = {0, 1};
    protected static final int[] ANIMATION_MOVE_DOWN = {2, 3};
    protected static final int[] ANIMATION_MOVE_STOP = {2};

    //dead animation set (directional)
    protected static final int[] ANIMATION_EATEN_MOVE_LEFT = {1};
    protected static final int[] ANIMATION_EATEN_MOVE_RIGHT = {3};
    protected static final int[] ANIMATION_EATEN_MOVE_UP = {2};
    protected static final int[] ANIMATION_EATEN_MOVE_DOWN = {0};
    protected static final int[] ANIMATION_EATEN_MOVE_STOP = {0};

    protected static final int AI_SEARCH_DEPTH_MAX = 3;
    protected static final int AI_INFINITY = 10000;

    protected boolean bFrightened = false;
    protected boolean bRecovering = false;
    protected boolean bEaten = false;

    protected Sprite afraidSprite;
    protected Sprite normalSprite;
    protected Sprite eatenSprite;


    protected int previousMovementDirection;
    private int stupidityGenerator = 0;

    protected int[] directionCosts = new int[MOVE_MAX];
    protected int[] directionX = new int[MOVE_MAX];
    protected int[] directionY = new int[MOVE_MAX];

    protected Pawn enemy;

    Monster()
    {
	Debug.Assert(MOVE_STOP == 0, "MOVE_STOP is not the start.");
	Debug.Assert(Math.min(MOVE_STOP, Math.min(MOVE_LEFT, Math.min(MOVE_UP, Math.min(MOVE_RIGHT, MOVE_DOWN)))) == MOVE_STOP, "MOVE_STOP is not lowest.");
	Debug.Assert(AI_INFINITY > Level.LEVEL_MAX_X + Level.LEVEL_MAX_Y, "AI infinity too small");
    }

    public void setSprite(Sprite sprite)
    {
	super.setSprite(sprite);
	normalSprite = sprite;
    }

    protected void setAfraidSprite(Sprite sprite)
    {
	afraidSprite = sprite;
	afraidSprite.defineReferencePixel(afraidSprite.getWidth()/2, afraidSprite.getHeight()/2);
    }
    protected void setDeadSprite(Sprite sprite)
    {
	eatenSprite = sprite;
	eatenSprite.defineReferencePixel(eatenSprite.getWidth()/2, eatenSprite.getHeight()/2);
    }
    
    protected boolean isObstacle(int tileType)
    {
	return TileTools.isObstacleForMonster(tileType);
    }

    public final void setFrightened()
    {
	if(!bEaten)
	{
	    afraidSprite.setFrameSequence(ANIMATION_AFRAID);
	    displayedSprite = afraidSprite;
	    bFrightened = true;
	    bRecovering = false;
	}
    }

    public final void setRecoveringFromFear()
    {
	//alter sprite frames
	if(bFrightened)
	{
	    afraidSprite.setFrameSequence(ANIMATION_RECOVERING_FROM_FEAR);
	    bFrightened = true;
	    bRecovering = true;
	}
//	else if(bEaten)
//	{
//	    //ignore
//	}
	else
	{
	    //Debug.Assert(false, "Monster::setRecoveringFromFear() - invalid monster state.");
	    //just ignore
	}
    }

    public final void setNormal()
    {
	if(!bEaten)
	{
	    displayedSprite = normalSprite;
	    bFrightened = false;
	    bRecovering = false;
	}
    }

    public boolean isAfraid()
    {
	return bFrightened;
    }

    protected void resetState()
    {
	super.resetState();
	if(bEaten)
	{
	    revive();
	}
	else
	{
	    setNormal();
	}
	setDirectionAnimSequence(MOVE_STOP);
    }

    protected void makeNextMove()
    {
	previousMovementDirection = movementDirection;
	super.makeNextMove();
	if(previousMovementDirection != movementDirection)
	{
	    setDirectionAnimSequence(movementDirection);
	}
    }

    public void update(UpdateContext uc)
    {
	if(myLevel.gameIsOn())
	{
	    if(isAfraid())
	    {
		myDesiredMove = getReverseDirection(aiMakeDecision(uc));
	    }
	    else if(isDead())
	    {
		//FIXME
		myDesiredMove = aiMakeDecision(uc);
	    }
	    else
	    {
		myDesiredMove = aiMakeDecision(uc);
	    }
	    super.update(uc);
	}
    }

    protected void setDirectionAnimSequence(int direction)
    {
	if(!isAfraid())
	{
	    if(direction == MOVE_STOP)
	    {
		displayedSprite.setFrameSequence((bEaten) ? ANIMATION_EATEN_MOVE_STOP : ANIMATION_MOVE_STOP);
	    }
	    else if(direction == MOVE_LEFT)
	    {
		displayedSprite.setFrameSequence((bEaten) ? ANIMATION_EATEN_MOVE_LEFT : ANIMATION_MOVE_LEFT);
	    }
	    else if(direction == MOVE_RIGHT)
	    {
		displayedSprite.setFrameSequence((bEaten) ? ANIMATION_EATEN_MOVE_RIGHT : ANIMATION_MOVE_RIGHT);
	    }
	    else if(direction == MOVE_UP)
	    {
		displayedSprite.setFrameSequence((bEaten) ? ANIMATION_EATEN_MOVE_UP : ANIMATION_MOVE_UP);
	    }
	    else if(direction == MOVE_DOWN)
	    {
		displayedSprite.setFrameSequence((bEaten) ? ANIMATION_EATEN_MOVE_DOWN : ANIMATION_MOVE_DOWN);
	    }
	    else
	    {
		Debug.Assert(false, "Monster::setDirectionAnimationSequence() - invalid movement direction.");
	    }
	}
    }

    public void setEaten()
    {
	setNormal();
	bEaten = true;
	displayedSprite = eatenSprite;
    }

    public void revive()
    {
	bEaten = false;
	setNormal();
    }

    public boolean isDead()
    {
	return bEaten;
    }

    //will be reversed if afraid
    public int aiMakeDecision(UpdateContext context)
    {
	return movementDirection;
    }

    public final int getReverseDirection(int direction)
    {
	if(direction == MOVE_LEFT)
	{
	    return MOVE_RIGHT;
	}
	else if(direction == MOVE_RIGHT)
	{
	    return MOVE_LEFT;
	}
	else if(direction == MOVE_UP)
	{
	    return MOVE_DOWN;
	}
	else if(direction == MOVE_DOWN)
	{
	    return MOVE_UP;
	}
	else if(direction == MOVE_STOP)
	{
	    return MOVE_STOP;
	}
	else
	{
	    Debug.Assert(false, "Monster::getReverseDirection() - invalid direction type");
	    return MOVE_STOP;
	}
    }

    public int superStupidAIDecision(UpdateContext context)
    {
	++stupidityGenerator;
	if(stupidityGenerator%2 == 0)
	{
	    stupidityGenerator = 0;
	    return myDesiredMove;
	}
	
	int decision = context.getRandomNumber(4);
	switch(decision)
	{
	    case 0:
	    {
		return MOVE_LEFT;
	    }
	    case 1:
	    {
		return MOVE_RIGHT;
	    }
	    case 2:
	    {
		return MOVE_UP;
	    }
	    case 3:
	    {
		return MOVE_DOWN;
	    }
	    default:
	    {
		Debug.Assert(false, "Monster::superStupidAIDecision() - random number generator error.");
		return MOVE_STOP;
	    }
	}
    }

    public int superSmartAIDecision(UpdateContext uc)
    {
	//Exclude teleportation from decision making - otherwise we might get
	//out-of-bound.
	if(myLevel.getTile(occupiedTile) == Level.CELL_TELEPORTATION)
	{
	    return movementDirection;
	}

	//Select best one
	if(isDead())
	{
	    computeDirectionsToTarget(myLevel.getPenaltyBoxLocation());
	}
	else
	{
	    computeDirectionsToTarget(enemy.getPosition());
	}

	int tmpDir = tileMax(1, 2, 3, 4, directionCosts[1], directionCosts[2], directionCosts[3], directionCosts[4]);
	return tmpDir;
    }

    private int tileMax(int a, int b, int c, int d, int aVal, int bVal, int cVal, int dVal)
    {
	//FIXME DRY
	if(aVal > bVal)
	{
	    if(aVal > cVal)
	    {
		return (aVal > dVal) ? a : d;
	    }
	    else
	    {
		return (cVal > dVal) ? c : d;
	    }
	}
	else
	{
	    if(bVal > cVal)
	    {
		return (bVal > dVal) ? b : d;
	    }
	    else
	    {
		return(cVal > dVal) ? c : d;
	    }
	}
    }

    private void computeDirectionsToTarget(int targetTileID)
    {
	int currentDir = -1;
	int oppositeDir = -1;
	int count = 0;

	//Enumerate available fields
	for(int i = 1 ; i < MOVE_MAX ; ++i)
	{
	    //FIXME optimize - move outside the loop
	    directionX[i] = TileTools.tileXCoordFromID(occupiedTile) + getNextTileXDir(i);
	    directionY[i] = TileTools.tileYCoordFromID(occupiedTile) + getNextTileYDir(i);
	    int tileType = myLevel.getTile(TileTools.tileIDFromCoords(directionX[i], directionY[i]));
	    if(isObstacle(tileType))// || tileType == Level.CELL_PENALTYBOX)
	    {
		directionCosts[i] = -DIRECTION_INFINITY;
	    }
	    else
	    {
		//directionCosts[i] = -TileTools.distanceBetweenTilesCoords(directionX[i], directionY[i], TileTools.tileXCoordFromID(targetTileID), TileTools.tileYCoordFromID(targetTileID));
		++count;
		if(i == movementDirection)
		{
		    currentDir = i;
		}
		else if(i == getReverseDirection(movementDirection))
		{
		    oppositeDir = i;
		}
		directionCosts[i] = -computeAITileValue(directionX[i], directionY[i], TileTools.tileXCoordFromID(targetTileID), TileTools.tileYCoordFromID(targetTileID), 1, i);
	    }
	}

	if((count == 2) && (currentDir >= 0) && (oppositeDir >= 0))
	{
	    directionCosts[oppositeDir] = -DIRECTION_INFINITY;
	}
    }

    public void setEnemy(Pawn pawn)
    {
	enemy = pawn;
    }

    public final int computeAITileValue(int tileX, int tileY, int targetX, int targetY, int currentDepthLevel, int directionTaken)
    {
	//1. If outside of the map or a collision field, then its distance to target is infinite.
	int tileType = myLevel.getTile(TileTools.tileIDFromCoords(tileX, tileY));
	int myDistance = TileTools.distanceBetweenTilesCoords(tileX, tileY, targetX, targetY) + 5 * currentDepthLevel;
	if(TileTools.fieldOutsideMap(tileX, tileY) || isObstacle(tileType))
	{
	    return AI_INFINITY;
	}
	if(currentDepthLevel == AI_SEARCH_DEPTH_MAX)
	{
	    //HACK +currentDepthLevel is an AI hack to favor shorter routes.
	    return myDistance;
	}
	if(tileType == Level.CELL_TELEPORTATION)
	{
	    return computeAITileValue(TileTools.getOtherBoundaryTeleportX(tileX), TileTools.getOtherBoundaryTeleportY(tileY), targetX, targetY, currentDepthLevel + 1, directionTaken);
	}
	//[A] Debug.Assert(currentDepthLevel < AI_SEARCH_DEPTH_MAX, "Monster::computeAITileValue() - gone too far with the recursion.");
	int[] array = new int[MOVE_MAX];
	for(int i = 0 ; i < array.length ; ++i)
	{
	    array[i] = AI_INFINITY;
	}
	
	int debugCounter = 0;
	int oppositeDirection = getReverseDirection(directionTaken);
	//recursively search neighbours
	for(int i = 1 ; i < MOVE_MAX ; ++i)
	{
	    ++debugCounter;
	    //always skip the direction we came from
	    if(i == oppositeDirection)
	    {
		array[i] = AI_INFINITY;
		--debugCounter;
		continue;
	    }
	    else
	    {
		array[i] = computeAITileValue(tileX + getNextTileXDir(i), tileY + getNextTileYDir(i), targetX, targetY, currentDepthLevel + 1, i);
		//System.out.println("Dir: " + i + "; val = " + array[i]);
	    }
	}
	//[A] Debug.Assert(debugCounter == (MOVE_MAX - 2), "Monster::computeAITileValue() - invalid number of directions checked out (" + debugCounter + " instead of " + (MOVE_MAX - 2) + ").");

	//select minimal element
	//As an initial minimal element we use the value of this field.
	int minimal = myDistance;
	for(int i = 0 ; i < array.length ; ++i)
	{
	    minimal = Math.min(minimal, array[i]);
	}
	return minimal;
    }
}

class Inky extends Monster
{
    public int aiMakeDecision(UpdateContext context)
    {
	int decision = context.getRandomNumber(4);
	if(decision %2 == 0)
	{
	    return superSmartAIDecision(context);
	}
	else
	{
	    return superStupidAIDecision(context);
	}
    }
}
class Pinky extends Monster
{
    public int aiMakeDecision(UpdateContext context)
    {
	int decision = context.getRandomNumber(4);
	if(decision != 0)
	{
	    return superStupidAIDecision(context);
	}
	else
	{
	    return superSmartAIDecision(context);
	}
    }
}

class Blinky extends Monster
{
    public int aiMakeDecision(UpdateContext context)
    {
	int decision = context.getRandomNumber(4);
	if(decision != 0)
	{
	    return superSmartAIDecision(context);
	}
	else
	{
	    return superStupidAIDecision(context);
	}
    }
}

class Clyde extends Monster
{
    public int aiMakeDecision(UpdateContext context)
    {
	return superStupidAIDecision(context);
    }
}