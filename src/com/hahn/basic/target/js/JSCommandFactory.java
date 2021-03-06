package com.hahn.basic.target.js;

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
import com.hahn.basic.intermediate.objects.PostfixOPObject;
import com.hahn.basic.intermediate.objects.StringConst;
import com.hahn.basic.intermediate.objects.TernaryObject;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.objects.VarAccess;
import com.hahn.basic.intermediate.objects.VarImpliedThis;
import com.hahn.basic.intermediate.objects.VarLocal;
import com.hahn.basic.intermediate.objects.VarParameter;
import com.hahn.basic.intermediate.objects.VarThis;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.register.SimpleRegisterFactory;
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
import com.hahn.basic.target.Command;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.js.function.JSFuncHead;
import com.hahn.basic.target.js.objects.JSArithmeticObject;
import com.hahn.basic.target.js.objects.JSArithmeticSetObject;
import com.hahn.basic.target.js.objects.JSArray;
import com.hahn.basic.target.js.objects.JSCastedObject;
import com.hahn.basic.target.js.objects.JSClassObject;
import com.hahn.basic.target.js.objects.JSConditionalObject;
import com.hahn.basic.target.js.objects.JSDefaultStruct;
import com.hahn.basic.target.js.objects.JSEmptyArray;
import com.hahn.basic.target.js.objects.JSExpressionObject;
import com.hahn.basic.target.js.objects.JSFuncCallPointer;
import com.hahn.basic.target.js.objects.JSFuncPointer;
import com.hahn.basic.target.js.objects.JSNewInstance;
import com.hahn.basic.target.js.objects.JSStringConst;
import com.hahn.basic.target.js.objects.JSTernaryObject;
import com.hahn.basic.target.js.objects.JSVarAccess;
import com.hahn.basic.target.js.objects.JSVarSuper;
import com.hahn.basic.target.js.statements.JSBreakStatement;
import com.hahn.basic.target.js.statements.JSCallFuncStatement;
import com.hahn.basic.target.js.statements.JSClassDefinition;
import com.hahn.basic.target.js.statements.JSContinueStatement;
import com.hahn.basic.target.js.statements.JSDefaultCallFuncStatement;
import com.hahn.basic.target.js.statements.JSDefineVarStatement;
import com.hahn.basic.target.js.statements.JSExpressionStatement;
import com.hahn.basic.target.js.statements.JSForStatement;
import com.hahn.basic.target.js.statements.JSIfStatement;
import com.hahn.basic.target.js.statements.JSParamDefaultValStatement;
import com.hahn.basic.target.js.statements.JSReturnStatement;
import com.hahn.basic.target.js.statements.JSWhileStatement;
import com.hahn.basic.util.exceptions.UnimplementedException;

public class JSCommandFactory implements CommandFactory {
    private SimpleRegisterFactory registerFactory;
    
    public JSCommandFactory() {
        registerFactory = new SimpleRegisterFactory();
    }
    
    @Override
    public String getOutputExtension() {
        return "js";
    }
    
    @Override
    public String getEOL() {
        return ";";
    }
    
    @Override
    public IRegister getNextRegister(AdvancedObject objFor) {
        return registerFactory.getForObject(objFor);
    }
    
    @Override
    public int getAvailableRegisters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public String getTargetOPSymbol(OPCode code) {
        switch (code) {
            case INT: return "~~";
            default: return code.symbol;
        }
    }
    
    @Override
    public ClassDefinition ClassDefinition(Frame containingFrame, ClassType type) {
        return new JSClassDefinition(containingFrame, type);
    }
    
    @Override
    public IBasicObject PushObject() {
        throw new UnimplementedException();
    }
    
    @Override
    public IBasicObject DefaultStruct(StructType struct) {
        return new JSDefaultStruct(struct);
    }
    
    @Override
    public StringConst StringConst(String str) {
        return new JSStringConst(str);
    }
    
    @Override
    public ClassObject ClassObject(ClassType classType) {
        return new JSClassObject(classType);
    }
    
