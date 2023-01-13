package com.sr.salesmanapp.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sr.salesmanapp.R
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.databinding.FragmentShopListBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.adapter.ShopListAdapter
import com.sr.salesmanapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopListFragment : BaseFragment<FragmentShopListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentShopListBinding
        get() = FragmentShopListBinding::inflate

    var firebaseUser : FirebaseUser? = null
    lateinit var dbReference : DatabaseReference
    lateinit var userId : String
    var shopModelList = mutableListOf<ShopModel>()

    lateinit var shopLisAdapter : ShopListAdapter

    override fun initView() {
        shopLisAdapter = ShopListAdapter(requireContext(),shopModelList,onPhoneOneClick,onPhoneTwoClick,onAddressClick,onShareClick)
        binding.rvShops.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = shopLisAdapter
        }
        setListener()

        shopModelList?.clear()
        fetchDataFromDb()
    }

    private fun setListener() {
        binding.fabAddShop.setOnClickListener {
            findNavController().navigate(R.id.ShopDetailsFragment)
        }
    }

    val onPhoneOneClick : (String?)->Unit = {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${it}")
        startActivity(intent)
    }

    val onPhoneTwoClick : (String?)->Unit = {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${it}")
        startActivity(intent)
    }

    val onAddressClick : (String?)->Unit = {
        var latlng = it?.split(",")
        startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/maps?q=${latlng?.get(0)?.trim()?:0.0},${latlng?.get(1)?.trim()?:0.0}(Consumer)")))
    }

    val onShareClick : (String?)->Unit = {
        var latlng = it?.split(",")
        startActivity(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps?q=${latlng?.get(0)?.trim()?:0.0},${latlng?.get(1)?.trim()?:0.0}(Consumer)")
            type = "text/plain"
        })
    }

    private fun fetchDataFromDb() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        dbReference = FirebaseDatabase.getInstance().getReference(Constants.SHOP_MODEL)
        userId = firebaseUser?.uid!!

        showProgress()
        CoroutineScope(Dispatchers.IO).launch {
            dbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    hideProgress()
                }

                override fun onCancelled(p0: DatabaseError) {
                    hideProgress()
                }

            })

            dbReference.child(userId).addChildEventListener(object : ChildEventListener {


                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    hideProgress()
                    snapshot.getValue(ShopModel::class.java)?.let { shopModelList.add(it) }
                    shopLisAdapter.notifyDataSetChanged()
                    println("shop data : ${shopModelList}")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    hideProgress()
                    shopLisAdapter.notifyDataSetChanged()
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    hideProgress()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    hideProgress()
                }

                override fun onCancelled(error: DatabaseError) {
                    hideProgress()
                    Toast.makeText(requireContext(), "Unable to fetch data!", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
    }

    fun showProgress(){
        (requireActivity() as HomeActivity).showProgress()
    }

    fun hideProgress(){
        (requireActivity() as HomeActivity).hideProgress()
    }
}