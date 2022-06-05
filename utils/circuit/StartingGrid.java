package utils.circuit;

public class StartingGrid {
	private int 	rows					= Integer.MAX_VALUE;
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

	public void dump(String indent)
    {
		System.out.println(indent + "StartingGrid");
		System.out.println(indent + "  rows                   : " + rows);
		System.out.println(indent + "  polePositionSide       : " + polePositionSide);
		System.out.println(indent + "  distanceToStart        : " + distanceToStart);
		System.out.println(indent + "  distanceBetweenColumns : " + distanceBetweenColumns);
		System.out.println(indent + "  offsetWithinAColumn    : " + offsetWithinAColumn);
		System.out.println(indent + "  initialHeight          : " + initialHeight);
    }
}
