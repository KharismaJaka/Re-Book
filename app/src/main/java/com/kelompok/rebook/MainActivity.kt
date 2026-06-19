package com.kelompok.rebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelompok.rebook.ui.navigation.ReBookNavGraph
import com.kelompok.rebook.ui.theme.ReBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReBookTheme {
                ReBookNavGraph()
            }
        }
    }
}
