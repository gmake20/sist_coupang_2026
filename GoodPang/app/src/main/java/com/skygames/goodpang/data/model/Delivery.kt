package com.skygames.goodpang.data.model

data class Delivery(
    val deliveryNo: Int,
    val orderNo: Int,
    val deliveryServiceCode: String,
    val invoiceNo: String,
    val deliveryStatus: String,
    val deliveryStartDate: String,
    val buyerName: String,
    val buyerPhone: String,
    val productName: String,
    val productImageUrl: String,
    val storeName: String,
    val itemCount: Int
)

data class DeliveryCompleteResponse(
    val success: Boolean,
    val message: String? = null
)
