/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.game;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import trc.magnets.j2me.pacman.Debug;
import trc.magnets.j2me.pacman.RenderContext;
import trc.magnets.j2me.pacman.UpdateContext;


/**
 *
 * @author TeMPOraL
 */
public class Level
{
    //FIXME name MAX is misleading
    static final int LEVEL_MAX_X = 32;
    static final int LEVEL_MAX_Y = 32;
    static final int LEVEL_INTERNAL_MAX_X = 30;
    static final int LEVEL_INTERNAL_MAX_Y = 30;

    //FIXME can be computed from image size.
    static final int TILESET_ELEMENTS = 36;
    static final int TILE_SIZE = 8;

    //Terrain cell types
    //functional elements
    static final int CELL_EMPTY = 33;
    static final int CELL_PILL = 16;
    static final int CELL_POWERPILL = 34;
    static final int CELL_PENALTYBOX = 35;
    static final int CELL_PENALTYBARRIER = 32;
    static final int CELL_START_LOCATION = 17;
    static final int CELL_TELEPORTATION = 128;

    //error mark - not a real cell
    static final int CELL_INVALID = -1;
    static final int CELL_MAX = 35;

    //scores
    static final int SCORE_PILL = 10;
    static final int SCORE_POWERPILL = 50;
    protected static final int SCORE_MONSTER = 200;

    //powerup times
    static final int POWERPILL_END_TIME = 8000;
    static final int POWERPILL_WARNING_START_TIME = 6000;

    static final int GETREADY_DELAY = 4000;

    static final int PLAYER_DEAD_DELAY = 1500;

    static final int LIVES_GAINED_WHEN_PLAYER_DIES = -1;

    //Tileset
    protected Image tilesetImg;
    protected Sprite tilesetSprite;

    //Terrain
    protected int[] terrain = new int[LEVEL_MAX_X*LEVEL_MAX_Y];
    protected int startLocation;	//start location for player
    protected int penaltyBoxLocation;	//penalty box location for monsters

    //Game state
    protected boolean bGameOver = false;
    protected boolean bPlayerVictorious = false;
    protected boolean bGetReadyMode = false;

    //Game objects
    PacMan pacman;
    Monster[] monsters = new Monster[4];

    //Other stuff
    protected Image pinkyImg;
    protected Image blinkyImg;
    protected Image inkyImg;
    protected Image clydeImg;
    protected Image eyesImg;
    protected Image afraidImg;
    protected Image pacmanImg;
    protected Image getReadyImg;

    protected Sprite getReadySpr;
    protected int getReadyAccumulator;

    protected Player myPlayer;

    protected int xTranslation = 0;	//level horizontal translation on screen in pixels
    protected int yTranslation = 0;	//level vertical translation on screen in pixels

    protected int powerPillAccumulator;	//accumulator for duration
    protected boolean changedToRecoveryFromFear;

    protected int playerDeadAccumulator;

    protected int pillCount;

    boolean bSoundFired = false;
    boolean bFirstTime = true;
    boolean bKilledGhost = false;
    int eatenPill = 0;
    //PUBLIC INTERFACE

    //FIXME load from some input format
    //One-time init
    public void init()
    {
	try
	{
	    tilesetImg = Image.createImage("/gfx/tileset.png");
	    tilesetSprite = new Sprite(tilesetImg,8,8);
	    Debug.Assert(tilesetSprite.getRawFrameCount() == TILESET_ELEMENTS, "Invalid tileset image size.");

	    //pawn imgs
	    pacmanImg = Image.createImage("/gfx/pacman.png");
	    blinkyImg = Image.createImage("/gfx/blinky.png");
	    pinkyImg = Image.createImage("/gfx/pinky.png");
	    inkyImg = Image.createImage("/gfx/inky.png");
	    clydeImg = Image.createImage("/gfx/clyde.png");
	    eyesImg = Image.createImage("/gfx/eyes.png");
	    afraidImg = Image.createImage("/gfx/afraid.png");
	    getReadyImg = Image.createImage("/gfx/getready.png");

	    getReadySpr = new Sprite(getReadyImg);
	    getReadySpr.defineReferencePixel(getReadySpr.getWidth()/2, getReadySpr.getHeight()/2);
	    getReadySpr.setRefPixelPosition(getXTranslation() + getLevelWidth()/2, getYTranslation() + getLevelHeight()/2);
	}
	catch(Exception e)
	{
	    //FIXME exception handling? (or not?)
	    e.printStackTrace();
	}
    }

    //One-time deinit
    public void deInit()
    {
	tilesetSprite = null;
	tilesetImg = null;
    }
    
