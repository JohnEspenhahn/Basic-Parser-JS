package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.ClassType;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.BitFlag;
import com.hahn.basic.util.exceptions.CompileException;

public class VarSuper extends Var {
    private static final int FLAGS = BitFlag.CONST.b;
    
    public VarSuper(Frame frame, ClassType type) {
        super(frame, "super", type, FLAGS);
    }
    
    @Override
    public boolean isLocal() {
        return false;
    }
    
    @Override
    public boolean isVarSuper() {
        return false;
    }
    
    @Override
    public BasicObject castTo(Type type, int row, int col) {
        throw new CompileException("Cannot cast `super`", row, col);
    }
}
