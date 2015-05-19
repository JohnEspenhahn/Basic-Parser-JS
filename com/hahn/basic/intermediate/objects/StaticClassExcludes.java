package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.StructType;
import com.hahn.basic.intermediate.objects.types.StructType.StructParam;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public interface StaticClassExcludes {
    StructType getAsStruct();
    
    boolean doesExtend(Type t);
    
    StructParam getParam(Node nameNode);
    StructParam getParamSafe(String name);
    StructParam getParam(String name, boolean requireUnique, boolean safe, Node throwNode);
    
    FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types);
    FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types, boolean safe, boolean shallow);
}