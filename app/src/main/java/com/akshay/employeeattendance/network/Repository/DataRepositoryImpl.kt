package com.akshay.employeeattendance.network.Repository

import androidx.lifecycle.LiveData
import com.akshay.employeeattendance.data.Employee
import com.akshay.employeeattendance.data.EmployeeAttendance
import com.akshay.employeeattendance.network.DataSource.EmployeeListDataSource

class DataRepositoryImpl(private val employeeListDataSource: EmployeeListDataSource) :
    DataRepository {

    private var allEmployeeList : LiveData<List<Employee>>
    private var allEmployeeAttendanceList : LiveData<List<EmployeeAttendance>>

    init {
        allEmployeeList = employeeListDataSource.downloadedEmployeeList
        allEmployeeAttendanceList = employeeListDataSource.downloadedEmployeeAttendanceList
    }
    override suspend fun getEmployees(): LiveData<List<Employee>> {
        employeeListDataSource.fetchEmployees()
        return allEmployeeList
    }

    override suspend fun getEmployeeAttendance(employeeId : Int, fromDate : String, toDate : String): LiveData<List<EmployeeAttendance>> {
        employeeListDataSource.fetchEmployeeAttendance(employeeId, fromDate, toDate)
        return allEmployeeAttendanceList
    }
}