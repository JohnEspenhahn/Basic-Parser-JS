package com.hahn.basic.target;

import java.io.FileOutputStream;
import java.io.IOException;

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
    void writeRunnableTo(FileOutputStream os) throws IOException;
    
    @Override
    String toString();
    
    String getCodeEnd();
    
    String getContentStart();
    String getContentEnd();
}
