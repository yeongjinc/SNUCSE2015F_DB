package dataobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import db.BerkeleyDBHelper;
import db.Constant;

public class Table implements Serializable
{
	private static final long serialVersionUID = 3233935762509823986L;
	
	private String name;
	private ArrayList<Column> columnList;
	private ArrayList<String> primaryKeyList = null;	// 나머지는 생성 시 할당, 기본키는 설정 시 할당 후 중복 체크에 활용
	private ArrayList<FK> foreignKeyList;
	private HashSet<String> referencing;
	
	// record에 대해서만 처리하면 되므로 테이블에 보관할 필요 없을 듯
	//private String alias;	// 저장하는 값이 아님.
	
	// 여러 개의 에러가 있을 경우, JavaCC에서 파싱은 끝까지 하되, 에러 출력은 하나만 하도록 관리
	private boolean isErrorOccured = false;
	
	public static class FK implements Serializable
	{
		private static final long serialVersionUID = 1936961548954302338L;
		
		public String tableName;
		public ArrayList<String> columnNameList;
		public String rTableName;
		public ArrayList<String> rTableColumnNameList;
		
		public FK(String tableName,
					ArrayList<String> columnNameList,
					String rTableName,
					ArrayList<String> rTableColumnNameList)
		{
			this.tableName = tableName;
			this.columnNameList = columnNameList;
			this.rTableName = rTableName;
			this.rTableColumnNameList = rTableColumnNameList;
		}
	}
	
	public Table(String name)
	{
		this.name = name;
		//this.alias = name;
		this.columnList = new ArrayList<Column>();
		this.foreignKeyList = new ArrayList<FK>();
		this.referencing = new HashSet<String>();
	}
	
