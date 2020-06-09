package com.example.woodinvoicetest

import androidx.lifecycle.LiveData

class WoodInvoicesRepository (private val woodInvoicesDao: WoodInvoicesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allInvoices: LiveData<List<InvoicesTable>> = woodInvoicesDao.getAllInvoices(0,0)

    suspend fun insert(invoice: InvoicesTable) {
        woodInvoicesDao.insert(invoice)
    }
}