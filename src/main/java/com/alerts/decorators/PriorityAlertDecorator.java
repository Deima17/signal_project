package com.alerts.decorators;

import com.alerts.AlertInterface;

/**
 * Decorator that adds a priority level to an alert.
 * Higher priority alerts require more urgent attention from medical staff.
 */
public class PriorityAlertDecorator extends AlertDecorator {

    private String priorityLevel;

    /**
     * Constructs a PriorityAlertDecorator.
     *
     * @param alert the alert to wrap
     * @param priorityLevel the priority level to attach, e.g. "HIGH", "CRITICAL"
     */
    public PriorityAlertDecorator(AlertInterface alert, String priorityLevel) {
        super(alert);
        this.priorityLevel = priorityLevel;
    }

    /**
     * Returns the condition with the priority level prepended.
     *
     * @return the decorated condition string
     */
    @Override
    public String getCondition() {
        return "[" + priorityLevel + "] " + decoratedAlert.getCondition();
    }
}