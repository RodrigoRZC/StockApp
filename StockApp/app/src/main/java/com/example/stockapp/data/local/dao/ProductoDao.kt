package com.example.stockapp.data.local.dao

import androidx.room.*
import com.example.stockapp.data.local.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE idProducto = :id")
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity?

    @Query("""
        SELECT * FROM productos
        WHERE nombre LIKE '%' || :busqueda || '%'
        OR categoria LIKE '%' || :busqueda || '%'
    """)
    fun buscarProductos(busqueda: String): Flow<List<ProductoEntity>>
}