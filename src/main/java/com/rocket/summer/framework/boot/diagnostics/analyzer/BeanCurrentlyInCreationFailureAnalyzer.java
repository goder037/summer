package com.rocket.summer.framework.boot.diagnostics.analyzer;

import com.rocket.summer.framework.beans.factory.BeanCreationException;
import com.rocket.summer.framework.beans.factory.BeanCurrentlyInCreationException;
import com.rocket.summer.framework.beans.factory.InjectionPoint;
import com.rocket.summer.framework.beans.factory.UnsatisfiedDependencyException;
import com.rocket.summer.framework.boot.diagnostics.AbstractFailureAnalyzer;
import com.rocket.summer.framework.boot.diagnostics.FailureAnalysis;
import com.rocket.summer.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AbstractFailureAnalyzer} that performs analysis of failures caused by a
 * {@link BeanCurrentlyInCreationException}.
 *
 * @author Andy Wilkinson
 */
class BeanCurrentlyInCreationFailureAnalyzer
        extends AbstractFailureAnalyzer<BeanCurrentlyInCreationException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure,
                                      BeanCurrentlyInCreationException cause) {
        DependencyCycle dependencyCycle = findCycle(rootFailure);
        if (dependencyCycle == null) {
            return null;
        }
        return new FailureAnalysis(buildMessage(dependencyCycle), null, cause);
    }

    private DependencyCycle findCycle(Throwable rootFailure) {
        List<BeanInCycle> beansInCycle = new ArrayList<BeanInCycle>();
        Throwable candidate = rootFailure;
        int cycleStart = -1;
        while (candidate != null) {
            BeanInCycle beanInCycle = BeanInCycle.get(candidate);
            if (beanInCycle != null) {
                int index = beansInCycle.indexOf(beanInCycle);
                if (index == -1) {
                    beansInCycle.add(beanInCycle);
                }
                cycleStart = (cycleStart != -1) ? cycleStart : index;
            }
            candidate = candidate.getCause();
        }
        if (cycleStart == -1) {
            return null;
        }
        return new DependencyCycle(beansInCycle, cycleStart);
    }

    private String buildMessage(DependencyCycle dependencyCycle) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("The dependencies of some of the beans in the "
                + "application context form a cycle:%n%n"));
        List<BeanInCycle> beansInCycle = dependencyCycle.getBeansInCycle();
        int cycleStart = dependencyCycle.getCycleStart();
        for (int i = 0; i < beansInCycle.size(); i++) {
            BeanInCycle beanInCycle = beansInCycle.get(i);
            if (i == cycleStart) {
                message.append(String.format("┌─────┐%n"));
            }
            else if (i > 0) {
                String leftSide = (i < cycleStart) ? " " : "↑";
                message.append(String.format("%s     ↓%n", leftSide));
            }
            String leftSide = (i < cycleStart) ? " " : "|";
            message.append(String.format("%s  %s%n", leftSide, beanInCycle));
        }
        message.append(String.format("└─────┘%n"));
        return message.toString();
    }

    private static final class DependencyCycle {

        private final List<BeanInCycle> beansInCycle;

        private final int cycleStart;

        private DependencyCycle(List<BeanInCycle> beansInCycle, int cycleStart) {
            this.beansInCycle = beansInCycle;
            this.cycleStart = cycleStart;
        }

        public List<BeanInCycle> getBeansInCycle() {
            return this.beansInCycle;
        }

        public int getCycleStart() {
            return this.cycleStart;
        }

    }

    private static final class BeanInCycle {

        private final String name;

        private final String description;

        private BeanInCycle(BeanCreationException ex) {
            this.name = ex.getBeanName();
            this.description = determineDescription(ex);
        }

        private String determineDescription(BeanCreationException ex) {
            if (StringUtils.hasText(ex.getResourceDescription())) {
                return String.format(" defined in %s", ex.getResourceDescription());
            }
            InjectionPoint failedInjectionPoint = findFailedInjectionPoint(ex);
            if (failedInjectionPoint != null && failedInjectionPoint.getField() != null) {
                return String.format(" (field %s)", failedInjectionPoint.getField());
            }
            return "";
        }

        private InjectionPoint findFailedInjectionPoint(BeanCreationException ex) {
            if (!(ex instanceof UnsatisfiedDependencyException)) {
                return null;
            }
            return ((UnsatisfiedDependencyException) ex).getInjectionPoint();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.name.equals(((BeanInCycle) obj).name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public String toString() {
            return this.name + this.description;
        }

        public static BeanInCycle get(Throwable ex) {
            if (ex instanceof BeanCreationException) {
                return get((BeanCreationException) ex);
            }
            return null;
        }

        private static BeanInCycle get(BeanCreationException ex) {
            if (StringUtils.hasText(ex.getBeanName())) {
                return new BeanInCycle(ex);
            }
            return null;
        }

    }

}

