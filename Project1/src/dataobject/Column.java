package dataobject;

import java.io.Serializable;

public class Column implements Serializable
{
	private static final long serialVersionUID = 5186473335423077489L;
	
	public static enum PRIMITIVE_TYPE
	{
		INT,
		CHAR,
		DATE
	};
	
	public static class Type implements Serializable
	{
		private static final long serialVersionUID = -5151653063249484321L;
		
		public PRIMITIVE_TYPE ptype;
		public int length;
		
		public Type(PRIMITIVE_TYPE ptype, int length)
		{
			this.ptype = ptype;
			this.length = length;
		}
		
		public String getTypeString()
		{
			if(ptype == PRIMITIVE_TYPE.CHAR)
				return ptype.toString().toLowerCase() + "(" + length + ")";
			return ptype.toString().toLowerCase();
		}
		
		public boolean checkTypeValid()
		{
			if(ptype == PRIMITIVE_TYPE.CHAR && length < 1)
			{
				return false;
			}
			return true;
		}
	}
	
	private String name = "";
	private Type type;
	private boolean isNotNull = false;
	
	private boolean isPrimaryKey = false;
	private boolean isForeignKey = false;
	
	public Column(String name, Type type, boolean isNotNull)
	{
		this.name = name;
		this.type = type;
		this.isNotNull = isNotNull;
	}
	
	public boolean checkTypeValid()
	{
		return type.checkTypeValid();
	}
	
	public String getTypeString()
	{
		return type.getTypeString();
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getIsNull()
	{
		return isNotNull? "N" : "Y";
	}
	
	public void setIsPrimaryKey(boolean isPrimaryKey)
	{
		this.isPrimaryKey = isPrimaryKey;
		this.isNotNull = true;				// 기본키일 경우 enforce not null
	}
	
	public void setIsForeignKey(boolean isForeignKey)
	{
		this.isForeignKey = isForeignKey;
	}
	
	public String getKeyStatus()
	{
		if(isPrimaryKey && isForeignKey)
			return "PRI/FOR";
		else if(isPrimaryKey)
			return "PRI";
		else if(isForeignKey)
			return "FOR";
		else
			return "";
	}
	
	public boolean isForeignKey()
	{
		return isForeignKey;
	}
	
	public boolean isPrimaryKey()
	{
		return isPrimaryKey;
	}
	
	public boolean isNotNull()
	{
		return isNotNull;
	}
	
	public Type getType()
	{
		return type;
	}
}
