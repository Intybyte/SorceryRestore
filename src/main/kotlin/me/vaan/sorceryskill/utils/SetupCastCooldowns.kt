package me.vaan.sorceryskill.utils

import me.vaan.CooldownManager
import me.vaan.sorceryskill.auraskills.SorceryManaBlast

fun <T> CooldownManager<T>.setCastCooldowns() {
    if (!SorceryManaBlast.BLAST.isEnabled) return

    val max = if (SorceryManaBlast.BLAST.maxLevel == 0) 100 else SorceryManaBlast.BLAST.maxLevel

    for (i in 0..max) {
        val cooldown = SorceryManaBlast.BLAST.getCooldown(i).toLong()
        this@setCastCooldowns.setCooldown("cast_cooldown_$i", cooldown * 1000)
    }
}