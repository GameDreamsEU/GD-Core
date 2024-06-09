package eu.gamedreams.gdcore.modules.charfilter.listeners;

import dev.dejvokep.boostedyaml.YamlDocument;
import eu.gamedreams.gdcore.common.GDPermissions;
import eu.gamedreams.gdcore.modules.charfilter.CharFilterModule;
import eu.gamedreams.gdcore.modules.charfilter.utils.Validator;
import eu.gamedreams.gdcore.utils.ComponentUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FilterListeners implements Listener {

    private final CharFilterModule charFilterModule;
    private final YamlDocument moduleConfiguration;

    public FilterListeners(CharFilterModule charFilterModule) {
        this.charFilterModule = charFilterModule;
        this.moduleConfiguration = charFilterModule.getModuleConfiguration();
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String message = ComponentUtils.serializePlain(event.message());
        validate(message, event, event.getPlayer());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        validate(command, event, event.getPlayer());
    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent e) {
        ItemStack resultItem = e.getResult();
        if (resultItem == null) return;

        ItemMeta itemMeta = resultItem.getItemMeta();
        if (itemMeta == null) return;

        String itemName = ComponentUtils.serializePlain(itemMeta.displayName());
        if (Validator.isInvalidString(itemName, moduleConfiguration.getString("regex"))) {
            e.setResult(null);
            e.getView().getPlayer().sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("deny-message")));
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        for (Component line : e.lines()) {
            validate(ComponentUtils.serializePlain(line), e, e.getPlayer());
        }
    }

    @EventHandler
    public void onBookSign(PlayerEditBookEvent e) {
        String book = e.getNewBookMeta().getAsString();
        validate(book, e, e.getPlayer());
    }

    private void validate(String string, Cancellable event, Player player) {
        if (!charFilterModule.isEnabled()) return;
        if (player.hasPermission(GDPermissions.CHARFILTER_BYPASS.permission())) return;
        if (Validator.isInvalidString(string, moduleConfiguration.getString("regex"))) {
            event.setCancelled(true);
            player.sendMessage(ComponentUtils.deserialize(moduleConfiguration.getString("deny-message")));
        }
    }

}
