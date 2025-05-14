package io.github.betterclient.chatstack;

import net.fabricmc.api.ClientModInitializer;

public class ChatStack implements ClientModInitializer {
    private static ChatStack _instance;

    public static ChatStack getInstance() {
        return _instance;
    }

    @Override
    public void onInitializeClient() {
        _instance = this;
    }
}
