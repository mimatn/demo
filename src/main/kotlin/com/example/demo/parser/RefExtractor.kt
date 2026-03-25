package com.example.demo.parser

import com.fasterxml.jackson.databind.JsonNode

class RefExtractor {

    // Funzione per trovare i $ref all'interno di un JsonNode
    fun findRef(node : JsonNode) : List<String>{
        val ref = mutableListOf<String>();
        visitNode(node, ref)
        return ref;
    }

    // Funzione ricorsiva per visitare tutti i nodi del JsonNode e trovare i $ref
    fun visitNode(node : JsonNode, ref : MutableList<String>){
        if(node.isObject){
            // Controllo se il nodo è un oggetto e se ha un campo $ref
            val field = node.fields();

            // Se il campo è un $ref lo aggiungo alla lista, altrimenti continuo la visita
            while(field.hasNext()){
                val entry= field.next();

                if(entry.key == "\$ref"){
                    ref.add(entry.value.asText())
                }else{
                    visitNode(entry.value, ref);
                }
            }
        }

        // Se il nodo è un array visito tutti i suoi elementi
        if(node.isArray){
            for (e in node){
                visitNode(e, ref);
            }
        }
    }
}