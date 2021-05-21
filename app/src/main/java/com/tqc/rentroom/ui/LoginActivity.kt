package com.tqc.rentroom.ui

import MySqlConnection
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import com.tqc.rentroom.R
import com.tqc.rentroom.base.BaseActivity
import com.tqc.rentroom.base.shared.ProcessDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    override fun getContainerId() = R.id.container

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val dialog = ProcessDialog(this)
        val editUN = findViewById<EditText>(R.id.userNameEdt)
        val editPW = findViewById<EditText>(R.id.passwordEdt)
        findViewById<LinearLayout>(R.id.btnLogin)?.setOnClickListener {
            dialog.showLoadingDialog()
            editUN.setText("admin")
            editPW.setText("1")
            val str = "SELECT * FROM nhan_vien WHERE ten_nv = '${editUN.text}' AND cap_bac = ('quan_tri' OR 'nhan_vien')  LIMIT 1"
            if (editPW.text.isNullOrBlank() || editUN.text.isNullOrBlank()) {
                dialog.showAlertMessage("Vui lòng nhập đầy đủ thông tin đăng nhập")
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    MySqlConnection.executeMySQLQuery(str)?.let { rs ->
                        if (rs.next()) {
                            val pass = rs.getString(5)
                            if (pass == editPW.text.toString()) {
                                rs.close()
                                dialog.hideLoadingDialog()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            } else {
                                rs.close()
                                runOnUiThread {
                                    dialog.showAlertMessage("Sai mật khẩu hoặc tài khoản")
                                }
                            }
                        } else {
                            rs.close()
                            runOnUiThread {
                                dialog.showAlertMessage("Sai mật khẩu hoặc tài khoản")
                            }
                        }
                    }
                }
            }
        }
    }
}