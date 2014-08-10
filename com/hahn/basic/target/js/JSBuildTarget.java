package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.types.Type;
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
	    return (Main.PRETTY_PRINT ? "\n" : "");
	}
	
	@Override
	public String endFuncArea() {
	    StringBuilder builder = new StringBuilder();
        
        if (Type.getPublicTypes().size() != Type.COUNT_PRIMATIVES) {
            /*
             * function construct(clazz, func_constructor) { 
             *     var instance = new clazz(); 
             *     if (func_constructor) { 
             *         func_constructor.apply(instance, Array.prototype.slice.call(arguments, 2));
             *     } 
             *     return instance;
             * }
             */
            builder.append("function constructor(c,f,o){o=new c;if(f)f.apply(o,Array.prototype.slice.call(arguments,2));return o}");
            if (Main.PRETTY_PRINT) builder.append("\n");
            
            /*
             * var implements = this.implements || function(child, parent) {
             *     for(var prop_key in parent)
             *         if (parent.hasOwnProperty(prop_key)) child[prop_key] = parent[prop_key]; 
             *     function __() { 
             *          this.constructor = child; 
             *          this.super = parent; 
             *     }
             *     __.prototype = parent.prototype;
             *     return new __(); // the child prototype 
             * }
             */
            builder.append("function implements(d,b,p){for(p in b)if(b.hasOwnProperty(p))d[p]=b[p];function _(){this.constructor=d;this.super=b}_.prototype=b.prototype;return new _}");
            if (Main.PRETTY_PRINT) builder.append("\n");
        }
        
        return builder.toString();
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
