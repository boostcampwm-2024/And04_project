package com.and04.naturealbum.ui.utils

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class LocationHandler(
    private val context: Context,
) {

    private val client by lazy { LocationServices.getSettingsClient(context) }
    private val builder by lazy {
        LocationSettingsRequest.Builder().addLocationRequest(
            LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                Long.MAX_VALUE,
            ).build()
        )
    }
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            context
        )
    }

    fun checkLocationSettings(
        showGPSActivationDialog: (IntentSenderRequest) -> Unit,
        takePicture: () -> Unit,
        airPlaneModeMessage: () -> Unit
    ) {
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                takePicture()
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is ResolvableApiException -> {
                        resolveLocationSettings(exception, showGPSActivationDialog)
                    }

                    is ApiException -> {
                        airPlaneModeMessage()
                    }
                }
            }
    }

    private fun resolveLocationSettings(
        resolvable: ResolvableApiException,
        showGPSActivationDialog: (IntentSenderRequest) -> Unit,
    ) {
        val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()
        try {
            showGPSActivationDialog(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    fun getLocation(onSuccess: (Location?) -> Unit) {
        if (!checkPermission()) return
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                // TODO: location이 null로 오면?
                onSuccess(location)
            }
        } catch (e: NullPointerException) {
            onSuccess(null)
        }
    }

    private fun checkPermission(): Boolean {
        return listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).all { permission ->
            ActivityCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
