package eu.gamedreams.gdcore.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import eu.gamedreams.gdcore.GD_Core;
import eu.gamedreams.gdcore.modules.Module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ConfigurationManager {

    private final GD_Core plugin;

    public ConfigurationManager(GD_Core plugin) {
        this.plugin = plugin;
    }

    public YamlDocument initializeModuleFile(Module module) throws IOException {
        String moduleName = module.getName();
        return this.initializeUpdatableFile(new File(plugin.getDataFolder() + "/modules"), moduleName, plugin.getResource("modules/" + moduleName + ".yml"));
    }

    public YamlDocument initializeUpdatableFile(File folder, String fileName, InputStream resource) throws IOException {
        if (resource == null) return null;

        YamlDocument yamlDocument = YamlDocument.create(new File(folder, fileName + ".yml"),
                Objects.requireNonNull(resource),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version"))
                        .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
        );

        yamlDocument.update();
        yamlDocument.save();

        return yamlDocument;
    }

}
