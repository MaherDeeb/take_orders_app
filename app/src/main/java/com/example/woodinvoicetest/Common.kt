package com.example.woodinvoicetest

import android.content.Context
import android.util.Log
import java.io.File

object Common {
    fun getAppPath(context: Context): String {
        val dir = File(
            android.os.Environment.getExternalStorageDirectory().toString() +
                    File.separator + "invoices" + File.separator
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir.path + File.separator
    }
}