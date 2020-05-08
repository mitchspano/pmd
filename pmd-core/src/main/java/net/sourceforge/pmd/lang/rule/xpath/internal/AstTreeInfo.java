/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.GenericTreeInfo;


/**
 * A wrapper around the root node of an AST, implementing {@link net.sf.saxon.om.TreeInfo}.
 */
public final class AstTreeInfo extends GenericTreeInfo {

    private DeprecatedAttrLogger logger;

    /**
     * Builds an AstDocument, with the given node as the root.
     *
     * @param node          The root AST Node.
     * @param configuration Configuration of the run
     *
     * @see AstElementNode
     */
    public AstTreeInfo(RootNode node, Configuration configuration) {
        super(configuration);
        setRootNode(new AstDocumentNode(this, new IdGenerator(), node, configuration));
    }

    public AstElementNode findWrapperFor(Node node) {
        // for the RootNode, this returns the document node
        List<Integer> indices = node.ancestorsOrSelf().toList(Node::getIndexInParent);
        AstElementNode cur = getRootNode().getRootElement();

        // this is a quick but possibly expensive check
        assert cur.getUnderlyingNode() == node.getRoot() : "Node is not in this tree";

        // note we skip the first, who is the root
        for (int i = indices.size() - 2; i >= 0; i--) {
            Integer idx = indices.get(i);
            if (idx >= cur.getChildren().size()) {
                throw new IllegalArgumentException("Node is not part of this tree " + node);
            }

            cur = cur.getChildren().get(idx);
        }
        if (cur.getUnderlyingNode() != node) {
            // may happen with the root
            throw new IllegalArgumentException("Node is not part of this tree " + node);
        }
        return cur;
    }

    /**
     * Returns the document node of the tree. Note that this has a single
     * child of element type. Both the document and this element child have
     * the {@link RootNode} as {@link AstElementNode#getUnderlyingNode()}.
     */
    @Override
    public AstDocumentNode getRootNode() {
        return (AstDocumentNode) super.getRootNode();
    }

    public void setAttrCtx(DeprecatedAttrLogger attrCtx) {
        this.logger = attrCtx;
    }

    public DeprecatedAttrLogger getLogger() {
        return logger == null ? DeprecatedAttrLogger.noop() : logger;
    }
}
