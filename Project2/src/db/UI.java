package db;

import java.util.ArrayList;

import dataobject.Student;
import dataobject.University;

public class UI
{
	public static void main(String args[])
	{
		//Initialize
		
		//Input Loop
		run();
		
		//Close, if needed
	}
	
	public static void run()
	{
		while(true)
		{
			printMenu();
			
		}
	}
	
	
	public static String printMenu()
	{
		// StringBuilder를 쓰는 게 효율적이나 그냥 이렇게
		String ret = Constant.DOUBLE_LINE + "\n";
		for(int i=0; i<Constant.MENUS.length; i++)
		{
			ret += i + ". " + Constant.MENUS[i] + "\n";
		}
		ret += Constant.DOUBLE_LINE + "\n";
		ret += Constant.SELECT_ACTION;
		
		return ret;
	}
	
	public static String printStud(ArrayList<Student> arr)
	{
		return "";
	}
	
	public static String printUniv(ArrayList<University> arr)
	{
		return "";
	}
}
