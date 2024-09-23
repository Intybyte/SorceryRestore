package me.vaan.sorceryskill.auraskills.listeners

import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.auraskills.SorceryManaBlast
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
    val activationMap = HashMap<UUID, Boolean>()

    @EventHandler
    fun onActivate(event: PlayerInteractEvent) {
        if(!SorceryManaBlast.BLAST.isEnabled) return

        val player = event.player
        val uuid = player.uniqueId

        if (event.action.isRightClick) {
            SorceryRestore.debug("${this::class} Right click")

            if (player.inventory.itemInMainHand.type != Material.BLAZE_ROD) return

            activationMap.compute(uuid) { _, v ->
                val ret = if (v == null) true else !v
                player.sendMessage("SPELL CASTING - " + if (ret) "Enabled" else "Disabled")

                return@compute ret
            }
        } else if (event.action.isLeftClick) {
            SorceryRestore.debug("${this::class} Left click")

            val castSpell = activationMap.getOrPut(uuid) { false }
            if (!castSpell) return

            val skillPlayer = SorceryRestore.api().getUser(uuid)
            val level = skillPlayer.getManaAbilityLevel(SorceryManaBlast.BLAST)
            val manaCost = SorceryManaBlast.BLAST.getManaCost(level)
            val skillDamage = SorceryManaBlast.BLAST.getValue(level)

            if (!SorceryRestore.cooldown().check("cast_cooldown_$level", player.name)) {
                player.sendMessage("SPELL CASTING - On cooldown")
                return
            }

            if(!skillPlayer.consumeMana(manaCost)) return

            val callEvent = ManaAbilityActivateEvent(player, skillPlayer, SorceryManaBlast.BLAST, 0, manaCost)
            Bukkit.getServer().pluginManager.callEvent(callEvent)

            if (!callEvent.isCancelled)
                launchProjectile(player, skillDamage)
        }
    }

    private fun launchProjectile(shooter: Player, damage: Double) {
        object : BukkitRunnable() {
            var currentLocation: Location = shooter.eyeLocation.clone()
            var velocity: Vector = currentLocation.direction.normalize().multiply(0.6)
            var maxDistance: Int = 50 // Maximum distance the projectile will travel (in blocks)
            var traveledDistance: Int = 0

            override fun run() {

                if (traveledDistance >= maxDistance) {
                    cancel()
                    return
                }

                currentLocation.world.spawnParticle(Particle.WITCH, currentLocation, 10, 0.2, 0.2, 0.2, 0.05)

                currentLocation.add(velocity)
                traveledDistance++

                for (target in shooter.world.livingEntities) {
                    if (target.isDead) continue
                    if (target == shooter) continue // Don't hit the shooter

                    if (target.location.distance(currentLocation) >= 1.5) continue

                    target.damage(damage, shooter)

                    cancel()
                    return
                }

                if (currentLocation.block.type.isSolid) {
                    cancel()
                }
            }
        }.runTaskTimer(SorceryRestore.instance(), 0L, 1L)
    }
}