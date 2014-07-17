package com.hahn.basic.intermediate.statements;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.register.Register;
import com.hahn.basic.intermediate.register.StackRegister;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.asm.raw.ASMCommand;

public class Command extends Compilable {  
    protected OPCode opcode;
    protected BasicObject p1, p2;
    
    protected boolean p1LastUse, p2LastUse;
    
    public Command(Statement s, OPCode opcode) {
        this(s, opcode, null, null);
    }
    
    /**
     * @param s The owning statement
     * @param opcode The command op code
     * @param p Sets this.p1 = p.getForUse(s)
     */
    public Command(Statement s, OPCode opcode, BasicObject p) {
        this(s, opcode, p, null);
    }
    
    /**
     * @param s The owning statement
     * @param opcode The command op code
     * @param p1 Sets this.p1 = p1.getForUse(s)
     * @param p2 Sets this.p2 = p2.getForUse(s)
     */
    public Command(Statement s, OPCode opcode, BasicObject p1, BasicObject p2) {
        super(s.getFrame());
        
        this.opcode = opcode;
        this.p1 = (p1 != null ? p1.getForUse(s) : null);
        this.p2 = (p2 != null ? p2.getForUse(s) : null);
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
        return p1LastUse;
    }
    
    public boolean isP2LastUse() {
        return p2LastUse;
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
            p1LastUse = p1.setInUse();
        }
        
        if (p2 != null) { 
            Type.merge(opcode.type2, p2.getType()); 
            p2LastUse = p2.setInUse();
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
                ao1.setLiteral(null);
            }
        }
        
        if (p2 instanceof AdvancedObject && p2.hasLiteral()) {
            AdvancedObject ao2 = (AdvancedObject) p2;
            if (!ao2.isOwnerFrame(getFrame())) {
                ao2.setLiteral(null);
            } 
        }
        
        // Check registers
        if (p2 instanceof AdvancedObject) { ((AdvancedObject) p2).takeRegister(p2LastUse); }
        else if (p2 == Register.POP) StackRegister.pop();
        
        if (p1 instanceof AdvancedObject) { ((AdvancedObject) p1).takeRegister(p1LastUse); }
        else if (p1 == Register.PUSH) StackRegister.push();

        return false;
    }
    
    @Override
    public void toTarget(LangBuildTarget builder) {        
        if (p1 != null && p2 != null)
            builder.append(new ASMCommand(opcode, p1.toTarget(), p2.toTarget()));
        else if (p1 != null)
            builder.append(new ASMCommand(opcode, p1.toTarget()));
        else
            builder.append(new ASMCommand(opcode));
            
    }
    
    @Override
    public String toString() {
        if (p2 != null) {
            String str = String.format("%s %s, %s", opcode, p1, p2);
            return str +
                    (Main.DEBUG ? ";\tREM " + opcode + " " + p1.getName() + (p1LastUse?"*":"") + ", " + p2.getName() + (p2LastUse ? "*" : "") : "");
        } else if (p1 != null) {
            String str = String.format("%s %s", opcode, p1);
            return str +
                    (Main.DEBUG ? ";\tREM " + opcode + " " + p1.getName() + (p1LastUse?"*":"") : "");
        } else {
            return opcode.toString();
        }
    }
}
