package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for evaluating heart rate alert conditions.
 * Triggers an alert if heart rate falls below 60 or exceeds 100 bpm.
 */
public class HeartRateStrategy implements AlertStrategy {

    /**
     * Checks heart rate records against normal range thresholds.
     *
     * @param patient the patient to evaluate
     * @return a list of heart rate alerts triggered
     */
    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        long now = System.currentTimeMillis();
        List<PatientRecord> records = patient.getRecords(0, now);

        for (PatientRecord r : records) {
            if (r.getRecordType().equals("HeartRate")) {
                if (r.getMeasurementValue() < 60)
                    alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                            "Low Heart Rate: " + r.getMeasurementValue(), r.getTimestamp()));
                else if (r.getMeasurementValue() > 100)
                    alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                            "High Heart Rate: " + r.getMeasurementValue(), r.getTimestamp()));
            }
        }
        return alerts;
    }
}
