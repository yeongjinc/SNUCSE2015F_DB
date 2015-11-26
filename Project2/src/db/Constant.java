package db;

public class Constant
{
	/* DB Connection Info */
	public static final String SERVER_IP 	= "147.46.15.238";
	public static final String DB_NAME 		= "DB-2009-11841";
	public static final String USER_NAME 	= DB_NAME;
	public static final String PASSWORD		= DB_NAME;
	
	public static final String CONNECT_URL	= "jdbc:mariadb://" + SERVER_IP + "/" + DB_NAME;
	
	
	/* User Message */
	public static final String INSERT_UNIV_SUCC = "A university is successfully inserted.";
	public static final String DELETE_UNIV_SUCC = "A university is successfully deleted.";
	public static final String INSERT_STUD_SUCC = "A student is successfully inserted.";
	public static final String DELETE_STUD_SUCC = "A student is successfully deleted.";
	public static final String CAPACITY_ERROR 	= "Capacity should be over 0.";
	public static final String GROUP_ERROR		= "Group should be 'A', 'B', or 'C'.";
	public static final String WEIGHT_ERROR 	= "Weight of high school records cannot be negative.";
	public static final String SAT_ERROR 		= "CSAT score should be between 0 and 400.";
	public static final String HIGH_ERROR		= "High school records score should be between 0 and 100.";
	public static final String APPL_SUCC		= "Successfully made an application.";
	public static final String APPL_FAIL		= "A student can apply up to one university per group.";
	public static final String MENU_ERROR		= "Invalid action.";
	private static final String _UNIV_ID_ERROR	= "University %d doesn't exist.";
	private static final String _STUD_ID_ERROR	= "Student %d doesn't exist.";
	public static String UNIV_ID_ERROR(int id)
	{
		return String.format(_UNIV_ID_ERROR, id);
	}
	public static String STUD_ID_ERROR(int id)
	{
		return String.format(_STUD_ID_ERROR, id);
	}
	public static final String FORMAT_ERROR		= "[CUSTOM] Wrong type value.";
	
	/* System Message */
	public static final String DOUBLE_LINE 		= "============================================================";
	public static final String SINGLE_LINE		= "---------------------------------------------------------------------------";
	public static final String MENUS[]			= 
			{
				"print all universities",
				"print all students",
				"insert a new university",
				"remove a university",
				"insert a new student",
				"remove a student",
				"make an application",
				"print all students who applied for a university",
				"print all universities a student applied for",
				"print expected successful applicants of a university",
				"print universities expected to accept a student",
				"exit",
			};
	public static final String SELECT_ACTION 	= "Select your action: ";
	public static final String BYE				= "Bye!";
	
	/* Input */
	public static final String U1 = "University name: ";
	public static final String U2 = "University capacity: ";
	public static final String U3 = "University group: ";
	public static final String U4 = "Weight of high school records: ";
	
	public static final String S1 = "Student name: ";
	public static final String S2 = "CSAT score: ";
	public static final String S3 = "High school record score: ";
	
	public static final String A1 = "Student ID: ";
	public static final String A2 = "University ID: ";
}
