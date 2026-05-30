# 📦 StockApp

> Aplicación Android para control profesional de inventario, desarrollada con Kotlin, Jetpack Compose y Room Database.

---

## 📋 Tabla de contenido

- [Descripción](#descripción)
- [Capturas de pantalla](#capturas-de-pantalla)
- [Características](#características)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Requisitos previos](#requisitos-previos)
- [Instalación en Android Studio](#instalación-en-android-studio)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Arquitectura](#arquitectura)
- [Base de datos](#base-de-datos)
- [Autores](#autores)

---

## Descripción

**StockApp** es una aplicación móvil Android diseñada para que pequeños negocios puedan gestionar su inventario de forma sencilla y sin conexión a internet. Permite registrar productos, controlar entradas y salidas de mercancía, recibir alertas de stock bajo y exportar el inventario completo en formato CSV.

La app funciona completamente **offline**: todos los datos se guardan de forma local en el dispositivo usando una base de datos SQLite gestionada por Room.

---

## Características

- ✅ Registro, edición y eliminación de productos
- ✅ Búsqueda de productos por nombre o categoría
- ✅ Registro de entradas y salidas de inventario con historial
- ✅ Alertas automáticas cuando el stock cae por debajo del mínimo
- ✅ Detección visual de productos próximos a vencer (≤ 7 días)
- ✅ Gestión de categorías personalizadas
- ✅ Exportación del inventario a archivo CSV en la carpeta Descargas
- ✅ Interfaz moderna con Material Design 3
- ✅ Funciona 100% sin conexión a internet

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Lenguaje de programación principal |
| Jetpack Compose | BOM 2024.09.00 | Interfaz de usuario declarativa |
| Room | 2.8.4 | Base de datos local (SQLite) |
| ViewModel | 2.10.0 | Manejo de estado y lógica de negocio |
| Kotlin Coroutines | — | Operaciones asíncronas |
| StateFlow / Flow | — | Actualización reactiva de la UI |
| Navigation Compose | 2.9.5 | Navegación entre pantallas |
| KSP | 2.2.20-2.0.3 | Procesador de anotaciones para Room |
| Material Design 3 | — | Componentes visuales |
| Android Gradle Plugin | 9.0.1 | Compilación del proyecto |

---

## Requisitos previos

Antes de instalar el proyecto asegúrate de tener lo siguiente:

- **Android Studio** Hedgehog (2023.1.1) o versión más reciente
- **JDK 11** o superior (incluido en Android Studio)
- **SDK de Android** con API Level 24 o superior instalado
- **Conexión a internet** al abrir el proyecto por primera vez (para descargar dependencias de Gradle)
- Al menos **512 MB de espacio libre** en disco

> **Versión mínima de Android:** API 24 (Android 7.0 Nougat)
> **Versión objetivo:** API 36 (Android 16)

---

## Instalación en Android Studio

Sigue estos pasos en orden para abrir y ejecutar el proyecto correctamente.

### Paso 1 — Descargar el proyecto

Descarga el archivo en GitHub el repositorio `git clone https://github.com/RodrigoRZC/StockApp` y descomprímelo en una carpeta de tu preferencia. Asegúrate de que la ruta **no contenga espacios ni caracteres especiales** (por ejemplo, evita carpetas como `Mi Proyecto` o `C:\Users\José`).

```
✅ Correcto:   C:\Proyectos\StockApp
❌ Incorrecto: C:\Users\José Luis\Mis Documentos\Práctica Final
```

### Paso 2 — Abrir el proyecto en Android Studio

1. Abre **Android Studio**.
2. En la pantalla de bienvenida selecciona **Open** (o desde el menú **File → Open**).
3. Navega hasta la carpeta descomprimida y selecciona la carpeta raíz `StockApp` (la que contiene el archivo `build.gradle.kts` principal).
4. Haz clic en **OK**.

> ⚠️ Abre la carpeta **`StockApp`**, no un archivo `.kt` ni la carpeta `app` directamente.

### Paso 3 — Esperar la sincronización de Gradle

Al abrir el proyecto, Android Studio descargará automáticamente todas las dependencias (Room, Compose, etc.). Este proceso puede tardar entre **2 y 10 minutos** dependiendo de tu conexión a internet.

Sabrás que terminó cuando desaparezca la barra de progreso en la parte inferior y no aparezca ningún error en la pestaña **Build**.

Si aparece el mensaje *"Gradle sync failed"*, revisa lo siguiente:

- Que tienes conexión a internet activa.
- Que el SDK de Android está instalado. Ve a **File → Settings → Android SDK** y verifica que al menos la API 24 esté descargada.

### Paso 4 — Configurar un dispositivo para ejecutar la app

Tienes dos opciones:

**Opción A — Emulador (recomendado si no tienes un celular Android)**

1. Ve al menú **Tools → Device Manager**.
2. Haz clic en **Create Device**.
3. Selecciona un modelo de teléfono (por ejemplo *Pixel 6*) y haz clic en **Next**.
4. Descarga una imagen del sistema con **API Level 24 o superior** (se recomienda API 35) y haz clic en **Next**.
5. Deja el nombre por defecto y haz clic en **Finish**.
6. El emulador aparecerá en la lista; haz clic en el botón ▶ para iniciarlo.

**Opción B — Dispositivo físico Android**

1. En tu celular ve a **Configuración → Acerca del teléfono** y toca **Número de compilación** 7 veces seguidas para activar las opciones de desarrollador.
2. Ve a **Configuración → Opciones de desarrollador** y activa **Depuración USB**.
3. Conecta el celular a tu computadora con un cable USB.
4. En tu celular acepta el mensaje de *"¿Permitir depuración USB?"*.
5. El dispositivo aparecerá en Android Studio como opción de ejecución.

### Paso 5 — Ejecutar la aplicación

1. En la barra superior de Android Studio selecciona tu dispositivo (emulador o físico) en el menú desplegable.
2. Haz clic en el botón **▶ Run** (o presiona `Shift + F10`).
3. Android Studio compilará el proyecto e instalará la app automáticamente en el dispositivo.
4. La aplicación se abrirá mostrando la pantalla principal de inventario.

---

## Estructura del proyecto

```
StockApp/
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/example/stockapp/
│               ├── MainActivity.kt                          # Punto de entrada
│               ├── data/
│               │   └── local/
│               │       ├── StockDatabase.kt                 # Base de datos Room
│               │       ├── dao/
│               │       │   ├── ProductoDao.kt               # CRUD de productos
│               │       │   ├── CategoriaDao.kt              # Operaciones de categorías
│               │       │   ├── MovimientoInventarioDao.kt   # Historial de movimientos
│               │       │   └── AlertaStockDao.kt            # Gestión de alertas
│               │       └── entities/
│               │           ├── ProductoEntity.kt            # Tabla productos
│               │           ├── CategoriaEntity.kt           # Tabla categorias
│               │           ├── MovimientoInventarioEntity.kt# Tabla movimientos
│               │           └── AlertaStockEntity.kt         # Tabla alertas
│               ├── repository/
│               │   └── StockRepository.kt                   # Capa de acceso a datos
│               └── ui/
│                   ├── screens/
│                   │   └── ProductoScreen.kt                # Pantalla principal
│                   ├── theme/
│                   │   ├── Color.kt                         # Paleta de colores
│                   │   ├── Theme.kt                         # Tema Material3
│                   │   └── Type.kt                          # Tipografía
│                   └── viewmodel/
│                       └── StockViewModel.kt                # Lógica de negocio
├── app/build.gradle.kts                                     # Dependencias del módulo
├── build.gradle.kts                                         # Configuración raíz
└── gradle/
    └── libs.versions.toml                                   # Catálogo de versiones
```

---

## Arquitectura

El proyecto implementa la arquitectura **MVVM (Model-View-ViewModel)** recomendada por Google:

```
┌─────────────────────────────────────┐
│           PRESENTATION              │
│  MainActivity → ProductoScreen      │
│         ↕ observa StateFlow         │
│         StockViewModel              │
└──────────────┬──────────────────────┘
               │ llama a
┌──────────────▼──────────────────────┐
│            REPOSITORY               │
│          StockRepository            │
└──────────────┬──────────────────────┘
               │ usa
┌──────────────▼──────────────────────┐
│            DATA / LOCAL             │
│  StockDatabase (Room / SQLite)      │
│  ├── DAO (operaciones)              │
│  └── Entities (tablas)             │
└─────────────────────────────────────┘
```

---

## Base de datos

La app utiliza **4 tablas** en SQLite gestionadas por Room:

| Tabla | Entidad | Descripción |
|---|---|---|
| `productos` | `ProductoEntity` | Catálogo de productos con precio, cantidad y caducidad |
| `categorias` | `CategoriaEntity` | Categorías personalizadas del inventario |
| `movimientos` | `MovimientoInventarioEntity` | Historial de entradas y salidas |
| `alertas` | `AlertaStockEntity` | Alertas de stock bajo generadas automáticamente |

El archivo de base de datos se guarda localmente en:
```
/data/data/com.example.stockapp/databases/stock_database
```

---

## Autores

Proyecto desarrollado como práctica final de la materia de Programación para Dispositivos Móviles.

| Integrante | Módulo desarrollado |
|---|---|
| Marcela Ayala Gómez. | Entidades y configuración de Room |
| Ignacio Lauriano Méndez Ocaña. | DAO e interfaces de consulta |
| Rodrigo Zúñiga Castro | ViewModel, Repository (lógica MVVM) e IU |
| Abril Nucamendi Guizar | Exportación CSV, fechas y detección de vencimiento |

---
