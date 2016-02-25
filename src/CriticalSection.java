
public class CriticalSection {

	public CriticalSection() {
		// TODO Auto-generated constructor stub
	}
	private int resource;
	private int length;
	
	public int getResourceNumber()
	{
		return this.resource;
	}
	public int getLength()
	{
		return this.length;
	}
	
	public void setResourceNumber(int resourceNumber)
	{
		this.resource = resourceNumber;
	}
	public void setLength(int length)
	{
		this.length = length;
	}

}
