# react-native-app-guard

Native device integrity and tamper-detection toolkit for React Native. Detects rooted/jailbroken devices, emulators, and screen recording — and lets you block screenshots on sensitive screens. Built for apps handling payments, KYC, or other sensitive data.

Built using TurboModules (React Native's New Architecture) — Kotlin on Android, Objective-C++ on iOS.

## Why this exists

Apps that handle sensitive data (payments, banking, KYC, personal health info) face real risks from:
- **Rooted/jailbroken devices** — where the OS sandbox is broken, letting malicious apps read other apps' data or tamper with app behavior
- **Emulators** — commonly used at scale for fraud (fake sign-ups, bonus abuse, automated attacks)
- **Screen recording/casting** — which can leak sensitive on-screen data (OTPs, card numbers, balances)
- **Screenshots** — of the same sensitive content

This package gives you simple methods to detect these conditions and react accordingly — show a warning, block a feature, or hide sensitive UI.

## Installation

```sh
npm install react-native-app-guard
```

## Usage

```typescript
import AppGuard from 'react-native-app-guard';

// Check if the device is rooted/jailbroken
if (AppGuard.isDeviceRooted()) {
  // e.g. block payment features, show a warning
}

// Check if running on an emulator/simulator
if (AppGuard.isEmulator()) {
  // e.g. skip sign-up bonuses, flag for review
}

// Check if the screen is currently being recorded/mirrored
if (AppGuard.isScreenRecording()) {
  // e.g. hide sensitive fields
}

// Block screenshots on a sensitive screen
AppGuard.setScreenshotBlocked(true);

// Re-enable screenshots when leaving the sensitive screen
AppGuard.setScreenshotBlocked(false);
```

## API

### `isDeviceRooted(): boolean`

Returns `true` if the device is likely rooted (Android) or jailbroken (iOS).

**Android:** Checks for common `su` binary paths, non-release build tags (`test-keys`), and known root-manager apps (Magisk, SuperSU).

**iOS:** Checks for known jailbreak-related file paths (Cydia, MobileSubstrate) and attempts a sandbox-violation write test.

> **Limitation:** These are best-effort checks. Advanced root/jailbreak-hiding tools (e.g. Magisk Hide/Zygisk) can bypass them. For production fintech-grade protection, combine with Google Play Integrity API (Android) / DeviceCheck (iOS).

### `isEmulator(): boolean`

Returns `true` if the app is running on an emulator (Android) or the Simulator (iOS).

**Android:** Checks `Build` fingerprint, model, manufacturer, brand, device, hardware (`goldfish`/`ranchu`/`qemu`), and product against known emulator signatures.

**iOS:** Uses the `TARGET_OS_SIMULATOR` compile-time flag — a reliable, Apple-provided check.

### `isScreenRecording(): boolean`

Returns `true` if the screen is currently being recorded, mirrored, or cast.

**iOS:** Uses `UIScreen.main.isCaptured` — an official, reliable Apple API.

**Android:** Checks for the presence of an extra virtual display (beyond the device's own default display) with a name matching known recording/casting patterns.

> **Limitation:** Android provides no official public API for this. Detection relies on display-name heuristics observed during testing and may not catch all recording/casting tools or OS versions.

### `setScreenshotBlocked(blocked: boolean): void`

Enables or disables screenshot/recording protection for the current screen.

**Android:** Sets/clears `FLAG_SECURE` on the current window. This fully blocks screenshots, screen recordings, and even the app's thumbnail in the recent-apps switcher.

**iOS:** Apple does not allow apps to block the OS-level screenshot gesture. Instead, this listens for `UIScreenCapturedDidChangeNotification` and overlays a black view over the app's content whenever screen recording/mirroring is detected — so recorded/mirrored output shows nothing, even though a manual screenshot itself cannot be prevented.

> **Limitation:** This is per-activity (Android) / global overlay (iOS) — apply it on the specific screens that show sensitive content, and disable it when leaving them.

## Example app

See the [`example`](./example) folder for a working demo with test buttons for each method.

## Contributing

See the [contributing guide](CONTRIBUTING.md).

## License

MIT