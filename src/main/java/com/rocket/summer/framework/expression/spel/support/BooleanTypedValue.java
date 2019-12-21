package com.rocket.summer.framework.expression.spel.support;

import com.rocket.summer.framework.expression.TypedValue;

/**
 * @author Andy Clement
 * @since 3.0
 */
public class BooleanTypedValue extends TypedValue {

    public static final BooleanTypedValue TRUE = new BooleanTypedValue(true);

    public static final BooleanTypedValue FALSE = new BooleanTypedValue(false);


    private BooleanTypedValue(boolean b) {
        super(b);
    }


    public static BooleanTypedValue forValue(boolean b) {
        return (b ? TRUE : FALSE);
    }

}
