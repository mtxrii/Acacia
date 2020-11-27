package com.edavalos.acacia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Acacia {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: acacia [script]");
            System.exit(64);

        } else if (args.length == 1) { // run a script
            runFile(args[0]);

        } else { // no script specified, enter runtime
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("~#: ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }
}
