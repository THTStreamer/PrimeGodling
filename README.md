# Primordial Ascent - Tensura: Reincarnated Addon

A **Minecraft 1.21.1 NeoForge** addon for [Tensura: Reincarnated](https://www.curseforge.com/minecraft/mc-mods/tensura-reincarnated) that adds the **Godling** race -- a 7-stage evolution line culminating in the **Primordial Supreme God**, with a brand-new **Divine Nexus** awakening path that stands alongside True Demon Lord and True Hero as a third endgame option. Features 9 unique skills including toggleable passives, an AoE magicule nullification field, and a skill-stealing universal ability.

---

## Table of Contents

- [Dependencies](#dependencies)
- [Installation](#installation)
- [Getting Started](#getting-started)
- [Evolution Chain](#evolution-chain)
- [Skills](#skills)
- [Creative Flight](#creative-flight)
- [Nexus Cores](#nexus-cores)
- [Divine Nexus Awakening](#divine-nexus-awakening)
- [Configuration](#configuration)
- [Known Issues](#known-issues)
- [FAQ](#faq)

---

## Dependencies

| Mod | Version | Required |
|-----|---------|----------|
| [Minecraft](https://www.minecraft.net/) | 1.21.1 | Yes |
| [NeoForge](https://neoforged.net/) | 21.1.230+ | Yes |
| [Tensura: Reincarnated](https://www.curseforge.com/minecraft/mc-mods/tensura-reincarnated) | 2.0.1.0+ | Yes |
| [ManasCore](https://www.curseforge.com/minecraft/mc-mods/manascore) | 4.0.0.2+ | Yes |
| [GeckoLib](https://www.curseforge.com/minecraft/mc-mods/geckolib) | 4.8.4+ | Yes |
| [Architectury](https://www.curseforge.com/minecraft/mc-mods/architectury) | 13.0.8+ | Yes |

> **Note:** All dependencies must be installed for the mod to function. ManasCore and GeckoLib are pulled in automatically by Tensura: Reincarnated in most modpack setups.

---

## Installation

1. Install Minecraft 1.21.1 with NeoForge 21.1.230 or later.
2. Download the latest `PrimordialAscent-1.21.1-0.2.5.jar` from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/prime-godling/files).
3. Place the JAR into your Minecraft `mods/` folder alongside Tensura: Reincarnated, ManasCore, and GeckoLib.
4. Launch the game. The mod appears in the mod list as **Primordial Ascent**.

---

## Getting Started

### Choosing the Race

When you create a new world and join, the Tensura reincarnation screen will appear. Select **Half Godling** from the starter race list to begin your journey. You can also use the command:

```
/tensura edit <player> race primegodling:half_godling
```

### Starting Stats

| Stat | Value |
|------|-------|
| Starting EP Range | 100 - 4,000 |
| Starting Magicule | 100 - 1,000 |
| Difficulty | HARD |
| Starting Skills | None (granted at later stages) |

> **Tip:** EP is gained by killing mobs with EP. Your EP gain rate is multiplied by 0.5x (configurable) to balance the powerful late-game kit.

---

## Evolution Chain

The Godling line has **7 stages**. Each evolution requires hitting an EP threshold **and** consuming a certain number of **Nexus Cores** (see [Nexus Cores](#nexus-cores)).

| Stage | Race | EP Required | Nexus Cores | Magicule Range | Aura Range | Key Reward |
|-------|------|-------------|-------------|----------------|------------|------------|
| 0 | Half Godling | 0 | 0 | 100 - 1,000 | 50 - 500 | Base stats |
| 1 | Demi Godling | 50,000 | 1 | 5,000 - 20,000 | 2,500 - 10,000 | +HP, +DMG, +Speed |
| 2 | Prime Godling | 100,000 | 4 | 20,000 - 80,000 | 10,000 - 40,000 | Primordial Bloom, Cosmic Awareness, random resistances + skills |
| 3 | Celestial Godling | 200,000 | 16 | 100,000 - 500,000 | 50,000 - 250,000 | Stellar Ascension, Ecliptic Mastery, resistances evolve to nullifications |
| 4 | Ecliptic Godling | 400,000 | 64 | 500,000 - 2,000,000 | 250,000 - 1,000,000 | Luminarch Blessing, Dodge Negate, Resistance Degradation |
| 5 | New God | 800,000 | 256 | 2,000,000 - 8,000,000 | 1,000,000 - 4,000,000 | Primordial Fortitude (55-65% damage reduction) |
| 6 | Primordial Supreme God | 1,600,000 | 1,024 | 8,000,000 - 20,000,000 | 4,000,000 - 10,000,000 | All nullifications, Creation Authority |

### Evolution Requirements

To evolve, you need:
1. **Enough EP** -- kill mobs with EP to accumulate the required amount.
2. **Nexus Cores** -- craft them or loot them from mobs (see below).
3. **Named player** -- required for Stage 4+ evolutions.
4. **Awakening** -- Stage 6 requires you to have completed the Divine Nexus awakening, OR be a True Demon Lord, OR be a True Hero.

---

## Skills

The mod features **9 unique skills** across the evolution chain, plus 2 universal skills available to any race. All skills have configurable values in `skills.toml`. Skill icons and particle effects are included for all skills.

### Unique & Intrinsic Skills (granted at evolution milestones)

| Skill | Type | Stage | Description |
|-------|------|-------|-------------|
| **Primordial Bloom** | INTRINSIC | 2 | Passively regenerates magicules at 3%/s. Periodically spawns blooming particles. Mastery: grants Regeneration to nearby players and subordinates. |
| **Cosmic Awareness** | INTRINSIC | 2 | Toggle: grants presence sense, detects invisible entities, and marks entities that target you. Drains energy while active. |
| **Stellar Ascension** | INTRINSIC | 3 | Toggle: boosts attack damage and max health with a celestial aura. Deals bonus magic damage on attack. Mastery: slow falling. |
| **Ecliptic Mastery** | INTRINSIC | 3 | Toggle: grants bonus armor and toughness. Reflects damage back to attackers. Has a chance to negate incoming damage. Mastery doubles the effect. |
| **Luminarch Blessing** | INTRINSIC | 4 | Toggle: grants Regeneration, Absorption, and highlights all living entities within 24 blocks through walls (Glowing). Mastery: 48-block range and heals nearby allies. |
| **Primordial Fortitude** | INTRINSIC | 5 | Toggle: boosts all core attributes. Grants Fire Resistance, Water Breathing, fall damage immunity, and 55-65% damage reduction. Greatly accelerates skill learning. |

### Ultimate & Unique Skills (granted at endgame)

| Skill | Type | Stage | Description |
|-------|------|-------|-------------|
| **Creation Authority** | ULTIMATE | 6 | Passively grants +6 Attack, +16 HP, +4 Armor, +4 Toughness. Press to call down primordial lightning that devastates the target area with a massive explosion. Grants brief damage immunity after activation. Cooldown: 200 ticks (10s) / 60 ticks mastered (3s). |

### Universal Skills (available to any race)

| Skill | Type | How to Obtain | Description |
|-------|------|---------------|-------------|
| **Divine Devour** | UNIQUE | Random skill generation (any race) | Press the skill key while looking at a mob to attempt to steal one of its skills. 10% chance per use. Cooldown: 10 ticks (0.5s) / 5 ticks mastered (0.25s). |
| **Divine Disruption** | UNIQUE | Random skill generation (any race) | Press and hold to nullify all magicules within a configurable radius (default: 15 blocks), disabling enemy skills. Costs 10% magicules on activation and 5% per 10 ticks while held. Mastered: 5% activation, 2% per 10 ticks. |

> **Note:** Both Divine Devour and Divine Disruption are **not restricted to any race**. They are included in Tensura's universal unique skill pool and can be randomly granted to any player on first join or character reset, regardless of race. By default, only Common, Intrinsic, and Extra skills can be copied. Server owners can enable Unique/Ultimate skill copying and blacklist specific skills via the config.

### Skill Visuals

All 9 skills have dedicated icon textures and particle effects. Divine Disruption features a professional-grade client-side renderer with:
- Expanding shockwave rings
- Ground indicator circle showing AoE radius
- Spiraling energy tendrils from targets to caster
- Glow orbs and pulsing aura effects
- Warden sound effects on activation and release

### Random Skill Grants

At certain evolution milestones, you receive random skills from the Tensura skill pool:

- **Stage 2 (Prime Godling):** 3 random resistances + 3 random non-resistance skills (configurable).
- **Stage 3 (Celestial Godling):** Your resistances evolve into nullifications + 2 random skills granted or mastered (configurable).
- **Stage 6 (Primordial Supreme God):** All 22 nullifications granted + Creation Authority + 1 random Unique/Ultimate skill (from the universal pool, which includes Divine Devour and Divine Disruption).

---

## Creative Flight

Flight is unlocked at **Celestial Godling** (Stage 3) and above. Press the **R key** (default) to toggle flight on/off.

### Flight Costs

| Cost | Alone | With Named Subordinate Nearby |
|------|-------|-------------------------------|
| Activation | 40 magicules | 20 magicules |
| Maintenance | 10 magicules every 10 ticks | 2 magicules every 100 ticks |

> **Tip:** Having a named subordinate nearby halves both activation and maintenance costs. A "named subordinate" is any living entity within 32 blocks that has a custom name and is your subordinate (via Tensura's subordinate system).

Flight automatically disables when your magicules drop below the activation cost.

---

## Nexus Cores

Nexus Cores are the currency required for each evolution. You need them in addition to EP.

### Crafting

Nexus Core crafting is **disabled by default**. Server admins must enable it in the server config (`config/primegodling/server.toml`):

```toml
# config/primegodling/server.toml
general {
  crafting_enabled = true
}
```

**Recipe** (shaped crafting):

```
E D E
D N D
E D E
```

| Slot | Ingredient |
|------|------------|
| E | Echo Shard |
| D | Diamond |
| N | Nether Star |

> **Result:** 1 Nexus Core per craft.

### Mob Drops

Nexus Cores also drop from certain Tensura mobs. The drop table is configurable in `config/primegodling/nexus-drops.toml`.

**Default drop table:**

| Mob | Drop Chance | Amount |
|-----|-------------|--------|
| Hinata Sakaguchi | 100% | 1 |
| Ifrit | 100% | 1 |
| Charybdis | 100% | 1 |
| Wyrm | 50% | 1 |
| Ogre | 25% | 1 |
| Armorsaurus | 2% | 1 |
| Black Wolf | 2% | 1 |
| Goblin | 1% | 1 |
| Orc | 2% | 1 |
| Lizardman | 2% | 1 |

> **Note:** Rimuru Tempest is not included by default as he is not part of the base Tensura mod. You can add any Tensura entity ID via the config file.

### Nexus Core Consumption

Nexus Cores are consumed when you evolve. The amount spent is tracked separately from the amount eaten (required for the Divine Nexus awakening). You can check your progress via the Tensura status screen.

---

## Divine Nexus Awakening

The **Divine Nexus** is a third awakening path alongside **True Demon Lord** and **True Hero**. It is the intended endgame for Godling players.

### Requirements

To begin the Divine Nexus ritual, you must meet **all** of the following:

| Requirement | Details |
|-------------|---------|
| **Race** | Must be a **New God** (Stage 5) |
| **EP** | Configurable (default: 1,000,000 EP) |
| **Named** | Your character must have a name |
| **Nexus Cores Consumed** | Configurable (default: 1,000 Nexus Cores eaten) |
| **Kill Requirements** | Configurable: Kill X Awakened Demon Lords **OR** (Kill Hinata Sakaguchi + Kill Y hostile mobs) |
| **Unique Skills** | At least 5 unique skills learned |
| **Magicule** | Enough magicule to fuel the awakening (configurable, default 10,000) |

> **Default Kill Requirements:** 3 Awakened Demon Lords **OR** (Kill Hinata Sakaguchi + Kill 50,000 hostile mobs). All values can be changed in `config/primegodling/skills.toml` under `[divine_nexus_awakening]`. The Hinata kill is optional if `require_hinata_kill = false`.

### Starting the Ritual

Right-click with a **Nexus Core** in hand when all requirements are met. The ritual takes **200 ticks (10 seconds)** to complete.

During the ritual:
- You are slowed and given Slow Falling, Fire Resistance, and Damage Resistance.
- Three phases of escalating particle effects play (golden particles, swirling divine dome, intensifying light).
- You cannot move or act during the ritual.

### Awakening Rewards

| Reward | Value |
|--------|-------|
| Magicule Multiplier | 3x your pre-awakening max |
| Aura Multiplier | 4x your pre-awakening max |
| Alignment | MAJIN (gold name in Tensura menus) |
| Ultimate Skill | Creation Authority (learned automatically) |
| Evolution | Forces evolution to **Primordial Supreme God** |
| Advancement | "Divine Nexus" advancement granted |

### Kill Tracking

Your kill progress is tracked automatically:
- **Demon Lord kills:** Kill any player with True Demon Lord status.
- **Hinata kill:** Kill `tensura:hinata_sakaguchi` (if `require_hinata_kill` is enabled).
- **Hostile mob kills:** Kill any `Monster` entity.
- **Boss mob kills:** Kill any entity registered in the `boss_mobs` config list.

Progress is synced to your client and displayed in chat when you make progress.

---

## Configuration

All config files are located in `config/primegodling/`:

| File | Type | Purpose |
|------|------|---------|
| `races.toml` | COMMON | EP thresholds, magicule/aura ranges, flight costs, EP gain multiplier, random reward counts |
| `skills.toml` | COMMON | All skill settings, Divine Nexus requirements, Divine Devour/Disruption options |
| `server.toml` | SERVER | Nexus Core crafting recipe toggle |
| `nexus-drops.toml` | SERVER | Per-mob Nexus Core drop chances |

> **Note:** All skill config values are read at runtime and immediately affect gameplay when changed. Server owners can customize every aspect of skill behavior without restarting the server (config changes take effect on next server restart or world reload).

### Skills Config (`skills.toml`)

Every skill in the mod has configurable values. Below is a summary of all available options:

```toml
# Primordial Bloom — Stage 2 Unique Skill
primordial_bloom {
  regen_rate_percent_per_second = 3  # Magicule regen per second (% of max)
}

# Cosmic Awareness — Stage 2 Intrinsic Skill
cosmic_awareness {
  detection_range = 32  # Range for invisible entity detection and presence sense
}

# Stellar Ascension — Stage 3 Intrinsic Skill
stellar_ascension {
  attack_bonus = 4.0              # Bonus attack damage when toggled
  health_bonus = 20.0             # Bonus max health when toggled
  damage_bonus = 2.0              # Bonus magic damage on attack (non-mastered)
  mastered_damage_bonus = 4.0     # Bonus magic damage on attack (mastered)
  energy_cost = 5.0               # Energy cost per tick while toggled
}

# Ecliptic Mastery — Stage 3 Intrinsic Skill
ecliptic_mastery {
  armor = 6.0                     # Bonus armor
  toughness = 4.0                 # Bonus armor toughness
  reflect_damage = 1.0            # Damage reflected to attackers (non-mastered)
  mastered_reflect_damage = 3.0   # Damage reflected to attackers (mastered)
  negate_chance = 0.05            # Chance to negate damage (5%, non-mastered)
  mastered_negate_chance = 0.10   # Chance to negate damage (10%, mastered)
}

# Luminarch Blessing — Stage 4 Intrinsic Skill
luminarch_blessing {
  energy_cost_per_tick = 200      # Energy cost per tick while toggled
  glow_range = 24                 # Glowing detection range (non-mastered)
  mastered_glow_range = 48        # Glowing detection range (mastered)
  heal_range = 4.0                # Range for healing allies (mastered only)
}

# Primordial Fortitude — Stage 5 Intrinsic Skill
primordial_fortitude {
  attack_bonus = 10.0             # Bonus attack damage
  health_bonus = 40.0             # Bonus max health
  armor = 12.0                    # Bonus armor
  toughness = 8.0                 # Bonus armor toughness
  speed_multiplier = 0.1          # Movement speed bonus (0.1 = 10% faster)
  damage_reduction = 0.55         # Damage reduction (55%, non-mastered, configurable 0-100%)
  mastered_damage_reduction = 0.65 # Damage reduction (65%, mastered, configurable 0-100%)
  learning_gain = 50.0            # Bonus ability learning rate
  mastery_gain = 50.0             # Bonus ability mastery rate
  chant_speed = 5.0               # Chant speed multiplier
}

# Creation Authority — Stage 6 Ultimate Skill
creation_authority {
  cooldown_ticks = 200            # Cooldown after use (non-mastered)
  mastered_cooldown_ticks = 60    # Cooldown after use (mastered)
  energy_cost = 5000.0            # Energy cost to activate
  explosion_radius = 12.0         # Explosion radius (non-mastered)
  mastered_explosion_radius = 18.0 # Explosion radius (mastered)
  immunity_duration = 40          # Invulnerability after activation (ticks)
}

# Divine Devour — Universal Unique Skill
divine_devour {
  success_chance = 0.10            # Chance to steal Common/Intrinsic/Extra skills (10%)
  unique_success_chance = 0.05     # Chance to steal UNIQUE skills (5%)
  ultimate_success_chance = 0.01   # Chance to steal ULTIMATE skills (1%)
  allow_unique_skills = false     # Allow copying UNIQUE skills
  allow_ultimate_skills = false   # Allow copying ULTIMATE skills
  skill_blacklist = []            # Skills that cannot be copied (modid:skill_id)
  cooldown_ticks = 10             # Cooldown after use (non-mastered)
  mastered_cooldown_ticks = 5     # Cooldown after use (mastered)
  range = 32.0                    # Maximum targeting range in blocks
}

# Divine Disruption — Universal Unique Skill
divine_disruption {
  activation_cost = 0.10           # Magicule cost on activation (10% of max)
  activation_cost_mastered = 0.05  # Magicule cost on activation when mastered (5%)
  tick_cost = 0.05                 # Magicule cost per interval while active (5%)
  tick_cost_mastered = 0.02        # Magicule cost per interval when mastered (2%)
  aoe_radius = 15.0                # Radius of the disruption aura in blocks
  cost_interval = 10               # Ticks between each sustained cost drain
}

# Divine Nexus — Awakening prerequisites (New God only)
divine_nexus {
  min_unique_skills = 5           # Minimum unique skills to unlock path
  min_ep_required = 150000        # Minimum EP to see Divine Nexus option
  nexus_core_ep_cost = 10000      # EP cost per Nexus Core consumed
}

# Divine Nexus Awakening — Full awakening requirements
divine_nexus_awakening {
  ep_required = 1000000           # Minimum EP required
  cores_required = 1000           # Nexus Cores required
  demon_lord_kills_required = 3   # Awakened Demon Lords (Path A)
  hostile_mob_kills_required = 50000 # Hostile mobs (Path B)
  require_hinata_kill = true      # Require Hinata kill for Path B
  boss_mobs = []                  # Additional boss mobs (modid:entity_id)
}
```

---

## Known Issues

- `ScaledEPRequirement.java` and `ServerProxy.java` contain dead code from earlier development.
- `FLIGHT_DATA` and `LAST_EP` maps are not cleaned on player logout (minor memory concern on long-running servers).
- The Divine Nexus advancement reward loot table awards a placeholder item.
- Divine Disruption texture is a placeholder (replace with proper artwork).

See [ROADMAP.md](ROADMAP.md) for the full list of planned fixes and improvements.

---

## FAQ

**Q: How do I check my EP?**
A: Use the Tensura commands: `/tensura get stat <player> ep` and `/tensura get stat <player> magicule`.

**Q: Can I become both a True Demon Lord and a Divine Nexus?**
A: You can be a TDL or Hero *before* awakening as Divine Nexus. The `AwakenedOrTDLOrHeroRequirement` accepts any of the three. Once you are a Primordial Supreme God, you retain the MAJIN alignment from the Divine Nexus awakening.

**Q: Can I use this with other Tensura addons?**
A: Yes. Primordial Ascent injects its races additively into the Tensura config. It does not remove or modify existing races.

**Q: Does the mod work on a dedicated server?**
A: Yes. All logic is server-side. Client-only code (rendering, halos) is properly separated.

**Q: How do I reset my race?**
A: Use `/tensura reset <player>` to reset your race and re-enter the reincarnation screen.

---

## Building from Source

```bash
# Clone the repository
git clone https://github.com/THTStreamer/PrimeGodling.git
cd PrimeGodling

# Build the JAR
gradlew build
```

The output JAR will be in `build/libs/`.

> **Requires:** JDK 21, Gradle 9.x+

### Included Resources

The mod includes all 9 skill icon textures at `assets/primegodling/textures/skill/`. Divine Disruption texture is a placeholder and should be replaced with proper artwork.

### Included Resources

The mod includes all 9 skill icon textures at `assets/primegodling/textures/skill/`. Divine Disruption texture is a placeholder and should be replaced with proper artwork.

---

## License

All Rights Reserved. See [LICENSE.txt](LICENSE.txt) for details.

---

## Credits

- **THTStreamer** -- Author and maintainer
- **ManasMods** -- Tensura: Reincarnated and ManasCore
- **NeoForged** -- NeoForge mod loader
- **Community Addons** -- TR Addon, Ragnarok Races (inspiration for addon patterns)
