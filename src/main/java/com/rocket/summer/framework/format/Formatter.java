package com.rocket.summer.framework.format;

/**
 * Formats objects of type T.
 * A Formatter is both a Printer <i>and</i> a Parser for an object type.
 *
 * @author Keith Donald
 * @since 3.0
 * @param <T> the type of object this Formatter formats
 */
public interface Formatter<T> extends Printer<T>, Parser<T> {

}