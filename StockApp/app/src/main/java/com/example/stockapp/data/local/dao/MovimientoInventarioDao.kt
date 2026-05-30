package com.example.stockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockapp.data.local.entities.MovimientoInventarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoInventarioDao {

    @Insert
    suspend fun insertarMovimiento(movimiento: MovimientoInventarioEntity)

    @Query("SELECT * FROM movimientos ORDER BY idMovimiento DESC")
    fun obtenerMovimientos(): Flow<List<MovimientoInventarioEntity>>

    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY idMovimiento DESC")
    fun obtenerMovimientosPorProducto(productoId: Int): Flow<List<MovimientoInventarioEntity>>
}