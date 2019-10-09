package com.akshay.employeeattendance.ui.EmployeeSelectionActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.akshay.employeeattendance.R
import com.akshay.employeeattendance.internal.ScopeActivity
import com.akshay.employeeattendance.network.Connectivity
import com.akshay.employeeattendance.ui.EmployeeAttendanceActivity.EmployeeAttendanceActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ScopeActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val mainActivityViewModelFactory : MainActivityViewModelFactory by instance()
    private lateinit var viewModel: MainActivityViewModel

    var employeeIdList : MutableList<Int> = mutableListOf<Int>()
    var employeeList: MutableList<String> = mutableListOf<String>()
    var yearList : MutableList<Int> = mutableListOf<Int>()
    var monthList : MutableList<String> = mutableListOf<String>()

    var selectedEmployeeName : String = ""
    var selectedEmployeeId : Int = 0
    var selectedMonthIndex : Int = 99
    var selectedMonth : String = ""
    var selectedYear : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this, mainActivityViewModelFactory).get(MainActivityViewModel::class.java)
        bindUI()

        createYearList()
        setYearDropDown()

        createMonthList()
        setMonthDropDown()

        viewAttendanceButton.setOnClickListener{
            viewEmployeeAttendanceReport(it)
        }

        selectEmployeeDropDown.onFocusChangeListener = View.OnFocusChangeListener{
            view, focused ->

            if(focused)
            {
                if(!Connectivity.isConnectedToInternet(this)) {
                    val snackbar = Snackbar.make(view,
                        getString(R.string.no_internet_connection),
                        Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        }

        selectEmployeeDropDown.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, id ->

            Log.e("E","Clicked")
        }

        selectEmployeeDropDown.setOnItemClickListener{ parent, view, position, id ->
            selectedEmployeeName = employeeList[position]
            selectedEmployeeId = employeeIdList[position]
            selectEmployeeDropDown.setError(null)
        }

        selectMonthDropDown.setOnItemClickListener{ parent, view, position, id ->
            selectedMonth = monthList[position]
            selectedMonthIndex = position
            selectMonthDropDown.setError(null)
        }

        selectYearDropDown.setOnItemClickListener{ parent, view, position, id ->
            selectedYear = yearList[position]
            selectYearDropDown.setError(null)
        }
    }

    private fun viewEmployeeAttendanceReport(view : View)
    {
        if(Connectivity.isConnectedToInternet(this)){

            if(selectedEmployeeId == 0)
            {
                selectEmployeeDropDown.setError(getString(R.string.select_employee))
            }
            if(selectedMonthIndex == 99)
            {
                selectMonthDropDown.setError(getString(R.string.select_month))
            }
            if(selectedYear == 0)
            {
                selectYearDropDown.setError(getString(R.string.select_year))
            }

            if(selectedEmployeeId != 0 && selectedMonthIndex != 99 && selectedYear!= 0)
            {
                var fromDate = formatDate(selectedYear, selectedMonthIndex, 1)
                var toDate  = formatDate(selectedYear, selectedMonthIndex, 31)

                Log.e(""+fromDate," "+toDate)
                val intent = Intent(this@MainActivity, EmployeeAttendanceActivity::class.java)
                intent.putExtra(getString(R.string.emp_name),selectedEmployeeName)
                intent.putExtra(getString(R.string.emp_id),selectedEmployeeId)
                intent.putExtra(getString(R.string.from_date),fromDate)
                intent.putExtra(getString(R.string.to_date),toDate)
                intent.putExtra(getString(R.string.month), selectedMonth)
                intent.putExtra(getString(R.string.year),selectedYear)
                startActivity(intent)
            }

        }
        else{

            val snackbar = Snackbar.make(view,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG)

            snackbar.setAction("RETRY")
                {
                    viewEmployeeAttendanceReport(view)
                }
            snackbar.show()
        }
    }

    private fun formatDate(year: Int, month : Int, day: Int) : String
    {
        val builder = StringBuilder()
        builder.append(year.toString()).append("-").append((month+1).toString()).append("-").append(day).toString()
        return builder.toString()
    }

    private fun bindUI() = launch {
        val employees = viewModel.employees.await()
        employees.observe(this@MainActivity, Observer {

            if(it == null) return@Observer

            //Adding all employees fetched from API into MutableList
            for (item in it)
            {
                if(item.name!= null && item.emp_id != null && item.emp_id.toIntOrNull() != null) {

                    try{
                        employeeIdList.add(item.emp_id.toInt())
                    }
                    catch (e : NumberFormatException){
                        Log.e("NumberFormatException",""+e)
                    }

                    employeeList.add(item.name)
                }
            }
            setEmployeesDropDown()

        })
    }

    fun setEmployeesDropDown()
    {
        val adapter = ArrayAdapter(
            this, R.layout.dropdown_menu_popup_item, employeeList
        )
        selectEmployeeDropDown.setAdapter(adapter)
    }

    fun setYearDropDown()
    {
        val adapter = ArrayAdapter(
            this, R.layout.dropdown_menu_popup_item, yearList
        )
        selectYearDropDown.setAdapter(adapter)
    }

    fun setMonthDropDown()
    {
        val adapter = ArrayAdapter(
            this, R.layout.dropdown_menu_popup_item, monthList
        )
        selectMonthDropDown.setAdapter(adapter)
    }

    fun createYearList()
    {
        val c1 = Calendar.getInstance()
        c1.set(1995,1,1)

        val c2 = Calendar.getInstance()

        while(c1.compareTo(c2) < 1){
            yearList.add(c1.get(Calendar.YEAR))
            c1.add((Calendar.YEAR),1)
        }
    }


    fun createMonthList()
    {
        val formatd = SimpleDateFormat("MMMM")

        val c1 = Calendar.getInstance()
        c1.set(Calendar.MONTH,0)
        monthList.add(formatd.format(c1.time))
        c1.add((Calendar.MONTH),1)
        while(c1.get(Calendar.MONTH) != 0){
            monthList.add(formatd.format(c1.time))
            c1.add((Calendar.MONTH),1)

        }
    }
}

