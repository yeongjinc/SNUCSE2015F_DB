package db;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import dataobject.Column;
import dataobject.Common;
import dataobject.Common.InsertTypeMismatchException;
import dataobject.Common.SelectColumnResolveException;
import dataobject.Common.SelectTableExistenceException;
import dataobject.Common.WhereAmbiguousReferenceException;
import dataobject.Common.WhereColumnNotExistException;
import dataobject.Common.WhereIncomparableException;
import dataobject.Common.WhereTableNotSpecifiedException;
import dataobject.Condition._Condition;
import dataobject.ObjectHelper;
import dataobject.Record;
import dataobject.Record.Field;
import dataobject.Record.NullValue;
import dataobject.Record.SelectedColumn;
import dataobject.Record.Value;
import dataobject.RecordHelper;
import dataobject.Table;
import dataobject.Table.FK;

/*
 * 따로 분리한 이유 
 *  1) DB 관련 로직을 파서 관련 로직과 분리하여 모듈화
 *  2) .jj 대신 .java를 씀으로써 Eclipse의 자동완성 기능 사용 가능 
 */
public class BerkeleyDBHelper
{
	// singleton structure
	private static BerkeleyDBHelper _instance;	
	
	private Environment dbEnv = null;
	private Database db = null;
	private PrintStream ps = System.out;
	
	public static final String PREFIX_TABLE = "table_";
	public static final String PREFIX_RECORD = "record_";
	public static final int INITIAL_RECORD_ID = 1;
	public static final String RECORD_ID_KEY = "meta_record_id";
	
	/*
	 * Record는 record_[tableName]_[recordID]  /  [...data...] 형태로 저장됨
	 * recordID = 0, join이나 특정 칼럼 선택 시의 임시 테이블에만 사용되며 실제 저장되는 데이터는 아님
	 * 일반 테이블에서는 1부터 시작하여 늘려감
	 * 
	 */
	
	public static BerkeleyDBHelper getInstance()
	{
		if(_instance == null)
			_instance = new BerkeleyDBHelper();
		return _instance;
	}
	
	private BerkeleyDBHelper()
	{
	}
	
	private String getTableKey(String tableName)
	{
		return PREFIX_TABLE + tableName.toLowerCase();
	}
	
	private String getRecordKey(String tableName, int recordID)
	{
		return PREFIX_RECORD + tableName.toLowerCase() + "_" + recordID;
	}
	
	private String getRecordSearchKey(String tableName)
	{
		return PREFIX_RECORD + tableName.toLowerCase();
	}
	
	// 전역으로 관리되며, 늘어날 때마다 1씩 증가
	private int getRecordID()
	{
		String id = get(RECORD_ID_KEY);
		if(id == null)
		{
			put(RECORD_ID_KEY, (INITIAL_RECORD_ID+1)+"");
			return INITIAL_RECORD_ID;
		}
		
		int intID = Integer.parseInt(id);
		remove(RECORD_ID_KEY);
		put(RECORD_ID_KEY, (intID+1)+"");
		return intID;
	}
	
	public void setPS(PrintStream ps)
	{
		this.ps = ps;
	}
	
	public void printError(String err)
	{
		ps.println(err);
	}
	
	public void printMessage(String msg)
	{
		ps.println(msg);
	}
	
	public void printLine()
	{
		ps.println("-------------------------------------------------");
	}
	
	public void openDB()
	{
		if(dbEnv != null || db != null)
		{
			printError("Try to open again");
		}
		
		// Open DB Env or if not, create one
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		dbEnv = new Environment(new File("db/"), envConfig);
		
		// Open DB or if not, create one
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		db = dbEnv.openDatabase(null, "yeongjinDB", dbConfig);
	}
	
