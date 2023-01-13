package com.sr.salesmanapp.ui.auth

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pixplicity.easyprefs.library.Prefs
import com.sr.salesmanapp.R
import com.sr.salesmanapp.databinding.FragmentSplashBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.HomeActivity
import com.sr.salesmanapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    lateinit var mAuth : FirebaseAuth

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashBinding
        get() = FragmentSplashBinding::inflate

    override fun initView() {

        mAuth = FirebaseAuth.getInstance()

        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            findNavController().popBackStack(R.id.SplashFragment, true);

            if(Prefs.getString(Constants.USER_MODEL,null)!=null){
                startActivity(Intent(requireContext(),HomeActivity::class.java))
                requireActivity().finish()
            }
            else findNavController().navigate(R.id.LoginFragment)
        }
    }
}