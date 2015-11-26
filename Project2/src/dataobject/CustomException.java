package dataobject;

import db.Constant;

public class CustomException
{
	// 에러 발생 시 예외 던짐
	public abstract static class DBException extends Exception
	{
		private static final long serialVersionUID = 6698501866138081646L;
		public abstract String getErrorMessage();
	}
	
	public static class CapacityException extends DBException
	{
		private static final long serialVersionUID = 7843354708944074392L;
		public String getErrorMessage()
		{
			return Constant.CAPACITY_ERROR;
		}
	}
	
	public static class GroupException extends DBException
	{
		private static final long serialVersionUID = -2462050980638128574L;	
		public String getErrorMessage()
		{
			return Constant.GROUP_ERROR;
		}
	}
	
	public static class OverApplyException extends DBException
	{
		private static final long serialVersionUID = 4784345355547469461L;
		public String getErrorMessage()
		{
			return Constant.APPL_FAIL;
		}
	}
	
	public static class HighschoolWeightException extends DBException
	{
		private static final long serialVersionUID = -2599574730155198141L;
		public String getErrorMessage()
		{
			return Constant.WEIGHT_ERROR;
		}
	}
	
	public static class CSATRangeException extends DBException
	{
		private static final long serialVersionUID = -6898481644645494596L;
		public String getErrorMessage()
		{
			return Constant.SAT_ERROR;
		}
	}
	
	public static class HighschoolRangeException extends DBException
	{
		private static final long serialVersionUID = -5638791430058691609L;
		public String getErrorMessage()
		{
			return Constant.HIGH_ERROR;
		}
	}
	
	public static class NoUniversityException extends DBException
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
		public String getErrorMessage()
		{
			return Constant.UNIV_ID_ERROR(univID);
		}
	}
	
	public static class NoStudentException extends DBException
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
		public String getErrorMessage()
		{
			return Constant.STUD_ID_ERROR(studID);
		}
	}
	
}
