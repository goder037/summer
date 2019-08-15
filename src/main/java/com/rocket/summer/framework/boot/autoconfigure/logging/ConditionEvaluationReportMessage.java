package com.rocket.summer.framework.boot.autoconfigure.logging;

import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionEvaluationReport;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

import java.util.*;

/**
 * A condition evaluation report message that can logged or printed.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class ConditionEvaluationReportMessage {

    private StringBuilder message;

    public ConditionEvaluationReportMessage(ConditionEvaluationReport report) {
        this.message = getLogMessage(report);
    }

    private StringBuilder getLogMessage(ConditionEvaluationReport report) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("%n%n%n"));
        message.append(String.format("=========================%n"));
        message.append(String.format("AUTO-CONFIGURATION REPORT%n"));
        message.append(String.format("=========================%n%n%n"));
        message.append(String.format("Positive matches:%n"));
        message.append(String.format("-----------------%n"));
        Map<String, ConditionEvaluationReport.ConditionAndOutcomes> shortOutcomes = orderByName(
                report.getConditionAndOutcomesBySource());
        for (Map.Entry<String, ConditionEvaluationReport.ConditionAndOutcomes> entry : shortOutcomes.entrySet()) {
            if (entry.getValue().isFullMatch()) {
                addMatchLogMessage(message, entry.getKey(), entry.getValue());
            }
        }
        message.append(String.format("%n%n"));
        message.append(String.format("Negative matches:%n"));
        message.append(String.format("-----------------%n"));
        for (Map.Entry<String, ConditionEvaluationReport.ConditionAndOutcomes> entry : shortOutcomes.entrySet()) {
            if (!entry.getValue().isFullMatch()) {
                addNonMatchLogMessage(message, entry.getKey(), entry.getValue());
            }
        }
        message.append(String.format("%n%n"));
        message.append(String.format("Exclusions:%n"));
        message.append(String.format("-----------%n"));
        if (report.getExclusions().isEmpty()) {
            message.append(String.format("%n    None%n"));
        }
        else {
            for (String exclusion : report.getExclusions()) {
                message.append(String.format("%n    %s%n", exclusion));
            }
        }
        message.append(String.format("%n%n"));
        message.append(String.format("Unconditional classes:%n"));
        message.append(String.format("----------------------%n"));
        if (report.getUnconditionalClasses().isEmpty()) {
            message.append(String.format("%n    None%n"));
        }
        else {
            for (String unconditionalClass : report.getUnconditionalClasses()) {
                message.append(String.format("%n    %s%n", unconditionalClass));
            }
        }
        message.append(String.format("%n%n"));
        return message;
    }

    private Map<String, ConditionEvaluationReport.ConditionAndOutcomes> orderByName(
            Map<String, ConditionEvaluationReport.ConditionAndOutcomes> outcomes) {
        Map<String, ConditionEvaluationReport.ConditionAndOutcomes> result = new LinkedHashMap<String, ConditionEvaluationReport.ConditionAndOutcomes>();
        List<String> names = new ArrayList<String>();
        Map<String, String> classNames = new HashMap<String, String>();
        for (String name : outcomes.keySet()) {
            String shortName = ClassUtils.getShortName(name);
            names.add(shortName);
            classNames.put(shortName, name);
        }
        Collections.sort(names);
        for (String shortName : names) {
            result.put(shortName, outcomes.get(classNames.get(shortName)));
        }
        return result;
    }

    private void addMatchLogMessage(StringBuilder message, String source,
                                    ConditionEvaluationReport.ConditionAndOutcomes matches) {
        message.append(String.format("%n   %s matched:%n", source));
        for (ConditionEvaluationReport.ConditionAndOutcome match : matches) {
            logConditionAndOutcome(message, "      ", match);
        }
    }

    private void addNonMatchLogMessage(StringBuilder message, String source,
                                       ConditionEvaluationReport.ConditionAndOutcomes conditionAndOutcomes) {
        message.append(String.format("%n   %s:%n", source));
        List<ConditionEvaluationReport.ConditionAndOutcome> matches = new ArrayList<ConditionEvaluationReport.ConditionAndOutcome>();
        List<ConditionEvaluationReport.ConditionAndOutcome> nonMatches = new ArrayList<ConditionEvaluationReport.ConditionAndOutcome>();
        for (ConditionEvaluationReport.ConditionAndOutcome conditionAndOutcome : conditionAndOutcomes) {
            if (conditionAndOutcome.getOutcome().isMatch()) {
                matches.add(conditionAndOutcome);
            }
            else {
                nonMatches.add(conditionAndOutcome);
            }
        }
        message.append(String.format("      Did not match:%n"));
        for (ConditionEvaluationReport.ConditionAndOutcome nonMatch : nonMatches) {
            logConditionAndOutcome(message, "         ", nonMatch);
        }
        if (!matches.isEmpty()) {
            message.append(String.format("      Matched:%n"));
            for (ConditionEvaluationReport.ConditionAndOutcome match : matches) {
                logConditionAndOutcome(message, "         ", match);
            }
        }
    }

    private void logConditionAndOutcome(StringBuilder message, String indent,
                                        ConditionEvaluationReport.ConditionAndOutcome conditionAndOutcome) {
        message.append(String.format("%s- ", indent));
        String outcomeMessage = conditionAndOutcome.getOutcome().getMessage();
        if (StringUtils.hasLength(outcomeMessage)) {
            message.append(outcomeMessage);
        }
        else {
            message.append(conditionAndOutcome.getOutcome().isMatch() ? "matched"
                    : "did not match");
        }
        message.append(" (");
        message.append(
                ClassUtils.getShortName(conditionAndOutcome.getCondition().getClass()));
        message.append(String.format(")%n"));
    }

    @Override
    public String toString() {
        return this.message.toString();
    }

}

