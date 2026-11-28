package financial.shared.adapters.json

import tools.jackson.databind.DeserializationFeature
import tools.jackson.module.kotlin.jacksonMapperBuilder
import kotlin.reflect.KClass

class JacksonJsonMapper : JsonMapper {

    private val mapper = jacksonMapperBuilder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()

    override fun toJson(value: Any): String = mapper.writeValueAsString(value)

    override fun <T : Any> toObject(json: String, type: KClass<T>): T = mapper.readValue(json, type.javaObjectType)
}
