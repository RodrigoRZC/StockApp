package com.example.stockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockapp.data.local.entities.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert
    suspend fun insertarCategoria(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias ORDER BY nombreCategoria ASC")
    fun obtenerCategorias(): Flow<List<CategoriaEntity>>
}