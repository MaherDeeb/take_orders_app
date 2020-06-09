package com.example.woodinvoicetest

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_customer_name.*
import java.util.*
import kotlin.math.round

class AddCustomerName : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer_name)
        totalSum.text = decimalFormatChanger.format(ordersInvoice.invoiceTotalSum).toString()


        soldInputs.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    var totalSumWithSold = ordersInvoice.invoiceTotalSum - s.toString().toDouble()
                    totalSumWithSold = round(totalSumWithSold * 100) / 100
                    totalSum.text = decimalFormatChanger.format(totalSumWithSold).toString()

                }
            }
        })
        confirmPayment.setOnClickListener { goToConfirmEverything() }
        cancelInvoiceButton.setOnClickListener { resetInvoiceYesNoDialog() }
        backButton.setOnClickListener { goToInvoiceReviewScreen() }
    }

    private fun goToConfirmEverything() {
        if (customerNameInput.text.isNotEmpty()) {
            ordersInvoice.customerName = customerNameInput.text.toString()
            ordersInvoice.invoiceId = getAndShowDate()
            if (soldInputs.text.isEmpty()) {
                ordersInvoice.invoiceSold = 0.0
            } else {
                ordersInvoice.invoiceSold = soldInputs.text.toString().toDouble()
            }

            ordersInvoice.invoiceNotes = invoiceNotesEditText.text.toString()

            val intent = Intent(this, ConfirmEverything::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                getString(R.string.missingCustomerNameMessageError),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun cancelInvoice() {
        ordersInvoice = InvoiceObjectClass()
        orderedProduct = OrderedProduct()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun resetInvoiceYesNoDialog() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.cancelInvoiceMessageTitle))
        builder.setMessage(getString(R.string.doYouWantToCancelInvoiceMessage))
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> cancelInvoice()
                DialogInterface.BUTTON_NEGATIVE -> doNothing()
            }
        }
        builder.setPositiveButton(getString(R.string.yesAnswer), dialogClickListener)
        builder.setNegativeButton(getString(R.string.noAnswer), dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun goToInvoiceReviewScreen() {
        val intent = Intent(this, InvoiceReviewScreen::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        goToInvoiceReviewScreen()
    }
}

fun getAndShowDate(): String {
    return Calendar.getInstance().timeInMillis.toString()

}