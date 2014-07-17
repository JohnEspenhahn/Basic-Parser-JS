package com.hahn.basic.intermediate.register;


public class StackRegisterSnapshot extends StackRegister {
    private static int FORCED_OFFSET = 0;
    
    public StackRegisterSnapshot(int offset) {
        super(offset);
    }

    @Override
    public final int getOffset() {
        return idx + FORCED_OFFSET;
    }
    
    @Override
    public final IRegister snapshot() {
        return this;
    }
    
    public static void setForcedOffset(int i) {
        FORCED_OFFSET = i;
    }
    
    public static void resetForcedOffset() {
        FORCED_OFFSET = 0;
    }

    public static int getForcedOffset() {
        return FORCED_OFFSET;
    }
}
