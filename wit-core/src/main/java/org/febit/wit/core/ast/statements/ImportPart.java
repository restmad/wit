// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.ArrayList;
import java.util.List;
import org.febit.wit.Template;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.ResetableValueExpression;
import org.febit.wit.exceptions.ParseException;

/**
 *
 * @author zqq90
 */
public class ImportPart {

    protected final int line;
    protected final int column;
    private Expression expr;
    private Expression paramsExpr;
    private List<String> exportNameList;
    private List<ResetableValueExpression> toResetableValueList;

    public ImportPart(Expression expr, int line, int column) {
        this(expr, null, line, column);
    }

    public ImportPart(Expression expr, Expression paramsExpr, int line, int column) {
        this.line = line;
        this.column = column;
        this.expr = expr;
        this.paramsExpr = paramsExpr;
        this.exportNameList = new ArrayList<>();
        this.toResetableValueList = new ArrayList<>();
    }

    public ImportPart append(String name, Expression to) {
        if (to instanceof ResetableValueExpression) {
            this.exportNameList.add(name);
            this.toResetableValueList.add((ResetableValueExpression) to);
            return this;
        } else {
            throw new ParseException("Need a resetable expression.", to);
        }
    }

    public Import pop(Template template) {

        final int len = exportNameList.size();
        return len == 0
                ? new Import(expr, paramsExpr, null, null, template, line, column)
                : new Import(expr, paramsExpr,
                        exportNameList.toArray(new String[len]),
                        toResetableValueList.toArray(new ResetableValueExpression[len]),
                        template, line, column);
    }
}
