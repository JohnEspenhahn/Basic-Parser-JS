package com.hahn.basic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JFileChooser;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.lexer.basic.BasicLexer;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.JSLangFactory;
import com.hahn.basic.util.EnumInputType;
import com.hahn.basic.util.exceptions.CompileException;
import com.hahn.basic.viewer.ViewerBuilder;

public abstract class Main {
    private static Main instance;
    
    private static final String FILE_KEY = "file";
    private static final String DIR_KEY = "dir";
    
    private int row, col;
    private Stack<Integer> rows = new Stack<Integer>(), columns = new Stack<Integer>();
    
    private List<String> lines = new ArrayList<String>();
    
    private boolean debug = false, pretty = false, library = false;
    
    private Map<String, String> values;
    
    private EnumInputType inputType;
    protected File inputFile;
    
    public Main() {
        this.inputType = null;
        this.inputFile = null;
        
        this.values = new HashMap<String, String>();
    }
    
    public abstract LangBuildTarget getLangBuildTarget();
    
    public abstract void printShellTitle();
    
    public abstract void reset();
    public abstract void handleInput();
    
    public void setInputType(EnumInputType type) {
        if (this.inputType == null) this.inputType = type;
        else throw new IllegalArgumentException("Only one input type parameter is allowed");
    }
    
    public void setValue(String name, String value) {
        if (!values.containsKey(name)) values.put(name, value);
        else throw new IllegalArgumentException("Duplicate parameter `" + name + "`");
    }
    
    public String getValue(String name) {
        return values.get(name);
    }
    
    public File getTargetFile() {
        return new File(inputFile.getAbsolutePath() + "." + getLangBuildTarget().getExtension());
    }
    
    public File getInputFile() {
        return new File(inputFile.getAbsolutePath() + "." + getLangBuildTarget().getInputExtension());
    }
    
    public EnumInputType getInputType() {
        return inputType;
    }
    
    public void run() {
        if (inputType == EnumInputType.SHELL) {
            shellInput();
        } else if (inputType == EnumInputType.GUI_FILE) {
            guiFileInput();
        } else if (inputType == EnumInputType.FILE) {
            fileInput(new File(getValue(FILE_KEY)));
        } else if (inputType == EnumInputType.DIR) {
            dirInput(getValue(DIR_KEY));
        } else {
            printHelp();
        }
    }
    
    public void printHelp() {
        System.out.println("Usage: cmp [-options] input");
        System.out.println();
        System.out.println("Options:");
        System.out.println(" --debug                       Run in debug mode");
        System.out.println(" --pretty                      Run in pretty print mode");
        System.out.println();
        System.out.println("Input Modes:");
        System.out.println(" -d [<path>], --dir [<path>]   Attempt to compile a project");
        System.out.println("                               in the given directory <path>");
        System.out.println(" -f <path>, --file <path>      Compile file at the given");
        System.out.println("                               file <path>");
        System.out.println(" -g, --gui                     Select file to be compiled");
        System.out.println("                               with the gui file selector");
        System.out.println(" -s, --shell                   Start an input shell");
    }
    
    public void printShellHelp() {
        System.out.println("Shell Commands:");
        System.out.println(" debug     Toggle debug mode");
        System.out.println(" help      Print this");
        System.out.println(" pretty    Toggle pretty print");
        System.out.println(" exit      Quit the shell");
        System.out.println();
    }
    
