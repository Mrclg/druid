package com.alibaba.druid.mysql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

import java.util.List;

public class MysqlAlterTableSql extends TestCase {

    public void testAlterSQl(){
        String addSql = "ALTER TABLE `xxl-sso`.`xxl_sso_user` CHANGE `column1` `column1` INT(11) NOT NULL   COMMENT 'upd'";

        SQLStatementParser sqlStatementParser = new MySqlStatementParser(addSql);
        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
        for (SQLStatement sqlStatement : sqlStatements) {
            if(sqlStatement instanceof SQLAlterTableStatement){
                SQLAlterTableStatement sqlAlterTableStatement = (SQLAlterTableStatement) sqlStatement;
                System.out.println(sqlAlterTableStatement.toString());
                System.out.println("----------------------------------");

                List<SQLAlterTableItem> sqlAlterTableItemList = sqlAlterTableStatement.getItems();
                for(SQLAlterTableItem item : sqlAlterTableItemList){
                    if(item instanceof MySqlAlterTableChangeColumn){
                        MySqlAlterTableChangeColumn changeColumn = (MySqlAlterTableChangeColumn) item;

                        SQLColumnDefinition columnDefinition = changeColumn.getNewColumnDefinition();
                        System.out.println(columnDefinition.getName());
                        System.out.println(columnDefinition.getDataType());
                        System.out.println(columnDefinition.getComment());

                        System.out.println("----------------------------------");

                    }
                }
            }
        }
    }
}
