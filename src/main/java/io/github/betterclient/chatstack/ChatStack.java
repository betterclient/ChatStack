package io.github.betterclient.chatstack;

import net.fabricmc.api.ClientModInitializer;

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
}
