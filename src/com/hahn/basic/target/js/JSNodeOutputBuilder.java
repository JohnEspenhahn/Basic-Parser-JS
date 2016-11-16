package com.hahn.basic.target.js;

import com.hahn.basic.intermediate.Compiler;

public class JSNodeOutputBuilder extends JSOutputBuilder {
	
	public JSNodeOutputBuilder(Compiler compiler) {
		super(compiler);
	}
	
	@Override
	public String getStart() {
	    return "";
	}
	
	@Override
	public String getInputExtension() {
	    return "b";
	}
	
	@Override
	public String getOutputExtension() {
	    return "js";
	}
	
	@Override
	public String getCodeEnd() {
	    return (compiler.isPretty() ? "\n" : ";");
	}
	
	@Override
	public String getContentStart() {
	    return "";
	}
	
	@Override
    public String getEnd() {
        return "";
    }
	
	@Override
	public String getContentEnd() {
	    return "";
	}
}
