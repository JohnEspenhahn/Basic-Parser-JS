package com.hahn.basic.intermediate.statements;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import lombok.NonNull;

import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.opcode.OPCode;
import com.hahn.basic.target.LangBuildTarget;

public abstract class Statement extends Compilable {
    private Deque<Compilable> targetCode;
    
    public Statement(Statement container) {
        super(container == null ? null : container.getFrame());
        
        if (useAddTargetCode()) {
            this.targetCode = new ArrayDeque<Compilable>();
        } else {
            this.targetCode = null;
        }
    }
    
    /**
     * @return True to allow use of addTargetCode
     */
    public abstract boolean useAddTargetCode();
    
    @Override
    public abstract boolean reverseOptimize();
    
    @Override
    public abstract boolean forwardOptimize();
    
    protected final void addCode(Compilable c) {
        targetCode.addLast(c);
    }
    
    protected final void prependCode(Compilable c) {
        targetCode.addFirst(c);
    }
    
    /**
     * Called from reverseOptimize. Add target code to be handled. 
     * Should NOT do any optimization 
     */
    protected void addTargetCode() { }
    
    /**
     * Do reverse optimization of the target code
     * if use of target code is enabled (through
     * useAddTargetCode)
     */
    public void reverseOptimizeTargetCode() {
        if (useAddTargetCode()) {
            addTargetCode();
            
            Iterator<Compilable> it = targetCode.descendingIterator();
            Compilable a = null, b = null;
            while (it.hasNext()) {
                b = a;
                a = it.next();
                
                if (optimize(a, b) || a.reverseOptimize()) {
                    a = b;
                    it.remove();
                }
            }
        }
    }
    
    /**
     * Do forward optimization of the target code
     * if use of target code is enabled (through
     * useAddTargetCode)
     */
    public void forwardOptimizeTargetCode() {
        if (useAddTargetCode()) {
            Iterator<Compilable> it = targetCode.iterator();
            while (it.hasNext()) {
                Compilable compilable = it.next();            
                boolean remove = compilable.forwardOptimize();
                
                if (remove) {
                    it.remove();
                }
            }
        }
    }
    
    public String joinTargetCode(LangBuildTarget builder) {
    	StringBuilder str = new StringBuilder();    	
    	if (useAddTargetCode()) {
            for (Compilable c: targetCode) {
                str.append(c.toTarget(builder));
            }
    	}
        
        return str.toString();
    }
    
    /**
     * Called from reverse optimize. Optimize a compilable pair
     * @param a NonNull Compilable
     * @param b NullAble compilable
     * @return True to remove `a`
     */
    private boolean optimize(@NonNull Compilable a, Compilable b) {
        if (a instanceof ReturnStatement && b instanceof ReturnStatement && a.equals(b)) {
            return true;
        } else if (a instanceof DefineVarStatement && b instanceof Command) {
            DefineVarStatement create = (DefineVarStatement) a;
            Command bCmd = (Command) b;
            if (create.hasVar(bCmd.getP1()) && bCmd.isP1LastUse()) {
                bCmd.forceP1(create.getValFor(bCmd.getP1()));
                return true;
            } else if (create.hasVar(bCmd.getP2()) && bCmd.isP2LastUse()) {
                bCmd.forceP2(create.getValFor(bCmd.getP1()));
                return true;
            } else if (create.hasVar(bCmd.getP2())) {
                bCmd.forceP2(create.getValFor(bCmd.getP1()));
            }
        } else if (a instanceof Command) {
            Command aCmd = (Command) a;
            BasicObject aP1 = aCmd.getP1();
            BasicObject aP2 = aCmd.getP2();
            OPCode aOP = aCmd.getOP();
            
            if ((aOP == OPCode.ADD || aOP == OPCode.SUB || aOP == OPCode.SHL || aOP == OPCode.SHR) && aP2.equals(0)) {
                return true;
            } else if (aOP == OPCode.MUL && aP2.hasLiteral() && aP2.getLiteral().getValue() % 2 == 0) {
                aCmd.setOP(OPCode.SHL);
                aCmd.forceP2(new LiteralNum(aP2.getLiteral().getValue() / 2, aP2.getType()));
            } else if (aOP == OPCode.SET && aP1 == aP2) {
                return true;
            }
        }
        
        return false;
    }
    
    public String getTargetCodeString() {
        return targetCode.toString();
    }
}
