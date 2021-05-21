package com.tqc.rentroom.ui.list

import MySqlConnection
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tqc.rentroom.R
import com.tqc.rentroom.base.BaseFragment
import com.tqc.rentroom.entities.CustomerModel
import com.tqc.rentroom.entities.RoomModel
import com.tqc.rentroom.eventbus.GetListRoom
import com.tqc.rentroom.ui.room.CreateRoomFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ManagerRoomFragment : BaseFragment() {

    private var recyclerView: RecyclerView? = null
    private var listRoom: ArrayList<RoomModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_room, container, false)
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ManagerRoomFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { setDialog(it) }
        recyclerView = view.findViewById(R.id.listRoom)
        recyclerView?.layoutManager = GridLayoutManager(context, 2)
        getData()

        view.findViewById<FloatingActionButton>(R.id.addBtn).setOnClickListener {
            push(CreateRoomFragment.newInstance(getIdRoomMax()))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        showLoadingDialog()
        listRoom = ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            val str = "SELECT ten_phong,trang_thai,ma_phong FROM phongtro order by ten_phong ASC"
            MySqlConnection.executeMySQLQuery(str)?.let { rs ->
                rs.next()
                while (rs.next()) {
                    listRoom?.add(
                        RoomModel(
                            name = rs.getString(1),
                            status = rs.getInt(2),
                            idRoom = rs.getString(3)
                        )
                    )
                }
            }
        }
        hideLoadingDialog()
        val adapterRoom = AdapterRoom(R.layout.item_room_layout, listRoom!!, onSuccessRent = {
            val str = "UPDATE phongtro set trang_thai = '1' where ma_phong = '$it'"
            GlobalScope.launch(Dispatchers.IO) {
                if (MySqlConnection.executeUpdate(str) > 0) {
                    activity?.runOnUiThread {
                        showMessageDialog("Trả phòng thành công", onClick = {
                            getData()
                        }, isHidden = true)
                    }
                }
            }
        }, onEdit = {
            push(CreateRoomFragment.newInstance(it.toInt(), true))
        }, onRent = {
            showLoadingDialog()
            context?.let { context ->
                val dialog = Dialog(context)
                val view = View.inflate(context, R.layout.dialog_choose_customer_layout, null)
                val recyclerView = view.findViewById<RecyclerView>(R.id.listCustomer)
                val spnTime = view.findViewById<Spinner>(R.id.spnTime)
                val numberAft = view.findViewById<TextView>(R.id.numberAft)
                val numberNight = view.findViewById<TextView>(R.id.numberNight)
                val numberAll = view.findViewById<TextView>(R.id.numberAll)
                val listCustomer = ArrayList<CustomerModel>()
                var adapter: AdapterCustomer? = null
                var roomModel: RoomModel? = null
                val strRoom =
                    "SELECT ten_phong,gia_qua_trua,gia_qua_dem,gia_ngay_dem FROM phongtro where ma_phong='$it'"
                val str = "SELECT ma_kh,ten_kh,mobile from khach_hang"

                GlobalScope.launch(Dispatchers.IO) {
                    MySqlConnection.executeMySQLQuery(str)?.let { rs ->
                        while (rs.next()) {
                            val customer = CustomerModel()
                            customer.id = rs.getString(1)
                            customer.fullName = rs.getString(2)
                            customer.phoneNumber = rs.getString(3)
                            listCustomer.add(customer)
                        }
                        adapter = AdapterCustomer(R.layout.item_customer_layout, listCustomer)
                    }

                    MySqlConnection.executeMySQLQuery(strRoom)?.let {
                        if (it.next()) {
                            roomModel = RoomModel(
                                name = it.getString(1),
                                priceAft = it.getInt(2),
                                priceNight = it.getInt(3),
                                priceAll = it.getInt(4)
                            )
                        }
                    }
                    activity?.runOnUiThread {
                        view.findViewById<TextView>(R.id.roomTV).text = "Phòng: ${roomModel?.name}"
                        view.findViewById<TextView>(R.id.priceAft).text = "Phòng: ${roomModel?.priceAft}"
                        view.findViewById<TextView>(R.id.priceNight).text =
                            "Phòng: ${roomModel?.priceNight}"
                        view.findViewById<TextView>(R.id.priceAll).text = "Phòng: ${roomModel?.priceAll}"

                        recyclerView.layoutManager = LinearLayoutManager(context)
                        recyclerView.adapter = adapter

                        hideLoadingDialog()
                        dialog.show()
                    }
                }

                numberAll.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (s.toString().toInt() > 0) {
                            spnTime.isEnabled = false
                            spnTime.setSelection(23)
                        }
                    }

                })

                view.findViewById<CardView>(R.id.btnOke).setOnClickListener {
                    adapter?.getItemSelected()?.let {
                        roomModel?.run {
                            if (numberAft.text.isNullOrBlank() || numberNight.text.isNullOrBlank() || numberAll.text.isNullOrBlank()) {
                                showMessageDialog("Số lượng bằng không chứ không được bỏ trống")
                            } else {
                                val dateInt = SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                ).format(Calendar.getInstance().time)

                                showLoadingDialog()
                                val money = (numberAft.text.toString().toInt() * priceAft) + (numberNight.text.toString().toInt() * priceNight) + (numberAll.text.toString().toInt() * priceAll)

                                val strInsert1 =
                                    "\"INSERT INTO phieu_thue(ma_phieuthue,ma_kh,ma_nv,ngay_thue,ngay_tra,gio_thue,gio_tra,tien_tra_truoc)" +
                                            " VALUES('${getID()}','${it.id}','1','$dateInt','$dateInt','${
                                                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                                            }','${
                                                spnTime.selectedItem.toString().replace(" giờ", "")
                                            }','$money')\""
                                GlobalScope.launch(Dispatchers.IO){
                                    if (MySqlConnection.executeInsert(strInsert1)?.size ?: 0 > 0) {
                                        val strIn = "INSERT INTO chitiet_thue(ma_phieuthue,ma_phong,tinh_doanh_thu,sl_qua_trua,sl_qua_dem,sl_ngay_dem,gia_qua_trua,gia_qua_dem,gia_ngay_dem)" +
                                                " VALUES('${getID()}','$idRoom','${if (view.findViewById<CheckBox>(R.id.ckbTotal).isChecked) "Có" else "Không"}'," +
                                                "'${numberAft.text.toString().toInt()}','$${numberNight.text.toString().toInt()}','$${numberAll.text.toString().toInt()}'," +
                                                "'${roomModel?.priceAft}','$${roomModel?.priceNight}','$${roomModel?.priceAft}')"
                                        if (MySqlConnection.executeInsert(strIn)?.size ?: 0 > 0){
                                            val strUpdate = "UPDATE phongtro set trang_thai = '1' where ma_phong = '${idRoom}'"
                                            GlobalScope.launch(Dispatchers.IO) {
                                                if (MySqlConnection.executeUpdate(strUpdate) > 0) {
                                                    EventBus.getDefault().post(GetListRoom())
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } ?: showMessageDialog("Vui lòng chọn khách hàng")
                }
                view.findViewById<CardView>(R.id.btnCancel).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.setContentView(view)
            }
        })
        recyclerView?.adapter = adapterRoom
        adapterRoom.notifyDataSetChanged()
    }

    private fun getIdRoomMax(): Int {
        var max = 0
        for (i in 0..(listRoom?.size ?: 0)) {
            listRoom?.forEach { item ->
                if (item.idRoom.isNullOrBlank()) return@forEach
                val idRoom = item.idRoom.toInt()
                if (idRoom > max) {
                    max = idRoom
                }
            }
        }
        return max
    }

    private fun getID(): Int {
        var id = 1
        GlobalScope.launch(Dispatchers.IO) {
            MySqlConnection.executeMySQLQuery("SELECT ma_phieuthue FROM `phieu_thue` ORDER BY `ma_phieuthue` DESC")
                ?.let {
                    if (it.next()) {
                        id = it.getInt(1) + 1
                    }
                }
        }
        return id
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetData(onGetData: GetListRoom) {
        getData()
    }
}