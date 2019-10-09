package com.akshay.employeeattendance.data

data class EmployeeRequest(
    val empId: Int,
    val fromDate: String,
    val toDate: String
)