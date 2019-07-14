package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.io.ClassPathResource;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;

/**
 * 单独的xml应用上下文, 加载上下文定义文件从类路径下面, 将会解析路径下的该资源 (比如. "mypackage/myresource.txt").
 * 在嵌入式容器应用中很有用.
 * <p>配置文件位置默认可以被重写覆盖通过 {@link #getConfigLocations},
 * 文件位置可以指明某些具体的文件像 "/myfiles/context.xml", 也可以使用 Ant风格的表达式像 "/myfiles/*-context.xml" (具体实现细节
 * {@link org.springframework.util.AntPathMatcher} ).
 *
 * <p>笔记: 在多个配置文件路径的情况下, 越往后的配置会覆盖前面加载的配置文件相关的信息. 这样可以使用额外的Xml文件故意重写某些定义.
 *
 * <p><b>这是一个简单, 一站式的 ApplicationContext.
 * 考虑使用 {@link GenericApplicationContext} 类整合了
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}
 * 提供了更多灵活的容器启动方式.</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getResource
 * @see #getResourceByPath
 * @see GenericApplicationContext
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    private Resource[] configResources;


    /**
     * 创建一个 ClassPathXmlApplicationContext.
     * @see #setConfigLocation
     * @see #setConfigLocations
     * @see #afterPropertiesSet()
     */
    public ClassPathXmlApplicationContext() {
    }

    /**
     * 创建一个 ClassPathXmlApplicationContext.
     * @param parent the parent context
     * @see #setConfigLocation
     * @see #setConfigLocations
     * @see #afterPropertiesSet()
     */
    public ClassPathXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    /**
     * 创建一个 ClassPathXmlApplicationContext, 从给定的XML加载上下文，并且自动刷新容器立即生效.
     * @param configLocation resource location
     * @throws BeansException if context creation failed
     */
    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this(new String[] {configLocation}, true, null);
    }

    /**
     * 创建一个 ClassPathXmlApplicationContext, loading the definitions
     * from the given XML files and automatically refreshing the context.
     * @param configLocations array of resource locations
     * @throws BeansException if context creation failed
     */
    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        this(configLocations, true, null);
    }

    /**
     * Create a new ClassPathXmlApplicationContext with the given parent,
     * loading the definitions from the given XML files and automatically
     * refreshing the context.
     * @param configLocations array of resource locations
     * @param parent the parent context
     * @throws BeansException if context creation failed
     */
    public ClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
        this(configLocations, true, parent);
    }

    /**
     * Create a new ClassPathXmlApplicationContext, loading the definitions
     * from the given XML files.
     * @param configLocations array of resource locations
     * @param refresh whether to automatically refresh the context,
     * loading all bean definitions and creating all singletons.
     * Alternatively, call refresh manually after further configuring the context.
     * @throws BeansException if context creation failed
     * @see #refresh()
     */
    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        this(configLocations, refresh, null);
    }

    /**
     * Create a new ClassPathXmlApplicationContext with the given parent,
     * loading the definitions from the given XML files.
     * @param configLocations array of resource locations
     * @param refresh whether to automatically refresh the context,
     * loading all bean definitions and creating all singletons.
     * Alternatively, call refresh manually after further configuring the context.
     * @param parent the parent context
     * @throws BeansException if context creation failed
     * @see #refresh()
     */
    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
            throws BeansException {

        super(parent);
        setConfigLocations(configLocations);
        if (refresh) {
            refresh();
        }
    }


    /**
     * Create a new ClassPathXmlApplicationContext, loading the definitions
     * from the given XML file and automatically refreshing the context.
     * <p>This is a convenience method to load class path resources relative to a
     * given Class. For full flexibility, consider using a GenericApplicationContext
     * with an XmlBeanDefinitionReader and a ClassPathResource argument.
     * @param path relative (or absolute) path within the class path
     * @param clazz the class to load resources with (basis for the given paths)
     * @throws BeansException if context creation failed
     * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
     * @see org.springframework.context.support.GenericApplicationContext
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     */
    public ClassPathXmlApplicationContext(String path, Class clazz) throws BeansException {
        this(new String[] {path}, clazz);
    }

    /**
     * Create a new ClassPathXmlApplicationContext, loading the definitions
     * from the given XML files and automatically refreshing the context.
     * @param paths array of relative (or absolute) paths within the class path
     * @param clazz the class to load resources with (basis for the given paths)
     * @throws BeansException if context creation failed
     * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
     * @see org.springframework.context.support.GenericApplicationContext
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     */
    public ClassPathXmlApplicationContext(String[] paths, Class clazz) throws BeansException {
        this(paths, clazz, null);
    }

    /**
     * Create a new ClassPathXmlApplicationContext with the given parent,
     * loading the definitions from the given XML files and automatically
     * refreshing the context.
     * @param paths array of relative (or absolute) paths within the class path
     * @param clazz the class to load resources with (basis for the given paths)
     * @param parent the parent context
     * @throws BeansException if context creation failed
     * @see org.springframework.core.io.ClassPathResource#ClassPathResource(String, Class)
     * @see org.springframework.context.support.GenericApplicationContext
     * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
     */
    public ClassPathXmlApplicationContext(String[] paths, Class clazz, ApplicationContext parent)
            throws BeansException {

        super(parent);
        Assert.notNull(paths, "Path array must not be null");
        Assert.notNull(clazz, "Class argument must not be null");
        this.configResources = new Resource[paths.length];
        for (int i = 0; i < paths.length; i++) {
            this.configResources[i] = new ClassPathResource(paths[i], clazz);
        }
        refresh();
    }


    protected Resource[] getConfigResources() {
        return this.configResources;
    }

}
