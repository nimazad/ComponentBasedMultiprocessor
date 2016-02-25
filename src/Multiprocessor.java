import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Multiprocessor {

	private String allocationAlg = "FF";
	private String MPRAllocationAlg = "CP";
	private List<Processor> processors;
	public Multiprocessor() 
	{
		processors = new ArrayList<Processor>();
	}
	public Multiprocessor(int procCnt) 
	{
		processors = new ArrayList<Processor>();
		for(int i=0;i<procCnt;i++)
		{
			Processor proc = new Processor();
			this.AddProcessor(proc);
		}
	}
	public void setAllocationAlg(String alg)
	{
		this.allocationAlg = alg;
	}
	public void setMPRAllocationAlg(String alg)
	{
		this.MPRAllocationAlg = alg;
	}
	public int getSize()
	{
		return processors.size();
	}
	public void AddProcessor(Processor newProc)
	{
		this.processors.add(newProc);
	}
	public int getNOProcessors()
	{
		return this.processors.size();
	}
	public boolean allocate( double size)
	{
		switch(this.allocationAlg)
		{
			case "WF": return WorstFit(size);
			case "BF": return BestFit(size);
			default: return FirstFit(size);
		}
	}
	public boolean MPRAllocate(double size, int parallelism, int Pi, Multiprocessor multiproc)
	{
		switch(this.MPRAllocationAlg)
		{
			case "CP": return allocateMPRCompact(size, parallelism, Pi, multiproc);
			case "BL": return allocateMPRBalanced(size, parallelism, Pi, multiproc);
			default: return allocateMPRCompact(size, parallelism, Pi, multiproc);
		}
	}
	public boolean AllocateOnProc(double size, int procID)
	{		
		Processor proc = this.processors.get(procID);
		if(proc.getSlack() >= size)
		{
			proc.AddSubcomp(size);
			return true; // Successfully fit
		}
		else
			return false;
	}

	/**
	 * Implements the first fit heuristic
	 * @param pr
	 * @return -1 means that the PR did not fit, +1 means it fit
	 */
	public boolean FirstFit(double size)
	{
		
		for(int i=0;i<this.getNOProcessors();i++)
		{
			Processor proc = this.processors.get(i);
			//System.out.printf("proc[%d] prUtil: %.2f, slack: %.2f \n", i, pr.getUtilization(), proc.getSlack());
			if(proc.getSlack() >= size)
			{
				proc.AddSubcomp(size);
				return true; // Successfully fit
			}
		}
		return false;
		// In this case we need to add a new processor
		// because the PR did not fit into the current processors
	}
	public boolean BestFit(double size)
	{
		int indx = -1;
		double leastSlack = Double.MAX_VALUE;
		for(int i=0;i<this.getNOProcessors();i++)
		{
			Processor proc = this.processors.get(i);
			double currentSlack = proc.getSlack() - size;
			if(proc.getSlack() >= size && currentSlack < leastSlack)
			{
				indx = i;
				leastSlack = currentSlack;
			}
		}
		if(indx > -1)
		{
			this.processors.get(indx).AddSubcomp(size);
			return true; // Successfully fit
		}
		else
			return false;
	}
	public boolean WorstFit(double size)
	{
		int indx = -1;
		double maxSlack = 0;
		for(int i=0;i<this.getNOProcessors();i++)
		{
			Processor proc = this.processors.get(i);
			double currentSlack = proc.getSlack() - size;
			if(proc.getSlack() >= size && currentSlack > maxSlack)
			{
				indx = i;
				maxSlack = currentSlack;
			}
		}
		if(indx > -1)
		{
			this.processors.get(indx).AddSubcomp(size);
			return true; // Successfully fit
		}
		else
			return false;
	}
	public double getMaxSlack()
	{
		double maxSlack = 0;
		for(int i=0;i<this.getNOProcessors();i++)
		{
			Processor proc = this.processors.get(i);
			if(proc.getSlack() > maxSlack)
				maxSlack = proc.getSlack();
		}
		return maxSlack;
	}
	public double getTotalSlack()
	{
		double totalSlack = 0;
		for(int i=0;i<this.getNOProcessors();i++)
		{
			Processor proc = this.processors.get(i);
			totalSlack += proc.getSlack();
		}
		return totalSlack;
	}
	public double getTotalUtilization()
	{
		double totalUtil = 0;
		for(int i=0;i<this.getNOProcessors();i++)
		{
			Processor proc = this.processors.get(i);
			totalUtil += proc.getUtilization();
		}
		return totalUtil;
	}

	public Multiprocessor Copy()
	{
		Multiprocessor multiProc = new Multiprocessor();
		multiProc.setAllocationAlg(this.allocationAlg);
		multiProc.setMPRAllocationAlg(this.MPRAllocationAlg);
		for(int i=0;i<processors.size();i++)
		{
			Processor proc = this.processors.get(i).Copy();
			multiProc.AddProcessor(proc);
		}
		return multiProc;
	}
	public void Copy(Multiprocessor from, Multiprocessor to)
	{
		//to = from;
		to.processors.clear();
		for(int i=0;i<from.processors.size();i++)
		{
			Processor proc = from.processors.get(i).Copy();
			to.AddProcessor(proc);
		}
		return;
	}
	public boolean allocateMPR(MPR mpr)
	{
		double size = mpr.getUtilization();
		int parallelism = mpr.getMprime();
		int Pi = mpr.getPi();
		return MPRAllocate(size, parallelism, Pi, this);
	}
	/*
	public boolean allocateMPR(double size, int parallelism, int Pi, Multiprocessor multiproc)
	{
		int cnt = 0;
		double tmpSize = size;
		while(tmpSize > 0)
		{
			double maxSlack = multiproc.getMaxSlack();
			cnt ++;
			int theta = (int) Math.min(Math.ceil(tmpSize*Pi), Math.floor(maxSlack * Pi));
			double util = ((double)theta)/Pi;
			if(multiproc.allocate(util) == false)
			{
				//System.err.println("firstfit failed!");
				return false;
			}
			if(theta == 0)
			{
				//System.err.println("theta == 0, i.e., slack is not sufficient!");
				return false;
			}
			
			tmpSize -= util;
			tmpSize = Math.round(tmpSize*1000)/1000.0; // To avoid double precision problem
			if(tmpSize>0 && tmpSize < 1.0/((double)Pi))
				tmpSize = 1.0/Pi; //This is to avoid tmpSize>0 && theta == 0
		}
		if(cnt <= parallelism)
		{
			return true; //Successful decomposition and allocation 
		}
		else
			//System.err.printf("mPrime:%d less than cntPR:%d! \n", parallelism, cnt);
		return false;
	}
	*/
	private boolean allocateMPRBalanced(double size, int parallelism, int Pi, Multiprocessor multiproc)
	{
		multiproc.SortProcsDecreasingSlack();
		int indx = multiproc.findFirstMProc(parallelism, size);
		if(indx<0)
		{
			return false;
		}
		double totalSlack = 0;
		for(int i=indx;i<parallelism;i++)
		{
			totalSlack += multiproc.processors.get(i).getSlack();
		}
		double targetSlack = (totalSlack-size)/parallelism;
		int targetSlackTheta = (int) Math.floor((targetSlack*Pi));
		targetSlack = ((double)targetSlackTheta)/Pi;
		targetSlack = Math.round(targetSlack*1000)/1000.0; // To avoid double precision problem
		double tmpSize = size;
		while(tmpSize > 0 && indx < multiproc.getSize())
		{
			double maxSlack = multiproc.processors.get(indx).getSlack() - targetSlack;
			int theta = (int) Math.min(Math.ceil(tmpSize*Pi), Math.floor(maxSlack * Pi));
			double util = ((double)theta)/Pi;
			if(multiproc.AllocateOnProc(util, indx) == false)
			{
				return false;
			}
			tmpSize -= util;
			tmpSize = Math.round(tmpSize*1000)/1000.0; // To avoid double precision problem
			indx++;
		}
		if(tmpSize <=0 )
			return true;
		else
			return false;
	}
	
	private boolean allocateMPRCompact(double size, int parallelism, int Pi, Multiprocessor multiproc)
	{
		multiproc.SortProcsInreasingSlack();
		int indx = multiproc.findFirstMProc(parallelism, size);
		if(indx<0)
		{
			return false;
		}
		double tmpSize = size;
		while(tmpSize > 0)
		{
			if(indx >= multiproc.getSize())
			{
				//System.err.print("index out of range");
				return false;//could not allocate because of the round ups due to integer Pi and int theta
			}
			double maxSlack = multiproc.processors.get(indx).getSlack();
			int theta = (int) Math.min(Math.ceil(tmpSize*Pi), Math.floor(maxSlack * Pi));
			double util = ((double)theta)/Pi;
			if(multiproc.AllocateOnProc(util, indx) == false)
			{
				return false;
			}
			tmpSize -= util;
			tmpSize = Math.ceil(tmpSize*1000)/1000.0; // To avoid double precision problem
			indx++;
		}
		return true;
	}
	/**
	 * finds first m processors that their total slack is more than or eq. size
	 * @param mPrime parallelism level
	 * @param size component size
	 * @return index of the first processor
	 */
	public int findFirstMProc(int mPrime, double size)
	{
		for(int i=0;i<this.getSize();i++)
		{
			int end = Math.min(this.getSize(), i+mPrime);
			double totalSlack = 0;
			for(int j=i;j<end;j++)
			{
				totalSlack += this.processors.get(j).getSlack();
			}
			if(totalSlack >= size)
			{
				return i;
			}
		}
		return -1;// could not find, i.e., it does not fit
	}
	public void SortProcsInreasingSlack()
	{
		int swapped = 1;
		while (swapped == 1)
		{
			swapped = 0;
			for(int i=1;i<this.getSize();i++)
			{
				if(this.processors.get(i-1).getSlack() > this.processors.get(i).getSlack())
					{
						swapProccessor(i-1, i);
						swapped = 1;
					}
			}
		}
	}
	public void SortProcsDecreasingSlack()
	{
		int swapped = 1;
		while (swapped == 1)
		{
			swapped = 0;
			for(int i=1;i<this.getSize();i++)
			{
				if(this.processors.get(i-1).getSlack() < this.processors.get(i).getSlack())
					{
						swapProccessor(i-1, i);
						swapped = 1;
					}
			}
		}
	}
	private void swapProccessor(int A, int B)
	{
		Collections.swap(this.processors, A, B);
	}
	private boolean allocateSubcomponent(int parallelism, Subcomponent subcomp, Multiprocessor multiproc)
	{
		if(parallelism == 1)
		{
			return multiproc.allocate(subcomp.getEPR()[parallelism]);
		}
		else
		{
			if(subcomp.getEPR()[parallelism] > multiproc.getTotalSlack())
			{
				//System.err.println("In this parallelism level the total utilization is more than the slack!");
				//System.out.printf("par: %d, mprUtil: %.2f, totalSlack: %.2f\n", parallelism, subcomp.getEPR()[parallelism], this.getTotalSlack());
				return false;
			}
			return MPRAllocate(subcomp.getEPR()[parallelism], parallelism , subcomp.getPi(), multiproc);
		}
	}
	private int getMaxArray(int[] array)
	{
		int max = 0;
		for(int i=0;i<array.length;i++)
		{
			if(array[i]>max)
					max = array[i];
		}
		return max;
	}
	public int allocateEPR(EPR epr)
	{
		int[] parallelisms = new int[epr.getNoSubcomps()];
		Multiprocessor tmpProc = this.Copy();
		// Initial all parallelsims to zero
		for(int i=0;i<epr.getNoSubcomps();i++)
			parallelisms[i] = 1;
		boolean allocated = false;
		while(!allocated && EPRPossible(epr, parallelisms, this))
		{
			allocated = true;
			tmpProc = this.Copy(); //Refresh the tmp, since the last one failed
			int maxPar = getMaxArray(parallelisms);
			for(int currentPar=1;currentPar<=maxPar;currentPar++)
			{
				for(int i=0;i<epr.getNoSubcomps();i++)
				{
					if(parallelisms[i] == currentPar)
					{
						Subcomponent tmpSubcomp = epr.subComponents.get(i);
						boolean allocationResult =  allocateSubcomponent(parallelisms[i], tmpSubcomp, tmpProc);
						if(allocationResult == false)
						{
							allocated = false;
							parallelisms = increaseFlexibility(epr, parallelisms);
							//tmpProc.Print();
							//System.out.printf("\n epr %f, %d \n", tmpSubcomp.getEPR()[parallelisms[i]], parallelisms[i]);
							currentPar = maxPar +1; //to break from the outer loop as well
							break;
						}
					}
				}
			}
		}
		if(allocated)
		{
			//printParallelisms(parallelisms);
			epr.SetParallelisms(parallelisms);
			this.Copy(tmpProc, this);
			return 1; //Successfully allocated
		}
		else
			return -1;//could not allocate
	}
	private boolean EPRPossible(EPR epr, int [] parallelisms, Multiprocessor multiproc)
	{
		double totalSlack = multiproc.getTotalSlack();
		double util = 0;
		for(int i=0;i<epr.getNoSubcomps();i++)
		{
			util += epr.getEPR(i, parallelisms[i]);
		}
		if(util > totalSlack)
		{
			//System.err.printf("In this parallelism (%d) level the total utilization is more than the slack! \n", parallelisms[i]);
			return false;
		}
		return true;
	}
	private int[] increaseFlexibility(EPR epr, int[] parallelisms)
	{
		int indx = epr.getMinDeltaSubcomp(parallelisms);
		parallelisms[indx]++;
		//System.out.printf("par:%d \n", parallelisms[indx]);
		return parallelisms;
	}
	public void Print()
	{
		for(int i=0;i<processors.size();i++)
		{
			System.out.printf("Processor %d: ", i);
			processors.get(i).PrintProcessor();
		}
		System.out.printf("\n === Total util: %.2f === \n", this.getTotalUtilization());
	}
}
