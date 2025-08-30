# CoreEngine Validation Checklist

This checklist must be applied to **every new module, class, or document** before merge.  
It complements [PROMPT_MAESTRO.md](./PROMPT_MAESTRO.md) and ensures continuous validation.

---

## 1. Alignment to Prompt Maestro
- [ ] Module or class follows the modular architecture (`coreengine-*`, `ide`, `samples`).
- [ ] ABI/API compatibility preserved (SemVer 1.x).
- [ ] Contracts validated with `:coreengine-api:apiCheck`.

## 2. Fidelity to TCD (Teoría del TODO)
- [ ] Symbolic mapping respected: α (time), Φ (space/camera), Ω− (dynamics), M (render), Ψ (input), ω (metrics).
- [ ] Correct formulas applied (κ, dt/dx, F_TCD, Φ).
- [ ] `StructureState` updated if structural coherence is affected.

## 3. Legacy of Roberto Ariel Nicolini
- [ ] Author cited in documentation/credits.
- [ ] Non-patentable condition respected.
- [ ] Usage limited to open educational and research purposes.
- [ ] If project touches *Núcleo Universal* or *LRX*, clarified as **simulation only**.

## 4. Symbolic Scenes
- [ ] Each TCD symbol has at least one active demo scene.
- [ ] Scenes visually represent formulas coherently.
- [ ] Metrics (ω) visible in HUD or CoreStudio Profiler.

## 5. CoreEngine / CoreStudio
- [ ] Engine maintains deterministic `tickFrame`.
- [ ] IDE (CoreStudio) reflects SceneGraph, properties, metrics.
- [ ] Hot reload works for scenes and assets.

## 6. Documentation & Governance
- [ ] README, CHANGELOG, ARCHITECTURE updated when relevant.
- [ ] LICENSE and NOTICE visible in repository root.
- [ ] Roadmap (2025–2027) intact and aligned.
- [ ] Governance files present: PROMPT_MAESTRO.md, CHECKLIST_VALIDACION.md, ARCHITECTURE.md, CHANGELOG.md.

## 7. Testing & Quality
- [ ] Unit tests added for new logic.
- [ ] Integration tests cover rendering and input.
- [ ] Golden images updated for visual regressions.
- [ ] Benchmarks updated (FPS, draw calls, memory).
- [ ] Risk register updated if applicable.

---

✅ **This checklist must be reviewed and ticked before merging any change.**
