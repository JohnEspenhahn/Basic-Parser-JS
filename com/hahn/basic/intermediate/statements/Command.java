package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.PopObject;
import com.hahn.basic.intermediate.objects.PushObject;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;

public abstract class Command extends Compilable {  
    protected OPCode opcode;
    protected BasicObject p1, p2;
    
    public Command(Statement container, OPCode opcode) {
        this(container, opcode, null, null);
    }
    
    /**
     * @param container The owning statement
     * @param opcode The command op code
     * @param p Sets this.p1 = p.getForUse(container)
     */
    public Command(Statement container, OPCode opcode, BasicObject p) {
        this(container, opcode, p, null);
    }
    
    /**
     * @param container The owning statement
     * @param opcode The command op code
     * @param p1 Sets this.p1 = p1.getForUse(container)
     * @param p2 Sets this.p2 = p2.getForUse(container)
     */
    public Command(Statement container, OPCode opcode, BasicObject p1, BasicObject p2) {
        super(container.getFrame());
        
        this.opcode = opcode;
        this.p1 = (p1 != null ? p1.getForUse(container) : null);
        this.p2 = (p2 != null ? p2.getForUse(container) : null);
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
    public boolean reverseOptimize() {        
        // Type check
        Main.setLine(row);
        if (p1 != null) { 
            Type.merge(opcode.type1, p1.getType());
            p1.setInUse(this);
        }
        
        if (p2 != null) { 
            Type.merge(opcode.type2, p2.getType()); 
            p2.setInUse(this);
        }
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
        // Check literals
        if (p1 instanceof AdvancedObject && p1.hasLiteral()) {
            AdvancedObject ao1 = (AdvancedObject) p1;
            if (p2.hasLiteral() && ao1.isOwnerFrame(getFrame())) {
                if (ao1.updateLiteral(opcode, p2.getLiteral())) {
                    return true;
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

        return false;
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
