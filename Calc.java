import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.IOException;

class Helper {

    public static double RadtoDeg(double radians) {
        // Convert radians to degrees
        return radians * (180.0 / Math.PI);
    }

    public static double DegtoRad(double degrees) {
        // Convert degrees to radians
        return degrees * (Math.PI / 180.0);
    }
}

class Token {
    String type;
    String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
}

class Lexer {
    private String input;
    private int position = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public Lexer(Lexer other) {
        this.input = other.getInput();
        this.position = other.getPosition();
    }

    public String getInput() {
        return input;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Token getNextToken() {
        while (position < input.length()) {
            char currentChar = input.charAt(position);
            if (Character.isDigit(currentChar) || (currentChar == '.' && position + 1 < input.length()
                    && Character.isDigit(input.charAt(position + 1)))) {
                // Recognize a number
                StringBuilder number = new StringBuilder();
                while (position < input.length()
                        && (Character.isDigit(input.charAt(position)) || (input.charAt(position) == '.'
                                && position + 1 < input.length() && Character.isDigit(input.charAt(position + 1))))) {
                    number.append(input.charAt(position));
                    position++;
                }
                return new Token("NUMBER", number.toString());
            } else if (currentChar == '^') {
                position++;
                return new Token("POWER", "^");
            } else if (currentChar == '+') {
                position++;
                return new Token("PLUS", "+");
            } else if (currentChar == '-') {
                position++;
                return new Token("MINUS", "-");
            } else if (currentChar == '*') {
                position++;
                return new Token("MULTIPLY", "*");
            } else if (currentChar == '/') {
                position++;
                return new Token("DIVIDE", "/");
            } else if (currentChar == '%') {
                position++;
                return new Token("MODULUS", "%");
            } else if (currentChar == '(') {
                position++;
                return new Token("L_Paren", "(");
            } else if (currentChar == ')') {
                position++;
                return new Token("R_Paren", ")");
            }

            else if (currentChar == 'c' && (position + 4) < input.length()
                    && input.substring(position, position + 5).equalsIgnoreCase("cosec")) {
                position += 5;
                return new Token("COSEC", "cosec");
            } else if (currentChar == 's' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("sec")) {
                position += 3;
                return new Token("SEC", "sec");
            } else if (currentChar == 'c' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("cot")) {
                position += 3;
                return new Token("COT", "cot");
            }

            else if (currentChar == 's' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("sin")) {
                position += 3;
                return new Token("SIN", "sin");
            } else if (currentChar == 'c' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("cos")) {
                position += 3;
                return new Token("COS", "cos");
            } else if (currentChar == 't' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("tan")) {
                position += 3;
                return new Token("TAN", "tan");
            } else if (currentChar == 's' && (position + 3) < input.length()
                    && input.substring(position, position + 4).equalsIgnoreCase("sqrt")) {
                position += 4;
                return new Token("SQRT", "sqrt");
            } else if (currentChar == 'a' && (position + 3) < input.length()) {
                if (input.substring(position, position + 4).equalsIgnoreCase("asin")) {
                    position += 4;
                    return new Token("ASIN", "asin");

                } else if (input.substring(position, position + 4).equalsIgnoreCase("acos")) {
                    position += 4;
                    return new Token("ACOS", "acos");

                } else if (input.substring(position, position + 4).equalsIgnoreCase("atan")) {
                    position += 4;
                    return new Token("ATAN", "atan");
                }
            }

            else if (currentChar == 'l' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("log")) {
                position += 3;
                return new Token("LOG", "log");
            } else if (currentChar == ',') {
                position++;
                return new Token("COMMA", ",");
            } else if (currentChar == 'p' && (position + 2) < input.length()
                    && input.substring(position, position + 3).equalsIgnoreCase("pow")) {
                position += 3;
                return new Token("POW", "pow");

            } else if (Character.isLetter(currentChar)) {
                StringBuilder identifier = new StringBuilder();
                while (position < input.length()
                        && (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                    identifier.append(input.charAt(position));
                    position++;
                }

                return new Token("IDENTIFIER", identifier.toString());
            } else if (currentChar == ' ' || currentChar == '\t') {
                position++;
            } else if (currentChar == '\n') {
                position++;
                return new Token("$", "$");
            } else {
                position++;
                throw new RuntimeException("unknown token");
            }
        }
        return null;
    }
}

class Parser {
    private Lexer lexer;
    private Token currentToken;

    public Parser(String sourceCode) {
        this.lexer = new Lexer(sourceCode);
        this.currentToken = lexer.getNextToken();
    }

    public void parse() {
        double result = parseExpression();
        System.out.println("Result: " + result);
    }

    private void eat(String tokenType) {

        if (currentToken != null && currentToken.type.equals(tokenType)) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException("Syntax error: Expected " + tokenType + " but got " + currentToken.type);
        }
    }

    private double parseExpression() {
        double result = parseTerm();
        while (currentToken != null && (currentToken.type.equals("PLUS") || currentToken.type.equals("MINUS"))) {
            String operator = currentToken.value;
            eat(currentToken.type);
            double termValue = parseTerm();

            if (operator.equals("+")) {
                result += termValue;
            } else {
                result -= termValue;
            }
        }
        return result;
    }

    private double parseTerm() {
        double result = parseFactor();
        while (currentToken != null && (currentToken.type.equals("MULTIPLY") || currentToken.type.equals("DIVIDE"))) {
            String operator = currentToken.value;
            eat(currentToken.type);
            double factorValue = parseFactor();

            if (operator.equals("*")) {
                result *= factorValue;
            } else {
                if (factorValue == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result /= factorValue;
            }
        }
        return result;
    }

    private double parseFactor() {
        if (currentToken == null) {
            throw new RuntimeException("Syntax error: Unexpected end of input");
        }

        if (currentToken.type.equals("NUMBER")) {
            double value = Double.parseDouble(currentToken.value);
            eat("NUMBER");
            if (currentToken != null && currentToken.type.equals("POWER")) {
                eat("POWER");
                if (currentToken != null && currentToken.type.equals("NUMBER")) {
                    double value2 = Double.parseDouble(currentToken.value);
                    eat("NUMBER");
                    return Math.pow(value, value2);
                } else if (currentToken != null && currentToken.type.equals("L_Paren")) {
                    eat("L_Paren");
                    double argument = parseExpression();
                    eat("R_Paren");
                    return Math.pow(value, argument);

                } else {
                    throw new RuntimeException(
                            "Syntax error: Expected " + "Expression or value" + " but got " + currentToken.type);
                }
            }
            return value;
        } else if (currentToken.type.equals("ASIN")) {
            eat("ASIN");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Helper.RadtoDeg(Math.asin(argument));
        } else if (currentToken.type.equals("ACOS")) {
            eat("ACOS");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Helper.RadtoDeg(Math.acos(argument));
        } else if (currentToken.type.equals("ATAN")) {
            eat("ATAN");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Helper.RadtoDeg(Math.atan(argument));
        } else if (currentToken.type.equals("SQRT")) {
            eat("SQRT");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Math.sqrt(argument);
        } else if (currentToken.type.equals("COSEC")) {
            eat("COSEC");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return 1 / Math.sin(Helper.DegtoRad(argument));
        } else if (currentToken.type.equals("SEC")) {
            eat("SEC");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return 1 / Math.cos(Helper.DegtoRad(argument));
        } else if (currentToken.type.equals("COT")) {
            eat("COT");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return 1 / Math.tan(Helper.DegtoRad(argument));
        } else if (currentToken.type.equals("SIN")) {
            eat("SIN");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Math.sin(Helper.DegtoRad(argument));
        } else if (currentToken.type.equals("COS")) {
            eat("COS");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Math.cos(Helper.DegtoRad(argument));
        } else if (currentToken.type.equals("TAN")) {
            eat("TAN");
            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Math.tan(Helper.DegtoRad(argument));
        } else if (currentToken.type.equals("L_Paren")) {
            eat("L_Paren");
            double result = parseExpression();
            eat("R_Paren");
            return result;
        } else if (currentToken.type.equals("MINUS")) {
            eat("MINUS");
            return -parseFactor();
        } else if (currentToken.type.equals("LOG")) {
            eat("LOG");

            if (currentToken != null && currentToken.type.equals("NUMBER")) {
                double base = Double.parseDouble(currentToken.value);
                eat("NUMBER");
                eat("L_Paren");
                double argument = parseExpression();
                eat("R_Paren");
                return Math.log10(argument) / Math.log10(base);
            }

            eat("L_Paren");
            double argument = parseExpression();
            eat("R_Paren");
            return Math.log(argument);
        } else if (currentToken.type.equals("POW")) {
            eat("POW");
            eat("L_Paren");
            double base = parseExpression();
            eat("COMMA");
            double exponent = parseExpression();
            eat("R_Paren");
            return Math.pow(base, exponent);
        } else {
            throw new RuntimeException("Syntax error: Unexpected token " + currentToken.type);
        }
    }
}

public class Calc {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">> ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the calculator.");
                break;
            }
            try {
                evaluateExpression(input);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        scanner.close();

    }

    public static void evaluateExpression(String sourceCode) {
        Parser parser = new Parser(sourceCode);
        parser.parse();
    }
}