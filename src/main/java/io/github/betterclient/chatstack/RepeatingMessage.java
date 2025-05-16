package io.github.betterclient.chatstack;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RepeatingMessage {
    private final Object messageOriginal;
    private final ArrayList<Object> instances;
    private final AtomicInteger count;

    public RepeatingMessage(Object messageOriginal, ArrayList<Object> instances, AtomicInteger count) {
        this.messageOriginal = messageOriginal;
        this.instances = instances;
        this.count = count;
    }

    public Object getOriginalMessage() {
        return messageOriginal;
    }

    public ArrayList<Object> getInstances() {
        return instances;
    }

    public AtomicInteger getCount() {
        return count;
    }
}