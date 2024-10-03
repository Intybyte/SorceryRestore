package me.vaan.sorceryskill.utils

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Might move this to a library together with similar stuff
 */
class PluginFileHandler(private val _plugin: JavaPlugin) {

    fun saveDefaultResources() {
        val resourceFolder = _plugin.dataFolder
        if (!resourceFolder.exists()) {
            resourceFolder.mkdirs()
        }

        // Load resources from the JAR
        try {
            val jarUri = _plugin.javaClass.protectionDomain.codeSource.location.toURI()
            JarFile(File(jarUri)).use { jarFile ->
                jarFile.stream()
                    .filter { jarEntry: JarEntry ->
                            !jarEntry.isDirectory &&
                            !jarEntry.name.startsWith("kotlin/") &&
                            jarEntry.name.endsWith(".yml") &&
                            jarEntry.name != "plugin.yml"
                    }
                    .forEach { jarEntry: JarEntry ->
                        saveResourceFromJar(
                            jarEntry,
                            resourceFolder
                        )
                    }
            }
        } catch (e: Exception) {
            _plugin.logger.severe("Failed to load resources from the JAR: " + e.message)
            e.printStackTrace()
        }
    }

    private fun saveResourceFromJar(jarEntry: JarEntry, resourceFolder: File) {
        val resourceFile = File(resourceFolder, jarEntry.name)
        if (resourceFile.exists()) {
            return
        }

        _plugin.saveResource(jarEntry.name, false)
    }
}