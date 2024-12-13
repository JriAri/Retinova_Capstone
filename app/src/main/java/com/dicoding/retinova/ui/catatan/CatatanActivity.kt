package com.dicoding.retinova.ui.catatan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.retinova.R
import com.dicoding.retinova.ui.customviews.CircularProgressView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.DecimalFormat

class CatatanActivity : AppCompatActivity() {
    private lateinit var circularProgressView: CircularProgressView
    private lateinit var tvAverageBloodSugarLevel: TextView
    private lateinit var btnAddSugarLevel: Button
    private lateinit var lineChart: LineChart

    private val viewModel: CatatanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catatan)

        circularProgressView = findViewById(R.id.circularProgressView)
        tvAverageBloodSugarLevel = findViewById(R.id.tvAverageBloodSugarLevel)
        btnAddSugarLevel = findViewById(R.id.btnAddSugarLevel)
        lineChart = findViewById(R.id.lineChart)

        observeViewModel()

        btnAddSugarLevel.setOnClickListener {
            openAddBloodSugarActivity()
        }
    }

    private fun observeViewModel() {
        viewModel.bloodSugarLevel.observe(this) { level ->
            circularProgressView.setProgress(level.toFloat())
        }

        viewModel.averageBloodSugarLevel.observe(this) { avgLevel ->
            val formattedAvg = DecimalFormat("#.#").format(avgLevel)
            tvAverageBloodSugarLevel.text = "$formattedAvg mg/dL"
        }

        viewModel.bloodSugarChartData.observe(this) { chartData ->
            updateChart(chartData)
        }
    }

    private fun updateChart(chartData: List<Pair<String, Float>>) {
        val entries = chartData.mapIndexed { index, data ->
            Entry(index.toFloat(), data.second)
        }

        val dataSet = LineDataSet(entries, "Rata-rata Gula Darah").apply {
            color = resources.getColor(R.color.primary, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            setCircleColor(resources.getColor(R.color.primary, null))
            circleRadius = 4f
            setDrawValues(false)
        }

        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(chartData.map { it.first })
            granularity = 1f
            setDrawGridLines(false)
        }

        lineChart.data = LineData(dataSet)
        lineChart.description.isEnabled = false

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let { entry ->
                    val index = entry.x.toInt()
                    val date = chartData[index].first
                    val value = chartData[index].second

                    Toast.makeText(
                        this@CatatanActivity,
                        "Tanggal: $date\nRata-rata: $value mg/dL",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected() {
            }
        })

        lineChart.invalidate()
    }


    private fun openAddBloodSugarActivity() {
        val intent = Intent(this, AddBloodSugarActivity::class.java)
        startActivity(intent)
    }
}