    public boolean reloadFromString(String contents)
    {
	bGameOver = false;
	bPlayerVictorious = false;

	startLocation = -1;
	penaltyBoxLocation = -1;
	pillCount = 0;

	//clear level
	for(int i = 0 ; i < terrain.length ; ++i)
	{
	    terrain[i] = CELL_TELEPORTATION;
	}

	int[] tmpTerrain = new int[LEVEL_INTERNAL_MAX_X*LEVEL_INTERNAL_MAX_Y];
	
	//read level from string
	int strLen = contents.length();
	Debug.Assert(strLen >= ( (LEVEL_INTERNAL_MAX_X) * (LEVEL_INTERNAL_MAX_Y)), "Terrain string is too short.");

	char tileChr;
	int counter = 0;
	for(int i = 0 ; (i < strLen) && (counter < (LEVEL_INTERNAL_MAX_X) * (LEVEL_INTERNAL_MAX_Y)) ; ++i)
	{
	    tileChr = contents.charAt(i);

	    //skip newlines
	    if((tileChr == '\r') || (tileChr == '\n'))
	    {
		continue;
	    }
	    tmpTerrain[counter] = TileTools.tileCharToCode(tileChr);
	    ++counter;
	}
	Debug.Assert(counter == (LEVEL_INTERNAL_MAX_X * LEVEL_INTERNAL_MAX_Y), "Level most likely too short.");

	int tmpID;
	//Rewrite temporary terrain into real terrain
	for(int y = 0 ; y < LEVEL_INTERNAL_MAX_Y ; ++y)
	{
	    for(int x = 0 ; x < LEVEL_INTERNAL_MAX_X ; ++x)
	    {
		tmpID = TileTools.tileIDFromCoords(x+1, y+1);
		terrain[tmpID] = tmpTerrain[y*LEVEL_INTERNAL_MAX_X + x];
		if(terrain[tmpID] == CELL_START_LOCATION)
		{
		    Debug.Assert(startLocation < 0, "Multiple start locations on one level.");
		    startLocation = tmpID;
		}
		else if(terrain[tmpID] == CELL_PENALTYBOX)
		{
		    penaltyBoxLocation = tmpID;
		}
		else if(terrain[tmpID] == CELL_PILL || terrain[tmpID] == CELL_POWERPILL)
		{
		    ++pillCount;
		}
	    }
	}

	//We should have a start location selected.
	Debug.Assert(startLocation >= 0, "No start location was specified.");
	Debug.Assert(penaltyBoxLocation >= 0, "No penalty box location was specified.");

	//TODO assert existance of penalty box

	//Spawn PacMan and ghosts
	pacman = new PacMan();
	monsters[0] = new Blinky();	//Blinky (red)
	monsters[1] = new Pinky();	//Pinky (pink)
	monsters[2] = new Inky();	//Inky (cyan)
	monsters[3] = new Clyde();	//Clyde (orange)

	//setup PacMan and Monsters
	pacman.setSprite(new Sprite(pacmanImg, 14, 14));
	pacman.setLevel(this);

	monsters[0].setSprite(new Sprite(blinkyImg, 14, 14));
	monsters[0].setAfraidSprite(new Sprite(afraidImg, 14, 14));
	monsters[0].setDeadSprite(new Sprite(eyesImg, 14, 14));
	monsters[0].setLevel(this);
	monsters[0].setEnemy(pacman);

	monsters[1].setSprite(new Sprite(pinkyImg, 14, 14));
	monsters[1].setAfraidSprite(new Sprite(afraidImg, 14, 14));
	monsters[1].setDeadSprite(new Sprite(eyesImg, 14, 14));
	monsters[1].setLevel(this);
	monsters[1].setEnemy(pacman);

	monsters[2].setSprite(new Sprite(inkyImg, 14, 14));
	monsters[2].setAfraidSprite(new Sprite(afraidImg, 14, 14));
	monsters[2].setDeadSprite(new Sprite(eyesImg, 14, 14));
	monsters[2].setLevel(this);
	monsters[2].setEnemy(pacman);
	
	monsters[3].setSprite(new Sprite(clydeImg, 14, 14));
	monsters[3].setAfraidSprite(new Sprite(afraidImg, 14, 14));
	monsters[3].setDeadSprite(new Sprite(eyesImg, 14, 14));
	monsters[3].setLevel(this);
	monsters[3].setEnemy(pacman);

	restartLevel();

	bGetReadyMode = true;
	bFirstTime = true;
	return true;
    }

    public void restartLevel()
    {
	//Relocate PacMan to start location
	pacman.setPosition(startLocation);
	pacman.resetState();

	//Relocate monsters to Penalty Box and reset their state
	for(int i = 0 ; i < monsters.length ; ++i)
	{
	    monsters[i].setPosition(penaltyBoxLocation);
	    monsters[i].resetState();
	}

	//TODO Reset all powerups
	powerPillAccumulator = 0;

	bGetReadyMode = false;
	getReadyAccumulator = 0;

	playerDeadAccumulator = PLAYER_DEAD_DELAY;
	bSoundFired = false;
    }

