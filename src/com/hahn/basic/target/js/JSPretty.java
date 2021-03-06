package com.hahn.basic.target.js;

import java.util.MissingFormatArgumentException;
import java.util.UnknownFormatConversionException;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.intermediate.Frame;
import com.hahn.basic.intermediate.IIntermediate;
import com.hahn.basic.util.CompilerUtils;

public class JSPretty {    
    private static int indent = 0;
    
    /**
     * Use pretty format with no indent
     * @param format String with format items to replace with args
     * @param args If empty will ignore format
     * @return Formatted string
     */
    public static String format(final boolean pretty, String format, Object... args) {
        return JSPretty.format(pretty, -1, format, args);
    }
    
    /**
     * Use pretty format
     * @param tabs If greater than or equal to 0 indent and add `tabs` extra tabs
     * @param format String with format items to replace with args
     * @param args If empty will ignore format
     * @return Formatted string with indent and `tabs` extra tabs
     */
    public static String format(final boolean pretty, final int tabs, String format, Object... args) {
        if (pretty && tabs >= 0) JSPretty.setTabs(getTabs() + tabs);
        
        StringBuilder builder = new StringBuilder(format.length());
        
        int argIdx = 0;
        boolean isFlag = false;
        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (isFlag) {
                isFlag = false;
                handleFlag(pretty, builder, c, args[argIdx++]);
                
            } else if (c == '%') {
                isFlag = true;
                
            } else if (c == '<') {
                if (!pretty) {
                    do {
                        i += 1;
                    } while (i < format.length() && format.charAt(i) != '>');
                }
            } else if (c == '>') {
                continue;
                
            } else {
                handleToken(pretty, builder, c);
            }
        }
        
        if (isFlag) throw new MissingFormatArgumentException(format);
        
        String str;        
        if (pretty && tabs >= 0) {
            str = getIndent() + builder.toString();
            JSPretty.indent -= tabs;
        } else {
            str = builder.toString();
        }
        
        return str;
    }
    
    private static void handleFlag(final boolean pretty, StringBuilder str, char flag, Object arg) {
        boolean require_brace = true;
        final int oldIndent = JSPretty.getTabs();
        
        switch (flag) {
        // Standard
        case 's': 
            str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
            break;
            
        // Indentless standard
        case 'S':
            JSPretty.setTabs(0);
            str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
            JSPretty.setTabs(oldIndent);
            
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
                if (require_brace) str.append(pretty ? " {}" : "{}");
                else str.append(pretty ? " ;" : ";");
            } else {
                // If more than one object in frame requires brace
                require_brace = require_brace || frame.getSize() > 1;
                
                if (require_brace) {
                    JSPretty.setTabs(getTabs() + 1);
                    str.append(pretty ? " {\n" : "{");
                } else {
                    JSPretty.setTabs(0);
                    str.append(pretty ? " " : "");
                }
                
                str.append(arg instanceof IIntermediate ? ((IIntermediate) arg).toTarget() : arg);
                
                if (require_brace) {
                    JSPretty.setTabs(getTabs() - 1);
                    str.append(pretty ? getIndent() + "}" : "}");
                } else {
                    JSPretty.setTabs(oldIndent);
                }
            }
            
            break;
        
        // List
        case 'l':
            if (arg instanceof IIntermediate[]) {
                str.append(CompilerUtils.toTarget((IIntermediate[]) arg));
            } else if (arg instanceof Object[]) {
                str.append(StringUtils.join((Object[]) arg, CompilerUtils.getListSeperator()));
            } else {
                throw new IllegalArgumentException(arg + " is not an array and cannot be formatted with %l");
            }
            break;
            
        // Indentless List
        case 'L':
            JSPretty.setTabs(0);
            if (arg instanceof IIntermediate[]) {
                str.append(CompilerUtils.toTarget((IIntermediate[]) arg));
            } else if (arg instanceof Object[]) {
                str.append(StringUtils.join((Object[]) arg, CompilerUtils.getListSeperator()));
            } else {
                throw new IllegalArgumentException(arg + " is not an array and cannot be formatted with %l");
            }
            JSPretty.setTabs(oldIndent);
            break;
            
        default:
            throw new UnknownFormatConversionException("%" + flag);
        }
    }
    
    private static void handleToken(final boolean pretty, StringBuilder str, char token) {
        if (pretty) {
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
        return getTabs(getTabs());
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
    
    public static void setTabs(int i) {
        JSPretty.indent = i;
    }
    
    public static int getTabs() {
        return JSPretty.indent;
    }
    
    public static final String TAB = "    ";
}