# Changelog

All notable changes to the **Prime Godling** mod are documented in this file.

---

## [0.2.1-BugFix] — 2026-06-10

### Fixed
- **Divine Devour Cooldown**: Reduced cooldown from 200/100 ticks (10s/5s) to 10/5 ticks (0.5s/0.25s) unmastered/mastered.
- **Divine Devour Skill Filter**: Now only steals Intrinsic, Common, and Extra skills from targets. Unique and Ultimate skills are excluded from the steal pool.
- **Divine Devour Description**: Updated lang file and README to accurately reflect the cooldown values and skill type restrictions.
- **Advancement Filename**: Renamed `primeordial_supreme_god.json` to `primordial_supreme_god.json` (typo fix that prevented Stage 6 advancement from granting).
- **Client Sync on Login**: Added `PlayerLoggedInEvent` handler to sync `awakened_nexus` state, nexus core counts, and kill counters to client on join.
- **Kill Counter Sync**: Added `SyncKillCountersPayload` and `ClientKillCountersCache` for client-side kill progress display.
- **Race Config Evolution Thresholds**: Wired config values to actual `RaceRegistry.EP_THRESHOLDS[]` array.
- **onRaceSet Re-login Overwrite**: Stage 2/3 random resistances and skills now check for existing stored values before granting new ones.
- **Divine Nexus Min Skills Check**: `NexusAwakening.startRitual()` now checks `SkillConfig.COMMON.divineNexusMinSkills` requirement.
- **Cosmic Awareness Config**: Now reads `SkillConfig.COMMON.cosmicAwarenessRange` instead of hardcoded value.

### Changed
- **Version Bump**: Updated from `0.2.0` to `0.2.1-BugFix`.
- **README Rewrite**: Complete rewrite with comprehensive player guide covering installation, evolution chain, skills, flight, nexus cores, awakening, and configuration.
- **License**: Replaced NeoForged template license with custom Prime Godling license including attribution to Tensura, ManasCore, GeckoLib, TR Addon, Ragnarok Races, Mysticism, and NeoForge.
- **CurseForge Link**: Updated download link from GitHub Releases to CurseForge files page.

---

## [0.2.0] — 2026-06-01

### Added
- **7-Stage Evolution Chain**: Complete overhaul from 5 stages to 7 (Half Godling → Demi Godling → Prime Godling → Celestial Godling → Ecliptic Godling → New God → Primordial Supreme God).
- **Nexus Core Evolution System**: Each evolution now requires consuming Nexus Cores in addition to EP thresholds.
- **Divine Nexus Awakening**: Third awakening path alongside True Demon Lord and True Hero, requiring New God race, 1M EP, 1000 Nexus Cores, and kill requirements.
- **Nexus Core Item**: Right-click consumable, stacks to 64, triggers ritual when all requirements met.
- **Nexus Core Crafting**: Shaped recipe (Echo Shard + Diamond + Nether Star), disabled by default in server config.
- **Nexus Core Mob Drops**: Configurable per-mob drop table with chance and amount ranges.
- **Nexus Core Requirement**: Custom `NexusCoreRequirement` evolution requirement class.
- **Creation Authority Ultimate Skill**: Lightning barrage + explosion with beam projectile, passive stat bonuses, configurable cooldowns.
- **Creation Beam Projectile**: Custom entity for Creation Authority skill visual effect.
- **Divine Devour Unique Skill**: 10% chance to steal target's skills, configurable cooldowns.
- **Primordial Fortitude Skill**: 90-95% damage reduction, Fire Resistance, Water Breathing, fall damage immunity.
- **Luminarch Blessing Toggleable Skill**: Regeneration, Absorption, entity highlighting through walls.
- **Ecliptic Mastery Skill**: Passive armor/toughness, damage reflection, damage negation chance.
- **Stellar Ascension Skill**: Attack/HP boost toggle, bonus magic damage.
- **Cosmic Awareness Skill**: Mob detection, presence sense, entity marking.
- **Primordial Bloom Unique Skill**: Passive magicule regen, blooming particles.
- **Resistance Helper**: 22 resistance↔nullification pairs for random skill grants.
- **Random Skill Grants**: Stage 2 (3 resistances + 3 skills), Stage 3 (nullifications + 2 skills), Stage 6 (all nullifications + Creation Authority + 1 random Unique).
- **Creative Flight**: R-key toggle, magicule cost system, subordinate discount.
- **Flight Cost System**: Activation and maintenance costs with subordinate proximity discount.
- **EP Gain Multiplier**: Configurable multiplier (default 0.5x) for balanced progression.
- **Attribute Modifiers**: Per-stage health, damage, speed, armor, toughness, dodge negate, resistance degradation bonuses.
- **Client-Server Network Sync**: `SyncAwakenedPayload`, `SyncNexusCoresPayload`, `SyncKillCountersPayload`.
- **Client Caches**: `ClientAwakenedCache`, `ClientNexusCoresCache`, `ClientKillCountersCache`.
- **Halo Rendering**: `HaloMeshRenderer` and `PrimordialHaloModel` for Primordial Supreme God visual effect.
- **Race Model Registry**: GeckoLib integration for custom race models.
- **Kill Tracking**: Demon Lord kills, Rimuru kills, Hinata kills, hostile mob kills with chat notifications.
- **Advancement Tree**: 9 advancements tracking evolution progress and Divine Nexus awakening.
- **Loot Table**: Divine Nexus advancement reward function.
- **Nexus Core Recipe**: Shaped crafting recipe with configurable toggle.
- **FTB Quests Integration**: Reflection-based compatibility with FTB Quests mod.
- **Config Files**: `races.toml`, `skills.toml`, `server.toml`, `nexus-drops.toml` in `config/primegodling/`.
- **Config Version Tracking**: `_meta.config_version` with outdated config warning.
- **Pack Metadata**: `pack.mcmeta` for proper asset loading.

