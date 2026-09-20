package com.appguard

import android.os.Build
import com.facebook.react.bridge.ReactApplicationContext
import java.io.File
import android.view.WindowManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.util.Log
import java.util.concurrent.Executors
import java.util.function.Consumer

class AppGuardModule(reactContext: ReactApplicationContext) :
  NativeAppGuardSpec(reactContext) {

  override fun isDeviceRooted(): Boolean {
    return checkRootBinaries() || checkTestKeys() || checkRootPackages()
  }

  override fun isEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT
    val model = Build.MODEL
    val manufacturer = Build.MANUFACTURER
    val brand = Build.BRAND
    val device = Build.DEVICE
    val product = Build.PRODUCT
    val hardware = Build.HARDWARE

    return fingerprint.contains("generic") ||
      fingerprint.contains("emulator") ||
      fingerprint.contains("goldfish") ||
      fingerprint.contains("ranchu") ||
      fingerprint.contains("emu") ||
      model.contains("Emulator", ignoreCase = true) ||
      model.contains("sdk", ignoreCase = true) ||
      model.contains("Android SDK built for", ignoreCase = true) ||
      manufacturer.contains("Genymotion", ignoreCase = true) ||
      (brand.startsWith("generic") && device.startsWith("generic")) ||
      hardware.contains("goldfish") ||
      hardware.contains("ranchu") ||
      hardware.contains("emulator") ||
      hardware.contains("qemu") ||
      product == "google_sdk" ||
      product.contains("sdk") ||
      device.contains("emu") ||
      device.startsWith("generic")
  }

override fun isScreenRecording(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      val windowManager = reactApplicationContext
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val executor = Executors.newSingleThreadExecutor()
      val callback = Consumer<Int> {}
      val state = runCatching {
        windowManager.addScreenRecordingCallback(executor, callback)
      }.getOrDefault(WindowManager.SCREEN_RECORDING_STATE_NOT_VISIBLE)
      windowManager.removeScreenRecordingCallback(callback)
      executor.shutdown()
      if (state == WindowManager.SCREEN_RECORDING_STATE_VISIBLE) {
        return true
      }
    }

    val displayManager = reactApplicationContext
      .getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val displays = displayManager.displays

    for (display in displays) {
      Log.d("AppGuard", "Display found -> id=${display.displayId}, name=${display.name}, flags=${display.flags}")
    }

    return displays.any { display ->
      display.displayId != Display.DEFAULT_DISPLAY &&
      (display.name.contains("Recording", ignoreCase = true) ||
       display.name.contains("cast", ignoreCase = true))
    }
}

  override fun setScreenshotBlocked(blocked: Boolean) {
    val activity = currentActivity ?: return
    activity.runOnUiThread {
      if (blocked) {
        activity.window.setFlags(
          WindowManager.LayoutParams.FLAG_SECURE,
          WindowManager.LayoutParams.FLAG_SECURE
        )
      } else {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
      }
    }
}

  // --- Root detection checks ---

  private fun checkRootBinaries(): Boolean {
    val paths = arrayOf(
      "/system/bin/su",
      "/system/xbin/su",
      "/sbin/su",
      "/system/su",
      "/system/bin/.ext/.su",
      "/system/usr/we-need-root/su-backup",
      "/system/xbin/mu"
    )
    return paths.any { File(it).exists() }
  }

  private fun checkTestKeys(): Boolean {
    return Build.TAGS != null && Build.TAGS.contains("test-keys")
  }

  private fun checkRootPackages(): Boolean {
    val rootPackages = arrayOf(
      "com.topjohnwu.magisk",
      "eu.chainfire.supersu",
      "com.noshufou.android.su",
      "com.koushikdutta.superuser",
      "com.thirdparty.superuser"
    )
    val pm = reactApplicationContext.packageManager
    return rootPackages.any {
      try {
        pm.getPackageInfo(it, 0)
        true
      } catch (e: Exception) {
        false
      }
    }
  }

  companion object {
    const val NAME = NativeAppGuardSpec.NAME
  }
}