package com.rocket.summer.framework.jmx.support;

/**
 * Indicates registration behavior when attempting to register an MBean that already
 * exists.
 *
 * @author Phillip Webb
 * @author Chris Beams
 * @since 3.2
 */
public enum RegistrationPolicy {

    /**
     * Registration should fail when attempting to register an MBean under a name that
     * already exists.
     */
    FAIL_ON_EXISTING,

    /**
     * Registration should ignore the affected MBean when attempting to register an MBean
     * under a name that already exists.
     */
    IGNORE_EXISTING,

    /**
     * Registration should replace the affected MBean when attempting to register an MBean
     * under a name that already exists.
     */
    REPLACE_EXISTING;

    /**
     * Translate from an {@link MBeanRegistrationSupport} registration behavior constant
     * to a {@link RegistrationPolicy} enum value.
     * @param registrationBehavior one of the now-deprecated REGISTRATION_* constants
     * available in {@link MBeanRegistrationSupport}.
     */
    @SuppressWarnings("deprecation")
    static RegistrationPolicy valueOf(int registrationBehavior) {
        switch (registrationBehavior) {
            case MBeanRegistrationSupport.REGISTRATION_IGNORE_EXISTING:
                return RegistrationPolicy.IGNORE_EXISTING;
            case MBeanRegistrationSupport.REGISTRATION_REPLACE_EXISTING:
                return RegistrationPolicy.REPLACE_EXISTING;
            case MBeanRegistrationSupport.REGISTRATION_FAIL_ON_EXISTING:
                return RegistrationPolicy.FAIL_ON_EXISTING;
        }
        throw new IllegalArgumentException(
                "Unknown MBean registration behavior: " + registrationBehavior);
    }

}

