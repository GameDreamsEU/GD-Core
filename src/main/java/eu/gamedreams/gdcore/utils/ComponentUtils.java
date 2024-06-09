package eu.gamedreams.gdcore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentUtils {

    public static Component deserialize(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static String serializePlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

}
