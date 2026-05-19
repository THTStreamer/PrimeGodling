package com.primegodling.primegodling.common.data;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.skill.CosmicAwarenessSkill;
import com.primegodling.primegodling.common.data.skill.CreationAuthoritySkill;
import com.primegodling.primegodling.common.data.skill.EclipticMasterySkill;
import com.primegodling.primegodling.common.data.skill.LuminarchBlessingSkill;
import com.primegodling.primegodling.common.data.skill.PrimordialBloomSkill;
import com.primegodling.primegodling.common.data.skill.PrimordialOmnipotenceSkill;
import com.primegodling.primegodling.common.data.skill.StellarAscensionSkill;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SkillData {

    private SkillData() {}

    public static void registerAll() {
        PrimeGodling.LOGGER.info("[{}] Registering custom skills …", PrimeGodling.MOD_ID);

        Map<ResourceLocation, ManasSkill> skills = createSkills();

        for (Map.Entry<ResourceLocation, ManasSkill> entry : skills.entrySet()) {
            ResourceLocation id = entry.getKey();
            ManasSkill skill = entry.getValue();
            SkillRegistry.SKILLS.register(id, () -> skill);
            PrimeGodling.LOGGER.debug("[{}] Registered skill: {}", PrimeGodling.MOD_ID, id);
        }

        PrimeGodling.LOGGER.info("[{}] Registered {} skills.", PrimeGodling.MOD_ID, skills.size());
    }

    private static Map<ResourceLocation, ManasSkill> createSkills() {
        var ids = com.primegodling.primegodling.common.data.SkillRegistry.class;
        Map<ResourceLocation, ManasSkill> map = new LinkedHashMap<>();

        map.put(com.primegodling.primegodling.common.data.SkillRegistry.PRIMORDIAL_BLOOM, new PrimordialBloomSkill());
        map.put(com.primegodling.primegodling.common.data.SkillRegistry.COSMIC_AWARENESS, new CosmicAwarenessSkill());
        map.put(com.primegodling.primegodling.common.data.SkillRegistry.STELLAR_ASCENSION, new StellarAscensionSkill());
        map.put(com.primegodling.primegodling.common.data.SkillRegistry.ECLIPTIC_MASTERY, new EclipticMasterySkill());
        map.put(com.primegodling.primegodling.common.data.SkillRegistry.LUMINARCH_BLESSING, new LuminarchBlessingSkill());
        map.put(com.primegodling.primegodling.common.data.SkillRegistry.PRIMORDIAL_OMNIPOTENCE, new PrimordialOmnipotenceSkill());
        map.put(com.primegodling.primegodling.common.data.SkillRegistry.CREATION_AUTHORITY, new CreationAuthoritySkill());

        return map;
    }
}
