// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.core.VariantIndexer;
import org.febit.wit.core.ast.expressions.FunctionDeclare;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.UnConstableMethodDeclare;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class FunctionMethodDeclare implements MethodDeclare, UnConstableMethodDeclare {

    private final FunctionDeclare function;
    private final InternalContext scopeContext;
    private final VariantIndexer[] indexers;
    private final int varSize;

    public FunctionMethodDeclare(FunctionDeclare function, InternalContext scopeContext, VariantIndexer[] indexers, int varSize) {
        this.function = function;
        this.scopeContext = scopeContext;
        this.indexers = indexers;
        this.varSize = varSize;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        try {
            return function.invoke(this.scopeContext.createSubContext(this.indexers, context, this.varSize), args);
        } catch (Exception e) {
            ScriptRuntimeException runtimeException = StatementUtil.castToScriptRuntimeException(e, function);
            if (context != this.scopeContext) {
                runtimeException.setTemplate(this.scopeContext.template);
            }
            throw runtimeException;
        }
    }
}
