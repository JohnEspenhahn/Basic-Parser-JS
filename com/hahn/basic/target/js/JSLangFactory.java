package com.hahn.basic.target.js;

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
import com.hahn.basic.intermediate.statements.ForStatement;
import com.hahn.basic.intermediate.statements.IfStatement.Conditional;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.ILangFactory;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.statements.JSCallFuncStatement;
import com.hahn.basic.target.js.statements.JSDefaultCallFuncStatement;
import com.hahn.basic.target.js.statements.JSDefineVarStatement;

public class JSLangFactory implements ILangFactory {
    
    @Override
    public LangBuildTarget LangBuildTarget() {
        // TODO Auto-generated method stub
        return null;
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
    public Var VarPointer(AdvancedObject obj) {
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
    public BasicObject NewInstance(Type type, List<BasicObject> params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ConditionalObject ConditionalObject(BasicObject temp, OPCode op, BasicObject p1, BasicObject p2) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public FuncHead FuncHead(String name, Node head, Type rtnType, Param[] params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public FuncPointer FuncPointer(String name, ParameterizedType<ITypeable> funcType) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public FuncCallPointer FuncCallPointer(String name, BasicObject[] params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Command Command(Statement container, OPCode op, BasicObject p1, BasicObject p2) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ILangCommand Import(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Compilable BreakStatement(Frame frame) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Compilable ContinueStatement(Frame frame) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Compilable ReturnStatement(Statement container, Frame returnFrom, BasicObject result) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Compilable IfStatement(Statement container, List<Conditional> conditionals) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Compilable WhileStatement(Statement container, Node conditional, Node body) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Compilable ForStatement(Statement container, Node define, Node condition, List<Node> modification, Node body) {
        return new JSForStatement(container, define, condition, modification, body);
    }
    
    @Override
    public Compilable CallFuncStatement(Statement container, FuncCallPointer funcCallPointer) {
        return new JSCallFuncStatement(container, funcCallPointer);
    }
    
    @Override
    public Compilable DefaultCallFuncStatement(Statement container, FuncCallPointer funcCallPointer) {
        return new JSDefaultCallFuncStatement(container, funcCallPointer);
    }
    
    @Override
    public Compilable DefineVarStatement(Statement container, BasicObject var, BasicObject val, boolean ignoreTypeCheck) {
        return new JSDefineVarStatement(container, var, val, ignoreTypeCheck);
    }
    
}
