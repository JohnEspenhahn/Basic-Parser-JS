package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.function.FuncBridge;
import com.hahn.basic.intermediate.function.FuncGroup;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.ClassObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.statements.ClassDefinition;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.Util;
import com.hahn.basic.util.exceptions.CompileException;

public class ClassType extends StructType {    
    private FuncBridge funcBridge;
    private Frame initFrame;
    private Frame staticFrame;
    
    private ClassObject classObj;    
    private Var varThis, varImpliedThis, varSuper;
    
    private ClassDefinition def;
    
    public ClassType(Frame containingFrame, String name, StructType parent, int flags, boolean isAbstract) {
        super(name, parent, flags, isAbstract);
        
        if (name == null) return;
        
        this.funcBridge = new FuncBridge(this);
        this.initFrame = new Frame(Compiler.getGlobalFrame(), null);
        this.staticFrame = new Frame(Compiler.getGlobalFrame(), null);
        
        this.classObj = Compiler.factory.ClassObject(this);
        
        this.varThis = Compiler.factory.VarThis(Compiler.getGlobalFrame(), this);
        this.varImpliedThis = Compiler.factory.VarImpliedThis(Compiler.getGlobalFrame(), this);
        
        if (parent instanceof ClassType) {
            defineSuper((ClassType) parent);
        } else {
            this.varSuper = null;
        }
        
        this.def = Compiler.factory.ClassDefinition(containingFrame, this);
    }
    
    /**
     * Adds necessary super objects and functions
     * @param parent
     */
    private void defineSuper(ClassType parent) {
        this.varSuper = Compiler.factory.VarSuper(Compiler.getGlobalFrame(), parent);
        
        for (FuncGroup funcs: parent.getDefinedFuncs()) {
            if (funcs.isConstructor()) {
                for (FuncHead func: funcs.getFuncs()) {
                    FuncHead superFunc = defineFunc(null, "super", null, func.getReturnType(), Util.toParams(func.getParams()));
                    superFunc.setFlags(BitFlag.PRIVATE.b);
                }
                
                break;
            }
        }
    }
    
    public ClassObject getClassObj() {
        return classObj;
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
     * Get a version of `this var` which knows it is implied
     * Ex] speak() vs this.speak()
     * @return The implied this var
     */
    public Var getImpliedThis() {
        return varImpliedThis;
    }
    
    /**
     * Get the super var. If parent is not a ClassType will be null
     * @return The super var or null
     */
    public Var getSuper() {
        return varSuper;
    }
    
    @Override
    public ClassType extendAs(Frame containingFrame, String name, int flags) {
        return this.extendAs(containingFrame, name, null, flags);
    }
    
    @Override
    public ClassType extendAs(Frame containingFrame, String name, List<BasicObject> ps, int flags) {
        ClassType newClass = new ClassType(containingFrame, name, this, flags, false);
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
     * @param outName The name to output in the target language or null for default
     * @param override If true will override a pre-existing parameter explicitly defined in this class
     * @return This
     */
    public ClassType systemParam(String name, Type type, String outName, boolean override) {
        super.addParam(new Node(null, null, name, -1, -1, -1), type, outName, override);
        return this;
    }
    
    public Frame getStaticFrame() {
        return staticFrame;
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
    
    public FuncHead defineFunc(Node head, String inName, String outName, Type rtnType, Param... params) {
        return defineFunc(head, false, inName, outName, rtnType, params);
    }
    
    public FuncHead defineFunc(Node head, boolean override, String inName, String outName, Type rtnType, Param... params) {        
        return funcBridge.defineFunc(Compiler.getGlobalFrame(), override, head, inName, outName, rtnType, params);
    }
    
    public Collection<FuncGroup> getDefinedFuncs() {
        return funcBridge.getFuncs();
    }
    
    public FuncHead getConstructor(ITypeable[] types) {
        return funcBridge.getFunc(Util.getConstructorName(), types);
    }
    
    /**
     * Get a function
     * @param objIn The object the function is in or null
     * @param nameNode The node that contains the requested name
     * @return The function
     * @throw CompileException If the requested function is not defined
     */
    public FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types) {
        return getFunc(objIn, nameNode, types, false, false);
    }
    
    /**
     * Get a function
     * @param objIn The object the function is in or null
     * @param nameNode The node that contains the requested name
     * @param safe If false can throw an exception
     * @param shallow If true only search this class, not super
     * @return The function; or, if `safe` is true and there is an error, null
     * @throw CompileException If `safe` is false and the function is not defined
     */
    public FuncHead getFunc(BasicObject objIn, Node nameNode, ITypeable[] types, boolean safe, boolean shallow) {
        boolean findingConstructor = Util.isConstructorName(nameNode.getValue());
        
        String name = nameNode.getValue();
        FuncHead func = funcBridge.getFunc(name, types);
        if (func != null) {
            return getValidFuncAccess(objIn, nameNode, func, safe);
        } else if (!shallow && getParent() instanceof ClassType) {
            func = ((ClassType) getParent()).getFunc(objIn, nameNode, types, true, shallow);
            if (func != null) {
                if (func.hasFlag(BitFlag.PRIVATE)) {
                    if (!safe) {
                        throw new CompileException("The function `" + func + "` is private");
                    } else {
                        return null;
                    }
                } else {
                    return getValidFuncAccess(objIn, nameNode, func, safe);
                }
            }
        } 
        
        // If reached this point then not found
        if (!safe) {
            if (findingConstructor) throw new CompileException("Unknown contructor with parameters `(" + Util.joinTypes(types, ',') + ")` in " + this, nameNode);
            else throw new CompileException("Unknown function `" + name + "(" + Util.joinTypes(types, ',') + ")` in " + this, nameNode);
        } else {
            return null;
        }
    }
    
    /**
     * Validate accessing the function through valid means
     * @param objIn The object the function is in or null
     * @param nameNode The node that contains the requested name
     * @param func The function being accessed
     * @param safe If false can throw an exception
     * @return The function; or, if `safe` is true and there is an error, null
     * @throw CompileException If `safe` is false and the function is not defined
     */
    private FuncHead getValidFuncAccess(BasicObject objIn, Node nameNode, FuncHead func, boolean safe) {
        if (func.hasFlag(BitFlag.STATIC) && !objIn.isClassObject()) {
            if (!safe) throw new CompileException("Must access function `" + func + "` directly through its defining class");
            else return null;
        } else {
            return func;
        }
    }
    
    @Override
    public void reverseOptimize() {
        for (FuncGroup group: getDefinedFuncs()) {
            for (FuncHead func: group) {
                if (func.hasFrameHead()) {
                    func.addTargetCode();
                    func.reverseOptimize();
                }
            }
        }
        
        initFrame.addTargetCode();
        initFrame.reverseOptimize();
        
        // If no constructor, define default
        if (funcBridge.getFuncGroup(Util.getConstructorName()) == null) {
            defineFunc(null, Util.getConstructorName(), null, Type.VOID, new Param[0]);
        }
    }
    
    @Override
    public void forwardOptimize() {
        initFrame.forwardOptimize();
        
        for (FuncGroup group: getDefinedFuncs()) {
            for (FuncHead func: group) {
                if (func.hasFrameHead()) {
                    func.forwardOptimize();
                }
            }
        }
    }
    
    @Override
    public String toTarget() {
        return def.toTarget();
    }
    
}