### Changed
- **Aura Rebalance**: Stage 0 starting aura randomized 200–3000 (was fixed ~1.5M).
- **EP Progress Formula**: Changed from `(currentEP - entryEP) / (required - entryEP)` to `currentEP / required`.
- **EntryEP Syncing**: Moved from `player.getPersistentData()` to `ManasRaceInstance.getOrCreateTag()` for client sync.
- **Flight Cost Deduction**: Uses `isOutOfMagiculeConsuming()` for pure magicule deduction.
- **Non-Intrinsic Skill Learning**: Creation Authority and FTB rewards learned in `onRaceSet()`.
- **Current Magicule Refill**: `gainMagicule()` runs on every `onRaceSet()` call.
- **Extra Modifier Consistency**: Changed `addTransientModifier()` to `addPermanentModifier()`.
- **LuminarchBlessing canTick**: Returns `instance.isToggled()` instead of `true`.
- **NexusDropsConfig Format**: Changed from `"modid:entity_id"` to `"modid:entity_id;chance;minAmount;maxAmount"`.
- **ConfigInjector**: Added `divine_devour` to `ALL_SKILLS` for config injection.

### Removed
- **Dead Code**: `SkillData.java`, stale duplicate `PrimordialBloomSkill.java`, unused constants.
- **Stale Default Config**: `data/primegodling/defaultconfigs/primegodling-common.toml`.
- **Hardcoded NEXUS_CORES_REQUIRED**: Removed from `RaceRegistry.java`.
- **Duplicate Imports**: Cleaned up across multiple files.

---

## [0.1.9] — 2026-05-25

### Added
- **Detailed Injection Logging**: Added logging for race/skill injection timing.
- **Init Phase Retry**: Retry injection at init phase if initial attempt fails.

### Changed
- **Halo Rendering**: Fixed halo stutter and glow issues.
- **Nexus Core Tracking**: Improved tracking and sync.
- **Race Selection Injection**: Fixed timing issues with race selection.

---

## [0.1.6] — 2026-05-21

### Fixed
- **Zero Damage After Evolution (Critical)**: `StellarAscensionSkill.onDamageEntity()` called `target.hurt()` reentrantly, causing recursive damage pipeline corruption. Fixed by adding bonus damage directly to `Changeable<Float> damage` parameter.
- **Mobs Not Attacking Player**: Root cause was same reentrant `target.hurt()` corrupting entity state.

### Changed
- Removed reentrant `target.hurt(magic, bonus)` from `StellarAscensionSkill`.

---

## [0.1.5] — 2026-05-20

