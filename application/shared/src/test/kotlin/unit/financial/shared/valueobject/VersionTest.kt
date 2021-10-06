package unit.financial.shared.valueobject

import financial.shared.valueobject.Version
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class VersionTest {

    @ParameterizedTest
    @MethodSource("invalidVersionProvider")
    fun `GIVEN the construction of a version, WHEN value less than 0, THEN return error`(value: Long) {
        assertThrows<Exception>("Invalid version") {
            Version(value)
        }
    }

    @Test
    fun `GIVEN two valid versions, WHEN the value is equal between them, THEN they are equal`() {
        val versionY = Version(value = 1)
        val versionX = Version(value = 1)

        assertEquals(versionY, versionX)
        assertEquals(versionY.value, versionX.value)
    }

    @Test
    fun `GIVEN I have the first version, WHEN the next method is called, THEN increment the version`() {
        var version = Version.first()

        assertEquals(1, version.value)

        version = version.next()

        assertEquals(2, version.value)
    }

    companion object {
        @JvmStatic
        fun invalidVersionProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(-1L),
                Arguments.of(-10000000000000L)
            )
        }
    }
}