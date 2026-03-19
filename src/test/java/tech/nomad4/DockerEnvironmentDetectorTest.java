package tech.nomad4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.nomad4.dockersocketmanager.util.DockerEnvironmentDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class DockerEnvironmentDetectorTest {

    @Test
    void isDockerSocketAvailable_returnsFalseForNonExistentPath() {
        assertFalse(DockerEnvironmentDetector.isDockerSocketAvailable("/nonexistent/path/docker.sock"));
    }

    @Test
    void isDockerSocketAvailable_returnsFalseForPathWithoutReadPermission(@TempDir Path tempDir) throws IOException {
        // Skip this test when running as root, since root bypasses file permission checks
        assumeFalse("root".equals(System.getProperty("user.name")), "Skipping permission test when running as root");

        File tempFile = tempDir.resolve("docker.sock").toFile();
        tempFile.createNewFile();
        tempFile.setReadable(false);
        tempFile.setWritable(false);

        assertFalse(DockerEnvironmentDetector.isDockerSocketAvailable(tempFile.getAbsolutePath()));
    }
}
