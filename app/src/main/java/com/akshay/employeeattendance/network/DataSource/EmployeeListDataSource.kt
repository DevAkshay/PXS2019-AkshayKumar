package com.akshay.employeeattendance.network.DataSource

import androidx.lifecycle.LiveData
import com.akshay.employeeattendance.data.Employee
import com.akshay.employeeattendance.data.EmployeeAttendance

interface EmployeeListDataSource {

    val downloadedEmployeeList : LiveData<List<Employee>>
    val downloadedEmployeeAttendanceList : LiveData<List<EmployeeAttendance>>
    suspend fun fetchEmployees()
    suspend fun fetchEmployeeAttendance(employeeId : Int, fromDate : String, toDate : String)
}