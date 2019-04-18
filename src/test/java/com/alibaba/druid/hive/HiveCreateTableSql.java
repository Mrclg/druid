package com.alibaba.druid.hive;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import junit.framework.TestCase;

import java.util.List;

public class HiveCreateTableSql extends TestCase {

    public void testCreateSql(){
        String hiveSql = "CREATE TABLE `wealth_mobility_resl_day`(\n" +
                "  `sell_date` string not null COMMENT '转让日期', \n" +
                "  `sell_loan_amt` double COMMENT '实际到期转让', \n" +
                "  `advance_loan_amt` double COMMENT '提前转让', \n" +
                "  `sell_amt` double COMMENT '转让总额', \n" +
                "  `return_amt` double COMMENT '实际回款金额', \n" +
                "  `advance_return_amt` double COMMENT '提前回款金额')\n" +
                "PARTITIONED BY ( \n" +
                "  `data_date` string)";

        SQLStatementParser sqlStatementParser = new HiveStatementParser(hiveSql);
        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();

        SQLCreateTableStatement sqlCreateTableStatement = null;
        for (SQLStatement sqlStatement : sqlStatements) {
            if (sqlStatement instanceof HiveCreateTableStatement){
                sqlCreateTableStatement = (HiveCreateTableStatement) sqlStatement;

                List<SQLTableElement> tableElementList = sqlCreateTableStatement.getTableElementList();
                for(SQLTableElement tableElement : tableElementList){
                    if(tableElement instanceof SQLColumnDefinition){
                        SQLColumnDefinition columnDefinition = (SQLColumnDefinition) tableElement;
                        System.out.println(columnDefinition.getNameAsString());
                        System.out.println(columnDefinition.getDataType());
                        System.out.println(columnDefinition.getDefaultExpr());
                        System.out.println(columnDefinition.getComment());

                        List<SQLColumnConstraint> constraints = columnDefinition.getConstraints();
                        for(SQLColumnConstraint constraint : constraints){
                            if(constraint instanceof SQLNotNullConstraint){
                                System.out.println(constraint);
                            }
                        }
                    }
                }

                List<SQLColumnDefinition> partitionColumns = sqlCreateTableStatement.getPartitionColumns();
                for(SQLColumnDefinition partitionColumn : partitionColumns){
                    System.out.println(partitionColumn.getNameAsString());
                }
            }
        }
    }


}
