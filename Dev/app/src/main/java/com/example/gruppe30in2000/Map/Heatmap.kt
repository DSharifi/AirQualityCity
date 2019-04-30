package com.example.gruppe30in2000.Map

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider


//Adds heatmap
// Skal man ta inn 3 forskjellige lister med kordinater som representerer rød, gul og grønn?
// Kan bli tungvindt hvis hele kartet må oppdateres hvis kun en stasjon skifter farge?

class Heatmap(googleMap: GoogleMap, listGreen: List<LatLng>, listYellow: List<LatLng>, listRed: List<LatLng>){
    init {
        val mMap = googleMap

        val greenHeat: IntArray = intArrayOf(Color.rgb(0, 255, 0))
        val yellowHeat: IntArray = intArrayOf(Color.rgb(255, 200, 25))
        val redHeat: IntArray = intArrayOf(Color.rgb(255, 0, 0))


        val startPoints: FloatArray = floatArrayOf(1f)

        val gradientGreen = Gradient(greenHeat, startPoints)
        val gradientYellow = Gradient(yellowHeat, startPoints)
        val gradientRed = Gradient(redHeat, startPoints)


        //Radius must be between 10 and 50, default is 20
        if (listGreen.isNotEmpty()) {
            val mProvider = HeatmapTileProvider.Builder()
                .data(listGreen)
                .radius(50)
                .gradient(gradientGreen)
                .build()

            mMap.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))
        }


        if (listYellow.isNotEmpty()) {
            val mProvider2 = HeatmapTileProvider.Builder()
                .data(listYellow)
                .radius(30)
                .gradient(gradientYellow)
                .build()

            mMap.addTileOverlay(TileOverlayOptions().tileProvider(mProvider2))

        }

        if (listRed.isNotEmpty()){
            val mProvider3 = HeatmapTileProvider.Builder()
                .data(listRed)
                .radius(20)
                .gradient(gradientRed)
                .build()

            mMap.addTileOverlay(TileOverlayOptions().tileProvider(mProvider3))

        }
    }
}