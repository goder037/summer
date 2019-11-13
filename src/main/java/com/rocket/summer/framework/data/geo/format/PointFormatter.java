package com.rocket.summer.framework.data.geo.format;

import java.text.ParseException;
import java.util.Locale;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.core.convert.converter.GenericConverter.ConvertiblePair;
import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.format.Formatter;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Converter to parse two comma-separated doubles into a {@link Point}.
 * 
 * @author Oliver Gierke
 */
public enum PointFormatter implements Converter<String, Point>, Formatter<Point> {

	INSTANCE;

	public static final ConvertiblePair CONVERTIBLE = new ConvertiblePair(String.class, Point.class);

	private static final String INVALID_FORMAT = "Expected two doubles separated by a comma but got '%s'!";

	/* 
	 * (non-Javadoc)
	 * @see com.rocket.summer.framework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public Point convert(String source) {

		String[] parts = source.split(",");

		if (parts.length != 2) {
			throw new IllegalArgumentException(String.format(INVALID_FORMAT, source));
		}

		try {

			double latitude = Double.parseDouble(parts[0]);
			double longitude = Double.parseDouble(parts[1]);

			return new Point(longitude, latitude);

		} catch (NumberFormatException o_O) {
			throw new IllegalArgumentException(String.format(INVALID_FORMAT, source), o_O);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.rocket.summer.framework.format.Printer#print(java.lang.Object, java.util.Locale)
	 */
	@Override
	public String print(Point point, Locale locale) {
		return point == null ? null : String.format("%s,%s", point.getY(), point.getX());
	}

	/*
	 * (non-Javadoc)
	 * @see com.rocket.summer.framework.format.Parser#parse(java.lang.String, java.util.Locale)
	 */
	@Override
	public Point parse(String text, Locale locale) throws ParseException {
		return !StringUtils.hasText(text) ? null : convert(text);
	}
}
