# Testing Strategy

## Test Pyramid
- **Unit tests**: pure logic, math functions, ECS systems.
- **Integration tests**: render loop, input → update → render.
- **UI/Host tests**: Android Host integration, overlays, CoreStudio plugin.

## Golden Images
- Reference screenshots for visual validation (HUD, TCD scenes).

## Fuzz Inputs
- Generate random `InputEvent` sequences.
- Vary `dt` in `tickFrame` to simulate unstable frame timings.

## Numerical Tolerances
- FPS ±5%
- DrawCalls ±10%
- Frame time deviation < 2 ms/frame
