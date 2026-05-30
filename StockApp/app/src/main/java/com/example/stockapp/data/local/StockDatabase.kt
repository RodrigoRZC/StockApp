package com.example.stockapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stockapp.data.local.dao.AlertaStockDao
import com.example.stockapp.data.local.dao.CategoriaDao
import com.example.stockapp.data.local.dao.MovimientoInventarioDao
import com.example.stockapp.data.local.dao.ProductoDao
import com.example.stockapp.data.local.entities.AlertaStockEntity
import com.example.stockapp.data.local.entities.CategoriaEntity
import com.example.stockapp.data.local.entities.MovimientoInventarioEntity
import com.example.stockapp.data.local.entities.ProductoEntity

@Database(
    entities = [
        ProductoEntity::class,
        CategoriaEntity::class,
        MovimientoInventarioEntity::class,
        AlertaStockEntity::class
    ],
    version = 2
)
abstract class StockDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun movimientoInventarioDao(): MovimientoInventarioDao
    abstract fun alertaStockDao(): AlertaStockDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}