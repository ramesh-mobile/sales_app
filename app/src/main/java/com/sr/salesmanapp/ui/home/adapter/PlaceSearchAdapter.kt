package com.sr.salesmanapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhv.lymouser.api.response.Prediction
import com.sr.salesmanapp.databinding.AdapterPlaceSearchBinding
import java.util.*

/**
 * Created by mobiiworld on 9/19/17.
 */

class PlaceSearchAdapter() : RecyclerView.Adapter<PlaceSearchAdapter.ViewHolder>() {
    var arrData = ArrayList<Prediction>()
    var onItemClickListener: OnItemClickListener? = null
    var onFavoriteClickListener: OnFavoriteClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(prediction: Prediction)
    }

    interface OnFavoriteClickListener {
        fun onFavoriteClick(prediction: Prediction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AdapterPlaceSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prediction = arrData[position]
        var data=prediction?.description?.split(",")
        holder.binding.placeName.text = data?.getOrNull(0)?:prediction?.description
        holder.binding.placeAddress.text = prediction?.description

        holder.itemView.tag = position

        holder.itemView.setOnClickListener({ v -> onItemClickListener!!.onItemClick(arrData[v.tag as Int]) })

    }

    override fun getItemCount(): Int {
        return arrData.size
    }

    fun setArrData(arrData: List<Prediction>) {
        this.arrData.clear()
        this.arrData.addAll(arrData)
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: AdapterPlaceSearchBinding) : RecyclerView.ViewHolder(binding.root)

}
