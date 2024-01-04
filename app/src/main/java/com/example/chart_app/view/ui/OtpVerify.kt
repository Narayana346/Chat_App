package com.example.chart_app.view.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chart_app.R
import com.example.chart_app.databinding.FragmentOtpVerifyBinding
import com.example.chart_app.util.AndroidUtil
import com.example.chart_app.view.MainActivity
import com.example.chart_app.viewModel.MainActiveViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.timerTask

class OtpVerify : Fragment(R.layout.fragment_otp_verify) {
    private lateinit var binding: FragmentOtpVerifyBinding
    private lateinit var viewModel: MainActiveViewModel
    private var timeOutSeconds:Long = 60L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOtpVerifyBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        val phoneNO = arguments?.getString("mobileNumber").toString()
        binding.verifyNo.text = "Verify $phoneNO"
        startResendTimer()
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

        binding.resendOTP.setOnClickListener{
            viewModel.reSendOTP(phoneNO,activity as MainActivity)
            viewModel.reSendOtpStatus.observe(viewLifecycleOwner){
                if(it.status){
                    AndroidUtil.showToast(requireContext(),it.message)
                    startResendTimer()
                }else{
                    AndroidUtil.showToast(requireContext(),it.message)
                }
            }
        }
    }

    private fun startResendTimer() {
        binding.resendOTP.isEnabled = false

        var timer  = Timer()
        val timerTask = timerTask {
            run {
                timeOutSeconds--
                binding.resendOTP.text = "Resend OTP in $timeOutSeconds seconds"
                if (timeOutSeconds <= 0) {
                    timeOutSeconds = 60L
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.resendOTP.isEnabled = true
                        binding.resendOTP.text = "click on resend OTP"
                    }
                    timer.cancel()
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask,0,1000)
    }
}