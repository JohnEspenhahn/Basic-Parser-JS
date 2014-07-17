package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.LangObject;

public abstract class Literal extends BasicObject implements LangObject {

	public Literal(Type type) {
		super("#", type);
	}
	
	public abstract double getValue();
	
	/**
     * Update the literal value of this variable
     * @param op The op update type
     * @param lit The value to use in update
     * @return True if did modify
     */
    public abstract boolean updateLiteral(OPCode op, Literal lit);

	@Override
    public boolean hasLiteral() {
        return true;
    }
    
    @Override
    public Literal getLiteral() {
        return this;
    }
	
	@Override
	public LangObject toTarget() {
		return this;
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public int hashCode() {
		return (int) getValue();
	}
	
	@Override
	public abstract String toString();
}
