package com.sr.salesmanapp.ui.home

import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.sr.salesmanapp.R
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.data.model.pojo.UsersModel
import com.sr.salesmanapp.databinding.FragmentShopListBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.ui.home.adapter.ShopListAdapter
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.Params
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
        var userType : String = Constants.NORMAL
        Prefs.getString(Constants.USER_MODEL,null)?.let {
            userType = Gson().fromJson<UsersModel>(it,UsersModel::class.java).userType!!
        }

        shopLisAdapter = ShopListAdapter(requireContext(),userType,shopModelList,onPhoneOneClick,onPhoneTwoClick,onAddressClick,onShareClick,onDeleteClick,onEditClick)
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

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                shopLisAdapter?.setSortDataList(shopModelList?.filter { p0?.toString()
                    ?.let { it1 -> it.shopName?.contains(it1,ignoreCase = true) } ==true })
            }

        })


    }

    private val onPhoneOneClick : (String?)->Unit = {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${it}")
        startActivity(intent)
    }

    private val onPhoneTwoClick : (String?)->Unit = {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${it}")
        startActivity(intent)
    }

    private val onAddressClick : (String?)->Unit = {
        var latlng = it?.split(",")
        startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/maps?q=${latlng?.get(0)?.trim()?:0.0},${latlng?.get(1)?.trim()?:0.0}(Consumer)")))
    }

    private val onShareClick : (String?)->Unit = {
        var latlng = it?.split(",")
        startActivity(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps?q=${latlng?.get(0)?.trim()?:0.0},${latlng?.get(1)?.trim()?:0.0}(Consumer)")
            type = "text/plain"
        })
    }

    private val onDeleteClick : (String?)->Unit = {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure to delete?")
            .setPositiveButton("Yes"){dialogInterface,which->
                deleteItemRequest(it)
            }.setNegativeButton("No"){dialogInterface,which->
                dialogInterface.dismiss()
            }
            .create()
            //.setCancelable(false)
            .show()

    }

    private val onEditClick : (ShopModel?)->Unit = {
        findNavController().navigate(R.id.ShopDetailsFragment, bundleOf(Pair(Params.SHOP_MODEL,it)))
    }

    private fun deleteItemRequest(deleteId: String?) {
        Toast.makeText(requireContext(), "Under process", Toast.LENGTH_SHORT).show()
        dbReference = FirebaseDatabase.getInstance().getReference(Constants.SHOP_MODEL)
        dbReference.orderByChild("shopId").equalTo(deleteId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    Toast.makeText(requireContext(), "data deleted", Toast.LENGTH_SHORT).show()
                    binding.etSearch?.setText("")
                    it.ref.removeValue()
                    shopLisAdapter.clearDataList()
                    shopModelList?.clear()
                    fetchDataFromDb()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "error to deleted", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        binding.etSearch?.setText("")
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

            dbReference./*child(userId).*/addChildEventListener(object : ChildEventListener {


                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    hideProgress()
                    snapshot.getValue(ShopModel::class.java)?.let { shopModelList.add(it) }
                    shopLisAdapter?.setSortDataList(shopModelList)
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

    private fun showProgress(){
        (requireActivity() as HomeActivity).showProgress()
    }

    private fun hideProgress(){
        (requireActivity() as HomeActivity).hideProgress()
    }


}