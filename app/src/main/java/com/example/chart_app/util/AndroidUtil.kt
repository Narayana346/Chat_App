package com.example.chart_app.util

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.PhoneAuthProvider

class AndroidUtil{
    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}