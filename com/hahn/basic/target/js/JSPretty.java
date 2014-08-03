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
     * @param format
     * @param args
     * @return
     */
    public static String format(String format, Object... args) {
        return JSPretty.format(-1, format, args);
    }
    
    /**
     * Use pretty format
     * @param tabs If >= 0 indent and add `tabs` extra tabs
     * @param format
     * @param args
     * @return
     */
    public static String format(int tabs, String format, Object... args) {
        String str;
        
        if (args.length > 0) {
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
            
            str = builder.toString();
        } else {
            str = format;
        }
        
        if (Main.PRETTY_PRINT && tabs >= 0) {
            return getIndent() + getTabs(tabs);
        } else {
            return str;
        }
    }
    
    private static void handleFlag(StringBuilder str, char flag, Object arg) {
        switch (flag) {
        // Standard
        case 's': 
            str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);            
            break;
            
        // Frame
        case 'f':
            if (arg instanceof Frame && ((Frame) arg).isEmpty()) {
                break;
            }
            
            str.append(Main.PRETTY_PRINT ? " {\n" : "{");
            
            JSPretty.indent += 1;
            str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
            JSPretty.indent -= 1;
            
            str.append(Main.PRETTY_PRINT ? getIndent() + "}" : "}");
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
        if (Main.PRETTY_PRINT) {
            switch (token) {
            case ',':
                str.append(", ");
                break;
                
            default:
                str.append(token);
            }
        } else {
            str.append(token);
        }
    }
    
    public static String getIndent() {
        return getTabs(JSPretty.indent);
    }
    
    public static String getTabs(int num) {
        return StringUtils.repeat(TAB, num);
    }
    
    public static final String TAB = "    ";
}
