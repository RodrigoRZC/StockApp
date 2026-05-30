package com.example.stockapp.ui.screens

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.stockapp.data.local.entities.ProductoEntity
import com.example.stockapp.ui.viewmodel.StockViewModel
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

private val AzulPrincipal = Color(0xFF071326)
private val AzulSecundario = Color(0xFF0B1F3A)
private val AzulAccion = Color(0xFF123A6F)
private val FondoApp = Color(0xFFF4F6F8)
private val BlancoCard = Color.White
private val GrisTexto = Color(0xFF475569)
private val GrisBorde = Color(0xFF94A3B8)
private val NaranjaAlerta = Color(0xFFFFEDD5)
private val AmarilloAlerta = Color(0xFFFFF7D6)
private val RojoAccion = Color(0xFFB91C1C)

@Composable
fun ProductoScreen(viewModel: StockViewModel) {
    val productos by viewModel.productos.collectAsState()
    val categoriasGuardadas by viewModel.categorias.collectAsState()
    val context = LocalContext.current

    var mostrarFormulario by remember { mutableStateOf(false) }
    var productoEditando by remember { mutableStateOf<ProductoEntity?>(null) }
    var busqueda by remember { mutableStateOf("") }

    val categorias = (
            listOf("Abarrotes", "Bebidas", "Lácteos", "Limpieza", "Botanas", "Farmacia", "Papelería") +
                    categoriasGuardadas.map { it.nombreCategoria }
            ).distinct().sorted()

    val productosFiltrados = productos.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.categoria.contains(busqueda, ignoreCase = true)
    }

    if (mostrarFormulario) {
        FormularioProducto(
            productoEditar = productoEditando,
            categorias = categorias,
            onGuardar = { nombre, categoria, precio, cantidad, unidad, fecha, stockMinimo ->
                if (productoEditando == null) {
                    viewModel.insertarProducto(nombre, categoria, precio, cantidad, unidad, fecha, stockMinimo)
                } else {
                    viewModel.actualizarProducto(
                        productoEditando!!.copy(
                            nombre = nombre,
                            categoria = categoria,
                            precio = precio,
                            cantidad = cantidad,
                            unidad = unidad,
                            fechaCaducidad = fecha,
                            stockMinimo = stockMinimo
                        )
                    )
                }

                productoEditando = null
                mostrarFormulario = false
            },
            onCancelar = {
                productoEditando = null
                mostrarFormulario = false
            }
        )
    } else {
        ListaProductos(
            productos = productosFiltrados,
            todosLosProductos = productos,
            busqueda = busqueda,
            categorias = categorias,
            onBusquedaChange = { busqueda = it },
            onAgregarCategoria = { nuevaCategoria -> viewModel.insertarCategoria(nuevaCategoria) },
            onExportarCSV = { exportarCSV(context, productos) },
            onAgregar = {
                productoEditando = null
                mostrarFormulario = true
            },
            onEditar = { producto ->
                productoEditando = producto
                mostrarFormulario = true
            },
            onEliminar = { producto -> viewModel.eliminarProducto(producto) },
            onEntrada = { producto, cantidad -> viewModel.registrarEntrada(producto, cantidad) },
            onSalida = { producto, cantidad -> viewModel.registrarSalida(producto, cantidad) }
        )
    }
}