### Added
- **Creative Flight**: Prime Godling races now have `HAS_CREATIVE_FLIGHT` tag.
- **Flight Cost System**: R key toggles flight; activation costs 40 magicules (20 with subordinates); maintenance costs 10 magicules every 10 ticks (2 every 100 ticks with subordinates).
- **Damage Bonus System**: Per-stage `AttributeModifier` lists for flat and multiplicative attack damage bonuses.
- **ServerConfig**: `nexus_cores_required` and `crafting_enabled` toggle.
- **NexusDropsConfig**: `drops_enabled` toggle + `mob_drops` string list.
- **Mob Drop System**: `LivingDropsEvent` handler with null-safe environmental kill support.
- **Config Folder Restructure**: All configs moved to `config/primegodling/`.
- **Config Version Tracking**: `_meta.config_version = 2` with outdated warning.
- **SkillConfig Fields**: `creationAuthorityMasteredCooldown` added.
- **Tensura Damage Pipeline Fix**: Added `PHYSICAL_RESIST_DEGRADATION`, `RESISTANCE_DEGRADATION`, and `DODGE_NEGATE_CHANCE` modifiers.
- **Client Sync on Removal**: `removeAttributeModifiers()` sends `ClientboundUpdateAttributesPacket`.

### Changed
- **Damage Bonus Refactor**: Replaced ad-hoc `LivingHurtEvent` with `AttributeModifier`-based system.
- **Aura Rebalance**: Stage 0 starting aura 200–3000; evolved stages set to half EP threshold.
- **DivineDevour Cooldowns**: Increased from 10/3 ticks to 200/100 ticks.
- **CreationAuthority Cooldowns**: Changed to config-driven values.
- **PrimordialFortitude Rename**: Renamed from `PrimordialOmnipotenceSkill`.

### Removed
- **Dead Code**: `SkillData.java`, unused constants, stale default config.

---

## [0.1.4] — 2026-05-18

### Added
- **Server Config**: `nexus_cores_required` and `crafting_enabled` toggle.
- **Nexus Drops Config**: Mob drop table with chance and amount ranges.
- **Config Version Tracking**: Config version validation on load.

### Changed
- **NexusCoreRequirement**: Now reads from server config.
- **NexusDropsConfig Format**: Updated to `"modid:entity_id;chance;minAmount;maxAmount"`.

---

## [0.1.3] — 2026-05-17

### Added
- **Primordial Supreme God Halo Layer**: Visual halo effect for final evolution.
- **Halo Rendering**: GeckoLib-based halo model and renderer.

### Changed
- **Server Crash Fix**: Guarded client-only init with `Dist.CLIENT` check.

---

## [0.1.2] — 2026-05-16

### Added
- **Creation Authority Refactor**: True Ultimate Skill with beam projectile, magic circle, and explosion effects.
- **Client Proxy**: Separated client-only code from server logic.
- **Race Model Registry**: GeckoLib integration for custom race models.

### Changed
- **Skill System**: Refactored from basic implementation to proper ManasCore skill API integration.

---

## [0.1.1] — 2026-05-15

### Added
- **Initial Race System**: 5-stage evolution chain with EP thresholds.
- **Custom Skills**: Primordial Bloom, Cosmic Awareness, Stellar Ascension, Ecliptic Mastery, Luminarch Blessing, Primordial Fortitude.
- **Config System**: Race and skill configuration files.
- **Integration with Tensura**: Race and skill injection into Tensura config.
- **Basic Advancement Tree**: Evolution tracking advancements.

### Changed
- **Template Port**: Initial port from NeoForge MDK to Prime Godling mod.

---

## [0.1.0] — 2026-05-14

### Added
- **Project Initialization**: Cloned NeoForge MDK 1.21.1 template.
- **Build Configuration**: Set up Gradle with ManasCore, GeckoLib, and Tensura dependencies.
- **Mod Entry Point**: `PrimeGodling.java` with `@Mod` annotation.
- **Package Structure**: `com.primegodling.primegodling` with `common`, `client`, `server`, and `network` packages.

---

## Version History Summary

| Version | Date | Key Milestone |
|---------|------|---------------|
| 0.1.0 | 2026-05-14 | Project initialization |
| 0.1.1 | 2026-05-15 | Initial 5-stage race system |
| 0.1.2 | 2026-05-16 | Creation Authority refactor |
| 0.1.3 | 2026-05-17 | Halo rendering system |
| 0.1.4 | 2026-05-18 | Server config and mob drops |
| 0.1.5 | 2026-05-20 | Creative flight and damage system |
| 0.1.6 | 2026-05-21 | Critical damage pipeline fix |
| 0.1.9 | 2026-05-25 | Injection logging and timing fixes |
| 0.2.0 | 2026-06-01 | Complete 7-stage overhaul with Divine Nexus |
| 0.2.1-BugFix | 2026-06-10 | Divine Devour balance and bug fixes |

---

## Credits

See [LICENSE.txt](LICENSE.txt) for full attribution and credits to Tensura: Reincarnated, ManasCore, GeckoLib, TR Addon, Ragnarok Races, Mysticism, and NeoForge.
