package com.example.chart_app.view.ui

import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentChartBinding
import com.example.chart_app.model.Message
import com.example.chart_app.model.User
import com.example.chart_app.view.MainActivity
import com.example.chart_app.view.adapter.MessageAdapter
import com.example.chart_app.viewModel.MainActiveViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Date

@Suppress("DEPRECATION")
class Chart : Fragment(R.layout.fragment_chart) {
    private lateinit var binding: FragmentChartBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var messages:ArrayList<Message>
    private lateinit var senderRoom:String
    private lateinit var receiverRoom:String
    private var  database = FirebaseDatabase.getInstance()
    private lateinit var storage:FirebaseStorage
    private lateinit var dilog:ProgressDialog
    private lateinit var sendUid:String
    private lateinit var receiveUid:String
    private lateinit var viewModel:MainActiveViewModel
    var live :LiveData<ArrayList<Message>> = MutableLiveData<ArrayList<Message>>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChartBinding.bind(view)
        viewModel =(activity as MainActivity).viewModel

        // user profile
        val user = arguments?.getSerializable("user") as User
        Glide.with(requireContext()).load(user.profileImg?.toUri())
            .placeholder(R.drawable.account_img)
            .into(binding.profileImg)
        binding.name.text = user.name
        receiveUid = user.uid.toString()
        sendUid = FirebaseAuth.getInstance().uid.toString()
        storage = FirebaseStorage.getInstance()
        dilog = ProgressDialog(requireContext())
        dilog.setMessage("Uploading")
        dilog.setCancelable(false)
        messages = ArrayList()
        database!!.reference.child("Presence").child(receiveUid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val  status = snapshot.getValue(String::class.java)
                        if(status == "offline"){
                            binding.status.visibility = View.GONE
                        }else{
                            binding.status.text = status
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        senderRoom = sendUid+receiveUid
        receiverRoom = receiveUid+sendUid
        adapter = MessageAdapter(requireContext(),messages,senderRoom!!,receiverRoom)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter


        database!!.reference.child("chats")
            .child(senderRoom)
            .child("message")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for (snap in snapshot.children){
                        val message: Message? = snap.getValue(Message::class.java)
                        message?.messageId = snap.key
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })



        binding.sendBtn.setOnClickListener{
            val messageTxt:String = binding.messageBox.text.toString()
            val formatter = SimpleDateFormat("EE,d MM yyyy HH: mm")
            val date = formatter.format(Date())
            val message = Message(message = messageTxt, senderId = sendUid, timestamp = date)

            binding.messageBox.text.clear()

            val randomKey= database!!.reference.push().key
            val lastMsg = HashMap<String,Any>()

            lastMsg["lastMsg"] = message.message.toString()
            lastMsg["lastMsgTime"] = formatter.format(Date())

            database!!.reference.child("chats").child(senderRoom)
                .updateChildren(lastMsg)

            database!!.reference.child("chats").child(receiverRoom)
                .updateChildren(lastMsg)
            database!!.reference.child("chats").child(senderRoom)
                .child("message")
                .child(randomKey.toString())
                .setValue(message).addOnSuccessListener {
                    database!!.reference.child("chats")
                        .child(receiverRoom)
                        .child("message")
                        .child(randomKey.toString())
                        .setValue(message)
                        .addOnSuccessListener {
                            dilog.cancel()
                        }
                }
        }

        binding.attachment.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,25)
        }
        val handler = Handler()
        binding.messageBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                database!!.reference.child("Presence")
                    .child(sendUid)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStopedTyping,1000)
            }
            var userStopedTyping = Runnable {
                database!!.reference.child("Presence")
                    .child(sendUid)
                    .setValue("Online")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId.toString())
            .setValue("Online")
    }
    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence")
            .child(currentId.toString())
            .setValue("Offline")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25){
            if(data != null){
                if(data.data != null){
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    var reference = storage.reference.child("chats")
                        .child(calendar.timeInMillis.toString()+"")
                    dilog.show()
                        reference.putFile(selectedImage!!).addOnCompleteListener{
                            if(it.isSuccessful){
                                reference.downloadUrl.addOnSuccessListener {uri ->
                                    val filePath = uri.toString()
                                    val messageTxt:String = binding.messageBox.text.toString()
                                    val formatter = SimpleDateFormat("EE,d MM yyyy HH: mm")
                                    val date = formatter.format(Date())
                                    val message = Message(message = messageTxt, senderId = sendUid, timestamp = date)
                                    message.message = "photo"
                                    message.imageUrl = filePath
                                    binding.messageBox.text.clear()
                                    val randomKey = database!!.reference.push().key
                                    val lastMsg = HashMap<String,Any>()
                                    lastMsg["lastMsg"] = message.message.toString()
                                    lastMsg["lastTime"] = formatter.format(Date())

                                    database!!.reference.child("chats")
                                        .updateChildren(lastMsg)
                                    database!!.reference.child("chats")
                                        .child(receiverRoom)
                                        .updateChildren(lastMsg)
                                    database!!.reference.child("chats")
                                        .child(senderRoom)
                                        .child("message")
                                        .child(randomKey.toString())
                                        .setValue(message).addOnSuccessListener {
                                            database!!.reference.child("chats")
                                                .child(receiverRoom)
                                                .child("message")
                                                .child(randomKey.toString())
                                                .setValue(message).addOnSuccessListener {
                                                    dilog.cancel()
                                                }
                                        }
                                }
                            }
                        }

                }
            }
        }
    }

}