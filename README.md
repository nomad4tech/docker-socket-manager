# docker-socket-manager

[![Build](https://github.com/nomad4tech/docker-socket-manager/actions/workflows/publish.yml/badge.svg)](https://github.com/nomad4tech/docker-socket-manager/actions/workflows/publish.yml)
[![Maven](https://img.shields.io/badge/maven-0.1.0-blue)](https://github.com/nomad4tech/docker-socket-manager/packages/2924050)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> See it in action: [docker-socket-manager-demo](https://github.com/nomad4tech/docker-socket-manager-demo)

A Java library for connecting to Docker daemons over local Unix sockets or remote SSH tunnels with automatic socat relay management.

Built to be embedded in backend applications that need to manage Docker containers remotely - for example, triggering database backups, scaling services, or monitoring container state across multiple hosts.

## How it works

For **local** connections, the library connects directly to a Unix socket (e.g. `/var/run/docker.sock`).

For **remote SSH** connections, the library:
1. Establishes an SSH connection to the remote host
2. Checks if socat is already running on the configured port - starts it if not
3. Opens a local SSH port-forward tunnel
4. Connects the Docker client through the tunnel

All connections are pooled in memory and reused across calls. Dead connections are detected via Docker ping and replaced on the next request.

## Requirements

- Java 17+
- Docker daemon accessible (local socket or remote SSH)
- For SSH connections: `socat` installed on the remote host (`sudo apt-get install socat`)

## Installation

### Maven

```xml
<dependency>
    <groupId>tech.nomad4</groupId>
    <artifactId>docker-socket-manager</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'tech.nomad4:docker-socket-manager:0.1.0'
```

> Currently published to GitHub Packages and JitPack. See [Package registry setup](#package-registry-setup) below.

## Usage

### Local Docker socket

```java
DockerSocketConfig config = DockerSocketConfig.builder()
        .id(1L)
        .type(SocketType.LOCAL)
        .socketPath("/var/run/docker.sock")
        .build();

DockerSocketService service = new DockerSocketService();
DockerClient client = service.getClient(1L, config);

client.listContainersCmd().exec().forEach(c ->
        System.out.println(c.getId() + " " + c.getStatus())
);
```

### Remote Docker via SSH

```java
DockerSocketConfig config = DockerSocketConfig.builder()
        .id(2L)
        .type(SocketType.REMOTE_SSH)
        .sshHost("192.168.1.100")
        .sshPort(22)
        .sshUser("ubuntu")
        .sshPrivateKeyPath("/home/user/.ssh/id_rsa")   // or use sshPassword
        .remoteDockerSocketPath("/var/run/docker.sock")
        .remoteSocatPort(2375)
        .build();

DockerSocketService service = new DockerSocketService();
DockerClient client = service.getClient(2L, config);

client.listContainersCmd().exec().forEach(c ->
        System.out.println(c.getId() + " " + c.getStatus())
);
```

### Connection pool management

```java
// Check if a connection is alive
boolean alive = service.isAlive(2L);

// Manually disconnect a specific socket
service.disconnect(2L);

// Disconnect all (e.g. on application shutdown)
service.close();
```

### Detecting Docker environment

```java
// Check if running inside a Docker container
boolean inDocker = DockerEnvironmentDetector.isRunningInDocker();

// Check if Docker socket is accessible at default path
boolean socketAvailable = DockerEnvironmentDetector.isDockerSocketAvailable();

// Get environment-specific setup recommendations
String hint = DockerEnvironmentDetector.getSetupRecommendations();
```

## Running inside Docker

If your application itself runs in a container, mount the Docker socket:

```yaml
services:
  your-app:
    image: your-app:latest
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    group_add:
      - "${DOCKER_GROUP_ID:-986}"
```

Get the Docker group ID on the host and put it in `.env`:

```bash
echo "DOCKER_GROUP_ID=$(getent group docker | cut -d: -f3)" >> .env
```

> The fallback value `986` is a common default but varies by system - always set `DOCKER_GROUP_ID` explicitly in production.

> **Warning:** Mounting the Docker socket gives the container full access to the host's Docker daemon. Only do this in trusted environments.

## Configuration reference

| Field | Type | Description |
|---|---|---|
| `id` | `Long` | Pool key - unique identifier for this connection |
| `type` | `SocketType` | `LOCAL` or `REMOTE_SSH` |
| `socketPath` | `String` | Path to local Unix socket (LOCAL only) |
| `sshHost` | `String` | Remote host address (REMOTE_SSH only) |
| `sshPort` | `Integer` | SSH port, typically `22` (REMOTE_SSH only) |
| `sshUser` | `String` | SSH username (REMOTE_SSH only) |
| `sshPassword` | `String` | SSH password - use key auth in production |
| `sshPrivateKeyPath` | `String` | Path to private key file (preferred over password) |
| `remoteDockerSocketPath` | `String` | Docker socket path on remote host |
| `remoteSocatPort` | `Integer` | TCP port socat will listen on (or already listens on) |

## Package registry setup

### GitHub Packages

Requires a GitHub account and a personal access token with `read:packages` scope (GitHub → Settings → Developer settings → Personal access tokens).

Add the repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/nomad4tech/docker-socket-manager</url>
    </repository>
</repositories>
```

Add credentials to `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
```

### JitPack

No token or credentials required. Add the repository and use the JitPack `groupId`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.nomad4tech</groupId>
    <artifactId>docker-socket-manager</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Known limitations

- SSH host key verification is currently disabled (`PromiscuousVerifier`). Do not use in security-sensitive environments without adding `known_hosts` verification - this will be configurable in a future release.
- Only `LOCAL` and `REMOTE_SSH` connection types are supported. Direct TCP (`REMOTE_TCP`) and TLS are planned.
- No built-in health check scheduler - connection liveness is checked lazily on `getClient()`. Schedule `isAlive()` / `evict()` calls from your application layer if you need proactive monitoring.

## Dependencies

- [docker-java](https://github.com/docker-java/docker-java) - Docker API client
- [sshj](https://github.com/hierynomus/sshj) - SSH connections and port forwarding
- [Lombok](https://projectlombok.org/) - boilerplate reduction

## License

MIT