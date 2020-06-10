package com.example.woodinvoicetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import kotlinx.android.synthetic.main.activity_confirm_everything.*
import java.io.File
import java.io.FileOutputStream
import com.example.woodinvoicetest.databinding.ActivityConfirmEverythingBinding
import com.google.gson.Gson
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.lang.Exception

class ConfirmEverything : AppCompatActivity() {
    private val filename: String = "invoice.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityConfirmEverythingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_confirm_everything)

        Toast.makeText(this, getString(R.string.messageConfirmPayment), Toast.LENGTH_LONG).show()

        val application = requireNotNull(this).application
        val dataSource = WoodInvoicesDatabase.getInstance(application).woodInvoiceDao
        val woodInvoicesViewModelFactory = WoodInvoicesViewModelFactory(dataSource, application)
        val woodInvoicesViewModel = ViewModelProviders.of(this, woodInvoicesViewModelFactory)
            .get(WoodInvoicesViewModel::class.java)

        showInvoice.setOnClickListener { goToShowOneInvoice() }
        backToMainScreen.setOnClickListener { finishInvoice() }

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    printInvoiceButton.setOnClickListener { sendInvoiceToPrinter() }
                    if (ordersInvoice.sendToServer == "false") {
                        val jsonOrderedInvoice = dataClassToJson()
                        sendInvoiceToServer(jsonOrderedInvoice)
                        ordersInvoice.sendToServer = "true"
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    doNothing()
                }
            })
            .check()

        binding.lifecycleOwner = this
        binding.woodInvoicesViewModel = woodInvoicesViewModel
        if (ordersInvoice.savedToDatabase == "false") {
            woodInvoicesViewModel.onConfirmEverything()
            ordersInvoice.savedToDatabase = "true"
        }

    }


    fun sendInvoiceToPrinter() {
        val pdfPath = Common.getAppPath(this@ConfirmEverything) + filename
        createPDFFile(pdfPath)
//        Toast.makeText(this, "the pdf path is $pdfPath", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "This feature is in development", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        finishInvoice()
    }

    private fun finishInvoice() {
        ordersInvoice = InvoiceObjectClass()
        orderedProduct = OrderedProduct()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun createPDFFile(path: String) {
        if (File(path).exists()) {
            File(path).delete()
            print("pdf deleted")
        }
        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(path))
        document.open()
        document.pageSize = PageSize.A4
        document.addCreationDate()

        val colorAccent = BaseColor(0, 153, 204, 255)
        val headingFontSize = 20.0f
        val valueFontSize = 26.0f

        val titleStyle = Font(Font.FontFamily.COURIER, 36.0f, Font.NORMAL, BaseColor.BLACK)
        addNewItem(document, "Invoice", Element.ALIGN_CENTER, titleStyle)
        document.close()
        Toast.makeText(this, "the pdf path is $path", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, getString(R.string.messageConfirmPrinting), Toast.LENGTH_LONG).show()

    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, style: Font) {
        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)
    }

    fun dataClassToJson(): String {
        val gson = Gson()
        val jsonTut: String = gson.toJson(ordersInvoice)
        Log.i("json", "the json object: $jsonTut")
        return jsonTut
    }

    fun sendInvoiceToServer(jsonOrderedInvoice: String) {
        val externalURL = getString(R.string.externalServerUrl)
        val internalURL = getString(R.string.internalServerUrl)
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST, internalURL,
            Response.Listener<String> { response ->
                try {
                    Log.i(
                        "server",
                        "the invoice was sent to the server and the response is $response"
                    )
                    Toast.makeText(
                        this,
                        getString(R.string.messageConfirmSentToServer),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        getString(R.string.messageErrorSendToServer),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Log.i("server", "did not work $e")
                }
            }, Response.ErrorListener {
                Log.i("server", "Error $it")
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                //Change with your post params
                params["sender"] = getString(R.string.serverPassword)
                params["data"] = jsonOrderedInvoice
                return params
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1F)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun goToShowOneInvoice() {
        val intent = Intent(this, ShowSingleInvoice::class.java)
        intent.putExtra("invoiceId", ordersInvoice.invoiceId)
        intent.putExtra("invoiceNote", ordersInvoice.invoiceNotes)
        intent.putExtra("invoiceSold", ordersInvoice.invoiceSold.toString())
        intent.putExtra(
            "netInvoiceValue",
            (ordersInvoice.invoiceTotalSum - ordersInvoice.invoiceSold).toString()
        )
        startActivity(intent)
    }
}
