package tech.nomad4;

import org.junit.jupiter.api.Test;
import tech.nomad4.dockersocketmanager.model.DockerSocketConfig;
import tech.nomad4.dockersocketmanager.model.SocketType;

import static org.junit.jupiter.api.Assertions.*;

class DockerSocketConfigTest {

    @Test
    void builder_setsAllFields() {
        DockerSocketConfig config = DockerSocketConfig.builder()
                .id(1L)
                .type(SocketType.LOCAL)
                .socketPath("/var/run/docker.sock")
                .sshHost("192.168.1.1")
                .sshPort(22)
                .sshUser("root")
                .sshPassword("secret")
                .sshPrivateKeyPath("/home/user/.ssh/id_rsa")
                .remoteDockerSocketPath("/var/run/docker.sock")
                .remoteSocatPort(2375)
                .build();

        assertEquals(1L, config.getId());
        assertEquals(SocketType.LOCAL, config.getType());
        assertEquals("/var/run/docker.sock", config.getSocketPath());
        assertEquals("192.168.1.1", config.getSshHost());
        assertEquals(22, config.getSshPort());
        assertEquals("root", config.getSshUser());
        assertEquals("secret", config.getSshPassword());
        assertEquals("/home/user/.ssh/id_rsa", config.getSshPrivateKeyPath());
        assertEquals("/var/run/docker.sock", config.getRemoteDockerSocketPath());
        assertEquals(2375, config.getRemoteSocatPort());
    }

    @Test
    void builder_nullSshPasswordDoesNotThrow() {
        assertDoesNotThrow(() -> DockerSocketConfig.builder()
                .id(1L)
                .type(SocketType.REMOTE_SSH)
                .sshHost("host")
                .sshPort(22)
                .sshUser("user")
                .sshPassword(null)
                .build());
    }

    @Test
    void builder_nullSshPrivateKeyPathDoesNotThrow() {
        assertDoesNotThrow(() -> DockerSocketConfig.builder()
                .id(1L)
                .type(SocketType.REMOTE_SSH)
                .sshHost("host")
                .sshPort(22)
                .sshUser("user")
                .sshPrivateKeyPath(null)
                .build());
    }
}
