// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;


import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.UnConstableMethodDeclare;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MethodExecute extends Expression implements Constable {

    private final Expression funcExpr;
    private final Expression[] paramExprs;

    public MethodExecute(Expression funcExpr, Expression[] paramExprs, int line, int column) {
        super(line, column);
        this.funcExpr = funcExpr;
        this.paramExprs = paramExprs;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Object func = funcExpr.execute(context);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        final Expression[] exprs = this.paramExprs;
        final int len = exprs.length;
        final Object[] results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = exprs[i].execute(context);
        }
        return ((MethodDeclare) func).invoke(context, results);
    }

    @Override
    public Object getConstValue() {

        final Object func = StatementUtil.calcConst(funcExpr, true);
        if (!(func instanceof MethodDeclare)) {
            throw new ScriptRuntimeException("not a function", this);
        }
        if (func instanceof UnConstableMethodDeclare) {
            return InternalVoid.VOID;
        }

        final Object[] params = StatementUtil.calcConstArrayForce(paramExprs);

        return ((MethodDeclare) func).invoke(null, params);
    }
}
