package com.tqc.rentroom.ui.room

import MySqlConnection
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.tqc.rentroom.R
import com.tqc.rentroom.base.BaseFragment
import com.tqc.rentroom.eventbus.GetListRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class CreateRoomFragment(var idRoom: Int, val isEdit: Boolean) : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_room, container, false)
    }

    private var name: String? = null
    private var priceAft: String? = null
    private var priceNight: String? = null
    private var priceAll: String? = null
    private var note: String? = null

    companion object {
        @JvmStatic
        fun newInstance(idRoom: Int, isEdit: Boolean = false) = CreateRoomFragment(idRoom, isEdit)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { setDialog(it) }

        val editName = view.findViewById<EditText>(R.id.name)
        val editPriceAft = view.findViewById<EditText>(R.id.priceAfternoon)
        val editPriceNight = view.findViewById<EditText>(R.id.priceNight)
        val editAllDate = view.findViewById<EditText>(R.id.priceAllDay)
        val editNote = view.findViewById<EditText>(R.id.note)
        val btn = view.findViewById<CardView>(R.id.createBtn)
        if (isEdit) {
            view.findViewById<TextView>(R.id.valueBtn).text = "Cập nhật"
            val str =
                "SELECT ten_phong,gia_qua_trua,gia_qua_dem,gia_ngay_dem,ghi_chu,trang_thai FROM phongtro where ma_phong='$idRoom'"
            GlobalScope.launch(Dispatchers.IO) {
                MySqlConnection.executeMySQLQuery(str)?.let {
                    if (it.next()) {
                        activity?.runOnUiThread {
                            name = it.getString(1)
                            priceAft = it.getString(2)
                            priceNight = it.getString(3)
                            priceAll = it.getString(4)
                            note = it.getString(5)

                            editName?.setText(it.getString(1))
                            editPriceAft?.setText(it.getString(2))
                            editPriceNight?.setText(it.getString(3))
                            editAllDate?.setText(it.getString(4))
                            editNote?.setText(it.getString(5))
                        }
                    }
                }
            }
        }

        btn.setOnClickListener {
            when {
                editName.text.isNullOrBlank() -> {
                    showMessageDialog("Vui lòng nhập 'tên phòng'")
                }
                editPriceAft.text.isNullOrBlank() -> {
                    showMessageDialog("Vui lòng nhập 'giá qua trưa'")
                }
                editPriceNight.text.isNullOrBlank() -> {
                    showMessageDialog("Vui lòng nhập 'giá qua đêm'")
                }
                editAllDate.text.isNullOrBlank() -> {
                    showMessageDialog("Vui lòng nhập 'giá qua đêm'")
                }
                else -> {
                    if (isEdit) {
                        if ((editName.text.toString() == name && editPriceAft.text.toString() == priceAft && editPriceNight.text.toString() == priceNight && editAllDate.text.toString() == priceAll) && editName.text.toString() == note) {
                            showMessageDialog("Không có thay đổi để cập nhật")
                        } else {
                            val str = "UPDATE phongtro set ten_phong ='${editName.text}',gia_qua_trua ='${editPriceAft.text}',gia_qua_dem ='${editPriceNight.text}',gia_ngay_dem ='${editAllDate.text}',ghi_chu ='${editNote.text}',trang_thai ='1' where ma_phong='$idRoom'"
                            GlobalScope.launch(Dispatchers.IO) {
                                val isError: Boolean
                                val mess = if (MySqlConnection.executeUpdate(str) != 0) {
                                    isError = false
                                    "Chỉnh sửa phòng thành công"
                                } else {
                                    isError = true
                                    "Có lỗi xảy ra! \n Hãy thử lại sau"
                                }
                                activity?.runOnUiThread {
                                    showMessageDialog(
                                        mess,
                                        isHidden = !isError,
                                        onClick = {
                                            EventBus.getDefault().post(GetListRoom())
                                        })
                                }
                                activity?.runOnUiThread {
                                    showMessageDialog(
                                        "Chỉnh sửa thông tin thành công",
                                        isHidden = true,
                                        onClick = {
                                            EventBus.getDefault().post(GetListRoom())
                                        })
                                }
                            }
                        }
                    } else GlobalScope.launch(Dispatchers.IO) {
                        val str =
                            "INSERT INTO phongtro(ma_phong,ten_phong,gia_qua_trua,gia_qua_dem,gia_ngay_dem,ghi_chu,trang_thai) " +
                                    "VALUES('${idRoom + 1}','${editName.text}','${editPriceAft.text}','${editPriceNight.text}','${editAllDate.text}','${editNote.text}','1')"
                        MySqlConnection.executeInsert(str)?.let {
                            val isError: Boolean
                            val mess = if (it.isNotEmpty()) {
                                isError = false
                                "Tạo phòng mới thành công"
                            } else {
                                isError = true
                                "Có lỗi xảy ra! \n Hãy thử lại sau"
                            }
                            activity?.runOnUiThread {
                                showMessageDialog(
                                    mess,
                                    isHidden = isError,
                                    onClick = {
                                        if (!isError) {
                                            idRoom++
                                            editName.setText("")
                                            editPriceAft.setText("")
                                            editPriceNight.setText("")
                                            editAllDate.setText("")
                                            editNote.setText("")
                                            EventBus.getDefault().post(GetListRoom())
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}