package com.tqc.rentroom.ui.list

import android.widget.CheckBox
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tqc.rentroom.R
import com.tqc.rentroom.base.shared.ProcessDialog
import com.tqc.rentroom.entities.CustomerModel
import com.tqc.rentroom.entities.RoomModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdapterCustomer(layoutID: Int, data : ArrayList<CustomerModel>) : BaseQuickAdapter<CustomerModel, BaseViewHolder>(layoutID, data) {

    override fun convert(holder: BaseViewHolder, item: CustomerModel) {
        holder.getView<CheckBox>(R.id.ckb).isChecked = item.isStatus
        holder.itemView.setOnClickListener {
            data.forEach {
                it.isStatus = false
            }
            item.isStatus = !item.isStatus
            notifyDataSetChanged()
        }
        holder.setText(R.id.nameTV, item.fullName)
        holder.setText(R.id.phoneTV, item.phoneNumber)
    }

    fun getItemSelected(): CustomerModel? {
        data.forEach{
            if (it.isStatus) {
                return it
            }
        }
        return  null
    }
}