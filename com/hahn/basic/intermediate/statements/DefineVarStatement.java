package com.hahn.basic.intermediate.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.objects.BasicObject;
import com.hahn.basic.parser.Node;

public abstract class DefineVarStatement extends Statement {
    private int flags;
    
    private List<DefinePair> definepairs;
    private boolean ignoreTypeCheck;
    
    private int lastMatchIdx;
    private BasicObject lastMatchObj;
    
    public DefineVarStatement(Statement container, boolean ignoreTypeCheck) {
        super(container);
        
        this.flags = 0;
        this.definepairs = new ArrayList<DefinePair>();
        this.ignoreTypeCheck = ignoreTypeCheck;
    }
    
    public int getFlags() {
        return flags;
    }
    
    public void setFlags(int flags) {
        this.flags = flags;
    }
    
    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }
    
    /**
     * Add a variable to be defined in this statement
     * @param var The variable to define
     * @param val The value to give it
     * @param node The node to throw an error at if needed
     */
    public void addVar(BasicObject var, BasicObject val, Node node) {
        definepairs.add(new DefinePair(node, var.getForCreateVar(), val));
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
                pair.val.getType().autocast(pair.var.getType(), pair.node.getRow(), pair.node.getCol(), true);
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
            var.takeRegister(this);
            
            // Check literals
            if (var.canSetLiteral() && val.hasLiteral()) {
                var.setLiteral(val.getLiteral());
            } else {
                var.setLiteral(null);
            }
            
            if (val.hasLiteral() && !val.canLiteralSurvive(getFrame())) {
                val.setLiteral(null);
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "define " + StringUtils.join(definepairs.toArray(), ", ");
    }
    
    public static class DefinePair {
        public final Node node;
        public final BasicObject var, val;
        
        public DefinePair(Node node, BasicObject var, BasicObject val) {
            this.node = node;
            
            this.var = var;
            this.val = val;
        }
        
        @Override
        public String toString() {
            return var + " = " + val;
        }
    }
}
