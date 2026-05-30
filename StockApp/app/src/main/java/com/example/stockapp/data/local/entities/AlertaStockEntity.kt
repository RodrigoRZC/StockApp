package com.example.stockapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertas")
data class AlertaStockEntity(

    @PrimaryKey(autoGenerate = true)
    val idAlerta: Int = 0,

    val productoId: Int,

    val tipoAlerta: String,

    val mensaje: String,

    val fechaAlerta: String,

    val estado: String
)