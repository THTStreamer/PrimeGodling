package com.primegodling.primegodling.common.data;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.skill.CosmicAwarenessSkill;
import com.primegodling.primegodling.common.data.skill.CreationAuthoritySkill;
import com.primegodling.primegodling.common.data.skill.DivineDevourSkill;
import com.primegodling.primegodling.common.data.skill.DivineDisruptionSkill;
import com.primegodling.primegodling.common.data.skill.EclipticMasterySkill;
import com.primegodling.primegodling.common.data.skill.LuminarchBlessingSkill;
import com.primegodling.primegodling.common.data.skill.PrimordialBloomSkill;
import com.primegodling.primegodling.common.data.skill.PrimordialFortitudeSkill;
import com.primegodling.primegodling.common.data.skill.StellarAscensionSkill;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSkills {

    private static final DeferredRegister<ManasSkill> SKILLS =
            DeferredRegister.create(SkillRegistry.KEY, PrimeGodling.MOD_ID);

    public static final DeferredHolder<ManasSkill, ManasSkill> PRIMORDIAL_BLOOM;
    public static final DeferredHolder<ManasSkill, ManasSkill> COSMIC_AWARENESS;
    public static final DeferredHolder<ManasSkill, ManasSkill> STELLAR_ASCENSION;
    public static final DeferredHolder<ManasSkill, ManasSkill> ECLIPTIC_MASTERY;
    public static final DeferredHolder<ManasSkill, ManasSkill> LUMINARCH_BLESSING;
    public static final DeferredHolder<ManasSkill, ManasSkill> PRIMORDIAL_FORTITUDE;
    public static final DeferredHolder<ManasSkill, ManasSkill> CREATION_AUTHORITY;
    public static final DeferredHolder<ManasSkill, ManasSkill> DIVINE_DEVOUR;
    public static final DeferredHolder<ManasSkill, ManasSkill> DIVINE_DISRUPTION;

    static {
        PRIMORDIAL_BLOOM = SKILLS.register("primordial_bloom", PrimordialBloomSkill::new);
        COSMIC_AWARENESS = SKILLS.register("cosmic_awareness", CosmicAwarenessSkill::new);
        STELLAR_ASCENSION = SKILLS.register("stellar_ascension", StellarAscensionSkill::new);
        ECLIPTIC_MASTERY = SKILLS.register("ecliptic_mastery", EclipticMasterySkill::new);
        LUMINARCH_BLESSING = SKILLS.register("luminarch_blessing", LuminarchBlessingSkill::new);
        PRIMORDIAL_FORTITUDE = SKILLS.register("primordial_fortitude", PrimordialFortitudeSkill::new);
        CREATION_AUTHORITY = SKILLS.register("creation_authority", CreationAuthoritySkill::new);
        DIVINE_DEVOUR = SKILLS.register("divine_devour", DivineDevourSkill::new);
        DIVINE_DISRUPTION = SKILLS.register("divine_disruption", DivineDisruptionSkill::new);
    }

    public static void init(IEventBus bus) {
        SKILLS.register(bus);
    }
}
