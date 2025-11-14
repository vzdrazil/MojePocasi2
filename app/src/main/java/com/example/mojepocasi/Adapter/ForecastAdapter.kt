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
class ForecastAdapter: RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    private lateinit var binding: ForecastViewholderBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForecastAdapter.ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        binding= ForecastViewholderBinding.inflate(inflater,parent,false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ForecastAdapter.ViewHolder, position: Int) {
        val binding= ForecastViewholderBinding.bind(holder.itemView)
        val date= SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(differ.currentList[position].dtTxt.toString())
        val calendar= Calendar.getInstance()
        calendar.time=date
        val dayOfWeek=when(calendar.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Po"
            2 -> "Ut"
            3 -> "St"
            4 -> "Ct"
            5 -> "Pa"
            6 -> "So"
            7 -> "Ne"
            else -> "-"
        }
        binding.nameDayText.text=dayOfWeek
        val hour=calendar.get(Calendar.HOUR_OF_DAY)
        val amPm=if(hour<12) "AM" else "PM"
        val hour12=calendar.get(Calendar.HOUR)
       binding.hourText.text=hour12.toString()+amPm
        binding.tempText.text=differ.currentList[position].main?.temp?.let{Math.round(it)}.toString()+"°"
        val icon = when (differ.currentList[position].weather?.get(0)?.icon) {
            "01d" -> "sunny"
            "01n" -> "moon"

            "02d" -> "cloudy_sunny"
            "02n" -> "moon_cloud"

            "03d" -> "cloudy_sunny"
            "03n" -> "moon_cloud"

            "04d", "04n" -> "cloudy"

            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "thunderstorm"
            "13d", "13n" -> "snowy"
            "50d", "50n" -> "windy"

            else -> "sunny"
        }

        val drawableResourceId:Int=binding.root.resources.getIdentifier(
            icon,
            "drawable",binding.root.context.packageName


        )
        Glide.with(binding.root.context)
            .load(drawableResourceId).into(binding.pic)




    }
    inner class ViewHolder: RecyclerView.ViewHolder(binding.root)

    override fun getItemCount()=differ.currentList.size
    private val differCallback=object : DiffUtil.ItemCallback<ForecastResponseApi.data>() {
        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.data,
            newItem: ForecastResponseApi.data):Boolean{return oldItem == newItem}



    }
    val differ = AsyncListDiffer(this, differCallback)
}