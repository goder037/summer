package com.rocket.summer.framework.boot.context.embedded.tomcat;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.boot.web.servlet.ErrorPage;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.catalina.Context;

import java.lang.reflect.Method;

/**
 * Tomcat specific management for an {@link ErrorPage}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
class TomcatErrorPage {

    private static final String ERROR_PAGE_CLASS = "org.apache.tomcat.util.descriptor.web.ErrorPage";

    private static final String LEGACY_ERROR_PAGE_CLASS = "org.apache.catalina.deploy.ErrorPage";

    private final String location;

    private final String exceptionType;

    private final int errorCode;

    private final Object nativePage;

    TomcatErrorPage(ErrorPage errorPage) {
        this.location = errorPage.getPath();
        this.exceptionType = errorPage.getExceptionName();
        this.errorCode = errorPage.getStatusCode();
        this.nativePage = createNativePage();
    }

    private Object createNativePage() {
        try {
            if (ClassUtils.isPresent(ERROR_PAGE_CLASS, null)) {
                return BeanUtils.instantiate(ClassUtils.forName(ERROR_PAGE_CLASS, null));
            }
            if (ClassUtils.isPresent(LEGACY_ERROR_PAGE_CLASS, null)) {
                return BeanUtils
                        .instantiate(ClassUtils.forName(LEGACY_ERROR_PAGE_CLASS, null));
            }
        }
        catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        catch (LinkageError ex) {
            // Swallow and continue
        }
        return null;
    }

    public void addToContext(Context context) {
        Assert.state(this.nativePage != null,
                "Neither Tomcat 7 nor 8 detected so no native error page exists");
        if (ClassUtils.isPresent(ERROR_PAGE_CLASS, null)) {
            org.apache.tomcat.util.descriptor.web.ErrorPage errorPage = (org.apache.tomcat.util.descriptor.web.ErrorPage) this.nativePage;
            errorPage.setLocation(this.location);
            errorPage.setErrorCode(this.errorCode);
            errorPage.setExceptionType(this.exceptionType);
            context.addErrorPage(errorPage);
        }
        else {
            callMethod(this.nativePage, "setLocation", this.location, String.class);
            callMethod(this.nativePage, "setErrorCode", this.errorCode, int.class);
            callMethod(this.nativePage, "setExceptionType", this.exceptionType,
                    String.class);
            callMethod(context, "addErrorPage", this.nativePage,
                    this.nativePage.getClass());
        }
    }

    private void callMethod(Object target, String name, Object value, Class<?> type) {
        Method method = ReflectionUtils.findMethod(target.getClass(), name, type);
        ReflectionUtils.invokeMethod(method, target, value);
    }

}

