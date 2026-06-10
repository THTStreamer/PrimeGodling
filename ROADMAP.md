# Prime Godling v0.2.0+ — Development Roadmap

Based on the full codebase audit of v0.2.0.

---

## Priority 0 — Must Fix (broken functionality)

### 1. Stage 6 advancement never grants
The file `primeordial_supreme_god.json` is misspelled (missing the "m"). The code looks up `primegodling:primordial_supreme_god` which doesn't match the filename. Rename the file to `primordial_supreme_god.json`.

### 2. Divine Devour lang description is wrong
The lang file says "10 ticks cooldown, 3 when mastered" but the code has `COOLDOWN_NORMAL = 200` (10 seconds) and `COOLDOWN_MASTERED = 100` (5 seconds). Fix the description to match the actual values.

---

## Priority 1 — Important Fixes (incorrect behavior)

### 3. CosmicAwarenessSkill ignores config
`PRESENCE_RANGE` is hardcoded to `200.0` while `SkillConfig.COMMON.cosmicAwarenessRange` (default 32) exists but is never read. Either use the config value or remove the dead config.

### 4. No sync on player login
When a player joins, their `awakened_nexus` state and nexus core counts are NOT synced to the client. The client cache defaults to `false`/`0`. Add a join handler that sends sync packets.

### 5. PrimordialBloom regen multiplier is extreme
`regenPerCall = maxMagicule * regenPercent / 100.0 * 5.0` runs every tick. At default config (3%), this is 15% of max magicule per tick = 3000%/second. Either reduce the multiplier or document it as intentional.

### 6. RaceConfig evolution thresholds are dead config
5 evolution EP config values are defined but never used. Actual thresholds come from `RaceRegistry.EP_THRESHOLDS[]`. Either wire the config to the thresholds or remove the dead config values.

### 7. onRaceSet overwrites random rewards on re-login
Stage 2/3 players get new random resistances/skills every time their race is re-applied on login. The existing stored values should be checked first.

### 8. SkillConfig.divineNexusMinSkills is never checked
The config value `divineNexusMinSkills` (default 5) exists but `NexusAwakening.startRitual()` never checks it. Either add the check or remove the config.

---

## Priority 2 — Quality of Life (polish & cleanup)

### 9. Add missing Primordial Fortitude skill texture
No `primordial_fortitude.png` exists. The skill shows up in-game without an icon.

### 10. Memory leaks on long servers
`FLIGHT_DATA` and `LAST_EP` HashMaps in `PrimeGodling.java` are never cleaned on player logout. Add a `PlayerEvent.LoggedOut` handler to remove entries.

### 11. Remove dead code
- `ScaledEPRequirement.java` — fully implemented but never used
- `NexusDropsConfig.MobDropEntry` record — defined but never instantiated
- `RaceModelRegistry.PRIME_GODLING_LAYER` — defined but never used
- `ServerProxy` — empty stub, never called
- Duplicate import in `CreationAuthoritySkill.java`

### 12. Divine Nexus advancement reward is placeholder
`divine_nexus.json` loot table awards a single `dragon_breath`. The reward function (`divine_nexus_unlock.mcfunction`) only gives `glowing 10`. Consider a more meaningful reward for the final awakening.

---

## Priority 3 — Future Features (nice to have)

### 13. Add a standalone /primegodling command
Currently all commands are nested under `/tensura edit awakening entity`. A root command would be easier for players and admins.

### 14. Replace hardcoded strings with translatable keys
All user-facing messages (`Component.literal("§...")`) should use `Component.translatable("primegodling.message.xxx")` for localization.

### 15. Use RandomSource instead of new Random()
`PrimeGodling.java` and `PrimeGodlingRace.java` use `new Random()` instead of Minecraft's `RandomSource`.

### 16. Add more config options
- Divine Devour success chance (hardcoded 10%)
- Divine Devour cooldown (hardcoded 200/100)
- Luminarch Blessing glow range (hardcoded 24/48)
- Hostile mob kill requirement (hardcoded 50,000)
- Demon Lord kill requirement (hardcoded 3)
- Nexus ritual threshold (hardcoded 1,000)

### 17. Advancement for skill milestones
No advancements for learning skills or reaching specific power thresholds beyond race evolution.

### 18. Sync kill counters to client
The `demon_lord_kills`, `rimuru_killed`, `hinata_killed`, `hostile_mob_kills` data are server-side only. A client-side display (e.g., in a GUI or action bar) would help players track progress.

---

## Summary Table

| # | Fix | Priority | Effort |
|---|-----|----------|--------|
| 1 | Rename advancement filename | P0 | 1 min |
| 2 | Fix Divine Devour lang | P0 | 1 min |
| 3 | Use cosmicAwarenessRange config | P1 | 5 min |
| 4 | Add login sync | P1 | 30 min |
| 5 | Review Bloom regen multiplier | P1 | 5 min |
| 6 | Wire or remove dead config | P1 | 15 min |
| 7 | Fix onRaceSet re-login overwrite | P1 | 30 min |
| 8 | Add minSkills check or remove | P1 | 10 min |
| 9 | Add Fortitude texture | P2 | 10 min |
| 10 | Clean up memory leaks | P2 | 15 min |
| 11 | Remove dead code | P2 | 15 min |
| 12 | Improve Divine Nexus reward | P2 | 20 min |
| 13 | Add /primegodling command | P3 | 1 hr |
| 14 | Translatable strings | P3 | 1 hr |
| 15 | Use RandomSource | P3 | 15 min |
| 16 | Add more config options | P3 | 1 hr |
| 17 | Skill milestone advancements | P3 | 1 hr |
| 18 | Sync kill counters | P3 | 30 min |
