package com.example.demo.parser

import com.example.demo.model.YamlFileInfo
import com.fasterxml.jackson.databind.JsonNode
import kotlin.collections.iterator
import kotlin.collections.mutableListOf

fun extractFileInfo(file: String, root: JsonNode, refExtractor: RefExtractor): YamlFileInfo {
    // Controllo che il file abbia un campo info
    val titleNode = root.path("info").path("title")
    val title = if (titleNode.isMissingNode) {
        println("Attenzione: $file non contiene info.title")
        null
    } else {
        titleNode.asText()
    }

    // Controllo che il file abbia un campo components.schemas
    val schemaNodes = root.path("components").path("schemas")
    val schemaNames = mutableListOf<String>()
    if (schemaNodes.isObject) {
        for (name in schemaNodes.fieldNames()) {
            schemaNames.add(name)
        }
    } else {
        println("Attenzione: $file non contiene components.schemas")
    }

    // Prendo i nomi degli schemi definiti nel file
    val schemasNames = mutableListOf<String>()
    for (s in schemaNodes.fieldNames()) {
        schemasNames.add(s)
    }

    // Controllo che il file abbia u campo paths
    val pathsNode = root.path("paths")
    val pathNames = mutableListOf<String>()
    if (pathsNode.isObject) {
        for (name in pathsNode.fieldNames()) {
            pathNames.add(name)
        }
    } else {
        println("Attenzione: $file non contiene paths")
    }

    // Prendo i $ref presenti nel file
    val refs = refExtractor.findRef(root)

    // Crea un oggetto YamlFileInfo con le informazioni estratte dal file
    return YamlFileInfo(file, title, schemasNames, pathNames, refs)
}