    public void shellInput() {
        printShellTitle();
        
        System.out.println("Type `help` for help");
        System.out.println();
        
        // Create input scanner
        Scanner scanner = new Scanner(System.in);
         
        String input;
        while (true) {
            System.out.print("> ");
            
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                input = "";
            }
            
            if (input.equalsIgnoreCase("debug")) {
                toggleDebug();
            } else if (input.equalsIgnoreCase("pretty")) {
                togglePretty();
            } else if (input.equalsIgnoreCase("help")) {
                printShellHelp();
            } else if (input.equalsIgnoreCase("exit")) {
                break;
            } else {
                try {               
                    // Reset
                    lines.clear();
                    clearLineErrors();
                    lines.add(input.trim());
                    
                    reset();                    
                    handleInput();
                } catch (CompileException e) {
                    printCompileException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        scanner.close();
    }
    
    public void guiFileInput() {        
        JFileChooser chooser = new JFileChooser();
        chooser.grabFocus();
        
        int result = chooser.showOpenDialog(null);        
        if (result == JFileChooser.APPROVE_OPTION) {
            fileInput(chooser.getSelectedFile());
        } else {
            System.out.println("ERROR: Canceled by user");
        }
    }
    
    public void fileInput(File file) {
        this.inputFile = file;
        
        // Reset
        resetFile();
        
        Scanner scanner = null;
        try {
            long start = System.currentTimeMillis();
            
            scanner = new Scanner(this.inputFile);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                lines.add(line + "\n");
            }
            // lines.add("#eof"); // End of File
            
            handleInput();
            
            if (debug) {
                System.out.println();
                System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (CompileException e) {
            printCompileException(e);
        } catch (FileNotFoundException e) {
            System.err.println("Could not find the file `" + getValue(FILE_KEY) + "`");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    public void dirInput(String dir) {
        if (dir == null) dir = System.getProperty("user.dir");
        File dirFile = new File(dir);
        
        System.out.println("Compiling files in directory `" + dir + "` with extension `." + getLangBuildTarget().getInputExtension() + "`");
        
        if (dirFile.isDirectory()) {            
            Scanner scanner = null;
            try {
                resetFile();
                
                int found = 0;
                long start = System.currentTimeMillis();                
                this.inputFile = new File(dirFile, "main");
                
                for (File f: dirFile.listFiles()) {
                    String extension = f.getName().replaceFirst("^.+\\.", "");
                    if (f.isFile() && extension.equals(getLangBuildTarget().getInputExtension())) {
                        found += 1;
                        
                        scanner = new Scanner(f);
                        while(scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            
                            lines.add(line + "\n");
                        }
                        // lines.add("#eof"); // End of File
                        
                        scanner.close();
                        scanner = null;
                    }
                }
                
                handleInput();
                
                System.out.println();
                if (found == 0) {
                    System.err.println("No files found in the directory `" + dir + "` with the file extension `." + getLangBuildTarget().getInputExtension() + "`");
                }
                
                System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
            } catch (CompileException e) {
                printCompileException(e);
            } catch (FileNotFoundException e) {
                System.err.println("Could not find the directory `" + dir + "`");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        } else {
            throw new IllegalArgumentException("The given directory path is not a directory");
        }
    }
    
    public void parseLinesFromString(String str) {
        resetFile();
        
        String[] ls = str.split("\n");
        for (String l: ls) {
            lines.add(l + "\n");
        }
    }
    
    private void resetFile() {
        lines.clear();
        clearLineErrors();
        library = false;
    }
    
    public void toggleDebug() {
        debug = !debug;
        System.out.println("Debug = " + debug);
        System.out.println();
    }
    
    public boolean isDebugging() {
        return debug;
    }
    
    public void togglePretty() {
        pretty = !pretty;
        System.out.println("Pretty = " + pretty);
        System.out.println();
    }
    
    public boolean isPretty() {
        return pretty;
    }
    
    public void setIsLibrary(boolean l) {
        this.library = l;
    }
    
    public boolean isLibrary() {
        return library;
    }
    
    public void printCompileException(CompileException e) {
        if (debug) e.printStackTrace();
        else System.out.println("ERROR: " + e.getMessage());
    }
    
    protected List<String> getLines() {
        return lines;
    }
    
    public String getLineStr() {
        return getLineStr(getRow());
    }
    
    public String getLineStr(int line) {
        return lines.get(line - 1);
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public void setLine(int row) {
        setLine(row, -1);
    }
    
    public void setLine(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public void putLineError(CompileException e) {
        ViewerBuilder.putLineError(e.getRow(), e.getTooltipMessage());
    }
    
    public void clearLineErrors() {
        ViewerBuilder.clearLineErrors();
    }
    
    public void pushLine(int row, int col) {
        rows.push(this.row);
        columns.push(this.col);
        
        setLine(row, col);
    }
    
    public void popLine() {
        setLine(rows.pop(), columns.pop());
    }
    
    public static Main getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        try {
            instance = new BASICMain(new JSLangFactory(), new BasicLexer(), EnumToken.class, EnumExpression.class);
            
            String s;
            for (int i = 0; i < args.length; i++) {
                s = args[i];
                if (s.equals("--debug")) {
                    instance.toggleDebug();
                } else if (s.equals("--pretty")) {
                    instance.togglePretty();
                } else if (s.equals("--gui") || s.equals("-g")) {
                    instance.setInputType(EnumInputType.GUI_FILE);
                } else if (s.equals("--shell") || s.equals("-s")) {
                    instance.setInputType(EnumInputType.SHELL);
                } else if (s.equals("--file") || s.equals("-f")) {
                    instance.setInputType(EnumInputType.FILE);
                    if (i+1 < args.length && !args[i+1].startsWith("-")) {
                        instance.setValue(FILE_KEY, args[++i]);
                    } else {
                        throw new IllegalArgumentException("No file name provided after `" + s + "`");
                    }
                } else if (s.equals("--dir") || s.equals("-d")) {
                    instance.setInputType(EnumInputType.DIR);
                    if (i+1 < args.length && !args[i+1].startsWith("-")) {
                        instance.setValue(DIR_KEY, args[++i]);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal command line parameter `" + s + "`");
                }
            }
            
            // Choose execution mode
            instance.run();
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}
