import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RTSystem {

	public List<Component> components;
	private Multiprocessor multiprocessor;
	private int maxProcessors = 3;//needed for new epr!
	private EPR globalEPR;// This list is used to perform integration
	public RTSystem() 
	{
		components = new ArrayList<Component>();
	}
	public void AddComponent(Component comp)
	{
		components.add(comp);
	}
	private double avgEPRparallelsim;
	private long index;
	public void setIndex(long i)
	{
		this.index = i;
	}
	public long getIndex()
	{
		return this.index;
	}
	public double getAverageEPRParallelsim()
	{
		return this.avgEPRparallelsim;
	}
	public int IntegrateEPROnMinProcessors_global(String taskPartitioning, String subcomponentAllocation)
	{
		int M = this.maxProcessors; // to save time
		/*for(int i=0; i<this.components.size();i++)
		{
			M += this.components.get(i).getM();
		}*/
		
		globalEPR = new EPR(M);
		//First update the subcomponents
		this.globalEPR.ClearSubcomponents();
		for(int i=0; i<this.components.size();i++)
		{
			Component comp = this.components.get(i);
			comp.setPartitioningAlg(taskPartitioning);
			comp.Decompose();
			globalEPR.importEPRSubcomponents(this.components.get(i).getEPRSubcomponents());
		}
		globalEPR.deriveEPR();
		globalEPR.SortSubcompsUtilization(0);
		int minProc = (int) Math.ceil(this.getEPRUtilization()) ;
		int noProcs = minProc;
		boolean allocation = false;
		while(!allocation && noProcs <= minProc*2)
		{
			//System.out.printf("====== %d ====== \n", noProcs);
			allocation = IntegrateEPROnMProcessors_global(noProcs, subcomponentAllocation);
			if(!allocation)
				noProcs ++;
		}
		if(allocation)
		{
			//System.out.printf("globalEPR integrated on %d processors, total global subcomps: %d \n", noProcs, this.globalEPR.getNoSubcomps());
			//this.globalEPR.PrintParallelisms();
			this.avgEPRparallelsim = this.globalEPR.GetAverageParallelisms();
			//System.out.printf(taskPartitioning+subcomponentAllocation+": \n");
			//multiprocessor.Print();
			return noProcs;
		}
		else
			return -1;
		
	}
	public boolean IntegrateEPROnMProcessors_global(int noProcs, String subcomponentAllocation)
	{
		multiprocessor = new Multiprocessor(noProcs);
		multiprocessor.setAllocationAlg(subcomponentAllocation);
		if(multiprocessor.allocateEPR(this.globalEPR) == -1)
		{
			//System.err.printf("could not integrate globalEPR on %d processors! \n", noProcs);
			return false;
		}
		else
			return true;
	}
	
	public void createSampleSystem(int cntComponents)
	{
		for(int i=0;i<cntComponents;i++)
		{
			Component comp = createSampleComponent(3, 2);
			this.AddComponent(comp);
		}
	}
	private Component createSampleComponent(int cntTasksSmall, int cntTasksBig)
	{
		TaskSet myTasks = new TaskSet();
		myTasks.CreateSampleTaskSet(cntTasksSmall, cntTasksBig);
		Component comp = new Component(myTasks, maxProcessors);
		int Pi = myTasks.getMinPeriods()/2;
		comp.setPi(Pi);
		return comp;
	}
	private Component createRandomComponent(int indx, double totalUtil, double maxTaskUtil, int minTaskPeriod, int deltaTaskPeriod)
	{
		TaskSet myTasks = new TaskSet();
		myTasks.setPeriodRange(minTaskPeriod, deltaTaskPeriod);
		myTasks.CreateRandomTaskSet(totalUtil, maxTaskUtil);
		myTasks.setIndex(indx);
		Component comp = new Component(myTasks, maxProcessors);
		int Pi = myTasks.getMinPeriods()/2;
		comp.setPi(Pi);
		return comp;
	}
	public void createRandomSystem(double totalSystemUtil, double maxTaskUtil, double minCompUtil, double maxCompUtil, int minTaskPeriod, int deltaTaskPeriod)
	{
		double Ur = totalSystemUtil;
		Random rand = new Random();
		int indx = 0;
		while (Ur > maxCompUtil)
		{
			double util = minCompUtil + rand.nextDouble()*(maxCompUtil - minCompUtil);
			Ur -= util;
			Component comp = createRandomComponent(indx, util, maxTaskUtil, minTaskPeriod, deltaTaskPeriod);
			this.AddComponent(comp);
			indx++;
		}
		Component comp = createRandomComponent(indx, Ur, maxTaskUtil, minTaskPeriod, deltaTaskPeriod);
		this.AddComponent(comp);
	}
	public boolean IntegrateMPROnMProcessors(int noProcs, String subcomponentAllocation)
	{
		multiprocessor = new Multiprocessor(noProcs);
		multiprocessor.setMPRAllocationAlg(subcomponentAllocation);
		for(int i=0; i<this.components.size();i++)
		{
			MPR mpr = components.get(i).getMPR();
			if(multiprocessor.allocateMPR(mpr) == false)
			{
				//System.err.printf("could not integrate MPR on %d processors! \n", noProcs);
				return false;//failed
			}
		}
		return true;//successful
	}
	public int IntegrateMPROnMinProcessors(String subcomponentAllocation)
	{
		int minProcs = (int) Math.ceil(this.getMPRUtilization());
		int noProcs = minProcs;
		boolean result = IntegrateMPROnMProcessors(noProcs, subcomponentAllocation);
		while(!result && noProcs <= minProcs*2)
		{
			noProcs ++;
			result = IntegrateMPROnMProcessors(noProcs, subcomponentAllocation);
		}
		/*
		if(result)
			System.out.printf("MPR integrated on %d processors \n", noProcs);
		else
			System.err.printf("could not integrate MPR on %d processors", noProcs);
			*/
		if(result)
		{
			//System.out.printf(subcomponentAllocation+":\n");
			//multiprocessor.Print();
			return noProcs;
		}
		else
			return -1;
	}
	public int IntegrateEPROnMinProcessors()
	{
		int noProcs = (int) Math.ceil(this.getEPRUtilization());
		int allocation = -1;
		while(allocation == -1 && noProcs <= this.maxProcessors*2)
		{
			//System.out.printf("====== %d ====== \n", noProcs);
			allocation = IntegrateEPROnMProcessors(noProcs);
			if(allocation == -1)
				noProcs ++;
		}
		if(allocation == 1)
			System.out.printf("EPR integrated on %d processors, total subcomps: %d \n", noProcs, getNoSubcomponents());
		return multiprocessor.getSize();
	}
	public int IntegrateEPROnMProcessors(int noProcs)
	{
		multiprocessor = new Multiprocessor(noProcs);
		int result = 1; //successful
		for(int i=0; i<this.components.size();i++)
		{
			Component comp = components.get(i);
			comp.setM(this.maxProcessors);
			EPR epr = comp.getEPR();
			//System.out.printf("component: %d totalSlack: %.2f \n", i, this.multiprocessor.getTotalSlack());
			//this.multiprocessor.Print();
			if(multiprocessor.allocateEPR(epr) == -1)
			{
				//System.err.printf("could not integrate EPR on %d processors! stopped at: %d \n", noProcs, i);
				return -1;
			}
		}
		System.out.printf("EPR used processor: %.2f, totalSlack: %.2f\n", this.multiprocessor.getSize()-this.multiprocessor.getTotalSlack(), this.multiprocessor.getTotalSlack());
		return result;
	}
	public void PrintComponents()
	{
		for(int i=0; i<this.components.size();i++)
		{
			//components.get(i).PrintComponentTaskSet();
			System.out.printf("== comp[%d]: tasksetUtil: %.2f EPRUtil: %.2f MPRUtil: %.2f \n", i, 
					components.get(i).getTaskSet().getUtilization(), components.get(i).getEPR().getUtilization()[0], components.get(i).getMPR().getUtilization());
			//components.get(i).getMPR().PrintMPR();
		}
	}
	public void PrintProcessors()
	{
		System.out.printf("Print processors MPR :\n");
		this.multiprocessor.Print();
		System.out.printf("Print processors EPR :\n");
		this.multiprocessor.Print();
	}
	public double getMPRUtilization()
	{
		double util = 0;
		for(int i=0; i<this.components.size();i++)
		{
			util += components.get(i).getMPR().getUtilization();
		}
		return util;
	}
	public void PrintTotalMPRUtilization()
	{
		System.out.printf("Total MPR util: %.2f \n", this.getMPRUtilization());
	}
	public void PrintTotalEPRUtilization()
	{
		System.out.printf("Total EPR util: %.2f \n", this.getEPRUtilization());
	}
	public double getEPRUtilization()
	{
		double util = 0;
		for(int i=0; i<this.components.size();i++)
		{
			Component comp = new Component(); 
			comp = components.get(i);
			comp.setM(this.maxProcessors);
			comp.getEPR();
			util += comp.getEPRUtilization(1); // 1 means parallelsim 1
		}
		return util;
	}
	public double getTotalTaskSetUtilization()
	{
		double util = 0;
		for(int i=0; i<this.components.size();i++)
		{
			Component comp = new Component(); 
			comp = components.get(i);
			util += comp.getTaskSet().getUtilization();
		}
		return util;
	}
	public int getNoSubcomponents()
	{
		int cnt = 0;
		for(int i=0; i<this.components.size();i++)
		{
			Component comp = new Component(); 
			comp = components.get(i);
			comp.setM(this.maxProcessors);
			comp.getEPR();
			cnt += comp.getEPR().getNoSubcomps();
		}
		return cnt;
	}
	public double GetAverageGlobalEPRParallelism()
	{
		return globalEPR.GetAverageParallelisms();
	}
	public List<Component> getComponentes()
	{
		return this.components;
	}
	
}
