/* Generated By:JavaCC: Do not edit this line. DBMSParser.java */
package db;

import java.io.InputStream;
import java.io.PrintStream;
import dataobject.*;
import dataobject.Column.*;
import java.util.ArrayList;
import com.sleepycat.je.*;
import dataobject.Table.*;
import dataobject.Condition.*;
import dataobject.Record.*;
import db.BerkeleyDBHelper.DeleteParam;
import db.BerkeleyDBHelper.InsertParam;
import db.BerkeleyDBHelper.SelectParam;
import db.BerkeleyDBHelper.TableReference;

public class DBMSParser implements DBMSParserConstants {
        private PrintStream ps = null;

        public static void main(String args []) throws ParseException
        {
                DBMSParser.run(System.in, System.out, false);
        }

        public void setPS(PrintStream ps)
        {
                this.ps = ps;
        }

        public static void run(InputStream is, PrintStream ps, boolean isTest)
        {
                DBMSParser parser = new DBMSParser(is);
                parser.setPS(ps);

                BerkeleyDBHelper.getInstance().openDB();

                if( ! isTest)
                        ps.print(Constant.PROMPT);
                if(isTest)
                        BerkeleyDBHelper.getInstance().setPS(ps);
                while (true)
                {
                        try
                        {
                                parser.command();
                        }
                        catch(EnvironmentLockedException e)
                        {
                                ps.println("EnvironmentLockedException occured. Perhaps didn't exit properly. Delete db/*.");
                                parser.ReInit(is);
                        }
                        catch(ParseException e)
                        {
                                parser.printMessage(Constant.PRINT_SYNTAX_ERROR);
                                parser.ReInit(is); // 뼈대 코드에서 SimpleDBMSParser로 되어있는 부분을, non static function 이므로 인스턴스 parser로 변경                        }
                        catch(Exception e)
                        {
                                e.printStackTrace();
                        }

                        if(isTest)
                        {
                                break;
                        }
                }
        }

        public void printMessage(int q)
        {
                // Do nothing since project 2 except Syntax Error                if(q != Constant.PRINT_SYNTAX_ERROR)
                        return;

                ps.print(Constant.PROMPT);
                switch (q)
                {
                case Constant.PRINT_SYNTAX_ERROR :
                        ps.println(Constant.STR_SYNTAX_ERROR);
                        break;
                /*		case Constant.PRINT_CREATE_TABLE : 			ps.println(Constant.STR_CREATE_TABLE);			break;		case Constant.PRINT_DROP_TABLE :			ps.println(Constant.STR_DROP_TABLE);			break;		case Constant.PRINT_DESC :			ps.println(Constant.STR_DESC);			break;		case Constant.PRINT_SHOW_TABLES :			ps.println(Constant.STR_SHOW_TABLES);			break;		case Constant.PRINT_INSERT :			ps.println(Constant.STR_INSERT);			break;		case Constant.PRINT_DELETE :			ps.println(Constant.STR_DELETE);			break;		case Constant.PRINT_SELECT :			ps.println(Constant.STR_SELECT);			break;		*/
                default :
                        break;
                }
        }

  final public void command() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DESC:
    case SELECT:
    case DELETE_FROM:
    case CREATE_TABLE:
    case INSERT_INTO:
    case DROP_TABLE:
      queryList();
      break;
    case EXIT:
      jj_consume_token(EXIT);
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().closeDB();
                        System.exit(0);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void queryList() throws ParseException {
    label_1:
    while (true) {
      query();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DESC:
      case SELECT:
      case DELETE_FROM:
      case CREATE_TABLE:
      case INSERT_INTO:
      case DROP_TABLE:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
    }
  }

  final public void query() throws ParseException {
        Table table;
        ArrayList<String> nameList;
        SelectParam selectParam;
        InsertParam insertParam;
        DeleteParam deleteParam;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE_TABLE:
      table = createTableQuery();
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().createTable(table);
      break;
    case DROP_TABLE:
      nameList = dropTableQuery();
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().dropTable(nameList);
      break;
    case DESC:
      nameList = descQuery();
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().desc(nameList);
      break;
    case SELECT:
      selectParam = selectQuery();
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().select(selectParam.scList,
                                                                                                selectParam.tableList,
                                                                                                selectParam.cond);
      break;
    case INSERT_INTO:
      insertParam = insertQuery();
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().insert(insertParam.tableName,
                                                                                                insertParam.columnNameList,
                                                                                                insertParam.valueList);
      break;
    case DELETE_FROM:
      deleteParam = deleteQuery();
      jj_consume_token(SEMICOLON);
                        BerkeleyDBHelper.getInstance().delete(deleteParam.tableName,
                                                                                                deleteParam.cond);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public Table createTableQuery() throws ParseException {
        Table table;
        String tableName;
    jj_consume_token(CREATE_TABLE);
    tableName = tableName();
                table = new Table(tableName);
    tableElementList(table);
                {if (true) return table;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<String> dropTableQuery() throws ParseException {
        ArrayList<String> arr;
    jj_consume_token(DROP_TABLE);
    arr = tableNameList();
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<String> descQuery() throws ParseException {
        ArrayList<String> arr;
    jj_consume_token(DESC);
    arr = tableNameList();
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public void showTablesQuery() throws ParseException {
    jj_consume_token(SHOW_TABLES);
  }

  final public SelectParam selectQuery() throws ParseException {
        SelectParam param = new SelectParam();
    jj_consume_token(SELECT);
    param.scList = selectList();
    tableExpression(param);
                {if (true) return param;}
    throw new Error("Missing return statement in function");
  }

  final public InsertParam insertQuery() throws ParseException {
        InsertParam param = new InsertParam();
    jj_consume_token(INSERT_INTO);
    param.tableName = tableName();
    insertColumnsAndSource(param);
                {if (true) return param;}
    throw new Error("Missing return statement in function");
  }

  final public DeleteParam deleteQuery() throws ParseException {
        DeleteParam param = new DeleteParam();
        param.cond = null;
    jj_consume_token(DELETE_FROM);
    param.tableName = tableName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      param.cond = whereClause();
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
                {if (true) return param;}
    throw new Error("Missing return statement in function");
  }

/*** Insert / Delete*/
  final public void insertColumnsAndSource(InsertParam param) throws ParseException {
        // columnNameList가 없으면 null로 처리하도록         param.columnNameList = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEFT_PAREN:
      param.columnNameList = columnNameList();
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    param.valueList = valueList();
  }

  final public ArrayList<Value> valueList() throws ParseException {
        ArrayList<Value> arr = new ArrayList<Value>();
        Value val;
    jj_consume_token(VALUES);
    jj_consume_token(LEFT_PAREN);
    val = value();
                arr.add(val);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_2;
      }
      jj_consume_token(COMMA);
      val = value();
                        arr.add(val);
    }
    jj_consume_token(RIGHT_PAREN);
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public Value value() throws ParseException {
        Value val;
        _ConstantOperand op;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NULL:
      jj_consume_token(NULL);
                val = new NullValue();
      break;
    case INT_VALUE:
    case DATE_VALUE:
    case CHAR_STRING:
      op = comparableValue();
                val = op.convertToValue();
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                {if (true) return val;}
    throw new Error("Missing return statement in function");
  }

/*** Select*/
  final public ArrayList<SelectedColumn> selectList() throws ParseException {
        ArrayList<SelectedColumn> scList = new ArrayList<SelectedColumn>();
        SelectedColumn sc;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ASTERISK:
      jj_consume_token(ASTERISK);
                {if (true) return null;}
      break;
    case LEGAL_IDENTIFIER:
      sc = selectedColumn();
                        scList.add(sc);
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_3;
        }
        jj_consume_token(COMMA);
        sc = selectedColumn();
                                scList.add(sc);
      }
                {if (true) return scList;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public SelectedColumn selectedColumn() throws ParseException {
        SelectedColumn sc;
        _ColumnOperand op;
        String colAlias = null;
    op = column();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      colAlias = columnName();
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
                sc = new SelectedColumn(op.tableName, op.colName, colAlias);
                {if (true) return sc;}
    throw new Error("Missing return statement in function");
  }

  final public void tableExpression(SelectParam param) throws ParseException {
    param.tableList = fromClause();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      param.cond = whereClause();
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
  }

  final public ArrayList<TableReference> fromClause() throws ParseException {
        ArrayList<TableReference> arr;
    jj_consume_token(FROM);
    arr = tableReferenceList();
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<TableReference> tableReferenceList() throws ParseException {
        ArrayList<TableReference> arr = new ArrayList<TableReference>();
        TableReference tr;
    tr = referedTable();
                arr.add(tr);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[11] = jj_gen;
        break label_4;
      }
      jj_consume_token(COMMA);
      tr = referedTable();
                        arr.add(tr);
    }
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public TableReference referedTable() throws ParseException {
        TableReference tr = new TableReference();
    tr.tableName = tableName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      tr.alias = tableName();
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
                {if (true) return tr;}
    throw new Error("Missing return statement in function");
  }

  final public _Condition whereClause() throws ParseException {
        _Condition cond;
    jj_consume_token(WHERE);
    cond = booleanValueExpression();
                {if (true) return cond;}
    throw new Error("Missing return statement in function");
  }

// OR과 AND의 순서 : http://stackoverflow.com/questions/16805630/and-or-order-of-operations// 보통 AND를 먼저 evaluate  final public _Condition booleanValueExpression() throws ParseException {
        _OrCondition orCond = new _OrCondition();
        _Condition cond;
    cond = booleanTerm();
                orCond.orList.add(cond);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_5;
      }
      jj_consume_token(OR);
      cond = booleanTerm();
                        orCond.orList.add(cond);
    }
                {if (true) return orCond;}
    throw new Error("Missing return statement in function");
  }

  final public _Condition booleanTerm() throws ParseException {
        _AndCondition andCond = new _AndCondition();
        _Condition cond;
    cond = booleanFactor();
                andCond.andList.add(cond);
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[14] = jj_gen;
        break label_6;
      }
      jj_consume_token(AND);
      cond = booleanFactor();
                        andCond.andList.add(cond);
    }
                {if (true) return andCond;}
    throw new Error("Missing return statement in function");
  }

