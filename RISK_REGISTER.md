# Risk Register

| Risk                           | Type       | Mitigation                           |
|--------------------------------|------------|--------------------------------------|
| Android coupling in core       | Technical  | Isolate in `coreengine-android-host` |
| ABI/API 1.x breakage           | Technical  | CI validation with `apiCheck`        |
| Unvalidated TCD formulas       | Scientific | Mark as simulation, not hardware     |
| License & Nicolini legacy      | Legal      | Keep non-patentable clause           |
