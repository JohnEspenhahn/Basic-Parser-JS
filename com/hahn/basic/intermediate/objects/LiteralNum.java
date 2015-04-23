package com.hahn.basic.intermediate.objects;

import static com.hahn.basic.intermediate.opcode.OPCode.ADD;
import static com.hahn.basic.intermediate.opcode.OPCode.ADDE;
import static com.hahn.basic.intermediate.opcode.OPCode.AND;
import static com.hahn.basic.intermediate.opcode.OPCode.ANDE;
import static com.hahn.basic.intermediate.opcode.OPCode.BNOT;
import static com.hahn.basic.intermediate.opcode.OPCode.BOR;
import static com.hahn.basic.intermediate.opcode.OPCode.BORE;
import static com.hahn.basic.intermediate.opcode.OPCode.DIV;
import static com.hahn.basic.intermediate.opcode.OPCode.DIVE;
import static com.hahn.basic.intermediate.opcode.OPCode.INT;
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
    
    public static final LiteralNum VOID = new LiteralNum(0, Type.VOID);
    public static final LiteralNum NULL  = new LiteralNum(1, Type.UNDEFINED);
    public static final LiteralNum UNDEFINED  = new LiteralNum(0, Type.UNDEFINED);
    
    private double value;
    
    public LiteralNum(double value) {
        this(value, Type.REAL);
    }
    
    private LiteralNum(double value, Type type) {
        super(type);
        
        this.value = value;
    }
    
    @Override
    public boolean updateLiteral(OPCode op, Literal lit) {    	
    	// Do operation
        if (op == SUB && lit == null)     { this.value = -this.value; }
        else if (op == ADD || op == ADDE) { if(isreal(op,lit)) this.value += lit.getValue(); }
        else if (op == SUB || op == SUBE) { if(isreal(op,lit)) this.value -= lit.getValue(); }
        else if (op == MUL || op == MULE) { if(isreal(op,lit)) this.value *= lit.getValue(); }
        else if (op == DIV || op == DIVE) { if(isreal(op,lit)) this.value /= lit.getValue(); }
        else if (op == MOD || op == MODE) { if(isreal(op,lit)) this.value %= lit.getValue(); }
        else if (op == BOR || op == BORE) { if(isreal(op,lit)) this.value = ((int) this.value) | ((int) lit.getValue()); }
        else if (op == AND || op == ANDE) { if(isreal(op,lit)) this.value = ((int) this.value) & ((int) lit.getValue()); }
        else if (op == XOR || op == XORE) { if(isreal(op,lit)) this.value = ((int) this.value) ^ ((int) lit.getValue()); }
        else if (op == SHL )              { if(isreal(op,lit)) this.value = ((int) this.value) << ((int) lit.getValue()); }
        else if (op == SHR )              { if(isreal(op,lit)) this.value = ((int) this.value) >> ((int) lit.getValue()); }
        else if (op == BNOT)              { this.value = ~((int) this.value);  }
        else if (op == INT )              { this.value = (int) this.value;     }
        else return false; // Not a modifiable op code
        
        return true; // Did modify
    }
    
    private boolean isreal(OPCode op, Literal lit) {
        if (lit == null) {
            return false;
        } else if (!lit.getType().doesExtend(Type.REAL)) {
            throw new CompileException("Can not preform operation " + op + " on a `real` and a `" + lit.getType() + "` value");
        } else {
            return true;
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
        } else if (value % 1.0 == 0) {
        	return String.valueOf((int) value);
        } else {
        	return String.valueOf(value);
        }
    }
}
