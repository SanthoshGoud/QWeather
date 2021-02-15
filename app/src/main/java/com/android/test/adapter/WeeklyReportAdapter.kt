package com.android.test.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.test.R
import com.android.test.models.Bookmark
import com.android.test.network.response.WeatherReport
import kotlinx.android.synthetic.main.fragment_today_report.*
import kotlinx.android.synthetic.main.weekly_report_item.view.*
import java.text.DecimalFormat


class WeeklyReportAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: ItemClickListener? = null
    private var results: List<WeatherReport> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.weekly_report_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LocationViewHolder -> {
                holder.bind(results[position])
                holder.itemView.setOnClickListener { view ->
                    onItemClickListener?.onItemClick(
                        view,
                        position
                    )
                }
            }
        }
    }

    fun setBookMarks(bookmarks: List<WeatherReport>){
        results = bookmarks
    }

    fun getBookMarks(): List<WeatherReport> {
        return results
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class LocationViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){

        private val tempTv = itemView.todayTemp
        private val humidityTv = itemView.humidityTv
        private val pressureTv = itemView.pressureTv
        private val windDirTv = itemView.windDirectionTv
        private val windSpeedTv = itemView.windSppedTv
        private val dateTv = itemView.dateTv
        private val todayWeatherTv = itemView.todayWeather



        fun bind(location: WeatherReport){

            val kelvin: Double? = location.main?.temp
            val celsius = kelvin?.minus(273.15f)
            val df = DecimalFormat("#.##")
            df.format(celsius)

            tempTv.text = df.format(celsius).toString() +"\u2103"
            humidityTv.text = location.main?.humidity.toString() +"%"
            pressureTv.text = location.main?.pressure.toString()
            windSpeedTv.text = location.wind?.speed.toString()
            windDirTv.text = location.wind?.deg.toString()
            dateTv.text = location.dt_txt
            todayWeatherTv.text = location.weather?.get(0)?.main

        }

    }

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

}
