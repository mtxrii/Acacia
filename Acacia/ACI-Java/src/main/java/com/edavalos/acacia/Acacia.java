package com.edavalos.acacia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public final class Acacia {
    static final String path = System.getProperty("user.dir") + "\\";
    static final List<String> filesOpened = new LinkedList<>();

    static String sysArgs = null;
    static String currentFile = "";

    private static final Interpreter interpreter = new Interpreter();

    static boolean replMode;
    static String[] fileLines;
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            System.out.println("[Usage]: acacia [file.aci] [args]");
            System.exit(64);

        } else if (args.length >= 1) { // run a script
            if (args.length == 2) {
                sysArgs = args[1];
            }

            replMode = false;
            runFile(args[0]);

        } else { // no script specified, enter interactive runtime
            replMode = true;
            runPrompt();
        }
    }

    protected static void runFile(String file) {
        if (filesOpened.contains(file)) return;
        filesOpened.add(file);

        String path = Acacia.path + file;
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException exception) {
            System.err.println("[Error]: could not find file '" + file + "'");
            System.exit(64);
        }

        // checks if file is of type .aci (really can just be any text document though)
        var parts = path.split("\\.");
        if (!parts[parts.length - 1].equals("aci")) {
            System.err.println("[Warn]: file '" + path + "' is not of filetype '.aci'");
        }

        // Compile string from file & safe it
        String rawText = new String(bytes, Charset.defaultCharset());
        fileLines = rawText.split("\\\\r?\\\\n");

        // Send compiled string to runner method
        currentFile = "'" + file + "' ";
        run(rawText);
        currentFile = "";

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("~#: ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error
        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there was a resolution error
        if (hadError) return;

        interpreter.interpret(statements);
    }


    /* --- Error reporting methods --- */

    // For reporting errors given only a line and message
    static void error(int line, String message) {
        report(line, "", message, false);
    }

    // For reporting errors given a faulty token and message
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message, false);
        }
        else {
            report(token.line, " at '" + token.lexeme + "'", message, false);
        }
    }

    // For reporting errors given a runtime error object
    static void error(RuntimeError error) {
        report(error.token.line, "", error.getMessage(), true);
//        report(error.token, error.getMessage(), true);
    }

    // For displaying error messages that only include a line
    private static void report(int line, String where, String message, boolean isRuntimeError) {
        System.err.println("\n[" + currentFile + "line " + line + "] Error" + where + ": " + message);
        if (isRuntimeError) hadRuntimeError = true;
        else hadError = true;
    }

    // For displaying error messages that include a line, column and length
    private static void report(Token token, String message, boolean isRuntimeError) {
        System.err.println("\n[\" + currentFile + \"line " + token.line + "] Error at:");
        System.out.println("'" + fileLines[token.line-1] + "'");
        System.err.println(repeat(token.column, " ") + repeat(token.length, "*") + "\n" + message);
        if (isRuntimeError) hadRuntimeError = true;
        else hadError = true;
    }


    /* --- Utility methods --- */

    // Repeats strings
    public static String repeat(int count, String str) {
        return new String(new char[count]).replace("\0", str);
    }

    // Creates a string representation of any Acacia data type
    public static String stringify(Object object) {
        // If object is nil, string returned should be 'nil' instead of 'null'
        if (object == null) return "nil";

        // If object is a number, and has a decimal where it doesn't need it, remove it
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        // If object is a set, stringify each element inside
        if (object instanceof List) {
            if (((List) object).size() == 0) return "[]";

            StringBuilder text = new StringBuilder("[");
            for (Object o : ((List) object)) {
                if (o instanceof String) text.append("\"").append(o).append("\"").append(", ");
                else text.append(stringify(o)).append(", ");
            }
            text.append("|**]**|");
            return text.toString().replace(", |**]**|", "]");
        }

        // Otherwise, toString() should take care of it
        return object.toString();
    }

    // Determines an object's truthiness
    public static boolean isTruthy(Object object) {
        // Anything nil is false
        if (object == null) return false;

        // Any boolean is just itself
        if (object instanceof Boolean) return (boolean)object;

        // Any number is false only if its value is zero
        if (object instanceof Double) return ((double)object != 0.0);

        // Anything else is true
        return true;
    }

    // Determines an object's "weight", or precedence based on size / length / order, etc
    public static double weight(Object object) {
        if (object == null) {
            return -999999999;
        }
        else if (object instanceof Double) {
            return ((Double) object);
        }
        else if (object instanceof String) {
            String s = ((String) object);
            if (s.length() == 0) return 0;
            else return ((int) s.charAt(0));
        }
        else if (object instanceof Boolean) {
            Boolean b = ((Boolean) object);
            return b ? 1 : 0;
        }
        else if (object instanceof List) {
            return ((List) object).size();
        }
        else return 0;
    }
}
