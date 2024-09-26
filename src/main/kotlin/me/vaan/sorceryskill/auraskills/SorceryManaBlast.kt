package me.vaan.sorceryskill.auraskills

import dev.aurelium.auraskills.api.mana.CustomManaAbility
import dev.aurelium.auraskills.api.registry.NamespacedId
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.utils.Utils

object SorceryManaBlast {
    val BLAST = CustomManaAbility.builder(NamespacedId.of(Utils.PLUGIN_NAME, "mana_blast"))
        .displayName("Mana blast")
        .description("Creates a mana blast that deals {value} damage, 5 second cooldown")
        .baseValue(0.75)
        .valuePerLevel(0.75)
        .baseManaCost(15.0)
        .manaCostPerLevel(5.0)
        .baseCooldown(3.0)
        .cooldownPerLevel(0.2)
        .unlock(6)
        .levelUp(6)
        .build()!!

    fun loadManaAbility() {
        val reg = SorceryRestore.registry
        reg.registerManaAbility(BLAST)
    }
}