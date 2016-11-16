package com.hahn.basic.target.js;

import java.io.IOException;
import java.io.OutputStream;

import com.hahn.basic.intermediate.Compiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.target.Command;
import com.hahn.basic.target.OutputBuilder;
import com.hahn.basic.target.js.library.LibraryJS;

import lombok.NonNull;

public class JSOutputBuilder implements OutputBuilder {
    public static final Library JS = new LibraryJS();
    
    protected final Compiler compiler;
	private StringBuilder builder;

	public JSOutputBuilder(@NonNull Compiler compiler) {
		this.builder = new StringBuilder();
		this.compiler = compiler;
	}
	
	@Override
	public String getStart() {
	    return "<!DOCTYPE html>\n<html>\n<body onload='main()'></body>\n<head>\n";
	}
	
	@Override
	public String getInputExtension() {
	    return "b";
	}
	
	@Override
	public String getOutputExtension() {
	    return "html";
	}
	
	@Override
	public void appendString(String str) {
	    builder.append(str);
	}

	@Override
	public void append(Command cmd) {
		builder.append(cmd.toTarget());
	}
	
	@Override
	public String getCodeEnd() {
	    return (compiler.isPretty() ? "\n" : ";");
	}
	
	@Override
	public String getContentStart() {
	    JSPretty.addTab();
	    return (compiler.isPretty() ? "\n<script>\nfunction main() {\n" : "<script>function main(){");
	}
	
	@Override
    public String getEnd() {
        return (compiler.isPretty() ? "\n" : "") + "</script>\n</head>\n</html>";
    }
	
	@Override
	public String getContentEnd() {
	    StringBuilder endBuilder = new StringBuilder();
	    
	    JSPretty.removeTab();
	    endBuilder.append(compiler.isPretty() ? "}\n" : "}");
        
        return endBuilder.toString();
	}

	@Override
	public void writeRunnableTo(OutputStream os, String encoding) throws IOException {
		os.write(builder.toString().getBytes(encoding));
		os.write('\n');
	}

	@Override
	public String toString() {
	    return builder.toString();
	}
}
