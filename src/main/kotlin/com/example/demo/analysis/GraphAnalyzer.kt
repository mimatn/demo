package com.example.demo.analysis

import com.example.demo.model.CountedDependencyEdge
import com.example.demo.model.NodeStats

class GraphAnalyzer {

    // Funzione per calcolare le statistiche dei nodi a partire dalle dipendenze contate
    fun computeNodeStats(edges: List<CountedDependencyEdge>): List<NodeStats> {
        val outMap = mutableMapOf<String, Int>()
        val inMap = mutableMapOf<String, Int>()
        val allNodes = mutableSetOf<String>()

        // Costruisco le mappe di out-degree e in-degree
        for (e in edges) {
            allNodes.add(e.from)
            allNodes.add(e.to)

            if (outMap.containsKey(e.from)) {
                outMap[e.from] = outMap[e.from]!! + e.count
            } else {
                outMap[e.from] = e.count
            }

            if (inMap.containsKey(e.to)) {
                inMap[e.to] = inMap[e.to]!! + e.count
            } else {
                inMap[e.to] = e.count
            }
        }

        // Costruisco la lista finale di NodeStats
        val result = mutableListOf<NodeStats>()

        // Assicuro che tutti i nodi siano rappresentati, anche quelli senza dipendenze
        for (node in allNodes) {
            val outDegree = outMap[node] ?: 0
            val inDegree = inMap[node] ?: 0

            result.add(NodeStats(node, outDegree, inDegree))
        }

        return result
    }
}
