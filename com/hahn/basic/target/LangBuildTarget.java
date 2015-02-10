package com.hahn.basic.target;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class LangBuildTarget {
    public abstract void init();
    public abstract String getStart();
    public abstract String getEnd();
    
    /**
     * The extension compiled output files standardly will
     * have. Should not be the safe as getInputExpression
     * @return The extension (without '.')
     */
    public abstract String getExtension();
    
    /**
     * The extension source input files standardly will have.
     * Should not be the same as getExension
     * @return The extension (without '.')
     */
    public abstract String getInputExtension();
    
    public abstract String getEOL();
    
    public abstract void append(ILangCommand cmd);
    public abstract void appendString(String str);
    public abstract void writeRunnableTo(FileOutputStream os) throws IOException;
    
    @Override
    public abstract String toString();
    
    public abstract String getCodeEnd();
    
    public abstract String getContentStart();
    public abstract String getContentEnd();
}
