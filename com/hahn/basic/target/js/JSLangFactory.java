package com.hahn.basic.target.js;

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
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.ILangFactory;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.objects.JSConditionalObject;
import com.hahn.basic.target.js.objects.JSExpressionStatementObject;
import com.hahn.basic.target.js.objects.JSFuncCallPointer;
import com.hahn.basic.target.js.objects.JSFuncHead;
import com.hahn.basic.target.js.objects.JSFuncPointer;
import com.hahn.basic.target.js.objects.JSNewInstance;
import com.hahn.basic.target.js.objects.JSOPObject;
import com.hahn.basic.target.js.objects.JSStringConst;
import com.hahn.basic.target.js.objects.JSVarAccess;
import com.hahn.basic.target.js.objects.JSVarGlobal;
import com.hahn.basic.target.js.objects.JSVarLocal;
import com.hahn.basic.target.js.objects.JSVarParameter;
import com.hahn.basic.target.js.objects.register.JSRegister;
import com.hahn.basic.target.js.statements.JSBreakStatement;
import com.hahn.basic.target.js.statements.JSCallFuncStatement;
import com.hahn.basic.target.js.statements.JSContinueStatement;
import com.hahn.basic.target.js.statements.JSDefaultCallFuncStatement;
import com.hahn.basic.target.js.statements.JSDefineVarStatement;
import com.hahn.basic.target.js.statements.JSExpressionStatement;
import com.hahn.basic.target.js.statements.JSForStatement;
import com.hahn.basic.target.js.statements.JSIfStatement;
import com.hahn.basic.target.js.statements.JSReturnStatement;
import com.hahn.basic.target.js.statements.JSWhileStatement;
import com.hahn.basic.util.exceptions.UnimplementedException;

public class JSLangFactory implements ILangFactory {
    
    @Override
    public void reset() {
        // TODO reset JS factory
    }
    
    @Override
    public LangBuildTarget LangBuildTarget() {
        return new JSBuildTarget();
    }
    
    @Override
    public IRegister getNextRegister(AdvancedObject objFor) {
        return JSRegister.getForObject(objFor);
    }
    
    @Override
    public int getAvailableRegisters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public BasicObject PushObject() {
        throw new UnimplementedException();
    }
    
    @Override
    public StringConst StringConst(String str) {
        return new JSStringConst(str);
    }
    
    @Override
    public OPObject OPObject(Statement container, OPCode op, BasicObject p1, BasicObject p2) {
        return new JSOPObject(container, op, p1, p2);
    }
    
    @Override
    public BasicObject ExpressionStatementObject(ExpressionStatement exp) {
        return new JSExpressionStatementObject(exp);
    }
    
    @Override
    public Var VarParameter(Frame frame, String name, Type type) {
        return new JSVarParameter(frame, name, type);
    }
    
    @Override
    public Var VarLocal(Frame frame, String name, Type type) {
        return new JSVarLocal(frame, name, type);
    }
    
    @Override
    public VarGlobal VarGlobal(String name, Type type) {
        return new JSVarGlobal(name, type);
    }
    
    @Override
    public VarAccess VarAccess(BasicObject var, BasicObject idx, Type type) {
        return new JSVarAccess(var, idx, type);
    }
    
    @Override
    public BasicObject NewInstance(Type type, List<BasicObject> params) {
        return new JSNewInstance(type, params);
    }
    
    @Override
    public ConditionalObject ConditionalObject(Statement container, OPCode op, BasicObject p1, BasicObject p2, BasicObject temp) {
        return new JSConditionalObject(container, op, p1, p2, temp);
    }
    
    @Override
    public FuncHead FuncHead(String name, Node head, Type rtnType, Param[] params) {
        return new JSFuncHead(name, head, rtnType, params);
    }
    
    @Override
    public FuncPointer FuncPointer(String name, ParameterizedType<ITypeable> funcType) {
        return new JSFuncPointer(name, funcType);
    }
    
    @Override
    public FuncCallPointer FuncCallPointer(String name, BasicObject[] params) {
        return new JSFuncCallPointer(name, params);
    }
    
    @Override
    public ILangCommand Import(String name) {
        throw new UnimplementedException();
    }
    
    @Override
    public ExpressionStatement ExpressionStatement(Statement container, BasicObject obj) {
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
    public Compilable ReturnStatement(Statement container, FuncHead returnFrom, BasicObject result) {
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
    
}