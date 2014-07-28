package com.hahn.basic.target.js.objects.register;

import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.register.Register;

public class JSRegister extends Register {
    
    protected JSRegister(String name) {
        super(name);
    }
    
    public static JSRegister getForObject(AdvancedObject obj) {
        JSRegister reg = new JSRegister(obj.getName());
        reg.reserve();
        
        return reg;
    }
}
