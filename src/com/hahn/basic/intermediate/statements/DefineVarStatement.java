package com.hahn.basic.intermediate.statements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.intermediate.objects.IBasicObject;
import com.hahn.basic.parser.Node;

public abstract class DefineVarStatement extends Statement {
    private int flags;
    
    private List<DefinePair> definepairs;
    private boolean ignoreTypeCheck;
    
    private int lastMatchIdx;
    private IBasicObject lastMatchObj;
    
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
    public void addVar(IBasicObject var, IBasicObject val, Node node) {
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
    public boolean hasVar(IBasicObject var) {
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
    
    public IBasicObject getValFor(IBasicObject var) {
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
        getFile().pushCurrentLine(row);
        
        ListIterator<DefinePair> it = definepairs.listIterator(definepairs.size());
        while (it.hasPrevious()) {
            DefinePair pair = it.previous();
            
            pair.var.setInUse(this);
            pair.val.setInUse(this);
            
            // Type check
            if (!ignoreTypeCheck) {
                pair.val.getType().autocast(pair.var.getType(), getFile(), pair.node.getRow(), pair.node.getCol(), true);
            }
            
            pair.var.removeInUse();
        }
        
        return (definepairs.size() == 0);
    }
    
    @Override
    public boolean forwardOptimize() {
        getFile().pushCurrentLine(row);
        
        Iterator<DefinePair> it = definepairs.iterator();
        while (it.hasNext()) {
            DefinePair pair = it.next();
            IBasicObject var = pair.var;
            IBasicObject val = pair.val;
            
            // TODO handling of literals not used
            if (var.hasLiteral() && var.getUses() == 1) {
                it.remove();
                continue;
            }
            
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
        
        return (definepairs.size() == 0);
    }
    
    @Override
    public String toString() {
        return "define " + StringUtils.join(definepairs.toArray(), ", ");
    }
    
    public static class DefinePair {
        public final Node node;
        public final IBasicObject var, val;
        
        public DefinePair(Node node, IBasicObject var, IBasicObject val) {
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
