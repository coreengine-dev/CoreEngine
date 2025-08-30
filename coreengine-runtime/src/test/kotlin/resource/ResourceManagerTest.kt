package resource

import org.coreengine.api.resource.ResourceManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class ResourceManagerTest {

    @Test
    fun registerGetAndReleaseOwnedBy() {
        val rm = ResourceManager()
        val closes = AtomicInteger(0)

        rm.registerChannel<String>(
            channel = "mem",
            loader = { path -> "v:$path" },
            closer = { closes.incrementAndGet() }
        )

        // Dos ids distintos -> dos cierres al liberar la escena
        rm.get<String>("mem:/a.txt", sceneId = "S1")
        rm.get<String>("mem:/b.txt", sceneId = "S1")

        rm.releaseOwnedBy("S1")

        assertEquals(2, closes.get())
    }


    @Test
    fun adoptAndDuplicateIdFails() {
        val rm = ResourceManager()
        val closes = AtomicInteger(0)

        rm.adopt(id = "inmem:x", value = 7, closer = { closes.incrementAndGet() }, sceneId = "S2")
        assertTrue(rm.contains("inmem:x"))

        val ex = assertThrows(IllegalArgumentException::class.java) {
            rm.adopt(id = "inmem:x", value = 8, closer = { }, sceneId = "S2")
        }
        assertTrue(ex.message!!.contains("Recurso ya existente"))

        rm.releaseOwnedBy("S2")
        assertEquals(1, closes.get())
    }

    @Test
    fun reifiedGetShortcutWorks() {
        val rm = ResourceManager()
        rm.registerChannel<Int>(
            channel = "mem",
            loader = { path: String -> path.length },
            closer = { _: Int -> }
        )
        val v: Int = rm.get("mem:/abc", sceneId = "S3")
        assertEquals(4, v) // " /abc".length == 4
        rm.releaseOwnedBy("S3")
    }
}
