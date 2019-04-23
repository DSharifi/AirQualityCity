package com.example.gruppe30in2000

class AQILevel{

    companion object {
        fun getAQILevel(aqiValue : Double) : Int {
            // Low = 1, Med =, High = 3
            if (aqiValue <= 1.6) {
                return 1
            } else if (aqiValue > 1.6 && aqiValue < 1.8) {
                return 2
            } else {
                return 3
            }
        }

        fun getAQILevelString(aqiValue : Double) : String {
            // Low = 1, Med =, High = 3
            if (aqiValue <= 1.6) {
                return "Lav"
            } else if (aqiValue > 1.6 && aqiValue < 1.8) {
                    return "Moderat"
                } else {
                return "Hoy"
            }
        }
    }

}
