package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import com.data_management.PatientRecord;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FileDataReader}.
 * Verifies correct parsing of output files into DataStorage.
 */
class FileDataReaderTest {

    private DataStorage storage;
    private FileDataReader reader;

    /**
     * Initialises a fresh DataStorage before each test.
     */
    @BeforeEach
    void setUp() {
        storage = new DataStorage();
    }

    /**
     * Verifies that a correctly formatted file is parsed and stored properly.
     *
     * @param tempDir a temporary directory created by JUnit for the test
     * @throws IOException if file creation or reading fails
     */
    @Test
    void testReadValidFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("ECG.txt");
        Files.writeString(file,
                "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5\n" +
                        "Patient ID: 1, Timestamp: 2000, Label: ECG, Data: 0.6\n");

        reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, 9999);
        assertEquals(2, records.size());
    }

    /**
     * Verifies that malformed lines are skipped without throwing an exception.
     *
     * @param tempDir a temporary directory created by JUnit for the test
     * @throws IOException if file creation or reading fails
     */
    @Test
    void testSkipsMalformedLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("ECG.txt");
        Files.writeString(file,
                "this is not valid\n" +
                        "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5\n");

        reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, 9999);
        assertEquals(1, records.size());
    }

    /**
     * Verifies that an empty file results in no records being stored.
     *
     * @param tempDir a temporary directory created by JUnit for the test
     * @throws IOException if file creation or reading fails
     */
    @Test
    void testEmptyFileProducesNoRecords(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("ECG.txt");
        Files.writeString(file, "");

        reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, 9999);
        assertEquals(0, records.size());
    }

    /**
     * Verifies that data from multiple files in the same directory is all loaded.
     *
     * @param tempDir a temporary directory created by JUnit for the test
     * @throws IOException if file creation or reading fails
     */
    @Test
    void testMultipleFilesAreRead(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("ECG.txt"),
                "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5\n");
        Files.writeString(tempDir.resolve("Saturation.txt"),
                "Patient ID: 1, Timestamp: 2000, Label: Saturation, Data: 95.0\n");

        reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, 9999);
        assertEquals(2, records.size());
    }
}