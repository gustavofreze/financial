package financial.shared.mapper

import kotlin.reflect.KClass

interface JsonMapper {

    fun toJson(value: Any): String

    fun <T : Any> toObject(json: String, clazz: KClass<T>): T
}