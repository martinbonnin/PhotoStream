package net.mbonnin.photostream

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class LocationMonitor(val context: Context) {

    private var job: Job? = null

    fun isRunning(): Boolean {
        return job != null
    }

    val database by lazy {
        val driver = AndroidSqliteDriver(Database.Schema, context, "photo.db")
        Database(driver)
    }

    fun start() {
        if (job != null) {
            return
        }


        job = GlobalScope.launch {
            var lastLocation: Location? = null

            // For now delete everything at startup as it's easier to test
            database.photoQueries.delete()

            locations(context).flowOn(Dispatchers.Main).collect {
                if (it.accuracy > 50) {
                    return@collect
                }

                if (lastLocation != null) {
                    val d = lastLocation!!.distanceTo(it)
                    if (d < 100) {
                        // we're too close from the last location
                        Log.d("LocationMonitor", "too close: $d")
                        return@collect
                    }
                }

                val url = FlickrApi.search(it.latitude, it.longitude)
                if (url != null) {
                    database.photoQueries.insert(url, System.currentTimeMillis())
                    lastLocation = it
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun locations(context: Context) = callbackFlow<Location> {

        val listener = object : LocationListener {
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }

            override fun onProviderEnabled(p0: String?) {
            }

            override fun onProviderDisabled(p0: String?) {
            }

            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    Log.d(
                        "LocationMonitor",
                        "accuracy=${location.accuracy} latitude=${location.latitude} longitude=${location.longitude}"
                    )
                    offer(location)
                }
            }
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            0.0f, // I tried putting 100m there but then we don't receive updates when the accuracy changes
            listener
        )

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    fun stop() {
        if (job == null) {
            return
        }

        job?.cancel()
        job = null
    }

    /**
     * returns a Flow of lists of urls to the pictures
     */
    fun photos(): Flow<List<String>> {
        return database.photoQueries.selectAll().asFlow().mapToList()
    }
}