package com.appguard

import android.os.Build
import com.facebook.react.bridge.ReactApplicationContext
import java.io.File
import android.view.WindowManager

class AppGuardModule(reactContext: ReactApplicationContext) :
  NativeAppGuardSpec(reactContext) {

  override fun isDeviceRooted(): Boolean {
    return checkRootBinaries() || checkTestKeys() || checkRootPackages()
  }

  override fun isScreenRecording(): Boolean {
    // Step 6 mein implement karenge
    return false
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