package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentChartBinding
import com.example.chart_app.model.User

@Suppress("DEPRECATION")
class Chart : Fragment(R.layout.fragment_chart) {
    private lateinit var binding: FragmentChartBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChartBinding.bind(view)
        val user = arguments?.getSerializable("user") as User

        Glide.with(requireContext()).load(user.profileImg?.toUri())
            .placeholder(R.drawable.account_img)
            .into(binding.profileImg)

        binding.name.text = user.name

    }

}