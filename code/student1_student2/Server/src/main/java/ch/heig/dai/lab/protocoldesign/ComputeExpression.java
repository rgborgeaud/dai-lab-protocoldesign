package ch.heig.dai.lab.protocoldesign;

import java.util.Stack;

public class ComputeExpression {

    final private double result;
    final private String allowedOperators;

    /**Constructor to ComputeExpression class
     *
     * @param expression The expression to process
     * @param allowedOperators All allowed operations
     */
    public ComputeExpression(String expression, String allowedOperators) {

        this.allowedOperators = allowedOperators + '(';
        this.result = compute(expression.replaceAll("\\s", ""));
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
     * @param expression The expression to compute
     * @return The result as a double
     *
     * @throws ArithmeticException if :
     *         - Expression contains division by 0
     * @throws MalformedExpression if :
     *         - Parenthesis count is odd
     * @throws IllegalCharacter if :
     *         - Unrecognized character in expression
     *
     */
    private double compute(String expression) {

        Stack<Double> valStack = new Stack<>();
        Stack<Character> opStack = new Stack<>();

        int numberOfParenthesis = 0;

        //Check for invalid number of parenthesis
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '(') ++numberOfParenthesis;
            else if (c == ')') --numberOfParenthesis;

            if (numberOfParenthesis < 0) throw new MalformedExpression("Bad expression");
        }

        if (numberOfParenthesis != 0) throw new MalformedExpression("Bad expression");


        for (int i = 0; i < expression.length(); ++i) {

            //Operators
            if (allowedOperators.indexOf(expression.charAt(i)) >= 0) {

                //Unary operators
                if ((expression.charAt(i) == '-' || expression.charAt(i) == '+') && (i == 0 || (opStack.empty() && expression.charAt(i - 1) == '(') ||
                        (allowedOperators.indexOf(expression.charAt(i - 1))) >= 0)) {
                    if (expression.charAt(i) == '-') {
                        opStack.push('!');
                    }
                }

                //Binary operators
                else {
                    opStack.push(expression.charAt(i));
                }
            }

            //Numbers
            else if (Character.isDigit(expression.charAt(i))) {
                String value = extractNumber(expression, i);
                i += value.length() - 1;
                valStack.push(Double.valueOf(value));

                handleUnaryOperator(valStack, opStack);
            }

            //Expression evaluation
            else if (expression.charAt(i) == ')') {

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
                        if (v2 == 0) throw new ArithmeticException("Division by 0");
                        valStack.push(v1 / v2);
                        break;
                    }
                }

                //Check if unary operator is before expression as in -(1+2)
                handleUnaryOperator(valStack, opStack);

                if (opStack.peek() == '(') opStack.pop();
                else throw new MalformedExpression("Parenthesis count not right");

            } else {

                throw new IllegalCharacter("Illegal character : " + expression.charAt(i));
            }
        }

        if (!opStack.empty() || valStack.size() != 1) throw new MalformedExpression("Bad expression");

        return valStack.pop();
    }

    @Override
    public String toString() {
        return String.valueOf(result);
    }

    public static void main(String[] args) {
        String expression = "(1+2)";
        String ops = "+-*/";

        ComputeExpression ce = new ComputeExpression(expression, ops);

        System.out.println("res : " + ce);
    }
}
