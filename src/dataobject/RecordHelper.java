package dataobject;

import java.util.ArrayList;

import dataobject.Common.SelectColumnResolveException;
import dataobject.Common.WhereAmbiguousReferenceException;
import dataobject.Common.WhereColumnNotExistException;
import dataobject.Common.WhereIncomparableException;
import dataobject.Common.WhereTableNotSpecifiedException;
import dataobject.Condition._Condition;
import dataobject.Record.SelectedColumn;
import dataobject.Record.Field;

public class RecordHelper
{
	public static ArrayList<Record> join(ArrayList<Record> a1, ArrayList<Record> a2)
	{
		ArrayList<Record> ret = new ArrayList<Record>();
		
		for(Record i1 : a1)
		{
			for(Record i2 : a2)
			{
				Record r = new Record(0);
				for(Field f : i1.fieldList)
					r.addField(f);
				for(Field f : i2.fieldList)
					r.addField(f);
				
				ret.add(r);
			}
		}
		
		return ret;
	}
	
	public static ArrayList<Record> where(ArrayList<Record> a, _Condition c) throws WhereIncomparableException,
																					WhereTableNotSpecifiedException, 
																					WhereColumnNotExistException, 
																					WhereAmbiguousReferenceException
	{
		if(c == null)
			return a;
		
		ArrayList<Record> ret = new ArrayList<Record>();
		
		for(Record i : a)
		{
			if(c.test(i))
			{
				Record r = new Record(i.recordID);
				for(Field f : i.fieldList)
					r.addField(f);
				
				ret.add(r);
			}
		}
		
		return ret;
	}
	
	public static ArrayList<Record> project(ArrayList<Record> a, ArrayList<SelectedColumn> scList) throws SelectColumnResolveException
	{
		// * 로 select 했을 때, 그냥 그대로 준다
		if(scList == null)
			return a;
		
		ArrayList<Record> ret = new ArrayList<Record>();
		
		for(Record i : a)
		{
			Record r = i.project(scList);
			ret.add(r);
		}
		
		return ret;
	}
	
	public static String print(ArrayList<SelectedColumn> scList, ArrayList<Record> recList) 
			throws WhereAmbiguousReferenceException,
					WhereColumnNotExistException,
					WhereAmbiguousReferenceException,
					WhereTableNotSpecifiedException
	{
		String ret = "";
		
		boolean isFirst = true;
		ret += "| ";
		for(SelectedColumn sc : scList)
		{
			if( ! isFirst)
				ret += "| ";
			isFirst = false;
			
			if(sc.colAlias != null)
				ret += String.format("%-15s", sc.colAlias.toUpperCase());
			else if(sc.alias != null)
				ret += String.format("%-15s", (sc.alias + "." + sc.colName).toUpperCase());
			else
				ret += String.format("%-15s", sc.colName.toUpperCase());
		}
		ret += "|";
		
		ret += "\n---------------------------------";
		
		for(Record rec : recList)
		{
			ret += "\n";
			isFirst = true;

			ret += "| ";
			for(SelectedColumn sc : scList)
			{
				if( ! isFirst)
					ret += "| ";
				isFirst = false;
				
				Field field = rec.getField(sc.alias, sc.colName);
				ret += String.format("%-15s", field.getPrintString());
			}
			ret += "|";
		}
		
		return ret;
	}
}
