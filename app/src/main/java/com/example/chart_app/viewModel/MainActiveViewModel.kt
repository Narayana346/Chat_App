package com.example.chart_app.viewModel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chart_app.model.AuthResult
import com.example.chart_app.model.FirebaseService
import com.example.chart_app.model.Message
import com.example.chart_app.model.User
import com.example.chart_app.model.UserAuthService
import com.example.chart_app.view.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActiveViewModel @Inject constructor (private val userAuthService: UserAuthService,private val firebaseService:FirebaseService):ViewModel() {
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
    suspend fun getUser(): User?{
        return firebaseService.singleUser()
    }
    fun getUsers(): MutableLiveData<List<User>> {
        return firebaseService.getUsers()
    }
    fun getStatus(receiveUid:String): LiveData<String> {
        return firebaseService.getStatus(receiveUid)
    }
    fun getUid(): String? {
        return firebaseService.getUid()
    }
    fun setStatus(uid:String,status:String){
        firebaseService.setStatus(uid,status)
    }
    fun getUsers(senderRoom:String,messages:ArrayList<Message>,adapter: MessageAdapter){
        firebaseService.getUsers(senderRoom,messages,adapter)
    }
}