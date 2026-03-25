package com.example.demo

import com.example.demo.analysis.GraphAnalyzer
import com.example.demo.io.CsvExporter
import com.example.demo.model.DependencyEdge
import com.example.demo.model.YamlFileInfo
import com.example.demo.parser.DependencyBuilder
import com.example.demo.parser.RefExtractor
import com.example.demo.parser.YamlLoader
import com.example.demo.parser.extractFileInfo

fun main() {
    // Inizializzo gli oggetti
    val loader = YamlLoader()
    val refExtractor = RefExtractor()
    val files = loader.loadDirectory("src/main/resources/yaml")

    // Estraggo le info dai file yaml in una lista
    val fileInfoList = mutableListOf<YamlFileInfo>()
    val allDependencies = mutableListOf<DependencyEdge>()

    for ((fileName, root) in files) {
        val fileInfo = extractFileInfo(fileName, root, refExtractor)
        fileInfoList.add(fileInfo)
    }

    allDependencies.addAll(DependencyBuilder().buildFileDependencies(fileInfoList))

    for ((fileName, root) in files) {
        allDependencies.addAll(DependencyBuilder().buildSchemaDependencies(fileName, root))
        allDependencies.addAll(DependencyBuilder().buildOperationDependencies(fileName, root))
    }

    val countedDependencies =
        DependencyBuilder().countDependencies(allDependencies).sortedByDescending { it.count }

    val analyzer = GraphAnalyzer()
    val nodeStats = analyzer.computeNodeStats(countedDependencies)

    val exporter = CsvExporter()
    exporter.exportDependencies(
        countedDependencies,
        "output\\dependencies.csv"
    )

    exporter.exportNodeStats(
        nodeStats,
        "output\\nodeStats.csv"
    )

    println()
    println("CSV esportati correttamente.")

    println("==============================================")
    println("INFORMAZIONI FILE")
    println("==============================================")

    for (info in fileInfoList) {
        println("File: ${info.fileName}")
        println("Titolo: ${info.title ?: "N/A"}")
        println("Schemi: ${info.schemaNames.size}")
        println("Paths: ${info.pathNames.size}")
        println("Refs: ${info.refs.size}")
        println("--------------------------------------------------")
    }

    println()
    println("==============================================")
    println("DIPENDENZE TOTALI")
    println("==============================================")
    println("Numero dipendenze raw: ${allDependencies.size}")
    println("Numero dipendenze aggregate: ${countedDependencies.size}")
    println()

    for (dep in countedDependencies) {
        println("${dep.from} -> ${dep.to} [${dep.type}] : ${dep.count}")
    }

    println()
    println("==============================================")
    println("TOP NODI")
    println("==============================================")

    for (stat in nodeStats.take(20)) {
        println("${stat.node}")
        println("  outDegree = ${stat.outDegree}")
        println("  inDegree  = ${stat.inDegree}")
        println("  total     = ${stat.outDegree + stat.inDegree}")
        println("--------------------------------------------------")
    }
}
