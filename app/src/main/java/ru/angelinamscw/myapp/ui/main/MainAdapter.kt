package ru.angelinamscw.myapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.angelinamscw.myapp.R
import ru.angelinamscw.domain.BaseConstants
import ru.angelinamscw.myapp.ui.common.BaseViewType
import ru.angelinamscw.domain.Record
import ru.angelinamscw.myapp.ui.common.Separator
import ru.angelinamscw.myapp.ui.common.ApplicableForMineList
import ru.angelinamscw.myapp.ui.common.ILongClicked
import ru.angelinamscw.myapp.ui.common.UnknownTypeViewHolder
import java.text.SimpleDateFormat
import kotlin.math.abs
import kotlin.math.max

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<ApplicableForMineList> = listOf()
    var itemLongClicked: ILongClicked? = null
    private val format = SimpleDateFormat(BaseConstants.HEADER_TIME_FORMAT)

    override fun getItemViewType(position: Int) = when (items[position]) {
        is Record -> BaseViewType.RECORD
        is Separator -> BaseViewType.SEPARATOR
        else -> BaseViewType.UNKNOWN_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            BaseViewType.SEPARATOR -> MainDataSeparatorViewHolder(layoutInflater, parent)
            BaseViewType.RECORD -> MainDataViewHolder(layoutInflater, parent)
            else -> UnknownTypeViewHolder(layoutInflater, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Record -> {
                holder as MainDataViewHolder
                holder.apply {
                    time.text = format.format(item.timestamp)
                    systolicPressure.text = item.systolicPressure.toString()
                    diastolicPressure.text = item.diastolicPressure.toString()
                    heartRate.text = item.heartRate.toString()
                    val d = max(
                        abs(item.diastolicPressure - BaseConstants.NORMAL_DIASTOLIC_PRESSURE),
                        abs(item.systolicPressure - BaseConstants.NORMAL_SYSTOLIC_PRESSURE)
                    )
                    when (d) {
                        in 0..9 -> card.setBackgroundResource(R.drawable.bg_fine)
                        in 10..19 -> card.setBackgroundResource(R.drawable.bg_slight_deviation_from_the_norm)
                        in 20..29 -> card.setBackgroundResource(R.drawable.bg_average_deviation_from_the_norm)
                        in 30..39 -> card.setBackgroundResource(R.drawable.bg_significant_deviation_from_the_norm)
                        in 40..1000 -> card.setBackgroundResource(R.drawable.bg_critical_deviation_from_the_norm)
                    }
                    card.setOnLongClickListener {
                        itemLongClicked?.onItemLongClicked(holder.itemView, position, item)
                        true
                    }
                }
            }
            is Separator -> {
                holder as MainDataSeparatorViewHolder
                holder.title.text = item.title
            }
            else -> {
                holder as UnknownTypeViewHolder
                holder.title.text = item.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}