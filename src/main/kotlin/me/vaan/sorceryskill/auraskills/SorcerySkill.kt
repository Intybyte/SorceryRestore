package me.vaan.sorceryskill.auraskills

import dev.aurelium.auraskills.api.item.ItemContext
import dev.aurelium.auraskills.api.registry.NamespacedId
import dev.aurelium.auraskills.api.skill.CustomSkill
import me.vaan.sorceryskill.utils.Utils.PLUGIN_NAME

object SorcerySkill {
    val SORCERY = CustomSkill
        .builder(NamespacedId.of(PLUGIN_NAME, "sorcery"))
        .displayName("Sorcery")
        .description("&7Use mana abilities to earn sorcery xp")
        .abilities(SorceryAbilities.MEDITATION.ability,
                   SorceryAbilities.OVERLOAD.ability)
        //.manaAbility(CannonManaAbilities.STORM_BLAST)
        .xpMultiplierAbility(SorceryAbilities.SORCERY_PROFICIENCY.ability)
        .item(
            ItemContext
            .builder()
            .material("blaze_rod")
            .pos("3,2")
            .build())
        .build()!!
}