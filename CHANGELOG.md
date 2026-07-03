# Changelog

All notable changes to the **Primordial Ascent** mod are documented in this file.

---

## [0.2.2] — 2026-06-24

### Fixed
- **RaceData Crash**: Fixed crash during world load when `RaceData.createStage()` tried to read config values before they were loaded. Now uses hardcoded `RaceRegistry.EP_THRESHOLDS` values.
- **Kill Counter Sync**: Fixed Divine Nexus kill counter sync showing hardcoded values instead of actual config values.

### Changed
- **Mod Renamed**: Mod renamed from "Prime Godling" to "Primordial Ascent" across all user-facing files. Race name "Prime Godling" (Stage 2) preserved.
- **Version Bump**: Updated to `0.2.2`.
- **Rimuru Tempest Removed**: Removed Rimuru Tempest from default Nexus Core drops and Divine Nexus kill requirements (not part of base Tensura mod).

### Added
- **Divine Nexus Awakening Config**: All awakening requirements now configurable via `skills.toml`:
  - `ep_required` (default: 1,000,000)
  - `cores_required` (default: 1,000)
  - `demon_lord_kills_required` (default: 3)
  - `hostile_mob_kills_required` (default: 50,000)
  - `require_hinata_kill` (default: true)
  - `boss_mobs` (configurable entity list for Path B)
- **Boss Mob Kill Tracking**: Generic kill tracking via configurable `boss_mobs` list.

---

## [0.2.3] — 2026-06-24

### Added
- **Divine Devour Config Options**: New config keys in `skills.toml` under `[divine_devour]`:
  - `allow_unique_skills` (default: false) — Allow copying UNIQUE skills
  - `allow_ultimate_skills` (default: false) — Allow copying ULTIMATE skills
  - `skill_blacklist` (default: empty) — List of skills that cannot be copied
- **Divine Devour Success Chance**: Configurable success rate for skill stealing (default: 10%).

### Changed
- **README Updated**: Added footnote for default Kill Requirements in Divine Nexus Awakening section.
- **Divine Devour Description**: Simplified lang file description.

---

## [0.2.4] — 2026-06-24

### Added
- **All Skill Config Options**: Every skill now has configurable values in `skills.toml`:
  - **Stellar Ascension**: `attack_bonus`, `health_bonus`, `damage_bonus`, `mastered_damage_bonus`, `energy_cost`
  - **Ecliptic Mastery**: `armor`, `toughness`, `reflect_damage`, `mastered_reflect_damage`, `negate_chance`, `mastered_negate_chance`
  - **Luminarch Blessing**: `glow_range`, `mastered_glow_range`, `heal_range` (new options)
  - **Primordial Fortitude**: `attack_bonus`, `health_bonus`, `armor`, `toughness`, `speed_multiplier`, `damage_reduction`, `mastered_damage_reduction`, `learning_gain`, `mastery_gain`, `chant_speed`
  - **Creation Authority**: `explosion_radius`, `mastered_explosion_radius`, `immunity_duration` (new options)
- **Tiered Divine Devour Success Chances**: Separate success rates for different skill tiers:
  - `success_chance` (default: 0.10) — Common/Intrinsic/Extra skills
  - `unique_success_chance` (default: 0.05) — UNIQUE skills
  - `ultimate_success_chance` (default: 0.01) — ULTIMATE skills

### Changed
- **Skills Config Reorganized**: `skills.toml` now has clear section headers for each skill with descriptive comments.
- **README Updated**: Complete configuration reference with all skill config options documented.
- **Version Bump**: Updated to `0.2.4`.

---

## [0.2.5] — 2026-07-03

### Fixed
- **Ecliptic Mastery Immortal Bug**: Skill is now toggleable. Damage negation and reflection only activate when toggled ON. Previously, the passive always-on behavior caused near-immortality when combined with other defensive skills.
- **Primordial Fortitude Attribute Loss on Rejoin**: Skill modifiers and progression bonuses now re-apply correctly when reconnecting to a server.
- **Config Values Not Affecting Stats**: Stellar Ascension, Ecliptic Mastery, and Primordial Fortitude now read their attribute values from config at runtime instead of using hardcoded constructor values.
- **Dead Config `divineNexusMinEp`**: Now properly checked as an early EP gate in the Divine Nexus awakening ritual.
- **Skill Modifier Persistence on Rejoin**: All toggled skills now re-apply their attribute modifiers when a player logs in.

### Changed
- **Primordial Fortitude Damage Reduction**: Reduced from 90-95% to 55-65% default. Config range expanded to 0-100% so server owners can customize freely.
- **Divine Disruption Visuals**: Professional-grade particle effects and client-side renderer with shockwave rings, ground indicator, spiraling tendrils, and glow orbs. Added Warden sound effects on activation/release.
- **Divine Devour & Disruption Configurable**: Hardcoded constants now exposed as config options:
  - `divine_devour.cooldown_ticks`, `mastered_cooldown_ticks`, `range`
  - `divine_disruption.aoe_radius`, `cost_interval`
- **Skill Textures**: All 9 skill icon textures now properly placed under `assets/primegodling/textures/skill/`.

### Added
- **Rejoin Skill Sync**: Skills automatically re-apply their held attribute modifiers when a player logs in, preventing stat loss on reconnect.

---

## Credits

See [LICENSE.txt](LICENSE.txt) for full attribution and credits.
