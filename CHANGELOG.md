# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2026-08-18

### Fixed
- `OkDockerHttpClient` had no connect/read timeout configured - the underlying transport
  defaults `readTimeout` to `0` (unbounded) when unset, so any Docker API call over a
  pooled `DockerClient` (local or SSH) could hang forever if the connection had silently
  gone dead, instead of failing fast. Both client creation sites (`connectLocal`,
  `createDockerClient`) now set explicit timeouts - configurable via
  `DockerSocketConfig.connectTimeoutMillis`/`readTimeoutMillis`, defaulting to 10s/30s
  (`DockerSocketService.DEFAULT_CONNECT_TIMEOUT_MILLIS`/`DEFAULT_READ_TIMEOUT_MILLIS`).

### Added
- `DockerSocketConfig.sshHostKeyFingerprint` - when set, SSH host key is verified against
  this fingerprint (`ssh-keygen -lf` format) via sshj's `FingerprintVerifier`. Falls back
  to `PromiscuousVerifier` (accept any key, logged as a warning) when unset, for backward
  compatibility.
- `DockerSocketConfig.sshKeyPassphrase` - passphrase support for encrypted private keys at
  `sshPrivateKeyPath` (previously silently unsupported - `loadKeys()` was always called
  with no passphrase overload).

---

## [0.1.1] - 2026-04-19

### Fixed
- `DockerEnvironmentDetector.isDockerSocketAvailable()` incorrectly returned `false` for Unix sockets - `File.canRead()` and `File.canWrite()` do not work correctly for Unix socket files and do not account for group-based permissions. Replaced with `Files.readAttributes()` + `BasicFileAttributes.isOther()` which correctly identifies Unix sockets by file type.

---

## [0.1.0] - 2026-03-19

### Added
- `DockerSocketService` - connection pool for local and remote Docker sockets
- `LOCAL` connection type via Unix socket
- `REMOTE_SSH` connection type via SSH tunnel with automatic socat relay management
- Automatic socat process lifecycle - starts if not running, kills on disconnect
- Connection health check via Docker ping (`isAlive`, `evict`)
- `DockerEnvironmentDetector` - detects Docker container environment and socket availability
- `DockerSocketConfig` - value object for connection parameters (builder API)
- `DockerConnectionException` - typed exception for connection failures