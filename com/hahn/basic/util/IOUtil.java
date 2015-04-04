package com.hahn.basic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IOUtil {
    public static String readFile(File f) throws FileNotFoundException {
        Scanner s = new Scanner(f);
        String content = s.useDelimiter("\\Z").next();
        s.close();
        
        return content;
    }
    
    public static String loadScript(File f) {
        try {
            return String.format("<script>%s\n</script>", IOUtil.readFile(f));
        } catch (FileNotFoundException e) {
            return String.format("<script>alert('Could not find file %s!'</script>", f.getName());
        }
    }
}
