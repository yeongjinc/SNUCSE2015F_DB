/* Generated By:JavaCC: Do not edit this line. DBMSParserConstants.java */
package db;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface DBMSParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int EXIT = 5;
  /** RegularExpression Id. */
  int INT = 6;
  /** RegularExpression Id. */
  int CHAR = 7;
  /** RegularExpression Id. */
  int DATE = 8;
  /** RegularExpression Id. */
  int CREATE = 9;
  /** RegularExpression Id. */
  int TABLE = 10;
  /** RegularExpression Id. */
  int NULL = 11;
  /** RegularExpression Id. */
  int NOT = 12;
  /** RegularExpression Id. */
  int PRIMARY = 13;
  /** RegularExpression Id. */
  int KEY = 14;
  /** RegularExpression Id. */
  int FOREIGN = 15;
  /** RegularExpression Id. */
  int DROP = 16;
  /** RegularExpression Id. */
  int SHOW = 17;
  /** RegularExpression Id. */
  int TABLES = 18;
  /** RegularExpression Id. */
  int IS = 19;
  /** RegularExpression Id. */
  int REFERENCES = 20;
  /** RegularExpression Id. */
  int DESC = 21;
  /** RegularExpression Id. */
  int SELECT = 22;
  /** RegularExpression Id. */
  int AS = 23;
  /** RegularExpression Id. */
  int FROM = 24;
  /** RegularExpression Id. */
  int WHERE = 25;
  /** RegularExpression Id. */
  int OR = 26;
  /** RegularExpression Id. */
  int AND = 27;
  /** RegularExpression Id. */
  int INSERT = 28;
  /** RegularExpression Id. */
  int INTO = 29;
  /** RegularExpression Id. */
  int DELETE = 30;
  /** RegularExpression Id. */
  int VALUES = 31;
  /** RegularExpression Id. */
  int DELETE_FROM = 32;
  /** RegularExpression Id. */
  int CREATE_TABLE = 33;
  /** RegularExpression Id. */
  int NOT_NULL = 34;
  /** RegularExpression Id. */
  int PRIMARY_KEY = 35;
  /** RegularExpression Id. */
  int FOREIGN_KEY = 36;
  /** RegularExpression Id. */
  int IS_NULL = 37;
  /** RegularExpression Id. */
  int IS_NOT_NULL = 38;
  /** RegularExpression Id. */
  int INSERT_INTO = 39;
  /** RegularExpression Id. */
  int DROP_TABLE = 40;
  /** RegularExpression Id. */
  int SHOW_TABLES = 41;
  /** RegularExpression Id. */
  int R_EXIT = 42;
  /** RegularExpression Id. */
  int R_INT = 43;
  /** RegularExpression Id. */
  int R_CHAR = 44;
  /** RegularExpression Id. */
  int R_DATE = 45;
  /** RegularExpression Id. */
  int R_CREATE = 46;
  /** RegularExpression Id. */
  int R_TABLE = 47;
  /** RegularExpression Id. */
  int R_NULL = 48;
  /** RegularExpression Id. */
  int R_NOT = 49;
  /** RegularExpression Id. */
  int R_PRIMARY = 50;
  /** RegularExpression Id. */
  int R_KEY = 51;
  /** RegularExpression Id. */
  int R_FOREIGN = 52;
  /** RegularExpression Id. */
  int R_DROP = 53;
  /** RegularExpression Id. */
  int R_SHOW = 54;
  /** RegularExpression Id. */
  int R_TABLES = 55;
  /** RegularExpression Id. */
  int R_IS = 56;
  /** RegularExpression Id. */
  int R_REFERENCES = 57;
  /** RegularExpression Id. */
  int R_DESC = 58;
  /** RegularExpression Id. */
  int R_SELECT = 59;
  /** RegularExpression Id. */
  int R_AS = 60;
  /** RegularExpression Id. */
  int R_FROM = 61;
  /** RegularExpression Id. */
  int R_WHERE = 62;
  /** RegularExpression Id. */
  int R_OR = 63;
  /** RegularExpression Id. */
  int R_AND = 64;
  /** RegularExpression Id. */
  int R_INSERT = 65;
  /** RegularExpression Id. */
  int R_INTO = 66;
  /** RegularExpression Id. */
  int R_DELETE = 67;
  /** RegularExpression Id. */
  int R_VALUES = 68;
  /** RegularExpression Id. */
  int SEMICOLON = 69;
  /** RegularExpression Id. */
  int LEFT_PAREN = 70;
  /** RegularExpression Id. */
  int RIGHT_PAREN = 71;
  /** RegularExpression Id. */
  int SIGN = 72;
  /** RegularExpression Id. */
  int COMP_OP = 73;
  /** RegularExpression Id. */
  int COMMA = 74;
  /** RegularExpression Id. */
  int UNDERSCORE = 75;
  /** RegularExpression Id. */
  int QUOTE = 76;
  /** RegularExpression Id. */
  int ASTERISK = 77;
  /** RegularExpression Id. */
  int PERIOD = 78;
  /** RegularExpression Id. */
  int DIGIT = 79;
  /** RegularExpression Id. */
  int LEGAL_IDENTIFIER = 80;
  /** RegularExpression Id. */
  int ALPHABET = 81;
  /** RegularExpression Id. */
  int INT_VALUE = 82;
  /** RegularExpression Id. */
  int DATE_VALUE = 83;
  /** RegularExpression Id. */
  int NNNN = 84;
  /** RegularExpression Id. */
  int NN = 85;
  /** RegularExpression Id. */
  int NON_QUOTE_SPECIAL_CHARACTERS = 86;
  /** RegularExpression Id. */
  int NON_QUOTE_CHARACTER = 87;
  /** RegularExpression Id. */
  int CHAR_STRING = 88;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\r\"",
    "\"\\t\"",
    "\"\\n\"",
    "\"exit\"",
    "\"int\"",
    "\"char\"",
    "\"date\"",
    "\"create\"",
    "\"table\"",
    "\"null\"",
    "\"not\"",
    "\"primary\"",
    "\"key\"",
    "\"foreign\"",
    "\"drop\"",
    "\"show\"",
    "\"tables\"",
    "\"is\"",
    "\"references\"",
    "\"desc\"",
    "\"select\"",
    "\"as\"",
    "\"from\"",
    "\"where\"",
    "\"or\"",
    "\"and\"",
    "\"insert\"",
    "\"into\"",
    "\"delete\"",
    "\"values\"",
    "<DELETE_FROM>",
    "<CREATE_TABLE>",
    "<NOT_NULL>",
    "<PRIMARY_KEY>",
    "<FOREIGN_KEY>",
    "<IS_NULL>",
    "<IS_NOT_NULL>",
    "<INSERT_INTO>",
    "<DROP_TABLE>",
    "<SHOW_TABLES>",
    "\"exit\"",
    "\"int\"",
    "\"char\"",
    "\"date\"",
    "\"create\"",
    "\"table\"",
    "\"null\"",
    "\"not\"",
    "\"primary\"",
    "\"key\"",
    "\"foreign\"",
    "\"drop\"",
    "\"show\"",
    "\"tables\"",
    "\"is\"",
    "\"references\"",
    "\"desc\"",
    "\"select\"",
    "\"as\"",
    "\"from\"",
    "\"where\"",
    "\"or\"",
    "\"and\"",
    "\"insert\"",
    "\"into\"",
    "\"delete\"",
    "\"values\"",
    "\";\"",
    "\"(\"",
    "\")\"",
    "<SIGN>",
    "<COMP_OP>",
    "\",\"",
    "\"_\"",
    "\"\\\'\"",
    "\"*\"",
    "\".\"",
    "<DIGIT>",
    "<LEGAL_IDENTIFIER>",
    "<ALPHABET>",
    "<INT_VALUE>",
    "<DATE_VALUE>",
    "<NNNN>",
    "<NN>",
    "<NON_QUOTE_SPECIAL_CHARACTERS>",
    "<NON_QUOTE_CHARACTER>",
    "<CHAR_STRING>",
  };

}