@Composable
fun ListaProductos(
    productos: List<ProductoEntity>,
    todosLosProductos: List<ProductoEntity>,
    busqueda: String,
    categorias: List<String>,
    onBusquedaChange: (String) -> Unit,
    onAgregarCategoria: (String) -> Unit,
    onAgregar: () -> Unit,
    onEditar: (ProductoEntity) -> Unit,
    onEliminar: (ProductoEntity) -> Unit,
    onEntrada: (ProductoEntity, Int) -> Unit,
    onSalida: (ProductoEntity, Int) -> Unit,
    onExportarCSV: () -> Unit
) {
    val bajoStock = todosLosProductos.count { it.cantidad <= it.stockMinimo }
    val porVencer = todosLosProductos.count { productoProximoAVencer(it.fechaCaducidad) }

    Scaffold(
        containerColor = FondoApp,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = AzulAccion,
                contentColor = Color.White
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(FondoApp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(AzulPrincipal, AzulSecundario)))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "StockApp",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Control profesional de inventario",
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ResumenCard("Productos", todosLosProductos.size.toString(), Modifier.weight(1f))
                        ResumenCard("Bajo stock", bajoStock.toString(), Modifier.weight(1f))
                        ResumenCard("Por vencer", porVencer.toString(), Modifier.weight(1f))
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                CampoTexto(
                    valor = busqueda,
                    onCambio = onBusquedaChange,
                    etiqueta = "Buscar producto o categoría"
                )

                BotonCategorias(
                    categorias = categorias,
                    onAgregarCategoria = onAgregarCategoria
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onExportarCSV,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrincipal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Exportar inventario CSV")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Inventario",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (productos.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoCard)
                    ) {
                        Text(
                            text = if (busqueda.isBlank())
                                "Aún no hay productos registrados.\nPresiona + para agregar uno."
                            else
                                "No se encontraron productos.",
                            modifier = Modifier.padding(20.dp),
                            color = GrisTexto
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(productos) { producto ->
                            ProductoCard(
                                producto = producto,
                                onEditar = { onEditar(producto) },
                                onEliminar = { onEliminar(producto) },
                                onEntrada = { cantidad -> onEntrada(producto, cantidad) },
                                onSalida = { cantidad -> onSalida(producto, cantidad) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BotonCategorias(
    categorias: List<String>,
    onAgregarCategoria: (String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { mostrarDialogo = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrincipal)
    ) {
        Text("Administrar categorías")
    }

    if (mostrarDialogo) {
        DialogoCategorias(
            categorias = categorias,
            onAgregarCategoria = onAgregarCategoria,
            onCerrar = { mostrarDialogo = false }
        )
    }
}

@Composable
fun DialogoCategorias(
    categorias: List<String>,
    onAgregarCategoria: (String) -> Unit,
    onCerrar: () -> Unit
) {
    var nuevaCategoria by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(
                text = "Categorías",
                color = AzulPrincipal,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Categorías disponibles:", color = GrisTexto)
                Spacer(modifier = Modifier.height(8.dp))

                categorias.take(8).forEach {
                    Text("• $it", color = AzulPrincipal)
                }

                Spacer(modifier = Modifier.height(12.dp))

                CampoTexto(
                    valor = nuevaCategoria,
                    onCambio = { nuevaCategoria = it },
                    etiqueta = "Nueva categoría"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nuevaCategoria.isNotBlank()) {
                        onAgregarCategoria(nuevaCategoria.trim())
                        nuevaCategoria = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Color.White
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCerrar,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrincipal)
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ResumenCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BlancoCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto
            )
        }
    }
}

@Composable
fun ProductoCard(
    producto: ProductoEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onEntrada: (Int) -> Unit,
    onSalida: (Int) -> Unit
) {
    var mostrarDialogoEntrada by remember { mutableStateOf(false) }
    var mostrarDialogoSalida by remember { mutableStateOf(false) }

    val esBajoStock = producto.cantidad <= producto.stockMinimo
    val estaPorVencer = productoProximoAVencer(producto.fechaCaducidad)

    val colorTarjeta = when {
        esBajoStock -> NaranjaAlerta
        estaPorVencer -> AmarilloAlerta
        else -> BlancoCard
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Categoría: ${producto.categoria}", color = GrisTexto)
            Text("Precio de venta: $${producto.precio} MXN", color = GrisTexto)
            Text("Cantidad disponible: ${producto.cantidad} ${producto.unidad}", color = GrisTexto)
            Text("Caducidad: ${producto.fechaCaducidad}", color = GrisTexto)
            Text("Stock mínimo: ${producto.stockMinimo} ${producto.unidad}", color = GrisTexto)

            if (esBajoStock) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⚠ Producto con bajo stock",
                    color = RojoAccion,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (estaPorVencer) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📅 Producto próximo a vencer",
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onEditar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrincipal,
                        contentColor = Color.White
                    )
                ) {
                    Text("Editar")
                }

                Button(
                    onClick = { mostrarDialogoEntrada = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulAccion,
                        contentColor = Color.White
                    )
                ) {
                    Text("Entrada")
                }

                Button(
                    onClick = { mostrarDialogoSalida = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulSecundario,
                        contentColor = Color.White
                    )
                ) {
                    Text("Salida")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onEliminar,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoAccion)
            ) {
                Text("Eliminar")
            }
        }
    }

    if (mostrarDialogoEntrada) {
        DialogoCantidad(
            titulo = "Registrar entrada",
            onConfirmar = { cantidad ->
                onEntrada(cantidad)
                mostrarDialogoEntrada = false
            },
            onCancelar = { mostrarDialogoEntrada = false }
        )
    }

    if (mostrarDialogoSalida) {
        DialogoCantidad(
            titulo = "Registrar salida",
            onConfirmar = { cantidad ->
                onSalida(cantidad)
                mostrarDialogoSalida = false
            },
            onCancelar = { mostrarDialogoSalida = false }
        )
    }
}

