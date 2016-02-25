
public class PR {
	private int Pi = 0;
	private int Theta;
	public PR() {
		
	}
	public void setPi(int inPi)
	{
		Pi = inPi;
	}
	public void setTheta(int inTheta)
	{
		Theta = inTheta;
	}
	public double getUtilization()
	{
		return (double)Theta/Pi;
	}
	private int deriveTheta(long t, double demand)
	{
		if(demand <= 0)
			return 0;
		double Theta1 = 0;
		//double Theta2 = 0;
		double a = 2/(double) Pi;
		double b = (((double)t/Pi) - 2);
		double c = -demand;
		double delta = b*b-4*a*c;
		Theta1 = (-b+ Math.sqrt(delta))/(2*a);
		//Theta2 = (-b- Math.sqrt(delta))/(2*a);
		//return Math.min(Theta1, Theta2);
		return (int) Math.ceil(Theta1);
	}
	/**
	 * Demand bound function assuming an implicit deadline task model.
	 * @param taskSet
	 * @param t
	 * @return
	 */
	private double dbf(TaskSet taskSet, long t)
	{
		double demand = 0;
	    for(int i=0;i<taskSet.getSize();i++)
	    {
	    	Task taski = taskSet.getTask(i);
	    	demand += Math.floor(t/taski.period)*taski.executionTime;
	    }
	        
	    return demand;
	}
	/**
	 * This function calculates budget assuming EDF task-scheduler.
	 * It assumes that the period Pi is already set.
	 * It also assumes an implicit deadline task model.
	 * @param taskSet
	 */
	public void calcInterface(TaskSet taskSet)
	{
		int tmpTheta = 0;
		double demand = 0;
		Theta = 0;
		if(Pi==0)
		{
			  System.err.println("Period is not set!");
			  return;
		}
		long t=1;
		while(t<2*taskSet.getLCMOfPeriods())
		{
			t = getNetTime(taskSet, t);
			demand = dbf(taskSet, t);
			tmpTheta = deriveTheta(t, demand);
			updateThetaIfLarger(tmpTheta);
		}
		
	}
	private void updateThetaIfLarger(int newTheta)
	{
		if(newTheta > Theta)
			Theta = newTheta;
	}
	public void Print()
	{
		System.out.print("--------------- PR -------------  ");
		System.out.printf("Pi: %d Theta: %f U: %f \n", this.Pi, this.Theta, getUtilization());
	}
	public void convertMPR2PR(MPR mpr)
	{
		this.Pi = mpr.getPi();
		this.Theta = mpr.getTheta();
	}
	public MPR convertPR2MPR(int parallelism)
	{
		MPR mpr = new MPR();
		mpr.setM(parallelism);
		mpr.setPi(this.getPi());
		mpr.setTheta(this.Theta);
		return mpr;
	}
	public int getTheta()
	{
		return this.Theta;
	}
	public int getPi()
	{
		return this.Pi;
	}
	private long getNetTime(TaskSet taskSet, long t)
	{
		Task taski = taskSet.getTask(0);
    	long nextT = (long) (Math.floor(t/taski.period)+1)*taski.period;
		for(int i=1;i<taskSet.getSize();i++)
	    {
	    	taski = taskSet.getTask(i);
	    	long tmp = (long) (Math.floor(t/taski.period)+1)*taski.period;
	    	if(tmp < nextT)
	    		nextT = tmp;
	    }
		return nextT;
	}
}
