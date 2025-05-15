package io.github.betterclient.chatstack;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RepeatingMessage {
    private final MutableText messageOriginal;
    private final ArrayList<ChatHudLine.Visible> instances;
    private final AtomicInteger count;

    public RepeatingMessage(MutableText messageOriginal, ArrayList<ChatHudLine.Visible> instances, AtomicInteger count) {
        this.messageOriginal = messageOriginal;
        this.instances = instances;
        this.count = count;
    }

    public MutableText getOriginalMessage() {
        return messageOriginal;
    }

    public ArrayList<ChatHudLine.Visible> getInstances() {
        return instances;
    }

    public AtomicInteger getCount() {
        return count;
    }
}