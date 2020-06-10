package com.example.woodinvoicetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.woodinvoicetest.databinding.ActivitySendToServerBinding
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_send_to_server.*
import java.lang.Exception

class SendToServer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySendToServerBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_send_to_server)

        val application = requireNotNull(this).application
        val dataSource = WoodInvoicesDatabase.getInstance(application).woodInvoiceDao
        val woodInvoicesViewModelFactory = WoodInvoicesViewModelFactory(dataSource, application)
        val woodInvoicesViewModel = ViewModelProviders.of(this, woodInvoicesViewModelFactory)
            .get(WoodInvoicesViewModel::class.java)
        binding.lifecycleOwner = this
        binding.woodInvoicesViewModel = woodInvoicesViewModel

        val invoicesList = intent.getStringArrayListExtra("invoicesList")

        val invoicesMap: MutableMap<String, InvoiceObjectClass> = HashMap()
        val productsMap: MutableMap<String, ArrayList<OrderedProduct>> = HashMap()

        if (invoicesList != null) {
            for (invoiceId in invoicesList) {
                val myLiveDAtaObserver = Observer<InvoicesTable> { invoiceById ->

                    val toSendInvoice = InvoiceObjectClass(
                        invoiceId = invoiceById.invoiceId,
                        customerName = invoiceById.customerName,
                        invoiceTotalSum = invoiceById.totalSum,
                        invoiceSold = invoiceById.invoiceSold,
                        invoiceNotes = invoiceById.invoiceNotes
                    )
                    invoicesMap[invoiceById.invoiceId] = toSendInvoice
                }
                woodInvoicesViewModel.getInvoicesById(invoiceId).observe(this, myLiveDAtaObserver)
            }

            for (invoiceId in invoicesList) {
                val productsObserver = Observer<List<ProductsTable>> { productsList ->
                    val productsListGathers: ArrayList<OrderedProduct> = ArrayList()
                    for (orderedProductI in productsList.indices) {
                        val productI = productsList[orderedProductI]
                        val toSendProduct = OrderedProduct(
                            productId = productI.productId,
                            productName = productI.productName,
                            productType = productI.productType,
                            marketProductName = productI.marketProductName,
                            productThickness = productI.productThickness,
                            productWidth = productI.productWidth,
                            productLength = productI.productLength,
                            productProperty1 = productI.productProperty1,
                            productProperty2 = productI.productProperty2,
                            productProperty3 = productI.productProperty3,
                            numberOfUnits = productI.numberOfUnits,
                            totalVolume = productI.totalVolume,
                            unitPrice = productI.unitPrice,
                            totalPrice = productI.totalPrice
                        )
                        productsListGathers.add(toSendProduct)
                        productsMap[productI.invoiceId] = productsListGathers
                    }
                }
                woodInvoicesViewModel.getProducts(invoiceId).observe(this, productsObserver)
            }
            sendButton.setOnClickListener {
                var counterI = 0
                for (invoiceId in invoicesList) {
                    if (productsMap[invoiceId] != null) {
                        for (orderedProductI in productsMap[invoiceId]!!) {
                            invoicesMap[invoiceId]?.orderedProductList?.add(orderedProductI)
                        }
                    }
                    val jsonOrderedInvoice = invoicesMap[invoiceId]?.let { it1 ->
                        dataClassToJson(
                            it1
                        )
                    }
                    if (jsonOrderedInvoice != null) {
                        sendInvoiceToServer(jsonOrderedInvoice)
                    }
                    counterI += 1
                    determinateBar.progress = 100 * counterI / invoicesList.size
                }
            }
        }
    }

    private fun dataClassToJson(toSendInvoice: InvoiceObjectClass): String {
        val gson = Gson()
        return gson.toJson(toSendInvoice)
    }

    private fun sendInvoiceToServer(jsonOrderedInvoice: String) {
        val externalURL = getString(R.string.externalServerUrl)
        val internalURL = getString(R.string.internalServerUrl)
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST, internalURL,
            Response.Listener { response ->
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
}
