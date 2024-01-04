package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentUsersBinding
import com.example.chart_app.view.MainActivity
import com.example.chart_app.viewModel.MainActiveViewModel

class Users : Fragment(R.layout.fragment_users) {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var viewModel:MainActiveViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersBinding.bind(view)
        viewModel =(activity as MainActivity).viewModel

    }
}