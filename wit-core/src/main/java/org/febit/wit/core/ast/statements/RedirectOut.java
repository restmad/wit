// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.ResetableValueExpression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.io.Out;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.util.ByteArrayOutputStream;
import org.febit.wit.util.CharArrayWriter;

/**
 *
 * @author zqq90
 */
public class RedirectOut extends Statement {

    private final Statement srcStatement;
    private final ResetableValueExpression toExpr;

    public RedirectOut(Statement srcStatement, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatement = srcStatement;
        this.toExpr = toExpr;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Out current = context.out;
        if (current.isByteStream()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(256);
            context.out = new OutputStreamOut(out, (OutputStreamOut) current);
            srcStatement.execute(context);
            toExpr.setValue(context, out.toArray());
        } else {
            CharArrayWriter writer = new CharArrayWriter(256);
            context.out = current instanceof WriterOut
                    ? new WriterOut(writer, (WriterOut) current)
                    : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory());
            srcStatement.execute(context);
            toExpr.setValue(context, writer.toArray());
        }
        context.out = current;
        return null;
    }
}
