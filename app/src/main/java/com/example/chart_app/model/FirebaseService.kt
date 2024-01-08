@file:Suppress("DEPRECATION")

package com.example.chart_app.model

import android.app.ProgressDialog
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private  val database = Firebase.database
    private val storage = Firebase.storage
    private val auth = Firebase.auth
    private val dialog:ProgressDialog? = null
    private var user: User = User()
    private var users: ArrayList<User> = ArrayList()
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
    suspend fun singleUser(): User? {
        val singleUser: User?
        val snapshort =database.reference.child("users").child("${auth.uid}")
            .get().await()
            singleUser = snapshort.getValue(User::class.java)
        return singleUser
    }
    fun getUser(): User? {
        var user :User? = null

        database.reference.child("users")
            .child("${auth.uid}").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot){
                    user = snapshot.getValue(User::class.java)!!
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        return user
    }
    fun getUsers(): MutableLiveData<List<User>> {
        val liveUsers: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
            database.reference.child("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    users.clear()
                    for (snapshot1 in snapshot.children) {
                        val user = snapshot1.getValue(User::class.java)
                        if (user != null) {
                            if (!user.uid.equals(auth.uid)) {
                                users.add(user)
                            }
                        }
                    }
                    liveUsers.value = users
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        return liveUsers
    }

}