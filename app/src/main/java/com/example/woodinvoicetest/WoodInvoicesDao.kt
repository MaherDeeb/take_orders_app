package com.example.woodinvoicetest

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface WoodInvoicesDao {
    @Insert
    fun insert(invoice: InvoicesTable)

    @Insert
    fun insertProduct(product: ProductsTable)

    @Query("SELECT * FROM invoices_table WHERE Not deleted AND invoiceDate>=:startChosenDate AND invoiceDate<=:endChosenDate")
    fun getAllInvoices(startChosenDate: Long, endChosenDate: Long): LiveData<List<InvoicesTable>>

    @Query("SELECT * FROM invoices_table WHERE Not deleted AND InvoiceId = :invoiceId")
    fun getAllInvoicesById(invoiceId: String): LiveData<InvoicesTable>

    @Query("SELECT * FROM products_table WHERE invoiceId=:invoiceNumber")
    fun getProducts(invoiceNumber: String): LiveData<List<ProductsTable>>
}