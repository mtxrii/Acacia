package com.edavalos.acacia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public final class Acacia {
    static String[] fileLines;
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("[Usage]: acacia [file.aci]");
            System.exit(64);

        } else if (args.length == 1) { // run a script
            runFile(args[0]);

        } else { // no script specified, enter interactive runtime
            runPrompt();
        }
    }

    private static void runFile(String path) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException exception) {
            System.err.println("[Error]: could not find file '" + path + "'");
            System.exit(64);
        }

        // checks if filetype is of .aci (really can just be any text document though)
        var parts = path.split("\\.");
        if (!parts[parts.length - 1].equals("aci")) {
            System.out.println("[Warn]: file '" + path + "' is not of filetype '.aci'");
        }

        // Compile string from file & safe it
        String rawText = new String(bytes, Charset.defaultCharset());
        fileLines = rawText.split("\\\\r?\\\\n");

        // Send compiled string to runner method
        run(rawText);

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
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

        // For now, just print the tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    // For reporting errors given only a line and message
    static void error(int line, String message) {
        report(line, "", message);
    }

    // For reporting errors given a faulty token and message
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        }
        else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    // For displaying error messages that only include a line
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    // For displaying error messages that include a line, column and length
    private static void report(Token token, String message) {
        System.err.println("[line " + token.line + "] Error at:");
        System.out.println("'" + fileLines[token.line-1] + "'");
        System.err.println(repeat(token.column, " ") + repeat(token.length, "*\n" + message));
        hadError = true;
    }

    // Utility method to repeat strings
    public static String repeat(int count, String str) {
        return new String(new char[count]).replace("\0", str);
    }
}
