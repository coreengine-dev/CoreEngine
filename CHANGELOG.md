# CoreEngine Changelog

## [0.1.0] - 2025-08-25
### Added
- Initial repository structure with modular layout:
    - `coreengine-api` (stable contracts: Camera, Entity, Renderer, HudLayer, Input).
    - `coreengine-runtime` (deterministic loop, ECS-lite, scene stack, InputManager).
    - `coreengine-render-canvas` (CanvasRenderer, HUD overlay).
    - `coreengine-render-gl` (GLSurfaceRenderer, OpenGL ES 2.0 pipeline skeleton).
    - `coreengine-integration` (HostBridge, UiOverlay).
    - `coreengine-android-host` (AndroidHostBridge, HudManager, FontManager).
    - `samples` (DemoApp with SimpleScene).
    - `ide` (CoreStudio plugin skeleton: Run, Stop, HotReload).
- Deterministic frame loop (`tickFrame`) with VSync alignment.
- Basic scene system and scene stack (`SceneManager`).
- Example `DemoScene` rendering a sprite with Canvas backend.
- HUD with basic metrics (FPS, draw calls, frame time).
- Initial documentation (`README.md`, `MANIFESTO.md`, `PROMPT_MAESTRO.md`).
- Governance documents (`LICENSE`, `NOTICE`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`).

### Notes
- This is an **alpha release** for architecture validation.
- Roadmap 2025–2027 defined (Canvas → OpenGL → CoreStudio → Vulkan).
- All code under **Apache 2.0** with additional non-patentability clause honoring Roberto Ariel Nicolini.
