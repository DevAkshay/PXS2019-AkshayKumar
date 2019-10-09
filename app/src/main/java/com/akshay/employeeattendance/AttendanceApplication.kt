package com.akshay.employeeattendance

import android.app.Application
import com.akshay.employeeattendance.data.EmployeeRequest
import com.akshay.employeeattendance.network.*
import com.akshay.employeeattendance.network.DataSource.EmployeeListDataSource
import com.akshay.employeeattendance.network.DataSource.EmployeeListDataSourceImpl
import com.akshay.employeeattendance.network.Interceptor.ConnectivityInterceptor
import com.akshay.employeeattendance.network.Interceptor.ConnectivityInterceptorImpl
import com.akshay.employeeattendance.network.Repository.DataRepository
import com.akshay.employeeattendance.network.Repository.DataRepositoryImpl
import com.akshay.employeeattendance.ui.EmployeeAttendanceActivity.EmployeeAttendanceViewModelFactory
import com.akshay.employeeattendance.ui.EmployeeSelectionActivity.MainActivityViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.generic.*

class AttendanceApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidModule(this@AttendanceApplication))

        bind<ConnectivityInterceptor>() with singleton {
            ConnectivityInterceptorImpl(
                instance()
            )
        }
        bind() from singleton { ApiService(instance()) }
        bind<EmployeeListDataSource>() with singleton {
            EmployeeListDataSourceImpl(
                instance()
            )
        }
        bind<DataRepository>() with singleton {
            DataRepositoryImpl(
                instance()
            )
        }
        bind() from provider {
            MainActivityViewModelFactory(
                instance()
            )
        }
        bind() from factory() {
            requestData : EmployeeRequest -> EmployeeAttendanceViewModelFactory(
                requestData,
                instance()
            )
        }
    }
}