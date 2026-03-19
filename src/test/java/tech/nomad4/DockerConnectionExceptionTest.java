package tech.nomad4;

import org.junit.jupiter.api.Test;
import tech.nomad4.dockersocketmanager.exception.DockerConnectionException;

import static org.junit.jupiter.api.Assertions.*;

class DockerConnectionExceptionTest {

    @Test
    void messageIsPreserved() {
        DockerConnectionException ex = new DockerConnectionException("connection failed");
        assertEquals("connection failed", ex.getMessage());
    }

    @Test
    void causeIsPreserved() {
        RuntimeException cause = new RuntimeException("root cause");
        DockerConnectionException ex = new DockerConnectionException("connection failed", cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void messageIsPreservedWhenCauseProvided() {
        RuntimeException cause = new RuntimeException("root cause");
        DockerConnectionException ex = new DockerConnectionException("connection failed", cause);
        assertEquals("connection failed", ex.getMessage());
    }
}
