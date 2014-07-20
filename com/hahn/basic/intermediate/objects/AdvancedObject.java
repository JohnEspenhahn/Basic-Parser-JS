package com.hahn.basic.intermediate.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.objects.register.IRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.intermediate.statements.Compilable;
import com.hahn.basic.intermediate.statements.Statement;

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

    public boolean isPointer() {
        return false;
    }

    public Frame getFrame() {
        return frame;
    }

    public boolean isOwnerFrame(Frame f) {
        return (f == getFrame());
    }

    public List<AdvancedObject> getParallelObjs() {
        return parallelObjs;
    }

    public AdvancedObject getPointer() {
        return LangCompiler.factory.VarPointer(this);
    }

    public BasicObject getAddress() {
        return this;
    }

    @Override
    public AdvancedObject getForUse(Statement s) {
        if (hasRegister()) {
            reg = reg.snapshot();
        }

        return this;
    }

    @Override
    public AdvancedObject castTo(Type t) {
        return new AdvancedObjectHolder(this, t);
    }

    @Override
    public String toTarget() {
        if (hasLiteral()) {
            return literal.toTarget();
        } else {
            return reg.toTarget();
        }
    }

    @Override
    public BasicObject getForCreateVar() {
        if (isRegisterOnStack()) {
            return LangCompiler.factory.PushObject();
        } else {
            return super.getForCreateVar();
        }
    }

    /*
     * ------------------------------- Register Management -------------------------------
     */
    @Override
    public boolean setInUse(Compilable by) {
        boolean firstCall = super.setInUse(by);

        if (firstCall && getFrame() != null) {
            frame.addInUseVar(this);
        }

        return firstCall;
    }
    
    public final void takeRegister(Compilable by) {
        doTakeRegister(isLastUse(by));
    }
    
    public abstract void doTakeRegister(boolean isLastUse);
    
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
        this.reg = reg;
    }

    public IRegister getRegister() {
        return reg;
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
     * ------------------------------- Literal Management -------------------------------
     */

    @Override
    public boolean canSetLiteral() {
        return literal != null;
    }

    @Override
    public Literal getLiteral() {
        return literal;
    }

    @Override
    public boolean hasLiteral() {
        return canSetLiteral() && literal != LiteralNum.UNDEFINED;
    }

    @Override
    public void setLiteral(Literal lit) {
        if (canSetLiteral()) {
            this.literal = lit;
        }
    }

    /**
     * If is a literal, modify it
     * @return True if should remove the containing op command
     */
    public boolean updateLiteral(OPCode op, BasicObject val) {
        if (hasLiteral() && val.hasLiteral()) {
            return literal.updateLiteral(op, val.getLiteral());
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        return (this == o);
    }
}
