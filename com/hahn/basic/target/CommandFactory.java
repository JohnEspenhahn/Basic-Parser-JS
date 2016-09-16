package com.hahn.basic.target;

import java.util.List;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.ArithmeticObject;
import com.hahn.basic.intermediate.objects.Array;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.CastedObject;
import com.hahn.basic.intermediate.objects.ClassObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.EmptyArray;
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
import com.hahn.basic.intermediate.statements.ClassDefinition;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.ExpressionStatement;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.FuncDefStatement;
import com.hahn.basic.intermediate.statements.IfStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.ParamDefaultValStatement;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.js.objects.JSArithmeticSetObject;

public interface CommandFactory {    
    String getOutputExtension();    
    String getEOL();
	
	// Registers
	int getAvailableRegisters();
	IRegister getNextRegister(AdvancedObject objFor);
	
	// OP Codes
	String getTargetOPSymbol(OPCode code);
	
	// Type
	ClassDefinition ClassDefinition(Frame containingFrame, ClassType type);
	
	// Objects	
	IBasicObject PushObject();
	IBasicObject DefaultStruct(StructType struct);
	StringConst StringConst(String str);
	ClassObject ClassObject(ClassType classType);
	
	OPObject OPObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node);
	OPObject PostfixOPObject(Statement container, OPCode op, IBasicObject p, Node pNode);
	ArithmeticObject ArithmeticObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node);
	JSArithmeticSetObject ArithmeticSetObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node);
	
	CastedObject CastedObject(IBasicObject obj, Type type, CodeFile file, int row, int col);
	ExpressionObject ExpressionObject(ExpressionStatement exp);
	
	Var VarParameter(Frame frame, String name, Type type, int flags);
	Var VarLocal(Frame frame, String name, Type type, int flags);
	
	Var VarThis(Frame frame, ClassType type);
	Var VarImpliedThis(Frame frame, ClassType type);
	
	Var VarSuper(Frame frame, ClassType type);
	
	VarAccess VarAccess(Statement container, IBasicObject var, IBasicObject idx, Type type, CodeFile file, int row, int col);
	
	EmptyArray EmptyArray(Node node, ParameterizedType<Type> type, List<IBasicObject> dimensionSizes);
	Array Array(List<IBasicObject> values);
	
	IBasicObject NewInstance(Type type, Node typeNode, List<IBasicObject> params);
	
	ConditionalObject ConditionalObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node, IBasicObject temp);
	TernaryObject TernaryObject(Statement container, IBasicObject condition, Node node_then, Node node_else, CodeFile file, int row, int col);
	
	FuncHead FuncHead(CodeFile file, Frame parent, ClassType classIn, String inName, String outName, Node head, Type rtnType, Param[] params);
    FuncPointer FuncPointer(Node nameNode, IBasicObject objectIn, ParameterizedType<ITypeable> funcType);
    FuncCallPointer FuncCallPointer(Node nameNode, IBasicObject objectIn, IBasicObject[] params);
    
    // Commands    
    Command Import(String name);
    
    Compilable BreakStatement(Frame frame);
    Compilable ContinueStatement(Frame frame);
    Compilable ReturnStatement(Statement container, FuncHead returnFrom, IBasicObject result);
    
    ExpressionStatement ExpressionStatement(Statement continer, IBasicObject obj);
    
    IfStatement IfStatement(Statement container, List<Conditional> conditionals);
    WhileStatement WhileStatement(Statement container, Node conditional, Node body);
    ForStatement ForStatement(Statement container, Node define, Node condition, List<Node> modification, Node body);
    
	DefineVarStatement DefineVarStatement(Statement container, boolean ignoreTypeCheck);
	ParamDefaultValStatement ParamDefaultValStatement(FuncHead func, boolean ignoreTypeCheck);
	
	FuncDefStatement FuncDefStatement(Frame frame, Node nameNode, FuncHead func);
	CallFuncStatement CallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
    CallFuncStatement DefaultCallFuncStatement(Statement container, FuncCallPointer funcCallPointer);
}
