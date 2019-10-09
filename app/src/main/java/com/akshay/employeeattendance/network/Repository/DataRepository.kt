package com.akshay.employeeattendance.network.Repository

import androidx.lifecycle.LiveData
import com.akshay.employeeattendance.data.Employee
import com.akshay.employeeattendance.data.EmployeeAttendance

interface DataRepository {

    suspend fun getEmployees() : LiveData<List<Employee>>
    suspend fun getEmployeeAttendance(employeeId : Int, fromDate : String, toDate : String): LiveData<List<EmployeeAttendance>>
}