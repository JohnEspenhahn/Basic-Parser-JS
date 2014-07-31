package com.hahn.basic.target.js.objects.register;

import java.util.ArrayDeque;
import java.util.Deque;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.register.Register;

public class JSRegister extends Register {
    private static final Deque<JSRegister> free = new ArrayDeque<JSRegister>();
    private static int next_idx = 0;
    
    protected JSRegister() {
        this(idxToName(next_idx));
        
        next_idx += 1;
    }
    
    protected JSRegister(String name) {
        super(name);
    }
    
    @Override
    public void release() {
        super.release();
        
        free.push(this);
    }
    
    public static void init() {
        JSRegister.next_idx = 0;
    }
    
    public static JSRegister getForObject(AdvancedObject obj) {
        if (obj.isLocal()) {
            return JSRegister.getNextFree(obj.getFrame());
        } else {
            JSRegister reg = new JSRegister(obj.getName());
            reg.reserve();
        
            return reg;
        }
    }
    
    public static JSRegister getNextFree(Frame frame) {
        AdvancedObject testVar = null;
        
        JSRegister first = null, reg;
        if (free.size() > 0) reg = first = free.removeLast();
        else reg = new JSRegister();
        
        do {
            free.addFirst(reg);
            
            if (free.peek() != first) reg = free.removeLast();
            else reg = new JSRegister();
        } while ((testVar = frame.safeGetVar(reg.getName())) != null && !testVar.isLocal());
        
        reg.reserve();        
        return reg;
    }
    
    public static String idxToName(int i) {
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
