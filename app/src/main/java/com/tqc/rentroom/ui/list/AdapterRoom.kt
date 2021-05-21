package com.tqc.rentroom.ui.list

import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tqc.rentroom.R
import com.tqc.rentroom.base.shared.ProcessDialog
import com.tqc.rentroom.entities.RoomModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdapterRoom(layoutID: Int, data : ArrayList<RoomModel>,private val onSuccessRent: ((String) -> Unit),
                  private val onEdit: ((String) -> Unit),private val onRent: (String) -> Unit) : BaseQuickAdapter<RoomModel, BaseViewHolder>(layoutID, data) {

    override fun convert(holder: BaseViewHolder, item: RoomModel) {
        holder.setText(R.id.nameTV, item.name)
        val statusName: String
        val color: Int
        when (item.status) {
            1 -> {
                statusName = "PHÒNG TRỐNG"
                color = R.color.white
            }
            0 -> {
                statusName = "ĐANG THUÊ"
                color = R.color.colorPrimary
            }
            else -> {
                statusName = "Quá Hạn"
                color = R.color.red
            }
        }
        holder.setText(R.id.statusRentTV, statusName)
        holder.getView<CardView>(R.id.roomLayOut).setCardBackgroundColor(ContextCompat.getColor(context, color))
        holder.getView<CardView>(R.id.btnRent).setOnClickListener {
            if (item.status == 0) {
                item.idRoom?.let { it1 -> onSuccessRent.invoke(it1) }
            } else if (item.status == 1) {
                item.idRoom?.let { it1 -> onRent.invoke(it1) }
            }
        }
        holder.getView<ImageView>(R.id.editImg).setOnClickListener {
            item.idRoom?.let { it1 -> onEdit.invoke(it1) }
        }
    }
}