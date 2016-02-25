import java.util.Random;


public class Task {

	/**
	 * @param args
	 */
	public int period;
	public int executionTime;
	public int deadline;
	public int index;
	private double utilization;
	private CriticalSectionSet csSet;
	
	private void calcUtilization()
	{
		utilization = ((double) executionTime) /  period;
	}
	public double getUtilization()
	{
		calcUtilization();
		return utilization;
	}
	public void setRandomExecTime(Random rand)
	{
		if(period==0)
		{
			  System.err.println("Period is not set!");
			  return;
		}
		//Random rand = new Random();
		executionTime = rand.nextInt(period - 1) + 1;
	}
	
	public void CreateCriticalSectionSet(double[] criticalSectionRatio, int numberOfResources, int maxNumberOfCriticalSections)
	{
		this.csSet = new CriticalSectionSet(criticalSectionRatio, numberOfResources);
		this.csSet.CreateRandomCriticalSection(maxNumberOfCriticalSections, this.executionTime);
	}
	public void PrintCriticalSections()
	{
		this.csSet.PrintCriticalSectionSet();
	}
	public CriticalSectionSet getCriticalSections()
	{
		return this.csSet;
	}
	
}
