package me.vaan.sorceryskill.auraskills.listeners

import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.auraskills.SorceryManaBlast
import me.vaan.sorceryskill.utils.StorageConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*

object CastListener : Listener {
    private val activationMap = HashMap<UUID, Boolean>()

    @EventHandler
    fun onActivate(event: PlayerInteractEvent) {
        if(!SorceryManaBlast.BLAST.isEnabled) return

        val player = event.player
        val uuid = player.uniqueId

        if (player.inventory.itemInMainHand.type != Material.BLAZE_ROD) return
        if (event.action.isRightClick) {
            activationMap.compute(uuid) { _, v ->
                val ret = if (v == null) true else !v
                val message = SorceryRestore.transaltor["spell-casting"] + if (ret) {
                    SorceryRestore.transaltor["enabled"]
                } else {
                    SorceryRestore.transaltor["disabled"]
                }
                player.sendMessage(message)

                return@compute ret
            }
        } else if (event.action.isLeftClick) {
            val castSpell = activationMap.getOrPut(uuid) { false }
            if (!castSpell) return

            val skillPlayer = SorceryRestore.api.getUser(uuid)
            val level = skillPlayer.getManaAbilityLevel(SorceryManaBlast.BLAST)
            val manaCost = SorceryManaBlast.BLAST.getManaCost(level)
            val skillDamage = SorceryManaBlast.BLAST.getValue(level)

            if (!SorceryRestore.cooldown.check("cast_cooldown_$level", player.name)) {
                val message = SorceryRestore.transaltor["spell-casting"] + SorceryRestore.transaltor["cooldown"]
                player.sendMessage(message)
                return
            }

            if(!skillPlayer.consumeMana(manaCost)) return

            val callEvent = ManaAbilityActivateEvent(player, skillPlayer, SorceryManaBlast.BLAST, 0, manaCost)
            Bukkit.getServer().pluginManager.callEvent(callEvent)

            if (!callEvent.isCancelled)
                attackSpell(player, skillDamage)
        }
    }

    private fun attackSpell(shooter: Player, damage: Double) {
        object : BukkitRunnable() {
            val hitEntities = HashMap<UUID, Boolean>()
            val currentLocation: Location = shooter.eyeLocation.clone()
            val velocity: Vector = currentLocation.direction.normalize().multiply(1) //speed
            var traveledDistance: Int = 0

            override fun run() {

                if (traveledDistance >= StorageConfig.maxSpellDistance) {
                    cancel()
                    return
                }

                currentLocation.world.spawnParticle(Particle.WITCH, currentLocation, 10, 0.2, 0.2, 0.2, 0.05)

                currentLocation.add(velocity)
                traveledDistance++

                for (target in shooter.world.livingEntities) {
                    if (target.isDead) continue
                    if (target == shooter) continue // Don't hit the shooter
                    if (hitEntities[target.uniqueId] == true) continue

                    if (target.location.distance(currentLocation) >= StorageConfig.spellAreaOfEffect) continue

                    hitEntities[target.uniqueId] = true
                    SorceryRestore.debug("Damage dealt $damage by ${shooter.name}")

                    val reduction = SorceryRestore.armorCalc.getDirectHitReduction(target, 0.0)
                    target.damage(damage * reduction / 2.0, shooter)
                    SorceryRestore.armorCalc.reduceArmorDurability(target)

                    return
                }

                if (currentLocation.block.type.isSolid) {
                    cancel()
                }
            }
        }.runTaskTimer(SorceryRestore.instance, 0L, 1L)
    }
}