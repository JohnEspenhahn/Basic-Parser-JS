package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.intermediate.statements.function.CallFuncStatement;
import com.hahn.basic.target.LangObject;

public abstract class FuncCallPointer extends FuncPointer {
    private Type returnType;

    public FuncCallPointer(String name, BasicObject[] params) {
        super(name, new ParameterizedType<ITypeable>(Type.FUNC, (ITypeable[]) params));

        this.returnType = Type.UNDEFINED;
    }

    public boolean isUsed() {
        return getUses() > 0;
    }

    @Override
    public Type getType() {
        return returnType;
    }

    @Override
    public BasicObject getForUse(Statement s) {
        s.addCode(new CallFuncStatement(s, this));
        
        return super.getForUse(s);
    }
    
    public void doAddPreCall(Statement s) { }
    
    public void doAddPostCall(Statement s) { }

    /**
     * Cast to given type
     * @param t The type to cast to
     * @return this
     */
    @Override
    public BasicObject castTo(Type t) {
        this.returnType = this.returnType.castTo(t);

        return this;
    }

    @Override
    public boolean setInUse() {
        for (BasicObject o : getParams()) {
            o.setInUse();
        }

        checkFunction();
        returnType = func.getReturnType().castTo(returnType);
        
        return super.setInUse();
    }

    @SuppressWarnings("unchecked")
    public BasicObject[] getParams() {
        return ((ParameterizedType<BasicObject>) super.getType()).getTypes();
    }

    @Override
    public abstract LangObject toTarget();

    @Override
    public String toString() {
        return super.toString() + "(...)";
    }
}