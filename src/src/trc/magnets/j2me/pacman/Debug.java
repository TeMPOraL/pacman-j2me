/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package trc.magnets.j2me.pacman;

/**
 *
 * @author TeMPOraL
 */
public final class Debug
{
    private static int assertCnt = 0;
    public static final boolean NDEBUG = false;
    public static final void Assert(boolean expr, String msg)
    {
	++assertCnt;
	if(!NDEBUG && expr == false)
	{
	    Throwable t = new Throwable("Assertion failed: " + msg);
	    t.printStackTrace();
	    throw new Error("Assertion failed: " + msg);
	}
    }

    public static final void Assert(boolean expr)
    {
	Assert(expr, "No assertion description was given.");
    }

    public static final int getCheckedAssertionsCount()
    {
	return assertCnt;
    }
}
