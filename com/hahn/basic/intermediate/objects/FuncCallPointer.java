package com.hahn.basic.intermediate.objects;

import java.util.Arrays;
import java.util.ListIterator;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;

public abstract class FuncCallPointer extends FuncPointer {
    private int row, col;
    private Type returnType;

    public FuncCallPointer(String name, BasicObject[] params, int row, int col) {
        super(name, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) params));

        this.returnType = Type.UNDEFINED;
        
        this.row = row;
        this.col = col;
    }

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
        checkFunction();
        returnType = func.getReturnType().castTo(returnType, this.row, this.col);
        
        ListIterator<BasicObject> it = Arrays.asList(getParams()).listIterator(countParams());
        while (it.hasPrevious()) {
            BasicObject param = it.previous();
            
            param.setInUse(this);
        }
        
        return super.setInUse(by);
    }
    
    @Override
    public void takeRegister(IIntermediate by) {
        for (BasicObject param: getParams()) {
            param.takeRegister(by);
        }
        
        super.takeRegister(by);
    }

    @SuppressWarnings("unchecked")
    public BasicObject[] getParams() {
        return ((ParameterizedType<BasicObject>) super.getType()).getTypes();
    }
    
    public int countParams() {
        return getParams().length;
    }

    @Override
    public String toString() {
        return super.toString() + "(...)";
    }
}