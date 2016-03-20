/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.ast.Identifier;
import apex.jorje.semantic.ast.compilation.UserClass;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTUserClass extends AbstractApexNode<UserClass> implements RootNode {
    public ASTUserClass(UserClass userClass) {
        super(userClass);
    }

    @Override
    public String getImage() {
        try {
            Field field = node.getClass().getDeclaredField("name");
            field.setAccessible(true);
            Identifier name = (Identifier)field.get(node);
            return name.value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getImage();
    }
}
