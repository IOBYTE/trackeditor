package utils.circuit;

public class StartingGrid {
	private int rows						= Integer.MAX_VALUE;
	private String	polePositionSide		= null;
	private double	distanceToStart			= Double.NaN;
	private double	distanceBetweenColumns	= Double.NaN;
	private double	offsetWithinAColumn		= Double.NaN;
	private double	initialHeight			= Double.NaN;

	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public String getPolePositionSide() {
		return polePositionSide;
	}
	public void setPolePositionSide(String polePositionSide) {
		this.polePositionSide = polePositionSide;
	}
	public double getDistanceToStart() {
		return distanceToStart;
	}
	public void setDistanceToStart(double distanceToStart) {
		this.distanceToStart = distanceToStart;
	}
	public double getDistanceBetweenColumns() {
		return distanceBetweenColumns;
	}
	public void setDistanceBetweenColumns(double distanceBetweenColumns) {
		this.distanceBetweenColumns = distanceBetweenColumns;
	}
	public double getOffsetWithinAColumn() {
		return offsetWithinAColumn;
	}
	public void setOffsetWithinAColumn(double offsetWithinAColumn) {
		this.offsetWithinAColumn = offsetWithinAColumn;
	}
	public double getInitialHeight() {
		return initialHeight;
	}
	public void setInitialHeight(double initialHeight) {
		this.initialHeight = initialHeight;
	}

	public void dump()
    {
		System.out.println("rows                   : " + rows);
		System.out.println("polePositionSide       : " + polePositionSide);
		System.out.println("distanceToStart        : " + distanceToStart);
		System.out.println("distanceBetweenColumns : " + distanceBetweenColumns);
		System.out.println("offsetWithinAColumn    : " + offsetWithinAColumn);
		System.out.println("initialHeight          : " + initialHeight);
    }
}
