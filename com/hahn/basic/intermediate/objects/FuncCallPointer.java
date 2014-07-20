package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.types.ITypeable;
import com.hahn.basic.intermediate.objects.types.ParameterizedType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.Statement;

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
        s.addCode(LangCompiler.factory.CallFuncStatement(s, this));
        
        return super.getForUse(s);
    }

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
    public boolean setInUse(Compilable by) {
        checkFunction();
        returnType = func.getReturnType().castTo(returnType);
        
        return super.setInUse(by);
    }

    @SuppressWarnings("unchecked")
    public BasicObject[] getParams() {
        return ((ParameterizedType<BasicObject>) super.getType()).getTypes();
    }

    @Override
    public abstract String toTarget();

    @Override
    public String toString() {
        return super.toString() + "(...)";
    }
}