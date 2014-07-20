package com.hahn.basic.intermediate.objects;

import java.util.ArrayDeque;
import java.util.Deque;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.Statement;

public abstract class ExpressionObject extends ObjectPointer {
    private Deque<Compilable> code;
    
    public ExpressionObject(Frame frame, BasicObject obj) {
        super(frame, obj);
        
        code = new ArrayDeque<Compilable>();
    }
    
    public void addCode(Compilable c) {
        this.code.addLast(c);
    }
    
    @Override
    public AdvancedObject getForUse(Statement s) {
        for (Compilable c: code) {
            s.addCode(c);
        }
        
        return super.getForUse(s);
    }
    
    @Override
    public abstract String toTarget();
    
    @Override
    public String toString() {
        return "{...}" + getObj();
    }
}
