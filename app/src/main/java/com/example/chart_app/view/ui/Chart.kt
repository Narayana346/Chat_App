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
import com.google.firebase.database.FirebaseDatabase
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
        sendUid = viewModel.getUid().toString()
        storage = FirebaseStorage.getInstance()
        dilog = ProgressDialog(requireContext())
        dilog.setMessage("Uploading")
        dilog.setCancelable(false)
        messages = ArrayList()
        viewModel.getStatus(receiveUid).observe(viewLifecycleOwner){ status ->
            if(status == "Offline"){
                binding.status.visibility = View.GONE
            }else{
                binding.status.text = status
            }
        }
        senderRoom = sendUid+receiveUid
        receiverRoom = receiveUid+sendUid
        adapter = MessageAdapter(requireContext(),messages,senderRoom!!,receiverRoom)
        binding.recycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleView.adapter = adapter

        viewModel.getUsers(senderRoom,messages,adapter)



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
                viewModel.setStatus(sendUid,"typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }
            var userStoppedTyping = Runnable {
                viewModel.setStatus(sendUid,"Online")
            }

        })
    }

    override fun onResume() {
        super.onResume()
        val currentId:String? = viewModel.getUid()
        if (currentId != null) {
            viewModel.setStatus(currentId,"Online")
        }
    }
    override fun onPause() {
        super.onPause()
        val currentId = viewModel.getUid()
        if (currentId != null) {
            viewModel.setStatus(currentId,"Offline")
        }
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