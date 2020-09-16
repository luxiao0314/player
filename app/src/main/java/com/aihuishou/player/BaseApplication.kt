package com.aihuishou.player

import android.app.Application
import androidx.multidex.MultiDexApplication

class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: Application
    }
}
