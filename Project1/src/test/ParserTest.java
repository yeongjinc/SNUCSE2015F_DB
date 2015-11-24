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

import db.Constant;
import db.DBMSParser;

/**
 * jUnit 테스트 코드
 */
public class ParserTest
{
	@Before
	public void before()
	{
		// 초기화 과정
	}
	
	@After
	public void after()
	{
		// 정리 과정
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
		
		DBMSParser.run(is, ps, true);
		
		String result = os.toString();
		return result;
	}
	
	@Test
	public void testCreateTable()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_CREATE_TABLE + '\n', 	mockMain("create table a(a int);"));
		assertEquals("Constraints", 	Constant.PROMPT + Constant.STR_CREATE_TABLE + '\n', 	mockMain("create table a(a int, foreign key(a) references t(b));"));
		assertEquals("SimpleError", 	Constant.PROMPT + Constant.STR_SYNTAX_ERROR + '\n', 	mockMain("create table a;"));
		assertEquals("KeywordError", 	Constant.PROMPT + Constant.STR_SYNTAX_ERROR + '\n', 	mockMain("create table a(select int);"));
	}
	
	@Test
	public void testDropTable()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_DROP_TABLE + '\n', 		mockMain("drop table a;"));
		assertEquals("NewLine", 		Constant.PROMPT + Constant.STR_DROP_TABLE + '\n', 		mockMain("drop table\n a;"));
	}
	
	@Test
	public void testDesc()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_DESC + '\n', 			mockMain("desc a;"));
	}
	
	// Grammar에서 없어져서 일단 삭제
	/*
	@Test
	public void testShowTables()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_SHOW_TABLES + '\n', 		mockMain("show tables;"));
	}
	*/
	
	@Test
	public void testInsert()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_INSERT + '\n', 			mockMain("insert into a values(9732, 'Perryridge');"));
		assertEquals("WithColName", 	Constant.PROMPT + Constant.STR_INSERT + '\n', 			mockMain("insert into a(col_a, col_b) values(9732, 'Perryridge');"));
		assertEquals("Error", 			Constant.PROMPT + Constant.STR_SYNTAX_ERROR + '\n', 	mockMain("insert into a values();"));
	}
	
	@Test
	public void testDelete()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_DELETE + '\n', 			mockMain("delete from a;"));
		assertEquals("WithWhere", 		Constant.PROMPT + Constant.STR_DELETE + '\n', 			mockMain("delete from a where b = 1;"));
		assertEquals("Error", 			Constant.PROMPT + Constant.STR_SYNTAX_ERROR + '\n', 	mockMain("delete a;"));
	}
	
	@Test
	public void testSelect()
	{
		assertEquals("Basic", 			Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select * from a;"));
		assertEquals("WithWhere", 		Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select * from a where k = 'aa';"));
		assertEquals("WithColumn", 		Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select col_a, col_b from a where k = 'aa';"));
		assertEquals("Error", 			Constant.PROMPT + Constant.STR_SYNTAX_ERROR + '\n', 	mockMain("select where from a;"));
	}
	
	@Test
	public void testWhere()
	{
		assertEquals("Where1", 			Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select * from a where b = c;"));
		assertEquals("Where2", 			Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select * from a where b = c or d = 1;"));
		assertEquals("Where3", 			Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select * from a where b = c and d = e or f = g and h = i;"));
		assertEquals("Where4", 			Constant.PROMPT + Constant.STR_SELECT + '\n', 			mockMain("select * from a where b > 0 and (c < 0 or d >= 1);"));
	}
	
	@Test
	public void testNestAndLine()
	{
		assertEquals("Nest1", 			Constant.PROMPT + Constant.STR_SELECT + '\n'
										+ Constant.PROMPT + Constant.STR_INSERT + '\n', 		mockMain("select * from a;\ninsert into a values(1);"));
		assertEquals("Nest2", 			Constant.PROMPT + Constant.STR_SELECT + '\n'
										+ Constant.PROMPT + Constant.STR_SELECT + '\n'
										+ Constant.PROMPT + Constant.STR_SELECT + '\n', 		mockMain("select * from a;select a from b;select\nc from d;"));
		assertEquals("Error", 			Constant.PROMPT + Constant.STR_SYNTAX_ERROR + '\n', 	mockMain("select * from a where b = c\nselect * from a;"));
	}
}
