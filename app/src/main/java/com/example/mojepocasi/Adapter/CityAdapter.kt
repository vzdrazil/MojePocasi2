package com.example.mojepocasi.Adapter

import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.RecyclerView
import com.example.mojepocasi.databinding.ForecastViewholderBinding
import com.example.mojepocasi.model.ForecastResponseApi
import org.jetbrains.annotations.Async
import java.text.SimpleDateFormat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.AsyncListDiffer
import java.util.Calendar
import com.bumptech.glide.Glide
import com.example.mojepocasi.Activity.MainActivity
import com.example.mojepocasi.databinding.CityViewholderBinding
import com.example.mojepocasi.model.CityResponseApi
import android.content.Intent

class CityAdapter: RecyclerView.Adapter<CityAdapter.ViewHolder>() {
    private lateinit var binding: CityViewholderBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityAdapter.ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        binding= CityViewholderBinding.inflate(inflater,parent,false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: CityAdapter.ViewHolder, position: Int) {
        val binding= CityViewholderBinding.bind(holder.itemView)
        binding.cityText.text=differ.currentList[position].name
        binding.root.setOnClickListener {
            val intent=Intent(binding.root.context, MainActivity::class.java)
            intent.putExtra("lat",differ.currentList[position].lat)
            intent.putExtra("lon",differ.currentList[position].lon)
            intent.putExtra("name",differ.currentList[position].name)
            binding.root.context.startActivity(intent)
        }


    }
    inner class ViewHolder: RecyclerView.ViewHolder(binding.root)

    override fun getItemCount()=differ.currentList.size
    private val differCallback=object : DiffUtil.ItemCallback<CityResponseApi.CityResponseApiItem>() {
        override fun areItemsTheSame(
            oldItem: CityResponseApi.CityResponseApiItem,
            newItem: CityResponseApi.CityResponseApiItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CityResponseApi.CityResponseApiItem,
            newItem: CityResponseApi.CityResponseApiItem):Boolean{return oldItem == newItem}



    }
    val differ = AsyncListDiffer(this, differCallback)
}