package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.Statement;

interface IHolderExcludeList {
    public Type getType();
    public void setType(Type t);
    public BasicObject castTo(Type t);
    public ExpressionStatement getAsExp(Statement container);
}
