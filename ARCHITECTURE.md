# CoreEngine Architecture

## Modules
- **coreengine-api**: stable public contracts (Camera, Entity, Renderer, HudLayer, Input).
- **coreengine-runtime**: deterministic loop, scene stack, ECS-lite, input manager.
- **coreengine-render-canvas**: CanvasRenderer, HUD metrics overlay.
- **coreengine-render-gl**: OpenGL ES 2.0 pipeline, GLSurfaceRenderer.
- **coreengine-integration**: HostBridge, UiOverlay, integration layer.
- **coreengine-android-host**: Android-specific bridge (HudManager, FontManager).
- **samples**: DemoApp with `SimpleScene`.
- **ide**: CoreStudio plugin (SceneGraph editor, viewport, inspector, profiler, hot reload).

## Principles
- Deterministic tickFrame: `intents → input → update → render → metrics`.
- ABI/API compatibility (SemVer 1.x).
- Clean modular design (Android-independent core).
- ECS hybrid with SceneGraph.
- TCD mapping: α (time), Φ (space/camera), Ω- (dynamics), M (render), Ψ (input), ω (metrics).
