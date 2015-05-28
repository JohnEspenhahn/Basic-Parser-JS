package com.hahn.basic.intermediate.objects;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Statement;
import com.hahn.basic.parser.Node;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.js.JSPretty;
import com.hahn.basic.util.exceptions.CompileException;
import com.sun.istack.internal.Nullable;

/**
 * An object that performs an operation on one or two parameters
 * @author John Espenhahn
 *
 */
public class OPObject extends BasicObject implements IFileObject {
    private Frame frame;
    private OPCode opcode;
    
    private Node p1Node;
    private BasicObject p1;
    private boolean isLiteral;
    
    private Node p2Node;
    private BasicObject p2;
    
    /**
     * Create a new operation object
     * @param container The container of this object
     * @param op The operation to perform
     * @param p1 The nonnull first parameter
     * @param p1Node Used when throwing errors related to p1
     * @param p2 The nullable second parameter
     * @param p2Node Used when throwing errors related to p2
     */
    public OPObject(Statement container, OPCode op, BasicObject p1, Node p1Node, @Nullable BasicObject p2, @Nullable Node p2Node) {
        super("@" + op + "@", p1.getType());
        
        this.isLiteral = false;
        this.frame = container.getFrame();
        
        this.opcode = op;
        
        this.p1 = p1;
        this.p1Node = p1Node;
        
        this.p2 = p2;
        this.p2Node = p2Node;
    }
    
    @Override
    public boolean isExpression() {
        return true;
    }
    
    @Override
    public boolean hasLiteral() {
        return isLiteral;
    }
    
    @Override
    public boolean isPrefixIncDec() {
        return OPCode.isPrefixIncDec(opcode);
    }
    
    @Override
    public boolean canUpdateLiteral(Frame frame, OPCode op) {
        return hasLiteral() && getP1().canUpdateLiteral(frame, op);
    }
    
    @Override
    public boolean updateLiteral(OPCode op, Literal lit, CodeFile file) {
        return getP1().updateLiteral(op, lit, file);
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
    public boolean isGrouped() {
        return getP1().isExpression() || (getP2() != null && getP2().isExpression());
    }
    
    public Frame getFrame() {
        return frame;
    }
    
    @Override
    public CodeFile getFile() {
        return getFrame().getFile();
    }
    
    @Override
    public CommandFactory getFactory() {
        return getFile().getFactory();
    }
    
    public BasicObject getP1() {
        return p1;
    }
    
    public Node getP1Node() {
        return p1Node;
    }
    
    public BasicObject getP2() {
        return p2;
    }
    
    public Node getP2Node() {
        return p2Node;
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
    
    public void setOP(OPCode o) {
        opcode = o;
    }
    
    public String getTargetOPSymbol() {
        return getFactory().getTargetOPSymbol(getOP());
    }
    
    @Override
    public boolean setInUse(IIntermediate by) {        
        // Set in use
        p1.setInUse(this);        
        if (p2 != null) p2.setInUse(this);
        
        // Special case to prevent "++1"
        if ((opcode == OPCode.PADD || opcode == OPCode.PSUB) && !p1.isVar()) {
            throw new CompileException("Illegal left-side argument `" + p1 + "` with operator `" + opcode.symbol + "`", getFile());
        }
        
        // Type check
        Type mergedType = p1.getType().autocast(opcode.type1, p1Node.getFile(), p1Node.getRow(), p1Node.getCol(), true);
        setType(mergedType);
        
        if (p2 != null) { 
            p2.getType().autocast(opcode.type2, p2Node.getFile(), p2Node.getRow(), p2Node.getCol(), true);
        }
        
        return false;
    }
    
    @Override
    public void takeRegister(IIntermediate by) {      
        // Check registers
        if (p2 != null) p2.takeRegister(this);
        p1.takeRegister(this);
        
        // Check literals
        if (p1.hasLiteral() && OPCode.canChangeLiteral(getOP())) {
            Literal p2Literal = (p2 == null ? null : p2.getLiteral());
            if (p1.canUpdateLiteral(getFrame(), getOP()) && p1.updateLiteral(opcode, p2Literal, getFile())) {
                this.isLiteral = true;
            } else {
                p1.setLiteral(null);
            }
        }
        
        if (p2 != null && p2.hasLiteral() && !p2.canLiteralSurvive(getFrame())) {
            p2.setLiteral(null);
        }
    }
    
    /**
     * Called by toTarget is the object is still valid. Should
     * handle conditions if p2 is not null and if p2 is null
     * @return A final form of the object
     */
    public String doToTarget() {
        if (getP2() != null) {
            return JSPretty.format("%s_%s_%s",
                    getP1().isGrouped() ? "("+getP1().toTarget()+")" : getP1().toTarget(),
                    getTargetOPSymbol(), 
                    getP2().isGrouped() ? "("+getP2().toTarget()+")" : getP2().toTarget()
                   );
        } else {
            return JSPretty.format("%s%s",
                    getTargetOPSymbol(),
                    getP1().isExpression() ? "("+getP1().toTarget()+")" : getP1().toTarget()
                   );
        }
    }  
    
    @Override
    public final String toTarget() {
        if (hasLiteral()) {
            if (OPCode.doesModify(getOP())) return "";
            else return p1.toTarget();
        } else {
            return doToTarget();
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
