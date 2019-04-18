/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsert;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveAlterTableChangeColumn;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class HiveStatementParser extends SQLStatementParser {
    public HiveStatementParser(String sql) {
        super (new HiveExprParser(sql));
    }

    public HiveStatementParser(String sql, SQLParserFeature... features) {
        super (new HiveExprParser(sql, features));
    }

    public HiveStatementParser(Lexer lexer){
        super(new HiveExprParser(lexer));
    }

    public HiveSelectParser createSQLSelectParser() {
        return new HiveSelectParser(this.exprParser, selectListCache);
    }

    public SQLStatement parseMerge() {
        accept(Token.MERGE);
        accept(Token.INTO);

        SQLReplaceStatement stmt = new SQLReplaceStatement();
        stmt.setDbType(JdbcConstants.HIVE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.token() == Token.KEY) {
            lexer.nextToken();
            accept(Token.LPAREN);
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES || lexer.identifierEquals("VALUE")) {
            lexer.nextToken();

            parseValueClause(stmt.getValuesList(), 0, stmt);
        } else if (lexer.token() == Token.SELECT) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr);
        } else if (lexer.token() == Token.LPAREN) {
            SQLSelect select = this.createSQLSelectParser().select();
            SQLQueryExpr queryExpr = new SQLQueryExpr(select);
            stmt.setQuery(queryExpr);
        }

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new HiveCreateTableParser(this.exprParser);
    }

    public SQLStatement parseInsert() {
        if (lexer.token() == Token.FROM) {
            lexer.nextToken();

            HiveMultiInsertStatement stmt = new HiveMultiInsertStatement();

            if (lexer.token() == Token.IDENTIFIER) {
                SQLName tableName = this.exprParser.name();
                SQLExprTableSource from = new SQLExprTableSource(tableName);
                stmt.setFrom(from);

                if (lexer.token() == Token.IDENTIFIER) {
                    from.setAlias(lexer.stringVal());
                    lexer.nextToken();
                }
            } else {
                accept(Token.LPAREN);

                SQLSelectParser selectParser = createSQLSelectParser();
                SQLSelect select = selectParser.select();

                accept(Token.RPAREN);

                String alias = lexer.stringVal();
                accept(Token.IDENTIFIER);

                SQLSubqueryTableSource from = new SQLSubqueryTableSource(select, alias);

                stmt.setFrom(from);
            }

            for (;;) {
                HiveInsert insert = parseHiveInsert();
                stmt.addItem(insert);

                if (lexer.token() != Token.INSERT) {
                    break;
                }
            }

            return stmt;
        }

        return parseHiveInsertStmt();
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.FROM) {
            SQLStatement stmt = this.parseInsert();
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    public SQLStatement parseAlter() {
        this.accept(Token.ALTER);

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();

            SQLAlterTableStatement stmt = new SQLAlterTableStatement(JdbcConstants.HIVE);
            stmt.setName(this.exprParser.name());

            for (; ; ) {
                if (lexer.token() == Token.DROP) {

                }else if (lexer.identifierEquals("ADD")) {
                    lexer.nextToken();

                    if (lexer.token() == Token.IDENTIFIER) {
                        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                        stmt.addItem(item);

                    }else if(lexer.token() == Token.COLUMN){
                        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                        stmt.addItem(item);
                    }

                }else if(lexer.token() == Token.REPLACE){

                }else if(lexer.identifierEquals("CHANGE")){
                    lexer.nextToken();

                    if (lexer.token() == Token.IDENTIFIER) {
                        HiveAlterTableChangeColumn item = parseAlterTableChangeColumn();
                        stmt.addItem(item);

                    }else if(lexer.token() == Token.COLUMN){
                        HiveAlterTableChangeColumn item = parseAlterTableChangeColumn();
                        stmt.addItem(item);
                    }

                }else{
                    break;
                }
            }

            return stmt;
        }
        throw new ParserException("TODO " + lexer.info());
    }

    protected HiveAlterTableChangeColumn parseAlterTableChangeColumn(){
        if(lexer.token() == Token.COLUMN){
            lexer.nextToken();
        }

        HiveAlterTableChangeColumn item = new HiveAlterTableChangeColumn();
        item.setColumnName(this.exprParser.name());
        item.setNewColumnDefinition(this.exprParser.parseColumn());

        if (lexer.identifierEquals("AFTER")) {
            lexer.nextToken();
            item.setAfterColumn(this.exprParser.name());
        } else if (lexer.identifierEquals("FIRST")) {
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER) {
                item.setFirstColumn(this.exprParser.name());
            } else {
                item.setFirst(true);
            }
        }
        return item;
    }

    protected SQLAlterTableAddColumn parseAlterTableAddColumn() {
        acceptIdentifier("COLUMNS");
        accept(Token.LPAREN);

        SQLAlterTableAddColumn item = new SQLAlterTableAddColumn();

        for (;;) {
            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.addColumn(columnDef);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                if (lexer.identifierEquals("ADD")) {
                    break;
                }
                continue;
            }
            break;
        }

        accept(Token.RPAREN);

        return item;
    }

}
