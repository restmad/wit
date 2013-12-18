// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Plus extends BinaryOperator implements Optimizable {

    public Plus(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    public Object execute(final Context context) {
        return ALU.plus(StatementUtil.execute(leftExpr, context), StatementUtil.execute(rightExpr, context));
    }

    public Expression optimize() {
        return (leftExpr instanceof DirectValue && rightExpr instanceof DirectValue)
                ? new DirectValue(ALU.plus(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value), line, column)
                : this;
    }
}