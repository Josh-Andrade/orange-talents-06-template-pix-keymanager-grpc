package br.com.ot6.itau

import io.micronaut.context.annotation.Value
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths

data class Instituicao(
    val nome: String,
    val ispb: String,

) {
    companion object{

        fun find(ispb: String): String? {
            val iterator = 0

            val csvParser = CSVParser(
                Files.newBufferedReader(Paths.get("ParticipantesSTRport.csv")),
                CSVFormat.DEFAULT
            )

            csvParser.map {
                if(it.get(iterator) == ispb){
                    return it.get(iterator + 1)
                }
            }
            return null;
        }
    }
}
