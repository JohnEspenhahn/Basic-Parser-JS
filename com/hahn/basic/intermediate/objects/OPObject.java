package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public abstract class OPObject extends AdvancedObject {
    private OPCode opcode;
    private BasicObject p1, p2;
    
    private boolean isValid;
    
    public OPObject(Statement container, OPCode opcode, BasicObject p1, BasicObject p2) {
        super(container.getFrame(), "@ " + opcode.toString() + " @", p1.getType());
        
        this.isValid = true;
        
        this.opcode = opcode;
        this.p1 = (p1 != null ? p1.getForUse(container) : null);
        this.p2 = (p2 != null ? p2.getForUse(container) : null);
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public BasicObject getP1() {
        return p1;
    }
    
    public BasicObject getP2() {
        return p2;
    }
    
    public OPCode getOP() {
        return opcode;
    }
    
    public boolean isP1LastUse() {
        return p1.isLastUse(this);
    }
    
    public boolean isP2LastUse() {
        return p2.isLastUse(this);
    }
    
    /**
     * @param o Set this.p1 = o (does not call getForUse)
     */
    public void forceP1(BasicObject o) {
        p1 = o;
    }
    
    /**
     * @param o Set this.p2 = o (does not call getForUse)
     */
    public void forceP2(BasicObject o) {
        p2 = o;
    }
    
    public void setOP(OPCode o) {
        opcode = o;
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {
        // Type check
        if (p1 != null) { 
            Type mergedType = Type.merge(opcode.type1, p1.getType());
            this.setType(mergedType);
            
            p1.setInUse(this);
        }
        
        if (p2 != null) { 
            Type.merge(opcode.type2, p2.getType()); 
            p2.setInUse(this);
        }
        
        return false;
    }
    
    @Override
    public void doTakeRegister(boolean isLastUse) {
        // Check literals
        if (p1 instanceof AdvancedObject && p1.hasLiteral()) {
            AdvancedObject ao1 = (AdvancedObject) p1;
            if (p2.hasLiteral() && ao1.isOwnerFrame(getFrame())) {
                if (ao1.updateLiteral(opcode, p2.getLiteral())) {
                    // This expression is no longer needed
                    this.isValid = false;
                    return;
                }
            } else if (p2 != null) {
                p1.setLiteral(null);
            }
        }
        
        if (p2 instanceof AdvancedObject && p2.hasLiteral()) {
            AdvancedObject ao2 = (AdvancedObject) p2;
            if (!ao2.isOwnerFrame(getFrame())) {
                ao2.setLiteral(null);
            } 
        }
        
        // Check registers
        if (p2 instanceof AdvancedObject) { ((AdvancedObject) p2).takeRegister(this); }
        else if (p2 instanceof PopObject) StackRegister.pop();
        
        if (p1 instanceof AdvancedObject) { ((AdvancedObject) p1).takeRegister(this); }
        else if (p1 instanceof PushObject) StackRegister.push();
    }
    
    public abstract String doToTarget(LangBuildTarget builder);
    
    @Override
    public final String toTarget(LangBuildTarget builder) {
        if (isValid()) {
            return doToTarget(builder);
        } else {
            return "";
        }
    }
    
    @Override
    public String toString() {
        if (p2 != null) {
            return String.format("%s({%s}->%s, {%s}->%s);", opcode, p1.getName() + (isP1LastUse()?"*":""), p1, p2.getName() + (isP2LastUse()?"*":""), p2);
        } else if (p1 != null) {
            return String.format("%s({%s}->%s)", opcode, p1.getName() + (isP1LastUse()?"*":""), p1);
        } else {
            return opcode.toString();
        }
    }
}
