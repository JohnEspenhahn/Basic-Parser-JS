package com.hahn.basic.intermediate.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.LangBuildTarget;

public abstract class AdvancedObject extends BasicObject {
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

    public boolean isLocal() {
        return false;
    }

    public Frame getFrame() {
        return frame;
    }

    public BasicObject getAddress() {
        return this;
    }

    @Override
    public BasicObject getForCreateVar() {
        if (isRegisterOnStack()) {
            return LangCompiler.factory.PushObject();
        } else {
            return super.getForCreateVar();
        }
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
        if (hasLiteral()) {
            return literal.toTarget(builder);
        } else {
            return reg.toTarget(builder);
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
    
    protected List<AdvancedObject> getParallelObjs() {
        return parallelObjs;
    }

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
    
    public abstract void doTakeRegister(boolean isLastUse);
    
    @Override
    public void takeRegister(IIntermediate by) {
        doTakeRegister(isLastUse(by));
    }
    
    public boolean hasRegister() {
        return reg != null;
    }

    public boolean isRegisterOnStack() {
        return hasRegister() && reg.isOnStack();
    }

    public void releaseRegister() {
        if (hasRegister()) {
            reg.release();
        }
    }

    public void setRegister(IRegister reg) {
        this.reg = reg.snapshot();
    }

    public IRegister getRegister() {
        return reg;
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
    public boolean updateLiteral(OPCode op, Literal lit) {
        return literal.updateLiteral(op, lit);
    }
}
