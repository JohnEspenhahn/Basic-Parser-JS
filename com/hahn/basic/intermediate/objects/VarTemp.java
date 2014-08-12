package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.types.Type;

public class VarTemp extends Var {
    public static int NEXT_TEMP_VAR = 0;

    public VarTemp(Frame frame, Type type) {
        super(frame, getNextTempName(), type, 0);
    }

    @Override
    public boolean isTemp() {
        return true;
    }
    
    @Override
    public boolean isLocal() {
        return true;
    }
    
    public static String getNextTempName() {
        return "_@" + VarTemp.NEXT_TEMP_VAR++;
    }
}
