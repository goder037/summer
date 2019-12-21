package com.rocket.summer.framework.format;

import java.util.Locale;

/**
 * Prints objects of type T for display.
 *
 * @author Keith Donald
 * @since 3.0
 * @param <T> the type of object this Printer prints
 */
public interface Printer<T> {

    /**
     * Print the object of type T for display.
     * @param object the instance to print
     * @param locale the current user locale
     * @return the printed text string
     */
    String print(T object, Locale locale);

}
