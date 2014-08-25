// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.VariantStack;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.lang.KeyIter;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForMap extends AbstractStatement implements Loopable {

    private final Expression mapExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    public final LoopInfo[] possibleLoopsInfo;
    private final Statement elseStatement;
    private final int label;

    public ForMap(Expression mapExpr, VariantIndexer varIndexer, Statement[] statements, LoopInfo[] possibleLoopsInfo, Statement elseStatement, int label, int line, int column) {
        super(line, column);
        this.mapExpr = mapExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatement = elseStatement;
        this.label = label;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final KeyIter iter = CollectionUtil.toKeyIter(StatementUtil.execute(mapExpr, context));
        if (iter != null && iter.hasNext()) {
            final LoopCtrl ctrl = context.loopCtrl;
            final VariantStack vars;
            (vars = context.vars).push(varIndexer);
            vars.set(0, iter);
            label:
            do {
                vars.resetForForMap(iter.next(), iter.value());
                StatementUtil.executeInvertedAndCheckLoops(this.statements, context);
                if (ctrl.getLoopType() != LoopInfo.NO_LOOP) {
                    if (ctrl.matchLabel(label)) {
                        switch (ctrl.getLoopType()) {
                            case LoopInfo.BREAK:
                                ctrl.reset();
                                break label; // while
                            case LoopInfo.RETURN:
                                //can't deal
                                break label; //while
                            case LoopInfo.CONTINUE:
                                ctrl.reset();
                                break; //switch
                            default:
                                break label; //while
                            }
                    } else {
                        break; //while
                    }
                }
            } while (iter.hasNext());
            vars.pop();
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
