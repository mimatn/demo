import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class YamlLoader {
    private val mapper = ObjectMapper(YAMLFactory());

    fun loadDirectory(path : String) : Map<String, JsonNode> {
        // Prendo i file Yaml all'interno del path
        val folder = File(path);
        val result = mutableMapOf<String, JsonNode>();

        // Controllo che il path esista e sia in una cartella
        if(!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Path non trovata e non valida: $path");
        }

        // Leggo i file Yaml e li converto in JsonNode per poi gestirli più facilmente
        val files = folder.listFiles();

        if(files!=null){
            for(f in files){
                if(f.isFile && f.extension.equals("yaml")){
                    // Conversione in JsonNode
                    val node = mapper.readTree(f)
                    result[f.name] = node;
                }
            }
        }

        return result;
    }
}