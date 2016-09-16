package com.hahn.basic;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.intermediate.CompilerStatus;
import com.hahn.basic.lexer.basic.BasicLexerFactory;
import com.hahn.basic.target.CommandFactory;
import com.hahn.basic.target.OutputBuilderFactory;
import com.hahn.basic.target.js.JSCommandFactory;
import com.hahn.basic.target.js.JSOutputBuilderFactory;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class Main {
	public static final String ENCODING = "UTF-8";
	
    private static Main instance;
    private static final String FILE_KEY = "file";
        
    private Map<String, String> parameters;    
    private EnumInputType inputType;
    protected File inputFile;
    
    public Main() {
        this.inputType = null;
        this.inputFile = null;        
        this.parameters = new HashMap<String, String>();
    }
    
    public abstract CommandFactory getCommandFactory();
    
    public abstract CompilerStatus getCompilerStatus();
    
    public abstract void printShellTitle();
    
    /**
     * Compile the inputed code
     */
    public abstract String handleInput(String input);
    
    public void setInputType(EnumInputType type) {
        if (this.inputType == null) this.inputType = type;
        else throw new IllegalArgumentException("Only one input type parameter is allowed");
    }
    
    public void setValue(String name, String value) {
        if (!parameters.containsKey(name)) parameters.put(name, value);
        else throw new IllegalArgumentException("Duplicate parameter `" + name + "`");
    }
    
    public String getValue(String name) {
        return parameters.get(name);
    }
    
    public File getTargetFile() {
        return new File(inputFile.getAbsolutePath() + "." + getCommandFactory().getOutputExtension());
    }
    
    public File getInputFile() {
        return inputFile;
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
        } else {
            printHelp();
        }
    }
    
    public void printHelp() {
        System.out.println("Usage: [options] [input mode]");
        System.out.println();
        System.out.println("Options:");
        System.out.println(" --debug                       Run in debug mode");
        System.out.println(" --pretty                      Run in pretty print mode");
        System.out.println();
        System.out.println("Input Modes:");
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
                    String output = handleInput(input);
                    System.out.println(output);
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
        
        try {
            handleInput(new String(Files.readAllBytes(Paths.get(file.toURI())), ENCODING));
        } catch (CompileException e) {
            printCompileException(e);
        } catch (FileNotFoundException e) {
            System.err.println("Could not find the file `" + getValue(FILE_KEY) + "`");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void toggleDebug() {
        getCompilerStatus().toggleDebug();
    }
    
    public boolean isDebugging() {
        return getCompilerStatus().isDebugging();
    }
    
    public void togglePretty() {
        getCompilerStatus().togglePretty();
    }
    
    public boolean isPretty() {
        return getCompilerStatus().isPretty();
    }
    
    public void setIsLibrary(boolean l) {
        getCompilerStatus().setIsLibrary(l);
    }
    
    public boolean isLibrary() {
        return getCompilerStatus().isLibrary();
    }
    
    public void printCompileException(CompileException e) {
        getCompilerStatus().printCompileException(e);
    }
    
    public static void forceNewInstance(OutputBuilderFactory out) {
    	System.out.println("WARNING: Forcing new instance (hopefully for testing)");
    	Main.instance = new KavaMain(new JSCommandFactory(), new BasicLexerFactory(), EnumToken.class, EnumExpression.class, out);
    }
    
    public static Main getInstance() {
    	if (Main.instance == null) {
    		Main.instance = new KavaMain(new JSCommandFactory(), new BasicLexerFactory(), EnumToken.class, EnumExpression.class, new JSOutputBuilderFactory());
    	}
    	
        return Main.instance;
    }
    
    public static void main(String[] args) {
        try {
            Main instance = Main.getInstance();
            
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
    
    public enum EnumInputType {
        SHELL, GUI_FILE, FILE
    }
}
