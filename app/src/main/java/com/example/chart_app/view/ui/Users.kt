package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentUsersBinding

class Users : Fragment(R.layout.fragment_users) {
    private lateinit var binding: FragmentUsersBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersBinding.bind(view)
    }
}