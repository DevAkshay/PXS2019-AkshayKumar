package com.akshay.employeeattendance.network.DataSource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akshay.employeeattendance.data.Employee
import com.akshay.employeeattendance.data.EmployeeAttendance
import com.akshay.employeeattendance.network.ApiService
import java.io.IOException

class EmployeeListDataSourceImpl(private val apiService: ApiService) :
    EmployeeListDataSource {

    private val _downloadedEmployeeList =  MutableLiveData<List<Employee>>()
    private val _downloadedEmployeeAttendanceList = MutableLiveData<List<EmployeeAttendance>>()

    override val downloadedEmployeeList: LiveData<List<Employee>>
        get() = _downloadedEmployeeList

    override val downloadedEmployeeAttendanceList: LiveData<List<EmployeeAttendance>>
        get() = _downloadedEmployeeAttendanceList

    override suspend fun fetchEmployees() {
        try {
            val fetchEmployees = apiService.getEmployees().await()
            _downloadedEmployeeList.postValue(fetchEmployees)
        }
        catch (e: IOException) {
            Log.e("Connecting","No Internet")
        }
    }

    override suspend fun fetchEmployeeAttendance(employeeId : Int, fromDate : String, toDate : String) {
        try {
            val fetchEmployeeAttendance = apiService.getEmployeeAttendance(employeeId,fromDate,toDate).await()
            _downloadedEmployeeAttendanceList.postValue(fetchEmployeeAttendance)
        }
        catch (e: IOException) {
            Log.e("Connecting","No Internet")
        }
    }
}