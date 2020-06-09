package com.example.woodinvoicetest

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getAndShowDate()

        enterMaterialProductButton.setOnClickListener {
            goToWoodProductsScreen()
        }
        showInvoicesButton.setOnClickListener { goToShowInvoices() }
        exitProgramButton.setOnClickListener { exitApp() }
    }

    private fun goToWoodProductsScreen() {
        val intent = Intent(this, WoodProducts::class.java)
        ordersInvoice = InvoiceObjectClass()
        orderedProduct = OrderedProduct()
        startActivity(intent)
    }

    override fun onBackPressed() {
        exitApp()
    }

    private fun exitApp() {
        showYesNoDialog()
    }

    private fun showYesNoDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.finishTheAppTitle))
        builder.setMessage(getString(R.string.messageDoYouWantToFinishTheApp))
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> this.finishAffinity()
                DialogInterface.BUTTON_NEGATIVE -> doNothing()
            }
        }
        builder.setPositiveButton(getString(R.string.yesAnswer), dialogClickListener)
        builder.setNegativeButton(getString(R.string.noAnswer), dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun goToShowInvoices() {
        val intent = Intent(this, ShowInvoices::class.java)
        startActivity(intent)
    }

    private fun getAndShowDate() {
        val calender = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMAN)
        actualDate.text = formatter.format(calender)
    }
}
