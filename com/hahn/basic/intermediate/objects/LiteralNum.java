package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.util.exceptions.CompileException;

public class LiteralNum extends Literal {
    public static final LiteralNum ZERO = new LiteralNum(0);
    public static final LiteralNum ONE  = new LiteralNum(1);
    
    public static final LiteralNum UNDEFINED  = new LiteralNum(0, Type.UNDEFINED);
    
    private double value;
    
    public LiteralNum(int value) {
        this(value, Type.INT);
    }
    
    public LiteralNum(char value) {
        this(value, Type.CHAR);
    }
    
    public LiteralNum(double value) {
    	this(value, Type.DBL);
    }
    
    public LiteralNum(double value, Type type) {
        super(type);
        
        this.value = value;
    }
    
    @Override
    public boolean updateLiteral(OPCode op, Literal lit) {
    	mergeTypes(lit);
    	
    	// Do operation
        if (op == OPCode.ADD)      { isnum(op, lit); this.value += lit.getValue(); }
        else if (op == OPCode.SUB) { isnum(op, lit); this.value -= lit.getValue(); }
        else if (op == OPCode.MUL) { isnum(op, lit); this.value *= lit.getValue(); }
        else if (op == OPCode.DIV) { isnum(op, lit); this.value /= lit.getValue(); }
        else if (op == OPCode.BOR) { isint(op, lit); this.value = (int) this.value | (int) lit.getValue(); }
        else if (op == OPCode.AND) { isint(op, lit); this.value = (int) this.value & (int) lit.getValue(); }
        else if (op == OPCode.XOR) { isint(op, lit); this.value = (int) this.value ^ (int) lit.getValue(); }
        else if (op == OPCode.SHL) { isint(op, lit); this.value = (int) this.value << (int) lit.getValue(); }
        else if (op == OPCode.SHR) { isint(op, lit); this.value = (int) this.value >> (int) lit.getValue(); }
        else return false; // Not a modifiable op code
        
        return true; // Did modify
    }
    
    private void mergeTypes(Literal lit) {
    	if (lit.getType().doesExtend(Type.DBL)) {
    		this.setType(lit.getType());
    	}
    }
    
    private void isnum(OPCode op, Literal lit) {
    	if (!lit.getType().doesExtend(Type.INT) && !lit.getType().doesExtend(Type.DBL)) {
    		throw new CompileException("Can not preform " + op + " on " + this.getType() + " with " + lit.getType());
    	}
    }
    
    private void isint(OPCode op, Literal lit) {
    	if (!getType().doesExtend(Type.INT) || !lit.getType().doesExtend(Type.INT)) {
    		throw new CompileException("Can not preform " + op + " on " + this.getType() + " with " + lit.getType());
    	}
    }
    
    @Override
    public double getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (getType() == Type.INT && o instanceof Integer) {
            return value == (int) o;
        } else if (o instanceof LiteralNum) {
            return ((LiteralNum) o).value == value;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        if (getType().doesExtend(Type.INT) || getType() == Type.BOOL) {
        	return String.valueOf((int) value);
        } else {
        	return String.valueOf(value);
        }
    }
}
