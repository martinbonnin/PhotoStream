package net.mbonnin.photostream

import android.app.Application

class MainApplication : Application() {
    lateinit var locationMonitor: LocationMonitor

    override fun onCreate() {
        super.onCreate()

        instance = this

        // This could use some dependency injection
        locationMonitor = LocationMonitor(this)
    }

    companion object {
        lateinit var instance: MainApplication
    }
}