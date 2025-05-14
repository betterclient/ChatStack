package io.github.betterclient.chatstack;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public record RepeatingMessage(MutableText messageOriginal, ArrayList<ChatHudLine.Visible> instances, AtomicInteger count) { }