@Composable
fun FormularioProducto(
    productoEditar: ProductoEntity? = null,
    categorias: List<String>,
    onGuardar: (
        nombre: String,
        categoria: String,
        precio: Double,
        cantidad: Int,
        unidad: String,
        fecha: String,
        stockMinimo: Int
    ) -> Unit,
    onCancelar: () -> Unit
) {
    val unidades = listOf("Piezas", "Cajas", "Paquetes", "Botellas", "Latas", "Bolsas", "Kg", "Litros")

    var nombre by remember { mutableStateOf(productoEditar?.nombre ?: "") }
    var categoria by remember { mutableStateOf(productoEditar?.categoria ?: "") }
    var precio by remember { mutableStateOf(productoEditar?.precio?.toString() ?: "") }
    var cantidad by remember { mutableStateOf(productoEditar?.cantidad?.toString() ?: "") }
    var unidad by remember { mutableStateOf(productoEditar?.unidad ?: "Piezas") }
    var fechaCaducidad by remember { mutableStateOf(productoEditar?.fechaCaducidad ?: "") }
    var stockMinimo by remember { mutableStateOf(productoEditar?.stockMinimo?.toString() ?: "") }

    val formularioCompleto =
        nombre.isNotBlank() &&
                categoria.isNotBlank() &&
                precio.isNotBlank() &&
                cantidad.isNotBlank() &&
                unidad.isNotBlank() &&
                fechaCaducidad.length == 10 &&
                stockMinimo.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (productoEditar == null) "Registrar producto" else "Editar producto",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AzulPrincipal
        )

        Text(
            text = "Completa todos los campos para guardar el producto.",
            color = GrisTexto
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CampoTexto(nombre, { nombre = it }, "Nombre del producto *")

                CategoriaDropdown(
                    categoriaSeleccionada = categoria,
                    categorias = categorias,
                    onCategoriaSeleccionada = { categoria = it }
                )

                CampoTexto(
                    valor = precio,
                    onCambio = { precio = filtrarDecimal(it) },
                    etiqueta = "Precio de venta *",
                    placeholder = "MXN",
                    keyboardType = KeyboardType.Decimal,
                    suffix = "MXN"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CampoTexto(
                        valor = cantidad,
                        onCambio = { cantidad = filtrarEntero(it) },
                        etiqueta = "Cantidad *",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )

                    DropdownSimple(
                        valorSeleccionado = unidad,
                        opciones = unidades,
                        etiqueta = "Unidad *",
                        onSeleccionar = { unidad = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                CampoTexto(
                    valor = fechaCaducidad,
                    onCambio = { fechaCaducidad = formatearFecha(it) },
                    etiqueta = "Fecha de caducidad *",
                    placeholder = "20/05/2026",
                    keyboardType = KeyboardType.Number
                )

                CampoTexto(
                    valor = stockMinimo,
                    onCambio = { stockMinimo = filtrarEntero(it) },
                    etiqueta = "Stock mínimo *",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!formularioCompleto) {
                    Text(
                        text = "Todos los campos son obligatorios.",
                        color = RojoAccion,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (formularioCompleto) {
                            onGuardar(
                                nombre.trim(),
                                categoria.trim(),
                                precio.toDoubleOrNull() ?: 0.0,
                                cantidad.toIntOrNull() ?: 0,
                                unidad.trim(),
                                fechaCaducidad.trim(),
                                stockMinimo.toIntOrNull() ?: 0
                            )
                        }
                    },
                    enabled = formularioCompleto,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulAccion,
                        contentColor = Color.White,
                        disabledContainerColor = GrisBorde,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (productoEditar == null) "Guardar producto" else "Actualizar producto")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrincipal)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDropdown(
    categoriaSeleccionada: String,
    categorias: List<String>,
    onCategoriaSeleccionada: (String) -> Unit
) {
    ExposedDropdown(
        valorSeleccionado = categoriaSeleccionada,
        opciones = categorias,
        etiqueta = "Categoría *",
        onSeleccionar = onCategoriaSeleccionada
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSimple(
    valorSeleccionado: String,
    opciones: List<String>,
    etiqueta: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdown(
        valorSeleccionado = valorSeleccionado,
        opciones = opciones,
        etiqueta = etiqueta,
        onSeleccionar = onSeleccionar,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdown(
    valorSeleccionado: String,
    opciones: List<String>,
    etiqueta: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valorSeleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(14.dp),
            colors = coloresCampoTexto()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    suffix: String = "",
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        placeholder = {
            if (placeholder.isNotBlank()) Text(placeholder)
        },
        suffix = {
            if (suffix.isNotBlank()) Text(suffix, color = GrisTexto)
        },
        modifier = modifier.padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = coloresCampoTexto()
    )
}

@Composable
fun coloresCampoTexto(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AzulPrincipal,
        unfocusedBorderColor = GrisBorde,
        focusedLabelColor = AzulPrincipal,
        unfocusedLabelColor = GrisTexto,
        cursorColor = AzulPrincipal,
        focusedTextColor = AzulPrincipal,
        unfocusedTextColor = AzulPrincipal,
        focusedPlaceholderColor = GrisTexto,
        unfocusedPlaceholderColor = GrisTexto
    )
}

@Composable
fun DialogoCantidad(
    titulo: String,
    onConfirmar: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    var cantidad by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, color = AzulPrincipal, fontWeight = FontWeight.Bold) },
        text = {
            CampoTexto(
                valor = cantidad,
                onCambio = { cantidad = filtrarEntero(it) },
                etiqueta = "Cantidad",
                keyboardType = KeyboardType.Number
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                    if (cantidadInt > 0) onConfirmar(cantidadInt)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulAccion,
                    contentColor = Color.White
                )
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancelar,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrincipal)
            ) {
                Text("Cancelar")
            }
        }
    )
}

