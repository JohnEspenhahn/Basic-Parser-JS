package com.hahn.basic.intermediate.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.AdvancedObject;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.intermediate.objects.PopObject;
import com.hahn.basic.intermediate.objects.PushObject;
import com.hahn.basic.intermediate.objects.register.StackRegister;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.util.Util;
import com.sun.istack.internal.Nullable;

public abstract class DefineVarStatement extends Statement {   
    @Nullable
    private List<String> flags;
    
    private List<DefinePair> definepairs;
    private boolean ignoreTypeCheck;
    
    private int lastMatchIdx;
    private BasicObject lastMatchObj;
    
    public DefineVarStatement(Statement container, boolean ignoreTypeCheck) {
        super(container);
        
        this.definepairs = new ArrayList<DefinePair>();
        this.ignoreTypeCheck = ignoreTypeCheck;
    }
    
    public boolean hasFlags() {
        return flags != null;
    }
    
    public List<String> getFlags() {
        return flags;
    }
    
    public void setFlags(List<String> flags) {
        this.flags = flags;
    }
    
    public boolean hasFlag(String name) {
        return hasFlags() && getFlags().contains(name);
    }
    
    public void addVar(BasicObject var, BasicObject val) {
        definepairs.add(new DefinePair(var.getForUse(this), val.getForUse(this)));
    }
    
    public List<DefinePair> getDefinePairs() {
        return definepairs;
    }
    
    /**
     * Checks if one of the vars defined by this statement
     * is the given var. If it is will cache it for a
     * preceding load of the var's value with getValFor(var)
     * @param var The var to check for
     * @return True if this statement defines that var
     */
    public boolean hasVar(BasicObject var) {
        for (int i = 0; i < definepairs.size(); i++) {
            DefinePair pair = definepairs.get(i);
            if (pair.var == var) {
                lastMatchIdx = i;
                lastMatchObj = var;
                
                return true;
            }
        }
        
        lastMatchIdx = 0;
        lastMatchObj = null;
        
        return false;
    }
    
    public BasicObject getValFor(BasicObject var) {
        if (lastMatchObj != null && lastMatchObj == var) {
            return definepairs.get(lastMatchIdx).val;
        } else {
            for (DefinePair pair: definepairs) {
                if (pair.var == var) {
                    return pair.val;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public boolean useAddTargetCode() {
        return false;
    }
    
    @Override
    public boolean reverseOptimize() {
        Main.setLine(row);
        
        ListIterator<DefinePair> it = definepairs.listIterator(definepairs.size());
        while (it.hasPrevious()) {
            DefinePair pair = it.previous();
            
            pair.var.setInUse(this);
            pair.val.setInUse(this);
            
            // Type check
            if (!ignoreTypeCheck) {
                Type.merge(pair.var.getType(), pair.val.getType());
            }
            
            pair.var.removeInUse();            
            if (pair.var.getUses() == 1) {
                it.remove();
            }
        }
        
        return (definepairs.size() == 0);
    }
    
    @Override
    public boolean forwardOptimize() {
        Main.setLine(row);
        
        for (DefinePair pair: definepairs) {
            BasicObject var = pair.var;
            BasicObject val = pair.val;
            
            // Check registers
            val.takeRegister(this);
            if (val instanceof PopObject) StackRegister.pop();
            
            var.takeRegister(this);
            if (var instanceof PushObject) StackRegister.push();
            
            // Check literals
            if (var.canSetLiteral() && val.hasLiteral()) {
                var.setLiteral(val.getLiteral());
            } else {
                var.setLiteral(null);
            }
            
            if (val instanceof AdvancedObject && val.hasLiteral()) {
                AdvancedObject aval = (AdvancedObject) val;
                if (!aval.isOwnerFrame(getFrame())) {
                    aval.setLiteral(null);
                } 
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "let " + Util.toString(definepairs.toArray(), ", ");
    }
    
    public class DefinePair {
        public final BasicObject var, val;
        
        public DefinePair(BasicObject var, BasicObject val) {
            this.var = var;
            this.val = val;
        }
        
        @Override
        public String toString() {
            return var + " = " + val;
        }
    }
}
