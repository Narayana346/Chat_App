package com.example.chart_app.viewModel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chart_app.model.AuthResult
import com.example.chart_app.model.FirebaseService
import com.example.chart_app.model.User
import com.example.chart_app.model.UserAuthService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential

class MainActiveViewModel(private val userAuthService: UserAuthService,private val firebaseService:FirebaseService):ViewModel() {
    var userLoginStatus:MutableLiveData<AuthResult> = MutableLiveData()
    var otpStatus:MutableLiveData<AuthResult> = MutableLiveData()
    var reSendOtpStatus:MutableLiveData<AuthResult> = MutableLiveData()

    fun sendOTP(phoneNO:String,activity: Activity){
        userAuthService.sendOTP(phoneNO,activity){
            otpStatus.value = it
        }
    }
    fun verifyOTP(otp:String): PhoneAuthCredential {
        return userAuthService.verifyOTP(otp)
    }
    fun reSendOTP(phoneNO:String,activity: Activity){
        userAuthService.reSendOTP(phoneNO,activity){
            reSendOtpStatus.value = it
        }
    }
    fun getCurrentUser(): FirebaseUser? {
    return userAuthService.getCurrentUser()
    }
    fun signIn(credential: PhoneAuthCredential){
        userAuthService.signIn(credential){
            userLoginStatus.value = it
        }
    }
    fun logOut(){
        userAuthService.signOut()
    }
    fun writeNewUser(user: User,image: Uri){
        firebaseService.createNewUser(user,image)
    }

}