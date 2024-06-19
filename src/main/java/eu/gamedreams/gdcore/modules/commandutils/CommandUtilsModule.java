package eu.gamedreams.gdcore.modules.commandutils;

import eu.gamedreams.gdcore.modules.Module;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;

public class CommandUtilsModule extends Module implements Listener {
    public CommandUtilsModule() {
        this.isEnabled = false;
    }

    @Override
    public String getName() {
        return "commandutils";
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent event) {
        this.handleTabComplete(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
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

        if (event.isCancelled()) {
            Player player = event.getPlayer();
            player.playSound(
                    player.getLocation(),
                    moduleConfiguration.getString("canceled-command-sound.name"),
                    moduleConfiguration.getFloat("canceled-command-sound.volume"),
                    moduleConfiguration.getFloat("canceled-command-sound.pitch")
            );
        }

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
