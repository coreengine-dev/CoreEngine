# **CoreEngine Integration**

**CoreEngine Integration** is the module that acts as a **bridge between the [CoreEngine](../coreengine)** and **Jetpack Compose**.  
Its goal is to provide a **minimal, stable, and decoupled API** to embed the engine into modern Android apps with **declarative UIs**.

> 🌐 Official site: [https://coreengine.dev](https://coreengine.dev)  
> 📖 Based on the book *“Theory of EVERYTHING: Directed Curvature Tunnels (TCD)”* by Roberto Ariel Nicolini.

---

## **📚 Context**

This module was created to:

- Integrate the **cross-platform core** (`:coreengine`) with modern UIs.
- Keep the engine **framework-agnostic** while enabling integration with Compose.
- Follow the **TCD structural principles**, representing the flow `𝛂 → 𝜱 → 𝝮 → 𝐌 → 𝚿 → 𝝎`.

**Reference:**  
CoreEngine is the result of years of research by **Roberto Ariel Nicolini** and his **TCD** theory, aimed at structurally representing vibrations, curvatures, and conscious manifestations within simulated environments.

---

## **📦 Architecture**

The project is organized into clearly defined layers:

| **Layer**        | **Module**                 | **Responsibility**                                                                                 |
|------------------|----------------------------|----------------------------------------------------------------------------------------------------|
| **Core Engine**  | `:coreengine`              | Pure core, independent of Android or Compose. Manages the game loop, state, scenes, and rendering. |
| **Android Host** | `:coreengine-android-host` | Classic Android integration (`SurfaceView`, input, native HUD).                                    |
| **Integration**  | `:coreengine-integration`  | Compose API for embedding the engine declaratively.                                                |

---

## **🚀 How to Use**

Basic example in your main `Activity`:

```kotlin
class DemoActivity : ComponentActivity() {

    private lateinit var engine: CoreEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ Configure the engine
        engine = CoreEngine.Builder()
            .renderer(CanvasRenderer()) // Default renderer
            .sceneFactory { SplashScene() } // Initial scene
            .build()

        // 2️⃣ Mount into Compose
        setContent {
            CoreEngineHost(
                engine = engine,
                modifier = Modifier.fillMaxSize()
            ) { fps ->
                Text(
                    text = "FPS: $fps",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.stop() // Clean up on exit
    }
}
