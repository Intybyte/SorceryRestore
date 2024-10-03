package me.vaan.sorceryskill.auraskills

import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.registry.NamespacedId
import dev.aurelium.auraskills.api.user.SkillsUser
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.auraskills.handlers.arcaneKnowledgeHandler
import me.vaan.sorceryskill.auraskills.handlers.meditationHandler
import me.vaan.sorceryskill.auraskills.handlers.overloadHandler
import me.vaan.sorceryskill.utils.Utils.PLUGIN_NAME
import org.bukkit.entity.Player

typealias AbilityHandler = (CustomAbility, Player, Array<out Any>) -> Unit

enum class SorceryAbilities(val ability: CustomAbility, private val handler: AbilityHandler) {
    MEDITATION(
        CustomAbility.builder(NamespacedId.of(PLUGIN_NAME, "meditation"))
            .displayName("Meditation")
            .description("When shifting mana regeneration is increased by {value}%")
            .info("{value}% Mana Regen Boost on Shift ")
            .baseValue(5.0)
            .valuePerLevel(5.0)
            .unlock(1)
            .levelUp(5)
            .build()!!
        , ::meditationHandler),
    SORCERY_PROFICIENCY(
        CustomAbility.builder(NamespacedId.of(PLUGIN_NAME, "sorcery_proficiency"))
            .displayName("Sorcery Proficiency")
            .description("Gain {value}% more XP from sorcery sources.")
            .info("+{value}% Sorcery XP ")
            .baseValue(10.0)
            .valuePerLevel(10.0)
            .unlock(2)
            .levelUp(5)
            .build()!!
        , { _, _, _ ->  }),
    OVERLOAD(
        CustomAbility.builder(NamespacedId.of(PLUGIN_NAME, "overload"))
            .displayName("Overload")
            .description("There is a {value}% chance of not using mana when a mana skill is activated")
            .info("{value}% No Mana Cost")
            .baseValue(1.0)
            .valuePerLevel(1.0)
            .unlock(3)
            .levelUp(5)
            .build()!!
        , ::overloadHandler),
    ARCANE_KNOWLEDGE(
        CustomAbility.builder(NamespacedId.of(PLUGIN_NAME, "arcane_knowledge"))
            .displayName("Arcane Knowledge")
            .description("Gain {value}% more XP from every source.")
            .info("+{value}% All XP")
            .baseValue(5.0)
            .valuePerLevel(5.0)
            .unlock(4)
            .levelUp(5)
            .build()!!
        , ::arcaneKnowledgeHandler); // Erik the bro on discord helped out with ideas o7

    fun callHandler(player: Player, vararg objects: Any) {
        this.handler(this.ability, player, objects)
    }

    fun getValue(user: SkillsUser): Double {
        return this.ability.getValue(user.getAbilityLevel(this.ability))
    }

    companion object {
        fun loadAbilities() {
            val reg = SorceryRestore.registry
            entries.forEach { abilityEnum ->
                reg.registerAbility(abilityEnum.ability)
            }
        }
    }
}