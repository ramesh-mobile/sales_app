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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.sr.salesmanapp.R
import com.sr.salesmanapp.data.model.pojo.UsersModel
import com.sr.salesmanapp.databinding.FragmentLoginBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.HomeActivity
import com.sr.salesmanapp.utils.Constants

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var googleSignClient: GoogleSignInClient

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    override fun initView() {
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default))
            .requestEmail()
            .build()
        googleSignClient = GoogleSignIn.getClient(requireContext(),gso)


        setListener()
    }

    private fun setListener() {
        binding.btnSubmit.setOnClickListener {
            doLogin()
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegistrationFragment)
        }

        binding.tvForgetPass.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_ForgetPasswordFragment)
        }

        binding.btnSignWithGoogle?.setOnClickListener {
            /*val googleSignInIntent = googleSignClient.signInIntent
            startActivityForResult(googleSignInIntent,FB_SIGN_IN)*/
        }

    }
    val FB_SIGN_IN = 10

    private fun doLogin(){
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



        var email = binding.etEmail?.text?.toString()!!
        var pass = binding.etPass?.text?.toString()!!



        showProgress()
        mAuth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                hideProgress()
                if(it.isSuccessful){
                    //FirebaseDatabase.getInstance().getReference(Constants.USER_MODEL).child(FirebaseAuth.getInstance()?.uid!!)

                    var userId = mAuth.currentUser?.uid
                    var dbReference : DatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.USER_MODEL)
                    userId?.let {
                        dbReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var userModel = snapshot.getValue(UsersModel::class.java)
                                Prefs.putString(Constants.USER_MODEL,Gson().toJson(userModel))
                                startActivity(Intent(requireContext(), HomeActivity::class.java))
                                requireActivity().finish()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(requireContext(),"Server busy, try again!", Toast.LENGTH_LONG).show()
                            }
                        })
                    }


                }else{
                    Toast.makeText(requireContext(),"Invalid credential, try again!", Toast.LENGTH_LONG).show()
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