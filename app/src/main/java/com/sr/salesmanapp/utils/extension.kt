package com.sr.salesmanapp.utils

import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.sr.salesmanapp.data.network.ResultStatus
import com.sr.salesmanapp.utils.ObserversUtil.observe

object extension {



}

object ObserversUtil{
    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit){
        if(this is Fragment)
            liveData.observe(viewLifecycleOwner, Observer(body))
        else
            liveData.observe(this, Observer(body))
    }
}

