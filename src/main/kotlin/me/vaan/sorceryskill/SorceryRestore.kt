package me.vaan.sorceryskill

import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.event.skill.SkillsLoadEvent
import dev.aurelium.auraskills.api.registry.NamespacedRegistry
import me.vaan.CooldownManager
import me.vaan.interfaces.SimpleDebugger
import me.vaan.playerutils.ArmorCalculator
import me.vaan.sorceryskill.auraskills.SorceryAbilities
import me.vaan.sorceryskill.auraskills.SorceryManaBlast
import me.vaan.sorceryskill.auraskills.SorcerySkill
import me.vaan.sorceryskill.auraskills.listeners.CastListener
import me.vaan.sorceryskill.auraskills.listeners.ManaRegenListener
import me.vaan.sorceryskill.auraskills.listeners.ManaUseListener
import me.vaan.sorceryskill.auraskills.listeners.XpEarnListener
import me.vaan.sorceryskill.auraskills.sources.ManaSource
import me.vaan.sorceryskill.utils.*
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.logging.Logger

class SorceryRestore : JavaPlugin(), Listener {

    companion object StaticStuff : SimpleDebugger {

        lateinit var api: AuraSkillsApi
            private set

        lateinit var instance: SorceryRestore
            private set

        lateinit var registry: NamespacedRegistry
            private set

        lateinit var cooldown: CooldownManager<String>
            private set

        lateinit var log: Logger
            private set

        lateinit var armorCalc: ArmorCalculator
            private set

        lateinit var transaltor: TranslationHandler
            private set

        override fun debug(s: String) {
            if (StorageConfig.debug)
                log.info(s)
        }
    }

    override fun onEnable() {
        api = AuraSkillsApi.get()
        instance = this
        registry = api.useRegistry(Utils.PLUGIN_NAME, dataFolder)
        log = this.logger

        saveResources()
        transaltor = TranslationHandler(this)

        registerSourceTypes()

        SorceryAbilities.loadAbilities()
        SorceryManaBlast.loadManaAbility()
        registry.registerSkill(SorcerySkill.SORCERY)

        cooldown = CooldownManager()
        registerListeners()

        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    SorceryAbilities.TOTEM_POWER.callHandler(player)
                }
            }
        }.runTaskTimer(this, 0, 10L)
    }

    @EventHandler
    private fun registerCooldowns(event: SkillsLoadEvent) {
        cooldown.setCastCooldowns()
    }

    private fun registerListeners() {
        val pm = this.server.pluginManager
        pm.registerEvents(this, this)
        pm.registerEvents(ManaUseListener, this)
        pm.registerEvents(ManaRegenListener, this)
        pm.registerEvents(CastListener, this)
        pm.registerEvents(XpEarnListener, this)
    }

    private fun registerSourceTypes() {
        registry.registerSourceType("manasource") { source, context ->
            ManaSource(context.parseValues(source))
        }
    }

    private fun saveResources() {
        PluginFileHandler(this).saveDefaultResources()

        StorageConfig.debug = config.getBoolean("debug")
        StorageConfig.maxSpellDistance = config.getInt("max-spell-distance")
        StorageConfig.spellAreaOfEffect = config.getDouble("spell-area-of-effect")
    }
}
