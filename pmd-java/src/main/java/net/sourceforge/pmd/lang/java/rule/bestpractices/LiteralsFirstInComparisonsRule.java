/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

public class LiteralsFirstInComparisonsRule extends AbstractJavaRulechainRule {

    private static final Set<String> STRING_COMPARISONS =
        setOf("equalsIgnoreCase",
              "compareTo",
              "compareToIgnoreCase",
              "contentEquals");

    public LiteralsFirstInComparisonsRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if ("equals".equals(call.getMethodName())
            && call.getArguments().size() == 1
            && isEqualsObjectAndNotAnOverload(call)) {
            checkArgs((RuleContext) data, call);
        } else if (STRING_COMPARISONS.contains(call.getMethodName())
            && call.getArguments().size() == 1
            && TypeTestUtil.isDeclaredInClass(String.class, call.getMethodType())) {
            checkArgs((RuleContext) data, call);
        }
        return data;
    }

    private boolean isEqualsObjectAndNotAnOverload(ASTMethodCall call) {
        return call.getOverloadSelectionInfo().isFailed() // failed selection is considered probably equals(Object)
                || call.getMethodType().getFormalParameters().equals(listOf(call.getTypeSystem().OBJECT));
    }

    private boolean isConstantString(@Nullable ASTExpression node) {
        return node instanceof ASTStringLiteral
            || node != null && node.getConstValue() instanceof String;
    }

    private void checkArgs(RuleContext ctx, ASTMethodCall call) {
        ASTExpression arg = call.getArguments().get(0);
        @Nullable ASTExpression qualifier = call.getQualifier();
        if (qualifier != null && !isConstantString(qualifier) && isConstantString(arg)) {
            ctx.addViolation(call);
        }
    }
}
