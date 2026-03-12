package com.duanett.gymmanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltAndroidApp triggers Hilt's code generation at compile time,
 * creating the application-level DI component that all other components
 * in the app inherit from.
 *
 * Must be declared in AndroidManifest.xml via android:name=".GymApplication".
 */
@HiltAndroidApp
class GymApplication : Application()
