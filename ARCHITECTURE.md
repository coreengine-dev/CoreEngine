# CoreEngine Architecture

## Modules
- **coreengine-api**: stable public contracts (`Camera`, `Entity`, `Renderer`, `HudLayer`, `Input`).
- **coreengine-runtime**: deterministic loop, scene stack, ECS-lite, input manager.
- **coreengine-render-canvas**: `CanvasRenderer`, HUD metrics overlay.
- **coreengine-render-gl**: OpenGL ES 2.0 pipeline, `GLSurfaceRenderer`.
- **coreengine-integration**: `HostBridge`, `UiOverlay`, integration layer.
- **coreengine-android-host**: Android-specific bridge (`HudManager`, `FontManager`).
- **samples**: DemoApp with `SimpleScene` and `MainActivity`.
- **ide**: CoreStudio plugin (SceneGraph editor, viewport, inspector, profiler, hot reload).

## Principles
- **Deterministic tickFrame**: `intents → input → update → render → metrics`.
- **Stable ABI/API**: SemVer 1.x enforced by `apiCheck`.
- **Clean modular design**: Core independent of Android runtime.
- **ECS hybrid**: SceneGraph + entity-component updates.
- **TCD mapping**:
    - α (time) → `CoreEngine.tickFrame`
    - Φ (space) → `Camera`
    - Ω− (dynamics) → `Entity`, `Scene`
    - M (render) → `Renderer`
    - Ψ (input) → `InputManager`
    - ω (metrics) → `HudLayer`, `FrameStats`

## Integration Points
- **Android Host**: `CoreSurfaceHost` binds engine lifecycle to Android Activity.
- **CoreStudio IDE**: IntelliJ plugin interacts with engine services for live preview and hot reload.
- **Web**: Planned WebGL demos for 2026, aligned with documentation site.

## Development Workflow
1. Modules are developed independently with Gradle.
2. Public APIs validated with `:coreengine-api:apiCheck`.
3. CI builds run `./gradlew build` and enforce tests.
4. Scenes and demos validated in `samples` before merge.

## Roadmap Reference
See [CHANGELOG.md](./CHANGELOG.md) and project roadmap (2025–2027) for planned milestones.