fun filtrarEntero(texto: String): String {
    return texto.filter { it.isDigit() }
}

fun filtrarDecimal(texto: String): String {
    var puntoUsado = false

    return texto.filter { caracter ->
        when {
            caracter.isDigit() -> true
            caracter == '.' && !puntoUsado -> {
                puntoUsado = true
                true
            }
            else -> false
        }
    }
}

fun formatearFecha(texto: String): String {
    val numeros = texto.filter { it.isDigit() }.take(8)

    return buildString {
        numeros.forEachIndexed { index, char ->
            append(char)
            if (index == 1 || index == 3) append("/")
        }
    }
}

fun productoProximoAVencer(fecha: String): Boolean {
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaCaducidad = formato.parse(fecha)

        val calendarioActual = Calendar.getInstance()
        val calendarioVencimiento = Calendar.getInstance()
        calendarioVencimiento.time = fechaCaducidad!!

        val diferencia = calendarioVencimiento.timeInMillis - calendarioActual.timeInMillis
        val diasRestantes = diferencia / (1000 * 60 * 60 * 24)

        diasRestantes in 0..7
    } catch (e: Exception) {
        false
    }
}

fun exportarCSV(context: Context, productos: List<ProductoEntity>) {
    try {

        val carpetaDescargas =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (!carpetaDescargas.exists()) {
            carpetaDescargas.mkdirs()
        }

        val archivo = File(carpetaDescargas, "inventario.csv")

        val writer = FileWriter(archivo)

        writer.append(
            "Nombre,Categoria,PrecioVenta,Cantidad,Unidad,Caducidad,StockMinimo\n"
        )

        productos.forEach { producto ->
            writer.append(
                "${producto.nombre}," +
                        "${producto.categoria}," +
                        "${producto.precio}," +
                        "${producto.cantidad}," +
                        "${producto.unidad}," +
                        "${producto.fechaCaducidad}," +
                        "${producto.stockMinimo}\n"
            )
        }

        writer.flush()
        writer.close()

        Toast.makeText(
            context,
            "Inventario exportado en Descargas",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "Error al exportar CSV",
            Toast.LENGTH_LONG
        ).show()
    }
}