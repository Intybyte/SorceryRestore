package me.vaan.sorceryskill.utils

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

class TranslationHandler(private val plugin: Plugin) {
    private var currentLanguage = "en"
    private val languageList = hashSetOf("en")
    private val languageCache = HashMap<String, String>()
    private val directory = plugin.dataFolder.path + "/translations/"

    init {
        val folder = File(directory)
        if (!folder.exists()) folder.mkdir()

        loadAvailableLanguages()
        loadDefaultLanguage()
        loadLanguageCache()
    }

    operator fun get(key: String) : String {
        val data = languageCache[key]
        data ?: throw RuntimeException("Key $key not found")
        return data
    }

    fun loadLanguageCache() {
        languageCache.clear()
        val languageFile = getCfg(directory, "translation_$currentLanguage.yml")

        for (entry in languageFile.getKeys(false)) {
            languageCache[entry] = languageFile.getString(entry)!!
            plugin.logger.info("Loaded key: $entry with value ${languageCache[entry]}")
        }
    }

    fun loadDefaultLanguage() {
        val config = getCfg(directory,"main.yml")
        currentLanguage = config.getString("default-language")!!
        if (currentLanguage !in languageList) throw RuntimeException("Default language not found!")
    }

    private fun getCfg(directory: String, path: String) : FileConfiguration {
        val file = File(directory, path)
        val config = YamlConfiguration()
        config.load(file)
        return config
    }

    private fun getFile(path: String) : File {
        val configFile = File(directory, path)
        if (!configFile.exists()) plugin.saveResource(path, false)
        return configFile
    }

    fun loadAvailableLanguages() {
        val folder = File(directory)
        val fileList = folder.listFiles()
        fileList ?: throw RuntimeException("Error loading message files")

        for (file in folder.listFiles()!!) {
            if (file.name == "main.yml") continue

            val language = file.name.split('_')[1].split('.')[0]
            languageList.add(language)
        }
    }

}