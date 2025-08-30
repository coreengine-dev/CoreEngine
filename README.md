# CoreEngine

[![Build](https://github.com/coreengine-dev/CoreEngine/actions/workflows/build.yml/badge.svg)](https://github.com/coreengine-dev/CoreEngine/actions/workflows/build.yml)
[![CI](https://github.com/coreengine-dev/CoreEngine/actions/workflows/ci.yml/badge.svg)](https://github.com/coreengine-dev/CoreEngine/actions/workflows/ci.yml)

**CoreEngine** is a **modular, deterministic 2D/3D graphics engine** built in **Kotlin**, designed for **Android-first integration** but architected for multiplatform scalability.  
It combines a **hybrid ECS + SceneGraph architecture** with pluggable **Canvas / OpenGL ES backends**, and aligns with the **Theory of Everything (TCD)** by **Roberto Ariel Nicolini** as well as modern **Android architecture guidelines**.

---

## ✨ Key Features
- **Android-first**: Seamless integration with Jetpack Compose, ViewModels, and StateFlow.
- **TCD Fidelity**: Symbolic structural flow `𝛂 → 𝜱 → 𝝮 → 𝐌 → 𝚿 → 𝝎` mapped to engine subsystems:
    - α (time / loop), Φ (space / camera), Ω− (dynamics), M (render), Ψ (input), ω (metrics).
- **Deterministic Loop**: VSync-aligned, predictable frame processing.
- **Clean Modular Architecture**: Stable ABI/API under `org.coreengine.*` (SemVer 1.x).
- **Rendering Backends**: `CanvasRenderer` and `GLSurfaceRenderer` with Surface support.
- **Resource Management**: Modular, scene-scoped, reference-counted.
- **HUD System**: Hybrid overlays (Android Views + engine HUD layers).
- **CoreStudio IDE**: IntelliJ-based plugin for SceneGraph editing, Inspector, Viewport, Profiler, and Hot Reload.
- **Future-proof**: Prepared for Vulkan (2027+), WebGPU export, and QuantumFields plugin.
- **Open Source**: Apache 2.0 License with explicit recognition of Roberto Ariel Nicolini’s legacy.

---

## 📅 Roadmap (2025–2027)

| Phase | Goal | Status |
|-------|------|--------|
| **1** | Stable core runtime, scene stack, deterministic loop | ✅ Completed |
| **2** | Canvas renderer + modular resource manager | ✅ Completed |
| **3** | Advanced OpenGL pipeline, ECS-lite integration | 🚧 In progress |
| **4** | CoreStudio IDE (SceneGraph editor, viewport, hot reload) | ⏳ Pending |
| **5** | Profiler, multi-tasking, advanced HUD metrics | ⏳ Pending |
| **6** | Documentation + WebGL demos | ⏳ Pending |
| **7+** | QuantumFields module + Vulkan transition | 🎯 Target: 2027 |

---

## 📖 Documentation
- **Architecture**: see [ARCHITECTURE.md](./ARCHITECTURE.md)
- **Testing Strategy**: see [TESTING.md](./TESTING.md)
- **Benchmarks & Metrics**: see [BENCHMARKS.md](./BENCHMARKS.md)
- **Validation Checklist**: aligned with [Prompt Maestro](./PROMPT_MAESTRO.md)

Official website (under construction): **[coreengine.dev](https://coreengine.dev)**

---

## ⚖️ License
This project is licensed under the **Apache 2.0 License**, with an additional non-patentability clause honoring **Roberto Ariel Nicolini**.  
See [LICENSE](./LICENSE) and [NOTICE](./NOTICE) for details.

---

## 🤝 Contributing
CoreEngine is **open source and community-driven**:
- Report issues via **GitHub Issues**.
- Submit **Pull Requests** following [CONTRIBUTING.md](./CONTRIBUTING.md).
- Respect community rules in [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md).
- Join the development of **CoreStudio** and the validation of **TCD scenes**.

---

## 🚀 Vision 2027
On **June 3rd, 2027**, CoreEngine will reach **v1.0** with:
- A stable, optimized runtime and GL/Vulkan backends.
- **CoreStudio IDE** with Scene Viewer, Inspector, Profiler, and Hot Reload.
- Complete documentation, benchmarks, and WebGL demos.
- Integration of **QuantumFields** for advanced physics simulation.

---

🔗 Repository: [github.com/coreengine-dev/CoreEngine](https://github.com/coreengine-dev/CoreEngine)
