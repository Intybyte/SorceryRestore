package me.vaan.sorceryskill.utils

import dev.aurelium.auraskills.api.ability.AbilityContext
import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.source.XpSource
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.auraskills.SorcerySkill
import org.bukkit.entity.Player
import kotlin.reflect.KClass

object Utils {
    const val PLUGIN_NAME = "sorcery-restore"
    val abilityContext = AbilityContext(SorceryRestore.api)
    val sourceMap = HashMap<KClass<out XpSource>, XpSource>()

    inline fun <reified T : XpSource> firstSource(): T {
        return sourceMap.getOrPut(T::class) {
            SorcerySkill.SORCERY.sources.filterIsInstance<T>().first()
        } as T
    }

    fun getSkillValue(player: Player, ability: CustomAbility): Double {
        val skillPlayer = player.getSkillPlayer()
        val level = skillPlayer!!.getAbilityLevel(ability)

        return ability.getValue(level)
    }
}