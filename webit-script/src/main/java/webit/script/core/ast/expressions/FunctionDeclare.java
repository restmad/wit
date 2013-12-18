// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.method.FunctionMethodDeclare;
import webit.script.core.runtime.VariantContext;
import webit.script.core.runtime.VariantStack;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclare extends AbstractExpression {

    private final int argsIndex;
    private final int[] argIndexs;
    public final int[] _overflowUpstairs;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final boolean hasReturnLoops;

    public FunctionDeclare(int argsIndex, int[] argIndexs, int[] overflowUpstairs, VariantIndexer varIndexer, Statement[] statements, boolean hasReturnLoops, int line, int column) {
        super(line, column);
        this.argIndexs = argIndexs;
        this.argsIndex = argsIndex;
        this._overflowUpstairs = overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.hasReturnLoops = hasReturnLoops;
    }

    public Object execute(final Context context) {
        final VariantContext[] variantContexts;
        final int[] overflowUpstairs;
        if ((overflowUpstairs = this._overflowUpstairs) != null) {
            final int len;
            final int max;
            variantContexts = new VariantContext[(max = overflowUpstairs[(len = overflowUpstairs.length) - 1]) + 1];
            for (int i = 0, j; i < len; i++) {
                variantContexts[max - (j = overflowUpstairs[i])] = context.vars.getContext(j);
            }
        } else {
            variantContexts = null;
        }
        return new FunctionMethodDeclare(this, context.template, variantContexts);
    }

    public Object invoke(final Context context, final Object[] args) {
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        vars.setArgumentsForFunction(argsIndex, argIndexs, args);
        if (hasReturnLoops) {
            StatementUtil.executeInvertedAndCheckLoops(statements, context);
            vars.pop();
            return context.loopCtrl.resetReturnLoop();
        } else {
            StatementUtil.executeInverted(statements, context);
            vars.pop();
        }
        return Context.VOID;
    }
}