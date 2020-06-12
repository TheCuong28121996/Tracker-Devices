package com.tracker.devices

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TrackerService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        buildNotification()
        loginToFirebase()
    }

    private fun buildNotification() {
        val stop = "stop"
        registerReceiver(stopReceiver, IntentFilter(stop))

        val broadcastIntent =
            PendingIntent.getBroadcast(this, 0, Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_text))
            .setContentIntent(broadcastIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
        startForeground(1, builder.build())
    }

    protected var stopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            unregisterReceiver(this)
            stopSelf()
        }
    }

    private fun loginToFirebase() {
        val email = getString(R.string.firebase_email)
        val password = getString(R.string.firebase_password)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        Log.d("TrackerService", "firebase auth success");
                        requestLocationUpdates()
                    } else {
                        Log.d("TrackerService", "firebase auth success");
                    }
                }
            })
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest()
        request.setInterval(10000)
        request.setFastestInterval(5000)
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val client: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        val path = getString(R.string.firebase_path) + "/" + getString(R.string.transport_id)

        val permission: Int = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val ref = FirebaseDatabase.getInstance().getReference(path)
                    val location: Location? = locationResult.lastLocation
                    if (location != null) {
                        Log.d("TrackerService", "location update $location")
                        ref.setValue(location)
                    }
                }
            }, null)
        }
    }
}