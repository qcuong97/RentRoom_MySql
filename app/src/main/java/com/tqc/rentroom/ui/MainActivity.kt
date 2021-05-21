package com.tqc.rentroom.ui

import android.os.Bundle
import com.tqc.rentroom.R
import com.tqc.rentroom.base.BaseActivity
import com.tqc.rentroom.base.shared.ProcessDialog
import com.tqc.rentroom.ui.list.ManagerRoomFragment

class MainActivity : BaseActivity() {

    override fun getContainerId() = R.id.container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        push(ManagerRoomFragment.newInstance())
    }
}