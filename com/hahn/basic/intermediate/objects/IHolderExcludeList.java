package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;


interface IHolderExcludeList {
    public Type getType();
    
    public BasicObject getForUse(Statement s);
    public BasicObject castTo(Type t);
}
