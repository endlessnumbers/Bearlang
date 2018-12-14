package com.interpreter.bear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Bear {

    public static boolean hadError = false;
    public static void main(String[] args) {
        //more than one argument -- invalid
        //this terminates and returns error code 64,
        //which is UNIX standard exit code for incorrect usage
        //see freebsd sysexits.h
        if (args.length > 1) {
            System.out.println("Usage: jbear [script]");
            System.exit(64);
        }
        //given one argument (file path), run that file
        else if (args.length == 1){
            runFile(args[0]);
        }
        //given no args, run REPL
        else {
            runPrompt();
        }
    }

    //Given path to source file, read and execute code
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        //error code incorrect data
        if (hadError)
            System.exit(65);
    }

    //run repl
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        //infinite loop - escape with ctrl-c for now
        //LATER implement actual exit command?? What do other interpreters do?
        for (;;){
            System.out.print("> ");
            run(reader.readLine());
            //reset error flag so an error doesn't kill repl
            hadError = false;
        }        
    }

    //actually do the interpreting
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        //to be implemented -- first just print the tokens 
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    //error reporting, tells user the line number (bare minimum!),
    //This is very basic so definitely a place to improve upon!!
    private static void report(int line, String location, String message) {
        System.err.println(
            "[line " + line + "] Error" + location + ": " + message
        );
        hadError = true;
    }
}