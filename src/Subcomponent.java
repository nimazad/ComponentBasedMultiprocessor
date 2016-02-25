
public class Subcomponent {

	public Subcomponent(int inPi, TaskSet inTaskSet)
	{
		this.taskSet = inTaskSet;
		this.Pi = inPi;
	}
	private PR pr;
	private MPR[] mpr;//mpr with different parallelism levels
	private TaskSet taskSet;
	private int Pi = 0;
	private int usedParallelism = 0;//The parallelism that is used for integration
	public void setUsedParallelism(int inUsedParallelism)
	{
		this.usedParallelism = inUsedParallelism;
	}
	public int getUsedParallelism()
	{
		return this.usedParallelism;
	}
	public int getPi()
	{
		return this.Pi;
	}
	/**
	 * Derives the first interface of EPR
	 * This function assumes utilization <= 1
	 * @param taskSet
	 */
	private PR derivePR()
	{
		if(taskSet.getUtilization() > 1)
		{
			System.err.println("Utilization is more than one, cannot derive PR");
			return null;
		}
		PR pr = new PR();
		pr.setPi(this.Pi);
		pr.calcInterface(taskSet);
		return pr;
	}
	private MPR deriveMPR(int mPrime)
	{
		MPR mpr = new MPR();
		mpr.setPi(this.Pi);
		mpr.calcThetaForInM(taskSet, mPrime);
		return mpr;
	}
	/**
	 * calculates the interface
	 * @param M: number of parallelism levels, if M<=1 then it only calculates the PR interface.
	 */
	public void deriveInterface(int M)
	{
		this.pr = derivePR(); // first parallelism level
		mpr = new MPR[M+2]; //because mpr[1] and mpr[0] are empty!
		for(int j=1;j<=M;j++)
		{
			mpr[j+1] = deriveMPR(j+1); //Number of processors is equal to j+1 because we start from j=0; 
		}
		//System.out.printf("subComp[%d] out of %d finished \n", i, subComponents.size());
		
	}
	public void PrintComponentTaskSet()
	{
		taskSet.PrintTaskSet();
	}
	public double[] getEPR()
	{
		int eprSize = mpr.length;
		double[] epr = new double[eprSize];
		epr[1] = pr.getUtilization();
		for(int i=2;i<mpr.length;i++) //start from 2 because mpr[0] and mpr[1] are empty
		{
			epr[i] = mpr[i].getUtilization(); 
		}
		return epr;
	}
}
