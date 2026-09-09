package com.appguard

import com.facebook.react.bridge.ReactApplicationContext

class AppGuardModule(reactContext: ReactApplicationContext) :
  NativeAppGuardSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeAppGuardSpec.NAME
  }
}
