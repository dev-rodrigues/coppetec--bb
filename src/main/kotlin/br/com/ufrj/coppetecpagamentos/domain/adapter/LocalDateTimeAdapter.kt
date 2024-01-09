package br.com.ufrj.coppetecpagamentos.domain.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        if (value == null) {
            out?.nullValue()
        } else {
            out!!.beginObject()
            out.name("date")
            out.value(value.toLocalDate().toString())
            out.endObject()
        }
    }

    override fun read(p0: JsonReader?): LocalDateTime {
        p0?.beginObject()
        p0?.nextName()

        val date = LocalDate.parse(p0?.nextString())
        p0?.endObject()
        return date.atStartOfDay()
    }
}