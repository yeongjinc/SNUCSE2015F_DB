package dataobject;

import dataobject.CustomException.CSATRangeException;
import dataobject.CustomException.HighschoolRangeException;

public class Student
{
	private static final int MAX_SAT = 400;
	private static final int MAX_HIGH = 100;
	
	// Data Object에서 범위 외 값을 체크함
	
	private int ID;
	private String name;
	private int SATScore;
	private int highschoolScore;
	public int getID()
	{
		return ID;
	}
	public void setID(int ID)
	{
		this.ID = ID;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getSATScore()
	{
		return SATScore;
	}
	public void setSATScore(int SATScore) throws CSATRangeException
	{
		if(SATScore < 0 || SATScore > MAX_SAT)
			throw new CSATRangeException();
		this.SATScore = SATScore;
	}
	public int getHighschoolScore()
	{
		return highschoolScore;
	}
	public void setHighschoolScore(int highschoolScore) throws HighschoolRangeException
	{
		if(highschoolScore < 0 || highschoolScore > MAX_HIGH)
			throw new HighschoolRangeException();
		this.highschoolScore = highschoolScore;
	}
	

}
