package ch.heig.dai.lab.protocoldesign;

import java.util.Stack;

public class ComputeExpression {

    final private String allowedOperators;
    final private String expression;

    /**Constructor to ComputeExpression class
     *
     * @param expression The desired expression to process
     * @param allowedOperators All allowed operations
     */
    public ComputeExpression(String expression, String allowedOperators) throws MalformedExpression {
        // Validate input
        if (expression == null || expression.isEmpty()) {
            throw new MalformedExpression("expression cannot be null or empty");
        }
        verifyExpression(expression);

        this.expression = expression;
        this.allowedOperators = allowedOperators + '(';
    }

    /** This function extracts full numbers from expression at given index. E.g. extracts 123 from (123 + 12).
     *
     * @param expression Expression from which to extract number
     * @param index Index at which the extraction should begin
     * @return Extracted number as a string
     */
    private String extractNumber(String expression, int index) {

        int startingIndex = index;

        while(Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.'){
            ++index;
        }

        return expression.substring(startingIndex, index);
    }

    private void handleUnaryOperator(Stack<Double> valStack, Stack<Character> opStack) {

        while (!opStack.empty() && opStack.peek() == '!') {
            double tmp = valStack.pop() * -1;
            valStack.push(tmp);
            opStack.pop();
        }
    }

    /** This function computes the result of mathematical expression passed as argument
     *
     * @return The result as a String
     *
     * @throws ArithmeticException if :
     *         - Expression contains division by 0
     * @throws MalformedExpression if :
     *         - Parenthesis count is odd
     * @throws IllegalCharacter if :
     *         - Unrecognized character in expression
     *
     */
    public String compute() {
        // Let's initialize a value stack and an operand stack
        final Stack<Double> valStack = new Stack<>();
        final Stack<Character> opStack = new Stack<>();

        for (int i = 0; i < this.expression.length(); ++i) {
            // Is it an operator?
            if (allowedOperators.indexOf(this.expression.charAt(i)) >= 0) {
                // If so, is it a unary operator?
                if ((this.expression.charAt(i) == '-' || this.expression.charAt(i) == '+') &&                 // If the character matches either - or +
                    (i == 0 ||                                                                                // AND that we're either the first character of the expression
                        (opStack.empty() && allowedOperators.indexOf(this.expression.charAt(i - 1)) >= 0))) { // OR that we have no operands to process yet AND we're preceded by a binary operator or opening parenthesis
                    // Then ensure correct measures are taken
                    if (this.expression.charAt(i) == '-') {
                        opStack.push('!');
                    }
                }
                // Binary operators
                else {
                    opStack.push(this.expression.charAt(i));
                }
            }

            // Numbers
            else if (Character.isDigit(this.expression.charAt(i))) {
                String value = extractNumber(this.expression, i);
                i += value.length() - 1;
                valStack.push(Double.valueOf(value));

                handleUnaryOperator(valStack, opStack);
            }

            // Expression evaluation
            else if (this.expression.charAt(i) == ')') {

                //Manage single value cases e.g. (-2)
                if (valStack.size() == 1) {

                    opStack.pop();
                    break;
                }

                double v2 = valStack.pop();
                double v1 = valStack.pop();
                char op = opStack.pop();

                switch(op) {
                    case '+' : {
                        valStack.push(v1 + v2);
                        break;
                    }
                    case '-' : {
                        valStack.push(v1 - v2);
                        break;
                    }
                    case '*' : {
                        valStack.push(v1 * v2);
                        break;
                    }
                    case '/' : {
                        if (v2 == 0) {
                            throw new ArithmeticException("Division by 0");
                        }
                        valStack.push(v1 / v2);
                        break;
                    }
                }

                //Check if unary operator is before expression as in -(1+2)
                handleUnaryOperator(valStack, opStack);

                if (opStack.peek() == '(') {
                    opStack.pop();
                } else {
                    throw new MalformedExpression("Parenthesis count not right");
                }

            } else {
                throw new IllegalCharacter("Illegal character : " + this.expression.charAt(i));
            }
        }

        if (!opStack.empty() || valStack.size() != 1) throw new MalformedExpression("Bad expression");

        return String.valueOf(valStack.pop());
    }

    private void verifyExpression(String expression) throws MalformedExpression {
        // Method to verify that we yield the same amount of opening parenthesis as closing ones
        if (!expression.startsWith("(") || !expression.endsWith(")")) {
            throw new MalformedExpression("Expression must be entirely enclosed in parentheses.");
        }
        int numberOfParenthesis = 0;

        //Check for invalid number of parenthesis
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '(') {
                ++numberOfParenthesis;
            }
            else if (c == ')') {
                --numberOfParenthesis;
            } else if (Character.isAlphabetic(c)) {
                throw new MalformedExpression("Unknown characters in expression");
            }
        }
        // If the expression has non-matching number of opposing parenthesis
        if (numberOfParenthesis != 0) {
            throw new MalformedExpression("Bad expression");
        }
    }

    @Override
    public String toString() {
        return "Registered expression: " + this.expression;
    }
}
