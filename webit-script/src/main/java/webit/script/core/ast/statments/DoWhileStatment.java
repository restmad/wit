// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class DoWhileStatment extends AbstractStatment implements Loopable {

    private final Expression whileExpr;
    private final VariantMap varMap;
    private final Statment[] statments;
    public final LoopInfo[] possibleLoopsInfo;
    private final String label;

    public DoWhileStatment(Expression whileExpr, VariantMap varMap, Statment[] statments, LoopInfo[] possibleLoopsInfo, String label, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.varMap = varMap;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.label = label;
    }

    public Object execute(final Context context) {
        final LoopCtrl ctrl = context.loopCtrl;
        final Statment[] statments = this.statments;
        final VariantStack vars;
        (vars = context.vars).push(varMap);
        label:
        do {
            StatmentUtil.executeAndCheckLoops(statments, context);
            if (!ctrl.goon()) {
                if (ctrl.matchLabel(label)) {
                    switch (ctrl.getLoopType()) {
                        case BREAK:
                            ctrl.reset();
                            break label; // while
                        case RETURN:
                            //can't deal
                            break label; //while
                        case CONTINUE:
                            ctrl.reset();
                            break; //switch
                        default:
                            break label; //while
                        }
                } else {
                    break;
                }
            }
            vars.resetCurrent();
        } while (ALU.toBoolean(StatmentUtil.execute(whileExpr, context)));
        vars.pop();
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}