package com.alerts.decorators;
import com.alerts.AlertInterface;

/**
 * Decorator that marks an alert as requiring repeated checks.
 * Adds a repeat interval to the alert condition for tracking purposes.
 */
public class RepeatedAlertDecorator extends AlertDecorator {

    private int repeatIntervalSeconds;

    /**
     * Constructs a RepeatedAlertDecorator.
     *
     * @param alert the alert to wrap
     * @param repeatIntervalSeconds how often the alert condition should be re-checked
     */
    public RepeatedAlertDecorator(AlertInterface alert, int repeatIntervalSeconds) {
        super(alert);
        this.repeatIntervalSeconds = repeatIntervalSeconds;
    }

    /**
     * Returns the condition with a repeat interval note appended.
     *
     * @return the decorated condition string
     */
    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " [Repeat every " + repeatIntervalSeconds + "s]";
    }
}