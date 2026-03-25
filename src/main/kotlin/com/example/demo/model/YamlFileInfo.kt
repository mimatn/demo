package com.example.demo.model

data class YamlFileInfo(
    val fileName: String,
    val title:String?,
    val schemaNames: List<String>,
    val pathNames: List<String>,
    val refs: List<String>
)