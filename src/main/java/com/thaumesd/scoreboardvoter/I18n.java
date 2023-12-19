package com.thaumesd.scoreboardvoter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

public class I18n {
    private static final String MESSAGES = "messages";
    private static I18n instance;
    private final transient ResourceBundle defaultBundle;
    private final ScoreboardVoter plugin;

    public I18n(ScoreboardVoter plugin) {
        this.plugin = plugin;
        this.defaultBundle = ResourceBundle.getBundle(MESSAGES, Locale.US, UTF8ResourceBundleControl.get());
    }

    private static Component parse(String message){
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static Component translate(String key, Boolean addPrefix, Object ...objects) {
        if (instance == null) {
            return Component.newline();
        }

        String message = "";

        if(addPrefix){
            message += instance.getBundle().getString("prefix") + " ";
        }

        message += instance.getBundle().getString(key);
        return parse(MessageFormat.format(message, objects));
    }

    public void onEnable() {
        instance = this;
    }

    public void onDisable() {
        instance = null;
    }

    public ResourceBundle getBundle(){
        return this.defaultBundle;
    }
}
