package com.hahn.basic.target;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputBuilder {   
    String getStart();
    String getEnd();
    
    /**
     * The extension compiled output files standardly will
     * have. Should not be the safe as getInputExpression
     * @return The extension (without '.')
     */
    String getOutputExtension();
    
    /**
     * The extension source input files standardly will have.
     * Should not be the same as getExension
     * @return The extension (without '.')
     */
    String getInputExtension();
    
    void append(Command cmd);
    void appendString(String str);
    void writeRunnableTo(OutputStream os, String encoding) throws IOException;
    
    @Override
    String toString();
    
    String getCodeEnd();
    
    String getContentStart();
    String getContentEnd();
}
