package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for evaluating blood pressure alert conditions.
 * Checks for critical thresholds and increasing or decreasing trends.
 */
public class BloodPressureStrategy implements AlertStrategy {

    /**
     * Checks systolic and diastolic pressure records for critical values and trends.
     *
     * @param patient the patient to evaluate
     * @return a list of blood pressure alerts triggered
     */
    @Override
    public List<Alert> check(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        long now = System.currentTimeMillis();
        List<PatientRecord> records = patient.getRecords(0, now);

        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");

        AlertFactory factory = new BloodPressureAlertFactory();

        for (PatientRecord r : systolic) {
            if (r.getMeasurementValue() > 180)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        "Critical Systolic High", r.getTimestamp()));
            else if (r.getMeasurementValue() < 90)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        "Critical Systolic Low", r.getTimestamp()));
        }

        for (PatientRecord r : diastolic) {
            if (r.getMeasurementValue() > 120)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        "Critical Diastolic High", r.getTimestamp()));
            else if (r.getMeasurementValue() < 60)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        "Critical Diastolic Low", r.getTimestamp()));
        }

        checkTrend(patient, systolic, "Systolic", factory, alerts);
        checkTrend(patient, diastolic, "Diastolic", factory, alerts);

        return alerts;
    }

    /**
     * Checks for three consecutive readings each changing by more than 10 mmHg.
     *
     * @param patient the patient being evaluated
     * @param records the list of pressure records to check
     * @param type the pressure type label for the alert condition
     * @param factory the factory used to create alerts
     * @param alerts the list to add triggered alerts to
     */
    private void checkTrend(Patient patient, List<PatientRecord> records,
                            String type, AlertFactory factory, List<Alert> alerts) {
        for (int i = 2; i < records.size(); i++) {
            double diff1 = records.get(i - 1).getMeasurementValue() - records.get(i - 2).getMeasurementValue();
            double diff2 = records.get(i).getMeasurementValue() - records.get(i - 1).getMeasurementValue();
            if (diff1 > 10 && diff2 > 10)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        type + " Increasing Trend", records.get(i).getTimestamp()));
            else if (diff1 < -10 && diff2 < -10)
                alerts.add(factory.createAlert(String.valueOf(patient.getPatientId()),
                        type + " Decreasing Trend", records.get(i).getTimestamp()));
        }
    }

    /**
     * Filters records by record type.
     *
     * @param records the full list of records
     * @param type the type to filter by
     * @return filtered list
     */
    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> result = new ArrayList<>();
        for (PatientRecord r : records)
            if (r.getRecordType().equals(type)) result.add(r);
        return result;
    }
}