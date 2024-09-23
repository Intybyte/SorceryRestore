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
    val activationMap = HashMap<UUID, Boolean>()

    @EventHandler
    fun onActivate(event: PlayerInteractEvent) {
        if(!SorceryManaBlast.BLAST.isEnabled) return

        val player = event.player
        val uuid = player.uniqueId

        if (player.inventory.itemInMainHand.type != Material.BLAZE_ROD) return
        if (event.action.isRightClick) {
            activationMap.compute(uuid) { _, v ->
                val ret = if (v == null) true else !v
                player.sendMessage("SPELL CASTING - " + if (ret) "Enabled" else "Disabled")

                return@compute ret
            }
        } else if (event.action.isLeftClick) {
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
            val hitEntities = HashMap<UUID, Boolean>()
            val currentLocation: Location = shooter.eyeLocation.clone()
            val velocity: Vector = currentLocation.direction.normalize().multiply(1) //speed
            val maxDistance: Int = StorageConfig.maxSpellDistance
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
                    if (hitEntities[target.uniqueId] == true) continue

                    if (target.location.distance(currentLocation) >= StorageConfig.spellAreaOfEffect) continue

                    hitEntities[target.uniqueId] = true
                    SorceryRestore.logger().info("Damage dealt $damage")
                    target.damage(damage / 2.0, shooter)
                    return
                }

                if (currentLocation.block.type.isSolid) {
                    cancel()
                }
            }
        }.runTaskTimer(SorceryRestore.instance(), 0L, 1L)
    }
}