# Changelog

All notable changes to the **Prime Godling** mod are documented in this file.

---

## [0.2.1-BugFix] — 2026-06-10

### Fixed
- Divine Devour cooldown reduced to 10 ticks (0.5s) unmastered / 5 ticks (0.25s) mastered.
- Divine Devour can no longer steal Unique or Ultimate skills from targets.
- Fixed advancement filename typo that prevented Stage 6 advancement from granting.
- Fixed client sync on login for awakened state, nexus cores, and kill counters.
- Fixed random resistances/skills being overwritten on re-login.
- Added missing Divine Nexus minimum skills check.

### Changed
- Rewrote README with comprehensive player guide.
- Added proper license with full attribution credits.
- Updated download link to CurseForge.

---

## [0.2.0] — 2026-06-01

### Added
- Expanded from 5 stages to 7-stage evolution chain.
- Divine Nexus awakening path (third option alongside True Demon Lord and True Hero).
- Nexus Core system — craftable currency required for each evolution.
- Creation Authority ultimate skill with beam projectile and explosion.
- Divine Devour unique skill — steal skills from other entities.
- Primordial Fortitude, Luminarch Blessing, Ecliptic Mastery, Stellar Ascension, Cosmic Awareness, and Primordial Bloom skills.
- Creative flight with magicule cost system.
- Configurable mob drop table for Nexus Cores.
- Kill tracking for Divine Nexus awakening requirements.
- FTB Quests integration.
- Full advancement tree tracking evolution progress.

### Changed
- Rebalanced aura values across all stages.
- Improved EP progression formula.
- Removed all nullification resistances for balanced gameplay.

---

## [0.1.9] — 2026-05-25

### Fixed
- Halo rendering stutter and glow issues.
- Race selection injection timing.

### Changed
- Improved injection logging and retry logic.

---

## [0.1.6] — 2026-05-21

### Fixed
- Critical damage bug — player dealt zero damage after evolution.
- Mobs no longer attack the player due to corrupted entity state.

---

## [0.1.5] — 2026-05-20

### Added
- Creative flight for all Godling races with magicule cost system.
- Configurable mob drops for Nexus Cores.
- Server config for crafting and nexus core requirements.
- Tensura damage pipeline compatibility fixes.

### Changed
- Rebalanced aura values and damage modifiers.
- Moved configs to `config/primegodling/` directory.
