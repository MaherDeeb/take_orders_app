package com.example.woodinvoicetest

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_quantity_and_price.*
import java.lang.Exception
import kotlin.math.round

class QuantityAndPrice : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quantity_and_price)

        totalAmountInput?.setText(orderedProduct.numberOfUnits.toString())
        unitPriceInput?.setText(orderedProduct.unitPrice.toString())

        if (orderedProduct.productLength * orderedProduct.productThickness * orderedProduct.productWidth < 1e-5 &&
            orderedProduct.productType == ProductType.Sawnwood.toString()
        ) {
            totalAmountLabel.text = getString(R.string.totalVolumeText)
            totalAmountInput.inputType = unitPriceInput.inputType
        } else {
            totalAmountLabel.text = getString(R.string.totalNumberText)
            totalAmountInput.inputType = InputType.TYPE_CLASS_NUMBER
        }

        totalAmountInput.addTextChangedListener(object : TextWatcher {
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
                val totalVolume = round(calculateVolume() * 10e+5) / 10e+5
                totalVolumeoutput.text = totalVolume.toString()

                if (unitPriceInput.text.isNotEmpty()) {
                    val totalPrice = round(calculateTotalPrice() * 100) / 100
                    totalPriceOutput.text = decimalFormatChanger.format(totalPrice).toString()
                }
            }
        })


        unitPriceInput.addTextChangedListener(object : TextWatcher {
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
                    val totalPrice = round(calculateTotalPrice() * 100) / 100
                    totalPriceOutput.text = decimalFormatChanger.format(totalPrice).toString()
                }
            }
        })
        backButton.setOnClickListener { goToWoodProductsScreen() }
        addToInvoiceButton.setOnClickListener { goToInvoiceReviewScreen() }
        cancelInvoiceButton.setOnClickListener { resetInvoiceYesNoDialog() }
    }

    private fun goToInvoiceReviewScreen() {
        if (unitPriceInput.text.isNotEmpty() && totalAmountInput.text.isNotEmpty()) {
            assignDataToOrderedProduct()
            ordersInvoice.orderedProductList.add(orderedProduct)
            val intent = Intent(this, InvoiceReviewScreen::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                getString(R.string.errorMessageNoAmountAndUnitPrice),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun assignDataToOrderedProduct() {
        assignValuesToOrderedProduct()
        var totalPrice = 0.0
        if (orderedProduct.productType == ProductType.Sawnwood.toString()) {
            val totalVolume = calculateVolume()
            orderedProduct.totalVolume = round(totalVolume * 10e+5) / 10e+5
            totalPrice = orderedProduct.unitPrice * totalVolume
        }
        if (orderedProduct.productType == ProductType.Plywood.toString()) {
            totalPrice = orderedProduct.unitPrice * orderedProduct.numberOfUnits
        }
        orderedProduct.totalPrice = round(totalPrice * 100) / 100
    }

    fun calculateVolume(): Double {
        val totalAmount = try {
            totalAmountInput.text.toString().toInt()
        } catch (e: Exception) {
            0
        }

        var totalVolume = 0.0
        if (orderedProduct.productType == ProductType.Sawnwood.toString()) {
            totalVolume =
                totalAmount * orderedProduct.productLength * orderedProduct.productWidth * orderedProduct.productThickness
        }

        if (orderedProduct.productLength * orderedProduct.productWidth * orderedProduct.productThickness < 1e-5 &&
            orderedProduct.productType == ProductType.Sawnwood.toString()
        ) {
            totalVolume = try {
                totalAmountInput.text.toString().toDouble()
            } catch (e: Exception) {
                0.0
            }

        }
        return totalVolume
    }

    fun calculateTotalPrice(): Double {
        var totalPrice = 0.0
        assignValuesToOrderedProduct()

        if (orderedProduct.productType == ProductType.Sawnwood.toString()) {
            val totalVolume = calculateVolume()
            orderedProduct.totalVolume = totalVolume
            totalPrice = orderedProduct.unitPrice * totalVolume
        }
        if (orderedProduct.productType == ProductType.Plywood.toString()) {
            totalPrice = orderedProduct.unitPrice * orderedProduct.numberOfUnits
        }
        return totalPrice
    }

    private fun assignValuesToOrderedProduct() {
        try {
            orderedProduct.unitPrice = unitPriceInput.text.toString().toDouble()
        } catch (e: Exception) {
            orderedProduct.unitPrice = 0.0
        }

        try {
            orderedProduct.numberOfUnits = totalAmountInput.text.toString().toInt()
        } catch (e: Exception) {
            orderedProduct.numberOfUnits = 0
        }
    }

    override fun onBackPressed() {
        goToWoodProductsScreen()
    }

    private fun goToWoodProductsScreen() {
        val intent = Intent(this, WoodProducts::class.java)
        startActivity(intent)
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
}
