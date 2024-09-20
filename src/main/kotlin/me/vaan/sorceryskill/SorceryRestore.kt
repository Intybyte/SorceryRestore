package me.vaan.sorceryskill

import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.registry.NamespacedRegistry
import me.vaan.sorceryskill.auraskills.SorceryAbilities
import me.vaan.sorceryskill.auraskills.SorceryManaBlast
import me.vaan.sorceryskill.auraskills.SorcerySkill
import me.vaan.sorceryskill.auraskills.listeners.CastListener
import me.vaan.sorceryskill.auraskills.listeners.ManaRegenListener
import me.vaan.sorceryskill.auraskills.listeners.ManaUseListener
import me.vaan.sorceryskill.auraskills.sources.ManaSource
import me.vaan.sorceryskill.utils.Utils
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class SorceryRestore : JavaPlugin() {

    companion object {
        @JvmStatic
        private lateinit var auraSkills: AuraSkillsApi
        @JvmStatic
        private lateinit var instance: SorceryRestore
        @JvmStatic
        private lateinit var registry: NamespacedRegistry
        @JvmStatic
        private lateinit var log: Logger
        @JvmStatic
        private var debug: Boolean = false

        fun api(): AuraSkillsApi {
            return auraSkills
        }

        fun instance(): SorceryRestore {
            return instance
        }

        fun registry(): NamespacedRegistry {
            return registry
        }

        fun debug(str: String) {
            if (debug)
                log.info(str)
        }

        fun logger(): Logger {
            return log
        }
    }

    override fun onEnable() {
        auraSkills = AuraSkillsApi.get()
        instance = this
        registry = auraSkills.useRegistry(Utils.PLUGIN_NAME, dataFolder)
        log = this.logger
        saveResources()

        registerSourceTypes()

        SorceryAbilities.loadAbilities()
        SorceryManaBlast.loadManaAbility()
        registry.registerSkill(SorcerySkill.SORCERY)

        registerListeners()
    }

    private fun registerListeners() {
        val pm = this.server.pluginManager
        pm.registerEvents(ManaUseListener, this)
        pm.registerEvents(ManaRegenListener, this)
        pm.registerEvents(CastListener, this)
    }

    private fun registerSourceTypes() {
        registry.registerSourceType("manasource") { source, context ->
            ManaSource(context.parseValues(source))
        }
    }

    private fun saveResources() {
        saveResource("sources/sorcery.yml", false)
        saveResource("rewards/sorcery.yml", false)
        saveResource("abilities.yml", false)
        saveResource("skills.yml", false)
        saveResource("mana_abilities.yml", false)
        saveResource("config.yml", false)
        debug = config.getBoolean("debug")
    }
}
