package com.sr.salesmanapp.ui.home

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sr.salesmanapp.R
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.data.model.pojo.UsersModel
import com.sr.salesmanapp.databinding.FragmentHomeBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.adapter.HomeItemAdapter
import com.sr.salesmanapp.utils.Constants

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    lateinit var homeItemAdapter : HomeItemAdapter

    override fun initView() {
        homeItemAdapter = HomeItemAdapter(requireContext(), listOf("Shops","Setting"),itemClick)
        binding.rvHome.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = homeItemAdapter
            homeItemAdapter.notifyDataSetChanged()
        }
        setListener()
    }

    override fun observeData() {
    }

    private val itemClick : (Int) -> Unit = {
        when(it){
            0-> findNavController().navigate(R.id.ShopListFragment)
            1-> Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_LONG).show()
            2-> (requireActivity() as HomeActivity).logout()
        }
    }

    private fun setListener() {

    }



}