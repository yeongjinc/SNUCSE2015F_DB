package dataobject;

import java.io.Serializable;
import java.util.ArrayList;

import dataobject.Column.PRIMITIVE_TYPE;
import dataobject.Common.InsertTypeMismatchException;
import dataobject.Common.SelectColumnResolveException;
import dataobject.Common.WhereAmbiguousReferenceException;
import dataobject.Common.WhereColumnNotExistException;
import dataobject.Common.WhereTableNotSpecifiedException;
import db.BerkeleyDBHelper;
import db.Constant;

public class Record implements Serializable
{
	private static final long serialVersionUID = -5053012490291863864L;

	public int recordID;
	public ArrayList<Field> fieldList = new ArrayList<Field>();
	
	public Record(int recordID)
	{
		this.recordID = recordID;
	}
	
	public static class SelectedColumn
	{
		public String alias;	// Table Alias
		public String colName;
		public String colAlias;
		
		public SelectedColumn(String alias, String colName, String colAlias)
		{
			this.alias = alias;
			this.colName = colName;
			this.colAlias = colAlias;
		}
	}
	
	public static class Field implements Serializable, Cloneable
	{
		private static final long serialVersionUID = -6284189900721859144L;
		public PRIMITIVE_TYPE pType; 	// 데이터에서는 length 를 알 필요 없음. 조건이 잘라서 넣기이므로 insert 시에만 체크하면 됨
		public int length;				// => 생성자에서 처리
		public String alias; 			// 비효율적이지만 조인 시 a.x, b.y 등을 각 레코드별로 처리하기 편하다
		public String colName;
		public Value value;
		
		public Field(PRIMITIVE_TYPE pType, int length, String alias, String colName, Value value) 
				throws InsertTypeMismatchException
		{
			this.pType = pType;
			this.alias = alias;
			this.length = length;
			this.colName = colName;
			this.value = value;
			
			// Null Value때문에 조건 순서 중요함
			if(value instanceof IntValue && pType != PRIMITIVE_TYPE.INT)
				throw new InsertTypeMismatchException();
			else if(value instanceof CharValue && pType != PRIMITIVE_TYPE.CHAR)
				throw new InsertTypeMismatchException();
			else if(value instanceof DateValue && pType != PRIMITIVE_TYPE.DATE)
				throw new InsertTypeMismatchException();
			
			// length만큼 잘라서 넣음
			if(value instanceof CharValue)
			{
				CharValue cv = (CharValue)value;
				if(cv.val.length() < length)
					cv.val = cv.val.substring(0, length);
			}
		}
		
		 public Object clone() throws CloneNotSupportedException {
			  return super.clone();
		 }
		 
		 public String getPrintString()
		 {
			 return value.getPrintString();
		 }
		 
		 public boolean isNull()
		 {
			 return (value instanceof NullValue);
		 }
		 
		 public boolean isEqual(Field other) throws InsertTypeMismatchException
		 {
			 return value.isEqual(other.value);
		 }
	}
	
	public static abstract class Value implements Serializable, Cloneable 
	{
		private static final long serialVersionUID = 325593516056833482L;

		public abstract String getPrintString();
		public abstract boolean isEqual(Value other) throws InsertTypeMismatchException;
	}
	
	public static class NullValue extends Value implements Serializable, Cloneable
	{
		private static final long serialVersionUID = -9096813668010431607L;

		public String getPrintString()
		{
			return "null";
		}
		public boolean isEqual(Value other) throws InsertTypeMismatchException
		{
			if( ! (other instanceof NullValue))
				throw new InsertTypeMismatchException();
			
			return true;
		}
	}
	
	public static class IntValue extends Value implements Serializable, Cloneable
	{
		private static final long serialVersionUID = 8054645757548143612L;
		public Integer val;
		public IntValue(Integer val)
		{
			this.val = val;
		}
		public String getPrintString()
		{
			return val+"";
		}
		public boolean isEqual(Value other) throws InsertTypeMismatchException
		{
			if( ! (other instanceof IntValue))
				throw new InsertTypeMismatchException();
			
			return ((IntValue)other).val == val;
		}
	}
	
