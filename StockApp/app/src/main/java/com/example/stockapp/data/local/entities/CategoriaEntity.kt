package com.example.stockapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(

    @PrimaryKey(autoGenerate = true)
    val idCategoria: Int = 0,

    val nombreCategoria: String
)