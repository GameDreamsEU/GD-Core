package eu.gamedreams.gdcore.modules.chat;

import eu.gamedreams.gdcore.common.GDPermissions;
import eu.gamedreams.gdcore.modules.Module;
import eu.gamedreams.gdcore.utils.ComponentUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatModule extends Module implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public ChatModule() {
        this.isEnabled = false;
    }

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        if (!this.isEnabled()) return;

        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        Component componentMessage = event.message();
        String rawMessage = PlainTextComponentSerializer.plainText().serialize(componentMessage);

        boolean continueProcesses;

        continueProcesses = this.handleBlackListedWords(player, rawMessage, event);
        if (continueProcesses) continueProcesses = this.handleCapsLock(player, rawMessage, event);
        if (continueProcesses) this.handleChatCooldown(player, event);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }

    public boolean handleChatCooldown(Player player, AsyncChatEvent event) {
        if (player.hasPermission(GDPermissions.CHAT_COOLDOWN_BYPASS.permission())) return true;

        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (isCooldownExpired(playerUUID, currentTime)) {
            cooldowns.put(playerUUID, currentTime);
            return true;
        }

        event.setCancelled(true);

        long playerChatCooldownTime = getCooldownTime() + cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();

        String denyMessage = moduleConfiguration.getString("cooldown.deny-message")
                .replace("%time%", String.valueOf((playerChatCooldownTime / 1000 + 1)));
        player.sendMessage(ComponentUtils.deserialize(denyMessage));
        return false;
    }

    private boolean isCooldownExpired(UUID playerUUID, long currentTime) {
        return !cooldowns.containsKey(playerUUID) ||
                (currentTime - cooldowns.get(playerUUID)) >= getCooldownTime();
    }

    private long getCooldownTime() {
        return moduleConfiguration.getLong("cooldown.cooldown-time");
    }

    public boolean handleCapsLock(Player player, String text, AsyncChatEvent event) {
        if (player.hasPermission(GDPermissions.CHAT_CAPSLOCK_BYPASS.permission())) return true;

        text = clearTextFromFormatting(text);
        long totalLetters = text.chars().filter(Character::isLetter).count();
        long uppercaseLetters = text.chars().filter(Character::isUpperCase).count();

        int minimumCharacters = moduleConfiguration.getInt("capslock.minimum-characters");

        if (totalLetters <= minimumCharacters) return true;

        double capslockPercentage = moduleConfiguration.getDouble("capslock.capslock-percentage");

        double uppercasePercentage = (double) uppercaseLetters / totalLetters;
        if (uppercasePercentage < capslockPercentage) return true;

        event.setCancelled(true);
        player.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("capslock.deny-message")));
        return false;
    }

    public boolean handleBlackListedWords(Player player, String text, AsyncChatEvent event) {
        if (player.hasPermission(GDPermissions.CHAT_BLACKLISTEDWORDS_BYPASS.permission())) return true;

        text = clearTextFromFormatting(text).toLowerCase();
        List<String> blockedWords = moduleConfiguration.getStringList("blacklisted-words.words");

        if (blockedWords.stream().noneMatch(text::contains)) return true;

        event.setCancelled(true);
        player.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("blacklisted-words.deny-message")));
        return false;
    }

    private String clearTextFromFormatting(String text) {
        boolean skip = false;
        int index = 0;
        StringBuilder newText = new StringBuilder();

        while (index < text.length()) {
            if (text.startsWith("<chat=", index)) {
                skip = true;
                index += "<chat=".length();
            } else if (text.startsWith("<IC^", index)) {
                skip = true;
                index += "<IC^".length();
            } else if (text.startsWith(">:>", index)) {
                skip = false;
                index += ">:>".length();
            } else {
                if (!skip) {
                    newText.append(text.charAt(index));
                }
                index++;
            }
        }

        return newText.toString();
    }

}
