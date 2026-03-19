# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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