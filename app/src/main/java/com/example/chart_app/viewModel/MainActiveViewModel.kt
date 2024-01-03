package com.example.chart_app.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chart_app.model.AuthResult
import com.example.chart_app.model.UserAuthService
import com.google.firebase.auth.PhoneAuthCredential

class MainActiveViewModel(private val userAuthService: UserAuthService):ViewModel() {
     var userLoginStatus:MutableLiveData<AuthResult> = MutableLiveData()
     var otpStatus:MutableLiveData<AuthResult> = MutableLiveData()

    fun sendOTP(phoneNO:String,activity: Activity){
        userAuthService.sendOTP(phoneNO,activity){
            otpStatus.value = it
        }
    }
    fun verifyOTP(otp:String): PhoneAuthCredential {
        return userAuthService.verifyOTP(otp)
    }
    fun signIn(credential: PhoneAuthCredential){
        userAuthService.signIn(credential){
            userLoginStatus.value = it
        }
    }

}