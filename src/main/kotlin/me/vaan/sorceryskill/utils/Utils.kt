package me.vaan.sorceryskill.utils

import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.source.XpSource
import me.vaan.sorceryskill.auraskills.SorcerySkill
import org.bukkit.entity.Player
import kotlin.reflect.KClass

object Utils {
    val PLUGIN_NAME = "sorcery-restore"
    val sourceMap = HashMap<KClass<out XpSource>, XpSource>()

    inline fun <reified T : XpSource> firstSource(): T {
        return sourceMap.getOrPut(T::class) {
            SorcerySkill.SORCERY.sources.filterIsInstance<T>().first()
        } as T
    }

    fun getSkillValue(player: Player, ability: CustomAbility): Double {
        val api = AuraSkillsApi.get()
        val skillPlayer = api.getUser(player.uniqueId)
        val level = skillPlayer.getAbilityLevel(ability)

        return ability.getValue(level)
    }
}