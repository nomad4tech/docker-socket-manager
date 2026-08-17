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
                .sshKeyPassphrase("keypass")
                .sshHostKeyFingerprint("SHA256:abcdefg")
                .remoteDockerSocketPath("/var/run/docker.sock")
                .remoteSocatPort(2375)
                .connectTimeoutMillis(5000)
                .readTimeoutMillis(15000)
                .build();

        assertEquals(1L, config.getId());
        assertEquals(SocketType.LOCAL, config.getType());
        assertEquals("/var/run/docker.sock", config.getSocketPath());
        assertEquals("192.168.1.1", config.getSshHost());
        assertEquals(22, config.getSshPort());
        assertEquals("root", config.getSshUser());
        assertEquals("secret", config.getSshPassword());
        assertEquals("/home/user/.ssh/id_rsa", config.getSshPrivateKeyPath());
        assertEquals("keypass", config.getSshKeyPassphrase());
        assertEquals("SHA256:abcdefg", config.getSshHostKeyFingerprint());
        assertEquals("/var/run/docker.sock", config.getRemoteDockerSocketPath());
        assertEquals(2375, config.getRemoteSocatPort());
        assertEquals(5000, config.getConnectTimeoutMillis());
        assertEquals(15000, config.getReadTimeoutMillis());
    }

    @Test
    void builder_newSecurityAndTimeoutFieldsDefaultToNull() {
        DockerSocketConfig config = DockerSocketConfig.builder()
                .id(1L)
                .type(SocketType.REMOTE_SSH)
                .sshHost("host")
                .sshPort(22)
                .sshUser("user")
                .build();

        assertNull(config.getSshKeyPassphrase());
        assertNull(config.getSshHostKeyFingerprint());
        assertNull(config.getConnectTimeoutMillis());
        assertNull(config.getReadTimeoutMillis());
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
