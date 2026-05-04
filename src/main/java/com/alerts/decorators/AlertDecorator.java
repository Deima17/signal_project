package com.alerts.decorators;
import com.alerts.AlertInterface;

/**
 * Abstract base decorator for alerts.
 * Wraps an existing AlertInterface and delegates calls to it.
 * Subclasses add extra behaviour on top.
 */
public abstract class AlertDecorator implements AlertInterface {

    protected AlertInterface decoratedAlert;

    /**
     * Constructs a decorator wrapping the given alert.
     *
     * @param alert the alert to wrap
     */
    public AlertDecorator(AlertInterface alert) {
        this.decoratedAlert = alert;
    }

    @Override
    public String getPatientId() {
        return decoratedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }
}