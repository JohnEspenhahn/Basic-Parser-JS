package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;

public abstract class Literal extends BasicObject {

	public Literal(Type type) {
		super("#", type);
	}
	
	public abstract double getValue();
	
	@Override
	public boolean canUpdateLiteral(Frame f, OPCode op) {
	    return true;
	}
	
	@Override
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
	public String toTarget() {
		return toString();
	}
	
	@Override
	public int hashCode() {
		return (int) getValue();
	}
	
	@Override
	public abstract String toString();
}
