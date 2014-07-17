package com.hahn.basic.intermediate.opcode;

import static com.hahn.basic.intermediate.objects.types.Type.UINT;

import com.hahn.basic.intermediate.objects.types.Type;

public enum OPCode {
    ADD ("+" , UINT, UINT,  0b101101, 4, 6), 
    SUB ("-" , UINT, UINT,  0b101110, 4, 6), 
    MUL ("*" , UINT, UINT,  0b101111, 4, 6),
    DIV ("/" , UINT, UINT,  0b110001, 4, 6),
    MOD ("%" , UINT, UINT,  0b110010, 4, 6),
    SHR (">>", UINT, UINT,  0b111000, 4, 6),
    SHL ("<<", UINT, UINT,  0b111010, 4, 6),
    AND ("&" , UINT, UINT,  0b110101, 4, 6),
    BOR ("|" , UINT, UINT,  0b110110, 4, 6),
    XOR ("^" , UINT, UINT,  0b110111, 4, 6),
    SET ("=" , null, null,  0b0010  , 6, 6),
    IFE ("==", null, null,  0b100010, 4, 6), 
    IFN ("!=", null, null,  0b100011, 4, 6), 
    IFP (">=", UINT , UINT, 0b100100, 4, 6),
    IFG (">" , UINT , UINT, 0b100101, 4, 6),
    IFM ("<=", UINT , UINT, 0b100111, 4, 6),
    IFL ("<" , UINT , UINT, 0b101000, 4, 6),
    IFA (      UINT , UINT, 0b100110, 4, 6),
    IFU (      UINT , UINT, 0b101001, 4, 6),
    SPA (UINT, 0b0001101011, 6, 0),
    JSR (UINT, 0b0001000000, 6, 0),
    MOV (UINT, 0b0001001101, 6, 0),
    HSG (UINT, 0b0001100011, 6, 0),
    HWI (UINT, 0b0001011011, 6, 0),
    RFI (UINT, 0b0001010101, 6, 0),
    IAS (UINT, 0b0001010000, 6, 0),
    BRK (null, 0b0000100000011100, 0, 0),
    HNG (null, 0b0000100000001000, 0, 0),
    POPA(null, 0b0000100000001100, 0, 0),
    PUSHA(null,0b0000100000001011, 0, 0);

    
    /* Types of the parameters */
    public final Type type1, type2;
    
    public final String symbol;
    
    /* For converting to bytecode */
    public final int bytecode, p1Lng, p2Lng;

    private OPCode(Type type1, int bits, int p1Bits, int p2Bits) {
        this(null, type1, null, bits, p1Bits, p2Bits);
    }
    
    private OPCode(Type type1, Type type2, int bits, int p1Bits, int p2Bits) {
        this(null, type1, type2, bits, p1Bits, p2Bits);
    }

    private OPCode(String symbol, Type type1, Type type2, int cmdBits, int p1Lng, int p2Lng) {
        this.symbol = symbol;
        this.type1 = type1;
        this.type2 = type2;
        
        this.p1Lng = p1Lng;
        this.p2Lng = p2Lng;
        this.bytecode = cmdBits << (p1Lng + p2Lng);
    }
    
    public String getName() {
        return name();
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public Type getType1() {
        return type1;
    }
    
    public Type getType2() {
        return type2;
    }
    
    public static OPCode fromName(String name) {
        for (OPCode op: OPCode.values()) {
            if (op.name().equals(name)) {
                return op;
            }
        }
        
        return null;
    }

    public static OPCode fromSymbol(String symbol) {
        for (OPCode op : OPCode.values()) {
            if (op.symbol != null && op.symbol.equals(symbol)) { return op; }
        }

        throw new RuntimeException("Unknown op code with symbol '" + symbol + "'!");
    }

    public static OPCode invert(OPCode code) {
        if (code == IFE)
            return IFN;
        else if (code == IFN)
            return IFE;
        else if (code == IFP)
            return IFL;
        else if (code == IFL)
            return IFP;
        else if (code == IFM)
            return IFG;
        else if (code == IFG)
            return IFM;
        else
            throw new RuntimeException("Unhandled opposite of '" + code + "'");
    }
    
    public static boolean isConditional(OPCode op) {
        return (op == IFE || op == IFN || op == IFP || op == IFL || op == IFM || op == IFG);
    }
    
    public static boolean isBitwise(OPCode op) {
        return (op == AND || op == BOR || op == XOR);
    }
    
    public static boolean isNonModify(OPCode op) {
        return isConditional(op);
    }
}
