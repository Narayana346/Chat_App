package com.example.chart_app.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.chart_app.R
import com.example.chart_app.databinding.ActivityMainBinding
import com.example.chart_app.model.FirebaseService
import com.example.chart_app.model.UserAuthService
import com.example.chart_app.viewModel.MainActiveViewModel
import com.example.chart_app.viewModel.MainActiveViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainActiveViewModel
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MainActiveViewModelFactory(
            UserAuthService(),
            FirebaseService()
        ))[MainActiveViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_container) as NavHostFragment
        val navController = navHostFragment.navController

        val user = viewModel.getCurrentUser()
        if(user != null){
            navController.navigate(R.id.users)
        }else {
            navController.navigate(R.id.verification)
        }
    }
}