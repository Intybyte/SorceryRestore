package me.vaan.sorceryskill.auraskills.listeners

import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.auraskills.SorceryAbilities
import me.vaan.sorceryskill.auraskills.SorcerySkill
import me.vaan.sorceryskill.auraskills.sources.ManaSource
import me.vaan.sorceryskill.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ManaUseListener : Listener {
    @EventHandler
    fun onManaAbility(event: ManaAbilityActivateEvent) {
        event.manaUsed
        val source = Utils.firstSource<ManaSource>()

        val skillUser = SorceryRestore.api.getUser(event.player.uniqueId)
        val award = source.xp * event.manaUsed
        SorceryRestore.debug("${this::class} awarded $award to ${event.player}, xp multiplier set to ${source.xp}")
        skillUser.addSkillXp(SorcerySkill.SORCERY, award)

        SorceryAbilities.OVERLOAD.callHandler(event.player, event)
    }
}