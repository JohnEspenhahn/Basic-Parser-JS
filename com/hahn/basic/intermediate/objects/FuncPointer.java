package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class FuncPointer extends BasicObject {
    protected Node nameNode;
    protected BasicObject objectIn;
    
    protected FuncHead func;
    
    /**
     * A pointer to a function
     * @param nameNode The node defining the function's name
     * @param objectIn The object that contains the function or null
     * @param funcType The function parameterized type
     */
    public FuncPointer(Node nameNode, BasicObject objectIn, ParameterizedType<ITypeable> funcType) {
        super(nameNode.getValue(), funcType);
        
        this.nameNode = nameNode;
        this.objectIn = objectIn;
    }
    
    @Override
    @Deprecated
    public void setType(Type t) {
        // This is required
        throw new RuntimeException("Can not change the type of a function pointer!");
    }
    
    /**
     * Get the ID of the function. Should only be called
     * after being register optimized
     * @return The function id
     */
    public String getFuncId() {        
        return func.getFuncId();
    }
    
    /**
     * Get the object in which the function this is pointing to lives
     * @return The object that contains the function that is being pointed to
     */
    public BasicObject getObjectIn() {
        return objectIn;
    }
    
    /**
     * Get the function parameterized type
     * @return The parameterized type
     */
    @SuppressWarnings("unchecked")
    private ParameterizedType<ITypeable> getParameterized() {
        return (ParameterizedType<ITypeable>) super.getType();
    }
    
    /**
     * Get the types of the function's parameters
     * @return Parameter's types
     */
    public ITypeable[] getTypes() {
        return getParameterized().getTypes();
    }

    /**
     * Get the function's return type
     * @return Return type
     */
    public Type getReturn() {
        return getParameterized().getReturn();
    }
    
    /**
     * Set the parameterized return type
     * @param t The type
     */
    private void setPTypeReturn(Type t) {
        getParameterized().setReturn(t);
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        checkFunction();
        if (objectIn != null) objectIn.setInUse(this);
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        if (objectIn != null) objectIn.takeRegister(this);
        
        super.setInUse(by);
    }
    
    /**
     * If needed find the function head this is pointing to
     * and store it
     */
    protected void checkFunction() {
        if (func == null) {
            FuncCallPair funcPair = LangCompiler.getFunc(objectIn, nameNode, getTypes());
            if (funcPair != null) {
                // If returned a function pair, update variables
                setFunction(funcPair.getFunc());
                this.objectIn = funcPair.getObjectIn();
            } else {
                // Otherwise default function to null
                setFunction(null);
            }
            
            // Still null then not found
            if (func == null) {
                throw new CompileException("Function `" + FuncHead.toHumanReadable(this) + "` was not found");
            }
        }
    }
    
    /**
     * Store reference to the actual function head
     * @param func The function head this points to
     */
    public void setFunction(FuncHead func) {
        this.func = func;
        
        if (func != null) setPTypeReturn(func.getReturnType());
    }
    
    /**
     * Gets the reference to the actual function head. Null until after
     * reverse optimize or until after being explicitly set by setFunction()
     */
    public FuncHead getFunction() {
        return func;
    }
    
    @Override
    public String toString() {
        if (getObjectIn() != null) return "&" + getObjectIn() + super.toString();
        else return "&" + super.toString();
    }
}
