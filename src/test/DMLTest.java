package test;

import static org.junit.Assert.*;

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
		mockMain("");
		mockMain("");
		mockMain("");
		mockMain("");
		mockMain("");
		mockMain("");
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
		
		assertTrue(mockMain("select fa from BasicTable where fc is null").contains("333"));
		assertFalse(mockMain("select fa from BasicTable where fc is not null").contains("333"));
		assertTrue(mockMain("select fa from BasicTable where fc is not null").contains("111"));
	}
	
	@Test
	public void selectError()
	{
		
	}
	
	@Test
	public void insertBasic()
	{
		assertEquals("Normal",
				Constant.InsertResult+'\n',
				(mockMain("insert into BasicTable values(555, 'eeeee', 2015-11-20);"))
				);
		
		// null
		
		
		// some column
	}
	
	@Test
	public void insertOverLength()
	{
		
	}
	
	@Test
	public void insertError()
	{
		assertEquals("NoSuchTable",
				Constant.NoSuchTable+'\n',
				(mockMain("insert into BaseTable values(555, 'eeeee', 2015-11-20);"))
				);
	}
	
	
	@Test
	public void deleteBasic()
	{
		
	}
	
	@Test
	public void deleteError()
	{
		
	}
	
	
	// 각 에러메세지
	// FK null
			
			// FK fail
			// not null 
}