    public void update(UpdateContext context)
    {
	//FIXME optimize all loops (try to blend them into one)!
	if(isPlayerDead())
	{
	    playerDeadAccumulator += context.getDeltaTime();
	    if(playerDeadAccumulator >= PLAYER_DEAD_DELAY)
	    {
		if(!myPlayer.isAlive())
		{
		    bGameOver = true;
		}
		else
		{
		    restartLevel();
		}
	    }
	}
	else
	{
	    for(int i = 0 ; i < monsters.length ; ++i)
	    {
		if(!monsters[i].isDead() && pacman.collidesWith(monsters[i]))
		{
		    //monster dies
		    if(pacman.isOnPowerPill())
		    {
			monsters[i].setEaten();
			myPlayer.score(SCORE_MONSTER);
			killedGhost();
		    }
		    //player dies
		    else
		    {
			//player dies
			killPlayer(context);
		    }
		}
		else if((getTile(monsters[i].getPosition()) == CELL_PENALTYBOX) && monsters[i].isDead())
		{
		    monsters[i].revive();
		}
	    }
	}
	if(pillCount == 0)
	{
	    win();
	}

	//FIXME temp
	if(context.risingEdge(UpdateContext.KEY_B))
	{
	    restartLevel();
	}
	
	//Update powerups
	if(pacman.isOnPowerPill())
	{
	    powerPillAccumulator += context.getDeltaTime();
	}
	if(powerPillAccumulator > POWERPILL_END_TIME)
	{
	    //disable powerpill
	    pacman.disablePowerPill();
	    for(int i = 0 ; i < monsters.length ; ++i)
	    {
		monsters[i].setNormal();
	    }
	    powerPillAccumulator = 0;
	}
	else if( (!changedToRecoveryFromFear) && (powerPillAccumulator >= POWERPILL_WARNING_START_TIME))
	{
	    for(int i = 0 ; i < monsters.length ; ++i)
	    {
		monsters[i].setRecoveringFromFear();
	    }
	    changedToRecoveryFromFear = true;
	}
	//TODO level update

	for(int i = 0 ; i < monsters.length ; ++i)
	{
	    monsters[i].update(context);
	}
	if(bGetReadyMode)
	{
	    getReadyAccumulator += context.getDeltaTime();
	    if(getReadyAccumulator >= GETREADY_DELAY)
	    {
		bGetReadyMode = false;
	    }
	}
	else
	{
	    pacman.update(context);
	}

	if(!bSoundFired)
	{
	    context.playSound((bFirstTime) ? UpdateContext.SOUND_NEW_GAME : UpdateContext.SOUND_LEVEL_NEXT);
	    bFirstTime = false;
	    bSoundFired = true;
	}
	if(eatenPill != 0)
	{
	    context.playSound( (eatenPill == CELL_PILL) ? UpdateContext.SOUND_EAT_PILL : UpdateContext.SOUND_EAT_POWERPILL);
	    eatenPill = 0;
	}
	if(bKilledGhost)
	{
	    context.playSound(UpdateContext.SOUND_EAT_GHOST);
	    bKilledGhost = false;
	}
    }

    public void render(RenderContext context)
    {
	//Currently do Brute-Force rendering
	//FIXME brute force rendering!
	for(int y = 0 ; y < LEVEL_INTERNAL_MAX_Y ; ++y)
	{
	    for (int x = 0 ; x < LEVEL_INTERNAL_MAX_X ; ++x)
	    {
		tilesetSprite.setFrame(terrain[TileTools.tileIDFromCoords(x+1, y+1)]);
		tilesetSprite.setPosition(xTranslation + x*TILE_SIZE, yTranslation + y*TILE_SIZE);
		tilesetSprite.paint(context.getGraphics());
	    }
	}

	//Render PacMan
	renderPawn(context, pacman);

	//Render Monsters
	for(int i = 0 ; i < monsters.length ; ++i)
	{
	    renderPawn(context, monsters[i]);
	}

	if(bGetReadyMode)
	{
	    getReadySpr.paint(context.getGraphics());
	}
    }

    public boolean isOver()
    {
	return bGameOver;
    }

    public boolean playerWasVictorious()
    {
	return bPlayerVictorious;
    }

