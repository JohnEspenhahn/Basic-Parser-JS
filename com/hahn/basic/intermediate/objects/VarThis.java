package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.util.structures.BitFlag;

public class VarThis extends Var {
    private static final int FLAGS = BitFlag.CONST.b;
    
    public VarThis(Frame frame, ClassType type) {
        super(frame, "this", type, FLAGS);
    }
    
    @Override
    public int getVarThisFlag() {
        return IS_THIS;
    }
    
    @Override
    public boolean isLocal() {
        return false;
    }
    
    @Override
    public BasicObject castTo(Type type, CodeFile file, int row, int col) {
        throw new CompileException("Cannot cast `this`", file, row, col);
    }
}
