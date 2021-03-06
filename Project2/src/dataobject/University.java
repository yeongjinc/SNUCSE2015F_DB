package dataobject;

import dataobject.CustomException.CapacityException;
import dataobject.CustomException.GroupException;
import dataobject.CustomException.HighschoolWeightException;

public class University
{
	// Data Object에서 범위 외 값을 체크함
	
	private static final char GROUP_A = 'A';
	private static final char GROUP_B = 'B';
	private static final char GROUP_C = 'C';
	private static final int MIN_CAPACITY = 1;
	
	private int ID;
	private String name;
	private int capacity;
	private char group;
	private double weight;
	private int applied;
	
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
	public int getCapacity()
	{
		return capacity;
	}
	public void setCapacity(int capacity) throws CapacityException
	{
		if(capacity < MIN_CAPACITY)
			throw new CapacityException();
		this.capacity = capacity;
	}
	public char getGroup()
	{
		return group;
	}
	public void setGroup(String str) throws GroupException
	{
		if(str == null || str.length() == 0 || str.length() > 1)
			throw new GroupException();
		char group = str.charAt(0);
		
		if(group != GROUP_A
			&& group != GROUP_B
			&& group != GROUP_C)
			throw new GroupException();
		this.group = group;
	}
	public double getWeight()
	{
		return weight;
	}
	public void setWeight(double weight) throws HighschoolWeightException
	{
		if(weight < 0.0)
			throw new HighschoolWeightException();
		this.weight = weight;
	}
	public int getApplied()
	{
		return applied;
	}
	public void setApplied(int applied)
	{
		this.applied = applied;
	}
}
