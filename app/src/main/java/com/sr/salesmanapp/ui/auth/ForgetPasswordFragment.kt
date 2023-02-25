package com.sr.salesmanapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.sr.salesmanapp.R
import com.sr.salesmanapp.databinding.FragmentForgetPasswordBinding
import com.sr.salesmanapp.databinding.FragmentLoginBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.HomeActivity

class ForgetPasswordFragment : BaseFragment<FragmentForgetPasswordBinding>() {

    private lateinit var mAuth : FirebaseAuth

    private fun setListener() {
        binding.btnSubmit.setOnClickListener {
            doResetPassword()
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_ForgetPasswordFragment_to_LoginFragment)
        }
    }

    private fun doResetPassword(){
        if(binding.etEmail?.text?.trimmedLength()==0) {
            binding.etEmail?.error = "Email Required"
            binding.etEmail?.requestFocus()
            return
        }
        else
            binding.etEmail?.error = null

        if(Patterns.EMAIL_ADDRESS.matcher(binding.etEmail?.text?.toString())?.matches()==false){
            binding.etEmail?.error = "Please provide valid email"
            binding.etEmail?.requestFocus()
            return
        }
        else
            binding.etEmail?.error = null

        /*if(binding.etPass?.text?.trimmedLength()==0) {
            binding.etPass?.error = "Pass Required"
            binding.etPass?.requestFocus()
            return
        }
        else
            binding.etPass?.error = null
*/


        var email = binding.etEmail?.text?.toString()!!

        showProgress()
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                hideProgress()
                if(it.isSuccessful){
                    Toast.makeText(requireContext(),"Check your email to reset password!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(requireContext(),"Try again, something wrong!", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun showProgress(){
        (requireActivity() as AuthActivity).showProgress()
    }

    fun hideProgress(){
        (requireActivity() as AuthActivity).hideProgress()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentForgetPasswordBinding
        get()  = FragmentForgetPasswordBinding::inflate

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        setListener()
    }

    override fun observeData() {

    }
}