    @Override
    public OPObject OPObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node) {
        return new OPObject(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public OPObject PostfixOPObject(Statement container, OPCode op, IBasicObject p, Node pNode) {
        return new PostfixOPObject(container, op, p, pNode);
    }
    
    @Override
    public ArithmeticObject ArithmeticObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node) {
        return new JSArithmeticObject(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public JSArithmeticSetObject ArithmeticSetObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node) {
        return new JSArithmeticSetObject(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public CastedObject CastedObject(IBasicObject obj, Type type, CodeFile file, int row, int col) {
        return new JSCastedObject(obj, type, file, row, col);
    }
    
    @Override
    public ExpressionObject ExpressionObject(ExpressionStatement exp) {
        return new JSExpressionObject(exp);
    }
    
    @Override
    public Var VarParameter(Frame frame, String name, Type type, int flags) {
        return new VarParameter(frame, name, type, flags);
    }
    
    @Override
    public Var VarLocal(Frame frame, String name, Type type, int flags) {
        return new VarLocal(frame, name, type, flags);
    }
    
    @Override
    public VarAccess VarAccess(Statement container, IBasicObject var, IBasicObject idx, Type type, CodeFile file, int row, int col) {
        return new JSVarAccess(container, var, idx, type, file, row, col);
    }
    
    @Override
    public Var VarThis(Frame frame, ClassType type) {
        return new VarThis(frame, type);
    }
    
    @Override
    public Var VarImpliedThis(Frame frame, ClassType type) {
        return new VarImpliedThis(frame, type);
    }
    
    @Override
    public Var VarSuper(Frame frame, ClassType type) {
        return new JSVarSuper(frame, type);
    }
    
    @Override
    public EmptyArray EmptyArray(Node node, ParameterizedType<Type> type, List<IBasicObject> dimensionSizes) {
        return new JSEmptyArray(node, type, dimensionSizes);
    }
    
    @Override
    public Array Array(Statement container, List<IBasicObject> values) {
        return new JSArray(container, values);
    }
    
    @Override
    public IBasicObject NewInstance(Type type, Node typeNode, List<IBasicObject> params) {
        return new JSNewInstance(type, typeNode, params);
    }
    
    @Override
    public ConditionalObject ConditionalObject(Statement container, OPCode op, IBasicObject p1, Node p1Node, IBasicObject p2, Node p2Node, IBasicObject temp) {
        return new JSConditionalObject(container, op, p1, p1Node, p2, p2Node, temp);
    }
    
    @Override
    public TernaryObject TernaryObject(Statement container, IBasicObject condition, Node node_then, Node node_else, CodeFile file, int row, int col) {
        return new JSTernaryObject(container, condition, node_then, node_else, file, row, col);
    }
    
    @Override
    public FuncHead FuncHead(CodeFile file, Frame parent, ClassType classIn, String inName, String outName, Node head, Type rtnType, Param[] params) {
        return new JSFuncHead(file, parent, classIn, inName, outName, head, rtnType, params);
    }
    
    @Override
    public FuncPointer FuncPointer(Node nameNode, IBasicObject objectIn, ParameterizedType<ITypeable> funcType) {
        return new JSFuncPointer(nameNode, objectIn, funcType);
    }
    
    @Override
    public FuncCallPointer FuncCallPointer(Node nameNode, IBasicObject objectIn, IBasicObject[] params) {
        return new JSFuncCallPointer(nameNode, objectIn, params);
    }
    
    @Override
    public Command Import(String name) {
        throw new UnimplementedException();
    }
    
    @Override
    public ExpressionStatement ExpressionStatement(Statement container, IBasicObject obj) {
        return new JSExpressionStatement(container, obj);
    }
    
    @Override
    public Compilable BreakStatement(Frame frame) {
        return new JSBreakStatement(frame);
    }
    
    @Override
    public Compilable ContinueStatement(Frame frame) {
        return new JSContinueStatement(frame);
    }
    
    @Override
    public Compilable ReturnStatement(Statement container, FuncHead returnFrom, IBasicObject result) {
        return new JSReturnStatement(container, returnFrom, result);
    }
    
    @Override
    public IfStatement IfStatement(Statement container, List<Conditional> conditionals) {
        return new JSIfStatement(container, conditionals);
    }
    
    @Override
    public WhileStatement WhileStatement(Statement container, Node conditional, Node body) {
        return new JSWhileStatement(container, conditional, body);
    }
    
    @Override
    public ForStatement ForStatement(Statement container, Node define, Node condition, List<Node> modification, Node body) {
        return new JSForStatement(container, define, condition, modification, body);
    }
    
    @Override
    public FuncDefStatement FuncDefStatement(Frame frame, Node nameNode, FuncHead func) {
        return new FuncDefStatement(frame, nameNode, func);
    }
    
    @Override
    public CallFuncStatement CallFuncStatement(Statement container, FuncCallPointer funcCallPointer) {
        return new JSCallFuncStatement(container, funcCallPointer);
    }
    
    @Override
    public CallFuncStatement DefaultCallFuncStatement(Statement container, FuncCallPointer funcCallPointer) {
        return new JSDefaultCallFuncStatement(container, funcCallPointer);
    }
    
    @Override
    public DefineVarStatement DefineVarStatement(Statement container, boolean ignoreTypeCheck) {
        return new JSDefineVarStatement(container, ignoreTypeCheck);
    }
    
    @Override
    public ParamDefaultValStatement ParamDefaultValStatement(FuncHead func, boolean ignoreTypeCheck) {
        return new JSParamDefaultValStatement(func, ignoreTypeCheck);
    }    
}
