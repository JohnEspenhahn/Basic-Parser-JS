package com.hahn.basic.target.js;

import java.io.FileOutputStream;
import java.io.IOException;

import com.hahn.basic.Main;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.LangCompiler;
import com.hahn.basic.intermediate.library.base.Library;
import com.hahn.basic.intermediate.objects.register.SimpleRegisterFactory;
import com.hahn.basic.intermediate.objects.types.Type;
import com.hahn.basic.target.ILangCommand;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.library.LibraryBuiltinJS;
import com.hahn.basic.target.js.library.LibraryJQuery;

public class JSBuildTarget extends LangBuildTarget {
    public static final Library BuiltinJS = new LibraryBuiltinJS();
    public static final Library JQuery = new LibraryJQuery();
    
	StringBuilder builder;
	SimpleRegisterFactory registerFactory;

	public JSBuildTarget() {
		builder = new StringBuilder();
		registerFactory = new SimpleRegisterFactory();
	}
	
	@Override
	public void init() {
	    builder.setLength(0);
	    
	    registerFactory.reset();
	    
	    LangCompiler.addLibrary("BuiltinJS");
	}
	
	@Override
	public String getStart() {
	    return "<!DOCTYPE>\n<html>\n<head>\n";
	}
	
	@Override
	public String getInputExtension() {
	    return "b";
	}
	
	@Override
	public String getExtension() {
	    return "html";
	}
	
	@Override
	public String getEOL() {
	    return ";";
	}
	
	@Override
	public void appendString(String str) {
	    builder.append(str);
	}

	@Override
	public void append(ILangCommand cmd) {
		builder.append(cmd.toTarget());
	}
	
	@Override
	public String getCodeEnd() {
	    return (Main.PRETTY ? "\n" : ";");
	}
	
	@Override
	public String getContentStart() {
	    return "<script>";
	}
	
	@Override
    public String getEnd() {
        return "\n</script>\n</head>\n</html>";
    }
	
	@Override
	public String getContentEnd() {
	    StringBuilder endBuilder = new StringBuilder();
        
        if (Type.getPublicTypes().size() > Type.COUNT_PRIMATIVES) {
            /*
             * function construct(clazz, func_constructor) { 
             *     var instance = new clazz(); 
             *     if (func_constructor) { 
             *         instance[func_constructor].apply(instance, Array.prototype.slice.call(arguments, 2));
             *     }
             *     return instance;
             * }
             */
            endBuilder.append("function " + EnumToken.__c__ + "(c,f){var o=new c;if(f)o[f].apply(o,Array.prototype.slice.call(arguments,2));return o}");
            
            /*
             * function extends(child, parent) {
             *     for(var prop_key in parent)
             *         if (parent.hasOwnProperty(prop_key)) child[prop_key] = parent[prop_key]; 
             *     function __() {
             *          this.constructor = child;
             *     }
             *     __.prototype = parent.prototype;
             *     child.prototype = new __(); // the child prototype 
             * }
             */
            endBuilder.append("function " + EnumToken.__e__ + "(d,b,p){for(p in b)if(b.hasOwnProperty(p))d[p]=b[p];function _(){this.constructor=d}_.prototype=b.prototype;d.prototype=new _}");
            
            /*
             * function createArr(dim, sizes) {
                    return (function f(arr, dim, sizes,i) {
                            if(dim >= 0)
                                for(i = 0; i < sizes[0]; i++)
                                    f(arr[i] = [] , dim - 1, sizes.slice(1));
                            return arr;
                        })([],dim-1,sizes); // Call function `f`
                }
             */
            endBuilder.append("function " + EnumToken.__a__ + "(d,s){return(function f(a,d,s,i){if(d>=0)for(i=0;i<s[0];i++)f(a[i]=[],d-1,s.slice(1));return a})([],d-1,s)}");
        }
        
        return endBuilder.toString();
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
