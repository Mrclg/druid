package com.alibaba.druid.sql.dialect.hive.ast;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;

public interface HiveObject extends SQLObject {
    void accept0(HiveASTVisitor visitor);
}
