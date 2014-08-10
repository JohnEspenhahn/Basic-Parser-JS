package com.hahn.basic.target.js;

import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
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
import com.hahn.basic.intermediate.objects.VarLocal;
import com.hahn.basic.intermediate.objects.VarParameter;
import com.hahn.basic.intermediate.objects.VarThis;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.types.ClassType;
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
import com.hahn.basic.target.js.objects.JSExpressionObject;
import com.hahn.basic.target.js.objects.JSFuncCallPointer;
import com.hahn.basic.target.js.objects.JSFuncPointer;
import com.hahn.basic.target.js.objects.JSNewInstance;
import com.hahn.basic.target.js.objects.JSOPObject;
import com.hahn.basic.target.js.objects.JSStringConst;
import com.hahn.basic.target.js.objects.JSTernaryObject;
import com.hahn.basic.target.js.objects.JSVarAccess;
import com.hahn.basic.target.js.objects.JSVarSuper;
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
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.UnimplementedException;

public class JSLangFactory implements ILangFactory {
    private static final JSBuildTarget build = new JSBuildTarget();
    
    @Override
    public LangBuildTarget getLangBuildTarget() {
        return build;
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
    public String createClass(ClassType c) {
        if (c.getName().equals("Object")) return "";
        
        StringBuilder builder = new StringBuilder();        
        Statement[] inits = c.getInitStatements().toArray(new Statement[c.getInitStatements().size()]);
        builder.append(JSPretty.format(0, "function %s()_{%s}^", c.getName(), Util.toTarget(inits, ";")));
        // TODO class static values
        
        boolean isChild = (c.getParent() instanceof ClassType);
        if (isChild) builder.append(JSPretty.format(0, "%s.prototype=implements(%s,%s);^", c.getName(), c.getName(), c.getParent().getName()));
        for (FuncGroup funcGroup: c.getDefinedFuncs()) {
            for (FuncHead func: funcGroup) {
                if (func.hasFrameHead()) {
                    func.reverseOptimize();
                    func.forwardOptimize();
                    
                    builder.append(JSPretty.format(0, "%s.prototype.%s=%s;^", c.getName(), func.getFuncId(), func.toFuncAreaTarget()));
                }
            }
        }
        
        return builder.toString();
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
    public OPObject OPObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node) {
        return new JSOPObject(container, op, p1, p1Node, p2, p2Node);
    }
    
    @Override
    public ExpressionObject ExpressionObject(ExpressionStatement exp) {
        return new JSExpressionObject(exp);
    }
    
    @Override
    public Var VarParameter(Frame frame, String name, Type type, List<String> flags) {
        return new VarParameter(frame, name, type, flags);
    }
    
    @Override
    public Var VarLocal(Frame frame, String name, Type type, List<String> flags) {
        return new VarLocal(frame, name, type, flags);
    }
    
    @Override
    public VarAccess VarAccess(Statement container, BasicObject var, BasicObject idx, Type type, int row, int col) {
        return new JSVarAccess(container, var, idx, type, row, col);
    }
    
    @Override
    public Var VarThis(Frame frame, ClassType type) {
        return new VarThis(frame, type);
    }
    
    @Override
    public Var VarSuper(Frame frame, ClassType type) {
        return new JSVarSuper(frame, type);
    }
    
    @Override
    public BasicObject NewInstance(Type type, Node typeNode, List<BasicObject> params) {
        return new JSNewInstance(type, typeNode, params);
    }
    
    @Override
    public ConditionalObject ConditionalObject(Statement container, OPCode op, BasicObject p1, Node p1Node, BasicObject p2, Node p2Node, BasicObject temp) {
        return new JSConditionalObject(container, op, p1, p1Node, p2, p2Node, temp);
    }
    
    @Override
    public TernaryObject TernaryObject(Statement container, BasicObject condition, Node node_then, Node node_else, int row, int col) {
        return new JSTernaryObject(container, condition, node_then, node_else, row, col);
    }
    
    @Override
    public FuncHead FuncHead(Frame parent, ClassType classIn, String name, boolean rawName, Node head, Type rtnType, Param[] params) {
        return new JSFuncHead(parent, classIn, name, rawName, head, rtnType, params);
    }
    
    @Override
    public FuncPointer FuncPointer(Node nameNode, BasicObject objectIn, ParameterizedType<ITypeable> funcType) {
        return new JSFuncPointer(nameNode, objectIn, funcType);
    }
    
    @Override
    public FuncCallPointer FuncCallPointer(Node nameNode, BasicObject objectIn, BasicObject[] params) {
        return new JSFuncCallPointer(nameNode, objectIn, params);
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
