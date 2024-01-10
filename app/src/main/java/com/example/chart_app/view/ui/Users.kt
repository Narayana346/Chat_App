package com.example.chart_app.view.ui

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentUsersBinding
import com.example.chart_app.model.User
import com.example.chart_app.view.MainActivity
import com.example.chart_app.view.adapter.UserAdapter
import com.example.chart_app.view.adapter.UsersItemClickListener
import com.example.chart_app.viewModel.MainActiveViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class Users : Fragment(R.layout.fragment_users), UsersItemClickListener {
    private lateinit var binding: FragmentUsersBinding
    private lateinit var viewModel:MainActiveViewModel
    private var dialog: ProgressDialog? = null
    private lateinit var selectedUser:User
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersBinding.bind(view)
        viewModel =(activity as MainActivity).viewModel

        dialog = ProgressDialog(requireContext())
        dialog!!.setMessage("Uploading Image ..")
        dialog!!.setCancelable(false)
        var user:User? = null
        lifecycleScope.launch(Dispatchers.Main){
            async {
                user = viewModel.getUser()
            }.await()
            Glide.with(requireContext()).load(user!!.profileImg?.toUri())
                .placeholder(R.drawable.account_img)
                .into(binding.profileImg)
            binding.name.text = user!!.name.toString()
        }


        binding.mRec.setHasFixedSize(true)
        val userAdapter = UserAdapter(requireContext(),this)
        binding.mRec.layoutManager = GridLayoutManager(requireContext(),2)
        binding.mRec.adapter = userAdapter
        val users = arrayListOf<User>()
        viewModel.getUsers().observe(viewLifecycleOwner){
            userAdapter.updateList(it)
        }
        binding.profileImg.setOnClickListener{
            findNavController().navigate(R.id.action_users_to_profile2)
        }

    }

    override fun onItemClick(user: User) {
        val bundle = Bundle()
        bundle.putSerializable("user",user)
        Chart().arguments = bundle
        findNavController().navigate(R.id.action_users_to_chart,bundle)
    }

    override fun onItemLongClicked(user: User, cardView: CardView) {
    }
    override fun onDetach() {
        super.onDetach()
        val currentId = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance()!!.reference.child("Presence")
            .child(currentId.toString())
            .setValue("Offline")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val currentId = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance()!!.reference.child("Presence")
            .child(currentId.toString())
            .setValue("Online")
    }

}