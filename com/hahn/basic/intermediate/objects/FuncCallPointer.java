package com.hahn.basic.intermediate.objects;

import java.util.Arrays;
import java.util.ListIterator;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.parser.Node;

public abstract class FuncCallPointer extends FuncPointer {
    private int row, col;
    private Type returnType;

    /**
     * A callable pointer to a function
     * @param nameNode The node that defines the name of the function to call
     * @param params The provided parameters for the call
     * @param row The row to throw an error at
     * @param col The column to throw an error at
     */
    public FuncCallPointer(Node nameNode, BasicObject[] params, int row, int col) {
        super(nameNode, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) params));

        this.returnType = Type.UNDEFINED;
        
        this.row = row;
        this.col = col;
    }

    /**
     * Is this object used
     * @return True if uses is greater than zero
     */
    public boolean isUsed() {
        return getUses() > 0;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    /**
     * Cast to given type
     * @param t The type to cast to
     * @return this
     */
    @Override
    public BasicObject castTo(Type t, int row, int col) {
        this.returnType = this.returnType.castTo(t, row, col);

        return this;
    }

    @Override
    public boolean setInUse(IIntermediate by) {        
        ListIterator<BasicObject> it = Arrays.asList(getParams()).listIterator(countParams());
        while (it.hasPrevious()) {
            BasicObject param = it.previous();
            
            param.setInUse(this);
        }
        
        checkFunction();
        returnType = func.getReturnType().autocast(returnType, this.row, this.col, true);
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        for (BasicObject param: getParams()) {
            param.takeRegister(by);
        }
        
        super.takeRegister(by);
    }
    
    /**
     * Get the function parameterized type
     * @return The parameterized type
     */
    @SuppressWarnings("unchecked")
    private ParameterizedType<BasicObject> getParameterized() {
        return (ParameterizedType<BasicObject>) super.getType();
    }

    /**
     * Get the actual parameter objects from the parameterized type
     * @return The parameter objects
     */
    public BasicObject[] getParams() {
        return getParameterized().getTypes();
    }
    
    /**
     * Get the number of parameters
     * @return Number of parameters
     */
    public int countParams() {
        return getParams().length;
    }

    @Override
    public String toString() {
        return super.toString() + "(...)";
    }
}