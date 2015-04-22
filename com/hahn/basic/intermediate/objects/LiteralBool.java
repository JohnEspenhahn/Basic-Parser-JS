package com.hahn.basic.intermediate.objects;

import static com.hahn.basic.util.Util.toBool;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.util.exceptions.CompileException;

public class LiteralBool extends Literal {
	private boolean value;
	
	public LiteralBool(boolean value) {
		super(Type.BOOL);
		
		this.value = value;
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
        else if (op == OPCode.BOR) { if(isbool(op, lit)) this.value = this.value || toBool(lit.getValue()); }
        else if (op == OPCode.AND) { if(isbool(op, lit)) this.value = this.value && toBool(lit.getValue()); }
        else if (op == OPCode.XOR) { if(isbool(op, lit)) this.value = this.value ^ toBool(lit.getValue()); }
        else if (op == OPCode.SHL) { badOP(op); }
        else if (op == OPCode.SHR) { badOP(op); }
        else if (op == OPCode.NOT) { this.value = !this.value; }
        else return false; // Not a modifiable op code
        
        return true; // Did modify
	}
	
	private void badOP(OPCode op) {
		throw new CompileException("Can not preform operation " + op + " on boolean values");
	}
	
	private boolean isbool(OPCode op, Literal lit) {
	    if (lit == null) {
	        return false;
	    } else if (!lit.getType().doesExtend(Type.BOOL)) {
			throw new CompileException("Can not preform operation " + op + " on bool with " + lit.getType());
		} else {
		    return true;
		}
	}
	
	@Override
	public String toTarget() {
		return toString();
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
