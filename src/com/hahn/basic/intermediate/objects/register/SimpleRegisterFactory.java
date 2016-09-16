package com.hahn.basic.intermediate.objects.register;

import java.util.ArrayDeque;
import java.util.Deque;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.IBasicObject;

public class SimpleRegisterFactory {  
    private final Deque<SimpleRegister> free;
    private int next_idx;
    
    public SimpleRegisterFactory() {
        free = new ArrayDeque<SimpleRegister>();
        next_idx = 0;
    }
    
    public void reset() {
        free.clear();
        next_idx = 0;
    }
    
    protected void release(SimpleRegister r) {
        free.push(r);
    }
    
    public SimpleRegister getForObject(AdvancedObject obj) {
        if (obj.isLocal()) {
            return getNextFree(obj.getFrame());
        } else {
            SimpleRegister reg = new SimpleRegister(obj.getName(), this);
            reg.reserve();
        
            return reg;
        }
    }
    
    protected SimpleRegister getNextFree(Frame frame) {
        IBasicObject testVar = null;
        
        SimpleRegister first = null, reg;
        if (free.size() > 0) reg = first = free.removeLast();
        else reg = getNextByIndex();
        
        do {
            free.addFirst(reg);
            
            if (free.peek() != first) reg = free.removeLast();
            else reg = getNextByIndex();
        } while ((testVar = frame.safeGetVar(reg.getName())) != null && !testVar.isLocal());
        
        reg.reserve();        
        return reg;
    }
    
    private SimpleRegister getNextByIndex() {
        return new SimpleRegister(idxToName(next_idx++), this);
    }
    
    private String idxToName(int i) {
        char[] char1 = getFirstChars();
        char[] charN = getNthChars();
        
        int remainder = i % char1.length;
        String str = String.valueOf(char1[remainder]);
        i = (i - remainder) / char1.length;
        
        while (i > 0) {
            remainder = i % charN.length;
            str += charN[remainder];
            i = (i - remainder) / charN.length;
        }
        
        return str;
    }
    
    public char[] getFirstChars() {
        return char1;
    }
    
    public char[] getNthChars() {
        return charN;
    }
    
    private static final char[] char1 = new char[] {
        'a','b','c','d','e','f','g','h','i','j','k','l',
        'm','n','o','p','q','r','s','t','u','v','w','x',
        'y','z','A','B','C','D','E','F','G','H','I','J',
        'K','L','M','N','O','P','Q','R','S','T','U','V',
        'W','X','Y','Z','_','$'
    };
    
    private static final char[] charN = new char[] {
        'a','b','c','d','e','f','g','h','i','j','k','l',
        'm','n','o','p','q','r','s','t','u','v','w','x',
        'y','z','A','B','C','D','E','F','G','H','I','J',
        'K','L','M','N','O','P','Q','R','S','T','U','V',
        'W','X','Y','Z','0','1','2','3','4','5','6','7',
        '8','9','$'
    };
}