    protected final void renderPawn(RenderContext context, Pawn pawn)
    {
	//Calculate its real (screen) X and Y
	int tileX = TileTools.tileXCoordFromID(pawn.getPosition());
	int tileY = TileTools.tileYCoordFromID(pawn.getPosition());

	int dir = pawn.getDirection();

	if(dir == Pawn.MOVE_STOP)
	{
	    //draw in-place
	    pawn.render(context, xTranslation + TileTools.tileScreenXFromMapX(tileX), yTranslation + TileTools.tileScreenYFromMapY(tileY));
	}
	else
	{
	    //calculate interpolation
	    tileX = xTranslation + TileTools.tileScreenXFromMapX(tileX);
	    tileY = yTranslation + TileTools.tileScreenXFromMapX(tileY);
	    int destTileX = tileX;
	    int destTileY = tileY;
	    int interpolation = pawn.getInterpolation();
	    if(dir == Pawn.MOVE_LEFT)
	    {
		destTileX = tileX - TILE_SIZE;
	    }
	    else if(dir == Pawn.MOVE_RIGHT)
	    {
		destTileX = tileX + TILE_SIZE;
	    }
	    else if(dir == Pawn.MOVE_UP)
	    {
		destTileY = tileY - TILE_SIZE;
	    }
	    else if(dir == Pawn.MOVE_DOWN)
	    {
		destTileY = tileY + TILE_SIZE;
	    }
	    else
	    {
		//[A] Debug.Assert(false, "Undefined direction of Pawn found while rendering.");
	    }

	    //Compute interpolated coords and draw
	    //Debug.Assert(interpolation >= 0, "Interpolation too small in renderPawn().");
	    //Debug.Assert(interpolation <= Pawn.ON_NEXT_TILE, "Interpolation too large in renderPawn().");
	    pawn.render(context, ((Pawn.ON_NEXT_TILE - interpolation)*tileX + interpolation*destTileX)/Pawn.ON_NEXT_TILE,
				   ((Pawn.ON_NEXT_TILE - interpolation)*tileY + interpolation*destTileY)/Pawn.ON_NEXT_TILE);
	}
    }

    public int getTile(int tileID)
    {
	//[A] Debug.Assert( (tileID >= 0) && (tileID < LEVEL_MAX_X * LEVEL_MAX_Y), "Level::getTile() - invalid tile ID.");
	return terrain[tileID];
    }

    public void setTile(int tileID, int value)
    {
	//[A] Debug.Assert( (tileID >= 0) &&(tileID < LEVEL_MAX_X * LEVEL_MAX_Y), "Level::setTile() - invalid tile ID.");
	//[A] Debug.Assert( (value >= 0) && (value <= CELL_MAX), "Level::setTile() - invalid tile code.");
	terrain[tileID] = value;
    }

    public void setPlayer(Player gamePlayer)
    {
	myPlayer = gamePlayer;
    }

    public void onPacmanPass(int tileID)
    {
	//[A] Debug.Assert( (tileID >= 0) &&(tileID < LEVEL_MAX_X * LEVEL_MAX_Y), "Level::onPacmanPass() - invalid tile ID.");
	terrain[tileID] = TileTools.onPass(terrain[tileID], myPlayer, pacman);
    }

    public void setTranslation(int x, int y)
    {
	xTranslation = x;
	yTranslation = y;
    }

    public final int getXTranslation()
    {
	return xTranslation;
    }

    public final int getYTranslation()
    {
	return yTranslation;
    }

    public final int getLevelWidth()
    {
	//-2 == including bounding teleportation ring
	return (LEVEL_INTERNAL_MAX_X) * TILE_SIZE;
    }

    public final int getLevelHeight()
    {
	//-2 == including bounding teleportation ring
	return (LEVEL_INTERNAL_MAX_Y) * TILE_SIZE;
    }

    public void activatePowerPill()
    {
	//change monsters
	changedToRecoveryFromFear = false;
	for(int i = 0 ; i < monsters.length; ++i)
	{
	    monsters[i].setFrightened();
	}
	eatenPill = CELL_POWERPILL;
    }

    public boolean isPlayerDead()
    {
	return playerDeadAccumulator < PLAYER_DEAD_DELAY;
    }

    public void killPlayer(UpdateContext uc)
    {
	if(!isPlayerDead())
	{
	    playerDeadAccumulator = 0;
	    pacman.die();
	    myPlayer.addLives(LIVES_GAINED_WHEN_PLAYER_DIES);
	    uc.playSound(UpdateContext.SOUND_PACMAN_DIE);
	}
    }

    public final int getPenaltyBoxLocation()
    {
	return penaltyBoxLocation;
    }

    public final void eatenPill()
    {
	Debug.Assert(pillCount > 0, "Level::eatenPill() - gone negative with pill count");
	--pillCount;
	eatenPill = (eatenPill == CELL_POWERPILL) ? eatenPill : CELL_PILL;
    }

    public final void win()
    {
	bGameOver = true;
	bPlayerVictorious = true;
    }

    public final boolean gameIsOn()
    {
	return !bGetReadyMode;
    }
    public final void killedGhost()
    {
	bKilledGhost = true;
    }
}
