package com.rudra.objectidentifier.core

import com.rudra.objectidentifier.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoProvider @Inject constructor() {

    fun getAppTitle(): String = "Real-Time Object Identifier"

    fun getVersionName(): String = BuildConfig.VERSION_NAME
}
