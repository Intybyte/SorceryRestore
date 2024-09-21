package me.vaan.sorceryskill.utils

import me.vaan.sorceryskill.SorceryRestore
import org.bukkit.event.Listener
import kotlin.reflect.KClass

// I will probably make some library util repo for this stuff in the future and make it more generic
object Cooldown {
    private val timeMap = HashMap<KClass<out Listener>, HashMap<String, Long>>()
    private val cooldowns = HashMap<KClass<out Listener>, Long>()

    fun setCooldown(clazz: KClass<out Listener>, cooldown: Long) {
        cooldowns[clazz] = cooldown
    }

    fun checkCooldown(clazz: KClass<out Listener>, playerName: String) : Boolean {
        val cooldown = cooldowns[clazz]!!
        val playerTime = getPlayer(clazz, playerName)
        val current = System.currentTimeMillis()
        SorceryRestore.debug("PlayerTime: $playerTime Cooldown: $cooldown Current: $current")

        if (playerTime == null || playerTime + cooldown <= current) {
            setPlayer(clazz, playerName)
            return true
        }

        return false
    }

    fun printAllCooldowns() {
        for (entry in cooldowns) {
            SorceryRestore.debug("KClass: " + entry.key + " Value: " + entry.value)
        }
    }

    private fun getPlayer(clazz: KClass<out Listener>, playerName: String) : Long? {
        timeMap.putIfAbsent(clazz, HashMap())
        val playerMap = timeMap[clazz]!!
        return playerMap[playerName]
    }

    private fun setPlayer(clazz: KClass<out Listener>, playerName: String) {
        timeMap.putIfAbsent(clazz, HashMap())
        val playerMap = timeMap[clazz]!!
        playerMap[playerName] = System.currentTimeMillis()
        SorceryRestore.debug("Set player time for $playerName in class ${clazz.simpleName} to ${playerMap[playerName]}")
    }
}