package com.hahn.basic.target.js;

import java.util.MissingFormatArgumentException;
import java.util.UnknownFormatConversionException;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.Main;
import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.util.Util;

public class JSPretty {    
    private static int indent = 0;
    
    /**
     * Use pretty format with no indent
     * @param format String with format items to replace with args
     * @param args If empty will ignore format
     * @return Formatted string
     */
    public static String format(String format, Object... args) {
        return JSPretty.format(-1, format, args);
    }
    
    /**
     * Use pretty format
     * @param tabs If greater than or equal to 0 indent and add `tabs` extra tabs
     * @param format String with format items to replace with args
     * @param args If empty will ignore format
     * @return Formatted string with indent and `tabs` extra tabs
     */
    public static String format(int tabs, String format, Object... args) {
        if (Main.PRETTY && tabs >= 0) JSPretty.indent += tabs;
        
        StringBuilder builder = new StringBuilder(format.length());
        
        int argIdx = 0;
        boolean isFlag = false;
        for (char c: format.toCharArray()) {
            if (isFlag) {
                isFlag = false;
                handleFlag(builder, c, args[argIdx++]);
                
            } else if (c == '%') {
                isFlag = true;
                
            } else {
                handleToken(builder, c);
            }
        }
        
        if (isFlag) throw new MissingFormatArgumentException(format);
        
        String str;        
        if (Main.PRETTY && tabs >= 0) {
            str = getIndent() + builder.toString();
            JSPretty.indent -= tabs;
        } else {
            str = builder.toString();
        }
        
        return str;
    }
    
    private static void handleFlag(StringBuilder str, char flag, Object arg) {
        boolean require_brace = true;
        final int oldIndent = JSPretty.indent;
        
        switch (flag) {
        // Standard
        case 's': 
            str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
            break;
            
        // Indentless standard
        case 'S':
            JSPretty.indent = 0;
            str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
            JSPretty.indent = oldIndent;
            
            break;
            
        // Block
        case 'b':
            require_brace = false;
            
        // Frame
        case 'f':
            if (!(arg instanceof Frame)) {
                throw new RuntimeException("Illegal argument `" + arg + "` with flag `%" + flag + "`");
            }
            
            Frame frame = (Frame) arg;
            if (frame.isEmpty()) {
                if (require_brace) str.append(Main.PRETTY ? " {}" : "{}");
                else str.append(Main.PRETTY ? " ;" : ";");
            } else {
                // If more than one object in frame requires brace
                require_brace = require_brace || frame.getSize() > 1;
                
                if (require_brace) {
                    JSPretty.indent += 1;
                    str.append(Main.PRETTY ? " {\n" : "{");
                } else {
                    JSPretty.indent = 0;
                    str.append(Main.PRETTY ? " " : "");
                }
                
                str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
                
                if (require_brace) {
                    JSPretty.indent -= 1;
                    str.append(Main.PRETTY ? getIndent() + "}" : "}");
                } else {
                    JSPretty.indent = oldIndent;
                }
            }
                
            break;
        
        // List
        case 'l':
            if (arg instanceof IIntermediate[]) {
                str.append(Util.toTarget((IIntermediate[]) arg));
            } else if (arg instanceof Object[]) {
                str.append(StringUtils.join((Object[]) arg, Util.getListSeperator()));
            } else {
                throw new IllegalArgumentException(arg + " is not an array and cannot be formatted with %l");
            }
            break;
            
        default:
            throw new UnknownFormatConversionException("%" + flag);
        }
    }
    
    private static void handleToken(StringBuilder str, char token) {
        if (Main.PRETTY) {
            switch (token) {
            case ',':
                str.append(", ");
                break;
            case '_':
                str.append(" ");
                break;
            case '^':
                str.append("\n");
                break;
            default:
                str.append(token);
            }
        } else {
            switch (token) {
            case '_': case '^':
                break;
            default:
                str.append(token);
            }
        }
    }
    
    public static String getIndent() {
        return getTabs(JSPretty.indent);
    }
    
    public static String getTabs(int num) {
        return StringUtils.repeat(TAB, num);
    }
    
    public static void addTab() {
        JSPretty.indent += 1;
    }
    
    public static void removeTab() {
        JSPretty.indent -= 1;
    }
    
    public static final String TAB = "    ";
}
