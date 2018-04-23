package com.company;
import java.util.*;
import java.io.*;

public class LexicalAnalyzer {
    // character classes
    public static final int LETTER = 0;
    public static final int DIGIT = 1;
    public static final int EOF = -1;
    public static final int EOL = -2;
    public static final int UNKNOWN = -3;

    // token codes
    public static final int INT_LIT = 10;
    public static final int IDENT = 11;
    public static final int COMMENT = 12;
    public static final int FLOAT_LIT = 13;
    public static final int STRING_LIT = 14;
    public static final int UTIL_PACKAGE = 15;
    public static final int IO_PACKAGE = 16;
    public static final int CHAR_LIT = 17;

    public static final int ASSIGN_OP = 20;
    public static final int ADD_OP = 21;
    public static final int SUB_OP = 22;
    public static final int MULT_OP = 23;
    public static final int DIV_OP = 24;
    public static final int EQUALS_OP = 25;
    public static final int NOTEQUALS_OP = 26;
    public static final int LESSEQUALS_OP = 27;
    public static final int GREATEREQUALS_OP = 28;
    public static final int AND_OP = 29;
    public static final int OR_OP = 30;
    public static final int NOT_OP = 31;
    public static final int ARROW_OP = 32;
    public static final int MOD_OP = 33;

    public static final int LEFT_PAREN = 41;
    public static final int RIGHT_PAREN = 42;
    public static final int LEFT_BRACE = 43;
    public static final int RIGHT_BRACE = 44;
    public static final int TILDE_SYM = 46;
    public static final int COLON_SYM = 47;
    public static final int RIGHT_BRACKET = 49;
    public static final int LEFT_BRACKET = 50;
    public static final int EXCL_MATION = 51;
    public static final int LESS_SYM = 52;
    public static final int GREATER_SYM = 53;
    public static final int AMPERSAND_SYM = 54;
    public static final int PIPE_SYM = 55;
    public static final int DOT_SYM = 56;
    public static final int DOUBLE_QUOTE = 57;
    public static final int SINGLE_QUOTE = 58;

    public static final int FOR_CODE = 61;
    public static final int IMPORT_CODE = 62;
    public static final int NEW_CODE = 63;
    public static final int IF_CODE = 64;
    public static final int ELSE_CODE = 65;
    public static final int EXTENDS_CODE = 66;
    public static final int PACKAGE_CODE = 67;
    public static final int SYNCHRONIZED_CODE = 68;
    public static final int DO_CODE = 69;
    public static final int INSTANCEOF_CODE = 70;
    public static final int SWITCH_CODE = 71;
    public static final int CASE_CODE = 72;
    public static final int DEFAULT_CODE = 73;
    public static final int CONTINUE_CODE = 74;
    public static final int BREAK_CODE = 75;
    public static final int CATCH_CODE = 76;
    public static final int GOTO_CODE = 77;
    public static final int NATIVE_CODE = 78;
    public static final int IMPLEMENTS_CODE = 79;
    public static final int RETURN_CODE = 80;
    public static final int INTERFACE_CODE = 81;

    public static final int PRINT_CODE = 82;
    public static final int THIS_CODE = 83;
    public static final int THROW_CODE = 84;
    public static final int THROWS_CODE = 85;
    public static final int TRANSIENT_CODE = 86;
    public static final int TRY_CODE = 87;
    public static final int VOID_CODE = 88;
    public static final int VOLATILE_CODE = 89;
    public static final int WHILE_CODE = 90;


    public static final int LONG_TYPE = 91;
    public static final int BOOLEAN_TYPE = 92;
    public static final int BYTE_TYPE = 93;
    public static final int CHAR_TYPE = 94;
    public static final int CLASS_TYPE = 95;
    public static final int CONSTANT_TYPE = 96;
    public static final int CONST_TYPE = 97;
    public static final int FLOAT_TYPE = 98;
    public static final int INT_TYPE = 99;
    public static final int DOUBLE_TYPE = 100;
    public static final int SHORT_TYPE = 101;
    public static final int STATIC_TYPE = 102;
    public static final int STRING_TYPE = 103;
    public static final int FINALLY_TYPE = 104;

