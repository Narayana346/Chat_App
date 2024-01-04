package com.example.chart_app.view.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentProfileBinding
import com.example.chart_app.model.User
import com.example.chart_app.view.MainActivity
import com.example.chart_app.viewModel.MainActiveViewModel
import java.util.Date

@Suppress("DEPRECATION")
class Profile : Fragment(R.layout.fragment_profile) {
    private lateinit var binding :FragmentProfileBinding
    private lateinit var viewModel: MainActiveViewModel
    private val PICK_IMAGE_REQUEST = 1 // Request code for image picker
    private var selectedImage: Uri? = null
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        viewModel = (activity as MainActivity).viewModel

        binding.profileImage.setOnClickListener {
            pickImageFromGallery()
        }


        binding.setupProfile.setOnClickListener {
            val formatter = SimpleDateFormat("EE,d MM yyyy HH: mm")
            val name = binding.editName.text.toString()
            if (name.isEmpty()){
                binding.editName.error = "Please Enter Name"
            }else{
                val userInfo =viewModel.getCurrentUser()
                val user = User(
                    uid = userInfo?.uid,
                    name = name,
                    phoneNO = userInfo?.phoneNumber,
                    profileImg = selectedImage.toString(),
                    time = formatter.format(Date())
                )
                selectedImage?.let {
                    viewModel.writeNewUser(user, it)
                }
                findNavController().navigate(R.id.action_profile2_to_users)
            }
        }
        // logout action
        binding.signOutBtn.setOnClickListener {
            viewModel.logOut()
            findNavController().navigate(R.id.action_profile2_to_verification)
        }
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data
            // Handle the selected image URI, e.g., display it in an ImageView
            binding.profileImage.setImageURI(selectedImage)
        }
    }
}

