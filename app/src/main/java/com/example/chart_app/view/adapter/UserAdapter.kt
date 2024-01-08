package com.example.chart_app.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chart_app.R
import com.example.chart_app.databinding.ItemProfileBinding
import com.example.chart_app.model.User

class UserAdapter(private var context: Context, private val usersItemClickListener: UsersItemClickListener): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var userFullList: ArrayList<User> = ArrayList()
    private var userList: ArrayList<User> = ArrayList()

    inner class UserViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val binding = ItemProfileBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile , parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.userName.text = user.name
        Glide.with(context).load(user.profileImg)
            .placeholder(R.drawable.account_img)
            .into(holder.binding.profile)
        holder.binding.cardView.setOnClickListener{
            usersItemClickListener.onItemClick(userList[position])
        }

        holder.binding.cardView.setOnLongClickListener{
            usersItemClickListener.onItemLongClicked(userList[position],holder.binding.cardView)
            true
        }
    }
    //search feature
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(search:String){
        userList.clear()
        for (item in userFullList){
            if(item.name!!.contains(search) || item.phoneNO!!.contains(search)){
                userList.add(item)
            }
        }
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList:List<User>){
        userFullList.clear()
        userFullList.addAll(newList)

        userList.clear()
        userList.addAll(userFullList)
        notifyDataSetChanged()
    }
}