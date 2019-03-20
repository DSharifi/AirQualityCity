package com.example.gruppe30in2000

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log



class LocationPermission(val activity: Activity){

    private val mActivity = activity

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1


        fun getLocationPermissionStatus(): Int {
            return REQUEST_LOCATION_PERMISSION
        }


    }

    fun enableMyLocation() {
        ActivityCompat.requestPermissions(
            mActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
        )
    }



}





