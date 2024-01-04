@file:Suppress("DEPRECATION")

package com.example.chart_app.model

import android.app.ProgressDialog
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FirebaseService {
    private  val database = Firebase.database
    private val storage = Firebase.storage
    private val auth = Firebase.auth
    private val dialog:ProgressDialog? = null
    fun createNewUser(user: User,selectedImg: Uri){
        dialog?.show()
        val reference = storage.reference.child("profile").child("${auth.uid}")
        reference.putFile(selectedImg).addOnCompleteListener {
            if (it.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener {uri ->
                    user.profileImg = uri.toString()
                    database.reference
                        .child("users")
                        .child("${auth.uid}")
                        .setValue(user)
                        .addOnCompleteListener{
                            dialog?.dismiss()
                        }
                }
            }
            else{
                user.profileImg = "No Image"
                database.reference
                    .child("users")
                    .child("${auth.uid}")
                    .setValue(user)
                    .addOnCompleteListener{
                        dialog?.dismiss()
                    }
            }
        }
    }


}