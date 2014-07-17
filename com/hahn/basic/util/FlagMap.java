package com.hahn.basic.util;

import java.util.HashMap;
import java.util.Map;

public class FlagMap<T> {
    private Map<T, Boolean> flags;
    
    public FlagMap() {
        flags = new HashMap<T, Boolean>();
    }
    
    public void mark(T id) {
        flags.put(id, true);
    }
    
    public boolean isMarked(T id) {
        Boolean flagged = flags.get(id);
        return flagged != null && flagged;
    }
    
    public void clear() {
        flags.clear();
    }
    
    @SafeVarargs
    public final void reset(T id, T... ids) {
        flags.remove(id);
        
        if (ids != null) {
            for (T i: ids) { flags.remove(i); }
        }
    }
}
