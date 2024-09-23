package me.vaan.sorceryskill.utils

import me.vaan.CooldownManager
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.auraskills.SorceryManaBlast
import org.bukkit.scheduler.BukkitRunnable

fun <T> CooldownManager<T>.setCastCooldowns() {
    object : BukkitRunnable() {
        override fun run() {
            val max = if (SorceryManaBlast.BLAST.maxLevel == 0) 100 else SorceryManaBlast.BLAST.maxLevel

            for (i in 0..max) {
                val cooldown = SorceryManaBlast.BLAST.getCooldown(i).toLong()
                this@setCastCooldowns.setCooldown("cast_cooldown_$i", cooldown * 1000)
            }
        }
    }.runTaskLaterAsynchronously(SorceryRestore.instance(), 20L)
    // wait for AS to load the skills, I hope the dumb approach works lol
}