package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.BerkeleyDBHelper;
import db.Constant;
import db.DBMSParser;

/**
 * jUnit 테스트 코드
 *  
 */
public class DMLTest
{
	@Before
	public void before()
	{
		// 초기화 과정
		// 매 테스트마다 불림
		
		// 여기서는 테이블 및 기본 데이터 세팅
		
		// BasicTable
		mockMain("create table BasicTable\n" + 
				"(fa int, fb char(10), fc date);");
		mockMain("insert into basicTable values (111, 'aaaaa', 2015-11-11);\n" + 
				"insert into basicTable values (222, 'bbbbb', 2015-11-12);\n" + 
				"insert into basicTable values (333, 'ccccc', null);\n" + 
				"insert into basicTable values (444, 'ddddd', 2015-11-14);");
		
		
		// Reference
		mockMain("create table ATable(fa int, fb int, primary key(fa));");
		mockMain("insert into ATable values(100, -100);");
		mockMain("insert into ATable values(200, -200);");
		mockMain("insert into ATable values(300, -300);");
		
		mockMain("create table BTable(fx date, fy int, foreign key(fy) references ATable(fa));");
		mockMain("insert into BTable values(null, 200);");
		mockMain("insert into BTable values(2015-01-01, 200);");
		mockMain("insert into BTable values(2015-02-02, 300);");
		
		
		// Reference(Not Null)
		mockMain("create table CTable(fa int, fb int, primary key(fa));");
		mockMain("insert into CTable values(100, -100);");
		mockMain("insert into CTable values(200, -200);");
		mockMain("insert into CTable values(300, -300);");
		
		mockMain("create table DTable(fx date not null, fy int not null, foreign key(fy) references CTable(fa));");
		mockMain("insert into DTable values(2015-03-03, 200);");
		mockMain("insert into DTable values(2015-01-01, 200);");
		mockMain("insert into DTable values(2015-02-02, 300);");
	}
	
	@After
	public void after()
	{
		// 정리 과정
		// 매 테스트마다 불림
		
		// delete all table & data
		mockMain("drop table *;");

		// close DB
		BerkeleyDBHelper.getInstance().closeDB();
	}
	
	/**
	 * 실제 프로그램의 main 함수를 흉내내는 함수로, String parameter로 input을 받아 결과를 String으로 리턴한다.
	 * 이를 위해 내부적으로 Stream과의 변환 과정이 있다.
	 */
	public String mockMain(String query)
	{
		InputStream is = new ByteArrayInputStream(query.getBytes());
		OutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		
		// 한 줄만 실행하고 종료함
		// 이번 프로젝트 2 부터 버클리 DB 가 추가되면서, DB close가 필요하게 되어 이는 after에서 해줌
		DBMSParser.run(is, ps, true);
		
		String result = os.toString();
		return result;
	}
	
	@Test
	public void selectBasic()
	{
		assertTrue(mockMain("select * from BasicTable;").contains("aaaaa"));
		assertTrue(mockMain("select * from BasicTable;").contains("2015-11-12"));
		assertTrue(mockMain("select * from BasicTable;").contains("333"));
		
		assertFalse(mockMain("select fb from BasicTable;").contains("333"));
	}
	
	@Test
	public void selectWhere()
	{
		assertFalse(mockMain("select fa from BasicTable where fa > 222;").contains("111"));
		assertTrue(mockMain("select fa from BasicTable where fa > 222;").contains("333"));
		
		assertTrue(mockMain("select BasicTable.fa from BasicTable where fc is null;").contains("333"));
		assertFalse(mockMain("select fa from BasicTable where fc is not null;").contains("333"));
		assertTrue(mockMain("select fa as isthisname from BasicTable where fc is not null;").contains("111"));
		assertTrue(mockMain("select fa as isthisname from BasicTable where fc is not null;").contains("isthisname".toUpperCase()));
	}
	
	@Test
	public void selectJoin()
	{
		assertTrue(mockMain("select * from BasicTable, ATable;").contains("-300"));
		assertTrue(mockMain("select * from BasicTable, ATable;").contains("ATable.fa".toUpperCase()));
		assertTrue(mockMain("select * from BasicTable, ATable;").contains("BasicTable.fa".toUpperCase()));
		
		assertFalse(mockMain("select ATable.fa from BasicTable, ATable;").contains("222"));
		assertFalse(mockMain("select ATable.fa from BasicTable, ATable;").contains("BasicTable.fa".toUpperCase()));
		assertTrue(mockMain("select ATable.fa from BasicTable, ATable;").contains("ATable.fa".toUpperCase()));
	}
	
