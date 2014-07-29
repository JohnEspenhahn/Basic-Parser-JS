package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.target.LangBuildTarget;

public abstract class OPObject extends BasicObject {
    private Frame frame;
    private OPCode opcode;
    private BasicObject p1, p2;
    
    public OPObject(Statement container, OPCode opcode, BasicObject p1, BasicObject p2) {
        super("@ " + opcode.toString() + " @", p1.getType());
        
        this.frame = container.getFrame();
        
        this.opcode = opcode;
        this.p1 = (p1 != null ? p1.getForUse(container) : null);
        this.p2 = (p2 != null ? p2.getForUse(container) : null);
    }
    
    @Override
    public boolean hasLiteral() {
        return p1.hasLiteral();
    }
    
    @Override
    public boolean canUpdateLiteral(Frame frame) {
        return p1.canUpdateLiteral(frame) && p1.hasLiteral();
    }
    
    @Override
    public boolean updateLiteral(OPCode op, Literal lit) {
        return p1.updateLiteral(op, lit);
    }
    
    public Frame getFrame() {
        return frame;
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
    public abstract BasicObject getForUse(Statement s);
    
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
    public void takeRegister(IIntermediate by) {
        // Check registers
        p2.takeRegister(this);
        if (p2 instanceof PopObject) StackRegister.pop();
        
        p1.takeRegister(this);
        if (p1 instanceof PushObject) StackRegister.push();
        
        // Check literals
        if (p1.hasLiteral() && p2 != null) {
            if (p1.canUpdateLiteral(getFrame()) && p2.hasLiteral()) {
                p1.updateLiteral(opcode, p2.getLiteral());
            } else {
                p1.setLiteral(null);
            }
        }
        
        if (p2 instanceof AdvancedObject && p2.hasLiteral()) {
            AdvancedObject advancedP2 = (AdvancedObject) p2;
            if (!advancedP2.isOwnerFrame(getFrame())) {
                advancedP2.setLiteral(null);
            } 
        }
    }
    
    /**
     * Called by toTarget is the object is still valid
     * @param builder
     * @return A final form of the object
     */
    public abstract String doToTarget(LangBuildTarget builder);
    
    @Override
    public final String toTarget(LangBuildTarget builder) {
        if (hasLiteral()) {
            return p1.toTarget(builder);
        } else {
            return doToTarget(builder);
        }
    }
    
    @Override
    public String toString() {
        if (hasLiteral()) {
            return p1.toString();
        } else if (p2 != null) {
            return String.format("%s({%s}->%s, {%s}->%s);", opcode, p1.getName() + (isP1LastUse()?"*":""), p1, p2.getName() + (isP2LastUse()?"*":""), p2);
        } else if (p1 != null) {
            return String.format("%s({%s}->%s)", opcode, p1.getName() + (isP1LastUse()?"*":""), p1);
        } else {
            return opcode.toString();
        }
    }
}
