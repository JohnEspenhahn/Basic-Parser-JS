package com.hahn.basic.intermediate.register;


public class StackRegister extends IRegister {
    private static int NEXT_IDX = 0;
    
    protected int idx;    
    protected StackRegister(int idx) {
        super("[SP + #]");
        
        this.idx = idx;
    }
    
    public int getOffset() {
        return StackRegisterSnapshot.getForcedOffset() + NEXT_IDX-1 - idx;
    }
    
    @Override
    public void release() {
        // Done within frame
    }
    
    @Override
    public boolean isOnStack() {
        return true;
    }
    
    @Override
    public IRegister snapshot() {
        return new StackRegisterSnapshot(getOffset());
    }
    
    @Override
    public String toString() {
        int offset = getOffset();
        if (offset == 0) {
            return "PEEK";
        } else {
            return "[SP + " + offset + "]";
        }
    }
    
    
    /////////////////////
    // Statics
    /////////////////////
    
    public static StackRegister peek() {
        return new StackRegister(NEXT_IDX);
    }
    
    public static StackRegister next() {
        return new StackRegister(NEXT_IDX++);
    }
    
    public static void push() {
        NEXT_IDX += 1;
    }
    
    public static void push(int amnt) {
        NEXT_IDX += amnt;
    }
    
    public static void pop() {
        NEXT_IDX -= 1;
    }
    
    public static void pop(int amnt) {
        NEXT_IDX -= amnt;
    }
}
