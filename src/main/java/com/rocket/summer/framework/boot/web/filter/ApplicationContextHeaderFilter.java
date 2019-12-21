package com.rocket.summer.framework.boot.web.filter;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@link OncePerRequestFilter} to add a {@literal X-Application-Context} header that
 * contains the {@link ApplicationContext#getId() ApplicationContext ID}.
 *
 * @author Phillip Webb
 * @author Venil Noronha
 * @since 1.4.0
 */
public class ApplicationContextHeaderFilter extends OncePerRequestFilter {

    /**
     * Public constant for {@literal X-Application-Context}.
     */
    public static final String HEADER_NAME = "X-Application-Context";

    private final ApplicationContext applicationContext;

    public ApplicationContextHeaderFilter(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.addHeader(HEADER_NAME, this.applicationContext.getId());
        filterChain.doFilter(request, response);
    }

}
