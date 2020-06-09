package com.example.woodinvoicetest

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_invoice_review_screen.*
import kotlin.math.round

class InvoiceReviewScreen : AppCompatActivity() {

    private var rowsList: ArrayList<TableRow> = ArrayList()
    private var deleteButtonsList: ArrayList<ImageButton> = ArrayList()
    private var editButtonsList: ArrayList<ImageButton> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_review_screen)

        calculateTotalSum()
        // create titles
        val firstRow = TableRow(this)
        createTableColumnsTitles(firstRow)
        invoiceTable.addView(firstRow)
        // create rows
        for (orderedProductI in ordersInvoice.orderedProductList.indices) {
            orderedProduct = ordersInvoice.orderedProductList[orderedProductI]
            val row = TableRow(this)
            val productNumber = orderedProductI + 1
            showOneProduct(row, productNumber)
            invoiceTable.addView(row)
            rowsList.add(row)
        }

        for (i in deleteButtonsList.indices) {
            deleteButtonsList[i].setOnClickListener { removeProductYesNoDialog(i) }
        }

        for (i in editButtonsList.indices) {
            editButtonsList[i].setOnClickListener { editProduct(i) }
        }

        paymentButton.setOnClickListener { goToAddCustomerName() }
        addProductbutton.setOnClickListener { addNewProduct() }
        cancelButton.setOnClickListener { resetInvoiceYesNoDialog() }
    }

    private fun createDeleteButton(row: TableRow) {
        val deleteButton: ImageButton = ImageButton(this)
        deleteButton.apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }
        deleteButton.setBackgroundResource(R.drawable.my_delete_icon)
        row.addView(deleteButton)
        deleteButtonsList.add(deleteButton)
    }

    private fun createEditButton(row: TableRow) {
        val editButton: ImageButton = ImageButton(this)
        editButton.apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        }
        editButton.setBackgroundResource(R.drawable.my_edit_icon)
        row.addView(editButton)
        editButtonsList.add(editButton)
    }

    private fun createTextView(row: TableRow, invoiceText: String) {
        val textView: TextView = TextView(this)
        textView.apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            text = invoiceText
            textSize = 14F
            gravity = 1
        }
        textView.margin(2F, 2F, 2F, 2F)
        row.addView(textView)
    }


    private fun goToAddCustomerName() {
        val intent = Intent(this, AddCustomerName::class.java)
        startActivity(intent)
    }

    private fun addNewProduct() {
        orderedProduct = OrderedProduct()
        val intent = Intent(this, WoodProducts::class.java)
        startActivity(intent)
    }

    private fun cancelInvoice() {
        ordersInvoice = InvoiceObjectClass()
        orderedProduct = OrderedProduct()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun calculateTotalSum() {
        var totalSum = 0.0
        for (productI in ordersInvoice.orderedProductList) {
            totalSum += productI.totalPrice
        }
        ordersInvoice.invoiceTotalSum = round(totalSum * 100) / 100
        totalPrice.text = decimalFormatChanger.format(ordersInvoice.invoiceTotalSum).toString()
    }

    private fun createTableColumnsTitles(row: TableRow) {
        var invoiceText = getString(R.string.spaceSeparator)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.spaceSeparator)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.idText)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.propertiesLabelText)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.totalNumberShortText)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.totalVolumeShortText)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.unitPriceText)
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.totalPriceText)
        createTextView(row, invoiceText)
    }

    private fun showOneProduct(row: TableRow, productNumber: Int) {
        createDeleteButton(row)
        createEditButton(row)

        var invoiceText = if (productNumber < 10) {
            "  00$productNumber  "
        } else {
            "  0$productNumber  "
        }
        createTextView(row, invoiceText)

        invoiceText = orderedProduct.marketProductName

        if (orderedProduct.productWidth > 0.0) {
            invoiceText += """ ${getString(R.string.widthText)} ${(orderedProduct.productWidth * 100)} ${getString(
                R.string.cm
            )} """
        }

        if (orderedProduct.productThickness > 0.0) {
            invoiceText += """ ${getString(R.string.thicknessText)} ${(orderedProduct.productThickness * 1000)} ${getString(
                R.string.mm
            )} """
        }

        if (orderedProduct.productLength > 0.0) {
            invoiceText += """ ${getString(R.string.lengthText)} ${orderedProduct.productLength} ${getString(
                R.string.m
            )} """
        }

        if (orderedProduct.productProperty1.isNotEmpty()) {
            invoiceText += """ ${orderedProduct.productProperty1} """
        }
        if (orderedProduct.productProperty2.isNotEmpty()) {
            invoiceText += """ ${orderedProduct.productProperty2} """
        }
        if (orderedProduct.productProperty3.isNotEmpty()) {
            invoiceText += """ ${orderedProduct.productProperty3} """
        }

        createTextView(row, invoiceText)

        invoiceText = if (orderedProduct.numberOfUnits > 0) {
            """ ${orderedProduct.numberOfUnits} """
        } else {
            """ ${getString(R.string.notGivenText)} """
        }

        createTextView(row, invoiceText)

        if (orderedProduct.productType == ProductType.Sawnwood.toString()) {
            invoiceText = """ ${orderedProduct.totalVolume} ${getString(R.string.m3Unit)} """
            createTextView(row, invoiceText)
        }
        if (orderedProduct.productType == ProductType.Plywood.toString()) {
            invoiceText = """ ${getString(R.string.notGivenText)} """
            createTextView(row, invoiceText)
        }
        invoiceText =
            """ ${decimalFormatChanger.format(orderedProduct.unitPrice)} ${getString(R.string.currencyUnitText)} """
        createTextView(row, invoiceText)
        invoiceText =
            """ ${decimalFormatChanger.format(orderedProduct.totalPrice)} ${getString(R.string.currencyUnitText)} """
        createTextView(row, invoiceText)
    }

    private fun deleteProduct(productNumber: Int) {
        val row = rowsList[productNumber]
        invoiceTable.removeView(row)
        rowsList.removeAt(productNumber)
        deleteButtonsList.removeAt(productNumber)
        editButtonsList.removeAt(productNumber)
        ordersInvoice.orderedProductList.removeAt(productNumber)
        if (ordersInvoice.orderedProductList.isEmpty()) {
            addNewProduct()
        } else {
            calculateTotalSum()
        }
    }

    override fun onBackPressed() {
        goToQuantityScreen()
    }

    private fun goToQuantityScreen() {
        ordersInvoice.orderedProductList.removeAt(ordersInvoice.orderedProductList.size - 1)
        val intent = Intent(this, QuantityAndPrice::class.java)
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

    private fun removeProductYesNoDialog(productNumber: Int) {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.deleteProductMessageTitle))
        builder.setMessage(getString(R.string.messageDoYouWantToDeleteTheProduct))
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> deleteProduct(productNumber)
                DialogInterface.BUTTON_NEGATIVE -> doNothing()
            }
        }
        builder.setPositiveButton(getString(R.string.yesAnswer), dialogClickListener)
        builder.setNegativeButton(getString(R.string.noAnswer), dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun editProduct(productNumber: Int) {
        val row = rowsList[productNumber]
        invoiceTable.removeView(row)
        rowsList.removeAt(productNumber)
        deleteButtonsList.removeAt(productNumber)
        editButtonsList.removeAt(productNumber)
        orderedProduct = ordersInvoice.orderedProductList[productNumber]
        ordersInvoice.orderedProductList.removeAt(productNumber)
        val intent = Intent(this, QuantityAndPrice::class.java)
        startActivity(intent)
    }
}