	@Test
	public void selectError()
	{
		assertEquals("TableNotExist",
				Constant.SelectTableExistenceError("dummyTable") + '\n',
				(mockMain("select * from dummyTable, BasicTable;"))
				);
		
		assertEquals("ColumnNotExist",
				Constant.SelectColumnResolveError("dummyCol") + '\n',
				(mockMain("select fa, dummyCol from BasicTable;"))
				);
		
		assertEquals("ColumnAmbiguous",
				Constant.SelectColumnResolveError("fa") + '\n',
				(mockMain("select fa from BasicTable, ATable;"))
				);
		
		// where 조건은 delete에서만 테스트하자, 다만 where의 column ambiguous는 delete에서는 발생할 수 없으므로
		// (대상 테이블이 하나) 여기서 하자
		
		assertEquals("WhereColumnAmbiguous",
				Constant.WhereAmbiguousReference + '\n',
				(mockMain("select * from BasicTable, ATable where fa != 100;"))
				);
	}
	
	@Test
	public void insertBasic()
	{
		assertEquals("Normal",
				Constant.InsertResult + '\n',
				(mockMain("insert into BasicTable values(555, 'eeeee', 2015-11-20);"))
				);
		
		// null
		assertEquals("InsertNullValue",
				Constant.InsertResult + '\n',
				(mockMain("insert into BasicTable values(666, null, null);"))
				);
		
		assertTrue(mockMain("select fb, fc as simsim from BasicTable where fa = 666;").contains("null"));

		// some column
		assertEquals("InsertSomeColumn",
				Constant.InsertResult + '\n',
				(mockMain("insert into BasicTable(fb) values('fffff');"))
				);
		assertTrue(mockMain("select fb, fc as simsim from BasicTable where fb = 'fffff';").contains("null"));
		assertTrue(mockMain("select fb, fc as simsim from BasicTable where fb = 'fffff';").contains("fffff"));
	}
	
	@Test
	public void insertOverLength()
	{
		assertEquals("InsertOver", 
					Constant.InsertResult + '\n',
					mockMain("insert into BasicTable values(999, 'abcdefghijklmnop', 2015-11-20);")
					);
		
		assertFalse(mockMain("select fb from BasicTable where fa = 999;").contains("k"));
		assertTrue(mockMain("select fb from BasicTable where fa = 999;").contains("j"));
	}
	
	@Test
	public void insertError()
	{
		assertEquals("NoSuchTable",
				Constant.NoSuchTable+'\n',
				(mockMain("insert into BaseTable values(555, 'eeeee', 2015-11-20);"))
				);
		
		// some column not null
		assertEquals("NotNull1",
				Constant.InsertColumnNonNullableError("fx")+'\n',
				(mockMain("insert into DTable(fy) values(1000);"))
				);
		
		// null column not null
		assertEquals("NotNull2",
				Constant.InsertColumnNonNullableError("fx")+'\n',
				(mockMain("insert into DTable values(null, 1000);"))
				);
		
		
		assertEquals("TypeMismatch1",
				Constant.InsertTypeMismatchError+'\n',
				(mockMain("insert into BasicTable values('1000', 'ggggg', 2014-10-10);"))
				);
		
		assertEquals("TypeMismatch2",
				Constant.InsertTypeMismatchError+'\n',
				(mockMain("insert into BasicTable values(1000, 111, 2014-10-10);"))
				);
		
		assertEquals("TypeMismatch3",
				Constant.InsertTypeMismatchError+'\n',
				(mockMain("insert into BasicTable values(1000, '111', 2014);"))
				);
		
		
		assertEquals("ColumnNotExist",
				Constant.InsertColumnExistenceError("dummyCol")+'\n',
				(mockMain("insert into BasicTable(fa, dummyCol) values(1000, 'ggggg');"))
				);
		
		
		assertEquals("PrimaryKeySuccess",
				Constant.InsertResult+'\n',
				(mockMain("insert into ATable values(400, -400);"))
				);
		
		assertEquals("PrimaryKeyFail",
				Constant.InsertDuplicatePrimaryKeyError+'\n',
				(mockMain("insert into ATable values(400, -400);"))
				);
		
		
		assertEquals("ForeignKeySuccess",
				Constant.InsertResult+'\n',
				(mockMain("insert into BTable values(null, 400);"))
				);
		
		assertEquals("ForeignKeyFail",
				Constant.InsertReferentialIntegrityError+'\n',
				(mockMain("insert into BTable values(null, 401);"))
				);
	}
	
	
	@Test
	public void deleteBasic()
	{
		assertEquals("BasicDelete",
				Constant.DeleteResult(2)+'\n',
				(mockMain("delete from BasicTable where fb < 'bbbbc';"))
				);
		
		assertTrue(mockMain("select * from BasicTable;").contains("ccccc"));
		assertFalse(mockMain("select * from BasicTable;").contains("aaaaa"));

		
		assertEquals("BasicDelete2",
				Constant.DeleteResult(2)+'\n',
				(mockMain("delete from BTable where fx is not null;"))
				);
		assertFalse(mockMain("select * from BTable;").contains("2015"));

		
		assertEquals("DeleteAll",
				Constant.DeleteResult(3)+'\n',
				(mockMain("delete from DTable;"))
				);
		assertFalse(mockMain("select * from DTable;").contains("2015"));
	}
	
