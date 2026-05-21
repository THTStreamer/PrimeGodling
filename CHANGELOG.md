# Changelog

## [0.1.5 → 0.1.6] — 2026-05-21

### Fixed
- **Zero Damage After Evolution (Critical)**: `StellarAscensionSkill.onDamageEntity()` called `target.hurt()` reentrantly inside the damage-processing callback, causing a recursive `hurt()` call that corrupted the damage pipeline and nullified the player's physical damage output. Fixed by adding the bonus damage directly to the `Changeable<Float> damage` parameter instead.
- **Mobs Not Attacking Player**: Root cause was the same reentrant `target.hurt()` corrupting entity state during damage processing. Mob AI should now function normally.

### Changed
- Removed reentrant `target.hurt(magic, bonus)` from `StellarAscensionSkill` — bonus is now added directly to the damage value (`damage.set(damage.get() + bonus)`).

## [0.1.4 → 0.1.5] — 2026-05-20

### Added
- **Creative Flight**: Prime Godling races now have `HAS_CREATIVE_FLIGHT` tag, granting creative flight.
- **Flight Cost System**: R key toggles flight (via `RaceEvents.ACTIVATE_ABILITY`); activation costs 40 magicules (20 with named subordinates nearby); maintenance costs 10 magicules every 10 ticks (2 every 100 ticks with subordinates); auto-disables when magicules are insufficient.
- **Damage Bonus System**: `PrimeGodlingRace` stores per-stage `AttributeModifier` lists; overrides `addAttributeModifiers`/`removeAttributeModifiers` to apply both flat (`ADD_VALUE`) and multiplicative (`MULTIPLY_TOTAL`) `ATTACK_DAMAGE` bonuses on race set.
- **ServerConfig**: `nexus_cores_required` (1–100, default 20) and `crafting_enabled` toggle for Nexus Core recipe.
- **NexusDropsConfig**: `drops_enabled` toggle + `mob_drops` string list in `"modid:entity_id;chance"` format; defaults include Tensura mobs (Harpy Queen, Arachne, Orc Lord, etc.).
- **Mob Drop System**: `LivingDropsEvent` handler checks enabled state, matches killed entity ID against config list, rolls chance, and spawns Nexus Core. Null-safe for environmental kills.
- **Config Folder Restructure**: All config files moved to `config/primegodling/` subdirectory (`races.toml`, `skills.toml`, `server.toml`, `nexus-drops.toml`).
- **Config Version Tracking**: `RaceConfig` includes `_meta.config_version = 2`; `ModConfigEvent.Loading` handler warns if config version is outdated.
- **NexusCoreRequirement**: Now reads from `ServerConfig.COMMON.nexusCoresRequired.get()` instead of hardcoded `RaceRegistry.NEXUS_CORES_REQUIRED`.
- **`pack.mcmeta`**: Added to `src/main/resources/` — mod assets now load correctly.
- **SkillConfig Fields**: `creationAuthorityMasteredCooldown` (default 60 ticks) added alongside existing cooldown fields.
- **Tensura Damage Pipeline Fix**: Added `PHYSICAL_RESIST_DEGRADATION` (1.0), `RESISTANCE_DEGRADATION` (1.0 at stage 2+), and `DODGE_NEGATE_CHANCE` (100) attribute modifiers to all Prime Godling stages. This bypasses Tensura's `getPhysicalAttackInputMultiplier()` which otherwise reduces physical damage to 1% (0.01x) for players lacking specific degradation modifier IDs like `DIVINE_KI` or `HAKI_COAT`.
- **Client Sync on Removal**: `PrimeGodlingRace.removeAttributeModifiers()` now sends a `ClientboundUpdateAttributesPacket` after removing extra modifiers, ensuring the client reflects the correct attribute state.

