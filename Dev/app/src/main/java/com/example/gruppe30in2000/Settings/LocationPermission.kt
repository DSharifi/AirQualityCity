package com.example.gruppe30in2000.Settings

import android.Manifest
import android.app.Activity
import android.support.v4.app.ActivityCompat


class LocationPermission(activity: Activity){

    private val mActivity = activity

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
    }

    fun enableMyLocation() {
        ActivityCompat.requestPermissions(
            mActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

}





