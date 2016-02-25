import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.management.relation.RelationTypeSupport;



public class Log {

	private Path logPath;
	private Path statusLogPath;
	private Path taskSetLogPath;
	private String sep = ", ";
	public Log(String inFilename) {
		logPath = Paths.get("C:\\log\\"+inFilename+".csv");
		//logPath = Paths.get("C:\\Users\\nmd01\\Dropbox\\My Papers\\Paper - 15.1 - RTS 2014\\Code\\JavaProgram\\log\\"+inFilename+".csv");
		taskSetLogPath = Paths.get("C:\\log\\"+inFilename+"Tasks.csv");
		statusLogPath = Paths.get("C:\\log\\status.txt");
		File file = new File("C:\\log");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
	}
	private void write2File(Path path, String inStrg)
	{
		try
		{
		    FileWriter writer = new FileWriter(path.toString(), true);
		    writer.append(inStrg);
		    //writer.append('\n'); 
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	private void deleteFile(Path path)
	{
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
	}
	private String convertTaskSet2String(TaskSet taskSet)
	{
		String out = "";
		out += taskSet.getIndex() + sep;
		for(int i=0;i<taskSet.getSize();i++)
		{
			Task task = taskSet.getTask(i);
			out += "\n" + task.index + sep;
			out += task.executionTime + sep;
			out += task.period + sep;
		}
		return out + "\n";
	}
	private void logTaskSet(TaskSet taskSet, Path path)
	{
		String tmp = convertTaskSet2String(taskSet);
		write2File(path, tmp);
	}
	private void logComponent(Component comp, Path path)
	{
		logTaskSet(comp.getTaskSet(), path);
	}
	public void LogSystem(RTSystem sys)
	{
		Path path = Paths.get("C:\\log\\Systems\\"+sys.getIndex()+".csv");
		for(int i=0;i<sys.getComponentes().size();i++)
		{
			logComponent(sys.getComponentes().get(i), path);
		}
	}
	private void logStatus(long indx, double util)
	{
		String tmp = indx + sep + String.format("%.2f \n", util) ;
		write2File(statusLogPath, tmp);
		//System.out.print(tmp);
	}
	public void WriteLog(TaskSet taskSet, String... args)
	{
		logTaskSet(taskSet, taskSetLogPath);
		logStatus(taskSet.getIndex(), taskSet.getUtilization());
		
		String finalStrg= taskSet.getIndex() + sep;
		 for( int i = 0; i < args.length; i++)
		 {
			 finalStrg += args[i] + sep;
		 }
		 write2File(logPath, finalStrg + "\n");
	}
	public void WriteLog(RTSystem system, String... args)
	{
		
		String finalStrg = system.getIndex() + sep;
		 for( int i = 0; i < args.length; i++)
		 {
			 finalStrg += args[i] + sep;
		 }
		 write2File(logPath, finalStrg+ "\n");
	}
	
	public void DeleteFiles()
	{
		deleteFile(logPath);
		deleteFile(taskSetLogPath);
		deleteFile(statusLogPath);
	}
	public ArrayList<Double[]> ReadFile(Path path)
	{
		String filePath = path.toString();
		ArrayList < Double[] > result = new ArrayList < Double[] > ();
		try
		{
			Scanner scan = new Scanner(new File(filePath));
			while (scan.hasNextLine()) 
		    {
		        String line = scan.nextLine();
		        result.add(convertLineToArray(line));
	        }
			scan.close();
		}
		catch (IOException x) 
		{
		    // File permission problems are caught here.
		    System.err.println(x);
		}
	  
	        return result;
	}
	private Double[] convertLineToArray(String line)
	{
		String[] lineArray = line.split(", ");
        Double[] result = new Double[lineArray.length];
        for(int i=0;i<lineArray.length;i++)
        {
        	result[i] = Double.valueOf(lineArray[i]);
        }
        return result;
	}
	public void PrintList(ArrayList<Double[]> inList)
	{
		for(int i=0;i<inList.size();i++)
		{
			Double[] tmp = inList.get(i);
			int length = tmp.length;
			for(int j=0;j<length;j++)
			{
				System.out.printf("%.2f, ",  tmp[j]);
			}
			System.out.printf("\n");
		}
	}
	public RTSystem ReadSystem(long id)
	{
		Path path = Paths.get("C:\\log\\Systems\\"+id+".csv");
		RTSystem sys = new RTSystem();
		ArrayList<Double[]> file = this.ReadFile(path);
		int i = 0;
		while(i<file.size())
		{
			Component comp = new Component();
			comp.setID((int)(double)file.get(i)[0]);
			i++;
			TaskSet taskSet = new TaskSet();
			while(i<file.size() && (int)(double)file.get(i).length > 1) //line is a task
			{
				Task task = new Task();
				task.index = (int)(double)file.get(i)[0];
				task.executionTime = (int)(double)file.get(i)[1];
				task.period = (int)(double)file.get(i)[2];
				task.deadline = task.period;
				taskSet.AddTask(task);
				i++;
			}
			comp.setTaskSet(taskSet);
			comp.setPi(taskSet.getMinPeriods()/2);
			sys.AddComponent(comp);
		}
		
		return sys;
	}

}
