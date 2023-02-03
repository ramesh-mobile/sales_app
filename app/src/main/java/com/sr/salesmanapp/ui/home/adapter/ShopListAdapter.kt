package com.sr.salesmanapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sr.salesmanapp.data.model.pojo.ShopModel
import com.sr.salesmanapp.databinding.LayoutShopItemBinding
import com.sr.salesmanapp.utils.Constants
import com.sr.salesmanapp.utils.ViewUtils.gone
import com.sr.salesmanapp.utils.ViewUtils.visible

class ShopListAdapter(
    var context: Context,
    var userType: String,
    var data: List<ShopModel>,
    var onPhoneOneClick: (String?) -> Unit,
    var onPhoneTwoClick: (String?) -> Unit,
    var onAddressClick: (String?) -> Unit,
    var onShareClick: (String?) -> Unit,
    var onDeleteClick: (String?) -> Unit
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
        holder.binding.tvShopName.setText(shopData.shopName)
        holder.binding.tvOwnerName.setText(shopData.ownerName)
        holder.binding.tvPhoneOne.setText(shopData.contact_one)
        holder.binding.tvPhoneTwo.setText(shopData.contact_two)
        holder.binding.tvAddress.setText(shopData.address)

        if(userType.equals(Constants.ADMIN)){
            holder.binding.ivDelete?.visible()
        }
        else{
            holder.binding.ivDelete?.gone()
        }

        holder.binding.ivDelete?.setOnClickListener {
            onDeleteClick?.invoke(shopData?.shopId)
        }

        if(shopData.contact_two.isNullOrBlank()) {
            holder.binding.tvPhoneTwo?.gone()
            holder.binding.ivPhoneTwo?.gone()
        }
        else {
            holder.binding.tvPhoneTwo?.visible()
            holder.binding.ivPhoneTwo?.visible()
        }

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

    fun setSortDataList(sortDataTemp:List<ShopModel>){
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