package com.example.demo.model

// Data class per rappresentare le statistiche di un nodo nel grafo delle dipendenze
data class NodeStats(
    val node: String,
    val outDegree: Int,
    val inDegree: Int
)