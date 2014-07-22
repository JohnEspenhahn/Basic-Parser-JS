package com.hahn.basic.intermediate;

import com.hahn.basic.target.LangBuildTarget;

public interface IIntermediate {
    /**
     * Convert to its final form
     * @param builder
     * @return A final form object
     */
    public String toTarget(LangBuildTarget builder);
}
