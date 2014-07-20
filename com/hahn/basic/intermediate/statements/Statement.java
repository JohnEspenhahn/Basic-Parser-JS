package com.hahn.basic.intermediate.statements;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import lombok.NonNull;

import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.LiteralNum;
import com.hahn.basic.intermediate.objects.VarPointer;
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
    
    public void addCode(Compilable c) {
        targetCode.addLast(c);
    }
    
    public void prependCode(Compilable c) {
        targetCode.addFirst(c);
    }
    
    /**
     * Called from reverseOptimize. Add target code to be handled. 
     * Should NOT do any optimization 
     */
    protected void addTargetCode() { }
    
    @Override
    public boolean reverseOptimize() {
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
        
        return false;
    }
    
    @Override
    public boolean forwardOptimize() {
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
        
        return false;
    }
    
    @Override
    public String toTarget(LangBuildTarget builder) {
    	StringBuilder str = new StringBuilder();
    	str.append(toTargetPre());
    	
    	if (useAddTargetCode()) {
            for (Compilable c: targetCode) {
                str.append(c.toTarget(builder));
            }
    	}
        
        str.append(toTargetPost());
        
        return str.toString();
    }
    
    protected String toTargetPre() { return ""; }
    
    protected String toTargetPost() { return ""; }
    
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
            if (create.getVar() == bCmd.getP1() && bCmd.isP1LastUse()) {
                bCmd.forceP1(create.getVal());
                return true;
            } else if (create.getVar() == bCmd.getP2() && bCmd.isP2LastUse()) {
                bCmd.forceP2(create.getVal());
                return true;
            } else if (bCmd.isP1LastUse() && bCmd.getP1() instanceof VarPointer && create.getVal() instanceof AdvancedObject && bCmd.getP1().equals(create.getVar())) {
                ((VarPointer) bCmd.getP1()).setObj((AdvancedObject) create.getVal());
                return true;
            } else if (create.getVal() == bCmd.getP2()) {
                bCmd.forceP2(create.getVar());
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
