package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.AtelierApp
import com.example.viewmodel.AtelierViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: AtelierViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AtelierApp(viewModel = viewModel)
    }
  }
}
