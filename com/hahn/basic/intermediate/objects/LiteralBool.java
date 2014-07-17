package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.LangObject;
import com.hahn.basic.util.CompileException;

public class LiteralBool extends Literal {
	private boolean value;
	
	public LiteralBool(boolean value) {
		super(Type.BOOL);
		
		this.value = value;
	}

	@Override
	public LangObject toTarget() {
		return this;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public double getValue() {
		return (value ? 1 : 0);
	}
	
	public boolean getBoolValue() {
		return value;
	}

	@Override
	public boolean updateLiteral(OPCode op, Literal lit) {
		// Do operation
        if (op == OPCode.ADD)      { badOP(op); }
        else if (op == OPCode.SUB) { badOP(op); }
        else if (op == OPCode.MUL) { badOP(op); }
        else if (op == OPCode.DIV) { badOP(op); }
        else if (op == OPCode.BOR) { isbool(op, lit); this.value = this.value | toBool(lit.getValue()); }
        else if (op == OPCode.AND) { isbool(op, lit); this.value = this.value & toBool(lit.getValue()); }
        else if (op == OPCode.XOR) { isbool(op, lit); this.value = this.value ^ toBool(lit.getValue()); }
        else if (op == OPCode.SHL) { badOP(op); }
        else if (op == OPCode.SHR) { badOP(op); }
        else return false; // Not a modifiable op code
        
        return true; // Did modify
	}
	
	private void badOP(OPCode op) {
		throw new CompileException("Can not preform operation " + op + " on boolean values");
	}
	
	private void isbool(OPCode op, Literal lit) {
		if (!lit.getType().doesExtend(Type.BOOL)) {
			throw new CompileException("Can not preform operation " + op + " on bool with " + lit.getType());
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Boolean) {
			return value == (boolean) o;
		} else if (o instanceof LiteralBool) {
			return value == ((LiteralBool) o).getBoolValue();
		} else {
			return false;
		}
	}
	
	public static boolean toBool(double d) {
		return (d != 0 ? true : false);
	}
}
