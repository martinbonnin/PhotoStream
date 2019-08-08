package net.mbonnin.photostream

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.mbonnin.photostream.MainService.Companion.stop

class MainActivity : AppCompatActivity() {
    val locationMonitor = MainApplication.instance.locationMonitor

    val adapter = MainAdapter()
    var updateAdapterJob: Job? = null


    fun startTracking() {
        locationMonitor.start()

        button.setText(R.string.stop)
        updateAdapterJob = GlobalScope.launch(Dispatchers.Main) {
            locationMonitor.photos().collect {
                if (it.isEmpty()) {
                    adapter.setItems(listOf(MainAdapter.Item.Text(getString(R.string.starting))))
                } else {
                    val items = it.map {
                        MainAdapter.Item.Image(it)
                    }
                    adapter.setItems(items)
                }
            }
        }

        MainService.start(this)
    }

    fun startTrackingIfPermissions() {
        val hasPermission = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            startTracking()
        }
    }

    fun stopTracking() {
        locationMonitor.stop()

        button.setText(R.string.start)
        adapter.setItems(listOf(MainAdapter.Item.Text(getString(R.string.press_start))))
        updateAdapterJob?.cancel()
        updateAdapterJob = null

        MainService.stop(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setText(R.string.start)
        adapter.setItems(listOf(MainAdapter.Item.Text(getString(R.string.press_start))))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        button.setOnClickListener {
            if (locationMonitor.isRunning()) {
                stopTracking()
            } else {
                startTrackingIfPermissions()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackingIfPermissions()
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 42
    }
}
