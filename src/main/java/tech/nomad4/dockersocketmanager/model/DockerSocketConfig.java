package tech.nomad4.dockersocketmanager.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Value object containing all parameters needed to establish a Docker socket connection.
 * <p>
 * Passed by the application layer to {@link tech.nomad4.dockersocketmanager.service.DockerSocketService}
 * so that the core socket package has no dependency on the database or JPA.
 * </p>
 */
@Getter
@Builder
public class DockerSocketConfig {

    private final Long id;
    private final SocketType type;

    /** Path to the local Docker Unix socket. Used for {@link SocketType#LOCAL} connections. */
    private final String socketPath;

    // --- SSH connection settings (for REMOTE_SSH) ---

    private final String sshHost;
    private final Integer sshPort;
    private final String sshUser;
    private final String sshPassword;
    private final String sshPrivateKeyPath;

    /** Passphrase for an encrypted private key at {@link #sshPrivateKeyPath}. Optional. */
    private final String sshKeyPassphrase;

    /**
     * Expected SSH host key fingerprint (the format produced by {@code ssh-keygen -lf},
     * e.g. {@code "SHA256:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}).
     * <p>
     * When set, the host key is verified against this single fingerprint instead of
     * accepting any key. When left unset, host key verification is skipped entirely
     * (MITM risk — see {@link tech.nomad4.dockersocketmanager.service.DockerSocketService}).
     */
    private final String sshHostKeyFingerprint;

    /** Path to the Docker Unix socket on the remote host. */
    private final String remoteDockerSocketPath;

    /** TCP port that socat listens on for the remote host. */
    private final Integer remoteSocatPort;

    /**
     * Connect timeout for the underlying Docker HTTP client, in milliseconds.
     * Defaults to {@code DockerSocketService.DEFAULT_CONNECT_TIMEOUT_MILLIS} when unset.
     */
    private final Integer connectTimeoutMillis;

    /**
     * Read timeout for a single Docker HTTP client request/response, in milliseconds.
     * Defaults to {@code DockerSocketService.DEFAULT_READ_TIMEOUT_MILLIS} when unset.
     * <p>
     * Applies per-call, including to streaming endpoints (e.g. log follow, exec attach) —
     * do not set this lower than the longest expected idle gap between bytes on such a
     * stream, or set it explicitly per-config for connections that use them.
     */
    private final Integer readTimeoutMillis;
}
