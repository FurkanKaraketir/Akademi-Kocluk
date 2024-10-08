package com.karaketir.akademi.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karaketir.akademi.R
import com.karaketir.akademi.databinding.StatsClassGridRowBinding
import com.karaketir.akademi.models.Statistic

class StatisticsRecyclerAdapter(
    private val statisticList: ArrayList<Statistic>
) : RecyclerView.Adapter<StatisticsRecyclerAdapter.StatisticHolder>() {

    class StatisticHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StatsClassGridRowBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stats_class_grid_row, parent, false)
        return StatisticHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StatisticHolder, position: Int) {
        with(holder) {

            if (statisticList.isNotEmpty() && position >= 0 && position < statisticList.size) {

                val myItem = statisticList[position]

                if (myItem.dersAdi == "Deneme") {
                    binding.statsDersAdi.text = "Deneme Tahlili"
                } else {
                    binding.statsDersAdi.text = myItem.dersAdi
                }
                binding.statsToplamCalisma.text = "${
                    myItem.toplamCalisma.toFloat().format(2)
                } dk"
                binding.statsCozulenSoru.text = "${
                    myItem.cozulenSoru.toFloat().format(2)
                } Soru"


            }

        }

    }

    override fun getItemCount(): Int {
        return statisticList.size
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)

}