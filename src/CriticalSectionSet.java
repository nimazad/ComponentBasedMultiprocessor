import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class CriticalSectionSet {

	/**
	 * @param args
	 */
	
	private double[] criticalSectionLengthRatio;
	private List<CriticalSection> csList;
	private int numberOfResources;
	public CriticalSectionSet(double[] criticalSectionRatio, int numberOfResources)
	{
		csList = new ArrayList<CriticalSection>();
		this.criticalSectionLengthRatio = criticalSectionRatio;
		this.numberOfResources = numberOfResources;
	}
	public CriticalSection getCriticalSection(int index)
	{
		if(index > csList.size()-1)
		{
			  System.err.println("indx out of range!");
			  System.out.printf("index %d \n", index);
			  return null;
		}
		return csList.get(index);
	}
	public void AddCriticalSection(CriticalSection cs)
	{
		csList.add(cs);
	}
	public void RemoveCriticalSection(int csIndex)
	{
		csList.remove(csIndex);
	}
	public void CreateRandomCriticalSection(int maxNumberOfCriticalSections, int executionTime)
	{

		Random rand = new Random();
		int count = rand.nextInt(maxNumberOfCriticalSections) + 1; //assuming at least one CS
		for(int i=0;i<count;i++)
		{
			CriticalSection cs = new CriticalSection();
			int resourceNumber = rand.nextInt(this.numberOfResources);
			int lengthID = rand.nextInt(this.criticalSectionLengthRatio.length);
			cs.setResourceNumber(resourceNumber); 
			cs.setLength((int)(this.criticalSectionLengthRatio[lengthID]*executionTime));
			this.AddCriticalSection(cs);
		}
	}
	public int getSize()
	{
		return csList.size();
	}
	public void PrintCriticalSectionSet()
	{
		System.out.printf("CS------------------------------------- \n");
		for(int i=0;i<this.getSize();i++)
		{
			CriticalSection cs = csList.get(i);
			System.out.printf("-- cs[%d]: resource %d length %d \n", i, cs.getResourceNumber(), cs.getLength());
		}
		System.out.printf("--------------------------------------- \n");
	}
	
	public int getMinLength()
	{
		int min = csList.get(0).getLength();
		for(int i=1;i<this.getSize();i++)
		{
			int tmp = csList.get(i).getLength();
			if(tmp < min)
				min = tmp;
		}
		return min;
	}
	
	public boolean IfResourceExists(int resourceID)
	{
		boolean result = false;
		for(int i=0;i<this.getSize();i++)
		{
			CriticalSection cs = csList.get(i);
			if(cs.getResourceNumber() == resourceID)
			{
				result = true;
			}
		}		
		return result;		
	}
	public List<CriticalSection> getCriticalSectionSet()
	{
		return this.csList;
	}
	public int getMaxLength(int resourceID)
	{
		int maxLength = 0;
		for(int i=0;i<this.getSize();i++)
		{
			CriticalSection cs = csList.get(i);
			if(cs.getResourceNumber() == resourceID)
			{
				if(cs.getLength() > maxLength)
					maxLength = cs.getLength();
			}
		}
		return maxLength;
	}
}
