import java.io.File

class CsvExporter {

    // Esporta le dipendenze in un file CSV con le colonne: from, to, type, count
    fun exportDependencies(dependencies: List<CountedDependencyEdge>, outputPath: String) {
        val file = File(outputPath)
        file.printWriter().use { out ->
            out.println("from,to,type,count")

            for (dep in dependencies) {
                out.println(
                        "${escape(dep.from)}," +
                                "${escape(dep.to)}," +
                                "${escape(dep.type)}," +
                                dep.count
                )
            }
        }
    }

    // Esporta le statistiche dei nodi in un file CSV con le colonne: node, outDegree, inDegree, total
    fun exportNodeStats(nodeStats: List<NodeStats>, outputPath: String) {
        val file = File(outputPath)
        file.printWriter().use { out ->
            out.println("node,outDegree,inDegree,total")

            for (stat in nodeStats) {
                val total = stat.outDegree + stat.inDegree
                out.println(
                        "${escape(stat.node)}," + stat.outDegree + "," + stat.inDegree + "," + total
                )
            }
        }
    }

    // Funzione per gestire l'escape dei valori che contengono virgole o virgolette
    private fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}
