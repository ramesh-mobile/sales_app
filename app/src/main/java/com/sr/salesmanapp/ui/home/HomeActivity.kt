package com.sr.salesmanapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.pixplicity.easyprefs.library.Prefs
import com.sr.salesmanapp.R
import com.sr.salesmanapp.databinding.ActivityMainBinding
import com.sr.salesmanapp.ui.auth.AuthActivity
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.ViewUtils.gone
import com.sr.salesmanapp.utils.ViewUtils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure to logout?")
            .setPositiveButton("Yes"){dialogInterface,which->
                FirebaseAuth.getInstance().signOut()
                Prefs.putString(Constants.USER_MODEL,null)
                startActivity(Intent(applicationContext,AuthActivity::class.java).apply {
                    putExtra(Constants.IS_LOGOUT,true)
                })
                finish()
            }.setNegativeButton("No"){dialogInterface,which->
                dialogInterface.dismiss()
            }
            .create()
            //.setCancelable(false)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
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

    var zoomLevel : Float = 18F
}