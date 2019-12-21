package com.rocket.summer.framework.aop;

import org.aopalliance.aop.Advice;

/**
 * Common marker interface for after advice,
 * such as {@link AfterReturningAdvice} and {@link ThrowsAdvice}.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see BeforeAdvice
 */
public interface AfterAdvice extends Advice {

}
