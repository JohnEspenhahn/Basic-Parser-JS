package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.function.FuncHead;

public class FuncCallPair {
    private final FuncHead func;
    private final BasicObject objectIn;
    
    public FuncCallPair(FuncHead func, BasicObject objIn) {
        this.func = func;
        this.objectIn = objIn;
    }
    
    public FuncHead getFunc() {
        return func;
    }
    
    public BasicObject getObjectIn() {
        return objectIn;
    }
}
