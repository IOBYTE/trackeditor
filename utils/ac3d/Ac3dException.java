package utils.ac3d;

public class Ac3dException extends RuntimeException
{
	private int lineNumber = 0;

    public Ac3dException()
    {
    }

    public Ac3dException(String msg, int lineNumber)
    {
        super(msg);
        this.lineNumber = lineNumber;
    }

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
}
