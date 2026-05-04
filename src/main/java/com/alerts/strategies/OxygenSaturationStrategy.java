package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for evaluating blood oxygen saturation alert conditions.
 * Checks for low saturation and rapid drops within 10 minutes.
 */
public class OxygenSaturationStrategy implements AlertStrategy {

    /**
     * Checks saturation records for low values and rapid drops.
     *
     * @param patient the patient to evaluate
     * @return a list of oxygen saturation alerts triggered
     */
    @Override
    public List<Alert> check(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60 * 1000;

        List<PatientRecord> records = patient.getRecords(0, now);
        List<PatientRecord> satRecords = filterByType(records, "Saturation");

        AlertFactory factory = new BloodOxygenAlertFactory();

        for (PatientRecord r : satRecords) {
            if (r.getMeasurementValue() < 92)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        "Low Saturation: " + r.getMeasurementValue(), r.getTimestamp()));
        }

        List<PatientRecord> recent = filterByTimeRange(satRecords, tenMinutesAgo, now);
        if (recent.size() >= 2) {
            double earliest = recent.get(0).getMeasurementValue();
            double latest = recent.get(recent.size() - 1).getMeasurementValue();
            if (earliest - latest >= 5)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        "Rapid Saturation Drop", recent.get(recent.size() - 1).getTimestamp()));
        }

        return alerts;
    }

    /**
     * Filters records by type.
     */
    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord r : records)
            if (r.getRecordType().equals(type)) result.add(r);
        return result;
    }

    /**
     * Filters records by time range.
     */
    private List<PatientRecord> filterByTimeRange(List<PatientRecord> records, long start, long end) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord r : records)
            if (r.getTimestamp() >= start && r.getTimestamp() <= end) result.add(r);
        return result;
    }
}