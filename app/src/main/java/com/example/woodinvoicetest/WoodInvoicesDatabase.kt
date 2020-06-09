package com.example.woodinvoicetest

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InvoicesTable::class, ProductsTable::class], version = 6, exportSchema = false)
abstract class WoodInvoicesDatabase : RoomDatabase() {
    abstract val woodInvoiceDao: WoodInvoicesDao

    companion object {

        @Volatile
        private var INSTANCE: WoodInvoicesDatabase? = null

        fun getInstance(context: Context): WoodInvoicesDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WoodInvoicesDatabase::class.java,
                        "wood_invoices_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}