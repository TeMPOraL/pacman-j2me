/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.game;

import trc.magnets.j2me.pacman.Debug;

/**
 *
 * @author TeMPOraL
 */
public class TileTools
{
    private static boolean charMapInitialized = false;

    //FIXME i know - lots of wasted storage
    private static int[] charMap = new int[255];

    static boolean isObstacleForPlayer(int fieldType)
    {
	//TODO test IsObstacleForPlayer
	return !((fieldType == Level.CELL_EMPTY) || (fieldType == Level.CELL_PILL)
		|| (fieldType == Level.CELL_POWERPILL) || (fieldType == Level.CELL_START_LOCATION)
		|| (fieldType == Level.CELL_TELEPORTATION));
    }

    static boolean isObstacleForMonster(int fieldType)
    {
	//TODO test IsObstacleForMonster
	return !((fieldType == Level.CELL_PENALTYBOX) || (fieldType == Level.CELL_PENALTYBARRIER)
		|| (!isObstacleForPlayer(fieldType)));
    }

    //FIXME comments - return value is a new field value
    //player param is for scoring
    static int onPass(int fieldType, Player player, PacMan playerPawn)
    {
	//[A] Debug.Assert(!isObstacleForPlayer(fieldType), "Done onPass() on (player) collision field.");

	switch(fieldType)
	{
	    case Level.CELL_PILL:
	    {
		player.score(Level.SCORE_PILL);
		playerPawn.eatenPill();
		return Level.CELL_EMPTY;
	    }
	    case Level.CELL_POWERPILL:
	    {
		player.score(Level.SCORE_POWERPILL);
		playerPawn.eatenPowerPill();
		return Level.CELL_EMPTY;
	    }
	    case Level.CELL_START_LOCATION:
	    {
		//don't harm start locations
		playerPawn.steppedOnFreeSpace();
		return Level.CELL_START_LOCATION;
	    }
	    case Level.CELL_TELEPORTATION:
	    {
		//also don't harm teleportation cells
		playerPawn.steppedOnFreeSpace();
		return Level.CELL_TELEPORTATION;
	    }
	    default:
	    {
		//nothing
	    }
	}
	playerPawn.steppedOnFreeSpace();
	return Level.CELL_EMPTY;
    }

    static final int tileIDFromCoords(int x, int y)
    {
	//Debug.Assert(((y * Level.LEVEL_MAX_X) + x) < Level.LEVEL_MAX_X * Level.LEVEL_MAX_Y, "Tile coords out of map bounds.");
	//[A] Debug.Assert((x >= 0) && (x < Level.LEVEL_MAX_X), "Tile X coord out of map bounds: " + x);
	//[A] Debug.Assert((y >= 0) && (y < Level.LEVEL_MAX_Y), "Tile Y coord out of map bounds: " + y);
	return ((y*Level.LEVEL_MAX_X) + x);
    }

    static final int tileXCoordFromID(int tileID)
    {
	return tileID % Level.LEVEL_MAX_X;
    }

    static final int tileYCoordFromID(int tileID)
    {
	return tileID / Level.LEVEL_MAX_X;
    }

    static final int tileScreenXFromMapX(int mapX)
    {
	//TILE_SIZE/2 is to return coord of the centre of
	//a tile and not its upper left corner.
	//mapX - 1 is for skipping outer boundaries of the map
	return Level.TILE_SIZE * (mapX-1) + Level.TILE_SIZE/2;
    }

    static final int tileScreenYFromMapY(int mapY)
    {
	//TILE_SIZE/2 is to return coord of the centre of
	//a tile and not its upper left corner.
	//mapY - 1 is for skipping outer boundaries of the map
	return Level.TILE_SIZE * (mapY-1) + Level.TILE_SIZE/2;
    }

    static void initializeTileCharToCodeLookupTable()
    {
	for(int i = 0 ; i < charMap.length ; ++i)
	{
	    charMap[i] = Level.CELL_INVALID;
	}

	//real initialization
	charMap[' '] = Level.CELL_EMPTY;
	charMap['.'] = Level.CELL_PILL;
	charMap['*'] = Level.CELL_POWERPILL;
	charMap['P'] = Level.CELL_PENALTYBOX;
	charMap['B'] = Level.CELL_PENALTYBARRIER;
	charMap['S'] = Level.CELL_START_LOCATION;

	charMap['|'] = 0;
	charMap['-'] = 1;
	charMap['\''] = 2;
	charMap['`'] = 3;
	charMap['}'] = 4;
	charMap['='] = 5;
	charMap['\\'] = 6;
	charMap['%'] = 7;
	charMap['Y'] = 8;
	charMap['U'] = 9;
	charMap['<'] = 10;
	charMap['^'] = 11;
	charMap['1'] = 12;
	charMap['2'] = 13;
	charMap['['] = 14;
	charMap[']'] = 15;

	charMap['I'] = 18;
	charMap['_'] = 19;
	charMap['L'] = 20;
	charMap['J'] = 21;
	charMap['{'] = 22;
	charMap[':'] = 23;
	charMap['/'] = 24;
	charMap['C'] = 25;
	charMap['H'] = 26;
	charMap['K'] = 27;
	charMap['>'] = 28;
	charMap['&'] = 29;
	charMap['3'] = 30;
	charMap['4'] = 31;

	charMapInitialized = true;
    }

    static int tileCharToCode(char code)
    {
	if(!charMapInitialized)
	{
	    initializeTileCharToCodeLookupTable();
	}

	Debug.Assert(code < 256, "Map character > ASCII 255!");
	Debug.Assert(charMap[code] != Level.CELL_INVALID, "Invalid map character: " + code + ".");
	return charMap[code];
    }

    static int distanceBetweenTiles(int tileA, int tileB)
    {
	return distanceBetweenTilesCoords(tileXCoordFromID(tileA), tileYCoordFromID(tileA), tileXCoordFromID(tileB), tileYCoordFromID(tileB));
    }

    static final int distanceBetweenTilesCoords(int aX, int aY, int bX, int bY)
    {
	return Math.abs(aX - bX) + Math.abs(aY - bY);	// - manhattan
	//return (aX - bX)*(aX - bX) + (aY - bY)*(aY - bY);   //euclidean^2
	//return Math.max(Math.abs(aX -bX), Math.abs(aY - bY));	// - maxumum
    }

    static final boolean fieldOutsideMap(int x, int y)
    {
	return ((x < 0) || (x >= Level.LEVEL_MAX_X) || (y < 0) || (y >= Level.LEVEL_MAX_Y));
    }

    static final int getOtherBoundaryTeleportX(int x)
    {
	return (x == Level.LEVEL_INTERNAL_MAX_X) ? ((x == 1) ? x : Level.LEVEL_INTERNAL_MAX_X):1;
    }

    static final int getOtherBoundaryTeleportY(int y)
    {
	return (y == Level.LEVEL_INTERNAL_MAX_Y) ? ((y == 1) ? y : Level.LEVEL_INTERNAL_MAX_Y):1;
    }
}
