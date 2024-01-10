package com.example.chart_app.model

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UserAuthService @Inject constructor(){
    private val auth = Firebase.auth
    lateinit var verificationCode : String
    lateinit var reSendingToken:PhoneAuthProvider.ForceResendingToken
    fun sendOTP(phoneNumber:String,activity: Activity,result:(AuthResult) -> Unit){
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signIn(credential,result)
            }

            override fun onVerificationFailed(credential: FirebaseException) {
                result(AuthResult(false,"OTP Verification Failed"))
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode = p0
                reSendingToken = p1
                result(AuthResult(true,"OTP sent successfully"))

            }
        }
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L ,TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)


        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }
    fun reSendOTP(phoneNumber: String,activity: Activity,result: (AuthResult) -> Unit){
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signIn(credential,result)
            }

            override fun onVerificationFailed(credential: FirebaseException) {
                result(AuthResult(false,"OTP Verification Failed"))
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode = p0
                reSendingToken = p1
                result(AuthResult(true,"OTP sent successfully"))
            }
        }
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L ,TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)

        PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(reSendingToken).build())
    }
    fun verifyOTP(otp: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationCode, otp)
    }
    fun signIn(credential: PhoneAuthCredential ,result: (AuthResult) -> Unit) {
        auth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                result(AuthResult(true,"signed"))
            }else{
                result(AuthResult(false,"failed to signIn"))
            }
        }

    }
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    fun signOut(){
        auth.signOut()
    }
}