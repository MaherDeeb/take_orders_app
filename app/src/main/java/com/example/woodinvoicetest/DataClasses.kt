package com.example.woodinvoicetest

import androidx.room.ColumnInfo
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

enum class MaterialName {
    Product1, Product2, Product3, Product4, Product5, Product6,
    Product7, Product8, Product9, Product10
}

enum class ProductType {
    Sawnwood, Plywood
}

val propertiesNames = listOf(
    "product", "Width cm", "Thickness mm", "Length m",
    "Extra property 1", "Extra property 2", "Extra property 3"
)

var availableProducts = listOf(
    "Product1", "Product2", "Product3", "Product4", "Product5", "Product6",
    "Product7", "Product8", "Product9", "Product10"
)

data class AvailableProduct(
    var productId: Int = 0,
    var productName: String = MaterialName.Product1.toString(),
    var arabicProductName: String = "",
    var productType: String = ProductType.Sawnwood.toString(),
    var availableThickness: ArrayList<Double> = ArrayList(),
    var availableWidth: ArrayList<Double> = ArrayList(),
    var availableLength: ArrayList<Double> = ArrayList(),
    var availableProperty1: ArrayList<String> = ArrayList(),
    var availableProperty2: ArrayList<String> = ArrayList(),
    var availableProperty3: ArrayList<String> = ArrayList(),
    var originCountry: String = "Romania"
)

data class OrderedProduct(
    var productId: Int = 0,
    var productName: String = MaterialName.Product1.toString(),
    var marketProductName: String = "",
    var productType: String = ProductType.Sawnwood.toString(),
    var productThickness: Double = 0.0,
    var productWidth: Double = 0.0,
    var productLength: Double = 0.0,
    var productProperty1: String = "",
    var productProperty2: String = "",
    var productProperty3: String = "",
    var numberOfUnits: Int = 0,
    var totalVolume: Double = 0.0,
    var unitPrice: Double = 0.0,
    var totalPrice: Double = 0.0,
    var lastNonEmptyProperty: Int = 0
)

data class InvoiceObjectClass(
    var invoiceId: String = "",
    var customerName: String = "",
    var orderedProductList: ArrayList<OrderedProduct> = ArrayList(),
    var invoiceTotalSum: Double = 0.0,
    var salesman: String = "",
    var invoiceSold: Double = 0.0,
    var invoiceNotes: String = "",
    var savedToDatabase: String = "false",
    var sendToServer: String = "false"
)

var product1 = AvailableProduct(
    productId = 0,
    productName = MaterialName.Product1.toString(),
    arabicProductName = availableProducts[0],
    productType = ProductType.Sawnwood.toString(),
    availableWidth = arrayListOf(7.5, 10.0, 12.0, 15.0, 17.0, 19.0, 22.0, 25.0, 30.0, 0.0),
    availableThickness = arrayListOf(24.0, 38.0, 48.0, 75.0, 0.0),
    availableLength = arrayListOf(1.0, 2.0, 3.0, 4.0, 0.0),
    originCountry = ""
)

var product2 = AvailableProduct(
    productId = 1,
    productName = MaterialName.Product2.toString(),
    arabicProductName = availableProducts[1],
    productType = ProductType.Sawnwood.toString(),
    availableWidth = arrayListOf(15.0, 17.5, 20.0, 22.5, 0.0),
    availableThickness = arrayListOf(25.0, 50.0, 0.0),
    availableLength = arrayListOf(1.6, 1.8, 2.5, 3.6, 4.0, 5.1, 5.4, 5.7, 6.0, 0.0),
    availableProperty2 = arrayListOf("BEL", "MK", "NE"),
    originCountry = ""
)

var product3 = AvailableProduct(
    productId = 2,
    productName = MaterialName.Product3.toString(),
    arabicProductName = availableProducts[2],
    productType = ProductType.Sawnwood.toString(),
    availableWidth = arrayListOf(0.0),
    availableThickness = arrayListOf(25.0, 38.0, 50.0, 60.0, 70.0, 80.0, 0.0),
    availableLength = arrayListOf(0.0),
    availableProperty1 = arrayListOf("Short", "Long"),
    availableProperty2 = arrayListOf("Wet", "Dry"),
    availableProperty3 = arrayListOf("Normal", "Wide"),
    originCountry = ""
)

var product4 = AvailableProduct(
    productId = 3,
    productName = MaterialName.Product4.toString(),
    arabicProductName = availableProducts[3],
    productType = ProductType.Sawnwood.toString(),
    availableWidth = arrayListOf(12.7, 15.0, 20.0, 25.0, 30.0, 0.0),
    availableThickness = arrayListOf(25.0, 42.0, 50.0, 0.0),
    availableLength = arrayListOf(3.1, 3.4, 3.7, 4.2, 4.6, 4.9, 5.1, 5.8, 6.1, 0.0),
    availableProperty2 = arrayListOf("Super", "Mixed", "First Class"),
    originCountry = ""
)

