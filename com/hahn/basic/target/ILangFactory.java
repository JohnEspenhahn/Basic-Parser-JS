package com.hahn.basic.target;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.CastedObject;
import com.hahn.basic.intermediate.objects.ClassObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.ExpressionObject;
import com.hahn.basic.intermediate.objects.FuncCallPointer;
import com.hahn.basic.intermediate.objects.FuncPointer;
import com.hahn.basic.intermediate.objects.OPObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.TernaryObject;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.StructType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.CallFuncStatement;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.IfStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.ParamDefaultValStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.parser.Node;

public interface ILangFactory {
	public LangBuildTarget getLangBuildTarget();
	
	// Registers
	public int getAvailableRegisters();
	public IRegister getNextRegister(AdvancedObject objFor);
	
	// Types
    public String createClass(ClassType c);
	
	// Objects	
	public BasicObject PushObject();
	public BasicObject DefaultStruct(StructType struct);
	public StringConst StringConst(String str);
	public ClassObject ClassObject(ClassType classType);
	
	public OPObject OPObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node);
	public OPObject ArithmeticObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node);
	public OPObject ArithmeticSetObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node);
	
	public CastedObject CastedObject(BasicObject obj, Type type, int row, int col);
	public ExpressionObject ExpressionObject(ExpressionStatement exp);
	
	public Var VarParameter(Frame frame, String name, Type type, int flags);
	public Var VarLocal(Frame frame, String name, Type type, int flags);
	
	public Var VarThis(Frame frame, ClassType type);
	public Var VarSuper(Frame frame, ClassType type);
	
	public VarAccess VarAccess(Statement container, BasicObject var, BasicObject idx, Type type, int row, int col);
	
	public BasicObject NewInstance(Type type, Node typeNode, List<BasicObject> params);
	
	public ConditionalObject ConditionalObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node, BasicObject temp);
	public TernaryObject TernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else, int row, int col);
	
	public FuncHead FuncHead(Frame parent, ClassType classIn, String name, boolean rawName, Node head, Type rtnType, Param[] params);
    public FuncPointer FuncPointer(Node nameNode, BasicObject objectIn, ParameterizedType<ITypeable> funcType);
    public FuncCallPointer FuncCallPointer(Node nameNode, BasicObject objectIn, BasicObject[] params);
    
    // Commands    
    public ILangCommand Import(String name);
    
    public Compilable BreakStatement(Frame frame);
    public Compilable ContinueStatement(Frame frame);
    public Compilable ReturnStatement(Statement container, FuncHead returnFrom, BasicObject result);
    
    public ExpressionStatement ExpressionStatement(Statement continer, BasicObject obj);
    
    public IfStatement IfStatement(Statement container, List<Conditional> conditionals);
    public WhileStatement WhileStatement(Statement container, Node conditional, Node body);
    public ForStatement ForStatement(Statement container, Node define, Node condition, List<Node> modification, Node body);
    
	public DefineVarStatement DefineVarStatement(Statement container, boolean ignoreTypeCheck);
	public ParamDefaultValStatement ParamDefaultValStatement(FuncHead func, boolean ignoreTypeCheck);
	
	public CallFuncStatement CallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
    public CallFuncStatement DefaultCallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
}
