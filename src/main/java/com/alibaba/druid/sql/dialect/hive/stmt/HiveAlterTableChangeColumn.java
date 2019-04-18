package com.alibaba.druid.sql.dialect.hive.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.hive.ast.HiveObjectImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;

public class HiveAlterTableChangeColumn extends HiveObjectImpl implements SQLAlterTableItem {

    private SQLName columnName;

    private SQLColumnDefinition newColumnDefinition;

    private boolean             first;

    private SQLName             firstColumn;
    private SQLName             afterColumn;

    @Override
    public void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columnName);
            acceptChild(visitor, newColumnDefinition);

            acceptChild(visitor, firstColumn);
            acceptChild(visitor, afterColumn);
        }
    }

    public SQLName getColumnName() {
        return columnName;
    }

    public void setColumnName(SQLName columnName) {
        this.columnName = columnName;
    }

    public SQLColumnDefinition getNewColumnDefinition() {
        return newColumnDefinition;
    }

    public void setNewColumnDefinition(SQLColumnDefinition newColumnDefinition) {
        this.newColumnDefinition = newColumnDefinition;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public SQLName getFirstColumn() {
        return firstColumn;
    }

    public void setFirstColumn(SQLName firstColumn) {
        this.firstColumn = firstColumn;
    }

    public SQLName getAfterColumn() {
        return afterColumn;
    }

    public void setAfterColumn(SQLName afterColumn) {
        this.afterColumn = afterColumn;
    }
}
