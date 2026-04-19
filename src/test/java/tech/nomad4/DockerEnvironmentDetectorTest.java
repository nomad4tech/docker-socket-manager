package tech.nomad4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.nomad4.dockersocketmanager.util.DockerEnvironmentDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

class DockerEnvironmentDetectorTest {

    @Test
    void isDockerSocketAvailable_returnsFalseForNonExistentPath() {
        assertFalse(DockerEnvironmentDetector.isDockerSocketAvailable("/nonexistent/path/docker.sock"));
    }

    @Test
    void isDockerSocketAvailable_returnsFalseForRegularFile(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("docker.sock").toFile();
        tempFile.createNewFile();
        assertFalse(DockerEnvironmentDetector.isDockerSocketAvailable(tempFile.getAbsolutePath()));
    }
}