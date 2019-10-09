package com.akshay.employeeattendance.ui.EmployeeAttendanceActivity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.akshay.employeeattendance.R
import com.akshay.employeeattendance.internal.ScopeActivity
import com.akshay.employeeattendance.data.EmployeeRequest
import kotlinx.android.synthetic.main.activity_employee_attendance.*
import kotlinx.android.synthetic.main.activity_employee_attendance.toolbar
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.factory
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

class EmployeeAttendanceActivity : ScopeActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val employeeAttendanceViewModelFactory : ((EmployeeRequest) -> EmployeeAttendanceViewModelFactory) by factory()
    private lateinit var viewModel: EmployeeAttendanceViewModel

    val attendanceList : MutableMap<Int, Double> = mutableMapOf<Int,Double>()
    var fromDate : String = ""
    var month : String = ""
    var toDate : String = ""
    var empId : Int = 0
    var empName : String = ""
    var year : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_attendance)
        setSupportActionBar(toolbar)

        //Intents from previous activity
        empName = intent.getStringExtra(getString(R.string.emp_name))
        empId = intent.getIntExtra(getString(R.string.emp_id),0)
        fromDate = intent.getStringExtra(getString(R.string.from_date))
        toDate = intent.getStringExtra(getString(R.string.to_date))
        month = intent.getStringExtra(getString(R.string.month))
        year = intent.getIntExtra(getString(R.string.year),0)


        viewModel = ViewModelProviders.
            of(this, employeeAttendanceViewModelFactory(EmployeeRequest(empId,fromDate,toDate)))
            .get(EmployeeAttendanceViewModel::class.java)

        bindUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun bindUI() = launch {
        val employees = viewModel.employees.await()
        employees.observe(this@EmployeeAttendanceActivity, Observer {

            if(it == null) return@Observer

            if(it.isEmpty())
            {
                Toast.makeText(applicationContext,getString(R.string.no_data_found), Toast.LENGTH_LONG).show()
            }

            for (data in it)
            {
                //Calculating difference of each day entry and storing it in map
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val entryAt = dateFormat.parse(data.entry_at)
                val exitAt = dateFormat.parse(data.exit_at)

                val diffInMin = (exitAt.time - entryAt.time)/(60*60*1000).toDouble()
                val diffInHours = diffInMin.toBigDecimal().setScale(1, RoundingMode.HALF_DOWN).toDouble()

                val dayFormat = SimpleDateFormat("dd")
                val day = dayFormat.format(entryAt.time).toInt()

                if(attendanceList.containsKey(day))
                {
                    attendanceList[day] = attendanceList[day]!!.plus(diffInHours)
                }
                else
                {
                    attendanceList.put(day, diffInHours)
                }

            }

            enableUIs()
            setHeader()
            CreateReportTable()
            createFinalReport()
        })
    }

    fun enableUIs()
    {
        progressBar.visibility = View.GONE
        cardAttendanceReport.visibility = View.VISIBLE
        cardFinalReport.visibility = View.VISIBLE
    }

    fun setHeader()
    {
        val builder = StringBuilder()
        builder.append(empName).append("'s ").append(getString(R.string.attendance_report_of)).append(" ").append(month).append(" ").append(year)
        headerText.text = builder.toString()
    }

    fun CreateReportTable()
    {
        val rowHead = LayoutInflater.from(this).inflate(R.layout.row_item, null) as TableRow
        val coloumheader1 = (rowHead.findViewById<View>(R.id.attrib_name) as TextView)
            coloumheader1.text=(getString(R.string.date_of_the_month))
            coloumheader1.setTextColor(Color.parseColor("#000000"))
        coloumheader1.setTypeface(ResourcesCompat.getFont(this,R.font.roboto_medium))
        //coloumheader1.setTypeface(Typeface.DEFAULT_BOLD)


        val coloumnheader2 = (rowHead.findViewById<View>(R.id.attrib_value) as TextView)
            coloumnheader2.text=(getString(R.string.no_of_hours_logged))
        coloumnheader2.setTextColor(Color.parseColor("#000000"))
        coloumnheader2.setTypeface(ResourcesCompat.getFont(this,R.font.roboto_medium))

        //coloumnheader2.setTypeface(Typeface.DEFAULT_BOLD)


        attendanceReportTable!!.addView(rowHead)
        var i = 0
        for ((mKey, mValue) in attendanceList) {

            val row = LayoutInflater.from(this).inflate(R.layout.row_item, null) as TableRow

            val coloum1 = (row.findViewById<View>(R.id.attrib_name) as TextView)
            coloum1.text=mKey.toString()

            val coloum2 = (row.findViewById<View>(R.id.attrib_value) as TextView)
            val hours : Double = mValue/60
            coloum2.text= mValue.toString()//hours.toBigDecimal().setScale(1, RoundingMode.UP).toDouble().toString()

            if(i%2 == 0)
            {
                coloum1.setBackgroundColor(Color.parseColor("#f2f2f2"))
                coloum2.setBackgroundColor(Color.parseColor("#f2f2f2"))

            }

            attendanceReportTable!!.addView(row)
            i += 1
        }
        attendanceReportTable!!.requestLayout()
    }

    fun createFinalReport()
    {
        var daysInMonth : Int
        var totalLoggedMinutes : Double = 0.0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val yearFormat = SimpleDateFormat("yyyy")
        val monthFormat = SimpleDateFormat("MM")

        val date = dateFormat.parse(fromDate)
        val year = yearFormat.format(date).toInt()
        val month = monthFormat.format(date).toInt()

        if(Build.VERSION.SDK_INT >= 26) {
            daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        }
        else {
            val gregorianCalendar = GregorianCalendar(year,month, 1)
            daysInMonth = gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        for((k,v) in attendanceList)
        {
            totalLoggedMinutes += v
        }
        hoursLoggedText.text = totalLoggedMinutes.toString()
        daysPresentText.text = attendanceList.size.toString()
        daysAbsent.text = (daysInMonth - attendanceList.size).toString()
    }

}
