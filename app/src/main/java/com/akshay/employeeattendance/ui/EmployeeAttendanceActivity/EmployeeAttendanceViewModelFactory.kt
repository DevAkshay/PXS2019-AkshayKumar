package com.akshay.employeeattendance.ui.EmployeeAttendanceActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akshay.employeeattendance.data.EmployeeRequest
import com.akshay.employeeattendance.network.Repository.DataRepository

class EmployeeAttendanceViewModelFactory (
    private val requestData: EmployeeRequest,
    private val dataRepository: DataRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EmployeeAttendanceViewModel(requestData, dataRepository) as T
    }
}