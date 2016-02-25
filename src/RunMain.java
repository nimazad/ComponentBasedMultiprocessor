

public class RunMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double totalUtil = 1.1;
		TaskSet myTasks = new TaskSet();
		myTasks.setPeriodRange(10000, 10000);
		double[] ratio = {0.01, 0.02, 0.03};
		myTasks.setResourceSharingParameters(ratio, 3, 3);
		myTasks.CreateRandomTaskSet(totalUtil, .9, true);
		myTasks.PrintTaskSet();
		
		Component comp = new Component(myTasks, 4);
		//System.out.printf("utilization %f \n", comp.getTaskSet().getUtilization());
		comp.setPi(5000);
		comp.getMPR().PrintMPR();
		comp.getMPRWithResource().PrintMPR();
		return;
		
		
		//Experiment ex = new Experiment();
		//ex.OverheadUtilExperiment(false, 4.1);
		//ex.AllRandomExperiment(true, 0);
		//Log log = new Log("Integration");
		//RTSystem mysys =  log.ReadSystem(6);
		
		//ex.PiExperiment(true, 1.2);
		//ex.MaxTaskUtilExperiment(true, 0.3);
		//ex.Integration(true, 0);
		
		//sys.IntegrateMPROnMinProcessors("CP");
		
		/*
		RTSystem mysys = new RTSystem();
		int totalSysUtil = 6;
		double maxTaskSetUtil = .9;
		double minComponentUtil = 1.5;
		double maxCompUtil = 3.0;
		mysys.createRandomSystem(totalSysUtil, maxTaskSetUtil, minComponentUtil, maxCompUtil, 100, 100);
		//mysys.createSampleSystem(2);
		//mysys.PrintComponents();
		//mysys.PrintTotalMPRUtilization();
		//System.out.print(mysys.getEPRUtilization());
		//mysys.IntegrateMPROnMProcessors(8);
		int mprCP = mysys.IntegrateMPROnMinProcessors("CP");
		int mprBL = mysys.IntegrateMPROnMinProcessors("BL");
		System.out.printf("mprCP: %d, mprBL:%d\n",  mprCP, mprBL);
		//mysys.IntegrateEPROnMProcessors(8);
		//mysys.IntegrateEPROnMinProcessors();
		//mysys.PrintProcessors();
		//mysys.PrintTotalEPRUtilization();
		
		
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
		
		System.out.printf("eprFFFF: %d,    eprWFFF:%d,   eprBFFF:%d,   eprFFWF: %d,   eprWFWF:%d,   eprBFWF:%d  \n",  eprFFFF, eprWFFF, eprBFFF, eprFFWF, eprWFWF, eprBFWF);
		System.out.printf("parFFFF: %.2f, parWFFF:%.2f, parBFFF:%.2f, parFFWF: %.2f, parWFWF:%.2f, parBFWF:%.2f  \n",  parFFFF, parWFFF, parBFFF, parFFWF, parWFWF, parBFWF);
		//System.out.print(mysys.GetAverageGlobalEPRParallelism());
		//mysys.PrintProcessors();
		
		System.out.print("\n end");*/
		
		//int cnt = mysys.IntegrateMPROnMinProcessors();
		//System.out.printf("\n processors needed: %d \n", cnt);
		
		/*
		Plot p = new Plot("test");
		p.pack();
        RefineryUtilities.centerFrameOnScreen(p);
        p.setVisible(true);
        */
	}

}
