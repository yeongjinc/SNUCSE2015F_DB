package dataobject;

import java.util.ArrayList;

import dataobject.Column.PRIMITIVE_TYPE;
import dataobject.Common.WhereAmbiguousReferenceException;
import dataobject.Common.WhereColumnNotExistException;
import dataobject.Common.WhereIncomparableException;
import dataobject.Common.WhereTableNotSpecifiedException;
import dataobject.Record.CharValue;
import dataobject.Record.DateValue;
import dataobject.Record.Field;
import dataobject.Record.IntValue;
import dataobject.Record.Value;

public class Condition
{
	public abstract static class _Condition
	{
		public boolean isNot;
		
		public abstract boolean testInternal(Record record) throws WhereIncomparableException, 
																WhereTableNotSpecifiedException, 
																WhereColumnNotExistException, 
																WhereAmbiguousReferenceException;
		public boolean test(Record record) throws WhereIncomparableException, 
												WhereTableNotSpecifiedException, 
												WhereColumnNotExistException, 
												WhereAmbiguousReferenceException
		{
			boolean result = testInternal(record);
			if(isNot)
				return ! result;
			else
				return result;
		}
	}
	
	public static class _OrCondition extends _Condition
	{
		public ArrayList<_Condition> orList = new ArrayList<_Condition>();
		
		public boolean testInternal(Record record) throws WhereIncomparableException,
														WhereTableNotSpecifiedException, 
														WhereColumnNotExistException, 
														WhereAmbiguousReferenceException
		{
			for(_Condition c : orList)
				if(c.test(record))
					return true;
			
			return false;
		}
	}
	
	public static class _AndCondition extends _Condition
	{
		public ArrayList<_Condition> andList = new ArrayList<_Condition>();
		
		public boolean testInternal(Record record) throws WhereIncomparableException,
														WhereTableNotSpecifiedException, 
														WhereColumnNotExistException, 
														WhereAmbiguousReferenceException
		{
			for(_Condition c : andList)
				if( ! c.test(record))
					return false;
			
			return true;
		}
	}
	
	public static class _Operand
	{
		
	}
	
	public static class _ColumnOperand extends _Operand
	{
		public String tableName = null;	// null if not exists
		public String colName;
	}
	
	public static class _ConstantOperand extends _Operand
	{
		public PRIMITIVE_TYPE pType; 	// 역시 char의 length는 필요 없음
		public String sValue = null; 	// union 같은 것... 여기서까지 클래스 분리할 필요 없다고 판단
		public int iValue = 0;
		
		// 파싱은 where의 operand와, insert할 값과 같이 하기 때문
		// insert할 때는 null도 value임
		public Value convertToValue()
		{
			if(pType == PRIMITIVE_TYPE.INT)
				return new IntValue(iValue);
			else if(pType == PRIMITIVE_TYPE.CHAR)
				return new CharValue(sValue);
			else
				return new DateValue(sValue);
		}
	}
	
	public static class _NullCondition extends _Condition
	{
		public _Operand operand;
		public boolean isNull; 		// if false, 'is not null'
		
		public _NullCondition(_Operand operand, boolean isNull)
		{
			this.operand = operand;
			this.isNull = isNull;
		}
		
		public boolean testInternal(Record record) throws WhereIncomparableException, 
														WhereTableNotSpecifiedException, 
														WhereColumnNotExistException, 
														WhereAmbiguousReferenceException
		{
			if(operand instanceof _ColumnOperand)
			{
				_ColumnOperand co = (_ColumnOperand)operand;
				Field f = record.getField(co.tableName, co.colName);
				if(isNull == f.isNull())
					return true;
				return false;
			}
			else
				throw new WhereIncomparableException();
		}
	}
	
	public static class _ComparisonCondition extends _Condition
	{
		public _Operand leftOperand, rightOperand;
		public String operator;
		
		public _ComparisonCondition(_Operand left, String operator, _Operand right)
		{
			this.leftOperand = left;
			this.operator = operator;
			this.rightOperand = right;
		}
		
		public boolean testInternal(Record record) throws WhereIncomparableException, 
														WhereTableNotSpecifiedException, 
														WhereColumnNotExistException, 
														WhereAmbiguousReferenceException
		{
			// 구현 편의를 위해 Column도 Constant 로 바꿔서 처리
			// NULL비교는 무조건 false
			
			_ConstantOperand l, r;
			if(leftOperand instanceof _ColumnOperand)
			{
				_ColumnOperand co = (_ColumnOperand)leftOperand;
				Field field = record.getField(co.tableName, co.colName);
				if(field.isNull())
					return false;
				
				l = new _ConstantOperand();
				l.pType = field.pType;
				Value value = field.value;
				if(value instanceof CharValue)
					l.sValue = ((CharValue)value).val;
				else if(value instanceof DateValue)
					l.sValue = ((DateValue)value).val;
				else if(value instanceof IntValue)
					l.iValue = ((IntValue)value).val;
			}
			else	// ConstantOperand
			{
				l = (_ConstantOperand)leftOperand;
			}
			
			
			if(rightOperand instanceof _ColumnOperand)
			{
				_ColumnOperand co = (_ColumnOperand)rightOperand;
				Field field = record.getField(co.tableName, co.colName);
				if(field.isNull())
					return false;
				
				r = new _ConstantOperand();
				r.pType = field.pType;
				Value value = field.value;
				if(value instanceof CharValue)
					r.sValue = ((CharValue)value).val;
				else if(value instanceof DateValue)
					r.sValue = ((DateValue)value).val;
				else if(value instanceof IntValue)
					r.iValue = ((IntValue)value).val;
			}
			else	// ConstantOperand
			{
				r = (_ConstantOperand)rightOperand;
			}
			
			
			if(l.pType != r.pType)
				throw new WhereIncomparableException();
			
			if(l.pType == PRIMITIVE_TYPE.INT)
			{
				int lv = l.iValue;
				int rv = r.iValue;
				
				if(operator.equals(">"))
					return lv > rv;
				else if(operator.equals("<"))
					return lv < rv;
				else if(operator.equals("="))
					return lv == rv;
				else if(operator.equals("!="))
					return lv != rv;
				else if(operator.equals(">="))
					return lv >= rv;
				else if(operator.equals("<="))
					return lv <= rv;
			}
			else	// char or string
			{
				String lv = l.sValue;
				String rv = r.sValue;
				
				if(operator.equals(">"))
					return lv.compareTo(rv) > 0;
				else if(operator.equals("<"))
					return lv.compareTo(rv) < 0;
				else if(operator.equals("="))
					return lv.compareTo(rv) == 0;
				else if(operator.equals("!="))
					return lv.compareTo(rv) != 0;
				else if(operator.equals(">="))
					return lv.compareTo(rv) >= 0;
				else if(operator.equals("<="))
					return lv.compareTo(rv) <= 0;
			}
				
			return false;
		}
	}
}
