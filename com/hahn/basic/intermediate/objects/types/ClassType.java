package com.hahn.basic.intermediate.objects.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.intermediate.FuncBridge;
import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public class ClassType extends StructType {
    private final Var varThis;
    
    private FuncBridge funcBridge;
    private List<Statement> initStatements;
    
    private boolean isAbstract;
    
    public ClassType(String name, StructType parent, boolean isAbstract) {
        super(name, parent);
        
        this.isAbstract = isAbstract;
        this.funcBridge = new FuncBridge(this);
        this.initStatements = new ArrayList<Statement>();
        
        this.varThis = LangCompiler.factory.VarThis(LangCompiler.getGlobalFrame(), this);
        
        // Define function super
        defineFunc(null, "super", true, Type.VOID);
    }
    
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public Var getThis() {
        return varThis;
    }
    
    @Override
    public ClassType extendAs(String name) {
        return this.extendAs(name, null);
    }
    
    @Override
    public ClassType extendAs(String name, List<BasicObject> ps) {
        ClassType newClass = new ClassType(name, this, false);
        newClass.loadParams(ps);
        
        return newClass;
    }
    
    public void addInitStatements(List<Statement> inits) {
        if (inits != null) {
            this.initStatements.addAll(inits);
        }
    }
    
    public void addInitStatement(Statement init) {
        if (init != null) {
            this.initStatements.add(init);
        }
    }
    
    public List<Statement> getInitStatements() {
        return initStatements;
    }
    
    public FuncHead defineFunc(Node head, String name, boolean rawName, Type rtnType, Param... params) {
        return funcBridge.defineFunc(LangCompiler.getGlobalFrame(), head, name, rawName, rtnType, params);
    }
    
    public Collection<FuncGroup> getDefinedFuncs() {
        return funcBridge.getFuncs();
    }
    
    /**
     * Get a function
     * @param nameNode The node that contains the requested name
     * @return The function
     * @throw CompileException If the requested function is not defined
     */
    public FuncHead getFunc(Node nameNode, ITypeable[] types) {
        return getFunc(nameNode, types, false);
    }
    
    /**
     * Get a function
     * @param nameNode The node that contains the requested name
     * @param safe If false can throw an exception
     * @return The function; or, if `safe` is true and there is an error, null
     * @throw CompileException If `safe` is false and the function is not defined
     */
    public FuncHead getFunc(Node nameNode, ITypeable[] types, boolean safe) {
        String name = nameNode.getValue();
        FuncHead func = funcBridge.getFunc(name, types);
        if (func != null) {
            return func;
        } else if (getParent() instanceof ClassType) {
            func =  ((ClassType) getParent()).getFunc(nameNode, types, true);
            if (func != null) return func;
        } 
        
        // If reached this point then not found
        if (!safe) {
            throw new CompileException("Unknown function `" + name + "` in " + this, nameNode);
        } else {
            return null;
        }
    }
    
    public void reverseOptimize() {
        ListIterator<Statement> it = initStatements.listIterator(initStatements.size());
        while (it.hasPrevious()) {
            it.previous().reverseOptimize();
        }
    }
    
    public void forwardOptimize() {
        ListIterator<Statement> it = initStatements.listIterator(0);
        while (it.hasNext()) {
            it.next().forwardOptimize();
        }
    }
    
    public String toTarget() {
        return LangCompiler.factory.createClass(this);
    }
}
