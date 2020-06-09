package com.example.woodinvoicetest

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*


class WoodInvoicesViewModel(
    val database: WoodInvoicesDao,
    application: Application
) : AndroidViewModel(application) {
    private val viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    fun getInvoices(startChosenDate: Long, endChosenDate: Long): LiveData<List<InvoicesTable>> {
        return database.getAllInvoices(startChosenDate, endChosenDate)
    }

    fun getInvoicesById(invoiceID: String): LiveData<InvoicesTable> {
        return database.getAllInvoicesById(invoiceID)
    }

    fun getProducts(invoiceNumber: String): LiveData<List<ProductsTable>> {
        return database.getProducts(invoiceNumber)
    }


    fun getNamesOfOrderedProducts(): String {
        var nameOfOrderedProducts = ""
        for (productI in ordersInvoice.orderedProductList) {
            nameOfOrderedProducts += " " + productI.marketProductName + " "
        }
        return nameOfOrderedProducts
    }

    fun onConfirmEverything() {
        uiScope.launch {
            val nameOfOrderedProducts = getNamesOfOrderedProducts()
            val newInvoice = InvoicesTable(
                invoiceId = ordersInvoice.invoiceId,
                customerName = ordersInvoice.customerName,
                nameOfProducts = nameOfOrderedProducts,
                totalSum = ordersInvoice.invoiceTotalSum,
                invoiceSold = ordersInvoice.invoiceSold,
                invoiceNotes = ordersInvoice.invoiceNotes
            )
            Log.i("livedata", "insert invoice data: $newInvoice")
            insert(newInvoice)

            for (productI in ordersInvoice.orderedProductList) {
                val newProduct = ProductsTable(
                    invoiceId = ordersInvoice.invoiceId,
                    productId = productI.productId,
                    productName = productI.productName.toString(),
                    marketProductName = productI.marketProductName,
                    productType = productI.productType.toString(),
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
                Log.i("livedata", "insert product data: $productI")
                insertProduct(newProduct)
            }

        }
    }

    private suspend fun insert(newInvoice: InvoicesTable) {
        withContext(Dispatchers.IO) {
            database.insert(newInvoice)
        }
    }

    private suspend fun insertProduct(newProduct: ProductsTable) {
        withContext(Dispatchers.IO) {
            database.insertProduct(newProduct)
        }
    }
}