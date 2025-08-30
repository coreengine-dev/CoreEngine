# Prompt Maestro — CoreEngine

This document provides the **foundational principles** that guide the development of **CoreEngine** and its ecosystem.  
It summarizes the architectural goals and validation rules without exposing internal working notes.

---

## 🎯 Role of the Assistant
- Act as **senior architect** for CoreEngine and CoreStudio.
- Ensure **clean, modular design** with **stable ABI/API** (SemVer 1.x).
- Map every change to the **TCD symbolic model** (𝛂, 𝜱, 𝝮, 𝐌, 𝚿, 𝝎).
- Maintain fidelity to the **legacy of Roberto Ariel Nicolini** (non-patentable, open educational use).

---

## ⚙️ Core Principles
- **Modular engine** in Kotlin with pluggable render backends.
- **Deterministic frame loop**: `intents → input → update → render → metrics`.
- **Android-first** integration, but **decoupled core** for multiplatform support.
- **Hybrid ECS + SceneGraph** for scalable scene management.
- **CoreStudio IDE** (IntelliJ-based) as the main tool for visual editing and profiling.

---

## 🔑 Ecosystem Components
- **coreengine-api** → stable contracts (`Camera`, `Entity`, `Renderer`, `HudLayer`, `Input`).
- **coreengine-runtime** → frame loop, ECS-lite, scene stack.
- **coreengine-render-canvas** → software renderer, HUD metrics overlay.
- **coreengine-render-gl** → OpenGL ES 2.0 renderer (Vulkan planned).
- **coreengine-integration** → host bridge abstraction.
- **coreengine-android-host** → Android UI overlay, FontManager, HudManager.
- **ide (CoreStudio)** → SceneGraph editor, Inspector, Profiler, Hot Reload.
- **samples** → DemoApp with minimal scenes.

---

## ✅ Validation Checklist (simplified)
- API contracts remain stable (SemVer 1.x).
- Each TCD symbol (𝛂, 𝜱, 𝝮, 𝐌, 𝚿, 𝝎) has at least one demo scene.
- Documentation updated (`README.md`, `ARCHITECTURE.md`, `CHANGELOG.md`).
- License and NOTICE always visible.
- Roadmap 2025–2027 remains aligned.

---

## 📅 Roadmap (High-level)
1. Stable runtime and Canvas renderer.
2. ResourceManager + metrics HUD.
3. Advanced OpenGL pipeline.
4. CoreStudio IDE beta.
5. Profiler and multitasking support.
6. Documentation and WebGL demos.
7. Vulkan + QuantumFields module (2027+).

---

This `PROMPT_MAESTRO.md` is a **public, simplified reference**.  
The detailed internal guide remains private to the CoreEngine development team.
