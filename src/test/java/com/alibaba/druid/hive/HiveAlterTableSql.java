package com.alibaba.druid.hive;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveAlterTableChangeColumn;
import junit.framework.TestCase;

import java.util.List;

public class HiveAlterTableSql extends TestCase {

//    public void testMysql(){
//        String sql = "ALTER TABLE `xxl-sso`.`xxl_sso_user ADD COLUMN `column5` VARCHAR(50) NOT NULL   COMMENT 'add' AFTER `column2`";
//        MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
//        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
//    }


    public void testAlterSQl(){
        String addSql = "ALTER TABLE employee ADD COLUMNS (dept STRING COMMENT 'Department name');";
        String replaceSql = "ALTER TABLE employee REPLACE COLUMNS ( eid INT empid Int, ename STRING name String);";
        String changeSql = "ALTER TABLE test_table CHANGE COLUMN col1 col2 STRING COMMENT 'The datatype of col2 is STRING' after col3;";

        HiveStatementParser sqlStatementParser = new HiveStatementParser(changeSql);

        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
        for (SQLStatement sqlStatement : sqlStatements) {
            if(sqlStatement instanceof SQLAlterTableStatement){
                SQLAlterTableStatement sqlAlterTableStatement = (SQLAlterTableStatement) sqlStatement;
                System.out.println(sqlAlterTableStatement.toString());
                System.out.println("----------------------------------");

                List<SQLAlterTableItem> sqlAlterTableItemList = sqlAlterTableStatement.getItems();
                for(SQLAlterTableItem item : sqlAlterTableItemList){
                    if(item instanceof SQLAlterTableRename) {
                        SQLAlterTableRename rename = (SQLAlterTableRename) item;
                        System.out.println(rename.getTo());         //new_table_name
                        System.out.println(rename.getToName());     //new_table_name

                    }else if(item instanceof SQLAlterTableAddColumn){
                        SQLAlterTableAddColumn addColumn = (SQLAlterTableAddColumn) item;
                        List<SQLColumnDefinition> sqlColumnDefinitionList = addColumn.getColumns();
                        for(SQLColumnDefinition columnDefinition : sqlColumnDefinitionList){
                            System.out.println(columnDefinition.getName());
                            System.out.println(columnDefinition.getDataType());
                            System.out.println(columnDefinition.getComment());
                        }

                    }else if(item instanceof HiveAlterTableChangeColumn){
                        HiveAlterTableChangeColumn changeColumn = (HiveAlterTableChangeColumn) item;
                        SQLColumnDefinition columnDefinition = changeColumn.getNewColumnDefinition();
                        System.out.println(columnDefinition.getName());
                        System.out.println(columnDefinition.getDataType());
                        System.out.println(columnDefinition.getComment());

                    }else if(item instanceof SQLAlterTableAlterColumn){
                        SQLAlterTableAlterColumn alterColumn = (SQLAlterTableAlterColumn) item;
                        SQLColumnDefinition columnDefinition = alterColumn.getColumn();
                        System.out.println(columnDefinition.getName());
                        System.out.println(columnDefinition.getDataType());
                        System.out.println(columnDefinition.getComment());
                    }
                }
            }
        }
    }

}
