package com.rocket.summer.framework.data.geo;

/**
 * Commonly used {@link Metric}s.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @since 1.8
 */
public enum Metrics implements Metric {

	KILOMETERS(6378.137, "km"), MILES(3963.191, "mi"), NEUTRAL(1, "");

	private final double multiplier;
	private final String abbreviation;

	/**
	 * Creates a new {@link Metrics} using the given multiplier.
	 * 
	 * @param multiplier the earth radius at equator, must not be {@literal null}.
	 * @param abbreviation the abbreviation to use for this {@link Metric}, must not be {@literal null}.
	 */
	private Metrics(double multiplier, String abbreviation) {

		this.multiplier = multiplier;
		this.abbreviation = abbreviation;
	}

	/*
	 * (non-Javadoc)
	 * @see com.rocket.summer.framework.data.mongodb.core.geo.Metric#getMultiplier()
	 */
	public double getMultiplier() {
		return multiplier;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.rocket.summer.framework.data.geo.Metric#getAbbreviation()
	 */
	@Override
	public String getAbbreviation() {
		return abbreviation;
	}
}
