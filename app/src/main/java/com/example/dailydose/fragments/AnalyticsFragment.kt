package com.example.dailydose.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.databinding.FragmentAnalyticsBinding
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsFragment : Fragment() {
    
    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var healthRepository: HealthRepository
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            if (!::healthRepository.isInitialized) {
                healthRepository = HealthRepository(requireContext())
            }
            setupCharts()
            loadAnalyticsData()
            startAnimations()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error gracefully - show fallback UI
            showFallbackUI()
        }
    }
    
    private fun showFallbackUI() {
        try {
            // Hide charts and show simple message
            binding.lineChart?.visibility = View.GONE
            binding.barChart?.visibility = View.GONE
            binding.pieChart?.visibility = View.GONE
            
            // Show error message
            binding.tvTotalEntries?.text = "0"
            binding.tvTodayEntries?.text = "0"
            binding.tvWeekEntries?.text = "0"
            binding.tvAvgDailyEntries?.text = "0.0"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupCharts() {
        setupLineChart()
        setupBarChart()
        setupPieChart()
    }
    
    private fun setupLineChart() {
        try {
            binding.lineChart?.apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                
                // Styling
                setBackgroundColor(Color.WHITE)
                setDrawGridBackground(false)
                
                // X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    textColor = Color.parseColor("#666666")
                    textSize = 12f
                }
                
                // Y-axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    setDrawAxisLine(true)
                    textColor = Color.parseColor("#666666")
                    textSize = 12f
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupBarChart() {
        try {
            binding.barChart?.apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                
                // Styling
                setBackgroundColor(Color.WHITE)
                setDrawGridBackground(false)
                
                // X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    textColor = Color.parseColor("#666666")
                    textSize = 12f
                }
                
                // Y-axis
                axisLeft.apply {
                    setDrawGridLines(true)
                    setDrawAxisLine(true)
                    textColor = Color.parseColor("#666666")
                    textSize = 12f
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupPieChart() {
        try {
            binding.pieChart?.apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setUsePercentValues(true)
                setDrawEntryLabels(false)
                
                // Styling
                setBackgroundColor(Color.WHITE)
                setHoleColor(Color.WHITE)
                setTransparentCircleColor(Color.WHITE)
                setTransparentCircleAlpha(110)
                setHoleRadius(58f)
                setTransparentCircleRadius(61f)
                setDrawCenterText(true)
                setCenterTextSize(16f)
                setCenterTextColor(Color.parseColor("#333333"))
                
                legend.isEnabled = true
                legend.textSize = 12f
                legend.textColor = Color.parseColor("#666666")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadAnalyticsData() {
        try {
            val allEntries = healthRepository.getAllHealthEntries()
            
            // Load weight trend (Line Chart)
            loadWeightTrend(allEntries)
            
            // Load weekly activity (Bar Chart)
            loadWeeklyActivity(allEntries)
            
            // Load health metrics distribution (Pie Chart)
            loadHealthMetricsDistribution(allEntries)
            
            // Update statistics
            updateStatistics(allEntries)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadWeightTrend(entries: List<HealthEntry>) {
        try {
            val weightEntries = entries
                .filter { it.type == HealthType.WEIGHT }
                .sortedBy { it.timestamp }
                .takeLast(7) // Last 7 entries
            
            if (weightEntries.isNotEmpty()) {
            val lineEntries = weightEntries.mapIndexed { index, entry ->
                Entry(index.toFloat(), entry.value.toFloat())
            }
            
            val lineDataSet = LineDataSet(lineEntries, "Weight (kg)").apply {
                color = Color.parseColor("#4F46E5")
                setCircleColor(Color.parseColor("#4F46E5"))
                lineWidth = 3f
                circleRadius = 6f
                setDrawFilled(true)
                fillColor = Color.parseColor("#4F46E5")
                fillAlpha = 50
                setDrawValues(true)
                valueTextSize = 12f
                valueTextColor = Color.parseColor("#666666")
            }
            
            val lineData = LineData(lineDataSet)
            binding.lineChart.data = lineData
            
            // Set X-axis labels
            val labels = weightEntries.map { entry ->
                SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(entry.timestamp))
            }
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            
            binding.lineChart.invalidate()
        }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadWeeklyActivity(entries: List<HealthEntry>) {
        val calendar = Calendar.getInstance()
        val weeklyData = mutableMapOf<String, Float>()
        
        // Get last 7 days
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val dayEnd = calendar.timeInMillis
            
            val dayEntries = entries.filter { entry ->
                entry.timestamp in dayStart until dayEnd
            }
            
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            weeklyData[dayName] = dayEntries.size.toFloat()
        }
        
        val barEntries = weeklyData.values.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }
        
        val barDataSet = BarDataSet(barEntries, "Daily Entries").apply {
            color = Color.parseColor("#10B981")
            setDrawValues(true)
            valueTextSize = 12f
            valueTextColor = Color.parseColor("#666666")
        }
        
        val barData = BarData(barDataSet)
        binding.barChart.data = barData
        
        // Set X-axis labels
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(weeklyData.keys.toList())
        
        binding.barChart.invalidate()
    }
    
    private fun loadHealthMetricsDistribution(entries: List<HealthEntry>) {
        val typeCounts = entries.groupBy { it.type }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
        
        if (typeCounts.isNotEmpty()) {
            val colors = listOf(
                Color.parseColor("#4F46E5"),
                Color.parseColor("#10B981"),
                Color.parseColor("#F59E0B"),
                Color.parseColor("#EF4444"),
                Color.parseColor("#8B5CF6"),
                Color.parseColor("#EC4899")
            )
            
            val pieEntries = typeCounts.mapIndexed { index, (type, count) ->
                PieEntry(count.toFloat(), type.displayName)
            }
            
            val pieDataSet = PieDataSet(pieEntries, "").apply {
                setColors(colors.take(pieEntries.size))
                setDrawValues(true)
                valueTextSize = 12f
                valueTextColor = Color.WHITE
            }
            
            val pieData = PieData(pieDataSet)
            binding.pieChart.data = pieData
            binding.pieChart.invalidate()
        }
    }
    
    private fun updateStatistics(entries: List<HealthEntry>) {
        val totalEntries = entries.size
        val todayEntries = entries.count { entry ->
            val today = Calendar.getInstance()
            val entryDate = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            today.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR) &&
            today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR)
        }
        
        val thisWeekEntries = entries.count { entry ->
            val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
            entry.timestamp >= weekAgo.timeInMillis
        }
        
        binding.tvTotalEntries.text = totalEntries.toString()
        binding.tvTodayEntries.text = todayEntries.toString()
        binding.tvWeekEntries.text = thisWeekEntries.toString()
        
        // Calculate average entries per day
        val daysSinceFirstEntry = if (entries.isNotEmpty()) {
            val firstEntry = entries.minByOrNull { it.timestamp }
            val days = (System.currentTimeMillis() - firstEntry!!.timestamp) / (1000 * 60 * 60 * 24)
            maxOf(1, days)
        } else 1
        
        val avgDailyEntries = totalEntries.toFloat() / daysSinceFirstEntry
        binding.tvAvgDailyEntries.text = String.format("%.1f", avgDailyEntries)
    }
    
    private fun startAnimations() {
        // Animate charts with staggered entrance
        binding.lineChart.let { chart ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            chart.startAnimation(animation)
        }
        
        binding.barChart.let { chart ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            animation.startOffset = 200
            chart.startAnimation(animation)
        }
        
        binding.pieChart.let { chart ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.scale_in)
            animation.startOffset = 400
            chart.startAnimation(animation)
        }
        
        // Animate statistics cards
        binding.cardTotalEntries.let { card ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
            card.startAnimation(animation)
        }
        
        binding.cardTodayEntries.let { card ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
            animation.startOffset = 100
            card.startAnimation(animation)
        }
        
        binding.cardWeekEntries.let { card ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
            animation.startOffset = 200
            card.startAnimation(animation)
        }
        
        binding.cardAvgDailyEntries.let { card ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
            animation.startOffset = 300
            card.startAnimation(animation)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
