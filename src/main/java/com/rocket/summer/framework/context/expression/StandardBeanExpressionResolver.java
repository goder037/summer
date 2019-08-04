package com.rocket.summer.framework.context.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanExpressionException;
import com.rocket.summer.framework.beans.factory.config.BeanExpressionContext;
import com.rocket.summer.framework.beans.factory.config.BeanExpressionResolver;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.expression.Expression;
import com.rocket.summer.framework.expression.ExpressionParser;
import com.rocket.summer.framework.expression.ParserContext;
import com.rocket.summer.framework.expression.spel.SpelParserConfiguration;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext;
import com.rocket.summer.framework.expression.spel.support.StandardTypeConverter;
import com.rocket.summer.framework.expression.spel.support.StandardTypeLocator;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Standard implementation of the
 * {@link com.rocket.summer.framework.beans.factory.config.BeanExpressionResolver}
 * interface, parsing and evaluating Spring EL using Spring's expression module.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.expression.ExpressionParser
 * @see com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser
 * @see com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext
 */
public class StandardBeanExpressionResolver implements BeanExpressionResolver {

    /** Default expression prefix: "#{" */
    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";

    /** Default expression suffix: "}" */
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";


    private String expressionPrefix = DEFAULT_EXPRESSION_PREFIX;

    private String expressionSuffix = DEFAULT_EXPRESSION_SUFFIX;

    private ExpressionParser expressionParser;

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>(256);

    private final Map<BeanExpressionContext, StandardEvaluationContext> evaluationCache =
            new ConcurrentHashMap<BeanExpressionContext, StandardEvaluationContext>(8);

    private final ParserContext beanExpressionParserContext = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }
        @Override
        public String getExpressionPrefix() {
            return expressionPrefix;
        }
        @Override
        public String getExpressionSuffix() {
            return expressionSuffix;
        }
    };


    /**
     * Create a new {@code StandardBeanExpressionResolver} with default settings.
     */
    public StandardBeanExpressionResolver() {
        this.expressionParser = new SpelExpressionParser();
    }

    /**
     * Create a new {@code StandardBeanExpressionResolver} with the given bean class loader,
     * using it as the basis for expression compilation.
     * @param beanClassLoader the factory's bean class loader
     */
    public StandardBeanExpressionResolver(ClassLoader beanClassLoader) {
        this.expressionParser = new SpelExpressionParser(new SpelParserConfiguration(null, beanClassLoader));
    }


    /**
     * Set the prefix that an expression string starts with.
     * The default is "#{".
     * @see #DEFAULT_EXPRESSION_PREFIX
     */
    public void setExpressionPrefix(String expressionPrefix) {
        Assert.hasText(expressionPrefix, "Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }

    /**
     * Set the suffix that an expression string ends with.
     * The default is "}".
     * @see #DEFAULT_EXPRESSION_SUFFIX
     */
    public void setExpressionSuffix(String expressionSuffix) {
        Assert.hasText(expressionSuffix, "Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }

    /**
     * Specify the EL parser to use for expression parsing.
     * <p>Default is a {@link com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser},
     * compatible with standard Unified EL style expression syntax.
     */
    public void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull(expressionParser, "ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }


    @Override
    public Object evaluate(String value, BeanExpressionContext evalContext) throws BeansException {
        if (!StringUtils.hasLength(value)) {
            return value;
        }
        try {
            Expression expr = this.expressionCache.get(value);
            if (expr == null) {
                expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
                this.expressionCache.put(value, expr);
            }
            StandardEvaluationContext sec = this.evaluationCache.get(evalContext);
            if (sec == null) {
                sec = new StandardEvaluationContext(evalContext);
                sec.addPropertyAccessor(new BeanExpressionContextAccessor());
                sec.addPropertyAccessor(new BeanFactoryAccessor());
                sec.addPropertyAccessor(new MapAccessor());
                sec.addPropertyAccessor(new EnvironmentAccessor());
                sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
                sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
                ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
                if (conversionService != null) {
                    sec.setTypeConverter(new StandardTypeConverter(conversionService));
                }
                customizeEvaluationContext(sec);
                this.evaluationCache.put(evalContext, sec);
            }
            return expr.getValue(sec);
        }
        catch (Throwable ex) {
            throw new BeanExpressionException("Expression parsing failed", ex);
        }
    }

    /**
     * Template method for customizing the expression evaluation context.
     * <p>The default implementation is empty.
     */
    protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
    }

}

