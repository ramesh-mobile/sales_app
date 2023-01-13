package com.sr.salesmanapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sr.salesmanapp.databinding.LayoutHomeItemBinding

class HomeItemAdapter(val context: Context, val dataList : List<String>, var itemClick: (Int)->Unit) : RecyclerView.Adapter<HomeItemAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: LayoutHomeItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutHomeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvTitle.text = dataList.get(position)
        holder.binding.root.setOnClickListener { itemClick.invoke(position) }
    }

    override fun getItemCount() = dataList.size?:0
}