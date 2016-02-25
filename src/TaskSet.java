import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TaskSet {

	/**
	 * @param args
	 */
	private int minPeriod;
	private int deltaPeriod;
	private long index;
	private double utilization;
	private List<Task> taskList;
	private double[] criticalSectionRatio;
	private int numberOfResources;
	private int maxNumberOfCriticalSections;
	
	public void setResourceSharingParameters(double[] ratio, int numberOfResources, int maxNumberOfCriticalSections)
	{
		this.criticalSectionRatio = ratio;
		this.numberOfResources = numberOfResources;
		this.maxNumberOfCriticalSections = maxNumberOfCriticalSections;
	}
	public TaskSet()
	{
		taskList = new ArrayList<Task>();
	}
	public Task getTask(int index)
	{
		if(index > taskList.size()-1)
		{
			  System.err.println("indx out of range!");
			  System.out.printf("index %d \n", index);
			  return null;
		}
		return taskList.get(index);
	}
	public void AddTask(Task task)
	{
		taskList.add(task);
	}
	public void RemoveTask(int taskIndex)
	{
		taskList.remove(taskIndex);
	}
	public void CreateRandomTaskSet(int count)
	{
		if(minPeriod<=0 || deltaPeriod <= 0)
		{
			  System.err.println("minPeriod or deltaPeriod is not set!");
			  return;
		}
		Random rand = new Random();
		for(int i=0;i<count;i++)
		{
			Task task = new Task();
			task.period = rand.nextInt((minPeriod+deltaPeriod - minPeriod) + 1) + minPeriod; 
			task.deadline = task.period;
			task.setRandomExecTime(rand);
			task.index = i;
			this.AddTask(task);
		}
	}
	public void CreateRandomTaskSet(double setUtil, double maxTaskUtil, boolean resource)
	{
		if(minPeriod<=0 || deltaPeriod <= 0)
		{
			  System.err.println("minPeriod or deltaPeriod is not set!");
			  return;
		}
		Random rand = new Random();
		double Ur = setUtil;
		int i = 0;
		while (Ur > maxTaskUtil)
		{
			Task task = new Task();
			double util = rand.nextDouble()*maxTaskUtil;
			Ur -= util;
			task.period = rand.nextInt((deltaPeriod) + 1) + minPeriod;
			task.period = task.period - task.period % 10; // resolution of timer is set to 10
			task.deadline = task.period;
			task.executionTime = (int) (util*task.period);
			// For resource sharing
			if(resource)
			{
				task.CreateCriticalSectionSet(criticalSectionRatio, numberOfResources, maxNumberOfCriticalSections);
			}
			task.index = i; i++;
			this.AddTask(task);
		}
		//Last task
		Task task = new Task();
		double util = Ur*maxTaskUtil;
		task.period = rand.nextInt((deltaPeriod) + 1) + minPeriod;
		task.period = task.period - task.period % 10; // resolution of timer is set to 10
		task.deadline = task.period;
		task.executionTime = (int) (util*task.period);
		if(resource)
		{
			task.CreateCriticalSectionSet(criticalSectionRatio, numberOfResources, maxNumberOfCriticalSections);
		}
		task.index = i; i++;
		this.AddTask(task);
	}
	public void CreateSampleTaskSet(int countSmall, int countBig)
	{
		for(int i=0;i<countSmall;i++)
		{
			Task task = new Task();
			task.period = 100; 
			task.deadline = task.period;
			task.executionTime = 13;
			task.index = i;
			this.AddTask(task);
		}
		for(int i=0;i<countBig;i++)
		{
			Task task = new Task();
			task.period = 100; 
			task.deadline = task.period;
			task.executionTime = 31;
			task.index = i;
			this.AddTask(task);
		}
	}
	
	public void setPeriodRange(int min, int delta)
	{
		minPeriod = min;
		deltaPeriod = delta;
	}
	private void calcUtilization()
	{
		utilization = 0;
		for(int i=0;i<this.getSize();i++)
		{
			utilization += taskList.get(i).getUtilization(); 
		}
	}
	public double getUtilization()
	{
		calcUtilization();
		return utilization;
	}
	public int getSize()
	{
		return taskList.size();
	}
	public void PrintTaskSet()
	{
		System.out.printf("------------------------------------------------------------ \n");
		for(int i=0;i<this.getSize();i++)
		{
			Task task = taskList.get(i);
			System.out.printf("-- task[%d]: period %d executionTime %d U:%.2f \n", task.index, task.period, task.executionTime, task.getUtilization());
			task.PrintCriticalSections();
		}
		System.out.printf("------------------------------------------------------------ \n");
	}
	public double[] getAllExecTimes()
	{
		double[] execTimes = new double[this.getSize()];
		for(int i=0;i<this.getSize();i++)
			execTimes[i] = taskList.get(i).executionTime;
		return execTimes;
	}
	public long[] getAllPeriods()
	{
		long[] periods = new long[this.getSize()];
		for(int i=0;i<this.getSize();i++)
			periods[i] = taskList.get(i).period;
		return periods;
	}
	public double getMeanPeriods()
	{
		double sum = 0;
		for(int i=0;i<this.getSize();i++)
			sum += taskList.get(i).period;
		return sum/this.getSize();
	}
	public int getMinPeriods()
	{
		int min = taskList.get(0).period;
		for(int i=1;i<this.getSize();i++)
		{
			int tmp = taskList.get(i).period;
			if(tmp < min)
				min = tmp;
		}
		return min;
	}
	public double getStdPeriods()
	{
		double mean = this.getMeanPeriods();
		double sum = 0;
		double[] array = new double[this.getSize()];
		for(int i = 0; i < this.getSize(); i++)
		{
		   array[i] = Math.pow((taskList.get(i).period - mean), 2);
		   sum += array[i]; 
		}
		return Math.sqrt(sum/this.getSize());
	}
	private static long gcd(long a, long b)
	{
	    while (b > 0)
	    {
	        long temp = b;
	        b = a % b; // % is remainder
	        a = temp;
	    }
	    return a;
	}

	/*private static long gcd(long[] input)
	{
	    long result = input[0];
	    for(int i = 1; i < input.length; i++) result = gcd(result, input[i]);
	    return result;
	}*/
	

	private static long lcm(long a, long b)
	{
	    return a * (b / gcd(a, b));
	}

	private static long lcm(long[] input)
	{
	    long result = input[0];
	    for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
	    return result;
	}
	public long getLCMOfPeriods()
	{
		long LCM = 0;
		LCM = lcm(getAllPeriods());
		return LCM;
	}
	public void SortTasksUtilization()
	{
		int swapped = 1;
		while (swapped == 1)
		{
			swapped = 0;
			for(int i=1;i<this.getSize();i++)
			{
				if(taskList.get(i-1).getUtilization() < taskList.get(i).getUtilization())
					{
						swapTasks(i-1, i);
						swapped = 1;
					}
			}
		}
	}
	private void swapTasks(int A, int B)
	{
		Collections.swap(taskList, A, B);
		/*Task tmp = new Task();
		tmp = tasks[A];
		tasks[A] = tasks[B];
		tasks[B] = tmp;*/
	}
	public void setIndex(long indx)
	{
		this.index = indx;
	}
	public long getIndex()
	{
		return this.index;
	}
	public int getMyMaxCriticalSection(int myId)
	{
		int maxLength = 0;
		Task myTask = taskList.get(myId);
		for(int i=0;i<this.getSize();i++)
		{
			if(i != myId)
			{
				Task task = taskList.get(i);
				CriticalSectionSet csSet = task.getCriticalSections();
				for (CriticalSection cs : csSet.getCriticalSectionSet()) 
				{
					if(myTask.getCriticalSections().IfResourceExists(cs.getResourceNumber()))
					{
						if(cs.getLength() > maxLength)
							maxLength = cs.getLength();
					}
				}
			}
		}
		return maxLength;
	}
	public int getMaxCriticalSectionExceptMe(int myId)
	{
		int maxLength = 0;
		Task myTask = taskList.get(myId);
		for(int i=0;i<this.getSize();i++)
		{
			if(i != myId)
			{
				Task task = taskList.get(i);
				CriticalSectionSet csSet = task.getCriticalSections();
				for (CriticalSection cs : csSet.getCriticalSectionSet()) 
				{
					if(!myTask.getCriticalSections().IfResourceExists(cs.getResourceNumber()))
					{
						if(cs.getLength() > maxLength)
							maxLength = cs.getLength(); // + spin i,q
					}
				}
			}
		}
		return maxLength;
	}
	
	public int getMaxmPrimeCriticalSectionExceptMeOfResource(int myTaskId, int myResourceID, int mPrime)
	{
		int sumMaxmPrimeLength = 0;
		Task myTask = taskList.get(myTaskId);
		List<Integer> allMaxCs =  new ArrayList<Integer>();
		
		for(int i=0;i<this.getSize();i++)
		{
			if(i != myTaskId)
			{
				Task task = taskList.get(i);				
				allMaxCs.add(task.getCriticalSections().getMaxLength(myResourceID));
			}
		}
		//Sum of m' max
		Collections.sort(allMaxCs);
		int startIndex = allMaxCs.size()-mPrime -1;
		if (startIndex < 0 )
			startIndex = 0;
		for(int i=startIndex; i<allMaxCs.size();i++)
			sumMaxmPrimeLength += allMaxCs.get(i);
			
		return sumMaxmPrimeLength ;
	}
	public void Inflation(int mPrime)
	{
		for(int i=0;i<this.getSize();i++)
		{
			int spini = 0;
			Task task = taskList.get(i);
			CriticalSectionSet csSet = task.getCriticalSections();
			for (CriticalSection cs : csSet.getCriticalSectionSet()) 
			{
				spini += getMaxmPrimeCriticalSectionExceptMeOfResource(i, cs.getResourceNumber(), mPrime);
			}
			task.executionTime += spini;
		}
	}
	public int Overrun(int mPrime)
	{
		int BudgetInflation = 0;
		for(int i=0;i<this.getSize();i++)
		{
			Task task = taskList.get(i);
			CriticalSectionSet csSet = task.getCriticalSections();
			for (CriticalSection cs : csSet.getCriticalSectionSet()) 
			{
				int spiniq = getMaxmPrimeCriticalSectionExceptMeOfResource(i, cs.getResourceNumber(), mPrime);
				if(spiniq + cs.getLength() > BudgetInflation)
				{
					BudgetInflation = spiniq + cs.getLength();
				}
			}
		}
		return BudgetInflation;
	}
}