	public void closeDB()
	{
		try
		{
			if(db != null)
			{
				db.close();
				db = null;
			}
			if(dbEnv != null)
			{
				dbEnv.close();
				dbEnv = null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// put wrapper
	// return false if fail
	public boolean put(String key, String data)
	{
		boolean result = false;
		Cursor cursor = db.openCursor(null, null);
		try
		{
			DatabaseEntry deKey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry deData = new DatabaseEntry(data.getBytes("UTF-8"));
			result = (cursor.put(deKey, deData) == OperationStatus.SUCCESS);
			cursor.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cursor.close();
		}
		return result;
	}
	
	// isExist wrapper
	public boolean isExist(String key)
	{
		try
		{
			DatabaseEntry deKey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry deData = new DatabaseEntry();
			if(db.get(null, deKey, deData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
				return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	// get wrapper
	// return null if not exist
	public String get(String key)
	{
		try
		{
			DatabaseEntry deKey = new DatabaseEntry(key.getBytes("UTF-8"));
			DatabaseEntry deData = new DatabaseEntry();
			if(db.get(null, deKey, deData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
				return new String(deData.getData(), "UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	// remove wrapper
	public boolean remove(String key)
	{
		boolean ret = false;
		try
		{
			DatabaseEntry deKey = new DatabaseEntry(key.getBytes("UTF-8"));
			ret = db.delete(null, deKey) == OperationStatus.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public interface DBIterator
	{
		public void iter(String key, String data);
	}
	
	// ret : where iterated at least once 
	public boolean iterate(DBIterator iterator)
	{
		return iterate(iterator, null);
	}
	
	public boolean iterate(DBIterator iterator, String prefix)
	{
		boolean atLeastOnce = false;
		try
		{
			DatabaseEntry deKey = new DatabaseEntry();
			DatabaseEntry deData = new DatabaseEntry();
			
			Cursor cursor = db.openCursor(null, null);
			while(cursor.getNext(deKey, deData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				String key = new String(deKey.getData(), "UTF-8");
				String data = new String(deData.getData(), "UTF-8");
				
				if(prefix != null && ! key.startsWith(prefix))
					continue;
				
				iterator.iter(key, data);
				atLeastOnce = true;
			}
			cursor.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return atLeastOnce;
	}
	
	public void printDebug()
	{
		iterate(new DBIterator()
		{
			@Override
			public void iter(String key, String data)
			{
				System.out.println(key + " : " + data);
			}
		});
	}
	
	public Table getTable(String tableName)
	{
		String key = getTableKey(tableName);
		String data = get(key);
		if(data == null)
			return null;
		return ObjectHelper.<Table>stringToObject(data);
	}
	
	public void createTable(Table table)
	{
		// 이미 파싱 과정에서 칼럼 등 에러가 발생한 경우는 아무것도 하지 않음 (이미 출력함)
		if(table.isError())
			return;
		
		String key = getTableKey(table.getName());
		
		// 해당 테이블이 존재하는 경우
		if(isExist(key))
		{
			printError(Constant.TableExistenceError);
			return;
		}
		
		String data = ObjectHelper.objectToString(table);
		put(key, data);
		printMessage(Constant.CreateTableSuccess(table.getName()));
	}
	
	public void alterTable(Table table)
	{
		// 일단 여기서는 referencing 추가만을 위한 것으로, 앞으로 데이터 다루게 되면 drop에서는 데이터도 날리기 때문에
		// 따로 구현해둠 
		
		String key = getTableKey(table.getName());
		String newData = ObjectHelper.objectToString(table);
		remove(key);
		put(key, newData);
	}
	
	private void dropTableInternal(Table table, boolean isALL)
	{
		// if isALL, do not print drop message, and ignore foreign key constraints
		String key = getTableKey(table.getName());
		if( ! isALL)
		{
			if(table.hasReferencing())
			{
				printMessage(Constant.DropReferencedTableError(table.getName()));
				return;
			}
			truncate(table.getName());	// delete record
			
			table.deleteReferencing();
			remove(key);
			printMessage(Constant.DropSuccess(table.getName()));
		}
		else
		{
			truncate(table.getName());	// delete record
			
			table.deleteReferencing();
			remove(key);
		}
	}
	
	public void dropTable(ArrayList<String> nameList)
	{
		// null means '*'
		if(nameList == null)
		{
			iterate(new DBIterator()
			{
				@Override
				public void iter(String key, String data)
				{
					Table t = ObjectHelper.<Table>stringToObject(data);
					dropTableInternal(t, true);
				}
			}, PREFIX_TABLE);
			
			remove(RECORD_ID_KEY);
			
			printMessage(Constant.DropSuccessAllTables);
		}
		else
		{
			for(String name : nameList)
			{
				String key = getTableKey(name);
				if(isExist(key))
				{
					String data = get(key);
					Table t = ObjectHelper.<Table>stringToObject(data);
					dropTableInternal(t, false);
				}
				else
				{
					printError(Constant.NoSuchTable);
				}
			}
		}
	}
	
	public void desc(ArrayList<String> nameList)
	{
		boolean atLeastOnce = false;
		
		// null means '*'
		if(nameList == null)
		{
			atLeastOnce = iterate(new DBIterator()
			{
				@Override
				public void iter(String key, String data)
				{
					Table t = ObjectHelper.<Table>stringToObject(data);
					printLine();
					printMessage(t.desc());
				}
			}, PREFIX_TABLE);
		}
		else
		{
			// desc의 경우 처음에 모두 체크해야 함
			for(String name : nameList)
			{
				String key = getTableKey(name);
				if( ! isExist(key))
				{
					printError(Constant.NoSuchTable);
					return;
				}
			}
			
			for(String name : nameList)
			{
				String key = getTableKey(name);
				String data = get(key);
				Table t = ObjectHelper.<Table>stringToObject(data);
				printLine();
				printMessage(t.desc());
				atLeastOnce = true;
			}
		}
		
		if(atLeastOnce)
			printLine();
	}
	
	public static class InsertParam
	{
		String tableName;
		ArrayList<String> columnNameList;
		ArrayList<Value> valueList;
	}
	
	// column을 명시하지 않으면 columnNameList = null 
	public void insert(String tableName, 
					ArrayList<String> columnNameList, 
					ArrayList<Value> valueList)
	{
		try
		{
			// 테이블 존재 유무 검증
			Table table = getTable(tableName);
			if(table == null)
			{
				printError(Constant.NoSuchTable);
				return;
			}
	
			// 저장할 레코드 세팅하면서 칼럼 수, 칼럼 타입, null 등의 검증
			Record record = new Record(getRecordID());
			
			// 따로 column을 명시한 경우
			if(columnNameList != null)
			{
				if(columnNameList.size() != valueList.size())
				{
					printError(Constant.InsertSizeMismatch);
					return;
				}
				
				for(String colName : columnNameList)
				{
					if(table.getColumn(colName) == null)
					{
						// 칼럼 존재하지 않음
						printError(Constant.InsertColumnExistenceError(colName));
						return;
					}
				}
				
				for(Column c : table.getColumnList())
				{
					// 테이블명, 칼럼명 대소문자 구분하지 않기 때문에 이렇게 체크 
					int index = Common.indexOfInsensitive(columnNameList, c.getName());
					Field f = null;
					
					if(index == -1) // there is no
					{
						if(c.isNotNull())
						{
							printError(Constant.InsertColumnNonNullableError(c.getName()));
							return;
						}
						
						f = new Field(c.getType().ptype, c.getType().length, tableName, c.getName(), new NullValue());
					}
					else
					{
						Value value = valueList.get(index);
						if(c.isNotNull() && value instanceof NullValue)
						{
							printError(Constant.InsertColumnNonNullableError(c.getName()));
							return;
						}
						f = new Field(c.getType().ptype, c.getType().length, tableName, c.getName(), value);
					}
					
					record.addField(f);
				}
			}
			else	// 따로 칼럼을 명시하지 않은 경우
			{
				ArrayList<Column> columnList = table.getColumnList();
				if(columnList.size() != valueList.size())
				{
					printError(Constant.InsertSizeMismatch);
					return;
				}
				
				for(int i=0; i<columnList.size(); i++)
				{
					Column c = columnList.get(i);
					Value v = valueList.get(i);
					
					if(c.isNotNull() && v instanceof NullValue)
					{
						printError(Constant.InsertColumnNonNullableError(c.getName()));
						return;
					}
					
					Field f = new Field(c.getType().ptype, c.getType().length, tableName, c.getName(), v);
					record.addField(f);
				}
			}
			
			
			// Primary Key의 점검
			ArrayList<String> pkList = table.getPrimaryKey();
			if(pkList != null)
			{
				ArrayList<Record> recList = getRecordList(tableName, null);
				
				for(Record rec : recList)
				{
					boolean isEqual = true;
					for(String pk : pkList)
					{
						Field oldF = rec.getField(null, pk);
						Field newF = record.getField(null, pk);
						
						if( ! oldF.isEqual(newF))
						{
							isEqual = false;
							break;
						}
					}
					
					if(isEqual)
					{
						printError(Constant.InsertDuplicatePrimaryKeyError);
						return;
					}
				}
			}
			
			// Foreign Key의 점검
			ArrayList<FK> fkList = table.getForeignKey();
			for(FK fk : fkList)
			{
				ArrayList<Record> fkRecList = getRecordList(fk.rTableName, null);
				
				boolean hasRef = false;
				for(Record fkRec : fkRecList)
				{
					boolean isEqual = true;
					for(int i=0; i<fk.columnNameList.size(); i++)
					{
						Field refField = fkRec.getField(null, fk.rTableColumnNameList.get(i));
						Field field = record.getField(null, fk.columnNameList.get(i));
						if(field.isNull()) // FK는 null 허용함
							continue;
						// 이 테이블 자체의 not null constraint는 다른 곳에서 체크됨
						
						if( ! refField.isEqual(field))
						{
							isEqual = false;
							break;
						}
					}
					
					if(isEqual)
					{
						hasRef = true;
						break;
					}
				}
				
				if(hasRef == false)
				{
					printError(Constant.InsertReferentialIntegrityError);
					return;
				}
			}
			
			saveRecord(tableName, record);
			printMessage(Constant.InsertResult);
		}
		catch(InsertTypeMismatchException e)
		{
			printError(Constant.InsertTypeMismatchError);
		}
		catch(WhereTableNotSpecifiedException e)	// 여기서는 getField가 반드시 있어야 함.Select 때의 예외들임.
		{
			printError(Constant.DebugNotReached);
		}
		catch(WhereColumnNotExistException e)
		{
			printError(Constant.DebugNotReached);
		}
		catch(WhereAmbiguousReferenceException e)
		{
			printError(Constant.DebugNotReached);
		}
	}
	
	public static class DeleteParam
	{
		String tableName;
		_Condition cond;
	}
	
	public enum DELETE_MODE
	{
		NONE,	// FK 없어서 그냥 삭제
		NULL, 	// FK 걸리는 부분은 null로 (모든 referencing이 nullable일 때)
		CANCEL,	// FK 걸리는 부분은 삭제 못하는 걸로
	}
	public void delete(String tableName, _Condition cond)
	{
		try
		{
			Table table = getTable(tableName);
			if(table == null)
			{
				printError(Constant.NoSuchTable);
				return;
			}
			
			ArrayList<Record> recordList = getRecordList(tableName, null);
			recordList = RecordHelper.where(recordList, cond);
			
			// Foreign Key의 점검
			DELETE_MODE dMode = DELETE_MODE.NONE;
			ArrayList<FK> FKList = new ArrayList<FK>();
			for(String refTableName : table.getReferencingTable())
			{
				Table refTable = getTable(refTableName);
				for(FK fk : refTable.getForeignKey())
				{
					// 이 테이블에 연관된 FK
					if(fk.rTableName.toLowerCase().equals(tableName.toLowerCase()))
					{
						FKList.add(fk);
						
						for(String colName : fk.columnNameList)
						{
							Column c = refTable.getColumn(colName);
							if(c.isNotNull())	// 하나라도 not null 이면 null로 못 만들고, 삭제 자체가 cancel 되어야 함 
							{
								dMode = DELETE_MODE.CANCEL;
							}
						}
					}
				}
			}
			if(FKList.size() > 0 && dMode == DELETE_MODE.NONE)
				dMode = DELETE_MODE.NULL;
			
			int deletedCount = 0;
			int refCount = 0;
			for(Record record : recordList)
			{
				if(dMode == DELETE_MODE.NONE)
				{
					deleteRecord(tableName, record.recordID);
					deletedCount++;
				}
				else if(dMode == DELETE_MODE.NULL)
				{
					for(FK fk : FKList)
					{
						ArrayList<Record> refRecordList = getRecordList(fk.tableName, null);
						// 참조하는 테이블의 모든 레코드에 대해서
						for(Record refRecord : refRecordList)
						{
							// FK 값이 모두 같으면 null 처리
							// 원래 참조되는 값이 모두 삭제될 때가 문제인데, 현재는 FK는 무조건 PK라는 가정이 있으므로
							// 한 레코드가 삭제되면 모든 값이 삭제되는 것으로 볼 수 있다.
							boolean isEqual = true;
							for(int i=0; i<fk.columnNameList.size(); i++)
							{
								Field refField = refRecord.getField(null, fk.columnNameList.get(i));
								Field field = record.getField(null, fk.rTableColumnNameList.get(i));
								
								if( ( ! refField.isNull()) && ( ! refField.isEqual(field)))
								{
									isEqual = false;
									break;
								}
							}
							
							if(isEqual)
							{
								for(String cName : fk.columnNameList)
								{
									Field f = refRecord.getField(null, cName);
									refRecord.nullify(f);
								}
								updateRecord(fk.tableName, refRecord);
							}
						}
					}
					
					deleteRecord(tableName, record.recordID);
					deletedCount++;
				}
				else if(dMode == DELETE_MODE.CANCEL)
				{
					boolean isCancel = false;
					for(FK fk : FKList)
					{
						ArrayList<Record> refRecordList = getRecordList(fk.tableName, null);
						// 참조하는 테이블의 모든 레코드에 대해서
						for(Record refRecord : refRecordList)
						{
							// FK 값이 같은 게 하나라도 있으면 fail 임
							boolean isEqual = true;
							for(int i=0; i<fk.columnNameList.size(); i++)
							{
								Field refField = refRecord.getField(null, fk.columnNameList.get(i));
								Field field = record.getField(null, fk.rTableColumnNameList.get(i));
								
								if( ! refField.isEqual(field))
								{
									isEqual = false;
									break;
								}
							}
							
							if(isEqual)
							{
								isCancel = true;
								break;
							}
						}
						
						if(isCancel)
							break;
					}
					
					if(isCancel)
					{
						refCount++;
					}
					else
					{
						deleteRecord(tableName, record.recordID);
						deletedCount++;
					}
				}
			}
			
			printMessage(Constant.DeleteResult(deletedCount));
			if(refCount > 0)
				printMessage(Constant.DeleteReferentialIntegrityPassed(refCount));
		}
		catch(WhereIncomparableException e)
		{
			printError(Constant.WhereIncomparableError);
		}
		catch(WhereTableNotSpecifiedException e)
		{
			printError(Constant.WhereTableNotSpecified);
		}
		catch(WhereColumnNotExistException e)
		{
			printError(Constant.WhereColumnNotExist);
		}
		catch(WhereAmbiguousReferenceException e)
		{
			printError(Constant.WhereAmbiguousReference);
		}
		catch(InsertTypeMismatchException e) // 여기서는 타입 무조건 일치해야 함(그렇지 않으면 저장조차 되지 않았을 것)
		{
			printError(Constant.DebugNotReached);
		}
	}
	
	private void truncate(String tableName)
	{
		//FK에 대한 점검은 여기서 안함(이미 끝남)
		
		ArrayList<Record> recordList = getRecordList(tableName, null);
		for(Record r : recordList)
			deleteRecord(tableName, r.recordID);
	}

	
	public static class SelectParam
	{
		ArrayList<SelectedColumn> scList;
		ArrayList<TableReference> tableList;
		_Condition cond;
	}
	
	// Select는 as가 들어올 수 있음
	public static class TableReference
	{
		public String tableName;
		public String alias = null;
	}
	public void select(ArrayList<SelectedColumn> scList, 
						ArrayList<TableReference> tableList, 
						_Condition cond)
	{
		try
		{
			ArrayList<Record> universal = null;
			
			for(TableReference tr : tableList)
			{
				String tableKey = getTableKey(tr.tableName);
				if( ! isExist(tableKey))
					throw new SelectTableExistenceException(tr.tableName);
				
				ArrayList<Record> arr = getRecordList(tr.tableName, tr.alias);
				if(universal == null)
					universal = arr;
				else
					universal = RecordHelper.join(universal, arr);
			}
			
			ArrayList<Record> recList = RecordHelper.where(universal, cond);
			recList = RecordHelper.project(recList, scList);
			
			// * 인 경우, project에서는 null을 보내 처리하지 않지만 print 시에는 필요함
			if(scList == null)
			{
				scList = new ArrayList<SelectedColumn>();
				
				// table이 하나인 경우 columnName만, 여러 개인 경우 tableName + colName
				if(tableList.size() == 1)
				{
					Table t = getTable(tableList.get(0).tableName);
					for(Column c : t.getColumnList())
					{
						SelectedColumn sc = new SelectedColumn(null, c.getName(), null);
						scList.add(sc);
					}
				}
				else
				{
					for(TableReference tr : tableList)
					{
						Table t = getTable(tr.tableName);
						for(Column c : t.getColumnList())
						{
							SelectedColumn sc = new SelectedColumn(tr.tableName, c.getName(), null);
							scList.add(sc);
						}
					}
				}
			}
			String printable = RecordHelper.print(scList, recList);
			ps.println(printable);
		}
		catch(SelectColumnResolveException e)
		{
			printError(Constant.SelectColumnResolveError(e.columnName));
		}
		catch(SelectTableExistenceException e)
		{
			printError(Constant.SelectTableExistenceError(e.tableName));
		}
		catch(WhereAmbiguousReferenceException e)
		{
			printError(Constant.WhereAmbiguousReference);
		}
		catch(WhereColumnNotExistException e)
		{
			printError(Constant.WhereColumnNotExist);
		}
		catch(WhereIncomparableException e)
		{
			printError(Constant.WhereIncomparableError);
		}
		catch(WhereTableNotSpecifiedException e)
		{
			printError(Constant.WhereTableNotSpecified);
		}
	}
	
	private ArrayList<Record> getRecordList(final String tableName, final String alias)
	{
		final ArrayList<Record> ret = new ArrayList<Record>();
		
		iterate(new DBIterator()
		{
			@Override
			public void iter(String key, String data)
			{
				Record r = ObjectHelper.<Record>stringToObject(data);
				if(alias != null)
					r.setAlias(alias);
				
				ret.add(r);
			}
		}, getRecordSearchKey(tableName));
		
		return ret;
	}
	
	private void saveRecord(String tableName, Record record)
	{
		String key = getRecordKey(tableName, record.recordID);
		String data = ObjectHelper.objectToString(record);
		put(key, data);
	}
	
	private void deleteRecord(String tableName, int recordID)
	{
		String key = getRecordKey(tableName, recordID);
		remove(key);
	}
	
	private void updateRecord(String tableName, Record record)
	{
		deleteRecord(tableName, record.recordID);
		saveRecord(tableName, record);
	}
}
