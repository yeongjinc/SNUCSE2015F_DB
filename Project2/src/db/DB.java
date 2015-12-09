package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import dataobject.CustomException.CSATRangeException;
import dataobject.CustomException.CapacityException;
import dataobject.CustomException.GroupException;
import dataobject.CustomException.HighschoolRangeException;
import dataobject.CustomException.HighschoolWeightException;
import dataobject.CustomException.NoStudentException;
import dataobject.CustomException.NoUniversityException;
import dataobject.CustomException.OverApplyException;
import dataobject.Student;
import dataobject.University;

public class DB
{
	private static DB _instance = null;
	private Connection conn = null;
	
	private DB()
	{
		try
		{
			conn = DriverManager.getConnection(Constant.CONNECT_URL, Constant.USER_NAME, Constant.PASSWORD);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	public static DB getInstance()
	{
		if(_instance == null)
			_instance = new DB();
		return _instance;
	}
	
	public void close()
	{
		if(conn != null)
		{
			try
			{
				conn.close();
				conn = null;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// Just for save, not called every run
	// Reset || Create
	public void createDB()
	{
		// Delete if exists
		String deleteAppl = "DROP TABLE IF EXISTS application;";
		
		String deleteStud = "DROP TABLE IF EXISTS student;";
		
		String deleteUniv = "DROP TABLE IF EXISTS university;";
		
		String createStud = "CREATE TABLE student (id INT PRIMARY KEY, "
							+ "name VARCHAR(20), csat_score INT, school_score INT);";
		
		String createUniv = "CREATE TABLE university (id INT PRIMARY KEY, "
							+ "name VARCHAR(128), capacity INT, `group` VARCHAR(1), weight DOUBLE, "
							+ "applied INT DEFAULT 0);";
		
		String createAppl = "CREATE TABLE application ("
							+ "stud_id INT, univ_id INT, "
							+ "FOREIGN KEY(stud_id) REFERENCES student(id) ON DELETE CASCADE, "
							+ "FOREIGN KEY(univ_id) REFERENCES university(id) ON DELETE CASCADE);";
		
		try
		{
			String sql = deleteAppl;
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
			sql = deleteStud;
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
			sql = deleteUniv;
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
			sql = createStud;
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
			sql = createUniv;
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
			sql = createAppl;
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<University> getAllUniversities()
	{
		ArrayList<University> arr = new ArrayList<>();
		
		try
		{
			String sql = "SELECT * FROM university;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int cap = rs.getInt("capacity");
				String group = rs.getString("group");
				double weight = rs.getDouble("weight");
				int applied = rs.getInt("applied");
				
				University univ = new University();
				univ.setID(id);
				univ.setName(name);
				univ.setCapacity(cap);
				univ.setGroup(group);
				univ.setWeight(weight);
				univ.setApplied(applied);
				
				arr.add(univ);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CapacityException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(GroupException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolWeightException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public ArrayList<Student> getAllStudents()
	{
		ArrayList<Student> arr = new ArrayList<>();
		
		try
		{
			String sql = "SELECT * FROM student;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int sat = rs.getInt("csat_score");
				int high = rs.getInt("school_score");
				
				Student s = new Student();
				s.setID(id);
				s.setName(name);
				s.setSATScore(sat);
				s.setHighschoolScore(high);
				
				arr.add(s);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CSATRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public boolean insertUniversity(University u)
	{
		int ret = 0;
		
		try
		{
			// Auto Increment 대신 ID 수동 관리. Default 1
			u.setID(1);
			
			String sqlID = "SELECT MAX(`id`) AS `id` FROM university;";
			PreparedStatement stmtID = conn.prepareStatement(sqlID);
			ResultSet rs = stmtID.executeQuery();
			if(rs.next())
			{
				u.setID(rs.getInt("id") + 1);
			}
			
			String sql = "INSERT INTO university(`id`, name, capacity, `group`, weight) VALUES(?, ?, ?, ?, ?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, u.getID());
			stmt.setString(2, u.getName());
			stmt.setInt(3, u.getCapacity());
			stmt.setString(4, u.getGroup() + "");
			stmt.setDouble(5, u.getWeight());
			
			ret = stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ret > 0;		// 입력되었다면 1 리턴
	}
	
	public boolean removeUniversity(int id) throws NoUniversityException
	{
		int ret = 0;
		
		try
		{
			// Application은 ON DELETE CASCADE 옵션에 의해 자동으로 사라짐
			// Applied는 어차피 레코드 자체가 지워지므로 상관 없음
			
			University u = getUniversity(id);
			
			String sql = "DELETE FROM university WHERE id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, u.getID());
			
			ret = stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ret > 0;
	}
	
	public boolean insertStudent(Student s)
	{
		int ret = 0;
		
		try
		{
			// Auto Increment 대신 ID 수동 관리. Default 1
			s.setID(1);
			
			String sqlID = "SELECT MAX(`id`) AS `id` FROM student;";
			PreparedStatement stmtID = conn.prepareStatement(sqlID);
			ResultSet rs = stmtID.executeQuery();
			if(rs.next())
			{
				s.setID(rs.getInt("id") + 1);
			}
			
			String sql = "INSERT INTO student(`id`, name, csat_score, school_score) VALUES(?, ?, ?, ?);";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, s.getID());
			stmt.setString(2, s.getName());
			stmt.setInt(3, s.getSATScore());
			stmt.setInt(4, s.getHighschoolScore());
			
			ret = stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ret > 0;
	}
	
	public boolean removeStudent(int id) throws NoStudentException
	{
		int ret = 0;
		
		try
		{
			// Application은 ON DELETE CASCADE 옵션에 의해 자동으로 사라짐
			// Applied는 조정해주어야 함
			
			Student s = getStudent(id);
			
			ArrayList<University> appliedUniv = getAppliedUniversity(s.getID());
			for(University u : appliedUniv)
			{
				String decreaseSQL = "UPDATE university SET applied = applied - 1 WHERE "
									+ "id = ?;";
				PreparedStatement decreaseSTMT = conn.prepareStatement(decreaseSQL);
				decreaseSTMT.setInt(1, u.getID());
				if(decreaseSTMT.executeUpdate() <= 0)
				{
					System.out.println("NEVER REACH HERE, maybe data was contamenated");
				}
			}
			
			String sql = "DELETE FROM student WHERE id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, s.getID());
			
			ret = stmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return ret > 0;
	}
	
	public void checkStudent(int studID) throws NoStudentException
	{
		//just check for existence
		getStudent(studID);
	}
	
	private Student getStudent(int studID) throws NoStudentException
	{
		try
		{
			String sql = "SELECT * FROM student WHERE id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, studID);
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int sat = rs.getInt("csat_score");
				int high = rs.getInt("school_score");
				
				Student s = new Student();
				s.setID(id);
				s.setName(name);
				s.setSATScore(sat);
				s.setHighschoolScore(high);
				
				return s;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CSATRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		throw new NoStudentException(studID);
	}
	
	private University getUniversity(int univID) throws NoUniversityException
	{
		try
		{
			String sql = "SELECT * FROM university WHERE id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, univID);
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int cap = rs.getInt("capacity");
				String group = rs.getString("group");
				double weight = rs.getDouble("weight");
				int applied = rs.getInt("applied");
				
				University univ = new University();
				univ.setID(id);
				univ.setName(name);
				univ.setCapacity(cap);
				univ.setGroup(group);
				univ.setWeight(weight);
				univ.setApplied(applied);
				
				return univ;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CapacityException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(GroupException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolWeightException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		throw new NoUniversityException(univID);
	}
	
	public boolean makeApplication(int studID, int univID)
					throws OverApplyException, NoUniversityException, NoStudentException
	{
		boolean ret = false;
		try
		{
			// get student / university, exception occurs if not exist
			Student s = getStudent(studID);
			University u = getUniversity(univID);
			
			// check duplicate group
			ArrayList<University> applied = getAppliedUniversity(studID);
			for(University a : applied)
			{
				if(a.getGroup() == u.getGroup())
					throw new OverApplyException();
			}
			
			// Transaction (1. insert into application 2. increase applied count in univ)
			conn.setAutoCommit(false);
			
			// apply
			String applySQL = "INSERT INTO application(stud_id, univ_id) VALUES(?, ?);";
			PreparedStatement applySTMT = conn.prepareStatement(applySQL);
			applySTMT.setInt(1, s.getID());
			applySTMT.setInt(2, u.getID());
			int ret1 = applySTMT.executeUpdate();
			
			// increase applied count 
			String increaseSQL = "UPDATE university SET applied = applied + 1 WHERE "
								+ "id = ?;";
			PreparedStatement increaseSTMT = conn.prepareStatement(increaseSQL);
			increaseSTMT.setInt(1, u.getID());
			int ret2 = increaseSTMT.executeUpdate();
			
			if(ret1 > 0 && ret2 > 0)
			{
				conn.commit();
				ret = true;
			}
			else
			{
				conn.rollback();
				System.out.println("Apply failed by unknown error.");
			}
		}
		catch(SQLException e)
		{
			try
			{
				conn.rollback();
			}
			catch (SQLException s)
			{
				s.printStackTrace();
			}
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	public ArrayList<Student> getAppliedStudent(int univID) throws NoUniversityException
	{
		ArrayList<Student> arr = new ArrayList<>();
	
		try
		{
			University u = getUniversity(univID);
			
			String sql = "SELECT S.id, name, csat_score, school_score FROM "
						+ "student S JOIN application ON stud_id = S.id "
						+ "WHERE univ_id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, u.getID());
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int sat = rs.getInt("csat_score");
				int high = rs.getInt("school_score");
				
				Student s = new Student();
				s.setID(id);
				s.setName(name);
				s.setSATScore(sat);
				s.setHighschoolScore(high);
				
				arr.add(s);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CSATRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public ArrayList<University> getAppliedUniversity(int studID) throws NoStudentException
	{
		ArrayList<University> arr = new ArrayList<>();
		
		try
		{
			Student s = getStudent(studID);
			
			String sql = "SELECT U.id, name, capacity, `group`, weight, applied "
					+ "FROM university U JOIN application ON univ_id = U.id "
					+ "WHERE stud_id = ?;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, s.getID());
			
			ResultSet rs = stmt.executeQuery();
		
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int cap = rs.getInt("capacity");
				String group = rs.getString("group");
				double weight = rs.getDouble("weight");
				int applied = rs.getInt("applied");
				
				University univ = new University();
				univ.setID(id);
				univ.setName(name);
				univ.setCapacity(cap);
				univ.setGroup(group);
				univ.setWeight(weight);
				univ.setApplied(applied);
				
				arr.add(univ);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CapacityException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(GroupException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolWeightException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public ArrayList<Student> getExpectedStudent(int univID) throws NoUniversityException
	{
		ArrayList<Student> arr = new ArrayList<>();
		
		try
		{
			University u = getUniversity(univID);
			
			String sql = "SELECT S.id, S.name, S.csat_score, S.school_score, "
						+ "(S.csat_score + S.school_score * U.weight) AS total_score FROM "
						+ "student S JOIN application A ON A.stud_id = S.id "
						+ "JOIN university U ON A.univ_id = U.id "
						+ "WHERE univ_id = ? "
						+ "ORDER BY total_score DESC, school_score DESC;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, u.getID());
			
			ResultSet rs = stmt.executeQuery();
			
			// 동점자 10% 룰 때문에 복잡해짐 
			int lastSameSAT = 0, lastSameSchool = 0;
			Student prev = null;
			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int sat = rs.getInt("csat_score");
				int high = rs.getInt("school_score");
				
				Student s = new Student();
				s.setID(id);
				s.setName(name);
				s.setSATScore(sat);
				s.setHighschoolScore(high);
				
				if(arr.size() < u.getCapacity()) 
				{
					// 아직 정원이 안찼으면 무조건 넣는다
					arr.add(s);
				}
				else if(prev != null && prev.getSATScore() == s.getSATScore() 
						&& prev.getHighschoolScore() == s.getHighschoolScore())
				{
					// 정원이 찼지만 앞 학생과 점수가 완전히 같으면 넣는다
					// 이 때 추후 삭제가능성이 있기 때문에 점수를 저장해놓는다
					arr.add(s);
					lastSameSAT = s.getSATScore();
					lastSameSchool = s.getHighschoolScore();
				}
				else
				{
					// 정원도 찼고, 앞 학생과 다르면 탈락이므로 break 
					break;
				}
				
				prev = s;
			}
			
			int tenPercent = (u.getCapacity() + 10 - 1) / 10; 
			if(arr.size() > u.getCapacity() + tenPercent)
			{
				// 정원 + 10% 올림까지 초과할 경우 마지막 동점학생들을 뺸다
				Iterator<Student> i = arr.iterator();
				while(i.hasNext())
				{
					Student iter = i.next();
					if(iter.getSATScore() == lastSameSAT && iter.getHighschoolScore() == lastSameSchool)
						i.remove();
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(CSATRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		catch(HighschoolRangeException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		return arr;
	}
	
	public ArrayList<University> getExpectedUniversity(int studID) throws NoStudentException
	{
		ArrayList<University> arr = new ArrayList<>();
		
		try
		{
			ArrayList<University> applied = getAppliedUniversity(studID);
			for(University u : applied)
			{
				ArrayList<Student> expectedStud = getExpectedStudent(u.getID());
				for(Student s : expectedStud)
				{
					if(s.getID() == studID)
					{
						// 합격 예상 명단에 있다는 것은 해당 대학이 합격 예상 대학이라는 뜻
						arr.add(u);
						break;
					}
				}
			}
		}
		catch(NoUniversityException e)
		{
			System.out.println("NEVER REACH HERE");
			e.printStackTrace();
		}
		
		return arr;
	}
	
}
