package com.edavalos.acacia;

import java.util.*;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    private String tempStr = null;
    private AcaciaSet tempSet = null;

    // When the interpreter is fired up, add all the built in functions to the environment
    Interpreter() {
        // Native functions
        for (AcaciaCallable nativeFunction : Natives.functions) {
            globals.hardDefine(nativeFunction.name(), nativeFunction);
        }
        // String methods
        for (AcaciaCallable nativeFunction : Natives.stringMethods) {
            globals.hardDefine(nativeFunction.name(), nativeFunction);
        }
    }

    // Main method to interpret given statements
    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Acacia.error(error);
        }
    }


    /* --- Expressions' visitor methods --- */

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            // If minus, assume both are numbers and return difference (num)
            case MINUS -> {
                validateNumbers(expr.operator, left, right);
                yield (double)left - (double)right;
            }
            // If addition, add numbers together, and concatenate strings
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left + (double)right;
                }

                if (left instanceof String || right instanceof String) {
                    yield Acacia.stringify(left).replaceAll("\"", "") +
                            Acacia.stringify(right).replaceAll("\"", "");
                }
                // If values are neither both numbers or one string, throw error
                throw new RuntimeError(expr.operator, "Operands must either all be numbers or" +
                        " at least one must be a string.");
            }
            // If division, assume both are numbers and return quotient (num)
            case SLASH ->  {
                validateNumbers(expr.operator, left, right);
                yield (double)left / (double)right;
            }
            // If multiplication, assume both are numbers and return product (num)
            case STAR ->  {
                validateNumbers(expr.operator, left, right);
                yield (double)left * (double)right;
            }
            // If modulo, assume both are numbers and return remainder (num)
            case MODULO ->  {
                validateNumbers(expr.operator, left, right);
                yield (double)left % (double)right;
            }

            // If greater/less or any variant, compare number sizes, and compare string lengths (bool for both)
            case GREATER -> {
                if (left instanceof Double && right instanceof Double) {
                    yield  (double)left > (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() > ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }
            case GREATER_EQUAL -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left >= (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() >= ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }
            case LESS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left < (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() < ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }
            case LESS_EQUAL -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left <= (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() <= ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }

            // If equality comparison, return equality value (bool)
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);

            default -> null;
        };
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof AcaciaCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        AcaciaCallable function = (AcaciaCallable)callee;
        if ((function.arity() != -1) && (arguments.size() != function.arity())) {
            throw new RuntimeError(expr.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + " (in '" + Acacia.stringify(callee) + "').");
        }

        if (Natives.setMethods.contains(function)) {
            if (tempSet == null) throw new RuntimeError(expr.paren, "Set method could not find set to preform on.");
            arguments.add(0, tempSet);
            tempSet = null;
        }

        else if (Natives.stringMethods.contains(function)) {
            if (tempStr == null)  throw new RuntimeError(expr.paren, "String method could not find string to preform on.");
            arguments.add(0, tempStr);
            tempStr = null;

        }



        return function.call(this, arguments, expr.paren);
    }

    @Override
    public Object visitEditSetExpr(Expr.EditSet expr) {
        Object value = evaluate(expr.value);

        Object var = environment.get(expr.name);
        if (!(var instanceof AcaciaSet)) {
            throw new RuntimeError(expr.name, "Failed to index. Only sets can be indexed and modified.");
        }
        AcaciaSet set = ((AcaciaSet) var);
        int ctr = 1;

        while (expr.depth.size() > 1) {
            Object idx = evaluate(expr.depth.pop());
            if ((!(idx instanceof Double)) || (((Double) idx) != Math.floor((Double) idx))) {
                throw new RuntimeError(expr.name, "Index must be a whole number.");
            }
            Object inner = set.get((int) Math.round((Double) idx));
            if (!(inner instanceof AcaciaSet)) {
                throw new RuntimeError(expr.name, "Cannot index deeper than " + ctr + ".");
            }
            set = ((AcaciaSet) inner);
            ctr++;
        }

        Object idx = evaluate(expr.depth.pop());
        if ((!(idx instanceof Double)) || (((Double) idx) != Math.floor((Double) idx))) {
            throw new RuntimeError(expr.name, "Index must be a whole number.");
        }
        set.put((int) Math.round((Double) idx), value);

        return value;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);

        if (object instanceof AcaciaSet) {
            tempSet = ((AcaciaSet) object);
            return ((AcaciaSet) object).findMethod(expr.name);
        }

        if (object instanceof AcaciaInstance) {
            return ((AcaciaInstance) object).get(expr.name);
        }

        if (object instanceof String) {
            Object method = globals.get(expr.name);
            if (!(method instanceof AcaciaCallable)) {
                throw new RuntimeError(expr.name, "Undefined string method '" + expr.name.lexeme + "'.");
            }
            tempStr = ((String) object);
            return method;
        }

        throw new RuntimeError(expr.name, "Only instances have properties.");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitIncrementExpr(Expr.Increment expr) {
        Object currentValue = lookUpVariable(expr.var, expr);
        if (!(currentValue instanceof Double)) {
            throw new RuntimeError(expr.type, "Invalid increment target.");
        }

        Double newValue = switch (expr.type.type) {
            case DOUBLE_PLUS -> ((Double) currentValue) + 1.0;
            case DOUBLE_MINUS -> ((Double) currentValue) - 1.0;
            case TRIPLE_PLUS -> ((Double) currentValue) * 2.0;
            case TRIPLE_MINUS -> ((Double) currentValue) / 2.0;
            default -> null;
        };

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.var, newValue);
        } else {
            globals.assign(expr.var, newValue);
        }

        return newValue;
    }

    @Override
    public Object visitIncSetExpr(Expr.IncSet expr) {
        Object var = environment.get(expr.name);
        if (!(var instanceof AcaciaSet)) {
            throw new RuntimeError(expr.name, "Failed to index. Only sets can be indexed and modified.");
        }
        AcaciaSet set = ((AcaciaSet) var);
        int ctr = 1;

        while (expr.depth.size() > 1) {
            Object idx = evaluate(expr.depth.pop());
            if ((!(idx instanceof Double)) || (((Double) idx) != Math.floor((Double) idx))) {
                throw new RuntimeError(expr.name, "Index must be a whole number.");
            }
            Object inner = set.get((int) Math.round((Double) idx));
            if (!(inner instanceof AcaciaSet)) {
                throw new RuntimeError(expr.name, "Cannot index deeper than " + ctr + ".");
            }
            set = ((AcaciaSet) inner);
            ctr++;
        }

        Object idx = evaluate(expr.depth.pop());
        if ((!(idx instanceof Double)) || (((Double) idx) != Math.floor((Double) idx))) {
            throw new RuntimeError(expr.name, "Index must be a whole number.");
        }

        return set.inc((int) Math.round((Double) idx), expr.type);
    }

    @Override
    public Object visitIndexExpr(Expr.Index expr) {
        Object idx = evaluate(expr.location);
        if ((!(idx instanceof Double)) || (((Double) idx) != Math.floor((Double) idx))) {
            throw new RuntimeError(expr.bracket, "Index must be a whole number.");
        }
        int index = (int) Math.round(((Double) idx));

        Object set = evaluate(expr.set);

        if (set instanceof AcaciaSet) {
            return ((AcaciaSet) set).get(index);
        }

        else if (set instanceof String) {
            int length = ((String) set).length();

            if (index >= 0) return ((String) set).charAt(index % length) + "";
            else return ((String) set).charAt(index + length) + "";
        }

        else {
            throw new RuntimeError(expr.bracket, "Failed to index. Only sets and strings can be indexed.");
        }

    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        List<Object> contents = new ArrayList<>();
        for (Expr e : expr.values) {
            contents.add(evaluate(e));
        }
        return new AcaciaSet(contents);
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        AcaciaClass superclass = (AcaciaClass) environment.getAt(distance, "super");
        AcaciaInstance object = (AcaciaInstance) environment.getAt(distance - 1, "this");
        AcaciaFunction method = superclass.findMethod(expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "'.");
        }
        else return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitPutExpr(Expr.Put expr) {
        Object object = evaluate(expr.object);

        if (!(object instanceof AcaciaInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((AcaciaInstance) object).put(expr.name, value);
        return value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            // If unary is a minus, assume value is a number and return its negation
            case MINUS -> {
                validateNumbers(expr.operator, right);
                yield -(double) right;
            }

            // If unary is a not, assess value as bool and return its negation
            case BANG -> !isTruthy(right);

            default -> null;
        };
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }


    /* --- Statements' visitor methods --- */

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        Object value = evaluate(stmt.expression);
        if (Acacia.replMode) System.out.println(Acacia.stringify(value));
        return null;
    }

    @Override
    public Void visitForeachStmt(Stmt.Foreach stmt) {
        environment.define(stmt.iterator, null);

        boolean logIndex = false;
        if (stmt.index != null) {
            environment.define(stmt.index, 0);
            logIndex = true;
        }

        Object iterable = evaluate(stmt.iterable);
        if (!(iterable instanceof String) && !(iterable instanceof AcaciaSet)) {
            throw new RuntimeError(stmt.iterableName, "'" + stmt.iterableName.lexeme + "' is not a set " +
                    "or a string, and therefore not iterable.");
        }

        int size;
        int index = 0;
        boolean isSet;
        if (iterable instanceof String) {
            size = ((String) iterable).length();
            isSet = false;
        } else {
            size = ((AcaciaSet) iterable).cSize();
            isSet = true;
        }

        while (index < size) {
            if (isSet) {
                environment.assign(stmt.iterator, ((AcaciaSet) iterable).get(index));
            } else {
                environment.assign(stmt.iterator, ((String) iterable).charAt(index) + "");
            }

            try {
                execute(stmt.body);
            } catch (Exit x) {
                break;
            } catch (Next x) {
                continue;
            } finally {
                index++;
                if (logIndex)
                    environment.assign(stmt.index, index);
            }
        }
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        AcaciaFunction function = new AcaciaFunction(stmt, environment, false);
        environment.define(stmt.name, function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        }
        else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitNextStmt(Stmt.Next stmt) {
        throw new Next();
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(Acacia.stringify(value).replaceAll("\\\\n", "\n"));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (Exit x) {
                break;
            } catch (Next x) {
                continue;
            } finally {
                evaluate(stmt.increment);
            }
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof AcaciaClass)) {
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
        }

        environment.define(stmt.name, null);

        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.hardDefine("super", superclass);
        }

        Map<String, AcaciaFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            AcaciaFunction function = new AcaciaFunction(method, environment,
                                                         method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }

        AcaciaClass klass = new AcaciaClass(stmt.name.lexeme, (AcaciaClass) superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitExitStmt(Stmt.Exit stmt) {
        throw new Exit();
    }


    /* --- Utility methods --- */

    // Sends a given expression back into the interpreter’s visitor method
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    // Sends a given statement back into the interpreter's visitor method
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    // Catches and stores the number of environments deep an expression is
    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    // Looks for a variable in its respective scope
    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    // Loops thru a list of statements in a block and executes them, also handles variable scoping and returns (breaks)
    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    // Determines whether a given value is considered true
    private boolean isTruthy(Object object) {
        // Anything nil is false
        if (object == null) return false;

        // Any boolean is just itself
        if (object instanceof Boolean) return (boolean)object;

        // Any number is false only if its value is zero
        if (object instanceof Double) return ((double)object != 0.0);

        // Anything else is true
        return true;
    }

    // Determined whether two values are equal
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    // Ensures given objects are numbers
    private void validateNumbers(Token operator, Object... objects) {
        for (Object object : objects) {
            // If any object of the ones given isn't a number, throw an error
            if (!(object instanceof Double)) {
                throw new RuntimeError(operator, "Operand must be a number.");
            }
        }
    }
}
