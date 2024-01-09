package com.example.chart_app.view.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chart_app.R
import com.example.chart_app.databinding.DeleteBinding
import com.example.chart_app.databinding.ReceiverMsgBinding
import com.example.chart_app.databinding.SendMsgBinding
import com.example.chart_app.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Suppress("UNREACHABLE_CODE")
class MessageAdapter(
    var context: Context,
    messages:ArrayList<Message>,
    senderRoom:String,
    receiverRoom:String):RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var messages:ArrayList<Message>
    val ITEM_SEND = 1
    val ITEM_RECEIVE = 2
    val senderRoom:String
    var receiverRoom:String
        inner class SendMsgHolder(iteView: View):RecyclerView.ViewHolder(iteView) {
            var binding:SendMsgBinding = SendMsgBinding.bind(iteView)

        }
    inner class ReceiveMsgHolder(iteView: View):RecyclerView.ViewHolder(iteView) {
        var binding:ReceiverMsgBinding = ReceiverMsgBinding.bind(iteView)
    }
    init {
        this.messages = messages
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        return if(viewType == ITEM_SEND){
            val view:View = LayoutInflater.from(context).inflate(R.layout.send_msg,parent,false)
            SendMsgHolder(view)
        }else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receiver_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if(message.senderId == FirebaseAuth.getInstance().uid){
            ITEM_SEND
        }else{
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int  = messages.size

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.javaClass == SendMsgHolder::class.java){
            val viewHolder = holder as SendMsgHolder
            if(message.message.equals("photo")){
                viewHolder.binding.sendImage.visibility = View.VISIBLE
                viewHolder.binding.sendMsg.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder_image_24)
                    .into(viewHolder.binding.sendImage)
            }
            viewHolder.binding.sendMsg.text = message.message
            viewHolder.itemView.setOnLongClickListener{
                val view = LayoutInflater.from(context).inflate(R.layout.delete,null)
                val binding:DeleteBinding = DeleteBinding.bind(view)
                val dialog:AlertDialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener{
                    message.message = "This message is Removed"
                    message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it).setValue(message)
                    }
                    message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom)
                            .child("message")
                            .child(it).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener{ dialog.dismiss() }
                dialog.show()
                false
            }
        }
        else{
            val viewHolder = holder as ReceiveMsgHolder
            if(message.message.equals("photo")){
                viewHolder.binding.receiveImage.visibility = View.VISIBLE
                viewHolder.binding.receiveMsg.visibility = View.GONE
                viewHolder.binding.rLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder_image_24)
                    .into(viewHolder.binding.receiveImage)
            }
            viewHolder.binding.receiveMsg.text = message.message
            viewHolder.itemView.setOnLongClickListener{
                val view = LayoutInflater.from(context).inflate(R.layout.delete,null)
                val binding:DeleteBinding = DeleteBinding.bind(view)
                val dialog:AlertDialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener{
                    message.message = "This message is Removed"
                    message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("charts")
                            .child(senderRoom)
                            .child("message")
                            .child(it).setValue(message)
                    }
                    message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("charts")
                            .child(receiverRoom)
                            .child("message")
                            .child(it).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("message")
                            .child(it).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener{ dialog.dismiss() }
                dialog.show()
                false
            }
        }
    }

}