package com.hahn.basic.target;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.opcode.OPCode;

public abstract class LangFactory {
    public abstract FuncPointer FuncPointer(String name, ParameterizedType<ITypeable> funcType);

    public abstract FuncCallPointer FuncCallPointer(String name, BasicObject[] params);
    
    public abstract FuncCallPointer FuncDeallocCallPointer(BasicObject var);
    
    public abstract ConditionalObject ConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2);

    public abstract LangBuildTarget LangBuildTarget();
}
