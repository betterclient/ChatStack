package io.github.betterclient.chatstack;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Style;

import java.util.HashMap;
import java.util.Map;

public class ChatStack implements ClientModInitializer {
    private static ChatStack _instance;
    public Map<String, RepeatingMessage> messages = new HashMap<>();

    public static ChatStack getInstance() {
        return _instance;
    }

    @Override
    public void onInitializeClient() {
        _instance = this;
    }

    public <T> T getColor(int count, T red, T yellow, T green) {
        T color;
        if (count >= 1 && count <= 30) {
            color = green;
        } else if (count > 30 && count <= 70) {
            color = yellow;
        } else {
            color = red;
        }
        return color;
    }
}