    public static final int PRIVATE_CLASS = 105;
    public static final int PROTECTED_CLASS = 106;
    public static final int PUBLIC_CLASS = 107;
    public static final int SUPER_CLASS = 108;
    public static final int ABSTRACT_CLASS = 109;
    public static final int TYPE_DEFINE = 110;


    // parallel arrays to speed up the process of looking for errors
    public String[] keywords = {"abstract","boo","break","byte","case","catch","char","class",
            "constant","continue","default","do","double","else","extends","const","finally","float",
            "for","goto","if","implements","import","instanceof","int","interface","long","native","new","package",
            "private","protected","public class","return","short","static","String","super","switch","synchronized","this","throw",
            "throws", "transient","try","void","volatile","while","System.out.println", "java.util.", "java.io.", };

    public int[] keywordsTokens = {ABSTRACT_CLASS, BOOLEAN_TYPE, BREAK_CODE, BYTE_TYPE, CASE_CODE, CATCH_CODE,
            CHAR_TYPE, CLASS_TYPE, CONSTANT_TYPE, CONTINUE_CODE, DEFAULT_CODE, DO_CODE, DOUBLE_TYPE, ELSE_CODE,
            EXTENDS_CODE, CONST_TYPE, FINALLY_TYPE, FLOAT_TYPE, FOR_CODE, GOTO_CODE, IF_CODE, IMPLEMENTS_CODE,
            IMPORT_CODE, INSTANCEOF_CODE,INT_TYPE, INTERFACE_CODE, LONG_TYPE, NATIVE_CODE, NEW_CODE, PACKAGE_CODE, PRIVATE_CLASS,
            PROTECTED_CLASS, PUBLIC_CLASS, RETURN_CODE, SHORT_TYPE, STATIC_TYPE, STRING_TYPE, SUPER_CLASS, SWITCH_CODE, SYNCHRONIZED_CODE,
            THIS_CODE, THROW_CODE, THROWS_CODE, TRANSIENT_CODE, TRY_CODE, VOID_CODE, VOLATILE_CODE, WHILE_CODE,
            PRINT_CODE, UTIL_PACKAGE, IO_PACKAGE};

    public String[] relationalOperators = {"=","<::",">::","!=","<=",">="};
    public int[] relationalTokens = {EQUALS_OP, LESS_SYM, GREATER_SYM, NOTEQUALS_OP, LESSEQUALS_OP,
            GREATEREQUALS_OP};

    public String[] mathematicalOperators = {"+","-","%","*","/"};
    public int[] mathematicalTokens = {ADD_OP, SUB_OP, MOD_OP, MULT_OP, DIV_OP};

    //all the typical mathematical assignments as well +:: *:: etc
    public String[] assignmentOperators = {"=", "::"};
    public int[] assignmentTokens = {TYPE_DEFINE, ASSIGN_OP};

    public String[] comparisonOperators = {"and","or","not"};
    public int[] comparisonTokens = {AND_OP, OR_OP, NOT_OP};

    public String[] otherSymbols = {"~","!","->","(",")","[","]",":","&","|",".","{","}","<",">"};
    public int[] otherTokens = {TILDE_SYM, EXCL_MATION, ARROW_OP, LEFT_PAREN, RIGHT_PAREN,
            LEFT_BRACE, RIGHT_BRACE, COLON_SYM, AMPERSAND_SYM, PIPE_SYM, DOT_SYM, LEFT_BRACKET,
            RIGHT_BRACKET, LESS_SYM, GREATER_SYM};

    // global variables
    public int charClass;
    public char[] lexeme;
    public char[] characters;
    public char nextChar;
    public char prevChar;
    public int index = 0;
    public int lexLen;
    public int token;
    public int nextToken;
    public int prevToken;
    public File in_fp;
    public boolean error = false;
    // convert string from file into a character array
    public void setCharacters(char[] line) {
        characters = line;
    }

