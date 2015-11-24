package dataobject;

public class CustomException
{
	// 에러 발생 시 예외 던짐
	
	public static class CapacityException extends Exception
	{
		private static final long serialVersionUID = 7843354708944074392L;	
	}
	
	public static class GroupException extends Exception
	{
		private static final long serialVersionUID = -2462050980638128574L;	
	}
	
	public static class HighschoolWeightException extends Exception
	{
		private static final long serialVersionUID = -2599574730155198141L;
	}
	
	public static class CSATRangeException extends Exception
	{
		private static final long serialVersionUID = -6898481644645494596L;
	}
	
	public static class HighschoolRangeException extends Exception
	{
		private static final long serialVersionUID = -5638791430058691609L;
	}
	
	public static class NoUniversityException extends Exception
	{
		private static final long serialVersionUID = 5084929144037719805L;
		private int univID;
		public NoUniversityException(int univID)
		{
			this.univID = univID;
		}
		public int getUnivID()
		{
			return univID;
		}
	}
	
	public static class NoStudentException extends Exception
	{
		private static final long serialVersionUID = 5721020054623432951L;
		private int studID;
		public NoStudentException(int studID)
		{
			this.studID = studID;
		}
		public int getStudID()
		{
			return studID;
		}
	}
	
}
