public class SpartieInterpreter {
    Object run(Expression expression) {
        Object result = interpret(expression);
        return result;
    }

    Object interpret(Expression expression) {
        // Depending on the expression type from the parser, we attempt to interpret the expression
        return switch (expression) {
            case Expression.LiteralExpression literalExpression -> interpretLiteral(literalExpression);
            case Expression.ParenthesesExpression parenthesesExpression -> interpretParenthesis(parenthesesExpression);
            case Expression.UnaryExpression unaryExpression -> interpretUnary(unaryExpression);
            case Expression.BinaryExpression binaryExpression -> interpretBinary(binaryExpression);
            case null, default -> null;
        };
    }

    private Object interpretLiteral(Expression.LiteralExpression expression) {
        // This is fairly simple, just return the actual literal value. For example "some string" or 3.0
        return expression.literalValue;
    }

    private Object interpretParenthesis(Expression.ParenthesesExpression expression) {
        // Take what is inside the parenthesis and send it back to our interpreter
        return this.interpret(expression.expression);
    }

    private Object interpretUnary(Expression.UnaryExpression expression) {
        Object right = interpret(expression.right);

        switch (expression.operator.type) {
            case NOT:
                if(!(right instanceof Boolean)) {
                    error("Invalid type on line " + expression.operator.line + " : " + expression.operator.text + right);
                }
                if (isTrue(right)){
                    return false;
                }
                else{
                return true ;
                }
            case SUBTRACT:
                validateOperand(expression.operator, right);
                return - (double) right;
        }

        return null;
    }

    private Object interpretBinary(Expression.BinaryExpression expression) {
        Object left = interpret(expression.left);
        Object right = interpret(expression.right);
     
        if (expression.operator.type == TokenType.ADD) {
            if(left instanceof String strLeft) {
                String toConcat = "";
                if(right instanceof Double dRight) {
                    toConcat = (Math.round(dRight * 100) / 100.0) + "";
                } else if(right instanceof String strRight) {
                    toConcat = strRight;
                }
                return strLeft + toConcat;
            } else if (left instanceof Double dLeft) {
                if(right instanceof Double dRight) {
                    return dLeft + dRight;
                } else if (right instanceof String strRight) {
                    String toConcat = (Math.round(dLeft * 100) / 100.0) + "";
                    return toConcat + strRight;
                }
            }
        }

        switch(expression.operator.type) {
            case EQUIVALENT:
                return left.equals(right);
            case NOT_EQUAL:
                return !left.equals(right) ;
        }

        // At this point, we can validate if our operands are doubles because they cannot be Strings for the other
        // operation
        validateOperands(expression.operator, left, right);

        // TODO: Handle binary operator for operands. Keep in mind, at this point, we know they are doubles, but you
        // TODO: still need to cast them to doubles. Use the primitive type, e.g. (double)left
        // TODO: we do not support >, >=, <, or <= on Strings

        if (!(left instanceof Double && right instanceof Double)){
            return null;
        }

        switch(expression.operator.type) {
            case SUBTRACT:
                return (double) left - (double) right;
            case MULTIPLY:
                return (double) left * (double) right;
            case DIVIDE:
                if ((double) right == 0) {
                    error("Division by zero on line " + expression.operator.line);
                }
                return (double) left / (double) right;
            case GREATER_THAN:
                return (double) left > (double) right;
            case GREATER_EQUAL:
                return (double) left >= (double) right;
            case LESS_THAN:
                return (double) left < (double) right;
            case LESS_EQUAL:
                return (double) left <= (double) right;
        }

        return null;
    }

    // Helper Methods

    // TODO: Complete implementation of testing for equivalency
    private boolean isEquivalent(Object left, Object right) {
        // They are equal under the following conditions:
        // 1. They are both null
        // 2. Their values are the same
        if (left == null && right == null) {
            return true;
        }
        if (left.getClass() == Expression.LiteralExpression.class && right.getClass() == Expression.LiteralExpression.class) {
            if (((Expression.LiteralExpression) left).literalValue.equals(((Expression.LiteralExpression) right).literalValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTrue(Object object) {
        // We should return false if an object is null
        // If an object is of type boolean, we should return the primitive equivalent of that value
        if (object == null){
        return false;
        }
        if (object instanceof Boolean){
            return (boolean) object;
        }
        return true;
        // finish

    }

    // Validate the type
    private void validateOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        error("Invalid type on line " + operator.line + " : " + operator.text + operand);
    }

    private void validateOperands(Token operator, Object operand1, Object operand2) {
        if (operand1 instanceof Double && operand2 instanceof Double) return;
        error("Invalid type on line " + operator.line + " : " + operand1 + operator.text + operand2);
    }


    private void error(String message) {
        System.err.println(message);
        System.exit(2);
    }
}
