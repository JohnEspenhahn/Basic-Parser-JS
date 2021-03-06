package com.hahn.basic.intermediate.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hahn.basic.intermediate.CodeFile;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.CommandFactory;

public abstract class AdvancedObject extends BasicObject implements IFileObject {
    private Frame frame;

    private IRegister reg;
    private Literal literal;

    private List<AdvancedObject> parallelObjs;

    public AdvancedObject(Frame frame, String name, Type type) {
        super(name, type);

        this.frame = frame;
        this.literal = LiteralNum.UNDEFINED;

        this.parallelObjs = new ArrayList<AdvancedObject>();
    }

    /**
     * Get the owning frame
     * @return The owning frame
     */
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

    /**
     * Get the address of this object
     * @return The address of this object
     */
    public IBasicObject getAddress() {
        return this;
    }

    @Override
    public IBasicObject getForCreateVar() {
        if (isRegisterOnStack()) {
            return getFactory().PushObject();
        } else {
            return super.getForCreateVar();
        }
    }
    
    @Override
    public String toTarget() {
        if (hasLiteral()) {
            return literal.toTarget();
        } else {
            return reg.toTarget();
        }
    }
    
    /*
     * ------------------------------- Use Management -------------------------------
     */
    
    @Override
    public boolean setInUse(IIntermediate by) {
        boolean firstCall = super.setInUse(by);

        if (firstCall && getFrame() != null) {
            frame.addInUseVar(this);
        }

        return firstCall;
    }
    
    /**
     * Get the objects that are used in parallel with this
     * @return List of the objects used in parallel
     */
    protected List<AdvancedObject> getParallelObjs() {
        return parallelObjs;
    }

    /**
     * Add an object that is being used at the same time this is
     * @param obj The object to add
     */
    public void addParallelObj(AdvancedObject obj) {
        if (!parallelObjs.contains(obj)) {
            parallelObjs.add(obj);
        }
    }

    /**
     * Determine if priority for register optimization
     */
    @Override
    public void removeInUse() {
        if (frame != null) {
            frame.removeInUseVar(this);
        }

        // Don't optimize as parallel if rarely used
        if (getUses() < 3) {
            for (AdvancedObject o : this.parallelObjs) {
                o.parallelObjs.remove(this);
            }

            this.parallelObjs.clear();
        } else {
            Collections.sort(parallelObjs, AdvancedObject.SORTER);
        }
    }

    private static final Comparator<AdvancedObject> SORTER = new Comparator<AdvancedObject>() {
        @Override
        public int compare(AdvancedObject v1, AdvancedObject v2) {
            return v2.getUses() - v1.getUses();
        }
    };

    /*
     * ------------------------------- Register Management -------------------------------
     */
    
    /**
     * Called from takeRegister. For most advanced objects do the
     * actual taking of the register.
     * @param isLastUse True if this is the first call from
     * reverse optimize and therefore the last used of this object
     */
    public abstract void doTakeRegister(boolean isLastUse);
    
    @Override
    public void takeRegister(IIntermediate by) {
        doTakeRegister(isLastUse(by));
    }
    
    public IRegister getRegister() {
        return reg;
    }
    
    public boolean hasRegister() {
        return reg != null;
    }

    public boolean isRegisterOnStack() {
        return hasRegister() && reg.isOnStack();
    }

    /**
     * Make the register available for use
     * by other objects
     */
    public void releaseRegister() {
        if (hasRegister()) {
            reg.release();
        }
    }

    /**
     * Stores a snapshot of the given register
     * as this object's register
     * @param reg The register to snapshot and store
     */
    public void setRegister(IRegister reg) {
        this.reg = reg.snapshot();
    }

    /*
     * ------------------------------- Literal Management -------------------------------
     */

    @Override
    public Literal getLiteral() {
        return literal;
    }
    
    @Override
    public boolean canSetLiteral() {
        return literal != null;
    }

    @Override
    public boolean hasLiteral() {
        return canSetLiteral() && literal != LiteralNum.UNDEFINED;
    }
    
    @Override
    public boolean canUpdateLiteral(Frame frame, OPCode op) {
        return hasLiteral() && canLiteralSurvive(frame) && OPCode.doesModify(op);
    }
    
    @Override
    public boolean canLiteralSurvive(Frame f) {
        return (f == getFrame());
    }

    @Override
    public void setLiteral(Literal lit) {
        if (canSetLiteral()) {
            this.literal = lit;
        }
    }

    @Override
    public boolean updateLiteral(OPCode op, Literal lit, CodeFile file) {
        return literal.updateLiteral(op, lit, file);
    }
}
