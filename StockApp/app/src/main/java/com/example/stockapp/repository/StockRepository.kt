package com.example.stockapp.repository

import com.example.stockapp.data.local.dao.AlertaStockDao
import com.example.stockapp.data.local.dao.CategoriaDao
import com.example.stockapp.data.local.dao.MovimientoInventarioDao
import com.example.stockapp.data.local.dao.ProductoDao
import com.example.stockapp.data.local.entities.AlertaStockEntity
import com.example.stockapp.data.local.entities.CategoriaEntity
import com.example.stockapp.data.local.entities.MovimientoInventarioEntity
import com.example.stockapp.data.local.entities.ProductoEntity

class StockRepository(
    private val productoDao: ProductoDao,
    private val categoriaDao: CategoriaDao,
    private val movimientoDao: MovimientoInventarioDao,
    private val alertaDao: AlertaStockDao
) {
    val productos = productoDao.obtenerProductos()
    val categorias = categoriaDao.obtenerCategorias()
    val movimientos = movimientoDao.obtenerMovimientos()
    val alertasActivas = alertaDao.obtenerAlertasActivas()

    suspend fun insertarProducto(producto: ProductoEntity) {
        productoDao.insertarProducto(producto)
    }

    suspend fun actualizarProducto(producto: ProductoEntity) {
        productoDao.actualizarProducto(producto)
    }

    suspend fun eliminarProducto(producto: ProductoEntity) {
        productoDao.eliminarProducto(producto)
    }

    fun buscarProductos(busqueda: String) =
        productoDao.buscarProductos(busqueda)

    suspend fun insertarCategoria(categoria: CategoriaEntity) {
        categoriaDao.insertarCategoria(categoria)
    }

    suspend fun registrarMovimiento(movimiento: MovimientoInventarioEntity) {
        movimientoDao.insertarMovimiento(movimiento)
    }

    suspend fun insertarAlerta(alerta: AlertaStockEntity) {
        alertaDao.insertarAlerta(alerta)
    }

    suspend fun marcarAlertaResuelta(idAlerta: Int) {
        alertaDao.marcarAlertaResuelta(idAlerta)
    }
}