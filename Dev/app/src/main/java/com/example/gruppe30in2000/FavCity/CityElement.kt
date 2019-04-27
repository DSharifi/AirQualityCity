package com.example.gruppe30in2000.FavCity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.gruppe30in2000.API.No2LocalFractionHeating


class CityElement(title: String, description: String, o3svevestov : String, o3value : Double, nOVal : Double,
                  nOunit : String, pm10val : Double, pm10Unit : String, nitHeating: Int, nitShip : Int, nitInd : Int,
                  nitExc : Int, pmHeat : Int, pmShip : Int, pmInd : Int, pmExc : Int, pmNonEx : Int) {

    var title = title
    var description = description
    var ozonUnit = o3svevestov
    var ozvalue = o3value
    var nOVal = nOVal
    var nOunit = nOunit
    var pm10val = pm10val
    var pm10Unit  = pm10Unit
    var nitShip = nitShip
    var nitHeating = nitHeating
    var nitInd = nitInd
    var nitExc = nitExc
    var pmHeat = pmHeat
    var pmShip = pmShip
    var pmInd = pmInd
    var pmExc = pmExc
    var pmNonEx = pmNonEx

}