	@Test
	public void deleteError()
	{
		assertEquals("TableNotExist",
				Constant.NoSuchTable+'\n',
				(mockMain("delete from dummyTable where fb < 'bbbbc';"))
				);
		
		assertEquals("Incomparable1",
				Constant.WhereIncomparableError+'\n',
				(mockMain("delete from BasicTable where fb = 3;"))
				);
		
		assertEquals("Incomparable2",
				Constant.WhereIncomparableError+'\n',
				(mockMain("delete from BasicTable where fa = '3';"))
				);
		
		assertEquals("Incomparable3",
				Constant.WhereIncomparableError+'\n',
				(mockMain("delete from BasicTable where fc = '20133';"))
				);
		
		assertEquals("TableNotSpecified",
				Constant.WhereTableNotSpecified+'\n',
				(mockMain("delete from BasicTable where ATable.fa = 3;"))
				);
		
		assertEquals("ColumnNotExist",
				Constant.WhereColumnNotExist+'\n',
				(mockMain("delete from BasicTable where dummy = 3;"))
				);
		
		// Ambiguous는 delete에는 없음, select에서만 테스트하자	
	}
	
	@Test
	public void deleteFKNull()
	{
		assertEquals("InsertNullAtFK",
				Constant.InsertResult + '\n',
				(mockMain("insert into BTable values(2015-11-20, null);"))
				);
		
		assertEquals("DeleteFK",
				Constant.DeleteResult(1)+'\n',
				(mockMain("delete from ATable where fb = -200;"))
				);
		assertTrue(mockMain("select * from ATable;").contains("100"));
		assertFalse(mockMain("select * from ATable;").contains("200"));
		
		assertTrue(mockMain("select * from BTable where fx = 2015-01-01;").contains("null"));
		assertFalse(mockMain("select * from BTable where fx = 2015-01-01;").contains("200"));
		
		//System.out.println(mockMain("select * from ATable;"));
		//System.out.println(mockMain("select * from BTable;"));
	}
	
	@Test
	public void deleteFKNotNull()
	{	
		assertEquals("InsertNullAtFK_Fail",
				Constant.InsertColumnNonNullableError("fy") + '\n',
				(mockMain("insert into DTable values(2015-11-20, null);"))
				);
		
		assertEquals("InsertOneMore",
				Constant.InsertResult+'\n',
				(mockMain("insert into CTable values(400, -200);"))
				);
		
		assertEquals("DeleteFK",
				Constant.DeleteResult(1)+'\n'+Constant.DeleteReferentialIntegrityPassed(1)+'\n',
				(mockMain("delete from CTable where fb = -200;"))
				);
		assertTrue(mockMain("select * from CTable;").contains("100"));
		assertTrue(mockMain("select * from CTable;").contains("200"));
		assertFalse(mockMain("select * from CTable;").contains("400"));
		
		assertTrue(mockMain("select * from DTable where fx = 2015-01-01;").contains("200"));
		assertFalse(mockMain("select * from DTable where fx = 2015-01-01;").contains("null"));
	}
}
