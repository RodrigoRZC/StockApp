package com.example.stockapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockapp.data.local.StockDatabase
import com.example.stockapp.data.local.entities.AlertaStockEntity
import com.example.stockapp.data.local.entities.CategoriaEntity
import com.example.stockapp.data.local.entities.MovimientoInventarioEntity
import com.example.stockapp.data.local.entities.ProductoEntity
import com.example.stockapp.repository.StockRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val database = StockDatabase.getDatabase(application)

    private val repository = StockRepository(
        productoDao = database.productoDao(),
        categoriaDao = database.categoriaDao(),
        movimientoDao = database.movimientoInventarioDao(),
        alertaDao = database.alertaStockDao()
    )

    val productos = repository.productos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categorias = repository.categorias.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val movimientos = repository.movimientos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val alertasActivas = repository.alertasActivas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertarProducto(
        nombre: String,
        categoria: String,
        precio: Double,
        cantidad: Int,
        unidad: String,
        fechaCaducidad: String,
        stockMinimo: Int
    ) {
        viewModelScope.launch {
            repository.insertarProducto(
                ProductoEntity(
                    nombre = nombre,
                    categoria = categoria,
                    precio = precio,
                    cantidad = cantidad,
                    unidad = unidad,
                    fechaCaducidad = fechaCaducidad,
                    stockMinimo = stockMinimo
                )
            )
        }
    }

    fun actualizarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            repository.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }

    fun registrarEntrada(producto: ProductoEntity, cantidadEntrada: Int) {
        val nuevaCantidad = producto.cantidad + cantidadEntrada

        viewModelScope.launch {
            repository.actualizarProducto(producto.copy(cantidad = nuevaCantidad))

            repository.registrarMovimiento(
                MovimientoInventarioEntity(
                    productoId = producto.idProducto,
                    tipoMovimiento = "Entrada",
                    cantidad = cantidadEntrada,
                    fechaMovimiento = obtenerFechaActual(),
                    descripcion = "Entrada de mercancía"
                )
            )
        }
    }

    fun registrarSalida(producto: ProductoEntity, cantidadSalida: Int) {
        if (cantidadSalida > producto.cantidad) return

        val nuevaCantidad = producto.cantidad - cantidadSalida

        viewModelScope.launch {
            repository.actualizarProducto(producto.copy(cantidad = nuevaCantidad))

            repository.registrarMovimiento(
                MovimientoInventarioEntity(
                    productoId = producto.idProducto,
                    tipoMovimiento = "Salida",
                    cantidad = cantidadSalida,
                    fechaMovimiento = obtenerFechaActual(),
                    descripcion = "Salida de producto"
                )
            )

            if (nuevaCantidad <= producto.stockMinimo) {
                repository.insertarAlerta(
                    AlertaStockEntity(
                        productoId = producto.idProducto,
                        tipoAlerta = "Bajo stock",
                        mensaje = "El producto ${producto.nombre} tiene bajo stock",
                        fechaAlerta = obtenerFechaActual(),
                        estado = "Activa"
                    )
                )
            }
        }
    }

    fun insertarCategoria(nombreCategoria: String) {
        viewModelScope.launch {
            repository.insertarCategoria(
                CategoriaEntity(nombreCategoria = nombreCategoria)
            )
        }
    }

    private fun obtenerFechaActual(): String {
        val fecha = java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.getDefault()
        )
        return fecha.format(java.util.Date())
    }
}