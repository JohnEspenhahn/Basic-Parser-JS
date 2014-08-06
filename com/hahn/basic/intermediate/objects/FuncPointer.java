package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.FuncHead;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class FuncPointer extends BasicObject {    
    protected FuncHead func;
    
    /**
     * A pointer to a function
     * @param name The function name
     * @param funcType The function parameterized type
     */
    public FuncPointer(String name, ParameterizedType<ITypeable> funcType) {
        super(name, funcType);
    }
    
    @Override
    @Deprecated
    public void setType(Type t) {
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
        
        return super.setInUse(by);
    }
    
    /**
     * If needed find the function head this is pointing to
     * and store it
     */
    protected void checkFunction() {
        if (func == null) {
            setFunction(LangCompiler.getFunc(getName(), getTypes()));
            
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
    protected void setFunction(FuncHead func) {
        this.func = func;
        
        if (func != null) setPTypeReturn(func.getReturnType());
    }
    
    @Override
    public String toString() {
        return "&" + super.toString();
    }
}
