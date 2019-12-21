package com.rocket.summer.framework.web.servlet.handler;

import com.rocket.summer.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link com.rocket.summer.framework.web.servlet.HandlerMapping}
 * interface that map from URLs to beans with names that start with a slash ("/"),
 * similar to how Struts maps URLs to action names.
 *
 * <p>This is the default implementation used by the
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}, along with
 * {@link com.rocket.summer.framework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping}
 * (on Java 5 and higher). Alternatively, {@link SimpleUrlHandlerMapping} allows for
 * customizing a handler mapping declaratively.
 *
 * <p>The mapping is from URL to bean name. Thus an incoming URL "/foo" would map
 * to a handler named "/foo", or to "/foo /foo2" in case of multiple mappings to
 * a single handler. Note: In XML definitions, you'll need to use an alias
 * name="/foo" in the bean definition, as the XML id may not contain slashes.
 *
 * <p>Supports direct matches (given "/test" -> registered "/test") and "*"
 * matches (given "/test" -> registered "/t*"). Note that the default is
 * to map within the current servlet mapping if applicable; see the
 * {@link #setAlwaysUseFullPath "alwaysUseFullPath"} property for details.
 * For details on the pattern options, see the
 * {@link com.rocket.summer.framework.util.AntPathMatcher} javadoc.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {

    /**
     * Checks name and aliases of the given bean for URLs, starting with "/".
     */
    @Override
    protected String[] determineUrlsForHandler(String beanName) {
        List<String> urls = new ArrayList<String>();
        if (beanName.startsWith("/")) {
            urls.add(beanName);
        }
        String[] aliases = getApplicationContext().getAliases(beanName);
        for (String alias : aliases) {
            if (alias.startsWith("/")) {
                urls.add(alias);
            }
        }
        return StringUtils.toStringArray(urls);
    }

}
