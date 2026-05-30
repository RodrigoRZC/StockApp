package com.example.stockapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoInventarioEntity(

    @PrimaryKey(autoGenerate = true)
    val idMovimiento: Int = 0,

    val productoId: Int,

    val tipoMovimiento: String,

    val cantidad: Int,

    val fechaMovimiento: String,

    val descripcion: String
)