    // check is the character is the start to a relational operator
    public boolean isRelationalStart(char ch) {
        int tempIndex = index + 1;

        if(tempIndex < characters.length) {
            if((ch == '<' || ch == '>' || ch == '!' || ch == '=') && !Character.isWhitespace(characters[index])) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // check if character is the start of an object operator
    public boolean isObjectOperatorStart(char ch) {
        if(ch == '-') {
            return true;
        } else {
            return false;
        }
    }

    // check if character is the start of a variable type definer
    public boolean isVariableTypeStart(char ch) {
        if(ch == ':') {
            return true;
        } else {
            return false;
        }
    }

    // check if character is the start of a comment
    public boolean isCommentStart(char ch) {
        if(ch == '/') {
            return true;
        } else {
            return false;
        }
    }

    // check if character is the start of a logical operator
    public boolean isLogicStart(char ch) {
        if(ch == '|' || ch == '&') {
            return true;
        } else {
            return false;
        }
    }

    // build the next lexeme if it is more than one character
    public void buildLexeme(String[] keyArray, int[] tokenArray) {
        int key = -1;  // index not found
        addChar();  // add character to array

        if(index < characters.length) {
            nextChar = characters[index];  // get character from array
            String temp = new String(lexeme);  // convert char[] to string
            String text = temp.trim() + String.valueOf(nextChar);  // trim extra spaces

            if(Arrays.asList(keyArray).indexOf(text) != -1) {  // if index found
                key = Arrays.asList(keyArray).indexOf(text);
                addChar();
                nextToken = tokenArray[key];  // find coresponding token from array
                if(index < characters.length) {
                    index++;
                }
            } else if(nextChar != ':' && characters[index+1] != ':' && !Character.isWhitespace(nextChar)) {  // wrong syntax
                System.out.print("SYNTAX ERROR - Relational operator not recognized - ");
                error();
            }
        } else {
            String character = String.valueOf(nextChar);

            // check if character has a defined token
            if(Arrays.asList(mathematicalOperators).indexOf(character) != -1) {
                key = Arrays.asList(mathematicalOperators).indexOf(character);
                nextToken = mathematicalTokens[key];
            } else if(Arrays.asList(otherSymbols).indexOf(character) != -1) {
                key = Arrays.asList(otherSymbols).indexOf(character);
                nextToken = otherTokens[key];
            } else if(Arrays.asList(assignmentOperators).indexOf(character) != -1) {
                key = Arrays.asList(assignmentOperators).indexOf(character);
                nextToken = assignmentTokens[key];
            } else if(Arrays.asList(comparisonOperators).indexOf(character) != -1) {
                key = Arrays.asList(comparisonOperators).indexOf(character);
                nextToken = comparisonTokens[key];
            } else if(Arrays.asList(relationalOperators).indexOf(character) != -1) {
                key = Arrays.asList(relationalOperators).indexOf(character);
                nextToken = relationalTokens[key];
            }
        }
    }

    // build the lexeme for a comment
    public void buildComment() {
        int key = -1;  // index not found
        addChar();  // add character to array

        if(index < characters.length - 1) {
            nextChar = characters[index];
            if(nextChar == '/') {  // next char needed for comment
                addChar();
                while(index < characters.length-1) {  // grab the rest of the line
                    index++;
                    lexeme[index] = characters[index];
                }
                index++;
                nextToken = COMMENT;
            } else {  // wrong syntax
                System.out.print("SYNTAX ERROR - Incorrect comment start - ");
                error();
            }
        }
    }

    // build the lexeme for a statement
    public void buildStatement() {
        int key = -1;  // index not found
        addChar();
        nextChar = characters[index];

        // grab all the following characters as long as it's a letter
        while(index <= characters.length - 1 && Character.isLetter(nextChar)) {
            addChar();
            index++;
            if (index < characters.length) {
                nextChar = characters[index];
            }
        }

        String temp = new String(lexeme);

        if(Arrays.asList(keywords).indexOf(temp.trim()) != -1) {  // if found in array
            key = Arrays.asList(keywords).indexOf(temp.trim());
            nextToken = keywordsTokens[key];
        } else {
            System.out.print("SYNTAX ERROR - Incorrect statement declaration - ");
            error();
        }
    }

    // build a lexeme for a character literal
    public void buildSingleQuote() {
        addChar();
        getChar();  // get next character from input stream

        if(charClass == SINGLE_QUOTE) {  // empty char literal
            addChar();
            index++;
            nextToken = CHAR_LIT;
        } else {
            addChar();
            getChar();

            if(charClass != SINGLE_QUOTE) {  // more than one character, syntax error
                System.out.print("SYNTAX ERROR - Missing end quote - ");
                error();
            } else {
                addChar();
                index++;
                nextToken = CHAR_LIT;
            }
        }
    }

    // assign a token to an unknown symbol
    public int lookup(char ch) {
        String character = String.valueOf(ch);
        int key = -1;

        // check to see if the start of a larger lexeme
        if(isRelationalStart(ch)) {
            buildLexeme(relationalOperators, relationalTokens);
        } else if(isLogicStart(ch)) {
            buildLexeme(comparisonOperators, comparisonTokens);
        } else if(isObjectOperatorStart(ch)) {
            buildLexeme(otherSymbols, otherTokens);
        } else if(isVariableTypeStart(ch)) {
            buildLexeme(assignmentOperators, assignmentTokens);
        }else {  // check arrays for character
            if(Arrays.asList(mathematicalOperators).indexOf(character) != -1) {
                key = Arrays.asList(mathematicalOperators).indexOf(character);
                addChar();
                nextToken = mathematicalTokens[key];
            } else if(Arrays.asList(otherSymbols).indexOf(character) != -1) {
                key = Arrays.asList(otherSymbols).indexOf(character);
                addChar();
                nextToken = otherTokens[key];
            } else if(Arrays.asList(assignmentOperators).indexOf(character) != -1) {
                key = Arrays.asList(assignmentOperators).indexOf(character);
                addChar();
                nextToken = assignmentTokens[key];
            } else if(Arrays.asList(comparisonOperators).indexOf(character) != -1) {
                key = Arrays.asList(comparisonOperators).indexOf(character);
                addChar();
                nextToken = comparisonTokens[key];
            } else if(Arrays.asList(relationalOperators).indexOf(character) != -1) {
                key = Arrays.asList(relationalOperators).indexOf(character);
                addChar();
                nextToken = relationalTokens[key];
            }
        }

        prevToken = nextToken;
        return nextToken;
    }

    // add nextChar to lexeme
    public void addChar() {
        if (lexLen <= 110) {
            lexeme[lexLen++] = nextChar;
            lexeme[lexLen] = 0;
        }
        else {
            System.out.println("Error - lexeme is too long");
        }
    }

    // get the next character of input and determine its character class
    public void getChar() {
        if (index < characters.length) {
            nextChar = characters[index];
            if (Character.isLetter(nextChar)) {
                charClass = LETTER;
            } else if (Character.isDigit(nextChar)) {
                charClass = DIGIT;
            } else if (nextChar == '.') {
                charClass = DOT_SYM;
            } else if (nextChar == '\"') {
                charClass = DOUBLE_QUOTE;
            } else if (nextChar == '\'') {
                charClass = SINGLE_QUOTE;
            } else{
                charClass = UNKNOWN;
            }
        } else {
            charClass = EOL;
        }

        index++;
    }

    // call getChar until it returns a non-whitespace character
    void getNonBlank() {
        while (Character.isWhitespace(nextChar)) {
            getChar();
        }
    }

    // simple lexical analyzer for arithmetic expressions
    public int lex() {
        lexeme = new char[250];
        lexLen = 0;
        getNonBlank();

        switch (charClass) {

            // parse identifiers
            case LETTER:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT || charClass == DOT_SYM) {
                    addChar();
                    getChar();
                }
                nextToken = IDENT;
                break;

            // parse integer literals
            case DIGIT:
                boolean decimal = false;
                addChar();
                getChar();
                while (charClass == DIGIT || charClass == DOT_SYM) {
                    if(decimal == false && charClass == DOT_SYM) {
                        decimal = true;
                        addChar();
                        getChar();
                    }
                    if(decimal == true && charClass == DOT_SYM) {
                        System.out.print("SYNTAX ERROR - More than one decimal - ");
                        error();
                        break;
                    } else {
                        addChar();
                        getChar();
                    }
                }

                if(decimal) {
                    nextToken = FLOAT_LIT;
                } else {
                    nextToken = INT_LIT;
                }
                break;

            // parse string literal
            case DOUBLE_QUOTE:
                addChar();
                getChar();
                boolean endQuote = false;
                while(index < characters.length && !endQuote) {
                    if(charClass == DOUBLE_QUOTE) {
                        addChar();
                        endQuote = true;
                        getChar();
                    } else {
                        addChar();
                        getChar();
                    }
                }
                if(!endQuote) {
                    System.out.print("SYNTAX ERROR - Missing end quote - ");
                    error();
                }
                nextToken = STRING_LIT;
                break;

            case SINGLE_QUOTE:
                buildSingleQuote();

                // parse parentheses, operators and other symbols
            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;

            // end of line from file
            case EOL:
                lexeme[0] = 'E';
                lexeme[1] = 'O';
                lexeme[2] = 'L';
                lexeme[3] = 0;
                nextToken = EOL;
                break;

        }

        String value = new String(lexeme);

        if(nextToken == IDENT) {  // check if identifier is a reserved keyword
            int key = Arrays.asList(keywords).indexOf(value.trim());
            if(key != -1) {
                nextToken = keywordsTokens[key];
            }
        }
        prevToken = nextToken;
        return nextToken;
    }

    // an error was found
    public void error() {
        error = true;
    }

    // MAIN CLASS -------------------------------------------------------------------
    public static void main(String[] args) {

        int lineCount = 1;  // keep track of source code line numbers
        boolean error = false;
        ArrayList<Integer> tokenArray = new ArrayList<Integer>();
        ArrayList<String> lexemeArray = new ArrayList<String>();

        // open the input data file and process its contents
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("PurpleGiraffes.pg")))) {
            String line;
            int i = 0;
            while((line = reader.readLine()) != null && !error) {
                lineCount++;
                LexicalAnalyzer analyze = new LexicalAnalyzer();
                char[] characters = line.toCharArray();  // convert string to character array
                /*System.out.println(line);*/
                analyze.setCharacters(characters);
                analyze.getChar();

                do {
                    analyze.lex();  // call to lexical analyzer

                    String temp = new String(analyze.lexeme);
                    String lexeme = temp.trim();  // trim white spaces

                    tokenArray.add(analyze.nextToken);
                    lexemeArray.add(lexeme);
                    i++;
                } while(analyze.nextToken != EOL && analyze.error == false && i < 200);

                if(analyze.error == true) {
                    error = true;
                }
            }

            // if there was a lexeme error
            if(error == true) {
                System.out.print("Line " + lineCount + "\n");
            } else {  // parse program
                tokenArray.add(-1);
                lexemeArray.add("EOF");

                parseProgram(tokenArray, lexemeArray);
                for(i = 0; i < lexemeArray.size(); i++){
                   System.out.print(lexemeArray.get(i) + " | ");
                    System.out.print(tokenArray.get(i));
                    System.out.println();
                }

            }
        } catch (IOException e) {
            System.out.println("ERROR - cannot open file");
        }
    }
    public static void parseProgram(ArrayList<Integer> tokenArray, ArrayList<String> lexemeArray) {
        try{
            PrintWriter writer = new PrintWriter("Test.java", "UTF-8");
            RecursiveParser parser = new RecursiveParser(tokenArray, lexemeArray, writer);

            parser.parsePackages();  // parse the program

            // if syntax error, write over file to simply print an error message
            if(parser.getError()) {
                writer.close();

                try {
                    PrintWriter error = new PrintWriter("Test.java", "UTF-8");
                    error.println("import java.util.*;");
                    error.println("import java.io.*;");
                    error.println("public class Test\r\n{");
                    error.println("public static void main(String[] args)\r\n{");
                    error.println("System.out.println(\"Error generating file.\");");
                    error.println("}\r\n}");
                    error.close();
                } catch(IOException e) {
                    System.out.println("ERROR - cannot open file");
                }
            } else { // no errors found
                System.out.println("FINISHED - No errors found");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("ERROR - cannot open file");
        }
    }
}

