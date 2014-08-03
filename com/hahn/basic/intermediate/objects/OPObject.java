package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.LangBuildTarget;
import com.sun.istack.internal.Nullable;

public abstract class OPObject extends BasicObject {
    private Frame frame;
    private OPCode opcode;
    
    private Node p1Node;
    private BasicObject p1;
    private boolean isLiteral;
    
    private Node p2Node;
    private BasicObject p2;
    
    public OPObject(Statement container, OPCode op, BasicObject p1, Node p1Node, @Nullable BasicObject p2, @Nullable Node p2Node) {
        super("@ " + op + " @", p1.getType());
        
        this.isLiteral = false;
        this.frame = container.getFrame();
        
        this.opcode = op;
        
        this.p1 = p1;
        this.p1Node = p1Node;
        
        this.p2 = p2;
        this.p2Node = p2Node;
    }
    
    @Override
    public boolean hasLiteral() {
        return isLiteral;
    }
    
    @Override
    public boolean canUpdateLiteral(Frame frame, OPCode op) {
        return getP1().canUpdateLiteral(frame, op) && hasLiteral();
    }
    
    @Override
    public boolean updateLiteral(OPCode op, Literal lit) {
        return getP1().updateLiteral(op, lit);
    }
    
    @Override
    public Literal getLiteral() {
        if (hasLiteral()) {
            return getP1().getLiteral();
        } else {
            return null;
        }
    }
    
    @Override
    public boolean isExpression() {
        return true;
    }
    
    @Override
    public boolean isGrouped() {
        return getP1().isExpression() || (getP2() != null && getP2().isExpression());
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
        return p2 != null && p2.isLastUse(this);
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
        if (p1 != null) p1.setInUse(this);        
        if (p2 != null) p2.setInUse(this);
        
        // Type check
        if (p1 != null) { 
            Type mergedType = Type.merge(opcode.type1, p1.getType(), p1Node.getRow(), p1Node.getCol());
            this.setType(mergedType);
        }
        
        if (p2 != null) { 
            Type.merge(opcode.type2, p2.getType(), p2Node.getRow(), p2Node.getCol());
        }
        
        return false;
    }
    
    @Override
    public void takeRegister(IIntermediate by) {      
        // Check registers
        if (p2 != null) p2.takeRegister(this);
        p1.takeRegister(this);
        
        // Check literals
        if (p1.hasLiteral() && p2 != null && OPCode.canChangeLiteral(getOP())) {
            if (p1.canUpdateLiteral(getFrame(), getOP()) && p2.hasLiteral()
                    && p1.updateLiteral(opcode, p2.getLiteral())) {
                this.isLiteral = true;
            } else {
                p1.setLiteral(null);
            }
        }
        
        if (p2.hasLiteral() && !p2.canLiteralSurvive(getFrame())) {
            p2.setLiteral(null);
        }
    }
    
    /**
     * Called by toTarget is the object is still valid. Should
     * handle conditions if p2 is not null and if p2 is null
     * @param builder
     * @return A final form of the object
     */
    public abstract String doToTarget(LangBuildTarget builder);
    
    @Override
    public final String toTarget(LangBuildTarget builder) {
        if (hasLiteral()) {
            if (OPCode.doesModify(getOP())) return "";
            else return p1.toTarget(builder);
        } else {
            return doToTarget(builder);
        }
    }
    
    @Override
    public String toString() {
        if (hasLiteral()) {
            if (OPCode.doesModify(getOP())) return "";
            else return p1.toString();
        } else if (p2 != null) {
            return String.format("%s({%s}->%s, {%s}->%s);", opcode, p1.getName() + (isP1LastUse()?"*":""), p1, p2.getName() + (isP2LastUse()?"*":""), p2);
        } else if (p1 != null) {
            return String.format("%s({%s}->%s)", opcode, p1.getName() + (isP1LastUse()?"*":""), p1);
        } else {
            return opcode.toString();
        }
    }
}
