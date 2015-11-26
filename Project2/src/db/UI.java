package db;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import dataobject.CustomException.DBException;
import dataobject.Student;
import dataobject.University;

public class UI
{
	public static void main(String args[])
	{
		// Initialize
		// DB.getInstance().createDB();
		
		// Input Loop
		run();
		
		// Close
		DB.getInstance().close();
	}
	
	public static void run()
	{
		Scanner input = new Scanner(System.in);
		PrintStream output = System.out;
		
		while(true)
		{
			output.print(printMenu());
			
			String menuStr = input.nextLine();
			int menu;
			
			try
			{
				menu = Integer.parseInt(menuStr);
			}
			catch(NumberFormatException e)
			{
				// 숫자 외의 값 입력
				output.print(Constant.MENU_ERROR);
				continue;
			}
			
			// 메뉴 범위 벗어난 값 입력
			if(menu < 1 || menu > Constant.MENUS.length)
			{
				output.print(Constant.MENU_ERROR);
				continue;
			}
			
			
			try
			{
				// continue, break의 사용을 위해 switch 대신 if를 사용함
				menu--;
				if(menu == 0) // print univ
				{
					String ret = Constant.SINGLE_LINE + "\n";
					ret += String.format("%-20s %-20s %-20s %-20s %-20s %-20s\n" ,
										"id",
										"name",
										"capacity",
										"group",
										"weight",
										"applied");
					ret += Constant.SINGLE_LINE + "\n";
					
					ArrayList<University> arr = DB.getInstance().getAllUniversities();
					for(University u : arr)
					{
						ret += String.format("%-20s %-20s %-20s %-20s %-20s %-20s\n", 
											u.getID(),
											u.getName(),
											u.getCapacity(),
											u.getGroup(),
											u.getWeight(),
											u.getApplied());
					}
					ret += Constant.SINGLE_LINE + "\n";
					
					output.print(ret);
				}
				else if(menu == 1) // print stud
				{
					String ret = Constant.SINGLE_LINE + "\n";
					ret += String.format("%-20s %-20s %-20s %-20s\n",
										"id",
										"name",
										"csat_score",
										"school_score");
					ret += Constant.SINGLE_LINE + "\n";
					
					ArrayList<Student> arr = DB.getInstance().getAllStudents();
					for(Student s : arr)
					{
						ret += String.format("%-20s %-20s %-20s %-20s\n",
											s.getID(),
											s.getName(),
											s.getSATScore(),
											s.getHighschoolScore()); 
					}
					ret += Constant.SINGLE_LINE + "\n";
					
					output.print(ret);
				}
				else if(menu == 2) // insert univ
				{
					// Constant.U1 등 : University name등을 묻는 프롬프트
					output.print(Constant.U1);
					String name = input.nextLine();
					output.print(Constant.U2);
					int capacity = Integer.parseInt(input.nextLine());
					output.print(Constant.U3);
					String group = input.nextLine();
					output.print(Constant.U4);
					double weight = Double.parseDouble(input.nextLine());
					
					University u = new University();
					u.setName(name);
					u.setCapacity(capacity);
					u.setGroup(group);
					u.setWeight(weight);
					
					boolean ret = DB.getInstance().insertUniversity(u);
					
					if(ret)
						output.println(Constant.INSERT_UNIV_SUCC);
				}
				else if(menu == 3) // remove univ
				{
					output.print(Constant.A2);
					int id = Integer.parseInt(input.nextLine());
					
					boolean ret = DB.getInstance().removeUniversity(id);
					
					if(ret)
						output.println(Constant.DELETE_UNIV_SUCC);
				}
				else if(menu == 4) // insert stud
				{
					output.print(Constant.S1);
					String name = input.nextLine();
					output.print(Constant.S2);
					int sat = Integer.parseInt(input.nextLine());
					output.print(Constant.S3);
					int high = Integer.parseInt(input.nextLine());
					
					Student s = new Student();
					s.setName(name);
					s.setSATScore(sat);
					s.setHighschoolScore(high);
					
					boolean ret = DB.getInstance().insertStudent(s);
					
					if(ret)
						output.println(Constant.INSERT_STUD_SUCC);
				}
				else if(menu == 5) // remove stud
				{
					output.print(Constant.A1);
					int id = Integer.parseInt(input.nextLine());
					
					boolean ret = DB.getInstance().removeStudent(id);
					
					if(ret)
						output.println(Constant.DELETE_STUD_SUCC);
				}
				else if(menu == 6) // make appl
				{
					output.print(Constant.A1);
					int studID = Integer.parseInt(input.nextLine());
					output.print(Constant.A2);
					int univID = Integer.parseInt(input.nextLine());
					
					boolean ret = DB.getInstance().makeApplication(studID, univID);
					
					if(ret)
						output.println(Constant.APPL_SUCC);
				}
				else if(menu == 7) // print stud by univ
				{
					output.print(Constant.A2);
					int univID = Integer.parseInt(input.nextLine());
					
					String ret = Constant.SINGLE_LINE + "\n";
					ret += String.format("%-20s %-20s %-20s %-20s\n",
										"id",
										"name",
										"csat_score",
										"school_score");
					ret += Constant.SINGLE_LINE + "\n";
					
					ArrayList<Student> arr = DB.getInstance().getAppliedStudent(univID);
					for(Student s : arr)
					{
						ret += String.format("%-20s %-20s %-20s %-20s\n",
											s.getID(),
											s.getName(),
											s.getSATScore(),
											s.getHighschoolScore()); 
					}
					ret += Constant.SINGLE_LINE + "\n";
					
					output.print(ret);
				}
				else if(menu == 8) // print univ by stud
				{
					output.print(Constant.A1);
					int studID = Integer.parseInt(input.nextLine());
					
					String ret = Constant.SINGLE_LINE + "\n";
					ret += String.format("%-20s %-20s %-20s %-20s %-20s %-20s\n" ,
										"id",
										"name",
										"capacity",
										"group",
										"weight",
										"applied");
					ret += Constant.SINGLE_LINE + "\n";
					
					ArrayList<University> arr = DB.getInstance().getAppliedUniversity(studID);
					for(University u : arr)
					{
						ret += String.format("%-20s %-20s %-20s %-20s %-20s %-20s\n", 
											u.getID(),
											u.getName(),
											u.getCapacity(),
											u.getGroup(),
											u.getWeight(),
											u.getApplied());
					}
					ret += Constant.SINGLE_LINE + "\n";
					
					output.print(ret);
				}
				else if(menu == 9) // expected of univ
				{
					output.print(Constant.A2);
					int univID = Integer.parseInt(input.nextLine());
					
					String ret = Constant.SINGLE_LINE + "\n";
					ret += String.format("%-20s %-20s %-20s %-20s\n",
										"id",
										"name",
										"csat_score",
										"school_score");
					ret += Constant.SINGLE_LINE + "\n";
					
					ArrayList<Student> arr = DB.getInstance().getExpectedStudent(univID);
					for(Student s : arr)
					{
						ret += String.format("%-20s %-20s %-20s %-20s\n",
											s.getID(),
											s.getName(),
											s.getSATScore(),
											s.getHighschoolScore()); 
					}
					ret += Constant.SINGLE_LINE + "\n";
					
					output.print(ret);
				}
				else if(menu == 10) // expected of stud
				{
					output.print(Constant.A1);
					int studID = Integer.parseInt(input.nextLine());
					
					String ret = Constant.SINGLE_LINE + "\n";
					ret += String.format("%-20s %-20s %-20s %-20s %-20s %-20s\n" ,
										"id",
										"name",
										"capacity",
										"group",
										"weight",
										"applied");
					ret += Constant.SINGLE_LINE + "\n";
					
					ArrayList<University> arr = DB.getInstance().getExpectedUniversity(studID);
					for(University u : arr)
					{
						ret += String.format("%-20s %-20s %-20s %-20s %-20s %-20s\n", 
											u.getID(),
											u.getName(),
											u.getCapacity(),
											u.getGroup(),
											u.getWeight(),
											u.getApplied());
					}
					ret += Constant.SINGLE_LINE + "\n";
					
					output.print(ret);
				}
				else	// exit
				{
					break;
				}
			}
			catch(DBException e)	// input 값 잘못된 에러 등의 예외
			{
				output.println(e.getErrorMessage());
			}
			catch(NumberFormatException e)
			{
				output.println(Constant.FORMAT_ERROR);
			}
		}
		
		input.close();
	}
	
	
	public static String printMenu()
	{
		// StringBuilder를 쓰는 게 효율적이나 그냥 이렇게
		String ret = Constant.DOUBLE_LINE + "\n";
		for(int i=0; i<Constant.MENUS.length; i++)
		{
			ret += (i+1) + ". " + Constant.MENUS[i] + "\n";
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
