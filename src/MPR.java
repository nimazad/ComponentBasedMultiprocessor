import java.util.Arrays;


public class MPR {

	public MPR() 
	{
		
	}
	
	private int mPrime;
	private int Pi = 0;
	private int Theta;
	private double epsilon = .001; //used for calculating AkMax - based on the email from Arvind
	private int M = -1;
	private double[] procAllocations;
	public void setPi(int inPi)
	{
		Pi = inPi;
	}
	public void setM(int inM)
	{
		this.M = inM;
		procAllocations = new double[this.M];
	}
	public double[] getProcAllocations()
	{
		return procAllocations;
	}
	public void setProcAllocation(int id, double portion)
	{
		this.procAllocations[id] = portion;
	}
	public int getPi()
	{
		return this.Pi;
	}
	public void setTheta(int inTheta)
	{
		this.Theta = inTheta;
	}
	public int getTheta()
	{
		return this.Theta;
	}
	public int getMprime()
	{
		return this.mPrime;
	}
	public void DeductTheta(int amount)
	{
		this.Theta -= amount;
	}
	/**
	 * This function calculates budget assuming gEDF task-scheduler.
	 * It assumes that the period Pi is already set.
	 * @param taskSet
	 */
	public void calcMinimalInterface(TaskSet taskSet)
	{
		double tmpTheta = 0;
		Task taskk;
		Theta = 0;
		if(Pi==0)
		{
			  System.err.println("Period is not set!");
			  return;
		}
		mPrime=(int) Math.ceil(taskSet.getUtilization());
		//for(mPrime<maxM;mPrime++)
		boolean calc = true;
		while (calc)
		{
			for(int k=0;k<taskSet.getSize();k++)
			{
				taskk = taskSet.getTask(k);
				tmpTheta = deriveThetaForTauk(taskSet, taskk, k);
				updateThetaIfLarger((int) Math.ceil(tmpTheta));
			}
			
			if(this.getUtilization() <= this.mPrime)
				calc = false; //condition for an acceptable interface
			else
				mPrime++; //try again with mPrime++ 
		}
		//System.err.println("Could not find MPR for maxM!");
	}
	/**
	 * This function calculates budget assuming gEDF task-scheduler for an input m'.
	 * It assumes that the period Pi is already set.
	 * @param taskSet
	 */
	public void calcThetaForInM(TaskSet taskSet, int inMPrime)
	{
		double tmpTheta = 0;
		Task taskk;
		Theta = 0;
		if(Pi==0)
		{
			  System.err.println("Period is not set!");
			  return;
		}
		if(taskSet.getUtilization() > inMPrime)
		{
			  System.err.println("The taskset utilization is more than the number of processors");
			  Theta = -1;
			  return;
		}
		mPrime = inMPrime;
		for(int k=0;k<taskSet.getSize();k++)
		{
			taskk = taskSet.getTask(k);
			tmpTheta = deriveThetaForTauk(taskSet, taskk, k);
			updateThetaIfLarger((int) Math.ceil(tmpTheta));
		}
		
		if(this.getUtilization() > this.mPrime)
		{
			System.err.println("Could not find MPR for InM!");
			Theta = -1;
		}
	}
	private double deriveThetaForTauk(TaskSet taskSet, Task taskK, int k)
	{
		double tmpTheta = 0;
		double finalTheta = 0;
		double tmpDemand = 0;
		int maxAk = (int) Math.ceil(deriveAkMax(taskSet, taskK));
		//System.out.printf("----maxAk: %d \n", maxAk);
		for(int Ak=0;Ak<=maxAk;Ak++)
		{
			tmpDemand = DEM(taskSet, k, Ak);
			tmpTheta = deriveTheta(Ak+taskK.deadline, tmpDemand);
			if(tmpTheta>finalTheta)
				finalTheta = tmpTheta;
		}
		return finalTheta;
	}
	private double deriveAkMax(TaskSet taskSet, Task taskK)
	{
		double result = 0;
		double tmpTheta = taskSet.getUtilization() + epsilon*taskSet.getUtilization();
		double AkStart = deriveAk(taskSet, taskK, tmpTheta);
		double AkEnd = deriveAk(taskSet, taskK, mPrime*Pi);
		//System.out.printf("----AkStart: %f, AkEnd %f \n", AkStart, AkEnd);
		result = Math.max(AkStart, AkEnd);
		if(result<0)
			result = 0; //Ak has to be >=0
		return result;
	}
	private double deriveAk(TaskSet taskSet, Task taskK, double tmpTheta)
	{
		double result = 0;
		double Utau = taskSet.getUtilization();
		//double tmpTheta = Utau + epsilon*Utau;
		double U = 0;
		for(int i=0;i<taskSet.getSize();i++)
		{
			Task taski = taskSet.getTask(i);
			U += (taski.period - taski.deadline)* ((double)taski.executionTime/taski.period);
		}
		double B = tmpTheta/Pi*(2+2*(Pi-tmpTheta/mPrime));
		double Csigma = getSumOfnLargest(taskSet.getAllExecTimes(), mPrime);
		double tmpUtil = tmpTheta/Utau;
		double tmpUtilDiff = tmpUtil - Utau; // It will be epsilon
		result = (Csigma + mPrime*taskK.executionTime - taskK.deadline*tmpUtilDiff + U + B)/(tmpUtilDiff);
		return result;
	}
	private double deriveTheta(int t, double demand)
	{
		double result = 0;
		double a = (double)2/(mPrime*Pi);
		double b = (double) (t-2) / Pi - 2;
		double c = -demand;
		double delta = b*b-4*a*c;
		result = (-b+ Math.sqrt(delta))/(2*a);
		return result; 
	}
	private double DEM(TaskSet taskSet, int k, int A_k)
	{
		double demand = 0;
		double diff[] = new double[taskSet.getSize()];
		demand = 0;
	    demand = demand + mPrime * taskSet.getTask(k).executionTime;
	    for(int i=0;i<taskSet.getSize();i++)
	    {
	    	Task taski = taskSet.getTask(i);
	    	Task taskk = taskSet.getTask(k);
	        demand = demand + I_hat(taski, taskk, A_k, i, k);
	    }
	    
	    for(int i=0;i<taskSet.getSize();i++)
	    {
	    	Task taski = taskSet.getTask(i);
	    	Task taskk = taskSet.getTask(k);
	        diff[i] = I_bar(taski, taskk, A_k, i, k) - I_hat(taski, taskk, A_k, i, k);
	    }
	    int diff_end;
	    if (mPrime-1>taskSet.getSize())
	        diff_end = taskSet.getSize();
	    else
	        diff_end = mPrime-1;
	    demand +=  getSumOfnLargest(diff, diff_end);
		return demand;
	}
	private double carryIn(Task task, int t)
	{
		double result = 0;
		double tmp;
		tmp = Math.floor((float)(t+(task.period-task.deadline))/task.period);
		result = Math.min(task.executionTime, Math.max(0, t-tmp*task.period));
		return result;
	}
	private double W(Task task, int t)
	{
		double result = 0;
		double tmp;
		tmp = Math.floor((float)(t+(task.period-task.deadline))/task.period);
		result = tmp*task.executionTime + carryIn(task, t);
		//System.out.printf("W %f \n", result);
		return result;
	}
	private double I_hat(Task task_i, Task task_k, int A_k, int i, int k)
	{
		double result=0;
		if(i!=k)
		{
	        result = Math.min(W(task_i, A_k + task_k.deadline) - carryIn(task_i, A_k + task_k.deadline) , A_k + task_k.deadline - task_k.executionTime);
		}
	    else
	    {
	        result = Math.min(W(task_k, A_k + task_k.deadline) - task_k.executionTime - carryIn(task_k, A_k + task_k.deadline), A_k);
		}
		return result;
	}
	private double I_bar(Task task_i, Task task_k, int A_k, int i, int k)
	{
		double result=0;
		if(i!=k)
		{
	        result = Math.min(W(task_i, A_k + task_k.deadline), A_k + task_k.deadline - task_k.executionTime);
		}
	    else
	    {
	        result = Math.min(W(task_k, A_k + task_k.deadline) - task_k.executionTime, A_k);
		}
		return result;
	}
	/**
	 * get the sum of n largest elements
	 */
	private double getSumOfnLargest(double arr[], int n)
	{
		double sum = 0;
		int size = arr.length;
		if(n>size)
			n = size;
		Arrays.sort(arr);
		for(int i=size-1;i>size-n-1;i--)
	        sum += arr[i];
		return sum;
	}
	private void updateThetaIfLarger(int newTheta)
	{
		if(newTheta > Theta)
			Theta = newTheta;
	}
	public void PrintMPR()
	{
		System.out.print("--------------- MPR -------------  ");
		System.out.printf("Pi: %d Theta: %d m': %d U: %f \n", this.Pi, this.Theta, this.mPrime, getUtilization());
	}
	public double getUtilization()
	{
		return (double)Theta/Pi;
	}
	/*private double lsbf(int theta, int pi, int mprime, int t)
	{
		double result = 0;
		double tmp = 2*(pi-((double)theta/mprime))+2;
		result = ((double)theta/pi)*(t-tmp);
		return result;
	}*/
}
