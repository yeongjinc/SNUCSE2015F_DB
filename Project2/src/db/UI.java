package db;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

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
			
			
			// continue, break의 사용을 위해 switch 대신 if를 사용함
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
				
				
			}
			else if(menu == 1) // print stud
			{
				
			}
			else if(menu == 2) // insert univ
			{
				
			}
			else if(menu == 3) // remove univ
			{
				
			}
			else if(menu == 4) // insert stud
			{
				
			}
			else if(menu == 5) // remove stud
			{
				
			}
			else if(menu == 6) // make appl
			{
				
			}
			else if(menu == 7) // print stud by univ
			{
				
			}
			else if(menu == 8) // print univ by stud
			{
				
			}
			else if(menu == 9) // expected of univ
			{
				
			}
			else if(menu == 10) // expected of stud
			{
				
			}
			else	// exit
			{
				break;
			}
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
