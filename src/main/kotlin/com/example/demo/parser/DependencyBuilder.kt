import com.fasterxml.jackson.databind.JsonNode

class DependencyBuilder {

    fun buildFileDependencies(file: List<YamlFileInfo>): List<DependencyEdge> {
        // Creo una lista di dipendenze tra i file
        val dip = mutableListOf<DependencyEdge>()

        // Per ogni file controllo se ha dei $ref che puntano ad altri file
        for (f in file) {
            for (ref in f.refs) {
                // Se il ref fa riferimento ad un altro file creo una dipendenza tra i due file
                if (ref.startsWith("./")) {
                    val target = ref.substringBefore("#").removePrefix("./")

                    dip.add(DependencyEdge(f.fileName, target, "file_ref"))
                }
            }
        }

        // Ritorno la lista delle dipendenze eliminando i duplicati (se ci sono)
        return dip
    }

    fun buildSchemaDependencies(fileName: String, root: JsonNode): List<DependencyEdge> {
        // Creo una lista di dipendenze tra gli schemi
        val edge = mutableListOf<DependencyEdge>()
        val refExtractor = RefExtractor()

        // Controllo se il file ha la sezione components.schemas
        val nodeOfSchemas = root.path("components").path("schemas")

        if (!nodeOfSchemas.isObject) {
            println("Attenzione: $fileName non contiene components.schemas")
            return edge
        }

        // Per ogni schema controllo se ha dei ref che puntano ad altri schemi
        val field = nodeOfSchemas.fields()
        while (field.hasNext()) {
            val entry = field.next()
            val schemaName = entry.key
            val schemaBody = entry.value
            val refs = refExtractor.findRef(schemaBody)

            // Per ogni ref controllo se punta ad un altro schema (interno o esterno) e creo una
            // dipendenza
            for (e in refs) {
                if (e.startsWith("#/components/schemas/")) {
                    val targetSchema = e.substringAfterLast("/")
                    edge.add(
                            DependencyEdge(
                                    "$fileName::$schemaName",
                                    "$fileName::$targetSchema",
                                    "internal_schema_ref"
                            )
                    )
                    // Se il ref punta ad un altro schema esterno creo una dipendenza tra i due file e i due schemi
                } else if (e.startsWith("./")) {
                    val targetSchema =
                            if (e.contains("/components/schemas/")) {
                                e.substringAfterLast("/")
                            } else {
                                // Se il ref non contiene la parte /components/schemas/ non riesco ad identificare lo schema di destinazione, quindi lo indico come UNDEFINED_SCHEMA
                                "UNDEFINED_SCHEMA"
                            }
                    edge.add(
                            DependencyEdge(
                                    "$fileName::$schemaName",
                                    "$fileName::$targetSchema",
                                    "external_schema_ref"
                            )
                    )
                }
            }
        }
        return edge
    }

    fun buildOperationDependencies(fileName: String, root: JsonNode): List<DependencyEdge> {
        // Creo una lista di dipendenze tra le operazioni e gli schemi
        val edges = mutableListOf<DependencyEdge>()
        val refExtractor = RefExtractor()
        val pathsNode = root.path("paths")
        if (!pathsNode.isObject) {
            println("Attenzione: $fileName non contiene paths")
            return edges
        }
        // Per ogni path e per ogni operazione controllo se ha dei ref che puntano ad altri schemi e creo

        val pathField = pathsNode.fields()
        while (pathField.hasNext()) {
            val pathEntry = pathField.next()
            val pathName = pathEntry.key
            val pathBody = pathEntry.value
            val operationField = pathBody.fields()
            //Per ogni operazione (get, post, put, delete, patch) controllo se ha dei ref che puntano ad altri
            while (operationField.hasNext()) {
                val operationEntry = operationField.next()
                val method = operationEntry.key
                val operationBody = operationEntry.value

                if (method != "get" &&
                                method != "post" &&
                                method != "put" &&
                                method != "delete" &&
                                method != "patch"
                ) {
                    continue
                }
                // Creo un nodo per rappresentare l'operazione
                val operationNode = "$fileName::${method.uppercase()} $pathName"
                val refs = refExtractor.findRef(operationBody)

                for (e in refs) {
                    if (e.startsWith("#/components/schemas/")) {
                        val targetSchema = e.substringAfterLast("/")

                        edges.add(
                                DependencyEdge(
                                        from = operationNode,
                                        to = "$fileName::$targetSchema",
                                        type = "operation_internal_schema_ref"
                                )
                        )
                    } else if (e.startsWith("./")) {
                        val targetFile = e.substringBefore("#").removePrefix("./")
                        val targetSchema =
                                if (e.contains("/components/schemas/")) {
                                    e.substringAfterLast("/")
                                } else {
                                    "UNDEFINED_SCHEMA"
                                }

                        edges.add(
                                DependencyEdge(
                                        from = operationNode,
                                        to = "$targetFile::$targetSchema",
                                        type = "operation_external_schema_ref"
                                )
                        )
                    }
                }
            }
        }
        return edges
    }

    fun countDependencies(edge: List<DependencyEdge>): List<CountedDependencyEdge> {
        // Creo una mappa per contare le dipendenze
        val count = mutableMapOf<Triple<String, String, String>, Int>()

        // Per ogni dipendenza incremento il contatore corrispondente
        for (e in edge) {
            val key = Triple(e.from, e.to, e.type)
            if (count.containsKey(key)) {
                count[key] = count[key]!! + 1
            } else {
                count[key] = 1
            }
        }

        // Creo una lista di dipendenze contate a partire dalla mappa
        val result = mutableListOf<CountedDependencyEdge>()

        // Per ogni elemento della mappa creo una dipendenza e la aggiungo alla lista
        for ((key, c) in count) {
            result.add(CountedDependencyEdge(key.first, key.second, key.third, c))
        }
        return result
    }
}
