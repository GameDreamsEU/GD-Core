package eu.gamedreams.gdcore.modules.main.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import eu.gamedreams.gdcore.GD_Core;
import eu.gamedreams.gdcore.common.GDPermissions;
import eu.gamedreams.gdcore.modules.Module;
import eu.gamedreams.gdcore.modules.ModuleManager;
import eu.gamedreams.gdcore.modules.main.MainModule;
import eu.gamedreams.gdcore.utils.ComponentUtils;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GDCoreCommand implements TabExecutor {

    private final MainModule mainModule;
    private final GD_Core plugin;
    private final ModuleManager moduleManager;
    private final YamlDocument moduleConfiguration;

    public GDCoreCommand(MainModule mainModule, GD_Core plugin, ModuleManager moduleManager) {
        this.plugin = plugin;
        this.moduleManager = moduleManager;
        this.mainModule = mainModule;
        this.moduleConfiguration = mainModule.getModuleConfiguration();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!mainModule.isEnabled()) return false;

        if (sender instanceof Player player) {
            if (!player.hasPermission(GDPermissions.MAIN_ADMIN.permission())) {
                player.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.no-permission")));
                return false;
            }
        }

        String subCommand = args.length != 0 ? args[0] : "";

        switch (subCommand.toLowerCase()) {
            case "reload" -> handleReload(sender, args);
            case "enable" -> handleEnable(sender, args);
            case "disable" -> handleDisable(sender, args);
            default -> sendPluginInfo(sender);
        }

        return true;
    }

    private void handleReload(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.reload-usage")));
            return;
        }

        Optional<Module> module = Optional.ofNullable(moduleManager.getModule(args[1]));

        if (module.isEmpty()) {
            sendAvailableModules(sender);
            return;
        }

        try {
            module.get().reload();
            sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.module-reloaded")));
        } catch (IOException e) {
            plugin.getLogger().severe("Couldn't reload module " + module.get().getName() + "!" + e.getMessage());
        }
    }

    private void handleEnable(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.enable-usage")));
            return;
        }

        Optional<Module> module = Optional.ofNullable(moduleManager.getModule(args[1]));

        if (module.isEmpty()) {
            sendAvailableModules(sender);
            return;
        }

        if (module.get().isEnabled()) {
            sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.module-already-enabled")));
            return;
        }

        module.get().setEnabled(true);
        sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.module-enabled")));
    }

    private void handleDisable(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.disable-usage")));
            return;
        }

        Optional<Module> module = Optional.ofNullable(moduleManager.getModule(args[1]));

        if (module.isEmpty()) {
            sendAvailableModules(sender);
            return;
        }

        if (!module.get().isEnabled()) {
            sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.module-already-disabled")));
            return;
        }

        module.get().setEnabled(false);
        sender.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.module-disabled")));
    }

    private void sendAvailableModules(CommandSender sender) {
        sender.sendMessage(
                ComponentUtils.deserialize(moduleConfiguration.getString("module-editor.available-modules")
                        .replace("%modules%", moduleManager.getModules().stream()
                                .map(Module::getName)
                                .collect(Collectors.joining(moduleConfiguration.getString("module-editor.available-modules-splitter"))))
                )
        );
    }

    private void sendPluginInfo(CommandSender sender) {
        PluginMeta pluginMeta = plugin.getPluginMeta();

        String pluginName = pluginMeta.getName();
        String pluginDescription = pluginMeta.getDescription();
        List<String> pluginAuthors = pluginMeta.getAuthors();
        String pluginVersion = pluginMeta.getVersion();
        String pluginWebsite = pluginMeta.getWebsite();

        TextComponent.Builder builder = Component.text()
                .appendNewline()
                .append(ComponentUtils.deserialize("<gradient:#3690fa:white:#3690fa><strikethrough>                                                                                 "))
                .appendNewline()
                .append(ComponentUtils.deserialize(" <#3690fa>" + pluginName + ":"))
                .appendNewline()
                .appendNewline()
                .append(ComponentUtils.deserialize("  <#9bd7fa>ᴅᴇsᴄʀɪᴘᴛɪᴏɴ: <#3690fa>" + pluginDescription))
                .appendNewline()
                .append(ComponentUtils.deserialize(String.format("  <#9bd7fa>ᴀᴜᴛʜᴏʀs: <#3690fa>%s", String.join("<#9bd7fa>,<#3690fa> ", pluginAuthors))))
                .appendNewline()
                .append(ComponentUtils.deserialize("  <#9bd7fa>ᴠᴇʀsɪᴏɴ: <#3690fa>" + pluginVersion))
                .appendNewline()
                .append(ComponentUtils.deserialize("  <#9bd7fa>ᴡᴇʙsɪᴛᴇ: <#3690fa><hover:show_text:'<#3690fa>" + pluginWebsite + "'><click:open_url:'" + pluginWebsite + "'>" + pluginWebsite + "</click></hover>"))
                .appendNewline()
                .appendNewline()
                .append(ComponentUtils.deserialize("<gradient:#3690fa:white:#3690fa><strikethrough>                                                                                 "));

        sender.sendMessage(builder);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return Arrays.asList("reload", "enable", "disable");
            }
            case 2 -> {
                switch (args[0]) {
                    case "reload", "enable", "disable" -> {
                        return moduleManager.getModules().stream().map(Module::getName).collect(Collectors.toList());
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}