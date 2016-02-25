import java.util.ArrayList;
import java.util.List;


public class Processor {

	public Processor() 
	{
		subcomponentList = new ArrayList<Double>();
	}
	private List<Double> subcomponentList;
	public void AddSubcomp(double size)
	{
		this.subcomponentList.add(size);
	}
	public double getUtilization()
	{
		double util = 0;
		for(int i=0;i<subcomponentList.size();i++)
		{
			double tmp;
			tmp = this.subcomponentList.get(i);
			util += tmp;
		}
		return util;
	}
	public double getSlack()
	{
		return 1 - this.getUtilization();
	}
	public void PrintProcessor()
	{
		for(int i=0;i<subcomponentList.size();i++)
		{
			double tmp = subcomponentList.get(i);
			System.out.printf("%.2f, ", tmp);	
		}
		System.out.printf(" \n");
	}
	public Processor Copy()
	{
		Processor newProc = new Processor();
		for(int i=0;i<subcomponentList.size();i++)
		{
			double tmpSize = subcomponentList.get(i);
			newProc.AddSubcomp(tmpSize);	
		}
		return newProc;
	}
	public void Copy(Processor from, Processor to)
	{
		to = from;
		return;
	}
}
