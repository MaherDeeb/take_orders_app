package com.example.woodinvoicetest

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.woodinvoicetest.databinding.ActivityShowSingleInvoiceBinding
import kotlinx.android.synthetic.main.activity_show_single_invoice.*

class ShowSingleInvoice : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityShowSingleInvoiceBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_show_single_invoice)
        val invoiceNumber = intent.getStringExtra("invoiceId")
        val invoiceNotes = intent.getStringExtra("invoiceNote")
        val invoiceSold = intent.getStringExtra("invoiceSold")
        val netInvoiceValue = intent.getStringExtra("netInvoiceValue")

        val application = requireNotNull(this).application
        val dataSource = WoodInvoicesDatabase.getInstance(application).woodInvoiceDao
        val woodInvoicesViewModelFactory = WoodInvoicesViewModelFactory(dataSource, application)
        val woodInvoicesViewModel = ViewModelProviders.of(this, woodInvoicesViewModelFactory)
            .get(WoodInvoicesViewModel::class.java)
        binding.lifecycleOwner = this
        binding.woodInvoicesViewModel = woodInvoicesViewModel

        var invoiceTotalSum = 0.0
        val firstRow = TableRow(this)
        createTableColumnsTitles(firstRow)
        invoiceTable.addView(firstRow)

        val myLiveDAtaObserver = Observer<List<ProductsTable>> { productsList ->

            for (orderedProductI in productsList.indices) {
                val productI = productsList[orderedProductI]
                val row = TableRow(this)
                val productNumber = orderedProductI + 1

                showOneProduct(productI, row, productNumber)
                invoiceTable.addView(row)
                invoiceTotalSum += productI.totalPrice
            }
            totalPrice.text = decimalFormatChanger.format(invoiceTotalSum).toString()
        }
        if (invoiceNumber != null) {
            woodInvoicesViewModel.getProducts(invoiceNumber).observe(this, myLiveDAtaObserver)
            invoiceNumberTextview.text =
                """${getString(R.string.invoiceNumberPlaceHolder)} $invoiceNumber"""

            invoiceNoteText.text = invoiceNotes
            soldValueText.text = invoiceSold
            netValueText.text = decimalFormatChanger.format(netInvoiceValue.toDouble()).toString()
        }

    }

    private fun createTableColumnsTitles(row: TableRow) {

        var invoiceText = getString(R.string.idText)
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

    private fun createTextView(row: TableRow, invoiceText: String) {
        val textView: TextView = TextView(this)
        textView.apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            text = invoiceText
            textSize = 16F
            gravity = 1
        }
        textView.margin(2F, 2F, 2F, 2F)
        row.addView(textView)
    }

    private fun showOneProduct(productI: ProductsTable, row: TableRow, productNumber: Int) {
        var invoiceText = if (productNumber < 10) {
            "  00$productNumber  "
        } else {
            "  0$productNumber  "
        }
        createTextView(row, invoiceText)
        invoiceText = productI.marketProductName

        if (productI.productWidth > 0.0) {
            invoiceText += """ ${getString(R.string.widthText)} ${(productI.productWidth * 100)}  ${getString(
                R.string.cm
            )} """
        }

        if (productI.productThickness > 0.0) {
            invoiceText += """ ${getString(R.string.thicknessText)} ${(productI.productThickness * 1000)} ${getString(
                R.string.mm
            )} """
        }

        if (productI.productLength > 0.0) {
            invoiceText += """ ${getString(R.string.lengthText)} ${productI.productLength} ${getString(
                R.string.m
            )} """
        }

        if (productI.productProperty1.isNotEmpty()) {
            invoiceText += """ ${productI.productProperty1} """
        }
        if (productI.productProperty2.isNotEmpty()) {
            invoiceText += """ ${productI.productProperty2} """
        }
        if (productI.productProperty3.isNotEmpty()) {
            invoiceText += """ ${productI.productProperty3} """
        }
        createTextView(row, invoiceText)
        invoiceText = if (productI.numberOfUnits > 0) {
            """ ${productI.numberOfUnits} """
        } else {
            """ ${getString(R.string.notGivenText)} """
        }
        createTextView(row, invoiceText)

        if (productI.productType == ProductType.Sawnwood.toString()) {
            invoiceText = """${productI.totalVolume}  ${getString(R.string.m3Unit)} """
            createTextView(row, invoiceText)
        }
        if (productI.productType == ProductType.Plywood.toString()) {
            invoiceText = """ ${getString(R.string.notGivenText)} """
            createTextView(row, invoiceText)
        }
        invoiceText =
            """${decimalFormatChanger.format(productI.unitPrice)} ${getString(R.string.currencyUnitText)} """
        createTextView(row, invoiceText)
        invoiceText =
            """${decimalFormatChanger.format(productI.totalPrice)} ${getString(R.string.currencyUnitText)} """
        createTextView(row, invoiceText)
    }

}
