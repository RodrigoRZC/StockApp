package com.example.stockapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockapp.ui.screens.ProductoScreen
import com.example.stockapp.ui.theme.StockAppTheme
import com.example.stockapp.ui.viewmodel.StockViewModel
import android.Manifest

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            StockAppTheme {

                val viewModel: StockViewModel = viewModel()

                ProductoScreen(viewModel = viewModel)
            }
        }
    }
}