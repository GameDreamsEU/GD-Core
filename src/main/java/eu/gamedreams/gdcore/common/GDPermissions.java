package eu.gamedreams.gdcore.common;

public enum GDPermissions {

    CHAT_COOLDOWN_BYPASS("chat.cooldown.bypass"),
    CHAT_CAPSLOCK_BYPASS("chat.capslock.bypass"),
    CHAT_BLACKLISTEDWORDS_BYPASS("blacklistedwords.bypass"),
    MAIN_ADMIN("main.admin"),
    CHARFILTER_BYPASS("charfilter.bypass");

    private final String permission;

    GDPermissions(String permission) {
        this.permission = permission;
    }

    public String permission() {
        return "gamedreams.core." + permission;
    }

}
