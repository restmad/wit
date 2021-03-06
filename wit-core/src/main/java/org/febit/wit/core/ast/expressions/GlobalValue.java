// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.ResetableValueExpression;
import org.febit.wit.global.GlobalManager;

/**
 *
 * @author zqq90
 */
public final class GlobalValue extends ResetableValueExpression {

    private final GlobalManager manager;
    private final int index;

    public GlobalValue(GlobalManager manager, int index, int line, int column) {
        super(line, column);
        this.index = index;
        this.manager = manager;
    }

    @Override
    public Object execute(final InternalContext context) {
        return this.manager.getGlobal(index);
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        this.manager.setGlobal(index, value);
        return value;
    }
}
