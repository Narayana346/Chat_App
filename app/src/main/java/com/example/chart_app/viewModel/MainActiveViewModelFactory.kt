package com.example.chart_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chart_app.model.FirebaseService
import com.example.chart_app.model.UserAuthService

class MainActiveViewModelFactory(private val userAuthService: UserAuthService,private val firebaseService: FirebaseService):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActiveViewModel(userAuthService,firebaseService) as T
    }
}