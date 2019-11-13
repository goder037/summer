package com.rocket.summer.framework.data.geo;

import java.io.Serializable;

/**
 * Interface for {@link Metric}s that can be applied to a base scale.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @since 1.8
 */
public interface Metric extends Serializable {

	/**
	 * Returns the multiplier to calculate metrics values from a base scale.
	 * 
	 * @return
	 */
	double getMultiplier();

	/**
	 * Returns the scientific abbreviation of the unit the {@link Metric} is in.
	 * 
	 * @return
	 */
	String getAbbreviation();
}
