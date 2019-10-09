package com.akshay.employeeattendance.ui.EmployeeAttendanceActivity

import androidx.lifecycle.ViewModel
import com.akshay.employeeattendance.data.EmployeeRequest
import com.akshay.employeeattendance.internal.lazyDeferred
import com.akshay.employeeattendance.network.Repository.DataRepository

class EmployeeAttendanceViewModel (requestData : EmployeeRequest, dataRepository: DataRepository) : ViewModel(){

    val employees by lazyDeferred {
        dataRepository.getEmployeeAttendance(requestData.empId, requestData.fromDate, requestData.toDate)
    }
}