package com.hahn.basic.target;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.VarGlobal;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.CallFuncStatement;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.IfStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.parser.Node;

public interface ILangFactory {
    public void reset();
    
	public LangBuildTarget LangBuildTarget();
	
	// Registers
	public int getAvailableRegisters();
	public IRegister getNextRegister(AdvancedObject objFor);
	
	// Objects	
	public BasicObject PushObject();
	public StringConst StringConst(String str);
	
	public OPObject OPObject(Statement container, OPCode op, BasicObject p1, BasicObject p2);
	public BasicObject ExpressionStatementObject(ExpressionStatement exp);
	
	public Var VarParameter(Frame frame, String name, Type type);
	public Var VarLocal(Frame frame, String name, Type type);
	public VarGlobal VarGlobal(String name, Type type);
	
	public VarAccess VarAccess(BasicObject var, BasicObject idx, Type type);
	public ExpressionStatement ExpressionStatement(Statement continer, BasicObject obj);
	
	public BasicObject NewInstance(Type type, List<BasicObject> params);
	
	public ConditionalObject ConditionalObject(Statement container, OPCode op, BasicObject p1, BasicObject p2, BasicObject temp);
	
	public FuncHead FuncHead(String name, Node head, Type rtnType, Param[] params);
    public FuncPointer FuncPointer(String name, ParameterizedType<ITypeable> funcType);

    public FuncCallPointer FuncCallPointer(String name, BasicObject[] params);
    
    // Commands    
    public ILangCommand Import(String name);
    
    public Compilable BreakStatement(Frame frame);
    public Compilable ContinueStatement(Frame frame);
    public Compilable ReturnStatement(Statement container, FuncHead returnFrom, BasicObject result);
    
    public IfStatement IfStatement(Statement container, List<Conditional> conditionals);
    public WhileStatement WhileStatement(Statement container, Node conditional, Node body);
    public ForStatement ForStatement(Statement container, Node define, Node condition, List<Node> modification, Node body);
    
	public DefineVarStatement DefineVarStatement(Statement container, boolean ignoreTypeCheck);
	
	public CallFuncStatement CallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
    public CallFuncStatement DefaultCallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
}