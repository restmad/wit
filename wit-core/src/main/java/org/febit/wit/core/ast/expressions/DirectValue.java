// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;

/**
 *
 * @author zqq90
 */
public final class DirectValue extends Expression implements Constable {

    public final Object value;

    public DirectValue(Object value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    public Object execute(final InternalContext context) {
        return value;
    }

    @Override
    public Object getConstValue() {
        return value;
    }
}
