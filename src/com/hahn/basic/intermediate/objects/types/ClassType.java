package com.hahn.basic.intermediate.objects.types;

import java.util.Collection;
import java.util.List;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.function.FuncBridge;
import com.hahn.basic.intermediate.function.FuncGroup;
import com.hahn.basic.intermediate.function.FuncHead;
import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.intermediate.objects.ClassObject;
import com.hahn.basic.intermediate.objects.Param;
import com.hahn.basic.intermediate.objects.Var;
import com.hahn.basic.intermediate.statements.ClassDefinition;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.util.ConstructorUtils;
import com.hahn.basic.util.TypeUtils;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.structures.BitFlag;

import lombok.NonNull;

public class ClassType extends StructType implements IClassType {
    private final Frame containingFrame;
    
    private final FuncBridge funcBridge;
    private final Frame initFrame;
    private final Frame staticFrame;
    
    private final ClassDefinition def;
    
    private final ClassObject classObj;    
    private Var varThis, varImpliedThis, varSuper;
    
    protected ClassType(Frame containingFrame, String name, @NonNull StructType parent, int flags, boolean isAbstract) {
        super(containingFrame.getFile(), name, parent, flags, isAbstract);
        
        if (name == null) throw new IllegalArgumentException();
        
        this.containingFrame = containingFrame;
        
        final Frame globalFrame = getCompiler().getGlobalFrame();
        this.funcBridge = new FuncBridge(this);
        this.initFrame = new Frame(getFile(), globalFrame, null);
        this.staticFrame = new Frame(getFile(), globalFrame, null);
        
        this.classObj = getFactory().ClassObject(this);
        
        this.varThis = getFactory().VarThis(globalFrame, this);
        this.varImpliedThis = getFactory().VarImpliedThis(globalFrame, this);
        
        if (parent instanceof ClassType) {
            defineSuper((ClassType) parent);
        } else {
            this.varSuper = null;
        }
        
        this.def = getFactory().ClassDefinition(containingFrame, this);
    }
    
    protected ClassType(ClassType self, boolean staticMode) {
    	super(self, staticMode);
    	
    	this.containingFrame = self.containingFrame;
        
        this.funcBridge = self.funcBridge;
        this.initFrame = self.initFrame;
        this.staticFrame = self.staticFrame;
        
        this.classObj = self.classObj;
        
        this.varThis = self.varThis;
        this.varImpliedThis = self.varImpliedThis;
        
        this.varSuper = self.varSuper;
        
        this.def = getFactory().ClassDefinition(containingFrame, this);
    }
    
    public ClassType cloneAsStatic() {
    	return new ClassType(this, staticMode);
    }
    
    /**
     * Adds necessary super objects and functions
     * @param parent
     */
    private void defineSuper(ClassType parent) {
        this.varSuper = getFactory().VarSuper(getCompiler().getGlobalFrame(), parent);
        
        for (FuncGroup funcs: parent.getDefinedFuncs()) {
            if (funcs.isConstructor()) {
                for (FuncHead func: funcs.getFuncs()) {
                    FuncHead superFunc = defineFunc(getFile(), null, "super", null, func.getReturnType(), TypeUtils.toParams(func.getParams()));
                    superFunc.setFlags(BitFlag.PRIVATE.b);
                }
                
                break;
            }
        }
    }
    
    public ClassObject getClassObj() {
        return classObj;
    }
    
    public Frame getContainingFrame() {
        return containingFrame;
    }
    
    /**
     * @return File associated with this class
     */
    public CodeFile getFile() {
        return getContainingFrame().getFile();
    }
    
    /**
     * @return Factory associated with this class
     */
    public CommandFactory getFactory() {
        return getFile().getFactory();
    }
    
    /**
     * @return Compiler associated with this class
     */
    public Compiler getCompiler() {
        return getFile().getCompiler();
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
    public ClassType extendAs(Frame containingFrame, String name, List<IBasicObject> ps, int flags) {
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
    
    public FuncHead defineFunc(CodeFile file, Node head, String inName, String outName, Type rtnType, Param... params) {
        return defineFunc(file, head, false, inName, outName, rtnType, params);
    }
    
    public FuncHead defineFunc(CodeFile file, Node head, boolean override, String inName, String outName, Type rtnType, Param... params) {        
        return funcBridge.defineFunc(file, getCompiler().getGlobalFrame(), override, head, inName, outName, rtnType, params);
    }
    
    public Collection<FuncGroup> getDefinedFuncs() {
        return funcBridge.getFuncs();
    }
    
    public FuncHead getConstructor(ITypeable[] types) {
        return funcBridge.getFunc(ConstructorUtils.getConstructorName(), types);
    }
    
    /**
     * Get a function
     * @param objIn The object the function is in or null
     * @param nameNode The node that contains the requested name
     * @return The function
     * @throw CompileException If the requested function is not defined
     */
    public FuncHead getFunc(IBasicObject objIn, Node nameNode, ITypeable[] types) {
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
    public FuncHead getFunc(IBasicObject objIn, Node nameNode, ITypeable[] types, boolean safe, boolean shallow) {
    	FuncHead func = doGetFunc(objIn, nameNode, types, safe, false);
        
    	if (!staticMode) {
    		return func;
    	} else {
    		if (!func.hasFlag(BitFlag.STATIC)) {
                if (safe) return null;
                else throw new CompileException("Can not make a static reference to a non-static function", nameNode);
            } else if (getFunc(objIn, nameNode, types, true, true) != func) {
                if (safe) return null;
                else throw new CompileException("Must access static function `" + func + "` directly through its defining class", nameNode);                
            } else {
                return func;
            }	
    	}
    }
    
    private FuncHead doGetFunc(IBasicObject objIn, Node nameNode, ITypeable[] types, boolean safe, boolean shallow) {
        final boolean isFindingConstructor = ConstructorUtils.isConstructorName(nameNode.getValue());
        
        String name = nameNode.getValue();
        FuncHead func = funcBridge.getFunc(name, types);
        if (func != null) {
            return getValidFuncAccess(objIn, nameNode, func, safe);
        } else if (!shallow && getParent() instanceof ClassType) {
            func = ((ClassType) getParent()).getFunc(objIn, nameNode, types, true, shallow);
            if (func != null) {
                if (func.hasFlag(BitFlag.PRIVATE)) {
                    if (!safe) {
                        throw new CompileException("The function `" + func + "` is private", nameNode.getFile(), nameNode.getRow(), nameNode.getCol());
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
            if (isFindingConstructor) throw new CompileException("Unknown contructor with parameters `(" + TypeUtils.joinTypes(types, ',') + ")` in " + this, nameNode);
            else throw new CompileException("Unknown function `" + name + "(" + TypeUtils.joinTypes(types, ',') + ")` in " + this, nameNode);
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
    private FuncHead getValidFuncAccess(IBasicObject objIn, Node nameNode, FuncHead func, boolean safe) {
        if (func.hasFlag(BitFlag.STATIC) && !objIn.isClassObject()) {
            if (!safe) throw new CompileException("Must access function `" + func + "` directly through its defining class", getFile());
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
        if (funcBridge.getFuncGroup(ConstructorUtils.getConstructorName()) == null) {
            defineFunc(getFile(), null, ConstructorUtils.getConstructorName(), null, Type.VOID, new Param[0]);
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
