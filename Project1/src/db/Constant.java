package db;

public class Constant
{
	// 각 케이스의 임시 리턴(결과 프린트)
	public static final String PROMPT 			= "DB_2009-11841> ";
	public static final int PRINT_SYNTAX_ERROR 	= 0;
	public static final int PRINT_CREATE_TABLE 	= 1;
	public static final int PRINT_DROP_TABLE 	= 2;
	public static final int PRINT_DESC 			= 3;
	public static final int PRINT_SHOW_TABLES 	= 4;
	public static final int PRINT_INSERT 		= 5;
	public static final int PRINT_DELETE 		= 6;
	public static final int PRINT_SELECT 		= 7;
	
	public static final String STR_SYNTAX_ERROR	= "Syntax Error";
	public static final String STR_CREATE_TABLE	= "\'CREATE TABLE\' requested";
	public static final String STR_DROP_TABLE	= "\'DROP TABLE\' requested";
	public static final String STR_DESC			= "\'DESC\' requested";
	public static final String STR_SHOW_TABLES	= "\'SHOW TABLES\' requested";
	public static final String STR_INSERT		= "\'INSERT\' requested";
	public static final String STR_DELETE		= "\'DELETE\' requested";
	public static final String STR_SELECT		= "\'SELECT\' requested";

	/// prj 1-2
	private static final String _CreateTableSuccess 			= "'%s' table is created";
	public static final String DuplicateColumnDefError 			= "Create table has failed: column definition is duplicated";
	public static final String DuplicatePrimaryKeyDefError 		= "Create table has failed: primary key definition is duplicated";
	public static final String ReferenceTypeError 				= "Create table has failed: foreign key references wrong type";
	public static final String ReferenceNonPrimaryKeyError 		= "Create table has failed: foreign key references non primary key column"; 
	public static final String ReferenceColumnExistenceError 	= "Create table has failed: foreign key references non existing column"; 
	public static final String ReferenceTableExistenceError 	= "Create table has failed: foreign key references non existing table";
	private static final String _NonExistingColumnDefError 		= "Create table has failed: '%s' does not exists in column definition";
	public static final String TableExistenceError 				= "Create table has failed: table with the same name already exists";
	
	private static final String _DropSuccess					= "'%s' table is dropped";
	public static final String DropSuccessAllTables				= "Every table is dropped";
	private static final String _DropReferencedTableError		= "Drop table has failed: '%s' is referenced by other table";
	public static final String NoSuchTable						= "No such table";
	public static final String CharLengthError					= "Char length should be > 0";
	
	public static String CreateTableSuccess(String tableName)
	{ return String.format(_CreateTableSuccess, tableName); }
	
	public static String NonExistingColumnDefError(String colName)
	{ return String.format(_NonExistingColumnDefError, colName); }
	
	public static String DropSuccess(String tableName)
	{ return String.format(_DropSuccess, tableName); }
	
	public static String DropReferencedTableError(String tableName)
	{ return String.format(_DropReferencedTableError, tableName); }
	
	
	/// prj 1-3
	public static String InsertResult = "The row is inserted";
	public static String InsertDuplicatePrimaryKeyError = "Insertion has failed: Primary key duplication";
	public static String InsertReferentialIntegrityError = "Insertion has failed: Referential integrity violation";
	public static String InsertTypeMismatchError = "Insertion has failed: Types are not matched";
	private static String _InsertColumnExistenceError = "Insertion has failed: '%s' does not exist";
	private static String _InsertColumnNonNullableError = "Insertion has failed: '%s' is not nullable";
	
	private static String _DeleteResult = "%d row(s) are deleted";
	private static String _DeleteReferentialIntegrityPassed = "%d row(s) are not deleted due to referential integrity";
	
	private static String _SelectTableExistenceError = "Selection has failed: '%s' does not exist";
	private static String _SelectColumnResolveError = "Selection has failed: fail to resolve '%s'";
	
	// NoSuchTable은 위의 것 그대로 사용
	
	public static String WhereIncomparableError = "Where clause try to compare incomparable values";
	public static String WhereTableNotSpecified = "Where clause try to reference tables which are not specified";
	public static String WhereColumnNotExist = "Where clause try to reference non existing column";
	public static String WhereAmbiguousReference = "Where clause contains ambiguous reference";
	
	public static String InsertColumnExistenceError(String colName)
	{ return String.format(_InsertColumnExistenceError, colName); }
	
	public static String InsertColumnNonNullableError(String colName)
	{ return String.format(_InsertColumnNonNullableError, colName); }
	
	public static String DeleteResult(int n)
	{ return String.format(_DeleteResult, n); }
	
	public static String DeleteReferentialIntegrityPassed(int n)
	{ return String.format(_DeleteReferentialIntegrityPassed, n); }
	
	public static String SelectTableExistenceError(String tableName)
	{ return String.format(_SelectTableExistenceError, tableName); }
	
	public static String SelectColumnResolveError(String colName)
	{ return String.format(_SelectColumnResolveError, colName); }
	
	
	/* Custom Defined */
	public static final String DuplicateForeignKeyError			= "[CUSTOM] Create table has failed: foreign key definition is duplicated";
	public static final String DuplicateKeyColumnError			= "[CUSTOM] Create table has failed: column definition of key is duplicated";
	
	public static final String InsertSizeMismatch				= "[CUSTOM] Number of columns does not match number of inputs";
	
	public static final String DebugNotReached					= "[CUSTOM][DEBUG] Never reach here";
	/* Custom Defined End */
	
	
}
