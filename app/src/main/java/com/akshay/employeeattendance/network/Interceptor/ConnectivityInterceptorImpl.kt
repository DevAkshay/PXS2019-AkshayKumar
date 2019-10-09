package com.akshay.employeeattendance.network.Interceptor

import android.content.Context
import com.akshay.employeeattendance.network.Connectivity
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptorImpl(context : Context) :
    ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!Connectivity.isConnectedToInternet(appContext))
            throw IOException()
        return chain.proceed(chain.request())
    }
}