  final public _Condition booleanFactor() throws ParseException {
        boolean isNot = false;
        _Condition cond;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
                        isNot = true;
      break;
    default:
      jj_la1[15] = jj_gen;
      ;
    }
    cond = booleanTest();
                if(isNot)
                        cond.isNot = true;

                {if (true) return cond;}
    throw new Error("Missing return statement in function");
  }

  final public _Condition booleanTest() throws ParseException {
        _Condition cond;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
    case INT_VALUE:
    case DATE_VALUE:
    case CHAR_STRING:
      cond = predicate();
      break;
    case LEFT_PAREN:
      cond = parenthesizedBooleanExpression();
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                {if (true) return cond;}
    throw new Error("Missing return statement in function");
  }

  final public _Condition parenthesizedBooleanExpression() throws ParseException {
        _Condition cond;
    jj_consume_token(LEFT_PAREN);
    cond = booleanValueExpression();
    jj_consume_token(RIGHT_PAREN);
                {if (true) return cond;}
    throw new Error("Missing return statement in function");
  }

  final public _Condition predicate() throws ParseException {
        _Condition cond;

        _Operand left, right;
        Token o;
        boolean isNull;
    left = compOperand();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMP_OP:
      o = jj_consume_token(COMP_OP);
      right = compOperand();
                        String operator = o.image;
                        cond = new _ComparisonCondition(left, operator, right);
      break;
    case IS_NULL:
    case IS_NOT_NULL:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IS_NULL:
        jj_consume_token(IS_NULL);
                                        isNull = true;
        break;
      case IS_NOT_NULL:
        jj_consume_token(IS_NOT_NULL);
                                        isNull = false;
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                                cond = new _NullCondition(left, isNull);
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                {if (true) return cond;}
    throw new Error("Missing return statement in function");
  }

  final public _Operand compOperand() throws ParseException {
        _Operand op;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case DATE_VALUE:
    case CHAR_STRING:
      op = comparableValue();
      break;
    case LEGAL_IDENTIFIER:
      op = column();
      break;
    default:
      jj_la1[19] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                {if (true) return op;}
    throw new Error("Missing return statement in function");
  }

  final public _ColumnOperand column() throws ParseException {
        _ColumnOperand op = new _ColumnOperand();
    if (jj_2_1(2)) {
      op.tableName = tableName();
      jj_consume_token(PERIOD);
    } else {
      ;
    }
    op.colName = columnName();
                {if (true) return op;}
    throw new Error("Missing return statement in function");
  }

  final public _ConstantOperand comparableValue() throws ParseException {
        _ConstantOperand op = new _ConstantOperand();
        Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
      t = jj_consume_token(INT_VALUE);
                        op.pType = PRIMITIVE_TYPE.INT;
                        op.iValue = Integer.parseInt(t.image);
      break;
    case CHAR_STRING:
      t = jj_consume_token(CHAR_STRING);
                        op.pType = PRIMITIVE_TYPE.CHAR;
                        op.sValue = t.image;
                        op.sValue = op.sValue.substring(1, op.sValue.length()-1);
      break;
    case DATE_VALUE:
      t = jj_consume_token(DATE_VALUE);
                        op.pType = PRIMITIVE_TYPE.DATE;
                        op.sValue = t.image;
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                {if (true) return op;}
    throw new Error("Missing return statement in function");
  }

