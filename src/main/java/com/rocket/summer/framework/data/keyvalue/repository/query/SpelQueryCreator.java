package com.rocket.summer.framework.data.keyvalue.repository.query;

import java.util.Iterator;

import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.data.repository.query.ParameterAccessor;
import com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator;
import com.rocket.summer.framework.data.repository.query.parser.Part;
import com.rocket.summer.framework.data.repository.query.parser.Part.IgnoreCaseType;
import com.rocket.summer.framework.data.repository.query.parser.Part.Type;
import com.rocket.summer.framework.data.repository.query.parser.PartTree;
import com.rocket.summer.framework.data.repository.query.parser.PartTree.OrPart;
import com.rocket.summer.framework.expression.spel.standard.SpelExpression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;

/**
 * {@link AbstractQueryCreator} to create {@link SpelExpression} based {@link KeyValueQuery}s.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
public class SpelQueryCreator extends AbstractQueryCreator<KeyValueQuery<SpelExpression>, String> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private final SpelExpression expression;

    /**
     * Creates a new {@link SpelQueryCreator} for the given {@link PartTree} and {@link ParameterAccessor}.
     *
     * @param tree must not be {@literal null}.
     * @param parameters must not be {@literal null}.
     */
    public SpelQueryCreator(PartTree tree, ParameterAccessor parameters) {

        super(tree, parameters);

        this.expression = toPredicateExpression(tree);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#create(com.rocket.summer.framework.data.repository.query.parser.Part, java.util.Iterator)
     */
    @Override
    protected String create(Part part, Iterator<Object> iterator) {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#and(com.rocket.summer.framework.data.repository.query.parser.Part, java.lang.Object, java.util.Iterator)
     */
    @Override
    protected String and(Part part, String base, Iterator<Object> iterator) {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#or(java.lang.Object, java.lang.Object)
     */
    @Override
    protected String or(String base, String criteria) {
        return "";
    }

    @Override
    protected KeyValueQuery<SpelExpression> complete(String criteria, Sort sort) {

        KeyValueQuery<SpelExpression> query = new KeyValueQuery<SpelExpression>(this.expression);

        if (sort != null) {
            query.orderBy(sort);
        }

        return query;
    }

    protected SpelExpression toPredicateExpression(PartTree tree) {

        int parameterIndex = 0;
        StringBuilder sb = new StringBuilder();

        for (Iterator<OrPart> orPartIter = tree.iterator(); orPartIter.hasNext();) {

            int partCnt = 0;
            StringBuilder partBuilder = new StringBuilder();
            OrPart orPart = orPartIter.next();

            for (Iterator<Part> partIter = orPart.iterator(); partIter.hasNext();) {

                Part part = partIter.next();

                if(!requiresInverseLookup(part)) {

                    partBuilder.append("#it?.");
                    partBuilder.append(part.getProperty().toDotPath().replace(".", "?."));
                }

                // TODO: check if we can have caseinsensitive search
                if (!part.shouldIgnoreCase().equals(IgnoreCaseType.NEVER)) {
                    throw new InvalidDataAccessApiUsageException("Ignore case not supported!");
                }

                switch (part.getType()) {
                    case TRUE:
                        partBuilder.append("?.equals(true)");
                        break;
                    case FALSE:
                        partBuilder.append("?.equals(false)");
                        break;
                    case SIMPLE_PROPERTY:
                        partBuilder.append("?.equals(").append("[").append(parameterIndex++).append("])");
                        break;
                    case IS_NULL:
                        partBuilder.append(" == null");
                        break;
                    case IS_NOT_NULL:
                        partBuilder.append(" != null");
                        break;
                    case LIKE:
                        partBuilder.append("?.contains(").append("[").append(parameterIndex++).append("])");
                        break;
                    case STARTING_WITH:
                        partBuilder.append("?.startsWith(").append("[").append(parameterIndex++).append("])");
                        break;
                    case AFTER:
                    case GREATER_THAN:
                        partBuilder.append(">").append("[").append(parameterIndex++).append("]");
                        break;
                    case GREATER_THAN_EQUAL:
                        partBuilder.append(">=").append("[").append(parameterIndex++).append("]");
                        break;
                    case BEFORE:
                    case LESS_THAN:
                        partBuilder.append("<").append("[").append(parameterIndex++).append("]");
                        break;
                    case LESS_THAN_EQUAL:
                        partBuilder.append("<=").append("[").append(parameterIndex++).append("]");
                        break;
                    case ENDING_WITH:
                        partBuilder.append("?.endsWith(").append("[").append(parameterIndex++).append("])");
                        break;
                    case BETWEEN:

                        int index = partBuilder.lastIndexOf("#it?.");

                        partBuilder.insert(index, "(");
                        partBuilder.append(">").append("[").append(parameterIndex++).append("]");
                        partBuilder.append("&&");
                        partBuilder.append("#it?.");
                        partBuilder.append(part.getProperty().toDotPath().replace(".", "?."));
                        partBuilder.append("<").append("[").append(parameterIndex++).append("]");
                        partBuilder.append(")");

                        break;

                    case REGEX:

                        partBuilder.append(" matches ").append("[").append(parameterIndex++).append("]");
                        break;
                    case IN:

                        partBuilder.append("[").append(parameterIndex++).append("].contains(");
                        partBuilder.append("#it?.");
                        partBuilder.append(part.getProperty().toDotPath().replace(".", "?."));
                        partBuilder.append(")");
                        break;

                    case CONTAINING:
                    case NOT_CONTAINING:
                    case NEGATING_SIMPLE_PROPERTY:
                    case EXISTS:
                    default:
                        throw new InvalidDataAccessApiUsageException(String.format("Found invalid part '%s' in query",
                                part.getType()));
                }

                if (partIter.hasNext()) {
                    partBuilder.append("&&");
                }

                partCnt++;
            }

            if (partCnt > 1) {
                sb.append("(").append(partBuilder).append(")");
            } else {
                sb.append(partBuilder);
            }

            if (orPartIter.hasNext()) {
                sb.append("||");
            }
        }

        return PARSER.parseRaw(sb.toString());
    }

    private static boolean requiresInverseLookup(Part part) {
        return part.getType() == Type.IN;
    }
}

