package com.hahn.basic.target;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarGlobal;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Command;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;

public abstract class LangFactory {
	public abstract LangBuildTarget LangBuildTarget();
	
	// Objects
	public abstract BasicObject PushObject();
	public abstract StringConst StringConst(String str);
	
	public abstract Var VarPointer(AdvancedObject obj);
	public abstract Var VarParameter(Frame frame, String name, Type type);
	public abstract Var VarLocal(Frame frame, String name, Type type);
	public abstract VarGlobal VarGlobal(String name, Type type);
	
	public abstract BasicObject NewInstance(Type type, List<BasicObject> params);
	
	public abstract ConditionalObject ConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2);
	
	public abstract FuncHead FuncHead(String name, Node head, Type rtnType, Param[] params);
    public abstract FuncPointer FuncPointer(String name, ParameterizedType<ITypeable> funcType);

    public abstract FuncCallPointer FuncCallPointer(String name, BasicObject[] params);
    
    // Commands
    public abstract Command Command(Statement container, OPCode op, BasicObject p1, BasicObject p2);
    
    public abstract ILangCommand Import(String name);
    
    public abstract Compilable BreakStatement(Frame frame);
    public abstract Compilable ContinueStatement(Frame frame);
    public abstract Compilable ReturnStatement(Statement container, Frame returnFrom, BasicObject result);
    
    public abstract Compilable IfStatement(Statement container, List<Conditional> conditionals);
    public abstract Compilable WhileStatement(Statement container, Node conditional, Node body);
    public abstract Compilable ForStatement(Statement continer, Node define, Node condition, List<Node> modification, Node body);
    
    public abstract Compilable CallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
    public abstract Compilable DefaultCallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
    
	public abstract Compilable DefineVarStatement(Statement container, BasicObject var, BasicObject value, boolean ignoreTypeCheck);
}
