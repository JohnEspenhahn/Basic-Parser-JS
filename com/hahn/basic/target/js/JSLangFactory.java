package com.hahn.basic.target.js;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ConditionalObject;
import com.hahn.basic.intermediate.objects.ExpressionObject;
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
import com.hahn.basic.intermediate.statements.CallFuncStatement;
import com.hahn.basic.intermediate.statements.Command;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.DefineVarStatement;
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.IfStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.WhileStatement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.ILangFactory;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.objects.JSFuncCallPointer;
import com.hahn.basic.target.js.objects.JSFuncHead;
import com.hahn.basic.target.js.objects.JSFuncPointer;
import com.hahn.basic.target.js.statements.JSBreakStatement;
import com.hahn.basic.target.js.statements.JSCallFuncStatement;
import com.hahn.basic.target.js.statements.JSCommand;
import com.hahn.basic.target.js.statements.JSContinueStatement;
import com.hahn.basic.target.js.statements.JSDefaultCallFuncStatement;
import com.hahn.basic.target.js.statements.JSDefineVarStatement;
import com.hahn.basic.target.js.statements.JSForStatement;
import com.hahn.basic.target.js.statements.JSIfStatement;
import com.hahn.basic.target.js.statements.JSReturnStatement;
import com.hahn.basic.target.js.statements.JSWhileStatement;
import com.hahn.basic.util.exceptions.UnimplementedException;

public class JSLangFactory implements ILangFactory {
    
    @Override
    public LangBuildTarget LangBuildTarget() {
        return new JSBuildTarget();
    }
    
    @Override
    public BasicObject PushObject() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public StringConst StringConst(String str) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Var VarParameter(Frame frame, String name, Type type) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Var VarLocal(Frame frame, String name, Type type) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public VarGlobal VarGlobal(String name, Type type) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public AdvancedObject VarAccess(AdvancedObject var, BasicObject idx, Type type) {
        // TODO
        return null;
    }
    
    @Override
    public BasicObject NewInstance(Type type, List<BasicObject> params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ExpressionObject ExpressionObject(Frame frame, BasicObject obj) {
        // TODO
        return null;
    }
    
    @Override
    public ConditionalObject ConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2) {
        // TODO Auto-generated method stub
        return null;
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
    public Command Command(Statement container, OPCode op, BasicObject p1, BasicObject p2) {
        return new JSCommand(container, op, p1, p2);
    }
    
    @Override
    public ILangCommand Import(String name) {
        throw new UnimplementedException();
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
