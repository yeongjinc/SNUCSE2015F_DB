package dataobject;

import java.util.ArrayList;

public class Common
{	
	public static int indexOfInsensitive(ArrayList<String> arr, String key)
	{
		for(int i=0; i<arr.size(); i++)
		{
			if(arr.get(i).toLowerCase().equals(key.toLowerCase()))
				return i;
		}
		return -1;
	}
	
	public static boolean containsInsensitive(ArrayList<String> arr, String key)
	{
		for(String item : arr)
		{
			if(item.toLowerCase().equals(key.toLowerCase()))
				return true;
		}
		return false;
	}
	
	public static class WhereAmbiguousReferenceException extends Exception
	{
		private static final long serialVersionUID = -4161786610063643937L;	
	}
	
	public static class WhereColumnNotExistException extends Exception
	{
		private static final long serialVersionUID = 6533108818887385894L;
	}
	
	public static class WhereTableNotSpecifiedException extends Exception
	{
		private static final long serialVersionUID = 8444435991577236194L;		
	}
	
	public static class WhereIncomparableException extends Exception
	{
		private static final long serialVersionUID = -6510149934757513795L;
	}
	
	public static class SelectColumnResolveException extends Exception
	{
		private static final long serialVersionUID = 7074128403245683215L;
		public String columnName;
		public SelectColumnResolveException(String colName)
		{
			this.columnName = colName;
		}
	}
	
	public static class SelectTableExistenceException extends Exception
	{
		private static final long serialVersionUID = -3308321380021634968L;
		public String tableName;
		public SelectTableExistenceException(String tableName)
		{
			this.tableName = tableName;
		}
	}
	
	public static class InsertTypeMismatchException extends Exception
	{
		private static final long serialVersionUID = -4028365184691622889L;
	}
}
