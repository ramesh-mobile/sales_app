package com.sr.salesmanapp.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sr.salesmanapp.R
import com.sr.salesmanapp.databinding.ActivityAuthBinding
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.ViewUtils.gone
import com.sr.salesmanapp.utils.ViewUtils.visible

class AuthActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding : ActivityAuthBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getBooleanExtra(Constants.IS_LOGOUT,false).let {
            if(it){
                var navController=findNavController(R.id.nav_host_fragment_content_auth).navigate(R.id.LoginFragment)
            }
            else {
                var navController = findNavController(R.id.nav_host_fragment_content_auth)
            }
        }
    }

    fun showProgress(){
        binding.progress.visible()
    }

    fun hideProgress(){
        binding.progress.gone()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}