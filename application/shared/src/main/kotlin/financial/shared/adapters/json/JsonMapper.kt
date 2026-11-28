package financial.shared.adapters.json

import kotlin.reflect.KClass

/**
 * Translation between values and their JSON text.
 */
interface JsonMapper {
    /**
     * Writes the given value as JSON text.
     */
    fun toJson(value: Any): String

    /**
     * Reads the given JSON text as a value of the given type, ignoring the fields the type does not declare.
     */
    fun <T : Any> toObject(json: String, type: KClass<T>): T
}
