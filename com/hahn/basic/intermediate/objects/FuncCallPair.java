package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.function.FuncHead;

public class FuncCallPair {
    private final FuncHead func;
    private final IBasicObject objectIn;
    
    public FuncCallPair(FuncHead func, IBasicObject objIn) {
        this.func = func;
        this.objectIn = objIn;
    }
    
    public FuncHead getFunc() {
        return func;
    }
    
    public IBasicObject getObjectIn() {
        return objectIn;
    }
}
