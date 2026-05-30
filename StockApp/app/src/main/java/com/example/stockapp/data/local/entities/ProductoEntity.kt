package com.example.stockapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val idProducto: Int = 0,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val cantidad: Int,
    val unidad: String,
    val fechaCaducidad: String,
    val stockMinimo: Int
)