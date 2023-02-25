package com.sr.salesmanapp.utils

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.sr.salesmanapp.BuildConfig

object ViewUtils {

    fun View.gone(){
        visibility = View.GONE
    }

    fun View.visible(){
        visibility = View.VISIBLE
    }

    fun Fragment.print(TAG : String, msg: String?){
        ViewUtils.print(TAG,msg)
    }

    fun AppCompatActivity.print(TAG : String, msg: String?){
        ViewUtils.print(TAG,msg)
    }

    fun Activity.print(TAG : String, msg: String?){
        ViewUtils.print(TAG,msg)
    }

    fun print(TAG : String, msg: String?){
        if(BuildConfig.DEBUG)
            Log.d(TAG, "$msg")
    }

    fun Fragment.error(TAG : String, msg: String?){
        ViewUtils.print(TAG,msg)
    }

    fun AppCompatActivity.error(TAG : String, msg: String?){
        ViewUtils.print(TAG,msg)
    }

    fun Activity.error(TAG : String, msg: String?){
        ViewUtils.print(TAG,msg)
    }

    fun error(TAG : String, msg: String?){
        if(BuildConfig.DEBUG)
            Log.e(TAG, "$msg")
    }


}