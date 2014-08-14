package com.hahn.basic.intermediate.objects;

import static com.hahn.basic.intermediate.opcode.OPCode.ADD;
import static com.hahn.basic.intermediate.opcode.OPCode.ADDE;
import static com.hahn.basic.intermediate.opcode.OPCode.AND;
import static com.hahn.basic.intermediate.opcode.OPCode.ANDE;
import static com.hahn.basic.intermediate.opcode.OPCode.BOR;
import static com.hahn.basic.intermediate.opcode.OPCode.BORE;
import static com.hahn.basic.intermediate.opcode.OPCode.DIV;
import static com.hahn.basic.intermediate.opcode.OPCode.DIVE;
import static com.hahn.basic.intermediate.opcode.OPCode.MOD;
import static com.hahn.basic.intermediate.opcode.OPCode.MODE;
import static com.hahn.basic.intermediate.opcode.OPCode.MUL;
import static com.hahn.basic.intermediate.opcode.OPCode.MULE;
import static com.hahn.basic.intermediate.opcode.OPCode.SHL;
import static com.hahn.basic.intermediate.opcode.OPCode.SHR;
import static com.hahn.basic.intermediate.opcode.OPCode.SUB;
import static com.hahn.basic.intermediate.opcode.OPCode.SUBE;
import static com.hahn.basic.intermediate.opcode.OPCode.XOR;
import static com.hahn.basic.intermediate.opcode.OPCode.XORE;

import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.util.exceptions.CompileException;

public class LiteralNum extends Literal {
    public static final LiteralNum ZERO = new LiteralNum(0);
    public static final LiteralNum ONE  = new LiteralNum(1);
    
    public static final LiteralNum NULL  = new LiteralNum(1, Type.UNDEFINED);
    public static final LiteralNum UNDEFINED  = new LiteralNum(0, Type.UNDEFINED);
    
    private double value;
    
    public LiteralNum(int value) {
        this(value, Type.INT);
    }
    
    public LiteralNum(char value) {
        this(value, Type.CHAR);
    }
    
    public LiteralNum(double value) {
    	this(value, Type.FLOAT);
    }
    
    public LiteralNum(double value, Type type) {
        super(type);
        
        this.value = value;
    }
    
    @Override
    public boolean updateLiteral(OPCode op, Literal lit) {    	
    	// Do operation
        if (op == ADD || op == ADDE)      { isnum(op, lit); this.value += lit.getValue(); }
        else if (op == SUB || op == SUBE) { isnum(op, lit); this.value -= lit.getValue(); }
        else if (op == MUL || op == MULE) { isnum(op, lit); this.value *= lit.getValue(); }
        else if (op == DIV || op == DIVE) { isnum(op, lit); this.value /= lit.getValue(); }
        else if (op == MOD || op == MODE) { isnum(op, lit); this.value %= lit.getValue(); }
        else if (op == BOR || op == BORE) { isint(op, lit); this.value = (int) this.value | (int) lit.getValue(); }
        else if (op == AND || op == ANDE) { isint(op, lit); this.value = (int) this.value & (int) lit.getValue(); }
        else if (op == XOR || op == XORE) { isint(op, lit); this.value = (int) this.value ^ (int) lit.getValue(); }
        else if (op == SHL)               { isint(op, lit); this.value = (int) this.value << (int) lit.getValue(); }
        else if (op == SHR)               { isint(op, lit); this.value = (int) this.value >> (int) lit.getValue(); }
        else return false; // Not a modifiable op code
        
        mergeTypes(lit);
        
        return true; // Did modify
    }
    
    private void mergeTypes(Literal lit) {
    	if (lit.getType().doesExtend(Type.FLOAT)) {
    		this.setType(lit.getType());
    	} else if (this.getType().doesExtend(Type.INT)) {
    	    this.value = (int) this.value;
    	}
    }
    
    private void isnum(OPCode op, Literal lit) {
    	if (!lit.getType().doesExtend(Type.INT) && !lit.getType().doesExtend(Type.FLOAT)) {
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
    public String toString() {
        if (this == LiteralNum.NULL) {
            return "null";
        } else if (getType().doesExtend(Type.INT) || value % 1.0 == 0) {
        	return String.valueOf((int) value);
        } else {
        	return String.valueOf(value);
        }
    }
}
