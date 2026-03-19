package tech.nomad4;

import com.github.dockerjava.api.DockerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.nomad4.dockersocketmanager.connection.DockerConnection;
import tech.nomad4.dockersocketmanager.model.DockerSocketConfig;
import tech.nomad4.dockersocketmanager.model.SocketType;
import tech.nomad4.dockersocketmanager.service.DockerSocketService;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DockerSocketServiceTest {

    private DockerSocketService service;

    @Mock
    private DockerConnection mockConnection;

    @Mock
    private DockerClient mockDockerClient;

    @BeforeEach
    void setUp() {
        service = new DockerSocketService();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, DockerConnection> activeConnections() throws Exception {
        Field field = DockerSocketService.class.getDeclaredField("activeConnections");
        field.setAccessible(true);
        return (Map<Long, DockerConnection>) field.get(service);
    }

    @Test
    void getClient_returnsClientWhenConnectionIsAlive() throws Exception {
        when(mockConnection.isAlive()).thenReturn(true);
        when(mockConnection.getClient()).thenReturn(mockDockerClient);
        activeConnections().put(1L, mockConnection);

        DockerClient result = service.getClient(1L, minimalConfig());

        assertSame(mockDockerClient, result);
        verify(mockConnection).isAlive();
        verify(mockConnection).getClient();
    }

    @Test
    void getClient_reconnectsWhenIsAliveReturnsFalse() throws Exception {
        when(mockConnection.isAlive()).thenReturn(false);
        activeConnections().put(1L, mockConnection);

        DockerSocketConfig config = DockerSocketConfig.builder()
                .id(1L)
                .type(SocketType.LOCAL)
                .socketPath("/nonexistent/docker.sock")
                .build();

        // connect() will fail for the non-existent socket — the important assertion
        // is that isAlive() was called on the existing connection before attempting reconnect
        assertThrows(Exception.class, () -> service.getClient(1L, config));
        verify(mockConnection).isAlive();
    }

    @Test
    void disconnect_removesConnectionFromPool() throws Exception {
        activeConnections().put(1L, mockConnection);

        service.disconnect(1L);

        assertFalse(activeConnections().containsKey(1L));
        verify(mockConnection).close();
    }

    @Test
    void disconnect_doesNothingWhenConnectionAbsent() {
        assertDoesNotThrow(() -> service.disconnect(999L));
    }

    @Test
    void isAlive_returnsFalseWhenNoConnectionExistsForId() {
        assertFalse(service.isAlive(999L));
    }

    @Test
    void isAlive_returnsTrueWhenConnectionIsAlive() throws Exception {
        when(mockConnection.isAlive()).thenReturn(true);
        activeConnections().put(1L, mockConnection);

        assertTrue(service.isAlive(1L));
    }

    @Test
    void isAlive_returnsFalseWhenConnectionIsNotAlive() throws Exception {
        when(mockConnection.isAlive()).thenReturn(false);
        activeConnections().put(1L, mockConnection);

        assertFalse(service.isAlive(1L));
    }

    private DockerSocketConfig minimalConfig() {
        return DockerSocketConfig.builder()
                .id(1L)
                .type(SocketType.LOCAL)
                .socketPath("/var/run/docker.sock")
                .build();
    }
}
