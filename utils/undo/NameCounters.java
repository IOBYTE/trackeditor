package utils.undo;

import utils.Properties;

/**
 * The numbers the last "straight N" and "curve N" names got. Adding a segment
 * can move them on by more than one, skipping numbers other segments already
 * have, so an undo puts back the values from before instead of counting down.
 */
public class NameCounters
{
	private final int	straight;
	private final int	curve;

	/** The counters as they are now */
	public NameCounters()
	{
		straight = Properties.getInstance().getStraightNameCount();
		curve = Properties.getInstance().getCurveNameCount();
	}

	/** Sets the counters back to these values. */
	public void restore()
	{
		Properties.getInstance().setStraightNameCount(straight);
		Properties.getInstance().setCurveNameCount(curve);
	}
}
