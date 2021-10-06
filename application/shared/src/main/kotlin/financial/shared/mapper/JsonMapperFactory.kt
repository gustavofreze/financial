package financial.shared.mapper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KClass

class JsonMapperFactory : JsonMapper {

    private val mapper: ObjectMapper = jacksonObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.findAndRegisterModules()
    }

    override fun toJson(value: Any): String {
        return mapper.writeValueAsString(value)
    }

    override fun <T : Any> toObject(json: String, clazz: KClass<T>): T {
        return mapper.readValue(json, clazz.javaObjectType)
    }
}