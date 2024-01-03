package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentVerificationBinding
import com.example.chart_app.util.AndroidUtil
import com.example.chart_app.view.MainActivity
import com.example.chart_app.viewModel.MainActiveViewModel

class Verification : Fragment(R.layout.fragment_verification) {
    private lateinit var binding: FragmentVerificationBinding
    private lateinit var viewModel: MainActiveViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVerificationBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel
        binding.progressBar.visibility = View.GONE
        binding.countryCode.registerCarrierNumberEditText(binding.mobileNo)

        binding.sendOTP.setOnClickListener {
            val phoneNo = binding.mobileNo.text.toString()
            if(!binding.countryCode.isValidFullNumber){
                binding.mobileNo.error = "Phone number is not valid"
                return@setOnClickListener
            }else {
                binding.progressBar.visibility = View.VISIBLE
                val bundle = Bundle()
                bundle.putString("mobileNumber", binding.countryCode.fullNumberWithPlus)
                Verification().arguments = bundle
                viewModel.sendOTP(phoneNo, activity as MainActivity)
                viewModel.otpStatus.observe(viewLifecycleOwner){
                    if(it.status){
                        AndroidUtil.showToast(requireContext(),it.message)
                        findNavController().navigate(R.id.action_verification_to_otpVerify,bundle)
                    }else{
                        AndroidUtil.showToast(requireContext(),it.message)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}