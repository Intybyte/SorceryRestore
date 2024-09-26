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
import me.vaan.sorceryskill.auraskills.sources.ManaSource
import me.vaan.sorceryskill.utils.StorageConfig
import me.vaan.sorceryskill.utils.Utils
import me.vaan.sorceryskill.utils.setCastCooldowns
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class SorceryRestore : JavaPlugin(), Listener {

    companion object StaticStuff : SimpleDebugger {
        @JvmStatic
        private lateinit var _auraSkills: AuraSkillsApi
        @JvmStatic
        private lateinit var _instance: SorceryRestore
        @JvmStatic
        private lateinit var _registry: NamespacedRegistry
        @JvmStatic
        private lateinit var log: Logger
        @JvmStatic
        private lateinit var cooldownManager: CooldownManager<String>
        @JvmStatic
        private val armorCalculator = ArmorCalculator()

        val api: AuraSkillsApi
            get() {
                return _auraSkills
            }

        val instance: SorceryRestore
            get() {
                return _instance
            }

        val registry: NamespacedRegistry
            get() {
                return _registry
            }

        override fun debug(s: String) {
            if (StorageConfig.debug)
                log.info(s)
        }

        val cooldown: CooldownManager<String>
            get() {
                return cooldownManager
            }

        val logger: Logger
            get() {
                return log
            }

        val armorCalc: ArmorCalculator
            get() {
                return armorCalculator
            }
    }

    override fun onEnable() {
        _auraSkills = AuraSkillsApi.get()
        _instance = this
        _registry = _auraSkills.useRegistry(Utils.PLUGIN_NAME, dataFolder)
        log = this.logger
        saveResources()

        registerSourceTypes()

        SorceryAbilities.loadAbilities()
        SorceryManaBlast.loadManaAbility()
        _registry.registerSkill(SorcerySkill.SORCERY)

        cooldownManager = CooldownManager()
        registerListeners()
    }

    @EventHandler
    private fun registerCooldowns(event: SkillsLoadEvent) {
        cooldownManager.setCastCooldowns()
    }

    private fun registerListeners() {
        val pm = this.server.pluginManager
        pm.registerEvents(this, this)
        pm.registerEvents(ManaUseListener, this)
        pm.registerEvents(ManaRegenListener, this)
        pm.registerEvents(CastListener, this)
    }

    private fun registerSourceTypes() {
        _registry.registerSourceType("manasource") { source, context ->
            ManaSource(context.parseValues(source))
        }
    }

    private fun saveResources() {
        saveResource("sources/sorcery.yml", false)
        saveResource("rewards/sorcery.yml", false)
        saveResource("abilities.yml", false)
        saveResource("skills.yml", false)
        saveResource("mana_abilities.yml", false)
        saveDefaultConfig()

        StorageConfig.debug = config.getBoolean("debug")
        StorageConfig.maxSpellDistance = config.getInt("max-spell-distance")
        StorageConfig.spellAreaOfEffect = config.getDouble("spell-area-of-effect")
    }
}
