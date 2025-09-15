package org.notesapp.utils

enum class Platform {
  Android,
  Desktop,
  IOS
}

expect fun getPlatform(): Platform

fun isIOS(): Boolean = getPlatform() == Platform.IOS