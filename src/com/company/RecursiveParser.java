package com.company;

import com.company.LexicalAnalyzer;

import java.io.PrintWriter;
import java.util.ArrayList;

// RECURSIVE-DECENT PARSER ------------------------------------------------------------
class RecursiveParser  extends LexicalAnalyzer {
    // global variables
    public int nextToken;
    public int index;
    public int line;
    public ArrayList<Integer> tokenArray;
    public ArrayList<String> lexemeArray;
    public PrintWriter writeFile;

    public ArrayList<String> variableArray = new ArrayList<String>();
    public ArrayList<String> typeArray = new ArrayList<String>();

    public String programName;
    public boolean errors = false;
    public boolean ifStatement = false;

    // assign starting values to the global variables
    public RecursiveParser(ArrayList<Integer> tokens, ArrayList<String> lexemes, PrintWriter writer) {
        tokenArray = tokens;
        lexemeArray = lexemes;
        writeFile = writer;
        index = 0;
        line = 1;
    }

    // get the nextToken from the array
    public void nextToken() {
        index++;
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }
    }

    // print error message to console
    public void error(String message) {
        System.out.println("ERROR - Line " + line + ": " + message);

        // go to end of the line and continue parsing
        while (nextToken != EOL && index < tokenArray.size()) {
            index++;
            nextToken = tokenArray.get(index);
        }
        line++;
        index++;

        errors = true;
    }

    // return errors boolean value
    public boolean getError() {
        return errors;
    }

    // parse the included program packages, if any
    public void parsePackages() {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        if (nextToken == IMPORT_CODE) {

            System.out.println(nextToken + "-|-" + IMPORT_CODE);
            writeFile.print("import");  // print corresponding java code to file
            nextToken();  // get next token
            if (nextToken == UTIL_PACKAGE || nextToken == IO_PACKAGE) {

                if (nextToken == UTIL_PACKAGE) {
                    writeFile.print(" java.util.*");  // print corresponding java code
                } else if (nextToken == IO_PACKAGE) {
                    writeFile.print(" java.io.*");  // print corresponding java code
                }
                nextToken();  // get next token
                if (nextToken != EXCL_MATION) {
                    String message = "Missing exclamation point";  // create error message
                    error(message);  // print message and line number to console
                } else {
                    writeFile.print(lexemeArray.get(index));  // write lexeme to file
                    nextToken();  // get next token
                    if (nextToken != EOL) {
                        String message = "No multiple declarations";
                        error(message);
                    } else {
                        writeFile.print("\r\n");  // print new line to file
                        line++;
                        index++;
                        parsePackages(); // recursive parsePackage call
                    }
                }
            } else {
                String message = "Invalid package";
                error(message);
                parsePackages(); // recursive parsePackage call
            }
        } else if (nextToken == EOL) {
            System.out.println(nextToken + "-|-" + EOL);
            line++;
            index++;
            writeFile.print("\r\n");
            parsePackages();  // recursive parsePackage call
        } else if (nextToken == PUBLIC_CLASS) {
            writeFile.print("public");  // print corresponding java code
            index++;

            parseProgram();  // call to program body parse
        } else if (nextToken == CLASS_TYPE){
            writeFile.print(" class");
            index++;
        }else {
            String message = "Incorrect start program declaration";
            error(message);
        }
    }

    // parse the starting declaration for the program
    public void parseProgram() {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);

        }

        if (nextToken == CLASS_TYPE) {
            index++; //skip to class name
            programName = lexemeArray.get(index);  // first indent is the name of the file
            writeFile.print(programName);  // write lexeme to file
            nextToken();  // get next token

            if (nextToken == LEFT_BRACKET) {
                writeFile.print("\r\n{\r\npublic static void main(String[] args) {\r\n");
                index+=12;
                nextToken();
                if (nextToken == EOL) {
                    writeFile.print("\r\n");
                    line++;
                    index++;
                    parseBlock();
                }else{
                    String message = "Wrong Index" + nextToken;
                    error(message);
                }
            }
        } else {
            System.out.println("other operations to come");
        }
    }

    // parse all the statements in the program block
    public void parseBlock() {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        if (nextToken == EOF) {
            System.out.println("reached end of the file");
        } else if (nextToken == EOL) {
            writeFile.print("\r\n");
            line++;
            index++;
            parseBlock();  // recursive parseBlock call
        } else if (nextToken == COMMENT) {
            String comment = lexemeArray.get(index);
            writeFile.print(comment);  // write comment to file
            nextToken();  // get next token
            if (nextToken == EOL) {
                writeFile.print("\r\n");
                line++;
                index++;
                parseBlock();  // recursive parseBlock call
            }
        } else if (nextToken == INT_TYPE || nextToken == FLOAT_TYPE || nextToken == STRING_TYPE) {
            if (nextToken == INT_TYPE) {
                writeFile.print("int");
            } else if (nextToken == FLOAT_TYPE) {
                writeFile.print("float");
            } else if (nextToken == STRING_TYPE) {
                writeFile.print("String");
            }

            String type = lexemeArray.get(index);

            index++;
            variableDeclaration(type);  // call to parse variable declaration
        } else if (nextToken == PRINT_CODE) {
            writeFile.print("System.out.println");  // write java code
            nextToken();
            if (nextToken == LEFT_PAREN) {
                writeFile.print(lexemeArray.get(index));
                nextToken();
                if (nextToken == IDENT) {
                    String variableName = lexemeArray.get(index);

                    // check if variable was initialized
                    if (variableArray.indexOf(variableName) != -1) {
                        int key = variableArray.indexOf(variableName);
                        String variableType = typeArray.get(key);

                        writeFile.print(lexemeArray.get(index)); // write variable

                        // if variable declaration also mathematical assignment
                        index++;
                        mathematicalAssignment(variableType);  // call to mathematical assignment
                    } else {
                        String message = "Variable may not have been initialized";
                        error(message);
                        parseBlock();  // even with error, continue parsing
                    }

                } else if (nextToken == STRING_LIT) {
                    writeFile.print(lexemeArray.get(index));
                    nextToken();
                    if (nextToken == ADD_OP) {
                        writeFile.print(lexemeArray.get(index));
                        index++;
                        String type = "String";
                        mathematicalAssignment(type);
                    } else if (nextToken == RIGHT_PAREN) {
                        writeFile.print(lexemeArray.get(index));
                        nextToken();  // get next token
                        if (nextToken == EXCL_MATION) {
                            writeFile.print(lexemeArray.get(index));  // write to file
                            nextToken();
                            if (nextToken == EOL) {
                                writeFile.print("\r\n");
                                line++;  // keep track of line number
                                index++;
                                parseBlock();  // recursive paresBlock call

                            } else {
                                String message = "Only one declaration per line";
                                error(message);
                                parseBlock();  // even with errors, continue to parse
                            }
                        } else {
                            String message = "Missing exclamation";
                            error(message);
                            parseBlock();
                        }
                    } else {
                        String message = "Incorrect printMessage parameters";
                        error(message);
                        parseBlock();
                    }
                } else {
                    String message = "Incorrect printMessage parameters";
                    error(message);
                    parseBlock();  // even with errors, continue to parse
                }
            } else {
                String message = "Missing left parentheses";  // error message
                error(message);
                parseBlock();  // even with errors, continue to parse
            }
        } else if (nextToken == IF_CODE) {
            writeFile.print("if");
            ifStatement = true;
            index++;
            parseIfStatement();  // call to parseIfStatement
        } else if (nextToken == ELSE_CODE) {
            writeFile.print("}else");
            ifStatement = true;
            nextToken();  // get token
            if (nextToken == COLON_SYM) {
                writeFile.print("{\r\n");
                index++;
                parseBlock();  // recursive parseBlock call
            } else {
                String message = "Missing colon";
                error(message);
                parseBlock();  // even with errors, continue to parse
            }
        }else if (nextToken == EOF && !ifStatement) {
            nextToken();
            if (nextToken == IDENT) {
                String variableName = lexemeArray.get(index);
                // make sure ending identifier is the same as the start
                if (variableName.equals(programName)) {
                    nextToken();
                    if (nextToken == EXCL_MATION) {
                        writeFile.print("}\r\n}");
                        nextToken();
                        if (nextToken == EOL) {
                            writeFile.print("\r\n");
                        } else {
                            String message = "Only one declaration per line";
                            error(message);
                            parseBlock();
                        }
                    } else {
                        String message = "Missing exclamation";
                        error(message);
                        parseBlock();  // even with errors, continue to parse
                    }
                } else {
                    String message = "Name does not match program name";
                    error(message);
                    parseBlock();
                }
            } else {
                String message = "Incorrect end program declaration";
                error(message);
                parseBlock();
            }
        } else if (nextToken == EXCL_MATION && tokenArray.get(index - 1) == RIGHT_PAREN) {
            writeFile.print(lexemeArray.get(index));
            nextToken();
            if (nextToken == EOL) {
                writeFile.print("\r\n");
                line++;
                index++;
                parseBlock();  // recursive parseBlock call
            } else {
                String message = "Only one declaration per line";
                error(message);
                parseBlock();  // even with errors, continue to parse
            }
        } else {
            String message = "Incorrect Expression";
            error(message);
            parseBlock();
        }
    }


    // parse the declaration of a variable
    public void variableDeclaration(String type) {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);

        }

        // declare an array variable
        if (nextToken == LEFT_BRACE) {
            writeFile.print(lexemeArray.get(index));
            nextToken();  // get next token
            if (nextToken == RIGHT_BRACE) {
                writeFile.print(lexemeArray.get(index));
                nextToken();
                if (nextToken == TYPE_DEFINE) {
                    writeFile.print(" ");
                    nextToken();
                    if (nextToken == IDENT) {
                        writeFile.print(lexemeArray.get(index));
                        nextToken();

                        // add variable to arraylist to keep track of inialized
                        // variables and their types
                        variableArray.add(lexemeArray.get(index));
                        typeArray.add(type);

                        if (nextToken == EXCL_MATION) {
                            writeFile.print(lexemeArray.get(index));
                            line++;
                            index++;
                            parseBlock();  // recursive parseBlock call
                        } else if (nextToken == ASSIGN_OP) {
                            writeFile.print(lexemeArray.get(index));
                            index++;
                            arrayAssignment(type);  // call to arrayAssignment
                        } else {
                            String message = "Incorrect array initialization";
                            error(message);
                            parseBlock();
                        }
                    } else {
                        String message = "Incorrect array initialization";
                        error(message);
                        parseBlock();  // even with errors, continue to parse
                    }
                } else {
                    String message = "Incorrect array initialization";
                    error(message);
                    parseBlock();
                }
            } else {
                String message = "Missing ending left brace";  // error message
                error(message);
                parseBlock();
            }
        } else if (nextToken == IDENT) {
            writeFile.print(" ");

            writeFile.print(lexemeArray.get(index));  // write to file

            // keep track of variables and type
            variableArray.add(lexemeArray.get(index));
            typeArray.add(type);

            nextToken();  // get next token
            if (nextToken == ASSIGN_OP) {
                writeFile.print(lexemeArray.get(index));
                index++;
                parseAssignment(type);
            } else if (nextToken == EXCL_MATION) {
                writeFile.print(lexemeArray.get(index));
                nextToken();
                if (nextToken == EOL) {
                    writeFile.print("\r\n");
                    line++;
                    index++;
                    parseBlock();  // recursive parseBlock call
                } else {
                    String message = "Only one declaration per line";
                    error(message);
                    parseBlock();
                }
            } else {
                String message = "Incorrect varibale initialization";
                error(message);
                parseBlock();
            }
        } else{
            String message = "Incorrect variable initialization";
            error(message);
            parseBlock();  // even with errors, continue to parse
        }
    }

    public void parseAssignment(String type) {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        if (nextToken == INT_LIT && type.equals("int")) {
            writeFile.print(lexemeArray.get(index));
            index++;
            assignmentStatement();
        } else if (nextToken == FLOAT_LIT && type.equals("float")) {
            writeFile.print(lexemeArray.get(index));
            index++;
            assignmentStatement();
        } else if (nextToken == STRING_LIT && type.equals("String")) {
            writeFile.print(lexemeArray.get(index));
            index++;
            System.out.println("STRING LIT");
            assignmentStatement();
        } else if (nextToken == IDENT) {
            String variableName = lexemeArray.get(index);

            if (variableArray.indexOf(variableName) != -1) {
                int key = variableArray.indexOf(variableName);
                String variableType = typeArray.get(key);

                if (type.equals("String")) {
                    writeFile.print(lexemeArray.get(index));
                    index++;
                    mathematicalAssignment(type);
                } else if (!type.equals(variableType)) {
                    String message = "Incompatable type values";
                    error(message);
                    parseBlock();
                } else {
                    writeFile.print(lexemeArray.get(index));
                    index++;
                    mathematicalAssignment(type);
                }
            } else {  // variable not found in variable array
                String message = "Variable may not have been initialized";
                error(message);
                parseBlock();
            }
        } else if (nextToken == EXCL_MATION) {
            writeFile.print(lexemeArray.get(index));
            nextToken();
            if (nextToken == EOL) {
                writeFile.print("\r\n");  // write to file
                line++;
                index++;
                parseBlock();
            } else {
                String message = "Only one declaraction per line";
                error(message);
                parseBlock();
            }
        } else if (nextToken == ADD_OP || nextToken == SUB_OP || nextToken == MOD_OP ||
                nextToken == MULT_OP || nextToken == DIV_OP) {
            writeFile.print(lexemeArray.get(index));
            index++;
            mathematicalAssignment(type);  // call to matematicalAssignment
        } else {
            String message = "Incorrect type value";
            error(message);
            parseBlock();  // even with errors, continue to parse
        }
    }

    public void mathematicalAssignment(String type) {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        // if next token is a mathematical operator and previous was a ident or literal
        if ((nextToken == ADD_OP || nextToken == SUB_OP || nextToken == MOD_OP ||
                nextToken == MULT_OP || nextToken == DIV_OP) && (tokenArray.get(index - 1) == IDENT ||
                tokenArray.get(index - 1) == INT_LIT || tokenArray.get(index - 1) == FLOAT_LIT ||
                tokenArray.get(index - 1) == STRING_LIT)) {

            writeFile.print(lexemeArray.get(index));
            nextToken();  // get token
            // if literal is the same as variable type
            if ((nextToken == INT_LIT && type.equals("integer")) ||
                    (nextToken == FLOAT_LIT && type.equals("float")) |
                            (nextToken == STRING_LIT && type.equals("String"))) {

                writeFile.print(lexemeArray.get(index));
                nextToken();
                if (nextToken == EXCL_MATION) {
                    writeFile.print(lexemeArray.get(index));  // write to file
                    nextToken();
                    if (nextToken == EOL) {
                        writeFile.print("\r\n");
                        line++;
                        index++;
                        parseBlock();  // recursive parseBlock call
                    } else {
                        String message = "Only one declaraction per line";
                        error(message);
                        parseBlock();
                    }
                } else {
                    System.out.println(lexemeArray.get(index));
                    String message = "Incorrect type value";
                    error(message);
                    parseBlock();  // even with errors, continue to parse
                }
            } else if (nextToken == IDENT) {
                String variableName = lexemeArray.get(index);

                if (variableArray.indexOf(variableName) != -1) {
                    int key = variableArray.indexOf(variableName);
                    String variableType = typeArray.get(key);

                    if (type.equals("String")) {
                        writeFile.print(lexemeArray.get(index));
                        index++;
                        mathematicalAssignment(type);  // call to mathematicalAssignment
                    } else if (!type.equals(variableType)) {
                        String message = "Incompatable type values";
                        error(message);
                        parseBlock();
                    } else {
                        writeFile.print(lexemeArray.get(index));
                        index++;
                        mathematicalAssignment(type); // call to mathematical assignment
                    }
                } else {
                    String message = "Variable may not have been initialized";
                    error(message);
                    parseBlock();
                }
            } else {
                String message = "Incorrect type value";  // error message
                error(message);
                parseBlock();
            }
        } else if ((nextToken == IDENT || nextToken == STRING_LIT) && (tokenArray.get(index - 1) == ADD_OP ||
                tokenArray.get(index - 1) == SUB_OP || tokenArray.get(index - 1) == MOD_OP ||
                tokenArray.get(index - 1) == MULT_OP || tokenArray.get(index - 1) == DIV_OP)) {

            if (nextToken == STRING_LIT) {
                writeFile.print(lexemeArray.get(index));
                index++;
                mathematicalAssignment(type);
            } else {
                String variableName = lexemeArray.get(index);

                if (variableArray.indexOf(variableName) != -1) {
                    int key = variableArray.indexOf(variableName);
                    String variableType = typeArray.get(key);

                    if (type.equals("String")) {
                        writeFile.print(lexemeArray.get(index));
                        index++;
                        mathematicalAssignment(type);
                    } else if (!type.equals(variableType)) {
                        String message = "Incompatable type values";
                        error(message);
                        parseBlock();
                    } else {
                        writeFile.print(lexemeArray.get(index));
                        index++;
                        mathematicalAssignment(type);
                    }
                } else {
                    String message = "Variable may not have been initialized";
                    error(message);
                    parseBlock();
                }
            }
        } else if (nextToken == EXCL_MATION) {
            writeFile.print(lexemeArray.get(index));  // write to file
            nextToken();  // get next token
            if (nextToken == EOL) {
                writeFile.print("\r\n");
                line++;
                index++;
                parseBlock();  // recursive parseBlock call
            } else {
                String message = "Only one declaraction per line";
                error(message);
                parseBlock();
            }
        } else if (nextToken == RIGHT_PAREN) {
            writeFile.print(lexemeArray.get(index));
            index++;
            parseBlock();
        } else {
            String message = "Incorrect expression";  // error message
            error(message);
            parseBlock();  // even with errors, continue to parse
        }
    }

    public void assignmentStatement() {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }
        if (nextToken == EXCL_MATION) {
            writeFile.print(lexemeArray.get(index));
            index++;
            nextToken = tokenArray.get(index);  // get next token
            if (nextToken == EOL) {
                writeFile.print("\r\n");
                line++;
                index++;
                parseBlock();  // recursive parseBlock call
            } else {
                String message = "Only one declaration per line";
                error(message);
                parseBlock();
            }
        } else {
            String message = "Missing exclamation point";
            error(message);
            parseBlock();  // even with errors, continue to parse
        }
    }

    // check for brackes after array assignment
    public void arrayAssignment(String type) {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        System.out.println(lexemeArray.get(index) + " " + nextToken);
        if (nextToken == LEFT_BRACKET) {
            writeFile.print(lexemeArray.get(index));
            index++;
            arrayLiteral(type);  // call to array literal assignment
        } else {
            String message = "Incorrect array initialization";
            error(message);
            parseBlock();  // even with errors, continue to parse
        }
    }

    // check the contents of the literals assigned to an array
    public void arrayLiteral(String type) {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        if ((nextToken == INT_LIT && type.equals("integer")) ||
                (nextToken == FLOAT_LIT && type.equals("float")) ||
                (nextToken == STRING_LIT && type.equals("String"))) {

            writeFile.print(lexemeArray.get(index));
            nextToken();
            if (nextToken == TILDE_SYM) {
                writeFile.print(lexemeArray.get(index));
                index++;
                arrayLiteral(type);  // recursive arrayLiteral call
            } else if (nextToken == RIGHT_BRACKET) {
                writeFile.print(lexemeArray.get(index));
                nextToken();
                if (nextToken == EXCL_MATION) {
                    writeFile.print(lexemeArray.get(index));
                    nextToken();
                    if (nextToken == EOL) {
                        writeFile.print("\r\n");
                        line++;
                        index++;
                        parseBlock();  // recursive parseBlock call
                    } else {
                        String message = "Only one declaraction per line";
                        error(message);
                        parseBlock();
                    }
                } else {
                    String message = "Missing exclamation";
                    error(message);
                    parseBlock();  // even with errors, continue to parse
                }
            } else {
                String message = "Missing comma delimiter";  // error message
                error(message);
                parseBlock();
            }
        } else {
            String message = "Value type not compatable with array type";
            error(message);
            parseBlock();
        }
    }

    // parse the parameters of an if statement
    public void parseIfStatement() {
        if (index < tokenArray.size()) {
            nextToken = tokenArray.get(index);
        }

        if (nextToken == LEFT_PAREN) {
            writeFile.print(lexemeArray.get(index));
            nextToken();  // get next token
            if (nextToken == IDENT) {
                String variableName = lexemeArray.get(index);

                // if variable was initialized
                if (variableArray.indexOf(variableName) != -1) {
                    int key = variableArray.indexOf(variableName);
                    String variableType = typeArray.get(key);

                    writeFile.print(lexemeArray.get(index));
                    nextToken();  // get next token
                    if (nextToken == EQUALS_OP || nextToken == LESS_SYM || nextToken == GREATER_SYM ||
                            nextToken == NOTEQUALS_OP || nextToken == LESSEQUALS_OP || nextToken == GREATEREQUALS_OP) {

                        writeFile.print(lexemeArray.get(index));
                        nextToken();

                        if (nextToken == IDENT || nextToken == INT_LIT || nextToken == FLOAT_LIT ||
                                nextToken == STRING_LIT) {

                            if (nextToken == IDENT) {
                                if (variableArray.indexOf(variableName) != -1) {
                                    key = variableArray.indexOf(variableName);
                                    variableType = typeArray.get(key);

                                    writeFile.print(lexemeArray.get(index));
                                    nextToken();  // get next token
                                    if (nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
                                        System.out.println("operation symbol");
                                        writeFile.print(lexemeArray.get(index));
                                        index++;
                                        parseIfStatement();  // recursive parseIfStatement call
                                    } else if (nextToken == RIGHT_PAREN) {
                                        writeFile.print(lexemeArray.get(index));
                                        nextToken();
                                        if (nextToken == COLON_SYM) {
                                            writeFile.print("{\r\n");
                                            nextToken();
                                            if (nextToken == EOL) {
                                                writeFile.print("\r\n");
                                                line++;
                                                index++;
                                                parseBlock();
                                            } else {
                                                String message = "Only one declaraction per line";
                                                error(message);
                                                parseBlock();  // even with errors, continue to parse
                                            }
                                        } else {
                                            String message = "Missing colon";
                                            error(message);
                                            parseBlock();
                                        }
                                    } else {
                                        String message = "Incorrect expression";
                                        error(message);
                                        parseBlock();
                                    }
                                } else {
                                    String message = "Variable may not have been initialized";
                                    error(message);
                                    parseBlock();  // even with errors, continue to parse
                                }
                            } else {
                                writeFile.print(lexemeArray.get(index));
                                nextToken();
                                if (nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
                                    System.out.println("operationldkfjslkdfjskldjf");
                                    writeFile.print(lexemeArray.get(index));
                                    index++;
                                    parseIfStatement();
                                } else if (nextToken == RIGHT_PAREN) {
                                    writeFile.print(lexemeArray.get(index));
                                    nextToken();
                                    if (nextToken == COLON_SYM) {
                                        writeFile.print("{\r\n");  // write to file
                                        nextToken();
                                        if (nextToken == EOL) {
                                            writeFile.print("\r\n");
                                            line++;
                                            index++;
                                            parseBlock();
                                        } else {
                                            String message = "Only one declaraction per line";
                                            error(message);
                                            parseBlock();
                                        }
                                    } else {
                                        String message = "Missing colon";
                                        error(message);
                                        parseBlock();
                                    }
                                } else {
                                    String message = "Incorrect expression";
                                    error(message);
                                    parseBlock();
                                }
                            }
                        } else {
                            String message = "Incorrect expression";
                            error(message);
                            parseBlock();  // even with errors, continue to parse
                        }
                    } else {
                        String message = "Incorrect relational operator";  // error message
                        error(message);
                        parseBlock();
                    }
                } else {
                    String message = "Variable may not have been initialized";
                    error(message);
                    parseBlock();
                }
            } else {
                String message = "Incorrect expression";
                error(message);
                parseBlock();
            }
        } else if (nextToken == IDENT) {
            System.out.println("next :" + lexemeArray.get(index));
            String variableName = lexemeArray.get(index);

            if (variableArray.indexOf(variableName) != -1) {
                int key = variableArray.indexOf(variableName);
                String variableType = typeArray.get(key);

                writeFile.print(lexemeArray.get(index));  // write to file
                nextToken();  // get next token

                if (nextToken == EQUALS_OP || nextToken == LESS_SYM || nextToken == GREATER_SYM ||
                        nextToken == NOTEQUALS_OP || nextToken == LESSEQUALS_OP || nextToken == GREATEREQUALS_OP) {

                    writeFile.print(lexemeArray.get(index));
                    nextToken();  // get next token

                    if (nextToken == IDENT || nextToken == INT_LIT || nextToken == FLOAT_LIT ||
                            nextToken == STRING_LIT) {

                        if (nextToken == IDENT) {
                            if (variableArray.indexOf(variableName) != -1) {
                                key = variableArray.indexOf(variableName);
                                variableType = typeArray.get(key);

                                writeFile.print(lexemeArray.get(index));
                                nextToken();
                                if (nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
                                    System.out.println("operation symbol");
                                    writeFile.print(lexemeArray.get(index));
                                    index++;
                                    parseIfStatement();  // recursive parseIfStatement
                                } else if (nextToken == RIGHT_PAREN) {
                                    writeFile.print(lexemeArray.get(index));
                                    nextToken();
                                    if (nextToken == COLON_SYM) {
                                        writeFile.print("{");
                                        nextToken();
                                        if (nextToken == EOL) {
                                            writeFile.print("\r\n");
                                            line++;
                                            index++;
                                            parseBlock();  // recursive parseBlock
                                        } else {
                                            String message = "Only one declaraction per line";
                                            error(message);
                                            parseBlock();
                                        }
                                    } else {
                                        String message = "Missing colon";
                                        error(message);
                                        parseBlock();
                                    }
                                } else {
                                    String message = "Incorrect expression";
                                    error(message);
                                    parseBlock();
                                }
                            } else {
                                String message = "Variable may not have been initialized";
                                error(message);
                                parseBlock();
                            }
                        } else {
                            writeFile.print(lexemeArray.get(index));
                            nextToken();
                            System.out.print(nextToken);
                            if (nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
                                writeFile.print(lexemeArray.get(index));
                                index++;
                                parseIfStatement();  // recursive parseIfStatement
                            } else if (nextToken == RIGHT_PAREN) {
                                writeFile.print(lexemeArray.get(index));
                                nextToken();
                                if (nextToken == COLON_SYM) {
                                    writeFile.print("{");
                                    nextToken();
                                    if (nextToken == EOL) {
                                        writeFile.print("\r\n");
                                        line++;
                                        index++;
                                        parseBlock();  // recursive parseBlock call
                                    } else {
                                        String message = "Only one declaraction per line";
                                        error(message);
                                        parseBlock();
                                    }
                                } else {
                                    String message = "Missing colon";
                                    error(message);
                                    parseBlock();
                                }
                            } else {
                                String message = "Incorrect expression";
                                error(message);
                                parseBlock();
                            }
                        }
                    } else {
                        String message = "Incorrect expression";
                        error(message);
                        parseBlock();
                    }
                } else {
                    String message = "Incorrect relational operator";
                    error(message);
                    parseBlock();  // even with errors, continue to parse
                }
            }
        } else {
            String message = "Missing left parentheses";
            error(message);
            parseBlock();
        }
    }
}