package com.tqc.rentroom.ui

import android.content.Intent
import android.os.Bundle
import com.tqc.rentroom.R
import com.tqc.rentroom.base.BaseActivity
import com.tqc.rentroom.ui.list.ManagerRoomFragment

class MainActivity : BaseActivity() {

    override fun getContainerId() = R.id.container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        push(ManagerRoomFragment.newInstance())
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}