package com.sr.salesmanapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sr.salesmanapp.R
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.databinding.LayoutShopItemBinding
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.ViewUtils.gone
import com.sr.salesmanapp.utils.ViewUtils.visible

class ShopListAdapter(
    var context: Context,
    var userType: String,
    var data: List<ShopModel?>,
    var onPhoneOneClick: (String?) -> Unit,
    var onPhoneTwoClick: (String?) -> Unit,
    var onAddressClick: (String?) -> Unit,
    var onShareClick: (String?) -> Unit,
    var onDeleteClick: (String?) -> Unit,
    var onEditClick: (ShopModel?) -> Unit
) :
    RecyclerView.Adapter<ShopListAdapter.ViewHolder>() {

    var sortData : ArrayList<ShopModel> = data as ArrayList<ShopModel>

    init {
        sortData = data as ArrayList<ShopModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_shop_item,parent,false))
        return ViewHolder(
            LayoutShopItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var shopData = sortData?.get(position)
        var spnrClickInitialized = false
        holder.binding.tvShopName.text = shopData.shopName
        holder.binding.tvOwnerName.text = shopData.ownerName
        holder.binding.tvPhoneOne.text = shopData.contact_one
        holder.binding.tvPhoneTwo.text = shopData.contact_two
        holder.binding.tvAddress.text = shopData.address

        if(userType.equals(Constants.ADMIN)){
            holder.binding.ivOption?.visible()
        }
        else{
            holder.binding.ivOption?.gone()
        }

        if(shopData.contact_two.isNullOrBlank()) {
            holder.binding.tvPhoneTwo?.gone()
            holder.binding.ivPhoneTwo?.gone()
        }
        else {
            holder.binding.tvPhoneTwo?.visible()
            holder.binding.ivPhoneTwo?.visible()
        }



        holder.binding.spinnerOption?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(!spnrClickInitialized) {
                    spnrClickInitialized = true
                    return
                }
                when (p2){
                    0-> onEditClick?.invoke(shopData)
                    1-> onDeleteClick?.invoke(shopData?.shopId)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        holder.binding.ivOption?.setOnClickListener {holder.binding.spinnerOption?.performClick()}
        holder.binding.ivPhoneOne?.setOnClickListener { onPhoneOneClick.invoke(shopData.contact_one) }
        holder.binding.ivPhoneTwo?.setOnClickListener { onPhoneTwoClick.invoke(shopData.contact_two) }
        holder.binding.ivAddress?.setOnClickListener { onAddressClick.invoke(shopData.address) }
        holder.binding.ivShare?.setOnClickListener {
            shopData?.lat?.let{
                onShareClick.invoke("${shopData.lat},${shopData.lng}")
            }?:kotlin.run {
                onShareClick.invoke("${shopData.address}")
            }
        }
    }

    fun setSortDataList(sortDataTemp:List<ShopModel?>){
        this.sortData = sortDataTemp as ArrayList<ShopModel>
        notifyDataSetChanged()
    }

    fun clearDataList(){
        this.sortData?.clear()
        notifyDataSetChanged()
    }



    override fun getItemCount() = sortData?.size ?: 0

    inner class ViewHolder(var binding: LayoutShopItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}