	public boolean isError()
	{
		return isErrorOccured;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ArrayList<Column> getColumnList()
	{
		return columnList;
	}
	
	/* 
	public void setAlias(String alias)
	{
		this.alias = alias;
	}
	
	public String getAlias()
	{
		return alias;
	}
	*/
	
	public void addReferencing(String name)
	{
		referencing.add(name);
	}
	
	public void removeReferencing(String name)
	{
		referencing.remove(name);
	}
	
	public boolean hasReferencing()
	{
		return referencing.size() > 0;
	}
	
	// 삭제될 때 참조되는 테이블들에서 관계 제거
	// 내가 아니라 참조되는 것들의 수정임
	public void deleteReferencing()
	{
		for(FK fk : foreignKeyList)
		{
			Table referenced = BerkeleyDBHelper.getInstance().getTable(fk.rTableName);
			referenced.removeReferencing(name);
			BerkeleyDBHelper.getInstance().alterTable(referenced);
		}
	}

	// 여러 개의 에러가 있을 경우, JavaCC에서 파싱은 끝까지 하되, 에러 출력은 하나만 하도록 관리
	private void printError(String err)
	{
		if(isErrorOccured)
			return;
		
		isErrorOccured = true;
		BerkeleyDBHelper.getInstance().printError(err);
	}
	
	// name으로 칼럼 찾기, 없으면 null 
	public Column getColumn(String name)
	{
		for(Column c : columnList)
		{
			// Case Insensitive 하므로 소문자화 후 비교 
			if(c.getName().toLowerCase().equals(name.toLowerCase()))
				return c;
		}
		return null;
	}
	
	// 칼럼 추가 시는 중복 검사
	// 키 설정 시는 존재 검사
	public boolean hasColumn(String name)
	{
		if(getColumn(name) == null)
			return false;
		return true;
	}
	
	public boolean addColumn(Column newC)
	{
		boolean ret = true;
		
		if(hasColumn(newC.getName()))
		{
			ret = false;
			printError(db.Constant.DuplicateColumnDefError);
		}
		
		// char length < 1
		if(newC.checkTypeValid() == false)
		{
			ret = false;
			printError(db.Constant.CharLengthError);
		}
		
		// 중복 아닐 때만 삽입
		if(ret == true)
			columnList.add(newC);
		
		return ret;
	}
	
	public boolean setPrimaryKey(ArrayList<String> keyList)
	{
		boolean ret = true;
		if(primaryKeyList != null)
		{
			// 이미 기본키가 있음
			printError(db.Constant.DuplicatePrimaryKeyDefError);
			ret = false;
		}
		else
		{
			// 처리하다가 중간에 중지되어도, 어차피 최종적으로 DB에 들어가지 않기 때문에 상관 없음
			primaryKeyList = new ArrayList<String>();
			for(String key : keyList)
			{
				Column c = getColumn(key);
				if(c == null)
				{
					// 키로 쓸 칼럼이 존재하지 않음
					ret = false;
					printError(db.Constant.NonExistingColumnDefError(key));
					break;
				}
				
				primaryKeyList.add(key);
				if(c.isPrimaryKey())	// (a, a) 식으로 중복된 키라는 의미
				{
					ret = false;
					printError(db.Constant.DuplicateKeyColumnError);
					break;
				}
				c.setIsPrimaryKey(true);
			}
		}
		
		return ret;
	}
	
	public ArrayList<String> getPrimaryKey()
	{
		return primaryKeyList;
	}
	
	public boolean addForeignKey(FK fk)
	{
		// 참조하는 테이블 구하기
		Table referenced = BerkeleyDBHelper.getInstance().getTable(fk.rTableName);
		if(referenced == null)
		{
			printError(Constant.ReferenceTableExistenceError);
			return false;
		}
		
		// 숫자 다를 때에도 ReferenceTypeError
		if(fk.columnNameList.size() != fk.rTableColumnNameList.size())
		{
			printError(Constant.ReferenceTypeError);
			return false;
		}
		
		// 이 테이블 칼럼 테스트 
		// 참조하는 테이블 타입 비교, 기본키 테스트
		for(int i=0; i<fk.columnNameList.size(); i++)
		{
			String cName = fk.columnNameList.get(i);
			String rcName = fk.rTableColumnNameList.get(i);
			Column c = getColumn(cName);
			Column rc = referenced.getColumn(rcName);
			
			if(c == null)	// 이 테이블에 칼럼이 없음
			{
				printError(Constant.NonExistingColumnDefError(cName));
				return false;
			}
			if(rc == null)	// 존재하지 않는 칼럼을 참조
			{
				printError(Constant.ReferenceColumnExistenceError);
				return false;
			}
			
			if( ! rc.isPrimaryKey()) // 참조하는 칼럼이 primary key 가 아님 
			{
				printError(Constant.ReferenceNonPrimaryKeyError);
				return false;
			}
			
			// 현재 정의로는 오브젝트 비교 없이 결과 스트링만 비교해도 type이 같음을 알 수 있음 
			if( ! c.getTypeString().equals(rc.getTypeString()))	// 참조하는 칼럼 타입이 다름
			{
				printError(Constant.ReferenceTypeError);
				return false;
			}
			
			if(c.isForeignKey()) // (a, a) 식으로 중복된 키라는 의미
			{
				printError(Constant.DuplicateKeyColumnError);
				return false;
			}
			c.setIsForeignKey(true);
		}
		
		
		// 같은 FK 중복 테스트
		// 비효율적 - n^3
		for(FK oldFK : foreignKeyList)
		{
			boolean isEqTotal = true;
			for(String newKey : fk.columnNameList)
			{
				boolean isEq = false;
				for(String oldKey : oldFK.columnNameList)	//FIXME contains 사용
				{
					if(newKey.equals(oldKey))
					{
						isEq = true;
						break;
					}
				}
				isEqTotal = isEqTotal && isEq;
			}
			if(isEqTotal)
			{
				printError(Constant.DuplicateForeignKeyError);
				return false;
			}
		}
		
		// FK 삽입
		foreignKeyList.add(fk);
		
		// 참조 테이블의 referencing에 추가
		referenced.addReferencing(name);
		BerkeleyDBHelper.getInstance().alterTable(referenced);
		
		return true;
	}
	
	public ArrayList<FK> getForeignKey()
	{
		return foreignKeyList;
	}
	
	public HashSet<String> getReferencingTable()
	{
		return referencing;
	}
	
	public String desc()
	{
		String ret = "table_name [" + name + "]\n";
		ret += String.format("%-20s %-20s %-20s %-20s\n", "column_name", "type", "null", "key");
		for(Column c : columnList)
		{
			ret += String.format("%-20s %-20s %-20s %-20s\n", c.getName(), c.getTypeString(), c.getIsNull(), c.getKeyStatus());
		}
		return ret.substring(0, ret.length()-2);
	}
}
