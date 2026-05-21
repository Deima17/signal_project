package data_management;

import com.alerts.Alert;
import com.alerts.AlertInterface;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Week 4 design patterns:
 * Factory Method, Strategy, Decorator, and Singleton.
 */
class DesignPatternsTest {

    private DataStorage storage;

    /**
     * Initialises a fresh DataStorage before each test.
     */
    @BeforeEach
    void setUp() {
        storage = new DataStorage();
    }

    //Factory

    /**
     * Verifies BloodPressureAlertFactory creates an alert with correct prefix.
     */
    @Test
    void testBloodPressureAlertFactory() {
        Alert alert = new BloodPressureAlertFactory().createAlert("1", "Critical High", 1000L);
        assertTrue(alert.getCondition().contains("BloodPressure"));
        assertEquals("1", alert.getPatientId());
        assertEquals(1000L, alert.getTimestamp());
    }

    /**
     * Verifies BloodOxygenAlertFactory creates an alert with correct prefix.
     */
    @Test
    void testBloodOxygenAlertFactory() {
        Alert alert = new BloodOxygenAlertFactory().createAlert("2", "Low Saturation", 2000L);
        assertTrue(alert.getCondition().contains("BloodOxygen"));
    }

    /**
     * Verifies ECGAlertFactory creates an alert with correct prefix.
     */
    @Test
    void testECGAlertFactory() {
        Alert alert = new ECGAlertFactory().createAlert("3", "Abnormal Peak", 3000L);
        assertTrue(alert.getCondition().contains("ECG"));
    }

    // Strategy

    /**
     * Verifies BloodPressureStrategy triggers alert on critical systolic high.
     */
    @Test
    void testBloodPressureStrategyHighSystolic() {
        storage.addPatientData(1, 185, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        List<Alert> alerts = new BloodPressureStrategy().checkAlert(patient);
        assertTrue(alerts.stream().anyMatch(a -> a.getCondition().contains("Critical Systolic High")));
    }

    /**
     * Verifies BloodPressureStrategy returns no alerts for normal readings.
     */
    @Test
    void testBloodPressureStrategyNoAlert() {
        storage.addPatientData(1, 120, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        List<Alert> alerts = new BloodPressureStrategy().checkAlert(patient);
        assertTrue(alerts.isEmpty());
    }

    /**
     * Verifies OxygenSaturationStrategy triggers alert on low saturation.
     */
    @Test
    void testOxygenSaturationStrategyLow() {
        storage.addPatientData(1, 89, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        List<Alert> alerts = new OxygenSaturationStrategy().checkAlert(patient);
        assertTrue(alerts.stream().anyMatch(a -> a.getCondition().contains("Low Saturation")));
    }

    /**
     * Verifies HeartRateStrategy triggers alert on high heart rate.
     */
    @Test
    void testHeartRateStrategyHigh() {
        storage.addPatientData(1, 110, "HeartRate", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient);
        assertTrue(alerts.stream().anyMatch(a -> a.getCondition().contains("High Heart Rate")));
    }

    /**
     * Verifies HeartRateStrategy triggers alert on low heart rate.
     */
    @Test
    void testHeartRateStrategyLow() {
        storage.addPatientData(1, 45, "HeartRate", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient);
        assertTrue(alerts.stream().anyMatch(a -> a.getCondition().contains("Low Heart Rate")));
    }

    /**
     * Verifies HeartRateStrategy returns no alerts for normal heart rate.
     */
    @Test
    void testHeartRateStrategyNormal() {
        storage.addPatientData(1, 75, "HeartRate", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient);
        assertTrue(alerts.isEmpty());
    }

    // Decorator
    /**
     * Verifies PriorityAlertDecorator prepends priority level to condition.
     */
    @Test
    void testPriorityAlertDecorator() {
        AlertInterface base = new Alert("1", "Low Saturation", 1000L);
        AlertInterface decorated = new PriorityAlertDecorator(base, "CRITICAL");
        assertTrue(decorated.getCondition().startsWith("[CRITICAL]"));
        assertEquals("1", decorated.getPatientId());
        assertEquals(1000L, decorated.getTimestamp());
    }

    /**
     * Verifies RepeatedAlertDecorator appends repeat interval to condition.
     */
    @Test
    void testRepeatedAlertDecorator() {
        AlertInterface base = new Alert("1", "High Blood Pressure", 1000L);
        AlertInterface decorated = new RepeatedAlertDecorator(base, 30);
        assertTrue(decorated.getCondition().contains("[Repeat every 30s]"));
    }

    /**
     * Verifies decorators can be stacked on top of each other.
     */
    @Test
    void testStackedDecorators() {
        AlertInterface base = new Alert("1", "ECG Abnormal", 1000L);
        AlertInterface priority = new PriorityAlertDecorator(base, "HIGH");
        AlertInterface repeated = new RepeatedAlertDecorator(priority, 60);
        assertTrue(repeated.getCondition().contains("[HIGH]"));
        assertTrue(repeated.getCondition().contains("[Repeat every 60s]"));
    }

    // Singleton
    /**
     * Verifies DataStorage getInstance always returns the same instance.
     */
    @Test
    void testDataStorageSingleton() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertSame(instance1, instance2);
    }
}
