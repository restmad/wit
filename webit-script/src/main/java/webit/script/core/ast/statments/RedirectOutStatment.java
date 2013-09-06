// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import jodd.io.FastByteArrayOutputStream;
import jodd.io.FastCharArrayWriter;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.ResetableValue;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.core.ast.Statment;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class RedirectOutStatment extends AbstractStatment {

    private final Statment srcStatment;
    private final ResetableValueExpression toExpr;

    public RedirectOutStatment(Statment srcStatment, ResetableValueExpression toExpr, int line, int column) {
        super(line, column);
        this.srcStatment = srcStatment;
        this.toExpr = toExpr;
    }

    public void execute(Context context) {

        final Out current = context.getOut();
        if (current instanceof OutputStreamOut) {

            FastByteArrayOutputStream out = new FastByteArrayOutputStream(128);

            StatmentUtil.execute(srcStatment, new OutputStreamOut(out, (OutputStreamOut) current), context);
            ResetableValue value = StatmentUtil.getResetableValue(toExpr, context);
            value.set(out.toByteArray());
        } else if (current instanceof WriterOut) {

            FastCharArrayWriter writer = new FastCharArrayWriter();

            StatmentUtil.execute(srcStatment, new WriterOut(writer, (WriterOut) current), context);
            ResetableValue value = StatmentUtil.getResetableValue(toExpr, context);
            value.set(writer.toString());
        } else {

            FastCharArrayWriter writer = new FastCharArrayWriter();

            StatmentUtil.execute(srcStatment, new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory()), context);
            ResetableValue value = StatmentUtil.getResetableValue(toExpr, context);
            value.set(writer.toString());
        }
    }
}