	public static class CharValue extends Value implements Serializable, Cloneable
	{
		private static final long serialVersionUID = 6068677009486533697L;
		public String val;
		public CharValue(String val)
		{
			this.val = val;
		}
		public String getPrintString()
		{
			return val;
		}
		public boolean isEqual(Value other) throws InsertTypeMismatchException
		{
			if( ! (other instanceof CharValue))
				throw new InsertTypeMismatchException();
			
			return ((CharValue)other).val.equals(val);
		}
	}
	
	public static class DateValue extends Value implements Serializable, Cloneable
	{
		private static final long serialVersionUID = 5846098361758385568L;
		public String val;
		public DateValue(String val)
		{
			this.val = val;
		}
		public String getPrintString()
		{
			return val;
		}
		public boolean isEqual(Value other) throws InsertTypeMismatchException
		{
			if( ! (other instanceof DateValue))
				throw new InsertTypeMismatchException();
			
			return ((DateValue)other).val.equals(val);
		}
	}
	
	public void setAlias(String alias)
	{
		for(Field f : fieldList)
			f.alias = alias;
	}

	public void addField(Field field)
	{
		fieldList.add(field);
	}
	
	// to select some columns
	public Record project(ArrayList<SelectedColumn> selectedList) throws SelectColumnResolveException
	{
		Record r = new Record(0);
		
		for(SelectedColumn sc : selectedList)
		{
			Field f = null;
			
			for(Field i : fieldList)
			{
				if(
					(sc.alias == null || sc.alias.toLowerCase().equals(i.alias.toLowerCase()))
					&&
					sc.colName.toLowerCase().equals(i.colName.toLowerCase())
					)
				{
					if(f != null)	// 이미 한 번 찾았다는 뜻으로, ambiguous
						throw new SelectColumnResolveException(sc.colName);
					
					try
					{
						f = (Field)i.clone();
					}
					catch(Exception e)
					{
						BerkeleyDBHelper.getInstance().printError("[CUSTOM] Clone Error");
					}
					
				}
			}
			
			if(f == null)	// 해당 칼럼이 없다는 뜻
				throw new SelectColumnResolveException(sc.colName);
			
			if(sc.alias != null)
				f.alias = sc.alias;
			r.addField(f);
		}
		
		return r;
	}
	
	// 외부에서는 tableName(can be null), colName을 모두 받는 함수만 쓰도록 private으로 선언
	private Field getField(String colName) throws WhereAmbiguousReferenceException, WhereColumnNotExistException
	{
		Field field = null;
		for(Field f : fieldList)
		{
			if(f.colName.toLowerCase().equals(colName.toLowerCase()))
			{
				if(field != null)
				{
					throw new WhereAmbiguousReferenceException();
				}
				
				field = f;
			}
		}
		
		if(field == null)
			throw new WhereColumnNotExistException();
		
		return field;
	}
	
	public Field getField(String alias, String colName) throws WhereTableNotSpecifiedException, 
															WhereColumnNotExistException, 
															WhereAmbiguousReferenceException
	{
		if(alias == null)	// table이 Null일 경우 colName만으로 호출, WhereAmbiguousReference 발생 가능
			return getField(colName);
		
		Field field = null;
		boolean isNoTable = true;
		for(Field f : fieldList)
		{
			// 테이블을 잘못 참고한 건 아님
			if(f.alias.toLowerCase().equals(alias.toLowerCase()))
				isNoTable = false;
			
			if(f.colName.toLowerCase().equals(colName.toLowerCase())
				&& f.alias.toLowerCase().equals(alias.toLowerCase()))
			{
				field = f;
			}
		}
		
		if(isNoTable)
			throw new WhereTableNotSpecifiedException();
		
		if(field == null)
			throw new WhereColumnNotExistException();
		
		return field;
	}
	
	public void nullify(Field field)
	{
		try
		{
			int index = fieldList.indexOf(field);
			fieldList.add(index, new Field(field.pType, field.length, field.alias, field.colName, new NullValue()));
			fieldList.remove(field);
		}
		catch(Exception e)	// 이 부분도 발생하면 안 됨. 타입은 무조건 일치.
		{
			BerkeleyDBHelper.getInstance().printError(Constant.DebugNotReached);
		}
	}
}
