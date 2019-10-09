package com.akshay.employeeattendance.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.akshay.employeeattendance.data.Employee
import com.akshay.employeeattendance.data.EmployeeAttendance
import com.akshay.employeeattendance.network.Interceptor.ConnectivityInterceptor
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

const val API_KEY = "e76c37b493ea168cea60b8902072387caf297979"

interface ApiService {

    @GET("accounting/att_rprt_api.php?$API_KEY")
    fun getEmployees() : Deferred<List<Employee>>

    @FormUrlEncoded
    @POST("accounting/att_rprt_api.php?$API_KEY")
    fun getEmployeeAttendance(@Field("emp_id") EmpId: Int,
                              @Field("from_dt") FromDt : String,
                              @Field("to_dt") ToDt : String
    ) : Deferred<List<EmployeeAttendance>>

    companion object{
        operator fun invoke(connectivityInterceptor: ConnectivityInterceptor): ApiService {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                Log.e("URL ",""+request.url());

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://parxsys.com/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

}