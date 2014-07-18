package com.hahn.basic.target;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarGlobal;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Compilable;

public abstract class LangFactory {
	public abstract LangBuildTarget LangBuildTarget();
	
	// Objects
	public abstract StringConst StringConst(String str);
	
	public abstract Var VarPointer(AdvancedObject obj);
	public abstract VarGlobal VarGlobal(String name, Type type);
	
	public abstract ConditionalObject ConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2);
	
    public abstract FuncPointer FuncPointer(String name, ParameterizedType<ITypeable> funcType);

    public abstract FuncCallPointer FuncCallPointer(String name, BasicObject[] params);    
    public abstract FuncCallPointer FuncDeallocCallPointer(BasicObject var);
    
    // Commands

    public abstract ILangCommand Import(String name);
    public abstract Compilable BreakStatement(Frame frame);

    // Helper Functions
    
	public void addPreCode(FuncHead funcHead) { }
}