/*** Create Table, Drop Table, Desc*/
  final public ArrayList<String> tableNameList() throws ParseException {
        ArrayList<String> arr = new ArrayList<String>();
        String name;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ASTERISK:
      jj_consume_token(ASTERISK);
                // It means Asterisk, BerkeleyDBHelper에서 *로 처리함
                arr = null;
      break;
    case LEGAL_IDENTIFIER:
      name = tableName();
                arr.add(name);
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[21] = jj_gen;
          break label_7;
        }
        jj_consume_token(COMMA);
        name = tableName();
                        arr.add(name);
      }
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public void tableElementList(Table table) throws ParseException {
    jj_consume_token(LEFT_PAREN);
    tableElement(table);
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[23] = jj_gen;
        break label_8;
      }
      jj_consume_token(COMMA);
      tableElement(table);
    }
    jj_consume_token(RIGHT_PAREN);
  }

  final public void tableElement(Table table) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      columnDefinition(table);
      break;
    case PRIMARY_KEY:
    case FOREIGN_KEY:
      tableConstraintDefinition(table);
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void columnDefinition(Table table) throws ParseException {
        Token t;
        String columnName;
        Type columnType;
        boolean isNotNull = false;
    columnName = columnName();
    columnType = dataType();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT_NULL:
      jj_consume_token(NOT_NULL);
                        isNotNull = true;
      break;
    default:
      jj_la1[25] = jj_gen;
      ;
    }
                Column c = new Column(columnName, columnType, isNotNull);
                table.addColumn(c);
  }

  final public void tableConstraintDefinition(Table table) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRIMARY_KEY:
      primaryKeyConstraint(table);
      break;
    case FOREIGN_KEY:
      referentialConstraint(table);
      break;
    default:
      jj_la1[26] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void primaryKeyConstraint(Table table) throws ParseException {
        ArrayList<String> columnList;
    jj_consume_token(PRIMARY_KEY);
    columnList = columnNameList();
                table.setPrimaryKey(columnList);
  }

  final public void referentialConstraint(Table table) throws ParseException {
        ArrayList<String> columnList;
        ArrayList<String> rColumnList;
        String rTableName;
    jj_consume_token(FOREIGN_KEY);
    columnList = columnNameList();
    jj_consume_token(REFERENCES);
    rTableName = tableName();
    rColumnList = columnNameList();
                FK fk = new FK(table.getName(), columnList, rTableName, rColumnList);
                table.addForeignKey(fk);
  }

  final public ArrayList<String> columnNameList() throws ParseException {
        ArrayList<String> arr = new ArrayList<String>();
        String name;
    jj_consume_token(LEFT_PAREN);
    name = columnName();
                arr.add(name);
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[27] = jj_gen;
        break label_9;
      }
      jj_consume_token(COMMA);
      name = columnName();
                        arr.add(name);
    }
    jj_consume_token(RIGHT_PAREN);
                {if (true) return arr;}
    throw new Error("Missing return statement in function");
  }

  final public Type dataType() throws ParseException {
        Token t;
        Token l = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      t = jj_consume_token(INT);
      break;
    case CHAR:
      t = jj_consume_token(CHAR);
      jj_consume_token(LEFT_PAREN);
      l = jj_consume_token(INT_VALUE);
      jj_consume_token(RIGHT_PAREN);
      break;
    case DATE:
      t = jj_consume_token(DATE);
      break;
    default:
      jj_la1[28] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                Type type;
                if("int".equals(t.image))
                        type = new Type(PRIMITIVE_TYPE.INT, 0);
                else if("date".equals(t.image))
                        type = new Type(PRIMITIVE_TYPE.DATE, 0);
                else
                {
                        int length = Integer.parseInt(l.image);
                        type = new Type(PRIMITIVE_TYPE.CHAR, length);
                }
                {if (true) return type;}
    throw new Error("Missing return statement in function");
  }

  final public String tableName() throws ParseException {
        Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
                {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final public String columnName() throws ParseException {
        Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
                {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(LEGAL_IDENTIFIER)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_10()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public DBMSParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[29];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
      jj_la1_init_2();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x600020,0x600000,0x600000,0x2000000,0x0,0x0,0x800,0x0,0x0,0x800000,0x2000000,0x0,0x800000,0x4000000,0x8000000,0x1000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1c0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x183,0x183,0x183,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x60,0x60,0x0,0x0,0x0,0x0,0x0,0x18,0x4,0x18,0x0,0x0,};
   }
   private static void jj_la1_init_2() {
      jj_la1_2 = new int[] {0x0,0x0,0x0,0x0,0x40,0x400,0x10c0000,0x400,0x12000,0x0,0x0,0x400,0x0,0x0,0x0,0x0,0x10d0040,0x0,0x200,0x10d0000,0x10c0000,0x400,0x12000,0x400,0x10000,0x0,0x0,0x400,0x0,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public DBMSParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public DBMSParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new DBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public DBMSParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new DBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public DBMSParser(DBMSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(DBMSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 29; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[89];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 29; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 89; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
