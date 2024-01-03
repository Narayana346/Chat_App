package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentOtpVerifyBinding
import com.example.chart_app.util.AndroidUtil
import com.example.chart_app.view.MainActivity
import com.example.chart_app.viewModel.MainActiveViewModel

class OtpVerify : Fragment(R.layout.fragment_otp_verify) {
    private lateinit var binding: FragmentOtpVerifyBinding
    private lateinit var viewModel: MainActiveViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpVerifyBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

            binding.verifyNo.text = arguments?.getString("mobileNumber")

        binding.continueBtn.setOnClickListener{
            val otp = binding.OTP.text.toString()
            val credential = viewModel.verifyOTP(otp)
            viewModel.signIn(credential)
            viewModel.userLoginStatus.observe(viewLifecycleOwner){
                if(it.status){
                    AndroidUtil.showToast(requireContext(),it.message)
                    findNavController().navigate(R.id.action_otpVerify_to_profile2)
                }else{
                    AndroidUtil.showToast(requireContext(),it.message)
                }
            }
        }
    }
}