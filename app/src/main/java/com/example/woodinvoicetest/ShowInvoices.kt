package com.example.woodinvoicetest

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.woodinvoicetest.databinding.ActivityShowInvoicesBinding
import kotlinx.android.synthetic.main.activity_invoice_review_screen.invoiceTable
import kotlinx.android.synthetic.main.activity_show_invoices.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowInvoices : AppCompatActivity() {
    var rowsList: ArrayList<TableRow> = ArrayList()
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    var invoicesSum: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityShowInvoicesBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_show_invoices)

        showColumnsTitles()
        initiateDate()

        val application = requireNotNull(this).application
        val dataSource = WoodInvoicesDatabase.getInstance(application).woodInvoiceDao
        val woodInvoicesViewModelFactory = WoodInvoicesViewModelFactory(dataSource, application)
        val woodInvoicesViewModel = ViewModelProviders.of(this, woodInvoicesViewModelFactory)
            .get(WoodInvoicesViewModel::class.java)
        binding.lifecycleOwner = this
        binding.woodInvoicesViewModel = woodInvoicesViewModel
        val myLiveDAtaObserver = Observer<List<InvoicesTable>> { InvoiceList ->
            val invoicesList: ArrayList<String> = ArrayList()
            var counterI = 0
            invoicesSum = 0.0
            for (invoiceI in InvoiceList) {

                val row = TableRow(this)
                row.gravity = 1
                row.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val netInvoiceValue = invoiceI.totalSum - invoiceI.invoiceSold

                var invoiceText = counterI.toString()
                createTextView(row, invoiceText)
                invoiceText = invoiceI.customerName
                createTextView(row, invoiceText)
                invoiceText = invoiceI.nameOfProducts
                createTextView(row, invoiceText)
                invoiceText =
                    """${decimalFormatChanger.format(netInvoiceValue)} ${getString(R.string.currencyUnitText)} """
                createTextView(row, invoiceText)

                invoicesSum += netInvoiceValue
                counterI += 1
                invoiceTable.addView(row)
                rowsList.add(row)
                invoicesList.add(invoiceI.invoiceId)
            }
            invoicesSumTextView.text = decimalFormatChanger.format(invoicesSum).toString()
            for (i in rowsList.indices) {
                rowsList[i].setOnClickListener { showInvoice(InvoiceList[i], i) }
            }
            sendInvoicestoServer.setOnClickListener { goToSendToServer(invoicesList) }

        }
        startChosenDateTextView.setOnClickListener { chooseDatesDialog(startChosenDateTextView) }
        endChosenDateTextView.setOnClickListener { chooseDatesDialog(endChosenDateTextView) }

        showInvoiceButton.setOnClickListener {
            showInvoicesFunction(
                woodInvoicesViewModel,
                myLiveDAtaObserver
            )
        }
    }

    fun goToSendToServer(invoicesList: ArrayList<String>) {
        val intent = Intent(this, SendToServer::class.java)
        intent.putStringArrayListExtra("invoicesList", invoicesList)
        startActivity(intent)
    }

    private fun showInvoicesFunction(
        woodInvoicesViewModel: WoodInvoicesViewModel,
        myLiveDAtaObserver: Observer<List<InvoicesTable>>
    ) {
        val startChosenDate = formatter.parse(startChosenDateTextView.text.toString()).time
        val endChosenDate = formatter.parse(endChosenDateTextView.text.toString()).time

        if (rowsList.isNotEmpty()) {
            invoiceTable.removeAllViews()
            showColumnsTitles()
        }


        woodInvoicesViewModel.getInvoices(startChosenDate, endChosenDate)
            .observe(this, myLiveDAtaObserver)
    }

    private fun showInvoice(invoice: InvoicesTable, invoiceNumber: Int) {
        Toast.makeText(this, "Currency Nr. $invoiceNumber", Toast.LENGTH_SHORT).show()
        val netInvoiceValue = invoice.totalSum - invoice.invoiceSold
        goToShowOneInvoice(
            invoice.invoiceId,
            invoice.invoiceNotes,
            netInvoiceValue,
            invoice.invoiceSold
        )
    }

    private fun createTableColumnsTitles(row: TableRow) {

        var invoiceText = " ${getString(R.string.idText)} "
        createTextView(row, invoiceText)
        invoiceText = " ${getString(R.string.customerName)} "
        createTextView(row, invoiceText)
        invoiceText = getString(R.string.Products)
        createTextView(row, invoiceText)
        invoiceText = " ${getString(R.string.totalPriceText)} "
        createTextView(row, invoiceText)
    }

    fun showColumnsTitles() {
        val firstRow = TableRow(this)
        createTableColumnsTitles(firstRow)
        invoiceTable.addView(firstRow)
    }

    fun createTextView(row: TableRow, invoiceText: String) {
        val textView: TextView = TextView(this)
        textView.apply {
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            text = invoiceText
            gravity = 1
            textSize = 16F

        }
        textView.margin(0F, 5F, 0F, 5F)

        row.addView(textView)
    }

    fun goToShowOneInvoice(
        invoiceId: String, invoiceNotes: String,
        netInvoiceValue: Double, invoiceSold: Double
    ) {
        val intent = Intent(this, ShowSingleInvoice::class.java)
        intent.putExtra("invoiceId", invoiceId)
        intent.putExtra("invoiceNote", invoiceNotes)
        intent.putExtra("invoiceSold", invoiceSold.toString())
        intent.putExtra("netInvoiceValue", netInvoiceValue.toString())
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    fun chooseDatesDialog(vText: TextView) {
        val calender = Calendar.getInstance()
        val currentYear = calender.get(Calendar.YEAR)
        val currentMonth = calender.get(Calendar.MONTH)
        val currentDay = calender.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val correctedMonth = monthOfYear + 1
                vText.text =
                    formatter.format(formatter.parse("$dayOfMonth.$correctedMonth.$year"))
                        .toString()
            },
            currentYear,
            currentMonth,
            currentDay
        )
        dpd.show()
    }

    fun initiateDate() {
        val calender = Calendar.getInstance()
        val currentYear = calender.get(Calendar.YEAR)
        var currentMonth = calender.get(Calendar.MONTH)
        var currentDay = calender.get(Calendar.DAY_OF_MONTH)
        currentMonth += 1
        startChosenDateTextView.text =
            formatter.format(formatter.parse("$currentDay.$currentMonth.$currentYear"))
        currentDay += 1
        endChosenDateTextView.text =
            formatter.format(formatter.parse("$currentDay.$currentMonth.$currentYear"))

    }

}
