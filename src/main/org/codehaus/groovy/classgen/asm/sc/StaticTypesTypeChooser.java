/*
 * Copyright 2003-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.classgen.asm.sc;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.classgen.asm.StatementMetaTypeChooser;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

/**
 * A {@link org.codehaus.groovy.classgen.asm.TypeChooser} which reads type information from node metadata
 * generated by the {@link groovy.transform.CompileStatic} annotation.
 *
 * @author Cedric Champeau
 */
public class StaticTypesTypeChooser extends StatementMetaTypeChooser {
    @Override
    public ClassNode resolveType(final Expression exp, final ClassNode current) {
        Expression target = exp instanceof VariableExpression ? getTarget((VariableExpression) exp) : exp;
        ClassNode inferredType = (ClassNode) target.getNodeMetaData(StaticTypesMarker.DECLARATION_INFERRED_TYPE);
        if (inferredType == null) {
            inferredType = (ClassNode) target.getNodeMetaData(StaticTypesMarker.INFERRED_TYPE);
        }
        if (inferredType != null) {
            if (ClassHelper.VOID_TYPE==inferredType) {
                // we are in a case of a type inference failure, probably because code was generated
                // it is better to avoid using this
                inferredType = super.resolveType(exp, current);
            }
            return inferredType;
        }
        return super.resolveType(exp, current);
    }

    /**
     * The inferred type, in case of a variable expression, can be set on the accessed variable, so we take it instead
     * of the facade one.
     * @param ve the variable expression for which to return the target expression
     * @return the target variable expression
     */
    private static VariableExpression getTarget(VariableExpression ve) {
        if (ve.getAccessedVariable()==null || ve.getAccessedVariable()==ve || (!(ve.getAccessedVariable() instanceof VariableExpression))) return ve;
        return getTarget((VariableExpression) ve.getAccessedVariable());
    }
}
