import java.util.Random;




public class Experiment {

	public Experiment() {
		
	}
	public void testExperiment()
	{
		//double totalUtil = 3.1;
		int M = 4;
		Log log = new Log("test");
		log.DeleteFiles();
		
		System.out.println("test exp started");
		for(int i=0;i<2;i++)
		{
			TaskSet myTasks = new TaskSet();
			myTasks.setPeriodRange(100, 100);
			//myTasks.CreateRandomTaskSet(totalUtil, .9);
			//myTasks.CreateSampleTaskSet(5);
			//myTasks.PrintTaskSet();
			
			Component comp = new Component(myTasks, M);
			//System.out.printf("utilization %f \n", comp.getTaskSet().getUtilization());
			comp.setPi(50);
			comp.getMPR();
			//comp.getPR().PrintMPR();
			
			//comp.FFDecompose();
			comp.setM(2);// to save computation time
			comp.getEPR();
			
			//System.out.printf("MPR overhead: %f \n", comp.getMPROverhead());
			//comp.PrintEPROverhead();
			comp.getTaskSet().setIndex(i);
			String util = Double.toString(myTasks.getUtilization());
			String MPROverhead = Double.toString(comp.getMPROverhead());
			String EPROverhead = Double.toString(comp.getEPROverhead()[0]);
			String MPRTime = Long.toString(comp.getMPRTime());
			String EPRTime = Long.toString(comp.getEPRTime());
			String LCM = Long.toString(myTasks.getLCMOfPeriods());
			String avgPeriods = Double.toString(myTasks.getMeanPeriods());
			String stdPeriods = Double.toString(myTasks.getStdPeriods());
			log.WriteLog(myTasks, util, MPROverhead, EPROverhead, MPRTime, EPRTime, LCM, avgPeriods, stdPeriods);
		}
		System.out.println("test exp  ended");
		//log.PrintList(log.ReadFile());
		/*
		Plot p = new Plot("test", log.ReadFile());
		p.pack();
        RefineryUtilities.centerFrameOnScreen(p);
        p.setVisible(true);
        */
	}
	public void OverheadUtilExperiment(boolean newExp, double totalUtil)
	{
		double maxUtil = 8.1;
		double maxTaskUtil = 0.9;
		int cntTaskSetPerUtil = 1000;
		int minTaskPeriod = 100;
		int deltaTaskPeriod = 100;
		int M = 4;
		int Pi = 50;
		Log log = new Log("OverheadUtil");
		if(newExp)
			log.DeleteFiles();
		
		System.out.println("\n Overhead Util experiment started");
		while (totalUtil <= maxUtil)
		{
			System.out.printf("\n util: %.2f ", totalUtil);
			for(int i=0;i<cntTaskSetPerUtil;i++)
			{
				System.out.printf("%d, ", i);
				TaskSet myTasks = new TaskSet();
				myTasks.setPeriodRange(minTaskPeriod, deltaTaskPeriod);
				myTasks.CreateRandomTaskSet(totalUtil, maxTaskUtil);
				Component comp = new Component(myTasks, M);
				comp.setPi(Pi);
				comp.getMPR();
				//comp.FFDecompose();
				comp.setM(2);// to save computation time
				comp.getEPR();
				comp.getTaskSet().setIndex(i);
				//Logging
				String util = Double.toString(myTasks.getUtilization());
				String MPROverhead = Double.toString(comp.getMPROverhead());
				String EPROverhead1 = Double.toString(comp.getEPROverhead()[1]);
				String EPROverhead2 = Double.toString(comp.getEPROverhead()[2]);
				String EPROverheadMean = Double.toString(comp.getEPRMeanDelta(1));//1 means the second level of parallelsim since we start from zero
				String cntTasks = Integer.toString(myTasks.getSize());
				log.WriteLog(myTasks, util, MPROverhead, EPROverhead1, EPROverhead2, EPROverheadMean, cntTasks);
			}
			totalUtil += 0.1;
		}
		System.out.println("OverheadUtil experiment ended");
		
	}
	public void MaxTaskUtilExperiment(boolean newExp, double maxTaskUtil)
	{
		double maxmaxTaskUtil = 0.9;
		double totalUtil = 2.5;
		int cntTaskSetPerUtil = 1000;
		int minTaskPeriod = 100;
		int deltaTaskPeriod = 100;
		int M = 4;
		int Pi = 50;
		Log log = new Log("MaxTaskUtil");
		if(newExp)
			log.DeleteFiles();
		
		System.out.println("\n MaxTaskUtil experiment started");
		while (maxTaskUtil <= maxmaxTaskUtil)
		{
			System.out.printf("\n maxTaskUtil: %.2f ", maxTaskUtil);
			for(int i=0;i<cntTaskSetPerUtil;i++)
			{
				System.out.printf("%d, ", i);
				TaskSet myTasks = new TaskSet();
				myTasks.setPeriodRange(minTaskPeriod, deltaTaskPeriod);
				myTasks.CreateRandomTaskSet(totalUtil, maxTaskUtil);
				Component comp = new Component(myTasks, M);
				comp.setPi(Pi);
				comp.getMPR();
				//comp.FFDecompose();
				comp.setM(2);// to save computation time
				comp.getEPR();
				comp.getTaskSet().setIndex(i);
				//Logging
				String util = Double.toString(myTasks.getUtilization());
				String MPROverhead = Double.toString(comp.getMPROverhead());
				String EPROverhead1 = Double.toString(comp.getEPROverhead()[1]);
				//String EPROverhead2 = Double.toString(comp.getEPROverhead()[2]);
				String EPROverheadMean = Double.toString(comp.getEPRMeanDelta(1));//1 means the second level of parallelsim since we start from zero
				String cntTasks = Integer.toString(myTasks.getSize());
				String maxTUtil = Double.toString(maxTaskUtil);
				log.WriteLog(myTasks, util, maxTUtil, MPROverhead, EPROverhead1, EPROverheadMean, cntTasks);
			}
			maxTaskUtil += 0.1;
		}
		System.out.println("MaxTaskUtil experiment ended");
		
	}
	public void PiExperiment(boolean newExp, double totalUtil)
	{
		
		double maxTaskUtil = 0.9;
		int cntTaskSetPerPi = 10000;
		int minTaskPeriod = 100;
		int deltaTaskPeriod = 100;
		int M = 4;
		int Pi = 10;
		int maxPi = 210;
		Log log = new Log("Pi");
		if(newExp)
			log.DeleteFiles();
		
		System.out.println("\n Pi experiment started");
		while (Pi <= maxPi)
		{
			System.out.printf("\n Pi: %d ", Pi);
			for(int i=0;i<cntTaskSetPerPi;i++)
			{
				System.out.printf("%d, ", i);
				TaskSet myTasks = new TaskSet();
				myTasks.setPeriodRange(minTaskPeriod, deltaTaskPeriod);
				myTasks.CreateRandomTaskSet(totalUtil, maxTaskUtil);
				Component comp = new Component(myTasks, M);
				comp.setPi(Pi);
				comp.getMPR();
				//comp.FFDecompose();
				comp.setM(2);// to save computation time
				comp.getEPR();
				comp.getTaskSet().setIndex(i);
				//Logging
				String util = Double.toString(myTasks.getUtilization());
				String MPROverhead = Double.toString(comp.getMPROverhead());
				String EPROverhead1 = Double.toString(comp.getEPROverhead()[1]);
				//String EPROverhead2 = Double.toString(comp.getEPROverhead()[2]);
				String EPROverheadMean = Double.toString(comp.getEPRMeanDelta(1));//1 means the second level of parallelsim since we start from zero
				String cntTasks = Integer.toString(myTasks.getSize());
				String currentPi = Integer.toString(Pi);
				log.WriteLog(myTasks, util, MPROverhead, EPROverhead1, EPROverheadMean, cntTasks, currentPi);
			}
			Pi+=10;
		}
		System.out.println("Pi experiment ended");
		
	}
	public void AllRandomExperiment(boolean newExp, long startTaskID)
	{
		double maxTotalUtil = 4;
		double minTotalUtil = 1.5;
		double maxTaskUtil = 0.9;
		int totalComps = 10000000;
		int minTaskPeriod = 50;
		int deltaTaskPeriod = 100;
		int M = 4;
		int Pi = 50;
		Log log = new Log("AllRandom");
		if(newExp)
			log.DeleteFiles();
		
		System.out.println("AllRandom experiment started");
		Random rand = new Random();
		for(long i=startTaskID;i<totalComps;i++)
		{
			printStatus(i);
			TaskSet myTasks = new TaskSet();
			myTasks.setPeriodRange(minTaskPeriod, deltaTaskPeriod);
			double totalUtil = minTotalUtil + rand.nextDouble()*(maxTotalUtil - minTotalUtil); 
			myTasks.CreateRandomTaskSet(totalUtil, maxTaskUtil);
			Component comp = new Component(myTasks, M);
			comp.setPi(Pi);
			comp.getMPR();
			//comp.FFDecompose();
			comp.setM(2);// to save computation time
			comp.getEPR();
			comp.getTaskSet().setIndex(i);
			//Logging
			String util = Double.toString(myTasks.getUtilization());
			String cntTasks = Integer.toString(myTasks.getSize());
			String utilDiff = Double.toString(Math.ceil(myTasks.getUtilization()) - myTasks.getUtilization());
			String MPROverhead = Double.toString(comp.getMPROverhead());
			String EPROverhead0 = Double.toString(comp.getEPROverhead()[0]);
			String EPROverhead1 = Double.toString(comp.getEPROverhead()[1]);
			String EPROverheadMean = Double.toString(comp.getEPRMeanDelta(1));//1 means second level of parallelsim since we start from zero
			String MPRTime = Long.toString(comp.getMPRTime());
			String EPRTime = Long.toString(comp.getEPRTime());
			String LCM = Long.toString(myTasks.getLCMOfPeriods());
			String avgPeriods = Double.toString(myTasks.getMeanPeriods());
			String stdPeriods = Double.toString(myTasks.getStdPeriods());
			log.WriteLog(myTasks, util, cntTasks, utilDiff, MPROverhead, EPROverhead0, EPROverhead1, 
					EPROverheadMean, MPRTime, EPRTime, LCM, avgPeriods, stdPeriods);
		}
		System.out.println("AllRandom experiment ended");
		
	}
	public void Integration(boolean newExp, long startSysID)
	{
		int totalSysUtil = 6;
		double maxTaskUtil = .9;
		double minComponentUtil = 1.5;
		double maxCompUtil = 3.0;
		int totalSys = 10000;
		Log log = new Log("Integration");
		//log.ReadSystem(0);
		if(newExp)
			log.DeleteFiles();
		
		System.out.println("Integration experiment started");
		long sysID = 0;
		for(totalSysUtil = 5;totalSysUtil<12;totalSysUtil++)
		{
			for(long i=startSysID;i<totalSys;i++)
			{
				printStatus(sysID);
				RTSystem mysys = new RTSystem();
				mysys.setIndex(sysID);
				sysID++;
				mysys.createRandomSystem(totalSysUtil, maxTaskUtil, minComponentUtil, maxCompUtil, 100, 100);
				log.LogSystem(mysys);
				int mprCP = mysys.IntegrateMPROnMinProcessors("CP");
				int mprBL = mysys.IntegrateMPROnMinProcessors("BL");
				double mprUtil = mysys.getMPRUtilization();
				
				int eprFFFF = mysys.IntegrateEPROnMinProcessors_global("FF", "FF");
				double parFFFF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprWFFF = mysys.IntegrateEPROnMinProcessors_global("WF", "FF");
				double parWFFF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprBFFF = mysys.IntegrateEPROnMinProcessors_global("BF", "FF");
				double parBFFF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprFFWF = mysys.IntegrateEPROnMinProcessors_global("FF", "WF");
				double parFFWF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprWFWF = mysys.IntegrateEPROnMinProcessors_global("WF", "WF");
				double parWFWF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprBFWF = mysys.IntegrateEPROnMinProcessors_global("BF", "WF");
				double parBFWF = mysys.GetAverageGlobalEPRParallelism();
	
				int eprFFBF = mysys.IntegrateEPROnMinProcessors_global("FF", "BF");
				double parFFBF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprWFBF = mysys.IntegrateEPROnMinProcessors_global("WF", "BF");
				double parWFBF = mysys.GetAverageGlobalEPRParallelism();
				
				int eprBFBF = mysys.IntegrateEPROnMinProcessors_global("BF", "BF");
				double parBFBF = mysys.GetAverageGlobalEPRParallelism();
				
				//Logging
				String Util			= Long.toString(totalSysUtil);
				String taskSetUtil  = Double.toString(mysys.getTotalTaskSetUtilization());
				String ProcmprCP 	= Integer.toString(mprCP);
				String ProcmprBL 	= Integer.toString(mprBL);
				String ProceprFFFF 	= Integer.toString(eprFFFF);
				String pareprFFFF 	= Double.toString(parFFFF);
				String ProceprWFFF 	= Integer.toString(eprWFFF);
				String pareprWFFF 	= Double.toString(parWFFF);
				String ProceprBFFF 	= Integer.toString(eprBFFF);
				String pareprBFFF 	= Double.toString(parBFFF);
				String ProceprFFWF 	= Integer.toString(eprFFWF);
				String pareprFFWF 	= Double.toString(parFFWF);
				String ProceprWFWF 	= Integer.toString(eprWFWF);
				String pareprWFWF 	= Double.toString(parWFWF);
				String ProceprBFWF 	= Integer.toString(eprBFWF);
				String pareprBFWF 	= Double.toString(parBFWF);
				String ProceprFFBF 	= Integer.toString(eprFFBF);
				String pareprFFBF 	= Double.toString(parFFBF);
				String ProceprWFBF 	= Integer.toString(eprWFBF);
				String pareprWFBF 	= Double.toString(parWFBF);
				String ProceprBFBF 	= Integer.toString(eprBFBF);
				String pareprBFBF 	= Double.toString(parBFBF);
				String totalMPRUtil = Double.toString(mprUtil);
				
				log.WriteLog(mysys, taskSetUtil, Util, totalMPRUtil, ProcmprCP, ProcmprBL, 
						ProceprFFFF, ProceprWFFF, ProceprBFFF, ProceprFFWF, ProceprWFWF, ProceprBFWF, ProceprFFBF, ProceprWFBF, ProceprBFBF,
						pareprFFFF,  pareprWFFF,  pareprBFFF,  pareprFFWF,  pareprWFWF,  pareprBFWF,  pareprFFBF,  pareprWFBF,  pareprBFBF);
			}
		}
		System.out.println("Integration experiment ended");
		
	}
	
	private void printStatus(long cnt)
	{
		if (cnt % 10 == 0)
			System.out.printf("\n ");
		System.out.printf("%d, ", cnt);
	}

}
