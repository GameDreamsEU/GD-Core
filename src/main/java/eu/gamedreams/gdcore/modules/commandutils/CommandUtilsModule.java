package eu.gamedreams.gdcore.modules.commandutils;

import eu.gamedreams.gdcore.modules.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;

public class CommandUtilsModule extends Module implements Listener {
    public CommandUtilsModule() {
        this.name = "commandutils";
        this.isEnabled = false;
    }

    @Override
    protected void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent event) {
        this.handleTabComplete(event);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        this.handleCommandPreProcess(event);
    }

    public void handleTabComplete(PlayerCommandSendEvent event) {
        if (!isEnabled()) {
            return;
        }

        List<String> commandsToExclude = moduleConfiguration.getStringList("blocked-commands");
        event.getCommands().removeAll(commandsToExclude);
    }

    public void handleCommandPreProcess(PlayerCommandPreprocessEvent event) {

        if (!isEnabled) return;

        String originalCommand = event.getMessage();
        String[] args = originalCommand.split(" ");
        String lowercaseCommand = args[0].toLowerCase();
        StringBuilder newCommandBuilder = new StringBuilder(lowercaseCommand);
        for (int i = 1; i < args.length; i++) {
            newCommandBuilder.append(" ").append(args[i]);
        }
        String newCommand = newCommandBuilder.toString();
        event.setMessage(newCommand);
    }
}
