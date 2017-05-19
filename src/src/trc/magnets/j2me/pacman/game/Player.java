/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman.game;

/**
 *
 * @author TeMPOraL
 */
public class Player
{
    public static final int DEFAULT_PLAYER_LIVES = 3;
    private int score;
    private int lives;

    public Player()
    {
	reset();
    }

    public void reset()
    {
	lives = DEFAULT_PLAYER_LIVES;
	score = 0;
    }

    public void score(int points)
    {
	score += points;
    }

    public void addLives(int livesNo)
    {
	lives += livesNo;
    }

    public final int getLives()
    {
	return lives;
    }

    public final boolean isAlive()
    {
	return lives > 0;
    }

    public final int getScore()
    {
	return score;
    }
}
