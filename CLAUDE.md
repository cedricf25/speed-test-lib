# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JSpeedTest is a speed test client library for Android (API 21+, targeting Android 35). It supports HTTP/HTTPS/FTP protocols for download and upload speed measurements with progress monitoring, proxy support, and repeated operations.

**Version**: 2.0.0 (Android Library)

## Build Commands

```bash
# Build with tests
./gradlew clean build

# Build without tests
./gradlew clean build -x test

# Build AAR library
./gradlew :jspeedtest:assembleRelease

# Run unit tests
./gradlew :jspeedtest:testDebugUnitTest

# Generate Javadoc
./gradlew :jspeedtest:generateJavadoc

# Run examples (JVM only)
./gradlew :examples:downloadFile      # HTTP download
./gradlew :examples:uploadFile        # HTTP upload
./gradlew :examples:downloadFTP       # FTP download
./gradlew :examples:uploadFTP         # FTP upload
./gradlew :examples:fixedDownload     # Time-bounded download
./gradlew :examples:repeatDownload    # Repeated downloads
```

## Architecture

### Module Structure

- **jspeedtest/** - Core Android library module (AAR)
- **examples/** - Example applications demonstrating API usage (JVM)

### Android Configuration

| Setting | Value |
|---------|-------|
| compileSdk | 35 |
| targetSdk | 35 |
| minSdk | 21 |
| Java Version | 17 |
| Gradle | 8.5 |
| Android Gradle Plugin | 8.2.2 |

### Core Components

| Class | Purpose |
|-------|---------|
| `SpeedTestSocket` | Main entry point implementing `ISpeedTestSocket`. Manages download/upload operations with listener pattern. |
| `SpeedTestTask` | Core transfer logic. Handles socket connections, data transfer, rate calculations for HTTP/HTTPS/FTP. |
| `SpeedTestReport` | Immutable data class containing transfer metrics (rate in octets/s and bits/s, progress %, timestamps). |
| `RepeatWrapper` | Enables chained/repeated operations with accumulated transfer rates. |

### HTTP Package (fr.bmartel.speedtest.http)

Local implementation replacing the deprecated `http-endec` library:

| Class | Purpose |
|-------|---------|
| `HttpFrame` | HTTP request/response parser |
| `HttpResponseFrame` | HTTP response builder |
| `HttpStates` | Enum for HTTP parsing states |
| `HttpVersion` | Enum for HTTP versions |
| `StatusCodeList` | Common HTTP status codes |
| `HttpHeader` | HTTP header constants |

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

- `commons-net:commons-net:3.10.0` - FTP support via Apache Commons Net

### Android Permissions

The library declares these permissions in its manifest:
- `android.permission.INTERNET` - Required for network operations
- `android.permission.ACCESS_NETWORK_STATE` - Required to check network connectivity

### Network Security Configuration

A `network_security_config.xml` is provided with cleartext traffic disabled by default. Common speed test servers are whitelisted for HTTP access.

## Code Quality

PMD rules are configured in `jspeedtest/config/pmd/rulesets.xml`. Key enabled rules: unused code detection, naming conventions, exception handling, and code structure checks.

## Usage in Android App

Add to your app's `build.gradle`:

```groovy
dependencies {
    implementation project(':jspeedtest')
    // or from Maven Central when published
    // implementation 'fr.bmartel:jspeedtest:2.0.0'
}
```

Reference the network security config in your `AndroidManifest.xml`:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
```
