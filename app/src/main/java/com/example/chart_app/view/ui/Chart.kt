package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentChartBinding

class Chart : Fragment(R.layout.fragment_chart) {
    private lateinit var binding: FragmentChartBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChartBinding.bind(view)
    }

}