# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JSpeedTest is a speed test client library for Java/Android (Java 1.7+). It supports HTTP/HTTPS/FTP protocols for download and upload speed measurements with progress monitoring, proxy support, and repeated operations.

## Build Commands

```bash
# Build with tests
./gradlew clean build

# Build without tests
./gradlew clean build -x test

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "fr.bmartel.speedtest.test.SpeedTestFunctionalTest"

# Generate Javadoc
./gradlew generateJavadoc

# Run examples
./gradlew downloadFile      # HTTP download
./gradlew uploadFile        # HTTP upload
./gradlew downloadFTP       # FTP download
./gradlew uploadFTP         # FTP upload
./gradlew fixedDownload     # Time-bounded download
./gradlew repeatDownload    # Repeated downloads
```

## Architecture

### Module Structure

- **jspeedtest/** - Core library module
- **examples/** - Example applications demonstrating API usage

### Core Components

| Class | Purpose |
|-------|---------|
| `SpeedTestSocket` | Main entry point implementing `ISpeedTestSocket`. Manages download/upload operations with listener pattern. |
| `SpeedTestTask` | Core transfer logic. Handles socket connections, data transfer, rate calculations for HTTP/HTTPS/FTP. |
| `SpeedTestReport` | Immutable data class containing transfer metrics (rate in octets/s and bits/s, progress %, timestamps). |
| `RepeatWrapper` | Enables chained/repeated operations with accumulated transfer rates. |

### Key Interfaces

- **ISpeedTestSocket** - Main API: `startDownload()`, `startUpload()`, `startFixedDownload/Upload()`, `startDownloadRepeat/UploadRepeat()`, `getLiveReport()`
- **ISpeedTestListener** - Progress callbacks: `onCompletion()`, `onProgress()`, `onError()`
- **IRepeatListener** - Repeat operation callbacks: `onCompletion()`, `onReport()`

### Enums

- `SpeedTestMode` - NONE, DOWNLOAD, UPLOAD
- `SpeedTestError` - Error types (SOCKET_ERROR, SOCKET_TIMEOUT, CONNECTION_ERROR, etc.)
- `FtpMode` - PASSIVE (default), ACTIVE
- `UploadStorageType` - RAM_STORAGE (default), FILE_STORAGE

### Dependencies

- `fr.bmartel:http-endec:1.04` - HTTP encoding/decoding
- `commons-net:commons-net:3.6` - FTP support via Apache Commons Net

## Code Quality

PMD rules are configured in `jspeedtest/config/pmd/rulesets.xml`. Key enabled rules: unused code detection, naming conventions, exception handling, and code structure checks.
