package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentProfileBinding

class Profile : Fragment(R.layout.fragment_profile) {
    private lateinit var binding :FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
    }
}