var product5 = AvailableProduct(
    productId = 4,
    productName = MaterialName.Product5.toString(),
    arabicProductName = availableProducts[4],
    productType = ProductType.Sawnwood.toString(),
    availableWidth = arrayListOf(0.0),
    availableThickness = arrayListOf(25.4, 50.8, 0.0),
    availableLength = arrayListOf(0.0),
    originCountry = ""
)

var product6 = AvailableProduct(
    productId = 5,
    productName = MaterialName.Product6.toString(),
    arabicProductName = availableProducts[5],
    productType = ProductType.Plywood.toString(),
    availableThickness = arrayListOf(2.5, 3.0, 3.6, 5.5, 7.5, 11.0, 14.0, 16.0, 18.0, 0.0),
    originCountry = "Different"
)

var product7 = AvailableProduct(
    productId = 6,
    productName = MaterialName.Product7.toString(),
    arabicProductName = availableProducts[6],
    productType = ProductType.Plywood.toString(),
    availableThickness = arrayListOf(2.4, 2.5, 2.7, 3.2, 3.6, 0.0),
    availableProperty2 = arrayListOf("Doors", "Wide"),
    originCountry = "Different"
)

var product8 = AvailableProduct(
    productId = 7,
    productName = MaterialName.Product8.toString(),
    arabicProductName = availableProducts[7],
    productType = ProductType.Plywood.toString(),
    availableThickness = arrayListOf(18.0, 22.0, 0.0),
    originCountry = "Different"
)

var product9 = AvailableProduct(
    productId = 8,
    productName = MaterialName.Product9.toString(),
    arabicProductName = availableProducts[8],
    productType = ProductType.Plywood.toString(),
    availableThickness = arrayListOf(12.0, 12.0, 0.0),
    availableProperty2 = arrayListOf("Colored", "White"),
    originCountry = "Different"
)

var product10 = AvailableProduct(
    productId = 8,
    productName = MaterialName.Product10.toString(),
    arabicProductName = availableProducts[9],
    productType = ProductType.Plywood.toString(),
    availableProperty1 = arrayListOf("Large", "Small"),
    originCountry = "Different"
)

var ordersInvoice = InvoiceObjectClass()
var orderedProduct = OrderedProduct()
val decimalFormatChanger = DecimalFormat("#,###.##")
var ProductsObjests = listOf(
    product1, product2, product3, product4, product5,
    product6, product7, product8, product9, product10
)

@Entity(tableName = "invoices_table")
data class InvoicesTable(
    @PrimaryKey(autoGenerate = true)
    var entryId: Long = 0L,

    @ColumnInfo(name = "invoiceDate")
    var invoiceDate: Long = Calendar.getInstance().timeInMillis,

    @ColumnInfo(name = "invoiceId")
    var invoiceId: String = "",

    @ColumnInfo(name = "customerName")
    var customerName: String = "",

    @ColumnInfo(name = "nameOfProducts")
    var nameOfProducts: String = "",

    @ColumnInfo(name = "totalSum")
    var totalSum: Double = 0.0,

    @ColumnInfo(name = "invoiceSold")
    var invoiceSold: Double = 0.0,

    @ColumnInfo(name = "invoiceNotes")
    var invoiceNotes: String = "",

    @ColumnInfo(name = "deleted")
    var deleted: Boolean = false
)

@Entity(tableName = "products_table")
data class ProductsTable(
    @PrimaryKey(autoGenerate = true)
    var entryId: Long = 0L,
    @ColumnInfo(name = "invoiceId")
    var invoiceId: String = "",
    @ColumnInfo(name = "productId")
    var productId: Int = -1,
    @ColumnInfo(name = "productName")
    var productName: String = "",
    @ColumnInfo(name = "arabicProductName")
    var marketProductName: String = "",
    @ColumnInfo(name = "productType")
    var productType: String = "",
    @ColumnInfo(name = "productThickness")
    var productThickness: Double = 0.0,
    @ColumnInfo(name = "productWidth")
    var productWidth: Double = 0.0,
    @ColumnInfo(name = "productLength")
    var productLength: Double = 0.0,
    @ColumnInfo(name = "productProperty1")
    var productProperty1: String = "",
    @ColumnInfo(name = "productProperty2")
    var productProperty2: String = "",
    @ColumnInfo(name = "productProperty3")
    var productProperty3: String = "",
    @ColumnInfo(name = "numberOfUnits")
    var numberOfUnits: Int = 0,
    @ColumnInfo(name = "totalVolume")
    var totalVolume: Double = 0.0,
    @ColumnInfo(name = "unitPrice")
    var unitPrice: Double = 0.0,
    @ColumnInfo(name = "totalPrice")
    var totalPrice: Double = 0.0

)

