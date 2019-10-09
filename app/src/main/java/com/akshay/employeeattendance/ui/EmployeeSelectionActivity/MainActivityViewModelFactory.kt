package com.akshay.employeeattendance.ui.EmployeeSelectionActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akshay.employeeattendance.network.Repository.DataRepository

class MainActivityViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(dataRepository) as T
    }
}