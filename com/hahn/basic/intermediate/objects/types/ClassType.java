package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.FuncBridge;
import com.hahn.basic.intermediate.FuncGroup;
import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CompileException;

public class ClassType extends StructType {
    private final Var varThis, varSuper;
    
    private FuncBridge funcBridge;
    private Frame initFrame;
    
    public ClassType(String name, StructType parent, int flags) {
        super(name, parent, flags);
        
        this.funcBridge = new FuncBridge(this);
        this.initFrame = new Frame(LangCompiler.getGlobalFrame(), null);
        
        this.varThis = LangCompiler.factory.VarThis(LangCompiler.getGlobalFrame(), this);
        
        if (parent instanceof ClassType) {
            this.varSuper = LangCompiler.factory.VarSuper(LangCompiler.getGlobalFrame(), (ClassType) parent);
        } else {
            this.varSuper = null;
        }
    }
    
    /**
     * Flag this class as a system class, will not add code
     * @return This
     */
    public ClassType setSystemClass() {
        setFlag(BitFlag.SYSTEM);
        return this;
    }
    
    /**
     * Get the this var
     * @return The this var
     */
    public Var getThis() {
        return varThis;
    }
    
    /**
     * Get the super var. If parent is not a ClassType will be null
     * @return The super var or null
     */
    public Var getSuper() {
        return varSuper;
    }
    
    @Override
    public ClassType extendAs(String name, int flags) {
        return this.extendAs(name, null, flags);
    }
    
    @Override
    public ClassType extendAs(String name, List<BasicObject> ps, int flags) {
        ClassType newClass = new ClassType(name, this, flags);
        newClass.loadParams(ps);
        
        return newClass;
    }
    
    @Override
    public ClassType setTypeParams(int num) {
        super.setTypeParams(num);
        return this;
    }
    
    /**
     * Attempt to force a system parameter for a class. Should
     * not be used by 3rd parties. Undefined behavior if the 
     * parameter is already defined
     * @param name The name of the parameter to define
     * @param type The type of the parameter to define
     * @return This
     */
    protected ClassType systemParam(String name, Type type) {
        super.addParam(new Node(null, null, name, -1, -1), type);
        return this;
    }
    
    public void addInitStatements(List<Statement> inits) {
        if (inits != null) {
            initFrame.getTargetCode().addAll(inits);
        }
    }
    
    public void addInitStatement(Statement init) {
        if (init != null) {
            initFrame.getTargetCode().add(init);
        }
    }
    
    public Frame getInitFrame() {
        return initFrame;
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
            throw new CompileException("Unknown function `" + name + "` with parameters `(" + Util.joinTypes(types, ',') + ")` in " + this, nameNode);
        } else {
            return null;
        }
    }
    
    public void reverseOptimize() {
        initFrame.reverseOptimize();
    }
    
    public void forwardOptimize() {
        initFrame.forwardOptimize();
    }
    
    public String toTarget() {
        if (hasFlag(BitFlag.SYSTEM)) return "";
        else return LangCompiler.factory.createClass(this);
    }
}
