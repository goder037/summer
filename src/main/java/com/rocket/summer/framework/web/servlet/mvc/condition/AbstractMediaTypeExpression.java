package com.rocket.summer.framework.web.servlet.mvc.condition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.web.bind.annotation.RequestMapping;

/**
 * Supports media type expressions as described in:
 * {@link RequestMapping#consumes()} and {@link RequestMapping#produces()}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
abstract class AbstractMediaTypeExpression implements MediaTypeExpression, Comparable<AbstractMediaTypeExpression> {

    protected final Log logger = LogFactory.getLog(getClass());

    private final MediaType mediaType;

    private final boolean isNegated;


    AbstractMediaTypeExpression(String expression) {
        if (expression.startsWith("!")) {
            this.isNegated = true;
            expression = expression.substring(1);
        }
        else {
            this.isNegated = false;
        }
        this.mediaType = MediaType.parseMediaType(expression);
    }

    AbstractMediaTypeExpression(MediaType mediaType, boolean negated) {
        this.mediaType = mediaType;
        this.isNegated = negated;
    }


    @Override
    public MediaType getMediaType() {
        return this.mediaType;
    }

    @Override
    public boolean isNegated() {
        return this.isNegated;
    }


    @Override
    public int compareTo(AbstractMediaTypeExpression other) {
        return MediaType.SPECIFICITY_COMPARATOR.compare(this.getMediaType(), other.getMediaType());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            AbstractMediaTypeExpression other = (AbstractMediaTypeExpression) obj;
            return (this.mediaType.equals(other.mediaType) && this.isNegated == other.isNegated);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.mediaType.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.isNegated) {
            builder.append('!');
        }
        builder.append(this.mediaType.toString());
        return builder.toString();
    }

}
