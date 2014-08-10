package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.library.LibraryBuiltinJS;
import com.hahn.basic.target.js.objects.register.JSRegister;

public class JSBuildTarget extends LangBuildTarget {
    public static final Library BuiltinJS = new LibraryBuiltinJS();
    
	StringBuilder builder;

	public JSBuildTarget() {
		builder = new StringBuilder();
	}
	
	@Override
	public void init() {
	    builder.setLength(0);
	    
	    JSRegister.init();
	    LangCompiler.addLibrary("BuiltinJS");
	}
	
	@Override
	public String getInputExtension() {
	    return "b";
	}
	
	@Override
	public String getExtension() {
	    return "js";
	}
	
	@Override
	public String getEOL() {
	    return ";";
	}
	
	@Override
	public void appendString(String str) {
	    if (Main.PRETTY_PRINT) builder.append(pretty(str));
	    else builder.append(str);
	}
	
	private String pretty(String str) {
	    return str;
	}

	@Override
	public void append(ILangCommand cmd) {
		builder.append(cmd.toTarget());
	}
	
	@Override
	public String endCodeArea() {
	    /*
	     * function construct(clazz, func_constructor) { 
	     *     var instance = new clazz(); 
	     *     if (func_constructor) { 
	     *         func_constructor.apply(instance, Array.prototype.slice.call(arguments, 2));
	     *     } 
	     *     return instance;
	     * }
	     * 
	     * var implements = this.implements || function(child, parent) {
	     *     for(var prop_key in parent)
	     *         if (parent.hasOwnProperty(prop_key)) child[prop_key] = parent[prop_key]; 
	     *     function __(){ this.constructor = child; }
	     *     __.prototype = parent.prototype;
	     *     return new __(); // the child prototype 
	     * }
	     */
	    return "var constructor=this.constructor||function(c,f){var o=new c();if(f){f.apply(o,Array.prototype.slice.call(arguments,2))}return o}"
	         + "var implements=this.implements||function(d,b){for(var p in b)if(b.hasOwnProperty(p))d[p]=b[p];function _(){this.constructor = d}_.prototype=b.prototype;return new _()}";
	}

	@Override
	public void writeRunnableTo(FileOutputStream os) throws IOException {
		os.write(builder.toString().getBytes());
		os.write('\n');
	}

	@Override
	public String toString() {
	    return builder.toString();
	}
}
