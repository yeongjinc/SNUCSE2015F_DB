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
public class DDLTest
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
		// 매 테스트마다 불림 ㅠㅠ

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
	
	// SNUCSE 이인용 님 테스트 케이스를 가져왔음을 밝힌다.
	@Test
	public void test()
	{
		assertEquals("Init", 			
				Constant.DropSuccessAllTables + '\n', 	
				mockMain("drop table *;"));
		
		assertTrue(mockMain("create table a(a int); desc *; drop table a;").contains("table_name"));
		
		assertEquals("Destroy", 			
				Constant.DropSuccessAllTables + '\n', 	
				mockMain("drop table *;"));
		
		assertEquals("Init", 			
				Constant.DropSuccessAllTables + '\n', 	
				mockMain("drop table *;"));
		
		assertEquals("NoReferenceTable", 			
				Constant.ReferenceTableExistenceError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("Normal_Customer", 			
				Constant.CreateTableSuccess("customer") + '\n', 	
				mockMain("create table customer( \n" + 
						"customer_number int not null, \n" + 
						"name char(50) not null, \n" + 
						"age int, \n" + 
						"primary key(customer_number, name) \n" + 
						");"));
		
		assertEquals("DuplicatedTable", 			
				Constant.TableExistenceError + '\n', 	
				mockMain("create table customer( \n" + 
						"asdf char(10) not null, \n" + 
						"primary key (asdf) \n" + 
						"); "));
		
		assertEquals("ReferenceCount", 			
				Constant.ReferenceTypeError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number, l) \n" + 
						");"));
		
		assertEquals("ReferenceType", 			
				Constant.ReferenceTypeError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (name) \n" + 
						");"));
		
		assertEquals("ReferenceNoTable", 			
				Constant.ReferenceTableExistenceError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customerfa (customer_number) \n" + 
						");"));
		
		assertEquals("CustomDuplicate", 			
				Constant.DuplicateKeyColumnError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number, customer_number) references customer (customer_number, customer_number) \n" + 
						");"));
		
		assertEquals("ReferenceType", 			
				Constant.ReferenceTypeError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number date not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("ReferenceType", 			
				Constant.ReferenceTypeError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number char(10) not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("MinusLength", 			
				Constant.CharLengthError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(-25), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("PKNotExist", 			
				Constant.NonExistingColumnDefError("asdf") + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(asdf), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("FKNotExist", 			
				Constant.NonExistingColumnDefError("fdssdf") + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(fdssdf) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("DuplicatePK", 			
				Constant.DuplicatePrimaryKeyDefError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"primary key(branch_name), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("CustomDuplicate", 			
				Constant.DuplicateKeyColumnError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number, account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("FKRefNotExist", 			
				Constant.ReferenceColumnExistenceError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (asdf) \n" + 
						");"));
		
		assertEquals("FKNotPK", 			
				Constant.ReferenceNonPrimaryKeyError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (age) \n" + 
						");"));
		
		assertEquals("FKType_CharLength", 			
				Constant.ReferenceTypeError + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_name char(45) not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_name) references customer (name) \n" + 
						");"));
		
		assertEquals("CreateTableAccount", 			
				Constant.CreateTableSuccess("account") + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_name char(50) not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_name) references customer (name) \n" + 
						");"));
		
		assertEquals("DropTableAccount", 			
				Constant.DropSuccess("account") + '\n', 	
				mockMain("drop table account;"));
		
		assertEquals("CreateTableAccount", 			
				Constant.CreateTableSuccess("account") + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertEquals("TryReferenced", 			
				Constant.DropReferencedTableError("customer") + '\n', 	
				mockMain("drop table customer;"));
		
		assertEquals("DescNotExist", 			
				Constant.NoSuchTable + '\n', 	
				mockMain("desc customer, asdf, account;"));
		
		assertEquals("DropSequence", 			
				Constant.DropReferencedTableError("customer") + '\n'
				+ Constant.NoSuchTable + '\n'
				+ Constant.DropSuccess("account") + '\n', 	
				mockMain("drop table customer, asdf, account;"));
		
		assertEquals("Drop", 			
				Constant.DropSuccess("customer") + '\n', 	
				mockMain("drop table customer;"));
		
		assertEquals("CreateTableCustomer", 			
				Constant.CreateTableSuccess("customer") + '\n', 	
				mockMain("create table customer( \n" + 
						"customer_number int not null, \n" + 
						"name char(50) not null, \n" + 
						"age int, \n" + 
						"primary key(customer_number) \n" + 
						");"));
		
		assertEquals("CreateTableAccount", 			
				Constant.CreateTableSuccess("account") + '\n', 	
				mockMain("create table account( \n" + 
						"account_number int not null, \n" + 
						"branch_name char(15), \n" + 
						"customer_number int not null, \n" + 
						"primary key(account_number, customer_number), \n" + 
						"foreign key(customer_number) references customer (customer_number) \n" + 
						");"));
		
		assertTrue(mockMain("desc account, customer;").contains("PRI/FOR"));
		assertTrue(mockMain("desc account, customer;").contains("N"));
		assertTrue(mockMain("desc account, customer;").contains("char(15)"));
		
		assertEquals("DropAll", 			
				Constant.DropSuccessAllTables + '\n', 	
				mockMain("drop table *;"));
		
		assertEquals("Nothing", 			
				"", 	
				mockMain("desc *;"));
	}
	
	
}
