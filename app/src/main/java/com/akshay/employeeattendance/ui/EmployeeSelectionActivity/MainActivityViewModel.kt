package com.akshay.employeeattendance.ui.EmployeeSelectionActivity

import androidx.lifecycle.ViewModel
import com.akshay.employeeattendance.internal.lazyDeferred
import com.akshay.employeeattendance.network.Repository.DataRepository

class MainActivityViewModel (dataRepository: DataRepository) : ViewModel(){

    val employees by lazyDeferred {
        dataRepository.getEmployees()
    }
}