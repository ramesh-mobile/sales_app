package com.sr.salesmanapp.ui.auth

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.sr.salesmanapp.R
import com.sr.salesmanapp.data.model.pojo.UsersModel
import com.sr.salesmanapp.databinding.FragmentRegistrationBinding
import com.sr.salesmanapp.utils.Constants
import java.util.regex.Pattern

class RegistrationFragment : Fragment() {

    private var _binding : FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = Firebase.auth
        setListener()
    }

    private fun setListener() {
        binding.btnSubmit.setOnClickListener {
            doRegistration()

        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
        }
    }

    private fun doRegistration() {
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

        if(binding.etPass?.text?.trimmedLength()==0) {
            binding.etPass?.error = "Pass Required"
            binding.etPass?.requestFocus()
            return
        }
        else
            binding.etPass?.error = null

        if(binding.etPhone?.text?.trimmedLength()==0) {
            binding.etPhone?.error = "Contact Required"
            binding.etPhone?.requestFocus()
            return
        }
        else
            binding.etPhone?.error = null


        var email = binding.etEmail?.text?.toString()!!
        var phone = binding.etPhone?.text?.toString()!!
        var pass = binding.etPass?.text?.toString()!!
        var user_type = Constants.NORMAL

        showProgress()
        mAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful){

                    FirebaseDatabase.getInstance().getReference(Constants.USER_MODEL)
                        .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .setValue(UsersModel(email,phone,user_type)).addOnCompleteListener {
                            hideProgress()
                            if(it.isSuccessful){
                                Toast.makeText(requireContext(),"${email} Registered Successfully",Toast.LENGTH_LONG).show()
                                findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
                            }
                            else if(it.isComplete){
                                Toast.makeText(requireContext(),"${email} Registered Completed",Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(requireContext(),"${email} Registered Failed",Toast.LENGTH_LONG).show()
                            }
                        }
                }else{
                    hideProgress()
                    Toast.makeText(requireContext(),"${email} already exists!",Toast.LENGTH_LONG).show()
                }
            }



    }

    fun showProgress(){
        (requireActivity() as AuthActivity).showProgress()
    }

    fun hideProgress(){
        (requireActivity() as AuthActivity).hideProgress()
    }
}