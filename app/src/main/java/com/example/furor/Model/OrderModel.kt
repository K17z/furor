package com.example.furor.model

data class OrderModel(
    val orderId: String = "",
    val timestamp: Long = 0L,
    val items: List<ItemsModel> = emptyList()
)
