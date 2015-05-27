package com.hahn.basic.intermediate.library;

import java.io.File;
import java.io.FileNotFoundException;

import com.hahn.basic.intermediate.library.base.Library;

public class CodeLibrary extends Library {

    public CodeLibrary(String name) throws FileNotFoundException {
        super(name);
        
        File f = new File(name.replace('.', File.pathSeparatorChar));
        if (f.isFile()) {
            
        } else {
            throw new FileNotFoundException();
        }
    }

    @Override
    public void define() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toTarget() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