### Changed
- **Damage Bonus Refactor**: Replaced ad-hoc `LivingHurtEvent` handler with proper `AttributeModifier`-based system using `MULTIPLY_TOTAL` operation on `Attributes.ATTACK_DAMAGE`.
- **EP Progress Formula**: `ScaledEPRequirement` progress changed from `(currentEP - entryEP) / (required - entryEP)` to `currentEP / required`, starting at 20% immediately after evolution (matching standard Tensura `EPRequirement` behaviour).
- **EntryEP Syncing**: Moved entryEP storage from `player.getPersistentData()` (server-only) to `ManasRaceInstance.getOrCreateTag()` (synced to client via race network sync), fixing `0/0` display on evolution screen.
- **Flight Cost Deduction**: Replaced `isOutOfEnergy(entity, minMagicule, percentage)` — which checks aura — with `isOutOfMagiculeConsuming(entity, cost, 0.0)` for pure magicule deduction.
- **Aura Rebalance**: Stage 0 starting aura randomized 200–3000 (was fixed ~1.5M); each evolved stage aura set to half its EP threshold (125K / 500K / 2.5M / 15M).
- **Non-Intrinsic Skill Learning**: Creation Authority (Ultimate) and FTB rewards now learned in `onRaceSet()`, not `triggerEvolutionRewards()` (which is never called by the Tensura evolution system). `triggerEvolutionRewards()` set to no-op.
- **Max-Magicules-10 Bug Fix**: `resetExistenceData(entity)` called in `onRaceSet()` for all stages to set proper `MAX_MAGICULE` base values; `awakenNexus()` moved into the stage-4 block (after `resetExistenceData` sets base to 10M); second `gainMagicule()` added after `awakenNexus()` to refill current magicules to awakened max.
- **Current Magicule Refill**: `EnergyHelper.gainMagicule(entity, getMaxMagicule(entity), GainType.NORMAL)` runs on every `onRaceSet()` call (removed `nextEvolution == null` guard), filling current magicules to max on initial set and all evolutions.
- **Flight Cache Fix**: Stale `currentMagicule` cached variable removed — all checks now read `getCurrentMagicule()` fresh to prevent false flight-disable after mid-tick regen.
- **DivineDevour Cooldowns**: Increased from 10/3 ticks (0.5s/0.15s) to 200 ticks normal / 100 ticks mastered (10s/5s).
- **CreationAuthority Cooldowns**: Changed from hardcoded 200/60 ticks to config-driven `creationAuthorityCooldown` (default 200) / `creationAuthorityMasteredCooldown` (default 60).
- **CreationAuthority Energy Deduction**: Removed redundant `gainMagicule(entity, -energyCost, GainType.NORMAL)` — `isOutOfEnergy()` already deducts the cost.
- **Extra Modifier Consistency**: Changed `addTransientModifier()` to `addPermanentModifier()` in `PrimeGodlingRace.addAttributeModifiers()` to match the base ManasRace system.
- **LuminarchBlessing `canTick`**: Now returns `instance.isToggled()` instead of `true`, preventing unnecessary ticks when toggled off.
- **PrimordialFortitude Rename**: Renamed `PrimordialOmnipotenceSkill` → `PrimordialFortitudeSkill` (ID `primordial_fortitude`), with modifier IDs updated from `omni_*` to `fort_*`.
- **NexusCoreItem**: Now uses config value for required cores count.
- **ConfigInjector**: `divine_devour` added to `ALL_SKILLS` for config injection.
- **NexusDropsConfig Entries**: Changed from `"modid:entity_id"` format to `"modid:entity_id;chance"` format with validation on config load.

### Removed
- **Dead Code**: `SkillData.java` (`registerAll()` never called) and stale duplicate `PrimordialBloomSkill.java` in `common/data/` deleted.
- **Unused Constants**: `PRIMORDIAL_BLOOM_TTL` and `NEXUS_AWAKENED` removed from `SkillRegistry.java`.
- **Stale Default Config**: `data/primegodling/defaultconfigs/primegodling-common.toml` removed (used old format/values, never read).
- **Hardcoded `NEXUS_CORES_REQUIRED`**: Removed from `RaceRegistry.java` in favour of config-driven value.
- **Duplicate Imports**: Cleaned up duplicate and unused imports across multiple files.
- **Old Build Artifact**: Previous `0.1.4` jar deleted and rebuilt.
