package com.hahn.basic.intermediate.opcode;

import static com.hahn.basic.intermediate.objects.types.Type.BOOL;
import static com.hahn.basic.intermediate.objects.types.Type.REAL;
import static com.hahn.basic.intermediate.objects.types.Type.REALSTR;
import static com.hahn.basic.intermediate.objects.types.Type.UNDEFINED;

import com.hahn.basic.intermediate.objects.types.Type;

public enum OPCode {
    NOT ("!" , BOOL, null, 0b000000, 0, 0),
    SAND("&&", BOOL, BOOL, 0b000000, 0, 0),
    SBOR("||", BOOL, BOOL, 0b000000, 0, 0),
    
    ADD ("+" , REALSTR, REALSTR,  0b101101, 4, 6),
    ADDE("+=", REALSTR, REALSTR,  0b101101, 4, 6),
    PADD("++", REAL   , null   ,  0b000000, 0, 0),
    SUB ("-" , REAL   , REAL   ,  0b101110, 4, 6),
    SUBE("-=", REAL   , REAL   ,  0b101110, 4, 6),
    PSUB("--", REAL   , null   ,  0b000000, 0, 0),
    MUL ("*" , REAL   , REAL   ,  0b101111, 4, 6),
    MULE("*=", REAL   , REAL   ,  0b101111, 4, 6),
    DIV ("/" , REAL   , REAL   ,  0b110001, 4, 6),
    DIVE("/=", REAL   , REAL   ,  0b110001, 4, 6),
    MOD ("%" , REAL   , REAL   ,  0b110010, 4, 6),
    MODE("%=", REAL   , REAL   ,  0b110010, 4, 6),
    BNOT("~" , REAL   , null   ,  0b000000, 0, 0),
    INT ("#" , REAL   , null   ,  0b000000, 0, 0),
    SHR (">>", REAL   , REAL   ,  0b111000, 4, 6),
    SHL ("<<", REAL   , REAL   ,  0b111010, 4, 6),
    AND ("&" , REAL   , REAL   ,  0b110101, 4, 6),
    ANDE("&=", REAL   , REAL   ,  0b110101, 4, 6),
    BOR ("|" , REAL   , REAL   ,  0b110110, 4, 6),
    BORE("|=", REAL   , REAL   ,  0b110110, 4, 6),
    XOR ("^" , REAL   , REAL   ,  0b110111, 4, 6),
    XORE("^=", REAL   , REAL   ,  0b110111, 4, 6),
    SET ("=" , UNDEFINED, UNDEFINED,  0b0010  , 6, 6),
    IFE ("==", UNDEFINED, UNDEFINED,  0b100010, 4, 6),
    IFN ("!=", UNDEFINED, UNDEFINED,  0b100011, 4, 6),
    IFP (">=", REAL    , REAL   , 0b100100, 4, 6),
    IFG (">" , REAL    , REAL   , 0b100101, 4, 6),
    IFM ("<=", REAL    , REAL   , 0b100111, 4, 6),
    IFL ("<" , REAL    , REAL   , 0b101000, 4, 6);

    
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
            throw new RuntimeException("Unhandled opposite of opcode `" + code + "`");
    }
    
    public static boolean isConditional(OPCode op) {
        return (op == IFE || op == IFN || op == IFP || op == IFL || op == IFM || op == IFG);
    }
    
    public static boolean doesModify(OPCode op) {
        return op == SET  || op == ADDE || op == SUBE || op == MULE || op == DIVE
                || op == MODE || op == ANDE || op == BORE || op == XORE || isPrefixIncDec(op);
    }
    
    public static boolean isPrefixIncDec(OPCode op) {
        return op == PADD || op == PSUB;
    }
    
    public static boolean isBitwise(OPCode op) {
        return (op == AND || op == BOR || op == XOR || op == SHR || op == SHL || op == BNOT || op == INT);
    }
    
    public static boolean isSimpleArithmetic(OPCode op) {
        return (op == ADD || op == SUB || op == MUL || op == DIV || op == MOD);
    }
    
    public static boolean canChangeLiteral(OPCode op) {
        return isBitwise(op) || isSimpleArithmetic(op) || doesModify(op);
    }
    
    public static boolean isNonModify(OPCode op) {
        return isConditional(op);
    }
}
