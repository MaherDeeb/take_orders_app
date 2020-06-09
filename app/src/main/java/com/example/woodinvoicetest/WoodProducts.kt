package com.example.woodinvoicetest

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_woodproducts.*
import java.lang.Exception

class WoodProducts : AppCompatActivity() {

    private var currentPropertyNumber: Int = 0
    private var numberOfProducts: Int = 0
    private var chosenProduct = ProductsObjests[0]
    private var availableProperties = listOf("")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_woodproducts)

        // Entering point
        currentPropertyNumber = orderedProduct.lastNonEmptyProperty
        chosenProduct = ProductsObjests[orderedProduct.productId]
        productHintTextview.text =
            """${getString(R.string.chooseWord)} ${propertiesNames[currentPropertyNumber]}"""
        nextProperty(0, 0)

        getPropertyValue()
        backButton.setOnClickListener { nextProperty(-1, 0) }
        cancelInvoiceButton.setOnClickListener { resetInvoiceYesNoDialog() }
    }

    override fun onBackPressed() {
        nextProperty(-1, 0)
    }

    private fun getPropertyValue() {
        val allButtonsArray = arrayOf(
            button1_1, button1_2, button1_3, button1_4, button1_5,
            button2_1, button2_2, button2_3, button2_4, button2_5
        )
        for (i in allButtonsArray.indices) {
            allButtonsArray[i].setOnClickListener { nextProperty(1, i) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun nextProperty(forwardOrBackNumber: Int, chosenOption: Int) {
        if (currentPropertyNumber == 0 && forwardOrBackNumber == -1) {
            goToMainScreen()
        } else {
            if (currentPropertyNumber == 0 && forwardOrBackNumber == 1) {

                chosenProduct = ProductsObjests[chosenOption]
                orderedProduct.productId = chosenProduct.productId
                orderedProduct.productName = chosenProduct.productName
                orderedProduct.productType = chosenProduct.productType
                orderedProduct.marketProductName = chosenProduct.arabicProductName

                theChosenTextView.text = chosenProduct.arabicProductName
                progressToNextProperty(forwardOrBackNumber, chosenOption, null)

            } else if (availableProperties[chosenOption] == getString(R.string.doublePlaceholder) && forwardOrBackNumber == 1) {
                manualValueEnteringEditTextAlertDialog(forwardOrBackNumber, chosenOption)
            } else {
                progressToNextProperty(forwardOrBackNumber, chosenOption, null)
            }
        }
    }

    private fun progressToNextProperty(
        forwardOrBackNumber: Int,
        chosenOption: Int,
        manualEnteredValue: String?
    ) {
        var updateChosenOption = chosenOption
        if (manualEnteredValue != null) {
            availableProperties = listOf(manualEnteredValue)
            updateChosenOption = 0
        }
        assignOrderedProductValue(availableProperties[updateChosenOption])
        getAvailableProperties(forwardOrBackNumber)

        if (currentPropertyNumber > 6) {
            currentPropertyNumber = 6
            goToQuantityScreen()
        } else {
            updateScreen()
        }
    }

    private fun getAvailableProperties(forwardOrBackNumber: Int) {
        do {
            currentPropertyNumber += forwardOrBackNumber
            availableProperties = when (currentPropertyNumber) {
                0 -> availableProducts
                1 -> chosenProduct.availableWidth.map { it.toString() }
                2 -> chosenProduct.availableThickness.map { it.toString() }
                3 -> chosenProduct.availableLength.map { it.toString() }
                4 -> chosenProduct.availableProperty1
                5 -> chosenProduct.availableProperty2
                6 -> chosenProduct.availableProperty3
                else -> emptyList()
            }
            numberOfProducts = availableProperties.size
        } while (numberOfProducts == 0 && currentPropertyNumber < 7)
    }

    @SuppressLint("SetTextI18n")
    fun assignOrderedProductValue(chosenProperty: String) {
        try {
            when (currentPropertyNumber) {
                1 -> orderedProduct.productWidth = chosenProperty.toDouble() / 100 // cm to m
                2 -> orderedProduct.productThickness = chosenProperty.toDouble() / 1000 // mm to m
                3 -> orderedProduct.productLength = chosenProperty.toDouble()
                4 -> orderedProduct.productProperty1 = chosenProperty
                5 -> orderedProduct.productProperty2 = chosenProperty
                6 -> orderedProduct.productProperty3 = chosenProperty
            }
        } catch (e: Exception) {
            Log.e("Error", "Not possible to assign values")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateScreen() {
        productHintTextview.text =
            """${getString(R.string.chooseWord)} ${propertiesNames[currentPropertyNumber]}"""
        changeButtonVisibility(numberOfProducts)
        changeButtonText(numberOfProducts)
        orderedProduct.lastNonEmptyProperty = currentPropertyNumber

        updateChosenTextHint()

    }

    @SuppressLint("SetTextI18n")
    private fun changeButtonText(numberOfProduct: Int) {
        val allButtonsArray = arrayOf(
            button1_1, button1_2, button1_3, button1_4, button1_5,
            button2_1, button2_2, button2_3, button2_4, button2_5
        )

        for (i in 0 until numberOfProduct) {
            if (availableProperties[i] == getString(R.string.doublePlaceholder)) {
                allButtonsArray[i].text =
                    """${getString(R.string.enterTextButton)} ${propertiesNames[currentPropertyNumber]}"""
            } else {
                allButtonsArray[i].text = availableProperties[i]
            }
        }

    }

    private fun changeButtonVisibility(numberOfProduct: Int) {
        val allButtonsArray = arrayOf(
            button1_1, button1_2, button1_3, button1_4, button1_5,
            button2_1, button2_2, button2_3, button2_4, button2_5
        )
        for (i in 0 until numberOfProduct) {
            allButtonsArray[i].visibility = View.VISIBLE
        }
        for (i in numberOfProduct..9) {
            allButtonsArray[i].visibility = View.INVISIBLE
        }
    }

    private fun goToMainScreen() {
        val intentBack = Intent(this, MainActivity::class.java)
        startActivity(intentBack)
        finish()
    }

    private fun goToQuantityScreen() {
        val intent = Intent(this, QuantityAndPrice::class.java)
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

    @SuppressLint("SetTextI18n")
    fun updateChosenTextHint() {
        var newText = ""
        if (currentPropertyNumber > 0) {
            theChosenTextView.text =
                """${orderedProduct.marketProductName} """
        }
        if (currentPropertyNumber > 1 && orderedProduct.productWidth > 0.0) {
            newText = theChosenTextView.text.toString()
            theChosenTextView.text =
                """ $newText${orderedProduct.productWidth * 100} ${getString(
                    R.string.cm
                )} """
        }
        if (currentPropertyNumber > 2 && orderedProduct.productThickness > 0.0) {
            newText = theChosenTextView.text.toString()
            theChosenTextView.text =
                """ $newText${orderedProduct.productThickness * 1000} ${getString(
                    R.string.mm
                )} """
        }

        if (currentPropertyNumber > 3 && orderedProduct.productLength > 0.0) {
            newText = theChosenTextView.text.toString()
            theChosenTextView.text =
                """ $newText${orderedProduct.productLength} ${getString(
                    R.string.m
                )} """
        }

        if (currentPropertyNumber > 4) {
            newText = theChosenTextView.text.toString()
            theChosenTextView.text =
                """ $newText${orderedProduct.productProperty1} """
        }

        if (currentPropertyNumber > 5) {
            newText = theChosenTextView.text.toString()
            theChosenTextView.text =
                """ $newText${orderedProduct.productProperty2} """
        }

        if (currentPropertyNumber > 6) {
            newText = theChosenTextView.text.toString()
            theChosenTextView.text =
                """ $newText${orderedProduct.productProperty3} """
        }
    }

    @SuppressLint("InflateParams")
    private fun manualValueEnteringEditTextAlertDialog(
        forwardOrBackNumber: Int,
        chosenOption: Int
    ) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(propertiesNames[currentPropertyNumber])
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.enterPropertyEditText)
        builder.setView(dialogLayout)
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> validateManualValues(
                    forwardOrBackNumber,
                    chosenOption,
                    editText.text
                )
            }
        }
        builder.setPositiveButton(getString(R.string.enterTextButton), dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun validateManualValues(
        forwardOrBackNumber: Int,
        chosenOption: Int,
        manualEnteredValue: Editable?
    ) {
        if (manualEnteredValue.isNullOrEmpty()) {
            doNothing()
        } else {
            progressToNextProperty(
                forwardOrBackNumber,
                chosenOption,
                manualEnteredValue.toString()
            )
        }
    }

}
