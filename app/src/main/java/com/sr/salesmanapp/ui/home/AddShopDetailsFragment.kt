package com.sr.salesmanapp.ui.home

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.trimmedLength
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.sr.salesmanapp.R
import com.sr.salesmanapp.SalesManApplication
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.data.model.pojo.ShopModelResponse
import com.sr.salesmanapp.data.network.ResultStatus
import com.sr.salesmanapp.databinding.FragmentShopDetailsBinding
import com.sr.salesmanapp.ui.base.BaseFragment
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.LocationUtils
import com.sr.salesmanapp.utils.ObserversUtil.observe
import com.sr.salesmanapp.utils.Params
import com.sr.salesmanapp.utils.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

@AndroidEntryPoint
class AddShopDetailsFragment : BaseFragment<FragmentShopDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentShopDetailsBinding
        get() = FragmentShopDetailsBinding::inflate

    val viewModel by viewModels<ShopListViewModel>()
    lateinit var user : FirebaseUser
    lateinit var dbReference : DatabaseReference
    lateinit var userId : String

    var shopModel : ShopModel? = null

    var isAdd = true
    var shopModelResponse : ShopModelResponse? = null
    override fun initView() {

        user = FirebaseAuth.getInstance().currentUser!!
        userId = user.uid
        dbReference = FirebaseDatabase.getInstance().getReference(Constants.SHOP_MODEL)
        setListener()

        try {

            shopModelResponse = requireArguments().get(Params.SHOP_MODEL_RESPONSE) as ShopModelResponse?
            isAdd = false
            setValues(shopModelResponse?.shopModel!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeData() {

        //for insert data
        observe(viewModel.insertResponse){
            handleResponse(it)
        }

        //for update data
        observe(viewModel.updateResponse){
            handleResponse(it)
        }
    }

    private fun handleResponse(it: ResultStatus<String>?) {
        when(it){
            is ResultStatus.Loading->{
                showProgress()
            }
            is ResultStatus.Success->{
                hideProgress()
                Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
            }
            is ResultStatus.Failure->{
                hideProgress()
                Toast.makeText(requireContext(), "${it.t.message}", Toast.LENGTH_LONG).show()
                println("fragment data error:${it.t.message}")
            }
        }
    }

    private fun setValues(shopModel: ShopModel) {
        this.shopModel = shopModel
        binding.etEmail.setText(shopModel.email)
        binding.etShopName.setText(shopModel.shopName)
        binding.etOwnerName.setText(shopModel.ownerName)
        binding.etContactOne.setText(shopModel.contact_one)
        binding.etContactTwo.setText(shopModel.contact_two)
        binding.etAddress.setText(shopModel.address)

        binding.btnSubmit.text = getString(R.string.update)
        binding.lblShop.text = getString(R.string.update_shop_details)
    }

    private fun setListener() {
        binding.btnSubmit.setOnClickListener {
            validateFields()
        }

        binding.ivMap.setOnClickListener {
            findNavController().navigate(R.id.MapFragment)
            //findNavController().navigate(R.id.MapsFragment)
            //startActivity(Intent(requireContext(),MapsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if(Constants.IS_LOCATION_UPDATE){
            SalesManApplication.lastAddressLatLog?.let {
                CoroutineScope(Dispatchers.IO).async {
                    var location = LocationUtils.getAddressFromLatLong(it.latitude,it.longitude,requireContext())
                    withContext(Dispatchers.Main){
                        binding.etAddress.setText(location)
                    }
                }
            }
            Constants.IS_LOCATION_UPDATE = false
        }
    }

    private fun validateFields() {
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

        if(binding.etShopName?.text?.trimmedLength()==0) {
            binding.etShopName?.error = "Shop Name Required"
            binding.etShopName?.requestFocus()
            return
        }
        else
            binding.etShopName?.error = null

        if(binding.etOwnerName?.text?.trimmedLength()==0) {
            binding.etOwnerName?.error = "Owner Name Required"
            binding.etOwnerName?.requestFocus()
            return
        }
        else
            binding.etOwnerName?.error = null


        if(binding.etContactOne?.text?.trimmedLength()==0) {
            binding.etContactOne?.error = "Contact One Required"
            binding.etContactOne?.requestFocus()
            return
        }
        else
            binding.etContactOne?.error = null

        /*if(binding.etContactTwo?.text?.trimmedLength()==0) {
            binding.etContactTwo?.error = "Contact Two Required"
            binding.etContactTwo?.requestFocus()
            return
        }
        else
            binding.etContactTwo?.error = null*/

        if(binding.etAddress?.text?.trimmedLength()==0) {
            binding.etAddress?.error = "Address Required"
            binding.etAddress?.requestFocus()
            return
        }
        else
            binding.etAddress?.error = null

        val shpModel = ShopModel(
            shopModelResponse?.shopModel?.shopId?:System.currentTimeMillis().toString(),
            binding.etShopName?.text?.toString(),
            binding.etOwnerName?.text?.toString(),
            binding.etContactOne?.text?.toString(),
            binding.etContactTwo?.text?.toString(),
            binding.etAddress?.text?.toString(),
            SalesManApplication.lastAddressLatLog?.latitude?.toString(),
            SalesManApplication.lastAddressLatLog?.longitude?.toString(),
            binding.etEmail?.text?.toString()
        )

        //saveDataInDb()
        if(isAdd)
            viewModel.insertShop(shpModel)
        else
            viewModel.updateShop(ShopModelResponse(shopModelResponse?.key,shpModel))

    }

    private fun showProgress(){
        (requireActivity() as HomeActivity).showProgress()
    }

    private fun hideProgress(){
        (requireActivity() as HomeActivity).hideProgress()
    }

    private fun saveDataInDb() {
        var storeId = System.currentTimeMillis().toString()
        dbReference.child(storeId).setValue(ShopModel(
            System.currentTimeMillis().toString(),
            binding.etShopName?.text?.toString(),
            binding.etOwnerName?.text?.toString(),
            binding.etContactOne?.text?.toString(),
            binding.etContactTwo?.text?.toString(),
            binding.etAddress?.text?.toString(),
            SalesManApplication.lastAddressLatLog?.latitude?.toString(),
            SalesManApplication.lastAddressLatLog?.longitude?.toString(),
            binding.etEmail?.text?.toString()
        )).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(requireContext(), "Shop saved successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            else
                Toast.makeText(requireContext(), "Failed to save!", Toast.LENGTH_SHORT).show()
        }
    }

}