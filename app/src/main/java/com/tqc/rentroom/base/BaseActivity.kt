package com.tqc.rentroom.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.tqc.rentroom.base.shared.ProcessDialog

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getContainerId(): Int

    fun push(fragment: BaseFragment) {
        if (supportFragmentManager.isStateSaved) return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(getContainerId(), fragment, fragment::class.simpleName)
        transaction.addToBackStack(fragment::class.java.name)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.commit()
    }

}