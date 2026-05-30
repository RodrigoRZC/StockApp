package com.example.stockapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockapp.data.local.entities.AlertaStockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaStockDao {

    @Insert
    suspend fun insertarAlerta(alerta: AlertaStockEntity)

    @Query("SELECT * FROM alertas WHERE estado = 'Activa' ORDER BY idAlerta DESC")
    fun obtenerAlertasActivas(): Flow<List<AlertaStockEntity>>

    @Query("UPDATE alertas SET estado = 'Resuelta' WHERE idAlerta = :idAlerta")
    suspend fun marcarAlertaResuelta(idAlerta: Int)
}