package com.akshay.employeeattendance.network

import android.content.Context
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
class Connectivity {

    companion object Connection{

         fun isConnectedToInternet(context: Context) : Boolean {
            val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val  networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}