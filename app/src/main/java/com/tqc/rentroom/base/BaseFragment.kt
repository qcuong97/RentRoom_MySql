package com.tqc.rentroom.base

import android.app.Activity
import androidx.fragment.app.Fragment
import com.tqc.rentroom.base.shared.ProcessDialog

open class BaseFragment : Fragment() {

    var loadingDialog: ProcessDialog? = null

    fun setDialog(activity: Activity) {
        loadingDialog = ProcessDialog(activity)
    }

    fun showLoadingDialog() {
        loadingDialog?.showLoadingDialog()
    }

    fun hideLoadingDialog() {
        loadingDialog?.hideLoadingDialog()
    }

    fun showMessageDialog(message: String, isHidden: Boolean ? = false, onClick: (() -> Unit?)? = null) {
        loadingDialog?.showAlertMessage(message, isHidden, onClick )
    }

    fun push(fragment : BaseFragment) {
        this.view?.clearFocus()
        val activity = activity as? BaseActivity ?: return
        activity.push(fragment)
    }
}