package com.example.demo.model

// Data class per rappresentare un arco di dipendenza tra due file o entità
data class DependencyEdge(
    val from: String,
    val to: String,
    val type: String
)

// Data class per rappresentare un arco di dipendenza con un conteggio delle occorrenze
data class CountedDependencyEdge(
    val from: String,
    val to: String,
    val type: String,
    val count: Int
)
